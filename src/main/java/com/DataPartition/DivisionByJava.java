package com.DataPartition;

import com.DataStructure.BasicManager.CurveManager;
import com.DataStructure.Model.Curve;
import com.DataStructure.Model.Point;
import com.DataStructure.Model.PointSet;
import com.Globals;
import com.Util.HBaseHelper;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.Table;
import org.apache.log4j.Logger;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.DataPartition.DataFormat.getPointListByLineRDD;
import static com.DataPartition.HBaseOperator.putCurveToHBase;
import static com.DataPartition.HBaseOperator.putPointSetListToHBase;
import static com.Globals.tblCurve;
import static com.Util.SparkInit.*;

public class DivisionByJava {
    private static Logger logger = Logger.getLogger(DivisionByJava.class);

    private static String ns = Globals.ns;
    private static String tbl = Globals.tbl;
    private static Table table = setTable(ns, tbl);
    private static Table tableCurve = setTableCurve(ns, tblCurve);

    private static Table setTableCurve(String nameSpace, String tableName) {
        Table table = null;
        try {
            HBaseHelper helper = new HBaseHelper();
            table = helper.getTbl(nameSpace, tableName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return table;
    }

    private static Table getTableCurve() {
        return tableCurve;
    }

    private static Table setTable(String nameSpace, String tableName) {
        Table table = null;
        try {
            HBaseHelper helper = new HBaseHelper();
            table = helper.getTbl(nameSpace, tableName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return table;
    }

    private static Table getTable() {
        return table;
    }

    /**
     * 从 HDFS 读取原始数据，划分后存入 HBase
     * 1 从HDFS读取数据，建立第一个fileRDD
     * 2 对于每一行，划分，建立一个PointSetList，一个Curve，每 10 行写入 HBase 一次，有两种写入，pointSet 和 curve
     * （文件 1 行 10000 个点，10000 * 32 b = 320 000 b = 320 kb，生成一个 Curve 约 100 b。10 行 320 kb * 10 + 100 * 10 b = 4M）
     * 3 保存resRDD到文件
     *
     * @param inputFile    输入文件
     * @param outputFolder 输出文件夹
     */
    public static String divideFromFileToHBase(String inputFile, String outputFolder, int level) {
        //配置
        Date d0 = new Date();

        setSystemProperty();
        JavaSparkContext sc = setSparkConf("divideCurveToPointSet");
        Configuration conf = setHBaseConf();

        Date d1 = new Date();

        //JavaRDD
        logger.info("开始读取数据");
        JavaRDD<String> fileRDD = sc.textFile(inputFile, 4).cache();
        JavaRDD<String> resRDD = fileRDD.map((Function<String, String>) s -> {
            String[] split = s.split(":");
            String curveNum = split[0];
            String pointListStr = split[1];
            List<Point> points = getPointListByLineRDD(pointListStr);
            List<PointSet> pointSetList = CurveManager.curveToPointSet(Long.parseLong(curveNum), points, level);
            Curve curve = new Curve();
            List<byte[]> divPointSetNums = new ArrayList<>();
            for (PointSet ps : pointSetList) {
                divPointSetNums.add(ps.getPointSetKey());
            }
            curve.setDivisionPointSetNums(divPointSetNums);
            curve.setCurveNum(Long.parseLong(curveNum));

            // 之前出现 bug 以及运行缓慢的原因：
            // 1 putPointSetListToHBase，putCurveToHBase对应的tableName错误
            // 2 之前函数内部实现是
            //   HBaseHelper hh = new HBaseHelper();
            //   hh.puts(table, ns, tableName, rks, cf, col, vals);
            //   仔细研究发现，HBaseHelper hh = new HBaseHelper() 会建立新的HBaseHelper对象，即会建立新的连接
            // 3 每次建立行的Table，速度慢，且使用后忘记释放，占空间
            //
            // 对策：
            // 1 改 tableName
            // 2 放弃调用HBaseHelper，自己写相应函数
            // 3 重写putPointSetListToHBase，putCurveToHBase，每台机器连接一次Table不急着释放。

            putPointSetListToHBase(getTable(), pointSetList);
            putCurveToHBase(getTableCurve(), curve);

            points.clear();
            pointSetList.clear();
            divPointSetNums.clear();
            logger.debug("points.length" + points.size());
            return curveNum;
        });

        Date d2 = new Date();

        long resNum = resRDD.count();

        Date d3 = new Date();
        logger.info("resRDD.count()=" + resNum);
        //deleteHDFSFile(conf, outputFolder);
        //resRDD.saveAsTextFile(outputFolder);
        //resRDD.saveAsTextFile(outputFolder);
        //logger.info("数据保存在文件中");

        //结束Spark
        sc.close();


        String timeInfo = "[具体时间]总有效时间:" + (d2.getTime() - d0.getTime()) + "ms。 划分:" + (d2.getTime() - d1.getTime() + "ms, 统计交点个数:" + (d3.getTime() - d2.getTime()) + "ms\n");
        return timeInfo;
    }

}

