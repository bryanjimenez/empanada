#!/bin/sh

export JAVA_HOME=/usr/lib/jvm/java-6-openjdk-amd64
export HADOOP_CLASSPATH=json-simple-1.1.1.jar

rm -R output

hadoop-1.2.1/bin/hadoop jar popular/WordCount.jar org.apache.hadoop.examples.WordCount ../www/result.txt output


# This is for the UI to use the results
cat output/part-* | sort -k2,2nr > ../www/popular_result.txt

#cp output/part-* ../www/result2.txt

