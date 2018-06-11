package com.pezy.test

import java.util.Properties

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

/**
  * Created by 冯刚 on 2018/1/30.
  */
object JdbcTest {

  def main(args: Array[String]): Unit= {

    /*if(args.length<1){
      System.err.println("need configration")
      System.exit(1)
    }*/
    val sparkConf = new SparkConf().setAppName("testJDBC").setMaster("local")
    val spark = SparkSession.builder().config(sparkConf).enableHiveSupport().getOrCreate()

    val prop = new Properties()

    val part =Array(
      "2017-02-01"->"2017-03-01",
      "2017-03-01"->"2017-04-01",
      "2017-04-01"->"2017-05-01",
      "2017-05-01"->"2017-06-01",
      "2017-06-01"->"2017-07-01",
      "2017-07-01"->"2017-08-01",
      "2017-08-01"->"2017-09-01",
      "2017-09-01"->"2017-10-01",
      "2017-10-01"->"2017-11-01",
      "2017-11-01"->"2017-12-01",
      "2017-12-01"->"2018-01-01")
      .map{
        case(start,end)=>
          s"sdate >= to_date('$start','yyyy-mm-dd') " + s"AND sdate <= to_date('$end','yyyy-mm-dd')"
      }

    prop.setProperty("user","gzy")

    prop.setProperty("password","gzy123")

    val url = "jdbc:oracle:thin:@132.98.23.28:1521:ORCL"

    val df = spark.sqlContext.read.jdbc(url,"tablenames",part,prop)

    df.show

  }

}
