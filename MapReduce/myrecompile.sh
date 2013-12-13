
javac -classpath hadoop-core-1.2.1.jar:lib/commons-cli-1.2.jar:lib/json-simple-1.1.1.jar:lib/java-json.jar:lib/commons-logging-api-1.0.4.jar -d playground/classes playground/src/MyWordCount.java

javac -classpath hadoop-core-1.2.1.jar:lib/commons-cli-1.2.jar:lib/json-simple-1.1.1.jar:lib/java-json.jar:lib/commons-logging-api-1.0.4.jar -d playground/classes/ playground/src/FileMaker.java
javac -classpath hadoop-core-1.2.1.jar:lib/commons-cli-1.2.jar:lib/json-simple-1.1.1.jar:lib/java-json.jar:lib/commons-logging-api-1.0.4.jar:playground/classes/ -d playground/classes/ playground/src/TweetClassifier.java 
jar -cvf playground/empanada.jar -C playground/classes/ .



