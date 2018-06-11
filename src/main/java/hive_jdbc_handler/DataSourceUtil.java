package hive_jdbc_handler;

import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.StandardStructObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;
import org.apache.hadoop.hive.serde2.typeinfo.StructTypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by 冯刚 on 2017/11/3.
 */
public class DataSourceUtil {
    static StandardStructObjectInspector structObjectInspector(Properties tableProperties)
    {
        List<String> columnNames = StringUtils.tokenize(tableProperties.getProperty("columns"), ",");
        List<TypeInfo> colTypes = TypeInfoUtils.getTypeInfosFromTypeString(tableProperties.getProperty("columns.types"));

        List<ObjectInspector> inspectors = new ArrayList();
        for (TypeInfo typeInfo : colTypes) {
            inspectors.add(TypeInfoUtils.getStandardWritableObjectInspectorFromTypeInfo(typeInfo));
        }
        return ObjectInspectorFactory.getStandardStructObjectInspector(columnNames, inspectors);
    }

    static StructTypeInfo typeInfo(StructObjectInspector inspector)
    {
        return (StructTypeInfo)TypeInfoUtils.getTypeInfoFromObjectInspector(inspector);
    }
}
