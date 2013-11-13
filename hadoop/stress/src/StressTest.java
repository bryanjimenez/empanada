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

//package org.apache.hadoop.examples;

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

public class StressTest {

	public static class TokenizerMapper extends
			Mapper<Object, Text, Text, Text> {

		private Text ftweet = new Text();
		private Text word = new Text();

		public void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {

				ftweet.set(value.toString());				
				word.set("fire");
				context.write(word, ftweet);
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
