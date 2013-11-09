#javac -classpath hadoop-1.2.1/hadoop-core-1.2.1.jar:hadoop-1.2.1/lib/commons-cli-1.2.jar:./json-simple-1.1.1.jar -d playground/classes/ playground/src/WordCount.java

#jar -cvf playground/wordcount.jar -C playground/classes/ .


#javac -classpath hadoop-*-core.jar -d playground/classes playground/src/WordCount.java

#jar -cvf playground/wordcount.jar -C playground/classes/ .


#javac -classpath hadoop-core-1.2.1.jar:lib/commons-cli-1.2.jar:./json-simple-1.1.1.jar -d playground/classes/ playground/src/empanada.java 

#jar -cvf playground/empanada.jar -C playground/classes/ .


#javac -classpath hadoop-core-1.2.1.jar:lib/commons-cli-1.2.jar:./json-simple-1.1.1.jar -d playground/classes/ playground/src/WordCount.java 

#jar -cvf playground/wordcount.jar -C playground/classes/ .


#javac -classpath hadoop-core-1.2.1.jar:lib/commons-cli-1.2.jar:./json-simple-1.1.1.jar:lib/java-json.jar -d playground/classes/ playground/src/empanada2.java 

#jar -cvf playground/empanada2.jar -C playground/classes/ .


javac -classpath hadoop-core-1.2.1.jar:lib/commons-logging-1.1.1.jar:lib/commons-logging-api-1.0.4.jar:lib/commons-cli-1.2.jar:./json-simple-1.1.1.jar:lib/java-json.jar -d playground/classes/ playground/src/empanada3.java 

jar -cvf playground/empanada3.jar -C playground/classes/ .

javac -classpath hadoop-core-1.2.1.jar:lib/commons-cli-1.2.jar:./json-simple-1.1.1.jar:lib/java-json.jar -d playground/classes playground/src/MyWordCount.java

jar -cvf playground/mywordcount.jar -C playground/classes/ .

#javac -classpath hadoop-core-1.2.1.jar:lib/commons-cli-1.2.jar:./json-simple-1.1.1.jar:lib/java-json.jar -d playground/classes/ playground/src/empanada4.java 

#jar -cvf playground/empanada4.jar -C playground/classes/ .

sudo cp playground/empanada3.jar /usr/local/hadoop/elsa
