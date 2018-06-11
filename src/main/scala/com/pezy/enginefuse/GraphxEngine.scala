package com.pezy.enginefuse

import org.apache.spark.graphx.GraphLoader
import org.apache.spark.internal.Logging
import org.apache.spark.sql.SparkSession

/**
  * Created by 冯刚 on 2018/3/23.
  */
object GraphxEngine extends Logging{

  def analysisGraphx(graphxtext: String, spark: SparkSession): Unit = {
    val sc = spark.sparkContext
    val sqltext = graphxtext.substring(7)
    val sqls = sqltext.split(";")
    val vertexdf = spark.sql(sqls(0))
    val lines = vertexdf.rdd.map (row => {
      val str = row.getString(0)+" "+row.getString(1)
      str
    })

    val graph = GraphLoader.edgeList(sc, lines)
    /*val graph = GraphLoader.edgeListFile(sc, "/graphx")*/
    // Run PageRank
    val ranks = graph.pageRank(0.0001).vertices
    // Join the ranks with the usernames
    val users = spark.sql(sqls(1)).rdd.map{ line =>
      (line.getString(0).toLong, line.getString(1))
    }
    /*val users = sc.textFile("/users").map { line =>
      val fields = line.split(",")
      (fields(0).toLong, fields(1))
    }*/
    val ranksByUsername = users.join(ranks).map {
      case (id, (username, rank)) => (username, rank)
    }
    // Print the result
    println(ranksByUsername.collect().mkString("\n"))
    // $example off$
    spark.stop()

  }

}
