package com.pezy.datafuse

/**
  * Created by 冯刚 on 2017/9/12.
  */

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.storage.StorageLevel
import org.apache.hadoop.hbase.{HBaseConfiguration, HTableDescriptor, TableName}
import org.apache.hadoop.hbase.mapreduce.TableInputFormat
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.hadoop.hbase.util.Bytes
import org.apache.hadoop.hbase.client.ConnectionFactory
import org.apache.hadoop.hbase.client.Get




object UDFTest {

  def main(args: Array[String]): Unit= {
    if(args.length<1){
      System.err.println("need configration")
      System.exit(1)
    }

    val sparkConf = new SparkConf().setAppName("useUDF").setMaster("local")
    val spark = SparkSession.builder().config(sparkConf).enableHiveSupport().getOrCreate()
    /*val sc = spark.sparkContext*/
    /*val usersRDD = sc.newAPIHadoopRDD(conf, classOf[TableInputFormat],
      classOf[org.apache.hadoop.hbase.io.ImmutableBytesWritable],classOf[org.apache.hadoop.hbase.client.Result])*/
    println(args(0))
    spark.udf.register("find",(id:String)=>{
      val conf = HBaseConfiguration.create()
      conf.set("hbase.zookeeper.quorum","hadoop231,hadoop232,hadoop233")
      conf.set("hbase.zookeeper.property.clientPort", "2181")
      conf.set(TableInputFormat.INPUT_TABLE, "hbasetest")
      val conn=ConnectionFactory.createConnection(conf)
      val userTable=TableName.valueOf("hbasetest")
      val table=conn.getTable(userTable)
      val g=new Get(args(0).getBytes)
      val result=table.get(g)
      val value=Bytes.toString(result.getValue("people".getBytes, "name".getBytes))
      println(value)
      value
    })
    println("+++++++++++++++++++++++++++++++")
    if(args(0)!=None) {
      val df = spark.sql("select * from test where name=find("+ args(0) + ")")
      println(df+"==============")
      df.show()
    }
  }

}
