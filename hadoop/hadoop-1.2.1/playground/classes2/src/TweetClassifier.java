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


package empanada;

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
import java.net.URL;
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
import org.apache.hadoop.mapreduce.Reducer.Context;
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

public class TweetClassifier {
	
	private static final Log LOG = LogFactory.getLog(TweetClassifier.class);
	
  public static class TokenizerMapper 
       extends Mapper<Object, Text, Text,  IntWritable>{
    

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
    		context.write(new Text("poweroutage" + "\t" + value), new IntWritable(10));
    		return;
    	}
    	
    	 Configuration config = context.getConfiguration();
    	 String allContent = config.get("vectorText");
    	 JSONObject vector = new JSONObject();
		try {
			vector = new JSONObject(allContent);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}   
    
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
			
			
			/*
			Iterator<?> keywordsList = keywordsVector.keys();
			Iterator<?> falseKeywordsList = falseKeywordsVector.keys();
			Iterator<?> bigramList = bigramVector.keys();
			Iterator<?> falseBigramList = falseBigramVector.keys();
			Iterator<?> trigramList = trigramVector.keys();
			Iterator<?> falseTrigramList = falseTrigramVector.keys();
			*/
			
			/*
			Scanner categoryScanner = new Scanner(vector.getString("categories"));
			while (categoryScanner.hasNext()){
				String category = categoryScanner.next();
				categoryMap.put(category, 0);
				falseCategoryMap.put(category, 0);
			}*/
	        
			StringTokenizer itr = new StringTokenizer(tweetText, " \t\n\r\f,.:;?![]\"");
			String bigram = "";
			String trigram = "";
			String previousWord = "";
	      //  String tweetCategory = "";
	       // String badTweetCategory = "";
	      //  int winningCategory = 0;
	      //  int winningBadCategory = 0;
	    	
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
				    	int weight = keyword.getInt("value");
				    	Scanner keywordCategoryScan = new Scanner(keyword.getString("category"));
				    	while (keywordCategoryScan.hasNext()){	    		
				    		String category = keywordCategoryScan.next();
				    		context.write(new Text(category + "\t" + value), new IntWritable(weight));  
				    	}	
							
					//	keywordsList = deleteSysnonyms(keywordsVector, keywordsList, i);	
				    	//DELETE SYNONYMS
				    	if (!keyword.isNull("synonyms")){
				        	Scanner synonymsScanner = new Scanner(keyword.getString("synonyms"));
				        	while (synonymsScanner.hasNext()){
				        		keywordsVector.remove(synonymsScanner.next());
				        	}
				    	}
				    	keywordsVector.remove(keywordsList.getString(i));
						keywordsList = keywordsVector.names();				    	
				    	
				    	
						break;
					}
				}				
				
				
				
				//if the word is not a keyword check if it's an invalid keyword
				if (!isKeyword){
					for (int i = 0; i < falseKeywordsList.length(); i++){
						if (tweetWord.equalsIgnoreCase(falseKeywordsList.getString(i))){
					//		falseCategoryMap = selectCategory(falseCategoryMap, falseKeywordsVector, falseKeywordsList, i, 1, false);
							JSONObject keyword = falseKeywordsVector.getJSONObject(falseKeywordsList.getString(i));						
							int weight = keyword.getInt("value");
							Scanner keywordCategoryScan = new Scanner(keyword.getString("category"));
					    	while (keywordCategoryScan.hasNext()){	    		
					    		String category = keywordCategoryScan.next();
					    		context.write(new Text(category + "\t" + value), new IntWritable(-weight));  		
					    	}

					    	
					    	
					    	
					    	
					    	
					    	
					//		falseKeywordsList = deleteSysnonyms(falseKeywordsVector, falseKeywordsList, i);
					    	//DELETE SYNONYMS
					    	if (!keyword.isNull("synonyms")){
					        	Scanner synonymsScanner = new Scanner(keyword.getString("synonyms"));
					        	while (synonymsScanner.hasNext()){
					        		falseKeywordsVector.remove(synonymsScanner.next());
					        	}
					    	}
					    	falseKeywordsVector.remove(falseKeywordsList.getString(i));
					    	falseKeywordsList = falseKeywordsVector.names();

					    	
					    	
					    	
					    	
					    	
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
					    	int weight = keyword.getInt("value");
					    	Scanner keywordCategoryScan = new Scanner(keyword.getString("category"));
					    	while (keywordCategoryScan.hasNext()){	    		
					    		String category = keywordCategoryScan.next();
					    		context.write(new Text(category + "\t" + value), new IntWritable(weight));   			
					    	}	
							

						//	trigramList = deleteSysnonyms(trigramVector, trigramList, i);
					    	//DELETE SYNONYMS
					    	if (!keyword.isNull("synonyms")){
					        	Scanner synonymsScanner = new Scanner(keyword.getString("synonyms"));
					        	while (synonymsScanner.hasNext()){
					        		trigramVector.remove(synonymsScanner.next());
					        	}
					    	}
					    	trigramVector.remove(trigramList.getString(i));
					    	trigramList = trigramVector.names();

					    	
					    	
					    	
					    	
							break;
						} 
					}
					//if no tigram, check for invalid tigram
					if (!isTrigram){
						for (int i = 0; i < falseTrigramList.length(); i++){
							if (trigram.equalsIgnoreCase(falseTrigramList.getString(i))){
						//		falseCategoryMap = selectCategory(falseCategoryMap, falseTrigramVector, falseTrigramList, i, 6, false);

								JSONObject keyword = falseTrigramVector.getJSONObject(falseTrigramList.getString(i));						
						    	int weight = keyword.getInt("value");
						    	Scanner keywordCategoryScan = new Scanner(keyword.getString("category"));
						    	while (keywordCategoryScan.hasNext()){	    		
						    		String category = keywordCategoryScan.next();
						    		context.write(new Text(category + "\t" + value), new IntWritable(-weight));   			
						    	}	
								
	
								
								
								//		falseTrigramList = deleteSysnonyms(falseTrigramVector, falseTrigramList, i);
						    	//DELETE SYNONYMS
						    	if (!keyword.isNull("synonyms")){
						        	Scanner synonymsScanner = new Scanner(keyword.getString("synonyms"));
						        	while (synonymsScanner.hasNext()){
						        		falseTrigramVector.remove(synonymsScanner.next());
						        	}
						    	}
						    	falseTrigramVector.remove(falseTrigramList.getString(i));
								falseTrigramList = falseTrigramVector.names();

						    	
						    	
						    	
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
					    	int weight = keyword.getInt("value");
					    	Scanner keywordCategoryScan = new Scanner(keyword.getString("category"));
					    	while (keywordCategoryScan.hasNext()){	    		
					    		String category = keywordCategoryScan.next();
					    		context.write(new Text(category + "\t" + value), new IntWritable(weight));   			
					    	}	
							
							
							
							
							
							
							
							
							
					//		bigramList = deleteSysnonyms(bigramVector, bigramList, i);
					    	//DELETE SYNONYMS
					    	if (!keyword.isNull("synonyms")){
					        	Scanner synonymsScanner = new Scanner(keyword.getString("synonyms"));
					        	while (synonymsScanner.hasNext()){
					        		bigramVector.remove(synonymsScanner.next());
					        	}
					    	}
					    	bigramVector.remove(bigramList.getString(i));
							bigramList = bigramVector.names();

					    	
					    	
					    	
					    	
					    	
					    	
					    	
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
						    	int weight = keyword.getInt("value");
						    	Scanner keywordCategoryScan = new Scanner(keyword.getString("category"));
						    	while (keywordCategoryScan.hasNext()){	    		
						    		String category = keywordCategoryScan.next();
						    		context.write(new Text(category + "\t" + value), new IntWritable(-weight)); 			
						    	}	
								
								
								
								
								
								
								//	falseBigramList = deleteSysnonyms(falseBigramVector, falseBigramList, i);
						    	//DELETE SYNONYMS
						    	if (!keyword.isNull("synonyms")){
						        	Scanner synonymsScanner = new Scanner(keyword.getString("synonyms"));
						        	while (synonymsScanner.hasNext()){
						        		falseBigramVector.remove(synonymsScanner.next());
						        	}
						    	}
						    	falseBigramVector.remove(falseBigramList.getString(i));
								falseBigramList = falseBigramVector.names();
			
						    	
						    	
						    	
						    	
								break;
							}
						}						
					}
					
				}					
							
				previousWord = tweetWord;	
			}

			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			LOG.info(e);
		}  
		
		//context.write(new Text("shelter" + "\t" + value), new IntWritable(0)); 
    }
  }

/*
	public static class IntSumReducer extends Reducer<Text, Text, Text, Text> {
		private Text result = new Text();

		public void reduce(Text key, Text values, Context context)
				throws IOException, InterruptedException {
			Text r = new Text();
			result.set(r);
			context.write(key, result);
		}
	}*/
	

  public static class IntSumReducer extends Reducer<Text,IntWritable,Text,IntWritable> {
	  private IntWritable result = new IntWritable();
	  public void reduce(Text key, Iterable<IntWritable> values, Context context) 
			  throws IOException, InterruptedException {
		  int sum = 0;
		  for (IntWritable val : values) {
			  sum += val.get();
		  }
		  result.set(sum);
		  if (sum > 2)
			  context.write(key, result);
	  }
  }


	
	

	
	
	

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		String[] otherArgs = new GenericOptionsParser(conf, args)
				.getRemainingArgs();
		if (otherArgs.length != 2) {
			System.err.println("Usage: TweetClassifier <in> <out>");
			System.exit(2);
		}
		
		
    	URL url = TweetClassifier.class.getResource("vector2.json");
    	LOG.info( "this is  file " + url);
		

		String allContent = "";	
		
		/*READING VECTOR FILE*/
    	try
        {
        	File f1 = new File(url.toURI());
    		Scanner scanner1 = new Scanner(f1);
    		allContent = scanner1.useDelimiter("//A").next();
    		//LOG.info("this is the vector file:" + allContent);
    				
        }
        catch( IOException e )
        {
            LOG.info( "Error handling file " + url + ":" + e );
		}    	
    	/*FINISH READING VECTOR FILE*/	
		conf.set("vectorText", allContent);
		

		
		Job job = new Job(conf, "empanada");
		job.setJarByClass(TweetClassifier.class);
		job.setMapperClass(TokenizerMapper.class);
		job.setCombinerClass(IntSumReducer.class);
		job.setReducerClass(IntSumReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
		FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}

}
