package com.DistributeTopologicalAnalysis;

import com.DataStructure.Model.CurveInfo;
import com.DataStructure.Model.IntersectionPoint;
import com.DataStructure.Model.PointSet;
import com.Globals;
import com.Util.HBaseHelper;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.Table;
import org.apache.log4j.Logger;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.DataPartition.HBaseOperator.getPointSetInCellFromHBase;
import static com.DataStructure.BasicManager.CurveManager.curveToPointSet;
import static com.DistributeTopologicalAnalysis.IntersectionHelper.getPreciseIntersectionPoint;
import static com.Util.SparkInit.*;

public class GetIntersectionByCurves {
    private static Logger logger = Logger.getLogger(GetIntersectionByCurves.class);

    private static String ns = Globals.ns;
    private static String tbl = Globals.tbl;
    private static Table table = setTable(ns, tbl);

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
     * 返回与 curveInfo 相交的所有交点
     *
     * @param curves
     * @param level     相交级别 0 表示准确相交，1-32表示模糊相交
     * @param nameSpace
     * @param tableName
     * @return
     */
    public static String getIntersectionByCurves(List<CurveInfo> curves, int level, String nameSpace, String tableName) {
        //配置property，Spark，HBase

        Date d0 = new Date();

        setSystemProperty();
        JavaSparkContext sc = setSparkConf("get Intersection");

        //求交点
        //1 将 curves 划分，得到 List<pointSet> curvePartition 在内存运算即可
        //2 divisionRDD =  sc.parll......( curvePartition )
        //3 resRDD = rdd.map(r -> {读取HBase相关记录，求交点})

        Date d1 = new Date();

        List<PointSet> curvePartition = new ArrayList<>();
        for (int i = 0; i < curves.size(); i++) {
            List<PointSet> pss = curveToPointSet(curves.get(i).getCurveNum(), curves.get(i).getPoints(), level);
            curvePartition.addAll(pss);
        }
        JavaRDD<PointSet> divisionRDD = sc.parallelize(curvePartition).cache();

        Date d2 = new Date();

        JavaRDD<IntersectionPoint> resRDD = divisionRDD.flatMap(pointSet -> {
            List<IntersectionPoint> intersectPoints = new ArrayList<>();
            byte[] prefix = pointSet.getPointSetKey();
            Date d31 = new Date();
            List<PointSet> psList = getPointSetInCellFromHBase(getTable(), prefix);
            Date d32 = new Date();
            for (PointSet pstemp : psList) {
                intersectPoints.addAll(getPreciseIntersectionPoint(pointSet, pstemp));
            }
            Date d33 = new Date();
            logger.debug("getPointSetInCellFromHBase:" + (d32.getTime() - d31.getTime()) + ", all_getPreciseIntersectionPoint:" + (d33.getTime() - d32.getTime()));

            return intersectPoints.iterator();
        }).cache();

        Date d3 = new Date();

        resRDD.count();

        Date d4 = new Date();

        sc.close();
        try {
            getTable().close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String timeInfo = "[具体时间]总有效时间:" + (d4.getTime() - d0.getTime()) + "ms。 划分曲线:" + (d2.getTime() - d1.getTime() +
                "ms, 求交点:" + (d3.getTime() - d2.getTime()) + "ms, 统计:" + (d4.getTime() - d3.getTime()) + "ms\n");
        return timeInfo;
    }
}

