package examples;
import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.util.Bytes;


public class HBaseExample {

    public static void main(String args[]) throws IOException {

        Configuration conf = HBaseConfiguration.create();
        //conf.addResource(new Path("/etc/hbase/conf/hbase-site.xml"));

        HTable table = new HTable(conf, "test");
        
        String tName = Bytes.toString(table.getTableName());
        System.out.println("Table name: " + tName);
        
        table.close();
    }
}