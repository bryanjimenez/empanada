package examples;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.*;

import static org.apache.hadoop.hbase.util.Bytes.*;


public class DeleteExample {

    public static void main(String args[]) throws IOException {

        Configuration conf = HBaseConfiguration.create();
        HTable table = new HTable(conf, "test");
        
        Delete delete = new Delete(toBytes("row2"));
        table.delete(delete);
        
        Delete delete1 = new Delete(toBytes("2"));
        delete1.deleteColumns(toBytes("cf1"), toBytes("col2"));
        table.delete(delete1);
        
        table.close();
    }
}