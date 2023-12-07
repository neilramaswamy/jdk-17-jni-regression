#!/bin/bash

JAVA_8_PATH=/usr/lib/jvm/java-8-openjdk-amd64
JAVA_17_PATH=/usr/lib/jvm/zulu17-ca-amd64

TIMES=5

jenv local zulu64-1.8.0.392

# mvn doesn't work with JDK 17?
# https://stackoverflow.com/questions/44438848/maven-crashes-when-trying-to-compile-a-project-error-executing-maven
mvn package

echo JAVA 8 TIMES
echo ----------------------------------------------
for i in {1..10}
do
    $(which java) -cp target/jni-threads-1.0-SNAPSHOT.jar org.ramaswamy.jdk17.Repro --log=true 2> error.log
    # echo "Log file size is $(du ./logs/rocksdb-demo.log | awk '{print $1'})"
    rm ./logs/rocksdb-demo.log
done
echo ----------------------------------------------

jenv local zulu64-17.0.9

echo JAVA 17 TIMES
echo ----------------------------------------------
for i in {1..10}
do
    $(which java) -cp target/jni-threads-1.0-SNAPSHOT.jar org.ramaswamy.jdk17.Repro --log=true 2> error.log
    # echo "Log file size is $(du ./logs/rocksdb-demo.log | awk '{print $1'})"
    rm ./logs/rocksdb-demo.log
done
echo ----------------------------------------------
