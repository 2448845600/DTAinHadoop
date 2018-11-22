package com.DataStructure.Model;

import java.util.ArrayList;
import java.util.List;

public class CurveInfo extends Points {
    private long curveNum;

    public CurveInfo() {
        this.setCurveNum(0);
        this.setPoints(new ArrayList<>());
    }

    public long getCurveNum() {
        return curveNum;
    }

    public void setCurveNum(long curveNum) {
        this.curveNum = curveNum;
    }

    public List<Point> getPoints() {
        return points;
    }

    public void setPoints(List<Point> points) {
        this.points = points;
    }

    @Override
    public String toString() {
        String s = "curveNum=" + this.curveNum + ", number of points=" + this.points.size() + ", points={";
        for (int i = 0; i < this.points.size() - 1; i++) {
            s += "(" + this.points.get(i).getX() + "," + this.points.get(i).getY() + ")";
        }
        s = s + "(" + this.points.get(this.points.size() - 1).getX() + "," + this.points.get(this.points.size() - 1).getY() + ")}";
        return s;
    }
}
