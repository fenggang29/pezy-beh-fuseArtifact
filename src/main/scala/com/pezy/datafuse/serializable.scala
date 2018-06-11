package com.pezy.datafuse

/**
  * Created by 冯刚 on 2017/9/30.
  *
  */
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.hadoop.hive.serde2.columnar.BytesRefArrayWritable
import org.apache.hadoop.hive.ql.io.RCFileInputFormat
import org.apache.hadoop.io.LongWritable

object serializable {
  def main(args: Array[String]) {

    val sparkConf = new SparkConf().setAppName("serializable").registerKryoClasses(Array(classOf[org.apache.hadoop.io.LongWritable],
      classOf[org.apache.hadoop.hive.serde2.columnar.BytesRefArrayWritable]))
    val sc = new SparkContext(sparkConf)

    val rdd=sc.hadoopFile("/user/hive/warehouse/fgtest/000000_0",classOf[RCFileInputFormat[LongWritable,BytesRefArrayWritable]],
      classOf[LongWritable],classOf[BytesRefArrayWritable])

    val s = rdd.take(1)


  }

}
