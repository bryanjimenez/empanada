import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;

public class B extends Thread {
	public static final String HADOOP = "/usr/local/hadoop/bin/hadoop";
	public static final String INPUT = "/user/jonathan/tweets/input/";
	public static final String OUTPUT = "/user/jonathan/tweets/output/";
	public static final String PENDING = "/user/jonathan/tweets/pending/";
	public static final String COMPLETE = "/user/jonathan/tweets/complete/";
	public static final String RESULT = "/home/jonathan/cache/mr_result.txt";
	public static final String MAPREDUCE_JAR="/usr/local/hadoop/elsa/empanada.jar";
	public static final String MAPREDUCE_JAVA="empanada.TweetClassifier";
	private static final String[] cmd = {"/bin/sh","-c",""};

	public void run() {
		while(true){
			System.out.println("Job B: Processing tweets...");
			
			String s;
			Process p;

			try {
//				System.out.println("Job B: Initiator");
//				cmd[2] = HADOOP + " fs -cp " + "/user/jonathan/tweets/pending/* " + PENDING;
//				p = Runtime.getRuntime().exec(cmd);
//				p.waitFor();
//				System.out.println("Initial Exit Code: : " + p.exitValue());

				System.out.println("Job B: Removing Old Output...");
				cmd[2] = HADOOP + " fs -rmr " + OUTPUT;
				p = Runtime.getRuntime().exec(cmd);
				p.waitFor();
				System.out.println("Remove Old Exit Code: : " + p.exitValue());
//				cmd[2] = HADOOP + " fs -ls " + OUTPUT;
//				p = Runtime.getRuntime().exec(cmd);

				System.out.println("Job B: Retrieving New Input");
				cmd[2] = HADOOP + " fs -cp " + PENDING + "/* " + INPUT;
				p = Runtime.getRuntime().exec(cmd);
				p.waitFor();
				System.out.println("Copy New Input Exit Code: : " + p.exitValue());
				
				System.out.println("Job B: Removing Pending files");
				cmd[2] = HADOOP + " fs -rm " + PENDING + "/*";
				p = Runtime.getRuntime().exec(cmd);
				p.waitFor();
				System.out.println("Remove Pending Exit Code: " + p.exitValue());

				System.out.println("Job B: Executing MapReduce...");
				cmd[2] = HADOOP + " jar " + MAPREDUCE_JAR + " " 
						+ MAPREDUCE_JAVA + " " + INPUT + " " + OUTPUT;
				p = Runtime.getRuntime().exec(cmd);
				p.waitFor();
				System.out.println("MapReduce Exit Code: " + p.exitValue());


				// Display MapReduce INFO LOGS
				BufferedReader br = new BufferedReader(
						new InputStreamReader(p.getErrorStream()));
				while ((s = br.readLine()) != null){
					System.out.println(s);
				}

				
				System.out.println("Job B: Writing New Results...");
				copyToLocal();

				System.out.println("Job B: Moving Complete");
				cmd[2] = HADOOP + " fs -cp " + INPUT + "/* " + COMPLETE;
				p = Runtime.getRuntime().exec(cmd);
				p.waitFor();
				System.out.println("Complete Exit Code: " + p.exitValue());

				System.out.println();
			

			} catch (InterruptedException e1) {
				System.out.println(e1);
				e1.printStackTrace();
			} catch (IllegalThreadStateException e) {
				System.out.println(e);
				e.printStackTrace();
			}	catch (IOException e) {
				System.out.println(e);
				e.printStackTrace();
			}
		}
	}
	
	public void copyToLocal(){
		Configuration conf = new Configuration();
		conf.addResource(new Path("/usr/local/hadoop/conf/core-site.xml"));
	    conf.addResource(new Path("/usr/local/hadoop/conf/conf/hdfs-site.xml"));
		FileSystem fs;
		FileUtil util = null;
		File outFile = new File("/home/jonathan/result.txt");
		Path path = new Path(OUTPUT + "part-r-00000");
		boolean worked = false;

    	try{   		
	    	fs = path.getFileSystem(conf);
	    	worked = util.copy(fs, path, outFile, false, conf);
	    	System.out.println("did it work? " + worked);
    	
	    } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	}
	
	public int executeCommand(String str) throws IOException, InterruptedException{
		Process p;
		cmd[2] = str;
		p = Runtime.getRuntime().exec(cmd);
		p.waitFor();
		return p.exitValue();
	}
}
