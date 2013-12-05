#!/bin/sh

export JAVA_HOME=/usr/lib/jvm/java-6-openjdk-amd64
export HADOOP_CLASSPATH=json-simple-1.1.1.jar


cd ../hadoop/
hadoop-1.2.1/bin/hadoop jar playground/wordcount.jar org.apache.hadoop.examples.WordCount tweets ../www/output

cd ../www
cp output/part-* ../www/result.txt
rm -R output
