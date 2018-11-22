package com.DistributeTopologicalAnalysis;

import com.DataStructure.Model.Point;
import com.DataStructure.Model.RegionInfo;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.List;

public class ContainHelper {

    /**
     * 判断点 point 是否在多边形中 regionInfo 中
     * 利用了 Java 自带的 Polygon 函数
     *
     * @param point
     * @param regionInfo
     * @return
     */
    public boolean isContains(Point point, RegionInfo regionInfo) {
        java.awt.Polygon p = new Polygon();

        final int TIMES = 1000;
        for (Point d : regionInfo.getPoints()) {
            int x = (int) point.getX() * TIMES;
            int y = (int) point.getY() * TIMES;
            p.addPoint(x, y);
        }
        int x = (int) point.getX() * TIMES;
        int y = (int) point.getY() * TIMES;
        return p.contains(x, y);
    }
}
