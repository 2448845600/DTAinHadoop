package com.DataPartition;

import com.DataStructure.Model.Curve;
import com.DataStructure.Model.Point;
import com.DataStructure.Model.PointSet;
import com.Globals;
import com.Util.HBaseHelper;
import jdk.nashorn.internal.runtime.arrays.ArrayLikeIterator;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.PrefixFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.IOException;
import java.util.*;

import static com.DataPartition.DataFormat.setPointSetByVal;
import static com.Util.ByteHelper.BytesToBit;


public class HBaseOperator {
    private static Logger logger = Logger.getLogger(HBaseOperator.class);

    private static String ns = Globals.ns;
    private static String tbl = Globals.tbl;
    private static String tblCurve = Globals.tblCurve;
    private static String tblTest = Globals.tblTest;
    private static String cf = Globals.cf;
    private static String col = Globals.col;
    private static int version = Globals.version;
    private static int nums = Globals.nums;//分区个数


    ////////////////////////////////////////////////
    ////////////////////建立库表////////////////////
    ///////////////////////////////////////////////

    //建立 默认的命名空间，数据库，表
    public static void createDB() {
        try {
            HBaseHelper helper = new HBaseHelper();
            helper.createNS(ns);
            helper.createTbl(ns, tbl, cf, version, "100000", "909090", nums);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //创建 默认的表
    public static void createTable() {
        try {
            HBaseHelper helper = new HBaseHelper();
            helper.createTbl(ns, tblCurve, Globals.cfCurve, version, "100000", "909090", nums);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //重新建立 默认的表
    //先删除表，在建立表
    public static void resetTable(String resetTable) {
        try {
            HBaseHelper helper = new HBaseHelper();
            helper.dropTable(ns, resetTable);
            helper.createTbl(ns, resetTable, cf, version, "100000", "909090", nums);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    ////////////////////////////////////////////////
    ////////////////////增删改查////////////////////
    ///////////////////////////////////////////////

    //输入rowKey，返回一条记录
    public static PointSet getOnePointSet(String rk) {
        Result result = null;
        PointSet pointSetRes = new PointSet();
        try {
            HBaseHelper hd = new HBaseHelper(tbl);
            result = hd.get(ns, tbl, rk);
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[] row = result.getRow();
        NavigableMap<byte[], NavigableMap<byte[], NavigableMap<Long, byte[]>>> map = result.getMap();
        Set<byte[]> cfs = map.keySet();
        for (byte[] cf : cfs) {
            NavigableMap<byte[], NavigableMap<Long, byte[]>> map1 = map.get(cf);
            Set<byte[]> cols = map1.keySet();
            for (byte[] col : cols) {
                NavigableMap<Long, byte[]> map2 = map1.get(col);
                Set<Long> tss = map2.keySet();
                //仅读取最近的时间戳对应的一条记录
                if (tss.iterator().hasNext()) {
                    Long ts = tss.iterator().next();
                    byte[] val = map2.get(ts);
                    PointSet ps = setPointSetByVal(rk, val);
                    //System.out.println("行键：" + new String(row) + "\t列族：" + new String(cf) + "\t列名：" + new String(col) + "\t时间戳：" + ts + "\t值：" + new String(val));
                }

                /*
                //读取所有时间戳对于的记录
                for (Long ts : tss) {
                    byte[] val = map2.get(ts);
                    PointSet ps = setPointSetByVal(rk, val);
                    System.out.println("行键：" + new String(row) + "\t列族：" + new String(cf) + "\t列名：" + new String(col) + "\t时间戳：" + ts + "\t值：" + new String(val));
                }
                */
            }
        }

        return pointSetRes;
    }


    public static PointSet getOnePointSet(byte[] rk) {
        return getOnePointSet(rk, ns, tbl);
    }

    public static PointSet getOnePointSet(byte[] rk, String nameSpace, String tableName) {
        Result result = null;
        PointSet pointSetRes = new PointSet();
        try {
            HBaseHelper hd = new HBaseHelper(tableName);
            result = hd.get(nameSpace, tableName, rk);
        } catch (IOException e) {
            e.printStackTrace();
        }

        NavigableMap<byte[], NavigableMap<byte[], NavigableMap<Long, byte[]>>> map = result.getMap();
        if (map == null) {
            return null;
        }
        Set<byte[]> cfs = map.keySet();
        for (byte[] cf : cfs) {
            NavigableMap<byte[], NavigableMap<Long, byte[]>> map1 = map.get(cf);
            Set<byte[]> cols = map1.keySet();
            for (byte[] col : cols) {
                NavigableMap<Long, byte[]> map2 = map1.get(col);
                Set<Long> tss = map2.keySet();
                //仅读取最近的时间戳对应的一条记录
                if (tss.iterator().hasNext()) {
                    Long ts = tss.iterator().next();
                    byte[] val = map2.get(ts);
                    PointSet ps = setPointSetByVal(BytesToBit(rk), val);
                }
            }
        }

        return pointSetRes;
    }

    public static List<PointSet> getPointSetInCellFromHBase(byte[] prefix) {
        return getPointSetInCellFromHBase(prefix, ns, tbl);
    }

    public static List<PointSet> getPointSetInCellFromHBase(byte[] prefix, String nameSpace, String tableName) {
        Iterator<Result> iterator = null;
        try {
            HBaseHelper helper = new HBaseHelper();
            iterator = helper.prefixFilter(nameSpace, tableName, prefix);
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<PointSet> resPointSetList = new ArrayList<>();

        while (iterator.hasNext()) {
            Result result = iterator.next();
            PointSet psTemp = new PointSet();
            byte[] row = result.getRow();
            String rk = BytesToBit(row);
            NavigableMap<byte[], NavigableMap<byte[], NavigableMap<Long, byte[]>>> map = result.getMap();

            Set<byte[]> cfs = map.keySet();
            for (byte[] cf : cfs) {
                NavigableMap<byte[], NavigableMap<Long, byte[]>> map1 = map.get(cf);
                Set<byte[]> cols = map1.keySet();
                for (byte[] col : cols) {
                    NavigableMap<Long, byte[]> map2 = map1.get(col);
                    Set<Long> tss = map2.keySet();

                    //仅读取最近的时间戳对应的一条记录
                    if (tss.iterator().hasNext()) {
                        Long ts = tss.iterator().next();
                        byte[] val = map2.get(ts);
                        resPointSetList.add(setPointSetByVal(rk, val));
                        logger.debug("行键：" + new String(row) + "\t列族：" + new String(cf) + "\t列名：" + new String(col) + "\t时间戳：" + ts + "\t值：" + new String(val));
                    }
                }
            }

        }
        return resPointSetList;
    }

    public static List<PointSet> getPointSetInCellFromHBase(Table table, byte[] prefix) {
        Scan scan = new Scan();
        Filter filter = new PrefixFilter(prefix);
        scan.setFilter(filter);
        ResultScanner scanner = null;
        try {
            scanner = table.getScanner(scan);
            logger.debug("scanner=" + scanner);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Iterator<Result> iterator = scanner.iterator();
        logger.debug("iterator=" + iterator.toString());

        List<PointSet> resPointSetList = new ArrayList<>();

        while (iterator.hasNext()) {
            Result result = iterator.next();
            byte[] row = result.getRow();
            String rk = BytesToBit(row);
            NavigableMap<byte[], NavigableMap<byte[], NavigableMap<Long, byte[]>>> map = result.getMap();

            Set<byte[]> cfs = map.keySet();
            for (byte[] cf : cfs) {
                NavigableMap<byte[], NavigableMap<Long, byte[]>> map1 = map.get(cf);
                Set<byte[]> cols = map1.keySet();
                for (byte[] col : cols) {
                    NavigableMap<Long, byte[]> map2 = map1.get(col);
                    Set<Long> tss = map2.keySet();

                    //仅读取最近的时间戳对应的一条记录
                    if (tss.iterator().hasNext()) {
                        Long ts = tss.iterator().next();
                        byte[] val = map2.get(ts);
                        resPointSetList.add(setPointSetByVal(rk, val));
                        logger.debug("行键：" + new String(row) + "\t列族：" + new String(cf) + "\t列名：" + new String(col) + "\t时间戳：" + ts);
                    }
                }
            }

        }
        return resPointSetList;
    }

    public static PointSet transformPointSetByHBaseResult(Result result) {
        PointSet resPointSet = new PointSet();

        byte[] row = result.getRow();
        String rk = BytesToBit(row);
        logger.debug("row=" + row + ", rk=" + rk);

        NavigableMap<byte[], NavigableMap<byte[], NavigableMap<Long, byte[]>>> map = result.getMap();

        Set<byte[]> cfs = map.keySet();
        for (byte[] cf : cfs) {
            NavigableMap<byte[], NavigableMap<Long, byte[]>> map1 = map.get(cf);
            Set<byte[]> cols = map1.keySet();
            for (byte[] col : cols) {
                NavigableMap<Long, byte[]> map2 = map1.get(col);
                Set<Long> tss = map2.keySet();

                //仅读取最近的时间戳对应的一条记录
                if (tss.iterator().hasNext()) {
                    Long ts = tss.iterator().next();
                    byte[] val = map2.get(ts);
                    resPointSet = setPointSetByVal(rk, val);
                    logger.debug("行键：" + new String(row) + "\t列族：" + new String(cf) + "\t列名：" + new String(col) + "\t时间戳：" + ts);
                }
            }
        }

        return resPointSet;
    }

    public static void putOnePointSet(PointSet ps) {
        try {
            byte[] rk = ps.getPointSetKey();
            String val = "";
            for (Point p : ps.getPoints()) {
                val += p.getX() + " " + p.getY() + " ";
            }

            //System.out.println("rk=" + rk + ", val=" + val);

            HBaseHelper hh = new HBaseHelper();
            hh.put(ns, tbl, rk, cf, col, val);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void putPointSetListToHBase(Table table, List<PointSet> pointSetList, String tableName) {
        try {
            List<byte[]> rks = new ArrayList<>();
            List<String> vals = new ArrayList<>();
            for (PointSet ps : pointSetList) {
                byte[] rk = ps.getPointSetKey();
                String val = "";
                for (Point p : ps.getPoints()) {
                    val += p.getX() + " " + p.getY() + " ";
                }
                rks.add(rk);
                vals.add(val);
            }
            HBaseHelper hh = new HBaseHelper();
            hh.puts(table, ns, tableName, rks, cf, col, vals);
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.info("[SUCCESS] put PointSetList To ns:tbl=" + ns + ":" + tableName);
    }

    public static void putPointSetListToHBase(Table table, List<PointSet> pointSetList) {
        List<Put> puts = new ArrayList<>();
        try {
            for (PointSet ps : pointSetList) {
                byte[] rk = ps.getPointSetKey();
                String val = "";
                for (Point p : ps.getPoints()) {
                    val += p.getX() + " " + p.getY() + " ";
                }
                Put put = new Put(rk);
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes(col), Bytes.toBytes(val));
                puts.add(put);
                String s = "";
                for (byte b : rk) s += b + ",";
                logger.debug("rowkey按照byte输出=" + s + "rowKey=" + rk + ", getRow()=" + put.getRow() + ", col=" + col);
            }
            table.put(puts);
            puts.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void putCurveToHBase(Table table, Curve curve, String tableName) {
        try {
            String rk = String.valueOf(curve.getCurveNum());
            String val = "";
            for (byte[] bytes : curve.getDivisionPointSetNums()) {
                val += bytes + " ";
            }
            logger.debug("rk=" + rk + ", val=" + val);
            HBaseHelper hh = new HBaseHelper();
            hh.put(table, ns, tableName, rk, Globals.cfCurve, Globals.colCurve, val);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void putCurveToHBase(Table table, Curve curve) {
        try {
            String rk = String.valueOf(curve.getCurveNum());
            String val = "";
            for (byte[] bytes : curve.getDivisionPointSetNums()) {
                val += bytes + " ";
            }
            logger.debug("rk=" + rk + ", val=" + val);
            Put put = new Put(Bytes.toBytes(rk));
            put.addColumn(Bytes.toBytes(cf), Bytes.toBytes(col), Bytes.toBytes(val));
            table.put(put);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void deleteOnePointSet(String rk) {
        try {
            HBaseHelper hber = new HBaseHelper();
            hber.delete(ns, tbl, rk, cf, col);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    ////////////////////////////////////////////////
    ////////////////////测试函数////////////////////
    ///////////////////////////////////////////////

    public static void main(String[] args) {
        resetTable(tblCurve);
        resetTable(tbl);
    }

}
