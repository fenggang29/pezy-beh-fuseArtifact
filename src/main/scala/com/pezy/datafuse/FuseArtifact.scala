package com.pezy.datafuse

/**
  * Created by 冯刚 on 2017/8/1.
  */


import com.pezy.datafuse.license.license3j.CheckLicense
import org.apache.spark.sql.{pezyinterface, SparkSession}
import org.apache.spark.internal.Logging

import com.pezy.enginefuse.MLEngine
import com.pezy.enginefuse.GraphxEngine

class FuseArtifact extends pezyinterface with Logging{

  // 解析sql
  def analysisSql(sqlText: String,sparkSession:SparkSession): Unit ={

    //license验证
    /*val checkLicense = new CheckLicense
    val licenseResult = checkLicense.checkResult()*/
    /*sparkSession.sessionState.conf.setConfString("engine","phoenix")*/
    /*val map = sparkSession.sqlContext.getAllConfs
    val flag = map.keys.exists(engine =>
      if(engine.toString == "phoenix"){
        true
      }else{
        false
      })*/
    /*if(licenseResult.getResult == true) {*/

    if (sqlText.startsWith("mlearn")) {

      MLEngine.analysisMLearn(sqlText,sparkSession)

    } else if (sqlText.startsWith("graphx")) {

      GraphxEngine.analysisGraphx(sqlText,sparkSession)

    }/* else if (flag) {
      getPhoenixDataFrame(sqlText,sparkSession)

    } */else{
      JDBCFuse.analysisJdbc(sqlText,sparkSession)
    }
    /*}else{
      println(licenseResult.getErrMsg)
    }*/

  }


  /*def getPhoenixDataFrame(spark: SparkSession,url: String,tName: String):DataFrame={

    /*val sqltext = phoenixtext.substring(8)*/

    Class.forName("org.apache.phoenix.jdbc.PhoenixDriver")

    val conn:Connection = DriverManager.getConnection(url)

    val statement:Statement = conn.createStatement()

    val time = System.currentTimeMillis()
    val rs:ResultSet = statement.executeQuery(sqltext)
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
    /*val N = rs.getRow*/
    /*var n =0
    while (rs.next()){
      n+=1
    }*/
    /*var values : Array[Row] = new Array(n)*/
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
  }*/

}

/** Case class for converting RDD to DataFrame
  * case class Record(word: String)*/
/*case class Record(label:Int, features: (Int,Vector,Vector))*/
