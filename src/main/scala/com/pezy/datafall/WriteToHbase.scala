package com.pezy.datafall

import org.apache.hadoop.hbase.client.{Put, HTable}

import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector
import java.util.HashMap
import java.util.Properties
import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hbase.client.HTable
import org.apache.hadoop.hbase.mapred.TableOutputFormat
import org.apache.hadoop.hive.serde2.objectinspector._
import org.apache.kafka.clients.producer.{ProducerRecord, KafkaProducer, ProducerConfig}

import org.apache.spark.sql.catalyst.InternalRow
import org.apache.spark.sql.types._
import org.apache.hadoop.hbase.client.Put
import org.apache.hadoop.hbase.util.Bytes
/**
  * Created by 冯刚 on 2018/3/26.
  */
object WriteToHbase {

  var conf = HBaseConfiguration.create()
  var prewrite = false;

  def writeToHbase(row: InternalRow,wrappers:Array[(Any)=>Any],dataTypes: Seq[DataType],fieldOIs:Array[ObjectInspector],outputData:Array[Any],properties: Properties) = {

    val (table:HTable, columns) = initHbase(properties)
    //iterator.foreach{ row => {
    var i = 0
    var j = 1
    while (i < fieldOIs.length) {
      outputData(i) = if (row.isNullAt(i)) null else wrappers(i)(row.get(i, dataTypes(i)))
      i += 1
    }
    val put = new Put(Bytes.toBytes(outputData(0).toString))
    while(j < i){
      if(outputData(j)!= null){
        put.add(Bytes.toBytes(columns(j).split(":")(0)),Bytes.toBytes(columns(j).split(":")(1)),Bytes.toBytes(outputData(j).toString))
      }
      j +=1
    }
    table.put(put)
  }

  def initHbase(properties: Properties) = {
    val columnsType = properties.get("columns.types").toString.split(":")
    val columns = properties.get("hbase.columns.mapping").toString.split(",")
    val hbaseTableName = properties.get("hbase.table.name").toString
    val zk = properties.get("zk").toString.split(":")
    val zkport = zk(0)
    val zkquorum = zk(1)

    val table=if(prewrite!=true){
      conf.set("hbase.zookeeper.property.clientPort", zkport)
      conf.set("hbase.zookeeper.quorum", zkquorum)
      conf.set(TableOutputFormat.OUTPUT_TABLE, hbaseTableName)
      val table = new HTable(conf,hbaseTableName)
      /*table:HTable = gettable(conf,zkport,zkquorum,hbaseTableName)*/
      prewrite = true
      table
    }
    (table,columns)
  }

}
