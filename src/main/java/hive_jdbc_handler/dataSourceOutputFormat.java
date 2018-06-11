package hive_jdbc_handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.util.Progressable;

import java.io.IOException;

/**
 * Created by 冯刚 on 2017/10/23.
 */
public class dataSourceOutputFormat extends org.apache.hadoop.mapreduce.OutputFormat implements org.apache.hadoop.mapred.OutputFormat {
    private static Log log = LogFactory.getLog(dataSourceOutputFormat.class);
    private static final int NO_TASK_ID = -1;

    public static class DataSourceOutputCommitter
            extends org.apache.hadoop.mapreduce.OutputCommitter
    {
        public void setupJob(org.apache.hadoop.mapreduce.JobContext jobContext)
                throws IOException
        {}

        @Deprecated
        public void cleanupJob(org.apache.hadoop.mapreduce.JobContext jobContext)
                throws IOException
        {}

        public void setupTask(org.apache.hadoop.mapreduce.TaskAttemptContext taskContext)
                throws IOException
        {}

        public boolean needsTaskCommit(org.apache.hadoop.mapreduce.TaskAttemptContext taskContext)
                throws IOException
        {
            return false;
        }

        public void commitTask(org.apache.hadoop.mapreduce.TaskAttemptContext taskContext)
                throws IOException
        {}

        public void abortTask(org.apache.hadoop.mapreduce.TaskAttemptContext taskContext)
                throws IOException
        {}
    }

    protected static class DataSourceRecordWriter
            extends org.apache.hadoop.mapreduce.RecordWriter
            implements org.apache.hadoop.mapred.RecordWriter
    {
        protected final Configuration cfg;
        protected boolean initialized = false;
        /*protected RestRepository repository;*/
        private String uri;
        /*private Resource resource;
        private HeartBeat beat;*/
        private final Progressable progressable;

        public DataSourceRecordWriter(Configuration cfg, Progressable progressable)
        {
            this.cfg = cfg;
            this.progressable = progressable;
        }

        public void write(Object key, Object value)
                throws IOException
        {

            dataSourceOutputFormat.log.info("=================DataSourceOutPut write====================");
        }


        public void close(org.apache.hadoop.mapreduce.TaskAttemptContext context)
                throws IOException
        {
            doClose(context);
        }

        public void close(Reporter reporter)
                throws IOException
        {
            doClose(reporter);
        }

        protected void doClose(Progressable progressable)
        {
            dataSourceOutputFormat.log.info("=================DataSourceOutPut doClose====================");
            this.initialized = false;
        }
    }

    public org.apache.hadoop.mapreduce.RecordWriter getRecordWriter(org.apache.hadoop.mapreduce.TaskAttemptContext context)
    {
        return (org.apache.hadoop.mapreduce.RecordWriter)getRecordWriter(null, null, null, context);
    }

    public void checkOutputSpecs(org.apache.hadoop.mapreduce.JobContext context)
            throws IOException
    {

    }

    public org.apache.hadoop.mapreduce.OutputCommitter getOutputCommitter(org.apache.hadoop.mapreduce.TaskAttemptContext context)
    {
        return new DataSourceOutputCommitter();
    }

    public org.apache.hadoop.mapred.RecordWriter getRecordWriter(FileSystem ignored, JobConf job, String name, Progressable progress)
    {
        dataSourceOutputFormat.log.info("=================DataSourceOutPut getRecordWrite====================");
        return new DataSourceRecordWriter(job, progress);
    }

    public void checkOutputSpecs(FileSystem ignored, JobConf cfg)
            throws IOException
    {
        dataSourceOutputFormat.log.info("=================DataSourceOutPut checkOutputSpecs====================");
    }

    private void init(Configuration cfg)
            throws IOException
    {
        /*Settings settings = HadoopSettingsManager.loadFrom(cfg);
        Assert.hasText(settings.getResourceWrite(), String.format("No resource ['%s'] (index/query/location) specified", new Object[] { "es.resource" }));

        InitializationUtils.checkIdForOperation(settings);
        InitializationUtils.checkIndexExistence(settings);
        if (HadoopCfgUtils.getReduceTasks(cfg) != null)
        {
            if (HadoopCfgUtils.getSpeculativeReduce(cfg)) {
                log.warn("Speculative execution enabled for reducer - consider disabling it to prevent data corruption");
            }
        }
        else if (HadoopCfgUtils.getSpeculativeMap(cfg)) {
            log.warn("Speculative execution enabled for mapper - consider disabling it to prevent data corruption");
        }*/
    }
}

