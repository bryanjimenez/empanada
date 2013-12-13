
import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;

import static org.apache.hadoop.hbase.util.Bytes.*;


public class NewGet {

    public static void main(String args[]) throws IOException {

        Configuration conf = HBaseConfiguration.create();
        HTable table = new HTable(conf, "cache");
        
        Get get = new Get(toBytes("388430620737"));
        Result result = table.get(get);
        print(result);
        
        table.close();
    }
    
    private static void print(Result result) {
    	System.out.println("----------------------------------------");
    	System.out.println("RowId: " + Bytes.toString(result.getRow()));
    	
    	byte[] val1 = result.getValue(toBytes("t"), toBytes("lat"));
    	System.out.println("cf1:col1=" + Bytes.toString(val1));
    	
    	byte[] val2 = result.getValue(toBytes("t"), toBytes("lng"));
    	System.out.println("cf1:col2=" + Bytes.toString(val2));
    }
}