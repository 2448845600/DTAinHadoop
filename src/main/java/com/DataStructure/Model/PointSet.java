package com.DataStructure.Model;

import org.apache.log4j.Logger;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.Util.ByteHelper.*;
import static java.lang.System.arraycopy;
import static java.util.Arrays.copyOfRange;

/**
 * pointSetNum 是该点集的键，方便Curve，Region找到对应的点集
 * inheritNum 是Curve，Region的键
 * cellId是BBox
 * 该结构是Curve，Region划分后的子结构，按照cellId映射到p台机器上以便分布式操作
 */
public class PointSet implements Serializable {
    private static Logger logger = Logger.getLogger(PointSet.class);
    // cellId, inheritNum, pointSetNum 这三个组合在一起
    // pointSetKey(16B) = cellId_32(4B,32) + inheritNum(8B,64) + pointSetNum(4B,32)，在setPointSetKey()里面实现
    // 有可能Curve在同一个cell里面有多段（迂回飞行），pointSetNum就是解决这个问题
    private byte[] pointSetKey;
    private CellId cellId;
    private long inheritNum;
    private int pointSetNum;
    private List<Point> points;


    public PointSet() {
        this.pointSetKey = new byte[16];
        this.cellId = new CellId();
        this.inheritNum = 0;
        this.pointSetNum = 0;
        this.points = new ArrayList<>();
    }

    public PointSet(String psk, List<Point> points) {

        //logger.debug(");
        this.pointSetKey = new byte[16];
        this.inheritNum = BitToLong(psk.substring(32, 96));
        this.pointSetNum = BitToInt(psk.substring(96, 128));
        this.points = points;
        // BUG
        // this.cellId = new CellId(BitToLong(psk.substring(0, 32)));
        // 这里得到的是long的高位32位
        this.cellId = new CellId(BitToLong(psk.substring(0, 32) + "00000000000000000000000000000000"));
        logger.debug("pointSetKey=" + psk + ", inheritNum=" + inheritNum + ", pointSetNum=" + pointSetNum + ", cellId.getId()=" + longToBit(cellId.getId()));
    }

    public PointSet(int pointSetNum, long inheritNum) {
        this.pointSetKey = new byte[16];
        this.pointSetNum = pointSetNum;
        this.inheritNum = inheritNum;
        this.points = new ArrayList<Point>();
        this.cellId = new CellId(0);
    }

    public PointSet(int pointSetNum, long inheritNum, List<Point> points, CellId cellId) {
        this.pointSetKey = new byte[16];
        this.pointSetNum = pointSetNum;
        this.inheritNum = inheritNum;
        this.points = points;
        this.cellId = cellId;
    }

    public byte[] getPointSetKey() {
        this.setPointSetKey();
        return pointSetKey;
    }

    public long getPointSetNum() {
        return pointSetNum;
    }

    public void setPointSetKey() {
        //bug: String.valueOf(this.pointSetNum) 这种操作，转换后的string长度不定，
        //this.pointSetKey = longToBitN(this.cellId.getId(), Globals.CellId_GET_32) + String.valueOf(this.inheritNum) + String.valueOf(this.pointSetNum);

        //得到128位“01......”字符串
        byte[] bytes = new byte[16];
        byte[] bt1 = copyOfRange(LongToBytes(this.cellId.getId()), 0, 4);
        byte[] bt2 = LongToBytes(this.inheritNum);
        byte[] bt3 = IntToBytes(this.pointSetNum);

        //logger.debug("bt1=" + bt1 + ", bt1.len=" + bt1.length + ", bt2=" + bt2 + ", bt2.len=" + bt2.length + ", bt3=" + bt3 + ", bt3.len=" + bt3.length);

        arraycopy(bt1, 0, bytes, 0, 4);
        arraycopy(bt2, 0, bytes, 4, 8);
        arraycopy(bt3, 0, bytes, 12, 4);

        this.pointSetKey = bytes;
        logger.debug("pointSetKey=" + BytesToBit(pointSetKey) + "len=" + pointSetKey.length);
    }

    public void setPointSetNum(int pointSetNum) {
        this.pointSetNum = pointSetNum;
    }

    public void setPoints(List<Point> points) {
        this.points = points;
    }

    public void setInheritNum(long inheritNum) {
        this.inheritNum = inheritNum;
    }

    public long getInheritNum() {
        return inheritNum;
    }

    public List<Point> getPoints() {
        return this.points;
    }

    public void setPointNums(List<Point> points) {
        this.points = points;
    }

    public CellId getCellId() {
        return cellId;
    }

    public void setCellId(CellId cellId) {
        this.cellId = cellId;
    }


    @Override
    public String toString() {
        String s = "[pointSetNum:" + this.pointSetNum + ", inheritNum:" + this.inheritNum + ", cellId:" +
                this.cellId.getId() + ", \npoints.size()=" + this.getPoints().size() + ":";
        for (Point p : this.points) {
            s = s + p.toString();
        }
        s += "]";
        return s;
    }

}
