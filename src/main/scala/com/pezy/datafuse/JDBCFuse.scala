package com.pezy.datafuse
/**
  * Created by 冯刚 on 2018/3/26.
  */

import java.util.Properties

import org.apache.spark.sql.SparkSession

import scala.collection.mutable.ArrayBuffer


object JDBCFuse {

  def analysisJdbc(sqlText: String,sparkSession:SparkSession):Unit = {

    //解析代码
    val logicalPlan = sparkSession.sessionState.sqlParser.parsePlan(sqlText)

    val logicalSt = logicalPlan.treeString
    val st = logicalSt.split("\n")

    var s = new Array[String](5)
    var tableName = new String()
    var dbName = new String()

    for(elem <- st) {
      if(elem.contains("InsertIntoTable")&&elem.contains("UnresolvedRelation")){
        s =  elem.split("`")

        if(s.length == 5){
          dbName = s(1)
          tableName = s(3)
        }
        if(s.length == 3){
          dbName = sparkSession.sessionState.catalog.getCurrentDatabase
          tableName = s(1)
        }
      }
      if (elem.contains("UnresolvedRelation")) {
        s = elem.split("`")
        if (s.length == 2) {
          dbName = sparkSession.sessionState.catalog.getCurrentDatabase
          tableName = s(1)
        }
        if (s.length == 4) {
          dbName = s(1)
          tableName = s(3)
        }
        val dbTable: String = dbName + "." + tableName
        //check方法返回option

        val df = sparkSession.sqlContext.emptyDataFrame

        val check = df.lookupPezyTable(dbTable, tableName)

        if (check == None) {
          selectDataSource(dbName, tableName, sparkSession)
        }
      }
    }

  }

  //获取数据源信息，链接数据源读取数据，并注册成临时表
  def selectDataSource(dbName:String,tableName:String,sparkSession: SparkSession): Unit ={

    try{
      val tableMessage = sparkSession.sharedState.externalCatalog.getTable(dbName,tableName)
      val tblProperties = tableMessage.properties
      /*val partition = tableMessage.partitionColumnNames*/
      //分区依据
      val ext = tblProperties.get("ext")

      if(ext!=None){
        val DB = tblProperties.get("DB")

        val cDB = tblProperties.get("cDB")

        val tName = tblProperties.get("tableName")

        val par = tblProperties.get("partition")

        val myDB = sparkSession.sharedState.externalCatalog.getDatabase(dbName)
        val dbProperties = myDB.properties
        var message = new Array[String](3)

        if(cDB != None){
          val value = dbProperties.get(cDB.get)
          println(value.get)
          if(value != None){
            message = value.get.split(" ")
          }
        }else if(DB != None){
          val value = dbProperties.get(DB.get)
          if(value != None){
            message = value.get.split(" ")
          }
        }else{
          throw new Exception("Unknow jdbc type")
        }

        if(message.length>1){
          val url = message(0)
          val user = message(1)
          val password = message(2)
          val prop = new Properties()
          prop.setProperty("user",user)
          prop.setProperty("password",password)

          val partition = new ArrayBuffer[(String,String)]()
          val part = par.get.split('|')

          val jdbcdf = if(!(par.get==" "||par.get=="")&&DB.get.equals("mysql")){
            sparkSession.sqlContext.read.jdbc(url,tName.get,part,prop)
          }else if(!(par.get==" "||par.get=="")&&DB.get.equals("oracle")){
            sparkSession.sqlContext.read.jdbc(url,tName.get,part,prop)
          }else{
            sparkSession.sqlContext.read.format("jdbc").options(Map("url" -> url,"user" -> user,"password" -> password
              ,"dbtable" -> tName.get)).load()
          }
          jdbcdf.registerPezyTable(dbName, tableName)

        }else{
          val zkUrl = message(0)
          if(DB.get.equals("phoenix")){
            val phoenixdf = sparkSession.read.format("org.apache.phoenix.spark").options(Map("table" -> "test", "zkUrl" -> "hadoop233:2181")).load
            /*val ph = new FuseArtifact
            val phoenixdf = ph.getPhoenixDataFrame(sparkSession,zkUrl,tName.get.toString)*/
            phoenixdf.registerPezyTable(dbName, tableName)
          }else if(DB.get.equals("hive")){
            val hivedf = HiveFuse.getHiveData(sparkSession,zkUrl,tableName)
            hivedf.registerPezyTable(dbName,tableName)
          }else{
            throw new Exception("can`t identify jdbc type")
          }
        }
        /*sparkSession.close()*/
      }

    }catch {
      case ex : Exception =>{
        return
      }
    }

  }

}
