package com.DistributeTopologicalAnalysis;

import com.DataStructure.Model.CurveInfo;
import com.DataStructure.Model.IntersectionPoint;
import org.apache.log4j.Logger;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.Util.SparkInit.setSparkConf;
import static com.Util.SparkInit.setSystemProperty;
import static com.Util.SubmitUtil.getIntersection;
import static com.Util.SubmitUtil.loadData;
import static com.Util.SubmitUtil.strToCurveInfo;

public class OriginalDTA {
    private static Logger logger = Logger.getLogger(OriginalDTA.class);

    /**
     * 传统并行算法
     * 1 读取原始数据文件（多个文件，输入其正则表达式），建立RDD
     * 2 flatMap 中，读取曲线文件，计算交点
     * 3 汇总 count()
     *
     * @param testfile  测试曲线文件，一般存在本地（Windows/Linux）
     * @param srcfileRE 原始数据文件集正则表达式,一般存在HDFS "hdfs://master:9000/haninput/test/test_600_10000_[0-9].txt"
     */
    public static String originalDTA(String testfile, String srcfileRE) {
        Date d0 = new Date();

        setSystemProperty();
        JavaSparkContext sc = setSparkConf("originalDTA");

        Date d1 = new Date();

        List<CurveInfo> testCurves = loadData(testfile);
        JavaRDD<String> srcRdd = sc.textFile(srcfileRE).cache();
        JavaRDD<IntersectionPoint> resRDD = srcRdd.flatMap(srcCurveStr -> {
            CurveInfo srcCurve = strToCurveInfo(srcCurveStr);
            List<IntersectionPoint> intersectPoints = new ArrayList<>();
            for (int i = 0; i < testCurves.size(); i++) {
                intersectPoints.addAll(getIntersection(srcCurve, testCurves.get(i)));
            }
            return intersectPoints.iterator();
        }).cache();

        Date d2 = new Date();

        resRDD.count();

        Date d3 = new Date();

        sc.close();

        String timeInfo = "[时间信息]有效时间:" + (d2.getTime() - d0.getTime()) + "ms, 总时间:" + (d3.getTime() - d0.getTime()) +
                "ms。 配置:" + (d1.getTime() - d0.getTime()) + "，求交点:" + (d2.getTime() - d1.getTime()) + ", 统计:" +
                (d3.getTime() - d2.getTime()) + "ms\n";
        return timeInfo;
    }
}
