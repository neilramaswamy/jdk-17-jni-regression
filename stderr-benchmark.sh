#!/bin/bash

JAVA_8_PATH=/usr/lib/jvm/java-8-openjdk-amd64
JAVA_17_PATH=/usr/lib/jvm/zulu17-ca-amd64

TIMES=5

jenv local zulu64-1.8.0.392
mvn package

jenv local zulu64-1.8.0.392
echo JAVA 8 TIMES
echo ----------------------------------------------
for i in {1..1}
do
    $(which java) -cp target/jni-threads-1.0-SNAPSHOT.jar org.ramaswamy.jdk17.ReproStderr 2> error.log
    echo "Log file size is $(du error.log | awk '{print $1'})"
done
echo ----------------------------------------------

jenv local zulu64-17.0.9
echo JAVA 17 TIMES
echo ----------------------------------------------
for i in {1..1}
do
    $(which java) -cp target/jni-threads-1.0-SNAPSHOT.jar org.ramaswamy.jdk17.ReproStderr 2> error.log
    echo "Log file size is $(du error.log | awk '{print $1'})"
done
echo ----------------------------------------------
