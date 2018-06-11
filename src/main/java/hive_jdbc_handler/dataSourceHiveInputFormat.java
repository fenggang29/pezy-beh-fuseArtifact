package hive_jdbc_handler;

import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableInputFormatBase;
import org.apache.hadoop.mapred.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by 冯刚 on 2017/11/6.
 */
public class dataSourceHiveInputFormat extends TableInputFormatBase implements InputFormat<ImmutableBytesWritable, ResultWritable> {

    static final Logger LOG = LoggerFactory.getLogger(dataSourceHiveInputFormat.class);
    private static final Object hbaseTableMonitor = new Object();
    private Connection conn = null;

    public InputSplit[] getSplits(JobConf jobConf, int i) throws IOException {
        return new InputSplit[0];
    }

    public RecordReader<ImmutableBytesWritable, ResultWritable> getRecordReader(InputSplit inputSplit, JobConf jobConf, Reporter reporter) throws IOException {
        return null;
    }
}
