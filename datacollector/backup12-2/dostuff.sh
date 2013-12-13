#!/bin/bash
SLEEP=1800

while :
do
echo 'moving:'
#/usr/local/hadoop/bin/hadoop fs -rm /user/jonathan/tweets/pending/
/usr/local/hadoop/bin/hadoop fs -put fplTweets.txt /user/jonathan/tweets/pending/
rm fplTweets.txt

echo "sleeping for $SLEEP"
sleep $SLEEP
done
