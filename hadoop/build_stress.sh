#!/bin/bash

export JAVA_HOME=/usr/lib/jvm/java-6-openjdk-amd64

javac -classpath hadoop-1.2.1/hadoop-core-1.2.1.jar:hadoop-1.2.1/lib/commons-cli-1.2.jar:stress/classes -d stress/classes/ stress/src/StressTest.java

jar -cvf stress/StressTest.jar -C stress/classes/ .
