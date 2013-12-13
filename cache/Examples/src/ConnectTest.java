
import static org.apache.hadoop.hbase.util.Bytes.toBytes;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;

public class ConnectTest {
		
	public static void main(String[] args) throws IOException {
		
		long initTime = System.currentTimeMillis();
		
		// Create config and connect
		Configuration conf = HBaseConfiguration.create();
		conf.set("hbase.zookeeper.quorum", "192.168.56.101");
		conf.set("hbase.zookeeper.property.clientPort", "2181");

		HTable table = new HTable(conf, "cache");
		
		// Get row from the table and print
		Get get = new Get(toBytes("388430620737"));
        Result result = table.get(get);
    	
    	byte[] val1 = result.getValue(toBytes("t"), toBytes("f"));
    	byte[] val2 = result.getValue(toBytes("t"), toBytes("lat"));
    	byte[] val3 = result.getValue(toBytes("t"), toBytes("lng"));
    	byte[] val4 = result.getValue(toBytes("t"), toBytes("json"));
    	
    	// Close table
    	table.close();
    	
    	long finishTime = System.currentTimeMillis();
    	
    	System.out.println("Row: " + Bytes.toString(result.getRow()) +
    			"\nt:f = " + Bytes.toString(val1) + 
    			"\nt:lat = " + Bytes.toString(val2) + 
    			"\nt:lng = " + Bytes.toString(val3) + 
    			"\nt:json = " + Bytes.toString(val4) +
    			"\nExecution time: " + (finishTime - initTime) + " ms");
	}
}