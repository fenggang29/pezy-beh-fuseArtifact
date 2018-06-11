package com.pezy.datafuse

/**
  * Created by 冯刚 on 2018/3/29.
  */

import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.Statement
import java.sql.ResultSetMetaData
import scala.collection.mutable.ArrayBuffer
import org.apache.spark.sql.types.{ StructType, StringType, StructField}
import org.apache.spark.sql.{DataFrame, SparkSession, Row}
import scala.collection.convert.wrapAsJava.bufferAsJavaList
object HiveFuse {

  def getHiveData(spark: SparkSession,url: String,tName: String):DataFrame={

    Class.forName("org.apache.hive.jdbc.HiveDriver")

    val conn:Connection = DriverManager.getConnection(url)

    val statement:Statement = conn.createStatement()

    val time = System.currentTimeMillis()
    val rs:ResultSet = statement.executeQuery("select * from "+tName)
    var list:List[Row] = List()
    val md : ResultSetMetaData  = rs.getMetaData
    val columncount = md.getColumnCount
    val columns : Array[String] = new Array[String](columncount)
    //将列名组成schema
    for(i <- 0 to columncount-1){
      columns(i) = md.getColumnName(i+1)
    }
    val schema = StructType (
      columns.map(fieldName => StructField(fieldName,StringType,true))
    )
    val rowvalues : Array[String] = new Array(columncount)
    val values = new ArrayBuffer[Row]
    while (rs.next()) {
      for(i <- 0 to columncount-1){
        rowvalues(i) = rs.getString(i+1)
      }
      val row= Row.fromSeq(rowvalues.toSeq)
      values += row
    }

    val rows = values.toArray.toList
    val s:java.util.List[Row] = bufferAsJavaList(rows.toBuffer)
    val df = spark.createDataFrame(s,schema)
    val timeUsed = System.currentTimeMillis() - time
    System.out.println("time " + timeUsed + "mm")

    rs.close()
    statement.close()
    conn.close()

    df
  }



}
