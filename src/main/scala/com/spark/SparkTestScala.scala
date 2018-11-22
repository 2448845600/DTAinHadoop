package com.spark

import org.apache.spark.{SparkConf, SparkContext}

object SparkTestScala {
  def wordCount(file: String): Unit = {
    val conf = new SparkConf()
      .setAppName("Spark Wordcount")
      .setMaster("local")
//      .set("spark.driver.host","192.168.1.129")
//      .setJars(List("E:\\Project\\JAVA\\SparkDemo\\out\\artifacts\\SparkDemo_jar\\SparkDemo.jar"))
    val sc =new SparkContext(conf)
    val lines = sc.textFile(file) //HDFS地址

    val words = lines.flatMap(_.split(" ")).filter(word => word != " ")  // 拆分单词，并过滤掉空格，当然还可以继续进行过滤，如去掉标点符号

    val pairs = words.map(word => (word, 1))  // 在单词拆分的基础上对每个单词实例计数为1, 也就是 word => (word, 1)

    val wordscount = pairs.reduceByKey(_ + _)  // 在每个单词实例计数为1的基础之上统计每个单词在文件中出现的总次数, 即key相同的value相加
    //  val wordscount = pairs.reduceByKey((v1, v2) => v1 + v2)  // 等同于

    wordscount.collect.foreach(println)  // 打印结果，使用collect会将集群中的数据收集到当前运行drive的机器上，需要保证单台机器能放得下所有数据

    sc.stop()   // 释放资源
  }
}
