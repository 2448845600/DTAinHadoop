package com.Util;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.mapreduce.TableInputFormat;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;

import java.io.IOException;

public class SparkInit {

    public static void setSystemProperty() {
        System.setProperty("HADOOP_USER_NAME", "cloud");
        //System.setProperty("hadoop.home.dir", "D:\\hadoop");
    }

    public static JavaSparkContext setSparkConf(String AppName) {
        SparkConf sparkConf = new SparkConf();
        sparkConf.setAppName(AppName);
        //sparkConf.setMaster("local");//远程调用，master是local
        sparkConf.setMaster("yarn-client");
        //sparkConf.setMaster("yarn-cluster");

        JavaSparkContext sc = new JavaSparkContext(sparkConf);
        return sc;
    }

    public static Configuration setHBaseConf() {
        Configuration conf = HBaseConfiguration.create();
        conf.set("hbase.zookeeper.property.clientPort", "2181");
        conf.set("hbase.zookeeper.quorum", "master,slave1,slave2");
        return conf;
    }

    public static Configuration setHBaseConf(String tableName) {
        Configuration conf = HBaseConfiguration.create();
        conf.set("hbase.zookeeper.property.clientPort", "2181");
        conf.set("hbase.zookeeper.quorum", "master,slave1,slave2");
        conf.set(TableInputFormat.INPUT_TABLE, tableName);
        return conf;
    }

    public static void deleteHDFSFile(Configuration conf, String pathStr) {
        Path path = new Path(pathStr);
        try {
            FileSystem fs = FileSystem.get(conf);
            if (fs.exists(path)) {
                fs.delete(path, true);
                System.out.println("存在此输出路径，已删除！！！");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
