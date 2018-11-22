package com.DistributeTopologicalAnalysis;

import com.DataStructure.Model.Point;
import com.DataStructure.Model.PointSet;
import com.Globals;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableInputFormat;
import org.apache.hadoop.hbase.protobuf.ProtobufUtil;
import org.apache.hadoop.hbase.protobuf.generated.ClientProtos;
import org.apache.hadoop.hbase.util.Base64;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.PairFunction;
import scala.Tuple2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.DataPartition.HBaseOperator.transformPointSetByHBaseResult;
import static com.DistributeTopologicalAnalysis.IntersectionHelper.getPreciseIntersectionPoint;
import static com.Globals.nstbl;
import static com.Util.ByteHelper.BytesToBit;
import static com.Util.SparkInit.*;

/**
 * 得到相交情况报表
 */
public class GetAllIntersectReport {
    private static Logger logger = Logger.getLogger(GetAllIntersectReport.class);

    /**
     * @return 将“查询 cf - col_pointList 的记录”的 HBase scan 转换为 Base64 的字符串
     */
    public static String setScanConf() {
        Scan scan = new Scan();
        scan.addFamily(Bytes.toBytes(Globals.cf));
        scan.addColumn(Bytes.toBytes(Globals.cf), Bytes.toBytes(Globals.col));
        ClientProtos.Scan proto = null;
        try {
            proto = ProtobufUtil.toScan(scan);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String ScanToString = Base64.encodeBytes(proto.toByteArray());
        return ScanToString;
    }

    /**
     * 对整个数据库进行报表操作，计算所有交点，将所有点写入文件allIntersectReport.txt
     *
     * @return 交点个数
     */
    public static String getAllIntersectReport(int level) {
        Date d0 = new Date();

        //配置property，Spark，HBase
        setSystemProperty();
        JavaSparkContext sc = setSparkConf("Get All-Intersect Report");
        Configuration conf = setHBaseConf(nstbl);
        conf.set(TableInputFormat.SCAN, setScanConf());

        Date d1 = new Date();

        //读取数据，建立第一个RDD
        JavaPairRDD<ImmutableBytesWritable, Result> HBaseRDD = sc.newAPIHadoopRDD(conf, TableInputFormat.class,
                ImmutableBytesWritable.class, Result.class).cache();
        logger.info("得到第一个RDD:HBaseRDD，HBaseRDD.count=" + HBaseRDD.count() + "，HBaseRDD=" + HBaseRDD.toString());

        Date d2 = new Date();

        //把 JavaPairRDD<ImmutableBytesWritable, Result> HBaseRDD 转换为 JavaRDD<PointSet>
        JavaPairRDD<String, PointSet> javaPairRDD = HBaseRDD.mapToPair((PairFunction<Tuple2<ImmutableBytesWritable, Result>, String, PointSet>) tuple2 -> {
            Result result = tuple2._2;
            PointSet ps = transformPointSetByHBaseResult(result);
            String keys = BytesToBit(ps.getPointSetKey());
            String key = BytesToBit(ps.getPointSetKey()).substring(0, level);
            logger.debug("keys=" + keys + ", key=" + key + ", points.size()=" + ps.getPoints().size());
            return new Tuple2<>(key, ps);
        }).cache();
        logger.info("javaPairRDD=" + javaPairRDD + ", count=" + javaPairRDD.count());

        Date d3 = new Date();

        //聚合
        JavaPairRDD<String, Iterable<PointSet>> groupedPointSetsRDD = javaPairRDD.groupByKey().cache();
        logger.info("groupedPointSetsRDD=" + groupedPointSetsRDD + ", count=" + groupedPointSetsRDD.count());

        Date d4 = new Date();

        //判断交点，每个group返回一个交点列表
        JavaRDD<Point> resRDD = groupedPointSetsRDD.flatMap(tuple2 -> {
            List<Point> points = new ArrayList<>();
            Iterable<PointSet> pointSetIterable = tuple2._2;
            for (PointSet ps : pointSetIterable) {
                for (PointSet pst : pointSetIterable) {
                    points.addAll(getPreciseIntersectionPoint(ps, pst));
                }
            }
            return points.iterator();
        }).cache();

        Date d5 = new Date();

        //存储RDD
        String path = "hdfs://master:9000/hanoutput/allIntersectReport";
        deleteHDFSFile(conf, path);
        resRDD.saveAsTextFile(path);
        logger.info("[成功] RDD写入HDFS");

        Date d6 = new Date();

        //结束Spark
        sc.close();

        String timeInfo = "[具体时间]总有效时间:" + (d6.getTime() - d1.getTime()) + "ms。 读取HBase:" + (d2.getTime() - d1.getTime() +
                "ms, 聚合:" + (d4.getTime() - d3.getTime()) + "ms, 求交点:" + (d5.getTime() - d4.getTime()) +
                "ms, 写入HDFS:" + (d6.getTime() - d5.getTime()) + "\n");
        return timeInfo;
    }
}
