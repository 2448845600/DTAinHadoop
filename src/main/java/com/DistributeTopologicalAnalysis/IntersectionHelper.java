package com.DistributeTopologicalAnalysis;

import com.DataStructure.Model.IntersectionPoint;
import com.DataStructure.Model.Point;
import com.DataStructure.Model.PointSet;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

import static com.Globals.CellId_GET_MAX;
import static com.Util.ByteHelper.longToBit;
import static com.Util.ByteHelper.longToBitN;
import static java.lang.Math.log;
import static java.lang.Math.max;
import static java.lang.Math.min;

public class IntersectionHelper {
    private static Logger logger = Logger.getLogger(IntersectionHelper.class);
    //返回两个点集的交点，对于这种相交，有两种思路
    //1 模糊相交，网格上的相交（近似相交），cellId 多少位相同即相交。
    //2 精确相交，现实意义的相交。

    // 1 模糊相交
    public static Boolean isFuzzyIntersect(Point p1, Point p2, int fuzzyNum) {
        if (fuzzyNum > CellId_GET_MAX) {
            fuzzyNum = CellId_GET_MAX;
        } else if (fuzzyNum < 0) {
            fuzzyNum = 0;
        }
        String id1str = longToBitN(p1.getCellId().getId(), fuzzyNum);
        String id2str = longToBitN(p2.getCellId().getId(), fuzzyNum);
        return id1str.equals(id2str);
    }

    public static Point calculateFuzzyIntersectionPoint(Point p1, Point p2, int fuzzyNum) {
        if (isFuzzyIntersect(p1, p2, fuzzyNum)) {
            return p1;
        }
        return null;
    }

    public static List<IntersectionPoint> getFuzzyIntersectionPoint(PointSet ps1, PointSet ps2, int fuzzyNum) {
        List<IntersectionPoint> resIntersectionPoint = new ArrayList<>();
        Point resPoint = new Point();
        for (Point p1 : ps1.getPoints()) {
            for (Point p2 : ps2.getPoints()) {
                resPoint = calculateFuzzyIntersectionPoint(p1, p2, fuzzyNum);
                if (resPoint != null) {
                    List<Long> inheritCurveNums = new ArrayList<>();
                    inheritCurveNums.add(ps1.getInheritNum());
                    inheritCurveNums.add(ps2.getInheritNum());

                    resIntersectionPoint.add(new IntersectionPoint(inheritCurveNums, resPoint));
                }
            }
        }

        return resIntersectionPoint;
    }


    // 2 精确相交
    // 应该把交点算出来，这样可以防止误判。存在可能是，每个网格最后一个冗余点可能是交点，这交点可能计算一次，也可能计算两次，
    // 我们根据经纬度算出交点，求CellId，判断是否在该网格中。
    // https://www.cnblogs.com/wuwangchuxin0924/p/6218494.html
    // http://www.iteye.com/topic/437556
    public static Boolean isPreciseIntersect(Point p1, Point p2, Point p3, Point p4) {
        // 快速排斥：
        // 两个线段为对角线组成的矩形，如果这两个矩形没有重叠的部分，那么两条线段是不可能出现重叠的
        if (!(min(p1.getX(), p2.getX()) <= max(p3.getX(), p4.getX()) &&
                min(p3.getY(), p4.getY()) <= max(p1.getY(), p2.getY()) &&
                min(p3.getX(), p4.getX()) <= max(p1.getX(), p2.getX()) &&
                min(p1.getY(), p2.getY()) <= max(p3.getY(), p4.getY())))
            // 这里的确如此，这一步是判定两矩形是否相交
            // 1.线段ab的低点低于cd的最高点（可能重合）
            // 2.cd的最左端小于ab的最右端（可能重合）
            // 3.cd的最低点低于ab的最高点（加上条件1，两线段在竖直方向上重合）
            // 4.ab的最左端小于cd的最右端（加上条件2，两直线在水平方向上重合）
            // 综上4个条件，两条线段组成的矩形是重合的，特别要注意一个矩形含于另一个矩形之内的情况。
            return false;

        //跨立实验：
        //如果两条线段相交，那么必须跨立，就是以一条线段为标准，另一条线段的两端点一定在这条线段的两段
        //也就是说a b两点在线段cd的两端，c d两点在线段ab的两端
        double u, v, w, z;//分别记录两个向量
        u = (p3.getX() - p1.getX()) * (p2.getY() - p1.getY()) - (p2.getX() - p1.getX()) * (p3.getY() - p1.getY());
        v = (p4.getX() - p1.getX()) * (p2.getY() - p1.getY()) - (p2.getX() - p1.getX()) * (p4.getY() - p1.getY());
        w = (p1.getX() - p3.getX()) * (p4.getY() - p3.getY()) - (p4.getX() - p3.getX()) * (p1.getY() - p3.getY());
        z = (p2.getX() - p3.getX()) * (p4.getY() - p3.getY()) - (p4.getX() - p3.getX()) * (p2.getY() - p3.getY());
        return (u * v <= 0.00000001 && w * z <= 0.00000001);
    }

    // 计算线段(p1, p2)与(p3, p4)的交点
    // 这里重叠不算交点，重叠的线段calculatePreciseIntersectionPoint的结果是(NaN,NaN)，可以理解为平行无穷远处相交
    public static Point calculatePreciseIntersectionPoint(Point p1, Point p2, Point p3, Point p4) {
        //第一条直线
        double x1 = p1.getX(), y1 = p1.getY(), x2 = p2.getX(), y2 = p2.getY();
        double a = (y1 - y2) / (x1 - x2);
        double b = (x1 * y2 - x2 * y1) / (x1 - x2);
        //第二条
        double x3 = p3.getX(), y3 = p3.getY(), x4 = p4.getX(), y4 = p4.getY();
        double c = (y3 - y4) / (x3 - x4);
        double d = (x3 * y4 - x4 * y3) / (x3 - x4);

        double resX = ((x1 - x2) * (x3 * y4 - x4 * y3) - (x3 - x4) * (x1 * y2 - x2 * y1))
                / ((x3 - x4) * (y1 - y2) - (x1 - x2) * (y3 - y4));

        double resY = ((y1 - y2) * (x3 * y4 - x4 * y3) - (x1 * y2 - x2 * y1) * (y3 - y4))
                / ((y1 - y2) * (x3 - x4) - (x1 - x2) * (y3 - y4));

        Point res = new Point(resX, resY);
        logger.debug("直线方程1: y=" + a + "x + " + b + "，直线方程2: y=" + c + "x + " + d + "， 交点(" + resX + "," + resY + ")");
        return res;
    }

    //计算交点
    public static List<IntersectionPoint> getPreciseIntersectionPoint(PointSet ps1, PointSet ps2) {
        List<IntersectionPoint> resIntersectionPoint = new ArrayList<>();
        for (int i = 0; i < ps1.getPoints().size() - 1; i++) {
            Point p1, p2;
            p1 = ps1.getPoints().get(i);
            p2 = ps1.getPoints().get(++i);
            for (int j = 0; j < ps2.getPoints().size() - 1; j++) {
                Point p3, p4;
                p3 = ps2.getPoints().get(j);
                p4 = ps2.getPoints().get(++j);
                // isPreciseIntersect(p1, p2, p3, p4)是为了计算这四个点组成的两条线段是否存在交点，
                // 以防calculatePreciseIntersectionPoint(p1, p2, p3, p4)得到的交点在延长线上
                // 这里重叠不算交点，重叠的线段calculatePreciseIntersectionPoint的结果是(NaN,NaN)，可以理解为平行无穷远处相交
                if (isPreciseIntersect(p1, p2, p3, p4)) {
                    Point resPoint = calculatePreciseIntersectionPoint(p1, p2, p3, p4);
                    if (resPoint.getX() < 90.00 && resPoint.getX() > -90.00 && resPoint.getY() < 180.00 && resPoint.getY() > -180.00) {
                        logger.debug("交点：" + resPoint);
                        List<Long> inheritCurveNums = new ArrayList<>();
                        inheritCurveNums.add(ps1.getInheritNum());
                        inheritCurveNums.add(ps2.getInheritNum());
                        resIntersectionPoint.add(new IntersectionPoint(inheritCurveNums, resPoint));
                    }
                }
            }
        }
        return resIntersectionPoint;
    }

    //计算交点（不包括切点）
    public static List<IntersectionPoint> getPreciseIntersectionPointWithoutContactPoint(PointSet ps1, PointSet ps2) {
        List<IntersectionPoint> resIntersectionPoint = new ArrayList<>();

        for (int i = 0; i < ps1.getPoints().size(); i++) {
            Point p1, p2;
            p1 = ps1.getPoints().get(i);
            p2 = ps1.getPoints().get(++i);
            for (int j = 0; j < ps2.getPoints().size(); j++) {
                Point p3, p4;
                p3 = ps2.getPoints().get(j);
                p4 = ps2.getPoints().get(++j);

                if (isPreciseIntersect(p1, p2, p3, p4)) {
                    Point resPoint = calculatePreciseIntersectionPoint(p1, p2, p3, p4);
                    if (resPoint != null) {
                        List<Long> inheritCurveNums = new ArrayList<>();
                        inheritCurveNums.add(ps1.getInheritNum());
                        inheritCurveNums.add(ps2.getInheritNum());
                        resIntersectionPoint.add(new IntersectionPoint(inheritCurveNums, resPoint));
                    }
                }
            }
        }
        return resIntersectionPoint;
    }

    public static void main(String[] args) {
        Point p1 = new Point(1.00, 1.00);
        Point p2 = new Point(5.00, 5.00);
        Point p3 = new Point(1.00, 5.00);
        Point p4 = new Point(5.00, 1.00);

        calculatePreciseIntersectionPoint(p1, p2, p3, p4);
    }

}
