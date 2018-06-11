package com.pezy.enginefuse

import org.apache.spark.internal.Logging
import org.apache.spark.mllib.clustering.KMeans
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.sql.SparkSession

/**
  * Created by 冯刚 on 2018/3/23.
  */
object MLEngine extends Logging {

  def analysisMLearn(mlearntext: String , spark: SparkSession): Unit = {

    val sc = spark.sparkContext

    val sqltext = mlearntext.substring(7)

    val dataset = spark.sql(sqltext)

    val length = dataset.columns.length

    val r = dataset.rdd
    r.foreach(s => println(s))
    val parsedData = r.map(s => {
      var arr = new Array[Double](length)
      for(i <-0 to length-1){
        arr(i) = s.get(i).toString.toDouble
      }
      Vectors.dense(arr)
    })
    parsedData.foreach(f=>{println(f)})

    val numClusters = 2
    val numIterations = 20
    val model = KMeans.train(parsedData, numClusters, numIterations)

    // Evaluate clustering by computing Within Set Sum of Squared Errors
    val WSSSE = model.computeCost(parsedData)
    println("Within Set Sum of Squared Errors = " + WSSSE)

  }

}
