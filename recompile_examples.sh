#!/bin/bash

export JAVA_HOME=/usr/lib/jvm/java-6-openjdk

javac -classpath hadoop-1.2.1/hadoop-core-1.2.1.jar:hadoop-1.2.1/lib/commons-cli-1.2.jar -d playground/classes/ playground/src/WordCount.java

jar -cvf playground/wordcount.jar -C playground/classes/ .

# To run the newly compiled map reduce run
# hadoop-1.2.1/bin/hadoop jar playground/wordcount.jar org.apache.hadoop.examples.WordCount <input> <output>
