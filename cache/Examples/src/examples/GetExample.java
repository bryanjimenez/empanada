package examples;
import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;

import static org.apache.hadoop.hbase.util.Bytes.*;


public class GetExample {

    public static void main(String args[]) throws IOException {

        Configuration conf = HBaseConfiguration.create();
        HTable table = new HTable(conf, "test");
        
        Get get = new Get(toBytes("row1"));
        Result result = table.get(get);
        print(result);

        // Col1 isn't selected the second time
        get.addColumn(toBytes("cf1"), toBytes("col2"));
        result = table.get(get);
        print(result);
        
        table.close();
    }
    
    private static void print(Result result) {
    	System.out.println("----------------------------------------");
    	System.out.println("RowId: " + Bytes.toString(result.getRow()));
    	
    	byte[] val1 = result.getValue(toBytes("cf1"), toBytes("col1"));
    	System.out.println("cf1:col1=" + Bytes.toString(val1));
    	
    	byte[] val2 = result.getValue(toBytes("cf1"), toBytes("col2"));
    	System.out.println("cf1:col2=" + Bytes.toString(val2));
    }
}