package hive_jdbc_handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.metastore.HiveMetaHook;
import org.apache.hadoop.hive.ql.metadata.DefaultStorageHandler;
import org.apache.hadoop.hive.ql.plan.TableDesc;
import org.apache.hadoop.hive.serde2.SerDe;
import org.apache.hadoop.mapred.OutputFormat;
import org.apache.kafka.clients.producer.ProducerConfig;

import java.util.Map;
import java.util.Properties;


/**
 * Created by 冯刚 on 2017/10/23.
 */
public class datasourceStorageHandler extends DefaultStorageHandler {



    private static Log log = LogFactory.getLog(datasourceStorageHandler.class);
    /*public Class<? extends InputFormat> getInputFormatClass()
    {
        return null;
    }*/

    private Configuration jobConf;

    public Class<? extends OutputFormat> getOutputFormatClass()
    {
        return dataSourceHiveOutputFormat.class;
    }

    public Class<? extends SerDe> getSerDeClass()
    {
        return DataSourceSerDe.class;
    }

    public HiveMetaHook getMetaHook()
    {
        return null;
    }


    public void configureOutputJobProperties(TableDesc tableDesc, Map<String, String> jobProperties)
    {
        copyToJobProperties(jobProperties, tableDesc.getProperties());
    }


    private void copyToJobProperties(Map<String, String> jobProperties, Properties properties)
    {
        String outputzkQuorum = properties.get("outputzkQuorum").toString();
        jobProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, outputzkQuorum);
        jobProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringSerializer");
        jobProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringSerializer");
    }

    @Deprecated
    public void configureTableJobProperties(TableDesc tableDesc, Map<String, String> jobProperties)
    {
        throw new UnsupportedOperationException();
    }
}
