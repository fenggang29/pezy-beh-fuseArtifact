package hive_jdbc_handler;

import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.ql.exec.FileSinkOperator;
import org.apache.hadoop.hive.ql.io.HiveOutputFormat;
import org.apache.hadoop.io.ObjectWritable;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.util.Progressable;
import org.apache.hive.com.esotericsoftware.minlog.Log;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by 冯刚 on 2017/10/30.
 */
public class dataSourceHiveOutputFormat extends dataSourceOutputFormat implements HiveOutputFormat {

    private static org.apache.commons.logging.Log log = LogFactory.getLog(dataSourceHiveOutputFormat.class);
    static class DataSourceHiveRecordWriter
            extends dataSourceOutputFormat.DataSourceRecordWriter
            implements FileSinkOperator.RecordWriter
    {
        private final Progressable progress;
        private KafkaProducer producer;
        private String outtopic;


        public DataSourceHiveRecordWriter(Configuration cfg, Progressable progress)
        {
            super(cfg,progress);
            this.progress = progress;
            this.producer = initKakfa();
            this.outtopic = cfg.get("outputtopics").toString();
        }


        public void write(Writable w)
                throws IOException
        {
            ObjectWritable ow = (ObjectWritable)w;
            StringBuffer str = new StringBuffer();
            String[] s = ow.get().toString().split(" ");
            for(int i=0;i<s.length;i++){
                String[] ss =s[i].split(":");
                str.append(ss[1]+" ");
            }
            Log.info("=======gggggg5========"+str.toString());
            ProducerRecord message = new ProducerRecord(outtopic, null,str.toString());

            producer.send(message);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

        public void close(boolean abort)
                throws IOException
        {
            super.doClose(this.progress);
        }

        public KafkaProducer initKakfa(){
            String outputzkQuorum = cfg.get("outputzkQuorum").toString();
            Map<String,Object> props = new HashMap<String, Object>();
            props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, outputzkQuorum);
            props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                    "org.apache.kafka.common.serialization.StringSerializer");
            props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                    "org.apache.kafka.common.serialization.StringSerializer");
            KafkaProducer producer = new KafkaProducer(props);
            return producer;
        }

    }

    public DataSourceHiveRecordWriter getHiveRecordWriter(JobConf jc, Path finalOutPath, Class valueClass, boolean isCompressed, Properties tableProperties, Progressable progress)
    {

        /*if(tableProperties != null && !tableProperties.isEmpty()) {
            Enumeration propertyNames = tableProperties.propertyNames();
            Object prop = null;

            while(propertyNames.hasMoreElements()) {
                prop = propertyNames.nextElement();
                if(prop instanceof String) {
                    Object value = tableProperties.get(prop);
                    jc.set((String)prop, value.toString());
                }
            }
        }*/

        return new DataSourceHiveRecordWriter(jc, progress);
    }


}
