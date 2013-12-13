package cache_controller;

import hbase.HBaseConnection;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FileReader implements Runnable {

	private static final int SLEEP_TIME = 10000; 		// Ten seconds
	private static final int FINISH_SLEEP_TIME = 10; 	// 10 ms
	
	private final static int FILE_FILTER_INDEX = 0;
	private final static int FILE_JSON_INDEX = 1;
	private final static int FILE_RATING_INDEX = 2;
	
	private final static int JSON_LAT_INDEX = 0;
	private final static int JSON_LONG_INDEX = 1;
	
	private final static String OUTPUT_FILE_LOCATION = "/home/jonathan/cache/mr_result.txt";

	private boolean runThread;
	private boolean threadFinished;

	public FileReader() {
		runThread = false;
		threadFinished = false;
	}

	public void run() {
		runThread = true;

		while (runThread) {
			// Sleep on every iteration
			try {

				Thread.sleep(SLEEP_TIME);
			} catch (InterruptedException e) {
				// Should never happen
				e.printStackTrace();
			}

			// Read file
			File outputFile = new File(OUTPUT_FILE_LOCATION);
			
			if (outputFile.exists()) {
				// Get scanner for the file
				Scanner filescan = null;
				try {
					filescan = new Scanner(outputFile);
				} catch (FileNotFoundException e) {
					// File was just deleted
					e.printStackTrace();
					continue;
				}
				
				Map<String, String[]> parsedTweets = new HashMap<String, String[]>();
								
				while (filescan.hasNextLine()) {
					String line = filescan.nextLine();
				
					// Get values from tweet
					String[] lineSplit = line.split("\t");
					String tweetFilter = lineSplit[FILE_FILTER_INDEX];
					String tweetStr = lineSplit[FILE_JSON_INDEX];
					String tweetRating = lineSplit[FILE_RATING_INDEX];
					
					JSONObject tweetJson = null;
					String tweetID = null;
					String tweetLat = null;
					String tweetLng = null;
					
					try {
						tweetJson = new JSONObject(tweetStr);
						
						// Get coordinates
						if (tweetStr.indexOf("\"geo\": null") == -1) {						
						
							JSONObject geo = tweetJson.getJSONObject("geo");
								
							JSONArray coordinates = geo.getJSONArray("coordinates");
							tweetLat = Double.toString(coordinates.getDouble(JSON_LAT_INDEX));	// Latitude
							tweetLng = Double.toString(coordinates.getDouble(JSON_LONG_INDEX));	// Longitude
							
							// Get tweet ID
							tweetID = tweetJson.getString("id_str");	// ID
							
							String[] columns = {tweetFilter, tweetStr, tweetLat, tweetLng, tweetRating};
							parsedTweets.put(tweetID, columns);
						}
					} catch (JSONException e) {
						// json not formatted properly, skip
						e.printStackTrace();
						continue;
					}
				}
				
				filescan.close();
				
				// Add to HBase
				HBaseConnection hbase = new HBaseConnection();
				
				try {
					hbase.put(parsedTweets);
				} catch (IOException e) {
					// Not written to HBase, don't delete the file
					e.printStackTrace();	
					continue;
				}
				
				// Erase file
				outputFile.setWritable(true);
				outputFile.delete();
			}
		}

		threadFinished = true;
	}

	public void finish() {
		runThread = false;

		while (!threadFinished) {
			try {
				Thread.sleep(FINISH_SLEEP_TIME);
			} catch (InterruptedException e) {
				// Should never happen
				e.printStackTrace();
			}
		}
	}
}