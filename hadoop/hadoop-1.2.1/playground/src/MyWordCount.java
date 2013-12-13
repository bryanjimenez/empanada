

package empanada;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
//import org.apache.hadoop.mapred.TextOutputFormat;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Partitioner;
//import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.json.JSONException;
//import org.json.simple.JSONArray;
//import org.json.simple.JSONObject;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;



public class MyWordCount {
	
	private static final Log LOG = LogFactory.getLog(MyWordCount.class);
	
	public static class TokenizerMapper extends Mapper<Object, Text, Text, IntWritable>{
		
		private final static IntWritable one = new IntWritable(1);
	    private Text word = new Text();
	    
	    public void map(Object key, Text value, Context context
                ) throws IOException, InterruptedException {
	    	
	    	String myString = value.toString();
		    String myCategory = myString.substring(0, myString.indexOf('\t'));
		    myString = myString.substring(myString.indexOf('\t')+1);
		    myString = myString.substring(0, myString.indexOf('\t'));
		    
		    /*
		    for (int i = 0; i < 100; i++){
		    	System.out.println("we're in mapper");
			    System.out.println(myString);
		    }*/
		    
		    //System.out.println(myString);
		    	
		    /*READING TWEET*/
		    String tweetText = "";
	    	try {
	    		JSONObject tweet = new JSONObject(myString);
	    		tweetText = tweet.get("text").toString();			
	    	} catch (JSONException e) {
	    		LOG.info(e);
	    		e.printStackTrace();
	    	}
		    /*FINISH READING TWEET*/ 
	    	
	    	ArrayList<String> myDeadWords = new ArrayList<String>();
	    	Configuration config = context.getConfiguration();
	    	String deadWords = config.get("deadWords");
	    	
	    	Scanner deadWordsScanner = new Scanner(deadWords);
	    	while (deadWordsScanner.hasNextLine()){
	    		myDeadWords.add(deadWordsScanner.nextLine());
	    	}
	    	
	    	boolean isDead = false;
	    	
	    	String previousWord = "";
		    StringTokenizer itr = new StringTokenizer(tweetText, " \t\n\r\f,.:;?![]\"()-/");
		    
		    while (itr.hasMoreTokens()) {
		    	String myWord = itr.nextToken().toLowerCase(); 
		    	if (!myWord.matches(".*\\d.*")){	    		
		        	for (int i = 0; i < myDeadWords.size(); i++){
		        		if (myWord.equalsIgnoreCase(myDeadWords.get(i))){ 	  
		        			isDead = true;
		        			i = myDeadWords.size();
		        		}
		        	} 	
		        	if (!isDead){
		        		word.set(myCategory + "_" + myWord);
		        		context.write(word, one);
		        	}
		    		if (!previousWord.isEmpty()){
		        		context.write(new Text(myCategory + "_" + previousWord + " " + myWord), one);
		    		}	  
		    		
		        	previousWord = myWord;
		    	}
		    	else
		    		previousWord= "";
		    }
	    }
	}
	
	
	public static class MyReducer extends Reducer<Text,IntWritable,Text,IntWritable> {
		
		String generateFileName(Text k, IntWritable v) {
			return k.toString() + "_" + v.toString();
		 }
	  	  
		  private MultipleOutputs<Text,IntWritable> mos;
		  private IntWritable result = new IntWritable();
	  
		  public void setup(Context context) {
			  mos = new MultipleOutputs<Text, IntWritable>(context);
		  }
	  
		  public void reduce(Text key, Iterable<IntWritable> values, Context context
                  ) throws IOException, InterruptedException {
			  
			  int sum = 0;
			  for (IntWritable val : values) {
				  sum += val.get();
			  }  
			  result.set(sum);
	  
			  String myString = key.toString();
			  if (myString.contains(" ") && sum > 0){
				  mos.write("bigram", key, result);	  
			  }
			  else if (sum > 0){
				  mos.write("keywords", key, result);
			  }
     //context.write(key, result);
	  
		  }
	  

	  public void cleanup(Context context) throws IOException {
		  try {
			mos.close();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	  }
  }
      
 /*
  Usage in Reducer:

	  String generateFileName(K k, V v) {
	   return k.toString() + "_" + v.toString();
	 }
	 
	 public class MOReduce extends
	   Reducer<WritableComparable, Writable,WritableComparable, Writable> {
	 private MultipleOutputs mos;
	 public void setup(Context context) {
	 ...
	 mos = new MultipleOutputs(context);
	 }

	 public void reduce(WritableComparable key, Iterator<Writable> values,
	 Context context)
	 throws IOException {
	 ...
	 mos.write("text", , key, new Text("Hello"));
	 mos.write("seq", LongWritable(1), new Text("Bye"), "seq_a");
	 mos.write("seq", LongWritable(2), key, new Text("Chau"), "seq_b");
	 mos.write(key, new Text("value"), generateFileName(key, new Text("value")));
	 ...
	 }

	 public void cleanup(Context) throws IOException {
	 mos.close();
	 ...
	 }

	 }
	 */
  
  
  
  
  public static class MyPartitioner extends Partitioner<Text, IntWritable> {
	  @Override
	  public int getPartition(Text key, IntWritable value, int numPartitions){
		  return key.hashCode() % numPartitions;
	  }
	  //@Override public void configure(Job conf) { }
  }
  


	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();	
		String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
		if (otherArgs.length != 2) {
			System.err.println("Usage: empanada.MyWordCount <in> <out>");
			System.exit(2);
		}	
		
		
    	URL url = MyWordCount.class.getResource("partOfSpeech.txt");
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
            e.printStackTrace();
		}    	
    	/*FINISH READING VECTOR FILE*/	
		conf.set("deadWords", allContent);
			
		// Create a new Job
		Job job = new Job(conf, "MyWordCount");
		job.setJarByClass(MyWordCount.class);
		
		
				
		
		
		
		// Specify various job-specific parameters
		job.setPartitionerClass(MyPartitioner.class);
		job.setMapperClass(TokenizerMapper.class);
		job.setCombinerClass(MyReducer.class);
		job.setReducerClass(MyReducer.class);

		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
		FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));
		
		MultipleOutputs.addNamedOutput(job, "keywords", TextOutputFormat.class, Text.class, IntWritable.class);
		 // Defines additional sequence-file based output 'sequence' for the job
		MultipleOutputs.addNamedOutput(job, "bigram", TextOutputFormat.class, Text.class, IntWritable.class);
		 
		// Submit the job, then poll for progress until the job is complete
		System.exit(job.waitForCompletion(true) ? 0 : 1);

		/*
		 * Here is an example on how to submit a job: job.setInputPath(new
		 * Path("in")); job.setOutputPath(new Path("out"));
		 */

			// FileInputFormat.setInputPath(job, inDir);
			 //FileOutputFormat.setOutputPath(job, outDir);

			 // Defines additional single text based output 'text' for the job
		 
		 
		/*	 MultipleOutputs.addNamedOutput(job, "text", TextOutputFormat.class,
			 LongWritable.class, Text.class);

			 // Defines additional sequence-file based output 'sequence' for the job
			 MultipleOutputs.addNamedOutput(job, "seq",
			   SequenceFileOutputFormat.class,
			   LongWritable.class, Text.class);
*/
		
		

	}
}
