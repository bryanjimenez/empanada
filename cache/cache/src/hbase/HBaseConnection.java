package hbase;

import static org.apache.hadoop.hbase.util.Bytes.toBytes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;

public class HBaseConnection {
	
	private static final String HBASE_ADDRESS = "empanada.cs.fiu.edu";
	private static final String HBASE_PORT = "2181";
	private static final String TABLE_NAME = "cache";
	
	private final static int INITIAL_REPUTATION = 3;
	
	private static final int FILTER_INDEX = 0;
	private static final int JSON_INDEX = 1;
	private static final int LAT_INDEX = 2;
	private static final int LNG_INDEX = 3;
	private static final int RATING_INDEX = 4;

	protected static final byte[] T = "t".getBytes();
	protected static final byte[] ID = "id".getBytes();
	protected static final byte[] F = "f".getBytes();
	protected static final byte[] JSON = "json".getBytes();
	protected static final byte[] LAT = "lat".getBytes();
	protected static final byte[] LNG = "lng".getBytes();
	protected static final byte[] RATE = "rate".getBytes();
	protected static final byte[] REP = "rep".getBytes();
	
	private Configuration conf;
	
	public HBaseConnection() {
		// Create config and connect
		conf = HBaseConfiguration.create();
		conf.set("hbase.zookeeper.quorum", HBASE_ADDRESS);
		conf.set("hbase.zookeeper.property.clientPort", HBASE_PORT);
	}
	
	public void put(Map<String, String[]> tweetMap) throws IOException {

		// Open the table			
		HTable table = new HTable(conf, TABLE_NAME);
		
		try {
			// Collect all puts
			List<Put> putList = new ArrayList<Put>();
			for (Map.Entry<String, String[]> entry : tweetMap.entrySet()) {
				
				// Get values from map
				String tweetID = entry.getKey();
				String filter = entry.getValue()[FILTER_INDEX];
				String json = entry.getValue()[JSON_INDEX];
				String lat = entry.getValue()[LAT_INDEX];
				String lng = entry.getValue()[LNG_INDEX];
				String rating = entry.getValue()[RATING_INDEX];
	
				// Add to put operation
				byte[] rowKey = Bytes.add(toBytes(filter), toBytes(lat), toBytes(lng));
				rowKey = Bytes.add(rowKey, toBytes(tweetID));
				Put tempPut = new Put(rowKey);
				tempPut.add(T, ID, toBytes(tweetID));
				tempPut.add(T, F, toBytes(filter));
				tempPut.add(T, JSON, toBytes(json));
				tempPut.add(T, LAT, toBytes(lat));
				tempPut.add(T, LNG, toBytes(lng));
				tempPut.add(T, RATE, toBytes(rating));
				tempPut.add(T, REP, toBytes(INITIAL_REPUTATION));
		
				// Add to put list
				putList.add(tempPut);
			}
		
			// Add to table
			table.put(putList);
		} finally {
			// Always close table
	    	table.close();
		}
	}
	
	public Map<String, String[]> getByFilters(String[] filters) throws IOException {

		// Open the table
		HTable table = new HTable(conf, TABLE_NAME);
		ResultScanner rScanner = null;
		
		Map<String, String[]> filteredResults = new HashMap<String, String[]>();
		
		try {				
			for (String currentFilter : filters) {
								
				// Create start and stop rows
				byte[] startRow = Bytes.add(toBytes(currentFilter), toBytes(""), toBytes(""));
				startRow = Bytes.add(startRow, toBytes(""));
				
				byte[] stopRow = Bytes.add(toBytes(currentFilter), toBytes("A"), toBytes(""));
				stopRow = Bytes.add(stopRow, toBytes(""));
				
				// Get result scanner for the filter
				Scan s = new Scan();
				
				s.setStartRow(startRow);
				s.setStopRow(stopRow);
				rScanner = table.getScanner(s);
			
				// Get results from HBase
				for (Result result = rScanner.next(); (result != null); result = rScanner.next()) {
					Get get = new Get(result.getRow());
				    Result row = table.get(get);
				    
				    String tweetID = Bytes.toString(row.getValue(T, ID));
				    String filter = Bytes.toString(row.getValue(T, F));
				    String json = Bytes.toString(row.getValue(T, JSON));
				    String lat = Bytes.toString(row.getValue(T, LAT));
				    String lng = Bytes.toString(row.getValue(T, LNG));
				    String rating = Bytes.toString(row.getValue(T, RATE));
				    
				    // Add to map
				    if (rating == null) {
				    	rating = "0";
				    }
				    
				    String[] columns = {filter, json, lat, lng, rating};
				    filteredResults.put(tweetID, columns);
				}
				
				rScanner.close();
			}
		} finally {
			// Always close table
	    	table.close();
		}
    	
    	return filteredResults;
	}
	
	public Map<String, String[]> getByFiltersAndCoordinates(String[] filters, String minLatitude, String maxLatitude) 
			throws IOException {

		// Open the table
		HTable table = new HTable(conf, TABLE_NAME);
		ResultScanner rScanner = null;
		
		Map<String, String[]> filteredResults = new HashMap<String, String[]>();
		
		try {				
			for (String currentFilter : filters) {
								
				// Create start and stop rows
				byte[] startRow = Bytes.add(toBytes(currentFilter), toBytes(minLatitude), toBytes(""));
				startRow = Bytes.add(startRow, toBytes(""));
				
				byte[] stopRow = Bytes.add(toBytes(currentFilter), toBytes(maxLatitude), toBytes(""));
				stopRow = Bytes.add(stopRow, toBytes(""));
				
				// Get result scanner for the filter
				Scan s = new Scan();
				
				s.setStartRow(startRow);
				s.setStopRow(stopRow);
				rScanner = table.getScanner(s);
			
				// Get results from HBase
				for (Result result = rScanner.next(); (result != null); result = rScanner.next()) {
					Get get = new Get(result.getRow());
				    Result row = table.get(get);
				    
				    String tweetID = Bytes.toString(row.getValue(T, ID));
				    String filter = Bytes.toString(row.getValue(T, F));
				    String json = Bytes.toString(row.getValue(T, JSON));
				    String lat = Bytes.toString(row.getValue(T, LAT));
				    String lng = Bytes.toString(row.getValue(T, LNG));
				    String rating = Bytes.toString(row.getValue(T, RATE));
				    
				    // Add to map
				    if (rating == null) {
				    	rating = "0";
				    }
				    
				    String[] columns = {filter, json, lat, lng, rating};
				    filteredResults.put(tweetID, columns);
				}
				
				rScanner.close();
			}
		} finally {
			// Always close table
	    	table.close();
		}
    	
    	return filteredResults;
	}
	
	public int getReputation(String filter, String lat, String lng, String id) throws IOException {
		
		// Open the table
		HTable table = new HTable(conf, TABLE_NAME);
		int currentRep = -1;
		
		try {
			// Construct rowkey of the tweet
			byte[] rowkey = Bytes.add(toBytes(filter), toBytes(lat), toBytes(lng));
			rowkey = Bytes.add(rowkey, toBytes(id));
			//byte[] rowkey = Bytes.add(toBytes(filter), toBytes(id));
					
			// Get row and column
			Result row = table.get(new Get(rowkey));
			byte[] currentRepBytes = row.getValue(T, REP);
			
			if (currentRepBytes == null) {
				// Not found
				return -1;
			}
			
			// Convert to int
			currentRep = Bytes.toInt(currentRepBytes);
		} finally {
			// Always close table
	    	table.close();
		}
		
		
		return currentRep;
	}
	
	public void deleteTweet(String filter, String lat, String lng, String id) throws IOException {
		// Open the table
		HTable table = new HTable(conf, TABLE_NAME);
		
		try {
			// Construct rowkey of the tweet
			byte[] rowkey = Bytes.add(toBytes(filter), toBytes(lat), toBytes(lng));
			rowkey = Bytes.add(rowkey, toBytes(id));
			//byte[] rowkey = Bytes.add(toBytes(filter), toBytes(id));
			
			// Delete
			table.delete(new Delete(rowkey));

		} finally {
			// Always close table
	    	table.close();
		}
	}
	
	public void updateRep(String filter, String lat, String lng, String id, int newRep) throws IOException {
		// Open the table
		HTable table = new HTable(conf, TABLE_NAME);
		
		try {
			// Construct rowkey of the tweet
			byte[] rowkey = Bytes.add(toBytes(filter), toBytes(lat), toBytes(lng));
			rowkey = Bytes.add(rowkey, toBytes(id));
			//byte[] rowkey = Bytes.add(toBytes(filter), toBytes(id));
			
			// Get row insertion timestamp
			Result row = table.get(new Get(rowkey));
			long insertionTime = row.raw()[0].getTimestamp();
			
			// Put new value
			Put put = new Put(rowkey);
			put.add(T, REP, insertionTime, toBytes(newRep));
			table.put(put);
			
		} finally {
			// Always close table
	    	table.close();
		}
	}
}