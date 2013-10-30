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
import java.io.FileReader;
import java.io.BufferedReader;

import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

/*neede for JSON manipulation*/
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/* external jar (json-simple-1.1.1.jar)
 * http://stackoverflow.com/questions/10464179/running-hadoop-libjars-using-hipi
 * */

/*
 * example of simplejson
 * http://www.mkyong.com/java/json-simple-example-read-and-write-json/
 * */

public class TweetCategorizer {

	public static class TokenizerMapper extends
			Mapper<Object, Text, Text, Text> {

		private final static IntWritable one = new IntWritable(1);
		private Text ftweet = new Text();
		private Text word = new Text();
		public static ArrayList<String> keyword_list = new ArrayList();
		public static ArrayList<String> keyname_list = new ArrayList();

		public void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {

			try {
				/* JSON */
				JSONParser parser = new JSONParser();

				ftweet.set(value.toString());
				Object obj = parser.parse(value.toString());

				JSONObject jsonObject = (JSONObject) obj;

				// get only text node of JSON for now ignore the rest
				String tweet = (String) jsonObject.get("text");
				// System.out.println(tweet);

				// StringTokenizer itr = new
				// StringTokenizer(value.toString()," \n\t\r\f,.:?/[]\'");
				StringTokenizer itr = new StringTokenizer(tweet.toLowerCase(),
						" \n\t\r\f,.:?/[]\'\"");
				while (itr.hasMoreTokens()) {
					String tok = itr.nextToken();

					// iterate through keyword list serching for matches on word
					// token
					// note that we are examining a word at a time, we might
					// want more than one word later on
					// or a better approach at searching
//					for (String keyword : keyword_list)
					for (int i=0;i< keyword_list.size();i++)
						if (tok.equals(keyword_list.get(i))) {
							word.set(keyname_list.get(i));
							context.write(word, ftweet);
						}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static class IntSumReducer extends
			Reducer<Text, IntWritable, Text, IntWritable> {
		private IntWritable result = new IntWritable();

		public void reduce(Text key, Iterable<IntWritable> values,
				Context context) throws IOException, InterruptedException {
			int sum = 0;
			for (IntWritable val : values) {
				sum += val.get();
			}
			if (sum > 4) {
				result.set(sum);
				context.write(key, result);
			}
		}
	}

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		// conf.addResource(new Path("../../env_vars"));

		String[] otherArgs = new GenericOptionsParser(conf, args)
				.getRemainingArgs();
		if (otherArgs.length != 2) {
			System.err.println("Usage: TweetCategorizer <in> <out>");
			System.exit(2);
		}
		
		// ----------------------------------------------------------
		//			READ FILTER FILE
		// ----------------------------------------------------------
		// Path pt=new Path("hdfs://pathTofile");
		//Path pt = new Path("../www/hfilters.json");
		String l;
		String line="";
		//FileSystem fs = FileSystem.get(conf);
		BufferedReader br = new BufferedReader(new FileReader("../www/json/filters.json"));

		try {
			//BufferedReader br = new BufferedReader(new FileReader(fs.open(pt)));
			
			while ((l = br.readLine()) != null) {
				line +=l;
				//System.out.println(line);
			}
			
		} finally {
			// you should close out the BufferedReader
			br.close();
		}
		// ----------------------------------------------------------
		//			PARSE JSON
		//http://stackoverflow.com/questions/6697147/json-iterate-through-jsonarray
		//http://juliusdavies.ca/json-simple-1.1.1-javadocs/org/json/simple/JSONObject.html
		// ----------------------------------------------------------
		JSONParser parser = new JSONParser();		
		JSONObject jsonObject = (JSONObject) parser.parse(line);


		Set<String> filters = jsonObject.keySet();
		
		// inside each object there is a "name" field, get value and add to keyword_list
		for(String i : filters)
		{
		      JSONObject objects = (JSONObject) jsonObject.get(i);
		      String keyword = ((String) objects.get("name")).toLowerCase();
		      TokenizerMapper.keyname_list.add(i);
		      TokenizerMapper.keyword_list.add(keyword);
		}
		// ----------------------------------------------------------

					
		Job job = new Job(conf, "categorize tweets");
		job.setJarByClass(TweetCategorizer.class);
		job.setMapperClass(TokenizerMapper.class);
		// job.setCombinerClass(IntSumReducer.class);
		// job.setReducerClass(IntSumReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
		FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}
