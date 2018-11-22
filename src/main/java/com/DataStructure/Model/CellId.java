package com.DataStructure.Model;

import com.google.common.geometry.S2CellId;
import com.google.common.geometry.S2LatLng;

import java.io.Serializable;

public class CellId implements Serializable {
    private long id;

    public CellId() {
        this.id = 0;
    }

    public CellId(long id) {
        this.id = id;
    }

    /**
     * @param x 维度 (-90 - 90)
     * @param y 精度 (-180 - 180)
     */
    public CellId(double x, double y) {
        S2LatLng s2LatLng = S2LatLng.fromDegrees(x, y);
        long s2id = S2CellId.fromLatLng(s2LatLng).id();
        this.id = s2id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    /**
     * 将GPS坐标点point(x,y)转换为cellId
     * 利用Hilbert填充曲线
     */
    public static CellId pointToCellId(Point point) {
        S2LatLng s2LatLng = S2LatLng.fromDegrees(point.getX(), point.getY());
        long s2id = S2CellId.fromLatLng(s2LatLng).id();
        CellId cellId = new CellId(s2id);
        return cellId;
    }

}
