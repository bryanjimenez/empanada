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
import java.util.Collection;
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
import org.json.JSONArray;
import org.json.JSONException;
//import org.json.simple.JSONArray;
//import org.json.simple.JSONObject;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
//import org.json.simple.parser.ParseException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class empanada6 {
	private static final Log LOG = LogFactory.getLog(empanada6.class);
	
	
  public static class TokenizerMapper 
       extends Mapper<Object, Text, Text, Text>{
    
    
    
    
    
    
    /*
    private String tweetCategory = "";
    private String badTweetCategory = "";
    private int winningCategory = 0;
    private int winningBadCategory = 0;

    private JSONArray deleteSysnonyms(JSONObject keywordsVector, JSONArray keywordsList, int i) throws JSONException{
    	//DELETE SYNONYMS
    	JSONObject keyword = keywordsVector.getJSONObject(keywordsList.getString(i));
    	if (!keyword.isNull("synonyms")){
        	Scanner synonymsScanner = new Scanner(keyword.getString("synonyms"));
        	while (synonymsScanner.hasNext()){
        		keywordsVector.remove(synonymsScanner.next());
        	}
    	}
    	keywordsVector.remove(keywordsList.getString(i));
    	return keywordsList;
    }
    
    private TreeMap<String, Integer> selectCategory(TreeMap<String, Integer> categoryMap, JSONObject keywordsVector, JSONArray keywordsList, int i, int counter, boolean valid) throws JSONException{
    	JSONObject keyword = keywordsVector.getJSONObject(keywordsList.getString(i));						
    	Scanner keywordCategoryScan = new Scanner(keyword.getString("category"));
    	while (keywordCategoryScan.hasNext()){	    		
    		String category = keywordCategoryScan.next();
    		//LOG.info("This is the cat " + category);
    		if (categoryMap.containsKey(category)){
        		int catCounter = categoryMap.get(category) + 1;
        		if (valid && catCounter > winningCategory){
        			winningCategory = catCounter;
        			tweetCategory = category; 	
        		}
        		else if (catCounter > winningBadCategory){
        			winningBadCategory = catCounter;
        			badTweetCategory = category;
        		}
        		categoryMap.put(category, catCounter); 
    			
    		}
    		
   		
    	}
    	return categoryMap;
    }
    
*/
    

    /*Start of Mapper function*/
    public void map(Object key, Text value, Context context
                    ) throws IOException, InterruptedException {
    	

    	
    	/*READING TWEET*/
    	String tweetText = "";
    	String tweetAuthor = "";
    	try {
    		JSONObject tweet = new JSONObject(value.toString());
    		tweetText = tweet.getString("text");			
    		tweetAuthor = tweet.getJSONObject("user").getString("screen_name");
		} catch (Exception e) {
			e.printStackTrace();
		}
    	/*FINISH READING TWEET*/ 
    	
    	if (tweetAuthor.equalsIgnoreCase("fpl")){
    		context.write(new Text("fpl"), value);
    		return;
    	}
    	
    	
    	
    	//String vectorFile = "/usr/local/hadoop/vector.json";
    	String vectorFile = "vector2.json";
			
		
    	JSONObject vector = new JSONObject();
		/*READING VECTOR FILE*/
    	try
        {
        	File f1 = new File(vectorFile);
    		Scanner scanner1 = new Scanner(f1);
    		String allContent = scanner1.useDelimiter("//A").next();
    		//LOG.info(allContent);
    		vector = new JSONObject(allContent);   		
        }
        catch( IOException e )
        {
            LOG.info( "Error handling file " + vectorFile + ":" + e );
        } catch (JSONException e) {
			LOG.info(e);
		}    	
    	/*FINISH READING VECTOR FILE*/
    	
    
    	TreeMap<String,Integer> categoryMap = new TreeMap<String, Integer>();
    	TreeMap<String,Integer> falseCategoryMap = new TreeMap<String, Integer>();
    	


		try {
			JSONObject keywordsVector =  vector.getJSONObject("keywords");
			JSONObject falseKeywordsVector = vector.getJSONObject("falseKeywords");
			JSONObject bigramVector = vector.getJSONObject("bigrams");
			JSONObject falseBigramVector = vector.getJSONObject("falseBigram");
			JSONObject trigramVector = vector.getJSONObject("trigrams");
			JSONObject falseTrigramVector = vector.getJSONObject("falseTrigram");
			
			JSONArray keywordsList = keywordsVector.names();
			JSONArray falseKeywordsList = falseKeywordsVector.names();
			JSONArray bigramList = bigramVector.names();
			JSONArray falseBigramList = falseBigramVector.names();
			JSONArray trigramList = trigramVector.names();
			JSONArray falseTrigramList = falseTrigramVector.names();
			
			Scanner categoryScanner = new Scanner(vector.getString("categories"));
			while (categoryScanner.hasNext()){
				String category = categoryScanner.next();
				categoryMap.put(category, 0);
				falseCategoryMap.put(category, 0);
			}
			
			/*
			Scanner authorsScanner = new Scanner(vector.getString("authors"));
			while (authorsScanner.hasNext()){
				
			}*/
	        
			StringTokenizer itr = new StringTokenizer(tweetText, " \t\n\r\f,.:;?![]\"");
			String bigram = "";
			String trigram = "";
			String previousWord = "";
	        String tweetCategory = "";
	        String badTweetCategory = "";
	        int winningCategory = 0;
	        int winningBadCategory = 0;
	    	
			while (itr.hasMoreTokens()) {
				boolean isKeyword = false;
				boolean isBigram = false;
				boolean isTrigram = false;
				String tweetWord =  itr.nextToken();		
				
				
				//check if the word in tweet is a valid keyword
				for (int i = 0; i < keywordsList.length(); i++){					
					if (tweetWord.equalsIgnoreCase(keywordsList.getString(i))){
						isKeyword = true;
										
						//	categoryMap = selectCategory(categoryMap, keywordsVector, keywordsList, i, 1, true);	
				    	JSONObject keyword = keywordsVector.getJSONObject(keywordsList.getString(i));						
				    	Scanner keywordCategoryScan = new Scanner(keyword.getString("category"));
				    	while (keywordCategoryScan.hasNext()){	    		
				    		String category = keywordCategoryScan.next();
				    		//LOG.info("This is the cat " + category);
			        		int catCounter = categoryMap.get(category) + 1;
			        		if (catCounter > winningCategory){
			        			winningCategory = catCounter;
			        			tweetCategory = category; 	
			        		}
			        		categoryMap.put(category, catCounter);		
				    	}
				    	
							
					//	keywordsList = deleteSysnonyms(keywordsVector, keywordsList, i);											
						break;
					}
				}				
				
				
				
				//if the word is not a keyword check if it's an invalid keyword
				if (!isKeyword){
					for (int i = 0; i < falseKeywordsList.length(); i++){
						if (tweetWord.equalsIgnoreCase(falseKeywordsList.getString(i))){
					//		falseCategoryMap = selectCategory(falseCategoryMap, falseKeywordsVector, falseKeywordsList, i, 1, false);
							JSONObject keyword = falseKeywordsVector.getJSONObject(falseKeywordsList.getString(i));						
					    	Scanner keywordCategoryScan = new Scanner(keyword.getString("category"));
					    	while (keywordCategoryScan.hasNext()){	    		
					    		String category = keywordCategoryScan.next();
				        		int catCounter = falseCategoryMap.get(category) + 1;
				        		if (catCounter > winningBadCategory){
				        			winningBadCategory = catCounter;
				        			badTweetCategory = category; 	
				        		}
				        		falseCategoryMap.put(category, catCounter); 		
					    	}

					    	
					    	
					    	
					    	
					    	
					    	
					//		falseKeywordsList = deleteSysnonyms(falseKeywordsVector, falseKeywordsList, i);
							break;
						}
					}					
				}
				
				
				
				if (!bigram.isEmpty()){
					trigram = bigram + " " + tweetWord;
					//look for tigram
					for (int i = 0; i < trigramList.length(); i++){
						if (trigram.equalsIgnoreCase(trigramList.getString(i))){
							isTrigram = true;
												
//							categoryMap = selectCategory(categoryMap, trigramVector, trigramList, i, 6, true);
					    	JSONObject keyword = trigramVector.getJSONObject(trigramList.getString(i));						
					    	Scanner keywordCategoryScan = new Scanner(keyword.getString("category"));
					    	while (keywordCategoryScan.hasNext()){	    		
					    		String category = keywordCategoryScan.next();
					    		//LOG.info("This is the cat " + category);
				        		int catCounter = categoryMap.get(category) + 6;
				        		if (catCounter > winningCategory){
				        			winningCategory = catCounter;
				        			tweetCategory = category; 	
				        		}
				        		categoryMap.put(category, catCounter); 	    		
					    	}
							

						//	trigramList = deleteSysnonyms(trigramVector, trigramList, i);
							break;
						} 
					}
					//if no tigram, check for invalid tigram
					if (!isTrigram){
						for (int i = 0; i < falseTrigramList.length(); i++){
							if (trigram.equalsIgnoreCase(falseTrigramList.getString(i))){
						//		falseCategoryMap = selectCategory(falseCategoryMap, falseTrigramVector, falseTrigramList, i, 6, false);

								JSONObject keyword = falseTrigramVector.getJSONObject(falseTrigramList.getString(i));						
						    	Scanner keywordCategoryScan = new Scanner(keyword.getString("category"));
						    	while (keywordCategoryScan.hasNext()){	    		
						    		String category = keywordCategoryScan.next();
						    		//LOG.info("This is the cat " + category);
						    		if (falseCategoryMap.containsKey(category)){
						        		int catCounter = falseCategoryMap.get(category) + 6;
						        		if (catCounter > winningBadCategory){
						        			winningBadCategory = catCounter;
						        			badTweetCategory = category; 	
						        		}
						        		falseCategoryMap.put(category, catCounter); 	
						    		}
						    	}
								
								
								
								
								
								
								
								
								
								
								
								
								
								//		falseTrigramList = deleteSysnonyms(falseTrigramVector, falseTrigramList, i);
								break;
							} 
						}
					}
				}	
				
				if (!previousWord.isEmpty()){					
					bigram = previousWord + " " + tweetWord;
					//check if a pair of words is a valid bigram
					
					for (int i = 0; i < bigramList.length(); i++){
						if (bigram.equalsIgnoreCase(bigramList.getString(i))){
					//		categoryMap = selectCategory(categoryMap, bigramVector, bigramList, i, 3, true);
							
							
					    	JSONObject keyword = bigramVector.getJSONObject(bigramList.getString(i));						
					    	Scanner keywordCategoryScan = new Scanner(keyword.getString("category"));
					    	while (keywordCategoryScan.hasNext()){	    		
					    		String category = keywordCategoryScan.next();
					    		//LOG.info("This is the cat " + category);
					    		if (categoryMap.containsKey(category)){
					        		int catCounter = categoryMap.get(category) + 3;
					        		if (catCounter > winningCategory){
					        			winningCategory = catCounter;
					        			tweetCategory = category; 	
					        		}
					        		categoryMap.put(category, catCounter); 
					    		}
					    	}
							
							
							
							
							
							
							
							
							
					//		bigramList = deleteSysnonyms(bigramVector, bigramList, i);
							isBigram = true;
							break;
						}
					}
					//if pair of word isn't bigram check if it's invalid bigram
					if (!isBigram){
						for (int i = 0; i < falseBigramList.length(); i++){
							if (bigram.equalsIgnoreCase(falseBigramList.getString(i))){
							//	falseCategoryMap = selectCategory(falseCategoryMap, falseBigramVector, falseBigramList, i, 3, false);
							
								JSONObject keyword = falseBigramVector.getJSONObject(falseBigramList.getString(i));						
						    	Scanner keywordCategoryScan = new Scanner(keyword.getString("category"));
						    	while (keywordCategoryScan.hasNext()){	    		
						    		String category = keywordCategoryScan.next();
						    		//LOG.info("This is the cat " + category);
						    		if (falseCategoryMap.containsKey(category)){
						        		int catCounter = falseCategoryMap.get(category) + 6;
						        		if (catCounter > winningBadCategory){
						        			winningBadCategory = catCounter;
						        			badTweetCategory = category; 	
						        		}
						        		falseCategoryMap.put(category, catCounter); 	
						    		}
						    	}
								
								
								
								
								
								
								//	falseBigramList = deleteSysnonyms(falseBigramVector, falseBigramList, i);
								break;
							}
						}						
					}
					
				}					
							
				previousWord = tweetWord;	
			}
			
			
			if (!tweetCategory.equals(badTweetCategory) && winningCategory > 1){
				context.write(new Text(tweetCategory), value);
			//	LOG.info(tweetText);
			}


			
/*
	    	String bigram;
	    	String tweetCategory = "";
	    	String badTweetCategory = "";
	    	int winningCategoryCounter = 0;
	    	int winningFalseCategory = 0;
	    	
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
			} */
			
			
	    	
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			LOG.info(e);
			//e.printStackTrace();
		}
    	
    	

    	

    	//Iterator<?> vectorItr = vector.keys();

    	
		
    	//CREATES MAPS TO APPLY VECTOR
    	/*
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
    	*/
    	//FINISH CREATING MAPS TO APPLY VECTOR
    	
    //	System.out.println(bigramMap.toString());
    	
    	//LOG.info(categoryMap.number);
		//LOG.info(keywordsMap.numberOfKeys);
    	
    	
    	/*

    	
    	categoryMap.putAll(empanada3.categoryMap);
    	falseCategoryMap.putAll(empanada3.falseCategoryMap);
    	keywordsMap.putAll(empanada3.keywordsMap);
    	falseKeywordsMap.putAll(empanada3.falseKeywordsMap);
    	bigramMap.putAll(empanada3.bigramMap);*/
    	
    	//System.out.println("This is the filter " + categoryMap.toString());
    	//System.out.println("These are the keywords " + keywordsMap.toString());
    	
    	
    	/*

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
		job.setJarByClass(empanada6.class);
		job.setMapperClass(TokenizerMapper.class);
	//	job.setCombinerClass(IntSumReducer.class);
	//	job.setReducerClass(IntSumReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
		FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}

}
