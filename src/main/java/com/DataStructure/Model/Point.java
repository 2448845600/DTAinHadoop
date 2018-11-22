package com.DataStructure.Model;

import org.bson.Document;

import java.io.Serializable;

/**
 * 点
 * 32B，xy是GPS坐标
 */
public class Point implements Serializable {
    //private long pointNum;
    private CellId cellId;
    private double x;
    private double y;

    public Point(long pointNum, double xx, double yy) {
        //this.pointNum = pointNum;
        this.x = xx;
        this.y = yy;
        this.setCellId();
    }

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
        this.setCellId();
    }

    public Point() {
        //this.pointNum = 0;
        this.x = 0.00;
        this.y = 0.00;
        this.setCellId();
    }

    public CellId getCellId() {
        return cellId;
    }

    public void setCellId() {
        this.cellId = new CellId(this.getX(), this.getY());
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public Document toDocumet() {
        return new Document().append("cellId", this.cellId.getId()).append("x", this.x).append("y", this.y);
    }

    @Override
    public String toString() {
        return "(" + this.x + ", " + this.y + ")";
        //return "[坐标(" + this.x + ", " + this.y + ")" + ", cellId:" + this.cellId.getId() +"]";
    }
}
