#!/bin/sh

export JAVA_HOME=/usr/lib/jvm/java-6-openjdk-amd64
#export HADOOP_CLASSPATH=json-simple-1.1.1.jar

rm -R output

hadoop-1.2.1/bin/hadoop jar playground/TweetCategorizer.jar TweetCategorizer tweets output


# This is for the UI to use the results
cp output/part-* ../www/result.txt

