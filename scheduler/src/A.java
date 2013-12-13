/**
 * Handles tasks from Data Collector and stores it
 * to Hadoop Distributed File System
 * @author Jonathan Lozano
 * 
 */
public class A extends Thread {
	public void run() {
		//Sample to run
		System.out.println("Thread A started");
		for(int i = 1; i <= 4; i++) {
			System.out.println("\t From ThreadA: i= " + i);
		}
		System.out.println("Exit from A");
		//Get file from Data Collector
		
		//Send file to MapReduce with Hadoop Job
		
		//Get file from MapReduce to store in HDFS
	}
}