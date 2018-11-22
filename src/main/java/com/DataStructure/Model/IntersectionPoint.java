package com.DataStructure.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据结构：交点
 * 继承 Point，增加inheritCurveNums，存储该交点所属曲线的num
 */
public class IntersectionPoint extends Point {

    private List<Long> inheritCurveNums;

    public IntersectionPoint(List<Long> inheritCurveNums, Point p) {
        this.inheritCurveNums = inheritCurveNums;
        this.setX(p.getX());
        this.setY(p.getY());
        //
    }

    public IntersectionPoint() {
        this.inheritCurveNums = new ArrayList<>();
    }

    public IntersectionPoint(List<Long> inheritCurveNums) {
        this.inheritCurveNums = inheritCurveNums;
    }

    public List<Long> getInheritCurveNums() {
        return inheritCurveNums;
    }

    public void setInheritCurveNums(List<Long> inheritCurveNums) {
        this.inheritCurveNums = inheritCurveNums;
    }

}
