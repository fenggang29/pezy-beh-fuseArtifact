package hive_jdbc_handler;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.serde2.AbstractSerDe;
import org.apache.hadoop.hive.serde2.SerDeException;
import org.apache.hadoop.hive.serde2.SerDeStats;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;
import org.apache.hadoop.hive.serde2.typeinfo.StructTypeInfo;
import org.apache.hadoop.io.ObjectWritable;
import org.apache.hadoop.io.Writable;
import org.apache.hive.com.esotericsoftware.minlog.Log;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Properties;

/**
 * Created by 冯刚 on 2017/11/1.
 */


public class DataSourceSerDe extends AbstractSerDe {

    private StructObjectInspector inspector;
    private StructTypeInfo structTypeInfo;
    private Configuration cfg;
    private Properties properties;
    private ObjectInspector objectInspector;
    private Object obj;
    private Writable w;


    public void initialize( Configuration configuration, Properties properties) throws SerDeException {

        this.inspector = DataSourceUtil.structObjectInspector(properties);
        this.structTypeInfo = DataSourceUtil.typeInfo(inspector);
        this.cfg = configuration;
        this.properties = properties;

    }

    public Class<? extends Writable> getSerializedClass() {
        return null;
    }


    public Writable serialize(Object o, ObjectInspector objectInspector) throws SerDeException {

        this.obj = o;
        this.objectInspector = objectInspector;


        ByteArrayOutputStream baops = null;
        ObjectOutputStream oos = null;
        Log.info("=======1========"+objectInspector.getCategory());
        Log.info("=======2========"+o);

        StructObjectInspector soi = (StructObjectInspector)objectInspector;
        List fields = soi.getAllStructFieldRefs();
        List values = soi.getStructFieldsDataAsList(o);
        StringBuffer str = new StringBuffer();

        for(int i=0;i<fields.size();i++){

            str.append(fields.get(i)+","+values.get(i)+" ");
            Log.info("=======3========"+fields.get(i));
            Log.info("=======5========"+values.get(i));
        }

        Log.info("=======5========"+str);

        if(properties.get("type")=="txt"){
            return null;

        }
        if(properties.get("type")=="json"){

            return null;
        }
        return new ObjectWritable(str.toString());

    }

    public SerDeStats getSerDeStats() {
        return null;
    }

    public Object deserialize(Writable writable) throws SerDeException {
        return null;
    }

    public ObjectInspector getObjectInspector() throws SerDeException {
        return this.inspector;
    }
}
