package com.Util;

import com.DataStructure.Model.PointSet;
import javafx.scene.control.Tab;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.PrefixFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;
import scala.util.control.Exception;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;

import static com.DataPartition.DataFormat.setPointSetByVal;


public class HBaseHelper {
    private Connection conn;
    private Table table;
    private Admin admin;

    private static Logger logger = Logger.getLogger(HBaseHelper.class);

    public HBaseHelper() throws IOException {
        System.setProperty("hadoop.home.dir", "D:\\hadoop");
        System.setProperty("HADOOP_USER_NAME", "cloud");

        // 0.先获取HadoopAPI中的Configuration对象
        Configuration hconf = new Configuration();
        // 1.获取HBaseConfiguration对象
        Configuration conf = HBaseConfiguration.create(hconf);
        // 2.获取Connection对象
        conn = ConnectionFactory.createConnection(conf);
        // 3.获取Admin对象
        admin = conn.getAdmin();
    }

    public HBaseHelper(String tbl) throws IOException {
        this();
        // 4.获取Table对象
        table = conn.getTable(TableName.valueOf(tbl));
    }

    // 封装方法，用来获取表对象
    public Table getTbl(String ns, String tbl) throws IOException {
        logger.debug("连接表");
        return conn.getTable(
                TableName.valueOf(
                        ns == null || "".equals(ns) ?
                                tbl : ns + ":" + tbl));
    }

    public Connection getConn() {
        return conn;
    }

    public void setConn(Connection conn) {
        this.conn = conn;
    }

    public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        this.table = table;
    }

    public Admin getAdmin() {
        return admin;
    }

    public void setAdmin(Admin admin) {
        this.admin = admin;
    }

    //上面是类的基础的方法
    //下面是包装后的一些方法
    //是把原生的一些的方法包装，以便于调用

    /**
     * 1.创建命名空间
     *
     * @param ns 命名空间
     */
    public void createNS(String ns) throws IOException {
        // 创建命名空间描述器对象
        NamespaceDescriptor nsDesc = NamespaceDescriptor.create(ns).build();
        admin.createNamespace(nsDesc);
    }

    /**
     * 2.创建表
     *
     * @param ns   命名空间
     * @param tbl  表名
     * @param cf   列族名
     * @param vers 某一列的版本数
     * @param sk   Region的行键的起始值
     * @param ek   Region的行键的结束值
     * @param nums 对该表进行分区的个数
     */
    public void createTbl(String ns, String tbl, String cf, int vers, String sk, String ek, int nums)
            throws IOException {
        // 构建表描述器
        HTableDescriptor tblDesc = new HTableDescriptor(TableName.valueOf(ns == null || "".equals(ns) ? tbl : ns + ":" + tbl));
        // 在创建表的时候需要传递列族，创建HColumnDescriptor
        HColumnDescriptor hcolDesc =
                new HColumnDescriptor(cf);
        // 使用HTableDescriptor对象的addFamily方法，
        // 将列族添加到表上
        tblDesc.addFamily(hcolDesc);
        // 给某一列设置版本数
        hcolDesc.setMaxVersions(vers > 1 ? vers : 1);
        // 创建表
        admin.createTable(
                tblDesc,
                Bytes.toBytes(sk),
                Bytes.toBytes(ek),
                nums);
    }

    /**
     * 3.插入
     * 将单个记录插入 ns:tbl
     *
     * @param ns  命名空间
     * @param tbl 表名
     * @param rk  行键
     * @param cf  列族名
     * @param col 列名
     * @param val 要插入的值
     */
    public void put(Table table, String ns, String tbl, String rk, String cf, String col, String val)
            throws IOException {
        // 构建Put对象，参数是行键 RowKey
        Put put = new Put(Bytes.toBytes(rk));
        put.addColumn(
                Bytes.toBytes(cf),      // 列族
                Bytes.toBytes(col),     // 列名
                Bytes.toBytes(val));    // 值
        // 插入数据
        table.put(put);
    }

    public void put(String ns, String tbl, String rk, String cf, String col, String val)
            throws IOException {
        // 获取表对象
        Table table = getTbl(ns, tbl);

        //System.out.println("in HBase put, table=" + table);

        // 构建Put对象，参数是行键 RowKey
        Put put = new Put(Bytes.toBytes(rk));
        put.addColumn(
                Bytes.toBytes(cf),      // 列族
                Bytes.toBytes(col),     // 列名
                Bytes.toBytes(val));    // 值
        // 插入数据
        table.put(put);
        table.close();
    }

    public void put(String ns, String tbl, byte[] rk, String cf, String col, String val)
            throws IOException {
        // 获取表对象
        Table table = getTbl(ns, tbl);

        // 构建Put对象，参数是行键 RowKey
        Put put = new Put(Bytes.toBytes(rk.toString()));
        put.addColumn(
                Bytes.toBytes(cf),      // 列族
                Bytes.toBytes(col),     // 列名
                Bytes.toBytes(val));    // 值
        // 插入数据
        table.put(put);
    }

    /*
    public void puts(String ns, String tbl, List<String> rks, String cf, String col, List<String> vals)
            throws IOException {
        // 获取表对象
        Table table = getTbl(ns, tbl);
        List<Put> puts = new ArrayList<>();
        for (int i = 0; i < rks.size(); i++) {
            Put put = new Put(Bytes.toBytes(rks.get(i)));
            put.addColumn(
                    Bytes.toBytes(cf),      // 列族
                    Bytes.toBytes(col),     // 列名
                    Bytes.toBytes(vals.get(i)));    // 值
        }
        table.put(puts);
    }
    */

    /**
     * 4.插入多条记录
     *
     * @param ns
     * @param tbl
     * @param rks
     * @param cf
     * @param col
     * @param vals
     * @throws IOException
     */
    //一次连接，多次puts
    public void puts(Table table, String ns, String tbl, List<byte[]> rks, String cf, String col, List<String> vals)
            throws IOException {
        // 获取表对象
        List<Put> puts = new ArrayList<>();
        Put put;
        for (int i = 0; i < rks.size(); i++) {
            put = new Put(rks.get(i));
            put.addColumn(
                    Bytes.toBytes(cf),      // 列族
                    Bytes.toBytes(col),     // 列名
                    Bytes.toBytes(vals.get(i)));    // 值
            puts.add(put);
            String s = "";
            for (byte b : rks.get(i)) s += b + ",";
            logger.debug("rowkey按照byte输出=" + s + "rowKey=" + rks.get(i) + ", getRow()=" + put.getRow() + ", col=" + col);
        }
        table.put(puts);
    }

    //一次连接，一次puts
    public void puts(String ns, String tbl, List<byte[]> rks, String cf, String col, List<String> vals)
            throws IOException {
        // 获取表对象
        Table table = getTbl(ns, tbl);
        List<Put> puts = new ArrayList<>();
        Put put;
        for (int i = 0; i < rks.size(); i++) {
            put = new Put(rks.get(i));
            put.addColumn(
                    Bytes.toBytes(cf),      // 列族
                    Bytes.toBytes(col),     // 列名
                    Bytes.toBytes(vals.get(i)));    // 值
            puts.add(put);
            String s = "";
            for (byte b : rks.get(i)) s += b + ",";
            logger.debug("rowkey按照byte输出=" + s + "rowKey=" + rks.get(i) + ", getRow()=" + put.getRow() + ", col=" + col);
        }
        table.put(puts);
        table.close();
    }

    /**
     * 5.获取值
     *
     * @param ns
     * @param tbl
     * @param rk
     * @return
     * @throws IOException
     */
    public Result get(String ns, String tbl, byte[] rk) throws IOException {
        // 获取表对象
        Table table = getTbl(ns, tbl);
        // 获取值
        Get get = new Get(rk);
        Result result = table.get(get);
        //showResult(result);
        return result;
    }

    public Result get(String ns, String tbl, String rk) throws IOException {
        // 获取表对象
        Table table = getTbl(ns, tbl);
        // 获取值
        Get get = new Get(Bytes.toBytes(rk));
        Result result = table.get(get);
        //showResult(result);
        return result;
    }

    public String getOneRecord(String ns, String tbl, String rk) throws IOException {
        // 获取表对象
        Table table = getTbl(ns, tbl);
        // 获取值
        Get get = new Get(Bytes.toBytes(rk));
        Result result = table.get(get);
        return result.toString();
    }


    /**
     * 删除记录
     *
     * @param ns
     * @param tbl
     * @param rk
     * @param cf
     * @param col
     * @throws IOException
     */
    public void delete(String ns, String tbl, String rk, String cf, String col) throws IOException {
        Table table = getTbl(ns, tbl);
        System.out.println("in delete, del start");
        Delete del = new Delete(Bytes.toBytes(rk));
        table.delete(del);
        System.out.println("in delete, del end");
        table.close();
    }

    /**
     * 删除表
     *
     * @param ns
     * @param tbl
     * @return
     * @throws IOException
     */
    public boolean dropTable(String ns, String tbl) throws IOException {
        HBaseAdmin admin = (HBaseAdmin) getConn().getAdmin();
        tbl = ns + ":" + tbl; //
        if (admin.tableExists(tbl)) {
            try {
                admin.disableTable(tbl);
                System.out.println("disabled " + tbl);
                admin.deleteTable(tbl);
                System.out.println("deleted " + tbl);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    /**
     * 扫描表
     *
     * @param ns
     * @param tbl
     * @throws IOException
     */
    public void scan(String ns, String tbl) throws IOException {
        // 获取表对象
        Table table = getTbl(ns, tbl);
        // 获取Scanner对象
        Scan scan = new Scan();
        // 通过Scan对象获取Scanner对象
        ResultScanner scanner = table.getScanner(scan);
        // 获取迭代器对象
        Iterator<Result> iterator = scanner.iterator();
        // 进行迭代，拿到Result对象
        while (iterator.hasNext()) {
            Result result = iterator.next();
            showResult(result);
        }
    }

    public void scan(String ns, String tbl, Filter filter) throws IOException {
        Table table = getTbl(ns, tbl);
        // 获取Scan对象，并且加入过滤器，
        Scan scan = new Scan();
        scan.setFilter(filter);
        Iterator<Result> iterator =
                table.getScanner(scan).iterator();
        while (iterator.hasNext()) {
            showResult(iterator.next());
        }
    }

    /**
     * 查询
     * PrefixFilter，取回rowkey以指定prefix开头的所有行
     *
     * @param ns
     * @param tbl
     * @param prefix
     * @return
     * @throws IOException
     */

    public Iterator<Result> prefixFilter(Table table, String ns, String tbl, String prefix) {
        Scan scan = new Scan();
        Filter filter = new PrefixFilter(Bytes.toBytes(prefix));
        scan.setFilter(filter);
        ResultScanner scanner = null;
        try {
            scanner = table.getScanner(scan);
            logger.debug("scanner=" + scanner);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Iterator<Result> iterator = scanner.iterator();
        logger.debug("iterator=" + iterator.toString());
        /*
        while (iterator.hasNext()) {
            showResult(iterator.next());
        }
        */
        return iterator;
    }

    public Iterator<Result> prefixFilter(String ns, String tbl, byte[] prefix) throws IOException {
        Table table = null;
        try {
            logger.info("连接HBase Table " + ns + tbl);
            table = getTbl(ns, tbl);
            logger.info("[成功] 连接到HBase Table");
        } catch (IOException e) {
            e.printStackTrace();
        }

        Scan scan = new Scan();
        Filter filter = new PrefixFilter(prefix);
        scan.setFilter(filter);
        ResultScanner scanner = null;
        try {
            scanner = table.getScanner(scan);
            logger.debug("scanner=" + scanner);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Iterator<Result> iterator = scanner.iterator();
        logger.debug("iterator=" + iterator.toString());
        table.close();
        /*
        while (iterator.hasNext()) {
            showResult(iterator.next());
        }
        */
        return iterator;
    }

    /**
     * 展示查询结果
     *
     * @param result
     */
    public void showResult(Result result) {
        // 通过Result对象获取行键
        byte[] row = result.getRow();
        // 获取Map对象
        NavigableMap<byte[],
                NavigableMap<byte[],
                        NavigableMap<Long, byte[]>>>
                map = result.getMap();
        // map中的键是什么？   -->     列族
        // map中的值是什么？   -->     某个列族对应的值
        // 遍历map
        Set<byte[]> cfs = map.keySet();
        for (byte[] cf : cfs) {
            NavigableMap<byte[],
                    NavigableMap<Long, byte[]>>
                    map1 = map.get(cf);
            // map1中的键是什么？  -->   列名
            // map1中的值是什么？  -->   某个列对应的值
            // 遍历map1
            Set<byte[]> cols = map1.keySet();
            for (byte[] col : cols) {
                NavigableMap<Long, byte[]>
                        map2 = map1.get(col);
                // map2中的键是什么？  -->   Timestamp
                // map2中的值是什么？  -->   值
                // 遍历map2
                Set<Long> tss = map2.keySet();
                for (Long ts : tss) {
                    byte[] val = map2.get(ts);
                    logger.info("行键：" + row.toString() +
                            "\t列族：" + new String(cf) +
                            "\t列名：" + new String(col) +
                            "\t时间戳：" + ts +
                            "\t值：" + new String(val));
                }
            }
        }
    }

}