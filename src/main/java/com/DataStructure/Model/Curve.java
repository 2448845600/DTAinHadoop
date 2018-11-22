package com.DataStructure.Model;

import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * 曲线
 * curveNum是曲线的键
 * cellId是BBox，有序的，按照顺序连接后得到曲线
 * divisionPointSetNums是该曲线划分后的点集PointSetNum的list
 */
public class Curve {
    public long curveNum;
    public CellId cellId;
    public List<byte[]> divisionPointSetNums;

    public Curve() {
        this.curveNum = 0;
        this.cellId = new CellId();
        this.divisionPointSetNums = new ArrayList<>();
    }

    public Curve(long curveNum) {
        this.curveNum = curveNum;
        this.cellId = new CellId();
        this.divisionPointSetNums = new ArrayList<>();
    }

    public Curve(long curveNum, long id) {
        this.curveNum = curveNum;
        this.cellId = new CellId(id);
        this.divisionPointSetNums = new ArrayList<>();
    }

    public Curve(long curveNum, CellId cellId) {
        this.curveNum = curveNum;
        this.cellId = cellId;
        this.divisionPointSetNums = new ArrayList<>();
    }

    ////////////////////////
    //////////测试//////////
    ///////////////////////
    public Curve(String curveNum, List<byte[]> divisionPointSetNums) {
        this.curveNum = Long.parseLong(curveNum);
        this.setDivisionPointSetNums(divisionPointSetNums);
    }

    public long getCurveNum() {
        return curveNum;
    }

    public void setCurveNum(long curveNum) {
        this.curveNum = curveNum;
    }

    public CellId getCellId() {
        return cellId;
    }

    public void setCellId(CellId cellId) {
        this.cellId = cellId;
    }

    public List<byte[]> getDivisionPointSetNums() {
        return divisionPointSetNums;
    }

    public void setDivisionPointSetNums(List<byte[]> divisionPointSetNums) {
        this.divisionPointSetNums = divisionPointSetNums;
    }

    public Document toDocumet() {
        return new Document().append("curveNum", this.curveNum).append("cellId", this.cellId.getId());
    }

    @Override
    public String toString() {
        return "curveNum:" + this.curveNum + ", cellId:" + this.cellId.getId();
    }
}
