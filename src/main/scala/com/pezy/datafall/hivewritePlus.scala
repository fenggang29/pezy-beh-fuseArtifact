package com.pezy.datafall

/**
  * Created by 冯刚 on 2018/3/26.
  */

import java.util.Properties
import org.apache.hadoop.hbase.HBaseConfiguration

import org.apache.hadoop.hive.serde2.objectinspector._
import org.apache.kafka.clients.producer.{ProducerRecord, KafkaProducer, ProducerConfig}

import org.apache.spark.sql.hive.pezyhivewriteif

import org.apache.hadoop.hive.ql.plan.TableDesc

import org.apache.spark.sql.catalyst.InternalRow
import org.apache.spark.sql.types._

class hivewritePlus extends pezyhivewriteif{

  var conf = HBaseConfiguration.create()
  var prewrite = false;
  var properties: Properties = new Properties()

  def pezyhivewrite(row:InternalRow,wrappers:Array[(Any)=>Any],dataTypes: Seq[DataType],fieldOIs:Array[ObjectInspector], outputData:Array[Any], standardOI: StructObjectInspector,tableDesc: TableDesc) : Unit = {

    properties= tableDesc.getProperties
    println(properties)

    if(properties.get("outputdatasource").toString.equals("kafka")){
      WriteToKafka.writeToKafka(properties,row)
    }else{
      WriteToHbase.writeToHbase(row,wrappers,dataTypes,fieldOIs,outputData,properties)
    }

  }

}
