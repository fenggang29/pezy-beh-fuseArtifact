package com.pezy.datafall

import java.util.{HashMap, Properties}

import org.apache.kafka.clients.producer.{ProducerRecord, KafkaProducer, ProducerConfig}
import org.apache.spark.sql.catalyst.InternalRow

/**
  * Created by 冯刚 on 2018/3/26.
  */
object WriteToKafka {

  def writeToKafka(properties:Properties,row:InternalRow)={
    val props = WriteToKafka.initKakfa(properties)
    val outputtopics = properties.get("outputtopics").toString
    val time = properties.get("time").toString.toInt
    val r = row.toString
    sendMessageTokafka(props,outputtopics,r,time)
  }

  def initKakfa(properties: Properties)={
    val outputzkQuorum = properties.get("outputzkQuorum").toString
    val props = new HashMap[String, Object]()
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, outputzkQuorum)
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
      "org.apache.kafka.common.serialization.StringSerializer")
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
      "org.apache.kafka.common.serialization.StringSerializer")
    props
  }

  def sendMessageTokafka(props:HashMap[String,Object],topic:String,json:String,time :Int): Unit ={
    val producer = new KafkaProducer[String, String](props)
    val message = new ProducerRecord[String, String](topic, null, json)
    producer.send(message)
    Thread.sleep(time)
  }

}
