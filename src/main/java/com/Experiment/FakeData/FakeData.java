package com.Experiment.FakeData;

import java.util.ArrayList;
import java.util.List;

public class FakeData {
    public int curveNo;
    public List<Double> xys;

    FakeData() {
        this.xys = new ArrayList<>();
    }

    @Override
    public String toString() {
        String s = this.curveNo + ":";
        for (double d : this.xys) {
            s += d + " ";
        }
        s += "\n";
        return s;
    }
}
