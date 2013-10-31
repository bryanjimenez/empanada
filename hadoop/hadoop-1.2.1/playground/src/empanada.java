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
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class empanada {

  public static class TokenizerMapper 
       extends Mapper<Object, Text, Text, Text>{
    
    private final static IntWritable one = new IntWritable(1);
    private Text word = new Text();

    private String vectorFile = "vector.json";
    
    private ArrayList<String> keywords = new ArrayList<String>();
    private ArrayList<String> falseKeywords;
    private ArrayList<String> badKeys;
    
    /*Start of Mapper function*/
    public void map(Object key, Text value, Context context
                    ) throws IOException, InterruptedException {
      //StringTokenizer itr = new StringTokenizer(value.toString());
    	

    	boolean noProfanity = true;
    	
    	JSONParser vectorParser = new JSONParser();
    	JSONObject vector = new JSONObject();
    	int numberOfKeys = 0;
    	
    	/*READING VECTOR FILE*/
    	try
        {
        	File f1 = new File(vectorFile);
    		Scanner scanner1 = new Scanner(f1);
    		String allContent = scanner1.useDelimiter("//A").next();
    		vector = ((JSONObject)vectorParser.parse(allContent));
    		numberOfKeys = vector.size(); 		
        }
        catch( IOException e )
        {
            System.err.println( "Error handling file " + keywords + ":" + e );
        } catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}    	
    	/*FINISH READING VECTOR FILE*/
    	
    	/*READING TWEET*/
    	JSONParser tweetParser = new JSONParser();
    	String tweetText = "";
    	try {
			tweetText = ((JSONObject)tweetParser.parse(value.toString())).get("text").toString();
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	/*FINISH READINGl TWEET*/
    	
    	java.util.TreeMap<String,ArrayList<String>> keywordsMap = new TreeMap<String, ArrayList<String>>();
    	java.util.TreeMap<String,ArrayList<String>> falseKeywordsMap = new TreeMap<String, ArrayList<String>>();
    	Iterator<?> vectorItr = (vector.keySet()).iterator();
		ArrayList<String> keywordsList = new ArrayList<String>();
		ArrayList<String> falseKeywordsList = new ArrayList<String>();
		
    	
    	while (vectorItr.hasNext()){
    		String test = vectorItr.next().toString();
    		JSONObject vectorField = (JSONObject) vector.get(test);
    		if (!test.equals("profanity")){
    			if (vectorField.containsKey("keywords")){
        	    	Scanner keywordsScanner = new Scanner(vectorField.get("keywords").toString());
        	    	while (keywordsScanner.hasNext())
        	    	{
        	    		keywordsList.add(keywordsScanner.next());	
        	    	}   				
    			}
    			if (vectorField.containsKey("falseKeywords")){
        	    	Scanner falseKeywordsScanner = new Scanner(vectorField.get("falseKeywords").toString());
        	    	while (falseKeywordsScanner.hasNext())
        	    	{
        	    		falseKeywordsList.add(falseKeywordsScanner.next());		
        	    	}
    			}
    	    	String category = vectorField.get("category").toString();
    	    	keywordsMap.put(category, keywordsList);
    	    	falseKeywordsMap.put(category, falseKeywordsList);
    		}       
    	}
    	
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
            		  if (word.equals(keywordsMap.get(myKey).get(j))){            
            			  System.out.println("keywords are HERE "+ myKey.toString() + " "+word.toString());
              			  context.write(myKey, value); 
            		  }
        		}
        		
        	}
  
 		
        }
      	  
      	  
      	  
/*          	  for (int i = 0; i < keywords.size(); i++)
          	  {
          		  Text keys = new Text(keywords.get(i).toLowerCase());
          		  if (word.equals(keys))
          			  context.write(keys, value);      		  
          	  }    */		  
      
        
    }
  }
  

//StringTokenizer itr = new StringTokenizer(line, " \t\n\r\f,.:;?![]'");
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

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		String[] otherArgs = new GenericOptionsParser(conf, args)
				.getRemainingArgs();
		if (otherArgs.length != 2) {
			System.err.println("Usage: wordcount <in> <out>");
			System.exit(2);
		}
		Job job = new Job(conf, "empanada");
		job.setJarByClass(empanada.class);
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
