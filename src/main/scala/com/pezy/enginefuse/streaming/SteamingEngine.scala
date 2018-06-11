package com.pezy.enginefuse.streaming

/**
  * Created by 冯刚 on 2018/3/25.
  */

import java.util.HashMap

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}
import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext, Time}
import scala.util.parsing.json.JSON
import scala.io.Source
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.types.StructField
import org.apache.spark.sql.types.StringType
import org.apache.spark.sql.Row
import com.pezy.datafall.WriteToKafka

object SteamingEngine {
  def main(args: Array[String]) {
    if (args.length < 1) {
      System.err.println("need configration file")
      System.exit(1)
    }

    /*val cfile = Source.fromFile("/opt/beh/core/spark/file/data.txt")*/

    val cfile = Source.fromFile(args(0))
    val str = cfile.mkString
    val b = JSON.parseFull(str)
    println(b)
    var name = " "
    var time = 2

    var inputdatasource = " "
    var inputzkQuorum = " "
    var group = " "
    var inputtopics = " "
    var numThreads = 0
    var inputcolumns = " "

    var sqls = " "

    var outputdatasource = " "
    var outputzkQuorum = " "
    var outputtopics = " "
    var outputcolumns =  " "
    var url = " "
    var sleeptime = 1000

    var hostName = " "
    var port = 0

    b match {
      // Matches if jsonStr is valid JSON and represents a Map of Strings to Any
      case Some(map: Map[String,Any]) => {
        map.get("Streaming") match{
          case Some(m:Map[String,Any]) => {
            if(m.get("name")!=None){
              name = m.get("name").get.toString
            }
            if(m.get("time")!=None){
              time = m.get("time").get.toString.toInt
            }
          }
        }
        map.get("input") match{
          case Some(m:Map[String,Any]) => {
            if(m.get("inputdatasource")!=None){
              inputdatasource = m.get("inputdatasource").get.toString
            }
            if(m.get("inputzkQuorum")!= None){
              inputzkQuorum = m.get("inputzkQuorum").get.toString
            }
            if(m.get("groupId")!=None){
              group = m.get("groupId").get.toString
            }
            if(m.get("inputtopics")!=None){
              inputtopics = m.get("inputtopics").get.toString
            }
            if(m.get("numThreads")!=None){
              numThreads = m.get("numThreads").get.toString.toInt
            }
            if(m.get("inputcolumns")!=None){
              inputcolumns = m.get("inputcolumns").get.toString
            }
            if(m.get("hostname")!=None){
              hostName = m.get("hostname").get.toString
            }
            if(m.get("port")!=None){
              port = m.get("port").get.toString.toInt
            }
          }
        }
        map.get("sql") match{
          case Some(m:Map[String,Any]) => {
            sqls = m.get("sqls").get.toString
          }
        }
        map.get("output") match{
          case Some(m:Map[String,Any]) => {
            if(m.get("outputdatasource")!=None){
              outputdatasource = m.get("outputdatasource").get.toString
            }
            if(m.get("outputzkQuorum")!= None){
              outputzkQuorum = m.get("outputzkQuorum").get.toString
            }
            if(m.get("outputtopics")!=None){
              outputtopics = m.get("outputtopics").get.toString
            }
            if(m.get("outputcolumns")!=None){
              outputcolumns = m.get("outputcolumns").get.toString
            }
            if(m.get("url")!=None){
              url = m.get("url").get.toString
            }
            if(m.get("sleeptime")!=None){
              sleeptime = m.get("sleeptime").get.toString.toInt
            }
          }
        }
      }
      case None => println("Parsing failed")
      case other => println("Unknown data structure: " + other)
    }
    val icolumns = inputcolumns.split(",")
    /*val ocolumns = outputcolumns.split(",")*/
    val sql = sqls.split(";")
    val sparkConf = new SparkConf().setAppName(name)

    //    StreamingExamples.setStreamingLogLevels()
    //
    // Create the context with a 2 second batch size
    val ssc = new StreamingContext(sparkConf, Seconds(time))

    // Create a socket stream on target ip:port and count the
    // words in input stream of \n delimited text (eg. generated by 'nc')
    // Note that no duplication in storage level only for running locally.
    // Replication necessary in distributed scenario for fault tolerance.
    ssc.checkpoint("checkpoint")
    val inputschema = StructType (
      icolumns.map(fieldName => StructField(fieldName,StringType,true))
    )

    val lines = if("kafka".equals(inputdatasource)){
      val topicMap = inputtopics.split(",").map((_, numThreads)).toMap
      KafkaUtils.createStream(ssc, inputzkQuorum, group, topicMap).map(_._2)

    }else{
      ssc.socketTextStream(hostName, port, StorageLevel.MEMORY_AND_DISK_SER)
    }

    //发送参数
    val props = new HashMap[String, Object]()
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, outputzkQuorum)
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
      "org.apache.kafka.common.serialization.StringSerializer")
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
      "org.apache.kafka.common.serialization.StringSerializer")
    /*val producer = new KafkaProducer[String, String](props)*/

    val words = lines.flatMap(_.split(" "))
    // Convert RDDs of the words DStream to DataFrame and run SQL query
    words.foreachRDD { (rdd: RDD[String], time: Time) =>
      // Get the singleton instance of SparkSession
      val spark = SparkSessionSingleton.getInstance(rdd.sparkContext.getConf)
      import spark.implicits._

      // Convert RDD[String] to RDD[case class] to DataFrame
      val rowRDD = rdd.map(p => Row(p))
      val wordsDataFrame = spark.createDataFrame(rowRDD,inputschema)
      /*rdd.map(w => Record(w)).toDF()*/
      wordsDataFrame.show
      // Creates a temporary view using the DataFrame
      wordsDataFrame.createOrReplaceTempView(inputtopics)
      // Do word count on table using SQL and print it
      for(s <- sql) {
        println("=========="+s)
        val wordCountsDataFrame = spark.sql(s)//"select word, count(*) as total from  group by word"
        println(s"========= $time =========")
        wordCountsDataFrame.show()
        /*savefortable(wordCountsDataFrame,url)*/
        val json = wordCountsDataFrame.toJSON.collectAsList().toString
        WriteToKafka.sendMessageTokafka(props,outputtopics,json,sleeptime)
      }
    }

    ssc.start()
    ssc.awaitTermination()
  }
  /*def sendMessageTokafka(props:HashMap[String,Object],topic:String,json:String,sleeptime:Int): Unit ={
    val producer = new KafkaProducer[String, String](props)
    val message = new ProducerRecord[String, String](topic, null, json)
    producer.send(message)
    Thread.sleep(sleeptime)
  }*/

}

/** Lazily instantiated singleton instance of SparkSession */
object SparkSessionSingleton {

  @transient  private var instance: SparkSession = _

  def getInstance(sparkConf: SparkConf): SparkSession = {
    if (instance == null) {
      instance = SparkSession
        .builder
        .config(sparkConf)
        .enableHiveSupport()
        .getOrCreate()

    }
    instance
  }
}
