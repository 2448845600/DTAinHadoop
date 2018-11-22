package com.DataStructure.Model;

import java.util.List;

public class Region {
    private long regionId;
    private CellId cellId;
    private List<Long> divisionPointSetNums;

    public long getRegionId() {
        return regionId;
    }

    public void setRegionId(long regionId) {
        this.regionId = regionId;
    }

    public CellId getCellId() {
        return cellId;
    }

    public void setCellId(CellId cellId) {
        this.cellId = cellId;
    }

    public List<Long> getDivisionPointSetNums() {
        return divisionPointSetNums;
    }

    public void setDivisionPointSetNums(List<Long> divisionPointSetNums) {
        this.divisionPointSetNums = divisionPointSetNums;
    }
}


