#!/bin/bash

HADOOP='/usr/local/hadoop/bin/hadoop'
HADOOP_INPUT='/user/jonathan/tweets/input/'
HADOOP_PENDING='/user/jonathan/tweets/pending/'
HADOOP_COMPLETE='/user/jonathan/tweets/complete/'
HADOOP_OUTPUT='/user/jonathan/tweets/output'
RESULT_FILE='/home/jonathan/result.txt'
TIMEOUT=60
MAPREDUCE_JAR='/usr/local/hadoop/elsa/empanada.jar'
MAPREDUCE_JAVA='empanada.TweetClassifier'
while :
do
	#fs -rmr /user/jonathan/tweets/output
	#hadoop fs -put /tweets/*  /user/jonathan/tweets/input/

	echo
	echo 'Cleaning Old Results...'
	echo
	$HADOOP fs -rmr $HADOOP_OUTPUT

	echo
	echo 'Retrieving input...'
	echo
	$HADOOP fs -cp "$HADOOP_PENDING/*" $HADOOP_INPUT	
	$HADOOP fs -rm "$HADOOP_PENDING/*" 
	$HADOOP fs -ls $HADOOP_INPUT

	echo
	echo 'Running MapReduce...'
	echo


	#need to update this next lines input path to retrieve todays tweets only
	$HADOOP jar $MAPREDUCE_JAR $MAPREDUCE_JAVA $HADOOP_INPUT $HADOOP_OUTPUT

#	echo
#	echo 'Removing old result file...'
#	echo
#	rm $RESULT_FILE

	echo
	echo 'Writing new result file...'
	echo
	$HADOOP fs -cat "$HADOOP_OUTPUT/part*" >> $RESULT_FILE
	ls -l $RESULT_FILE

	echo 'Moving complete'
	$HADOOP fs -cp "$HADOOP_INPUT/*" $HADOOP_COMPLETE
	$HADOOP fs -rm "$HADOOP_INPUT/*"
	
	echo
	echo "Sleeping for $TIMEOUT"
	sleep $TIMEOUT
done
