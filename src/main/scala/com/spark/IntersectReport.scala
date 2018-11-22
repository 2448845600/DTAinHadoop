package com.spark

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hbase.{HBaseConfiguration, HColumnDescriptor, HTableDescriptor, TableName}
import org.apache.hadoop.hbase.client._
import org.apache.hadoop.hbase.mapreduce.{TableInputFormat, TableOutputFormat}
import org.apache.hadoop.hbase.util.Bytes
import org.apache.spark.{SparkConf, SparkContext}

object IntersectReport {
  val tableName = "spatialDB:pointSet"
  val cf = "info"
  val col = "pointList"
  val rk = ""

  def getHBaseConf(): Configuration = {

    val conf = HBaseConfiguration.create()
    conf.set("hbase.zookeeper.quorum", "master,slave1,slave2")
    conf.set("hbase.zookeeper.property.clientPort", "2181")
    conf
  }

  //新版API HBaseSBDC可以更新了
  def connTable(conf: Configuration): Table = {
    val conn = ConnectionFactory.createConnection(conf)
    val admin = conn.getAdmin
    val userTable = TableName.valueOf(tableName)

    if (admin.tableExists(userTable)) {
      println("Table exists!")
    } else {
      val tableDesc = new HTableDescriptor(userTable)
      tableDesc.addFamily(new HColumnDescriptor(cf.getBytes))
      admin.createTable(tableDesc)
      println("Create table success!")
    }

    val table = conn.getTable(userTable)
    table
  }

  def getIntersectionReport(): Unit = {
    System.setProperty("HADOOP_USER_NAME", "cloud")
    System.setProperty("hadoop.home.dir", "D:\\hadoop")
    val sc = new SparkContext(new SparkConf().setAppName("Report").setMaster("local"))
    val conf = getHBaseConf()

    //读取HBase所有记录
    conf.set(TableInputFormat.INPUT_TABLE, tableName)
    val HBaseRDD = sc.newAPIHadoopRDD(conf, classOf[TableInputFormat],
      classOf[org.apache.hadoop.hbase.io.ImmutableBytesWritable],
      classOf[org.apache.hadoop.hbase.client.Result]).cache()

    //处理
    val count = HBaseRDD.count()
    //println("RDD Count:" + count)
    //HBaseRDD.foreach(h => println(h._2))
    val rdd2 = HBaseRDD.map(r => {
      val result = r._2
      val key = Bytes.toString(result.getRow)
      //通过列族和列名获取列
      val value = Bytes.toString(result.getValue(cf.getBytes, col.getBytes))
      println("Row key:" + key + ", value:" + value)
      (key, value)
    })
    rdd2.foreach(println)
    //处理交点，算了，还是用Java吧，要不然要重新写好多函数


    //结束
    sc.stop()
  }

  def main(args: Array[String]): Unit = {
    getIntersectionReport()
  }
}
