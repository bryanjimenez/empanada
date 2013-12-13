package cache_controller;

import hbase.HBaseConnection;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CacheController {
	
	private static final int FILTER_INDEX = 0;
	private static final int JSON_INDEX = 1;
	private static final int LAT_INDEX = 2;
	private static final int LNG_INDEX = 3;
	private static final int RATING_INDEX = 4;
	
	private static final String EMPTY_JSON = "{\"t\":[],\"f\":[],\"r\":[]}";
		
	// No instantiation allowed 
	private CacheController() {}
	
	public static String getResults(String latitude, String longitude, String radius,
			String oLatitude, String oLongitude, String oRadius, String reqFilters) {
		
		// Get HBase entries with specified filters
		HBaseConnection hbase = new HBaseConnection();
		String[] reqFiltersArr = reqFilters.split(",");
		Map<String, String[]> hbaseResults;
		
		try {
			hbaseResults = hbase.getByFilters(reqFiltersArr);
		} catch (IOException e) {
			// HBase error, couldn't get results
			e.printStackTrace();
			return EMPTY_JSON;
		}
		
		// Identify tweets in range of current view
		Map<String, String[]> selectedTweets = new HashMap<String, String[]>();
		
		for (Map.Entry<String, String[]> entry : hbaseResults.entrySet()) {
			// Get values from map
			String tweetID = entry.getKey();
			String tweetFilter = entry.getValue()[FILTER_INDEX];
			String tweetJson = entry.getValue()[JSON_INDEX];						
			String tweetLatStr = entry.getValue()[LAT_INDEX];
			String tweetLngStr = entry.getValue()[LNG_INDEX];
			String tweetRatingStr = entry.getValue()[RATING_INDEX];
			
			double tweetLatDouble = Double.parseDouble(tweetLatStr);
			double tweetLngDouble = Double.parseDouble(tweetLngStr);
			
			// Get tweets in range of user view
			double odelta = distance(Double.parseDouble(oLatitude), Double.parseDouble(oLongitude),
					tweetLatDouble, tweetLngDouble);
			double delta = distance(Double.parseDouble(latitude), Double.parseDouble(longitude),
					tweetLatDouble, tweetLngDouble);
			
			if (delta < Double.parseDouble(radius) && odelta > Double.parseDouble(oRadius) &&
					reqFilters.indexOf(tweetFilter) > -1) {
				
				String[] attr = {tweetFilter, tweetJson, tweetLatStr, tweetLngStr,tweetRatingStr};
				selectedTweets.put(tweetID, attr);
			}
		}
		
		// Generate response in JSON format
		String jsonResponse;
		try {
			jsonResponse = generateJsonResponse(selectedTweets);
		} catch (JSONException e) {
			// json in wrong format
			e.printStackTrace();
			jsonResponse = EMPTY_JSON;
		}
		return jsonResponse;
	}
	
	private static double distance(double lat1, double lng1, double lat2, double lng2) {
		
		double theta = lng1 - lng2;
		double distance = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) +
				Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
				Math.cos(Math.toRadians(theta));
		distance = Math.toDegrees(Math.acos(distance));
		double miles = distance * 60 * 1.1515;
		
		return miles;
	}
	
	private static String generateJsonResponse(Map<String, String[]> selectedTweets) throws JSONException {
	
		JSONObject response = new JSONObject();
		JSONArray t = new JSONArray();
		JSONArray f = new JSONArray();
		JSONArray r = new JSONArray();
		
		for (Map.Entry<String, String[]> entry : selectedTweets.entrySet()){
			t.put(entry.getValue()[JSON_INDEX]);
			f.put(entry.getValue()[FILTER_INDEX]);
			r.put(Integer.parseInt(entry.getValue()[RATING_INDEX]));
		}
		
		response.put("t", t);
		response.put("f", f);
		response.put("r", r);
		
		String responseStr = response.toString();
		
		// Correct results
		responseStr = responseStr.replaceAll("\"\\{", "\\{");
		responseStr = responseStr.replaceAll("\\}\"", "\\}");
		responseStr = responseStr.replaceAll("\\\\\"", "\\\"");
		responseStr = responseStr.replaceAll("\\\\\\\\\"", "\\\\\"");
		
		return responseStr;
	}
	
	public static int downvote(String filter, String lat, String lng, String id) {
		
		HBaseConnection hbase = new HBaseConnection();
		
		// Get current reputation
		int currentRep;
		
		try {
			currentRep = hbase.getReputation(filter, lat, lng, id);
		} catch (IOException e) {
			// HBase error, couldn't get rep
			e.printStackTrace();
			return -1;
		} 
		
		// If not found
		if (currentRep == -1) {
			return -2;
		}
		
		// Down vote and check for zero rep
		int newRep = currentRep - 1;
		
		try {
			if (newRep == 0) {
				hbase.deleteTweet(filter, lat, lng, id);
			}
			else {
				hbase.updateRep(filter, lat, lng, id, newRep);
			}
		} catch (IOException e) {
			// HBase error, couldn't get rep
			e.printStackTrace();
			return -3;
		} 
		
		return newRep;
	}
}
