package com.DataPartition;

import com.spark.HBaseSDBC;

public class PartitionByScala {
    private static String ns = "spatialDB";
    private static String tbl = "pointSet";
    private static String cf = "info";
    private static String col_pointSetNum = "pointSetNum";
    private static String col_pointList = "pointList";

    public static void main(String[] args) {
        System.setProperty("HADOOP_USER_NAME", "cloud");
        System.setProperty("hadoop.home.dir", "D:\\hadoop");

        //从 HDFS 读取数据
        String file = "hdfs://master:9000/haninput/FileToHBase.txt";
        HBaseSDBC.pointSetRDDToHBase(file, ns, tbl, cf, col_pointList);
    }
}
