package com.DataStructure.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * 存储 RegionInfo 的点
 *
 * 将 Region 划分为一个个 PointSet 存储；找到这些 PointSet 后，
 * 汇集信息，得到 RegionInfo，便于分析区域状况，如包含关系。
 */
public class RegionInfo extends Points{
    private long regionId;


    RegionInfo() {
        this.setRegionId(0);
        this.setPoints(new ArrayList<>());
    }

    public long getRegionId() {
        return regionId;
    }

    public void setRegionId(long regionId) {
        this.regionId = regionId;
    }

    public List<Point> getPoints() {
        return points;
    }

    public void setPoints(List<Point> points) {
        this.points = points;
    }

}
