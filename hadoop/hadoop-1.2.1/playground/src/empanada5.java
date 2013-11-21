/**
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */


package org.apache.hadoop.examples;

import java.io.IOException;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.Scanner;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.FileWriter;
import java.util.Map;
import java.util.TreeMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;














import javax.swing.text.Position.Bias;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.json.JSONException;
//import org.json.simple.JSONArray;
//import org.json.simple.JSONObject;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
//import org.json.simple.parser.ParseException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class empanada5 {
	private static final Log LOG = LogFactory.getLog(empanada5.class);
	
	private static TreeMap<String,Integer> categoryMap = new TreeMap<String, Integer>();
	private static TreeMap<String,Integer> falseCategoryMap = new TreeMap<String, Integer>();
	private static TreeMap<String,String> keywordsMap = new TreeMap<String, String>();
	private static TreeMap<String,String> falseKeywordsMap = new TreeMap<String, String>();
	private static TreeMap<String,String> bigramMap = new TreeMap<String, String>();
	
	private static  TreeMap<String,Integer> getCategoryMap(){
		TreeMap<String,Integer> categoryMap1 = categoryMap;
		return categoryMap1;
	}
	
	private static  TreeMap<String,Integer> getFalseCategoryMap(){
		return falseCategoryMap;
	}

	private static TreeMap<String,String> getKeywordsMap(){
		return keywordsMap;
	}
	
	private static TreeMap<String,String> getFalseKeywordsMap(){
		return falseKeywordsMap;
	}
	
	private static TreeMap<String,String> getBigramMap(){
		return bigramMap;
	}
	
  public static class TokenizerMapper 
       extends Mapper<Object, Text, Text, Text>{
    
    private final static IntWritable one = new IntWritable(1);
    private Text word = new Text();

    
    /*Start of Mapper function*/
    public void map(Object key, Text value, Context context
                    ) throws IOException, InterruptedException {
    	
    	
    	boolean noProfanity = true;
    	
    	JSONParser vectorParser = new JSONParser();

    	
    	
    	/*READING TWEET*/
    	String tweetText = "";
    	try {
    		JSONObject tweet = new JSONObject(value.toString());
    		tweetText = tweet.get("text").toString();			
		} catch (Exception e) {
			e.printStackTrace();
		}
    	/*FINISH READING TWEET*/ 
    	
    	
    			//String vectorFile = "/usr/local/hadoop/vector.json";
		
    			String vectorFile = "vector.json";
		
    	JSONObject vector = new JSONObject();
    	int numberOfKeys = 0;
		
		/*READING VECTOR FILE*/
    	try
        {
        	File f1 = new File(vectorFile);
    		Scanner scanner1 = new Scanner(f1);
    		String allContent = scanner1.useDelimiter("//A").next();
    		//LOG.info(allContent);
    		vector = new JSONObject(allContent);
    		numberOfKeys = vector.length(); 		
    		
        }
        catch( IOException e )
        {
            LOG.info( "Error handling file " + vectorFile + ":" + e );
        } catch (JSONException e) {
			// TODO Auto-generated catch block
			LOG.info(e);
		}    	
    	/*FINISH READING VECTOR FILE*/
    	

    	

    	Iterator<?> vectorItr = vector.keys();


		
    	//CREATES MAPS TO APPLY VECTOR
    	while (vectorItr.hasNext()){
    		String nextKey = vectorItr.next().toString();
    		JSONObject vectorField;
			try {
				vectorField = vector.getJSONObject(nextKey);
	    		if (!nextKey.equals("profanity")){
	    			String category = "";
	    			category = vectorField.get("category").toString();
	    			categoryMap.put(category, 0);
	    			falseCategoryMap.put(category, 0);
	    			if (!vectorField.isNull("keywords")){
	        	    	Scanner keywordsScanner = new Scanner(vectorField.get("keywords").toString());
	        	    	while (keywordsScanner.hasNext()){
	        	    		keywordsMap.put(keywordsScanner.next(), category);	        	    			
	        	    	}   					        	    	
	    			}
	    			if (!vectorField.isNull("falseKeywords")){
	        	    	Scanner falseKeywordsScanner = new Scanner(vectorField.get("falseKeywords").toString());
	        	    	while (falseKeywordsScanner.hasNext()){
	        	    		falseKeywordsMap.put(falseKeywordsScanner.next(), category);
	        	    	}
	    			}
	    			if (!vectorField.isNull("bigrams")){
	        	    	Scanner bigramScanner = new Scanner(vectorField.get("bigrams").toString());
	        	    	while (bigramScanner.hasNext()){
	        	    		bigramMap.put(bigramScanner.next(), category);
	        	    	}
	    			}
	    		}  
			} catch (JSONException e1) {
				e1.printStackTrace();
			}   
    	}
    	//FINISH CREATING MAPS TO APPLY VECTOR
    	
    	System.out.println(bigramMap.toString());
    	
    	//LOG.info(categoryMap.number);
		//LOG.info(keywordsMap.numberOfKeys);
    	
    	
    	/*
    	java.util.TreeMap<String,Integer> categoryMap = new TreeMap<String, Integer>();
    	java.util.TreeMap<String,Integer> falseCategoryMap = new TreeMap<String, Integer>();
    	java.util.TreeMap<String,String> keywordsMap =  new TreeMap<String, String>();
    	java.util.TreeMap<String,String> falseKeywordsMap = new TreeMap<String, String>();
    	java.util.TreeMap<String,String> bigramMap = new TreeMap<String, String>();
    	
    	categoryMap.putAll(empanada3.categoryMap);
    	falseCategoryMap.putAll(empanada3.falseCategoryMap);
    	keywordsMap.putAll(empanada3.keywordsMap);
    	falseKeywordsMap.putAll(empanada3.falseKeywordsMap);
    	bigramMap.putAll(empanada3.bigramMap);*/
    	
    	//System.out.println("This is the filter " + categoryMap.toString());
    	//System.out.println("These are the keywords " + keywordsMap.toString());
    	

    	String bigram;
    	String tweetCategory = "";
    	String badTweetCategory = "";
    	int winningCategoryCounter = 0;
    	int winningFalseCategory = 0;
    	StringTokenizer itr = new StringTokenizer(tweetText, " \t\n\r\f,.:;?![]'\"");
		//int wordCounter = 0;
		String previousWord = "";
    	while (itr.hasMoreTokens()) {
			
			String myWord = itr.nextToken();			
			String myBigram = previousWord.concat(" ").concat(myWord);
			
			word.set(myWord.toLowerCase());
			if(keywordsMap.containsKey(word.toString())){			
				int categoryCounter = categoryMap.get(keywordsMap.get(word.toString()));
				categoryCounter++;
				categoryMap.put(keywordsMap.get(word.toString()), categoryCounter);				
				if (categoryCounter > 1 && categoryCounter > winningCategoryCounter){
					tweetCategory = keywordsMap.get(word.toString());
					winningCategoryCounter = categoryCounter;
				}
				keywordsMap.remove(word.toString());
			}
			else if(falseKeywordsMap.containsKey(word.toString())){	
				int categoryCounter = falseCategoryMap.get(falseKeywordsMap.get(word.toString()));
				categoryCounter++;
				falseCategoryMap.put(falseKeywordsMap.get(word.toString()), categoryCounter);		
				if (categoryCounter > 0 && categoryCounter > winningFalseCategory){
					badTweetCategory = falseKeywordsMap.get(word.toString());
					winningFalseCategory = categoryCounter;
				}
				falseKeywordsMap.remove(word.toString());
			}
			
			if (bigramMap.containsKey(myBigram)){
				int categoryCounter = categoryMap.get(bigramMap.get(myBigram));
				categoryCounter++;
				categoryMap.put(bigramMap.get(myBigram), categoryCounter);				
				if (categoryCounter > 0 && categoryCounter > winningCategoryCounter){
					tweetCategory = bigramMap.get(myBigram);
					winningCategoryCounter = categoryCounter;
				}
				context.write(new Text(myBigram), value);
				bigramMap.remove(myBigram);
			}
			
			previousWord = myWord;
				
		}

		if (!tweetCategory.isEmpty() && !tweetCategory.equals(badTweetCategory)){
			context.write(new Text(tweetCategory), value);
		}
		
		
    	
    	
    	
    	
    	
    	
    	//WORKING SO-SO
    	/*ArrayList<String> keywordsAlreadyCounted = new ArrayList<String>(); 
    	StringTokenizer itr = new StringTokenizer(tweetText, " \t\n\r\f,.:;?![]'\"");
    	String[] categoryArray = keywordsMap.keySet().toArray(new String[keywordsMap.size()]);
    	int[] keywordsCounter = new int[keywordsMap.size()];
		while (itr.hasMoreTokens()) {
			word.set(itr.nextToken().toLowerCase());
			   	for (int i = 0; i < keywordsMap.size(); i++){
	    			Text category = new Text(categoryArray[i]);
					for (int j = 0; j < keywordsMap.get(category.toString()).size(); j++){				
						if (word.toString().equals(keywordsMap.get(category.toString()).get(j))){
						/*	if (keywordsAlreadyCounted.size() == 0){
								keywordsAlreadyCounted.add(keywordsMap.get(category.toString()).get(j));
								keywordsCounter[i]++;
							}
							for (int k = 0; k<keywordsAlreadyCounted.size(); k++){
								if (word.toString().equals(keywordsAlreadyCounted.get(j))){
									keywordsAlreadyCounted.add(keywordsMap.get(category.toString()).get(j));
									keywordsCounter[i]++;
								}
							}  *  /
							
						}
					}
					keywordsAlreadyCounted = new ArrayList<String>();
			   	}
		}
		for (int i = 0; i < keywordsCounter.length; i++){
			if (keywordsCounter[i]>1){
				context.write(new Text(categoryArray[i]), value);
			}
		}
    	   	*/
		
		
		
		
		
		
		
		
		
    //Working fine!!!!
    	/*
        StringTokenizer itr = new StringTokenizer(tweetText, " \t\n\r\f,.:;?![]'\"");
        while (itr.hasMoreTokens()) {
        	word.set(itr.nextToken().toLowerCase());
        	for (int i = 0; i < keywordsMap.size(); i++){
        		Text category = new Text(keywordsMap.keySet().toArray(new String[keywordsMap.size()])[i]);
        		for (int j = 0; j < keywordsMap.get(category.toString()).size(); j++){
        			if (word.toString().equals(keywordsMap.get(category.toString()).get(j))){
        				context.write(category, value);
        			}
        			
        		}
        	}
        } 
    	*/
    	
    	/*
    	Set<String> categoriesList = keywordsMap.keySet();    		
  		//System.out.println("keywords are"+ keys.next().toString());
        StringTokenizer itr = new StringTokenizer(tweetText, " \t\n\r\f,.:;?![]'\"");
        while (itr.hasMoreTokens()) {
        	word.set(itr.nextToken().toLowerCase());
        	for (int i = 0; i < keywordsMap.size(); i++){
        		String category = (String) categoriesList.toArray()[i];
        		Text myKey  = new Text((String) categoriesList.toArray()[i]);
        	//	System.out.println("keywords are HERE " +keywordsMap.get(category).size());
        		for(int j = 0; j < keywordsMap.get(category).size(); j++){
        			System.out.println("keywords are HERE "+ myKey.toString() + " "+word.toString());
            		  if (word.equals(keywordsMap.get(category).get(j))){            
            		//	  System.out.println("keywords are HERE "+ myKey.toString() + " "+word.toString());
              			  context.write(myKey, value); 
            		  }
        		}	
        	}
        } */
      	  
      	  
      	  
/*          	  for (int i = 0; i < keywords.size(); i++)
          	  {
          		  Text keys = new Text(keywords.get(i).toLowerCase());
          		  if (word.equals(keys))
          			  context.write(keys, value);      		  
          	  }    */		  
      
        
    }
  }
  

//StringTokenizer itr = new StringTokenizer(line, " \t\n\r\f,.:;?![]");
//word.set(itr.nextToken().toLowerCase());

	public static class IntSumReducer extends Reducer<Text, Text, Text, Text> {
		private Text result = new Text();

		public void reduce(Text key, Text values, Context context)
				throws IOException, InterruptedException {
			Text r = new Text();
			// int sum = 0;
			/*
			 * for (Text val : values) { // sum += val.get(); r = values; }
			 */
			result.set(r);
			context.write(key, result);
		}
	}
	
	private static String getTweetText(Text value){
    	/*READING TWEET*/
    	String tweetText = "";
    	try {
    		JSONObject tweet = new JSONObject(value.toString());
    		tweetText = tweet.get("text").toString();			
		} catch (JSONException e) {
			e.printStackTrace();
		}
    	/*FINISH READINGl TWEET*/ 
		return tweetText;
	}

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		String[] otherArgs = new GenericOptionsParser(conf, args)
				.getRemainingArgs();
		if (otherArgs.length != 2) {
			System.err.println("Usage: wordcount <in> <out>");
			System.exit(2);
		}
		

		

		
		Job job = new Job(conf, "empanada");
		job.setJarByClass(empanada5.class);
		job.setMapperClass(TokenizerMapper.class);
		job.setCombinerClass(IntSumReducer.class);
		job.setReducerClass(IntSumReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
		FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}

}
