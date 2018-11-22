package com.spark

import com.DataStructure.BasicManager.CurveManager
import org.apache.hadoop.hbase.client.{HBaseAdmin, Put, Result}
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.hadoop.hbase.mapreduce.{TableInputFormat, TableOutputFormat}
import org.apache.hadoop.hbase.util.Bytes
import org.apache.hadoop.hbase.{HBaseConfiguration, HTableDescriptor, TableName}
import org.apache.hadoop.mapreduce.Job
import org.apache.spark._

import scala.collection.mutable.ArrayBuffer

/**
  * 注意这些参数，错了就连接不了或者查询结果为空 ns  命名空间；tbl 表名；rk  行键；cf  列族名；col 列名
  * https://blog.csdn.net/gpwner/article/details/73530134
  * https://blog.csdn.net/u013468917/article/details/52822074
  */
object HBaseSDBC {

  /*
  def insertHBase(): Unit = {
    val sc = new SparkContext(new SparkConf().setMaster("local").setAppName("HBaseSDBC"))
    val rdd = sc.makeRDD(Array(1)).flatMap(_ => 0 to 1000000)
    rdd.map(value => {
      var put = new Put(Bytes.toBytes(value.toString))
      put.addColumn(Bytes.toBytes("f1"), Bytes.toBytes("c1"), Bytes.toBytes(value.toString))
      put
    }).foreachPartition(iterator => {
      var jobConf = new JobConf(HBaseConfiguration.create())
      //jobConf.set("hbase.zookeeper.quorum", "master,slave1,slave2")
      //jobConf.set("zookeeper.znode.parent", "/hbase")
      //jobConf.setOutputFormat(classOf[TableOutputFormat])
      val table = new HTable(jobConf, TableName.valueOf("pointSet"))
      import scala.collection.JavaConversions._
      table.put(seqAsJavaList(iterator.toSeq))
    })
  }
*/

  def pointSetRDDToHBase(filePath: String, ns: String, tbl: String, cf: String, col: String): Unit = {
    val sparkConf = new SparkConf().setAppName("RDDToHBase").setMaster("local")
    val sc = new SparkContext(sparkConf)
    val tableName = ns + ":" + tbl

    sc.hadoopConfiguration.set("hbase.zookeeper.quorum", "master,slave1,slave2")
    sc.hadoopConfiguration.set("hbase.zookeeper.property.clientPort", "2181")
    sc.hadoopConfiguration.set(TableOutputFormat.OUTPUT_TABLE, tableName)

    val job = new Job(sc.hadoopConfiguration)
    job.setOutputKeyClass(classOf[ImmutableBytesWritable])
    job.setOutputValueClass(classOf[Result])
    job.setOutputFormatClass(classOf[TableOutputFormat[ImmutableBytesWritable]])

    val fileRDD = sc.textFile(filePath) // 建立第一个RDD
    val rdd = fileRDD.map(_.split(':')).map {
      arr => {
        val put = new Put(Bytes.toBytes(arr(0))) //rk
        put.add(Bytes.toBytes(cf), Bytes.toBytes(col), Bytes.toBytes(arr(1))) //name
        (new ImmutableBytesWritable, put)
      }
    }

    rdd.saveAsNewAPIHadoopDataset(job.getConfiguration())
  }

  /*
  先用Java把文件处理一遍
  curveNum pointx pointy pointx pointy ......
   */
  def curveRDDToHBase(filePath: String, ns: String, tbl: String, cf: String, col: String): Unit = {
    val sparkConf = new SparkConf().setAppName("RDDToHBase").setMaster("local")
    val sc = new SparkContext(sparkConf)
    val tableName = ns + ":" + tbl

    sc.hadoopConfiguration.set("hbase.zookeeper.quorum", "master,slave1,slave2")
    sc.hadoopConfiguration.set("hbase.zookeeper.property.clientPort", "2181")
    sc.hadoopConfiguration.set(TableOutputFormat.OUTPUT_TABLE, tableName)

    val job = new Job(sc.hadoopConfiguration)
    job.setOutputKeyClass(classOf[ImmutableBytesWritable])
    job.setOutputValueClass(classOf[Result])
    job.setOutputFormatClass(classOf[TableOutputFormat[ImmutableBytesWritable]])

    val fileRDD = sc.textFile(filePath) // 建立第一个RDD
    val rdd = fileRDD.map(_.split(' ')).map {
      arr => {
        val put = new Put(Bytes.toBytes(arr(0))) //rk
        var curveNum = 0;
        for (i <- 1 until arr.length / 2) {
          var point = ""
          point :+ arr(i) :+ arr(i + 1)
        }


        put.add(Bytes.toBytes(cf), Bytes.toBytes(col), Bytes.toBytes(arr(1))) //name
        (new ImmutableBytesWritable, put)
      }
    }

    rdd.saveAsNewAPIHadoopDataset(job.getConfiguration())
  }

  /**
    * 将RDD存入HBase
    */
  def RDDToHBase(): Unit = {
    val sparkConf = new SparkConf().setAppName("RDDToHBase").setMaster("local")
    val sc = new SparkContext(sparkConf)
    val tablename = "spatialDB:pointSet"

    sc.hadoopConfiguration.set("hbase.zookeeper.quorum", "master,slave1,slave2")
    sc.hadoopConfiguration.set("hbase.zookeeper.property.clientPort", "2181")
    sc.hadoopConfiguration.set(TableOutputFormat.OUTPUT_TABLE, tablename)

    val job = new Job(sc.hadoopConfiguration)
    job.setOutputKeyClass(classOf[ImmutableBytesWritable])
    job.setOutputValueClass(classOf[Result])
    job.setOutputFormatClass(classOf[TableOutputFormat[ImmutableBytesWritable]])

    val indataRDD = sc.makeRDD(Array("1,jack,15", "2,Lily,16", "3,mike,16"))
    val rdd = indataRDD.map(_.split(',')).map { arr => {
      val put = new Put(Bytes.toBytes(arr(0))) //rk
      put.add(Bytes.toBytes("info"), Bytes.toBytes("name"), Bytes.toBytes(arr(1))) //name
      put.add(Bytes.toBytes("info"), Bytes.toBytes("age"), Bytes.toBytes(arr(2).toInt)) //age
      (new ImmutableBytesWritable, put)
    }
    }

    rdd.saveAsNewAPIHadoopDataset(job.getConfiguration())
  }

  def HBaseToRDD(): Unit = {
    val sparkConf = new SparkConf().setAppName("HBaseToRDD").setMaster("local")
    val sc = new SparkContext(sparkConf)

    val tablename = "spatialDB:pointSet"
    val conf = HBaseConfiguration.create()
    sc.hadoopConfiguration.set("hbase.zookeeper.quorum", "master,slave1,slave2")
    sc.hadoopConfiguration.set("hbase.zookeeper.property.clientPort", "2181")
    conf.set(TableInputFormat.INPUT_TABLE, tablename)

    // 如果表不存在则创建表
    val admin = new HBaseAdmin(conf)
    if (!admin.isTableAvailable(tablename)) {
      val tableDesc = new HTableDescriptor(TableName.valueOf(tablename))
      admin.createTable(tableDesc)
    }

    //读取数据并转化成rdd
    val hBaseRDD = sc.newAPIHadoopRDD(conf, classOf[TableInputFormat],
      classOf[org.apache.hadoop.hbase.io.ImmutableBytesWritable],
      classOf[org.apache.hadoop.hbase.client.Result])

    val count = hBaseRDD.count()
    println("count=" + count)
    hBaseRDD.foreach { case (_, result) => {
      //获取行键
      val key = Bytes.toString(result.getRow)
      //通过列族和列名获取列
      var name = "hh"
      if (result.getValue("info".getBytes, "name".getBytes) != null) {
        name = Bytes.toString(result.getValue("info".getBytes, "name".getBytes))
      }

      var age = 1
      if (result.getValue("info".getBytes, "age".getBytes) != null) {
        age = Bytes.toInt(result.getValue("info".getBytes, "age".getBytes))
      }

      println("Row key:" + key + " Name:" + name + " Age:" + age)
    }
    }

    sc.stop()
    admin.close()
  }

}
