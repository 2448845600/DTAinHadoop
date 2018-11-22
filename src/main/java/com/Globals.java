package com;

public class Globals {
    public static int s = 1;
    public static String LocalDBHost = "localhost";
    public static int LocalDBPort = 27017;
    public static String LocalDBName = "st";
    public static String PointCollectionName = "points";
    public static String CurveCollectionName = "curves";
    public static String CurveDetailCollectionName = "curveDetails";
    public static String RawDataCollectionName = "rawData";

    public static int CellId_GET_MAX = 32; //HBase 中, rk 利用 CellId 的最长长度
    public static int CellId_GET_32 = 32;
    public static int CellId_GET_16 = 16;
    public static int CellId_GET_8 = 8;

    public static String ns = "spatialDB";
    public static String tbl = "pointSet";
    public static String nstbl = "spatialDB:pointSet";
    public static String nstblTest = "spatialDB:test";
    public static String tblCurve = "curve";
    public static String tblTest = "test";
    public static String cf = "info";
    public static String cfCurve = "info";
    public static String col = "pointList";
    public static String colCurve = "curve";
    public static int version = 1;
    public static int nums = 8;//分区个数

    public static String REMOTE_IP = "192.168.0.2";
    public static String REMOTE_USER = "cloud";
    public static String REMOTE_PSW = "1234";
    public static int REMOTE_SSH_PORT = 22;


    //机器数量
    public static int MACSHINE_BIT = 4;
    public static int MACSHINE_NUM = 16;
}
