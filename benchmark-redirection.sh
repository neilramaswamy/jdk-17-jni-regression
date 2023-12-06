#!/bin/bash

JAVA_8_PATH=/usr/lib/jvm/java-8-openjdk-amd64
JAVA_17_PATH=/usr/lib/jvm/zulu17-ca-amd64

jenv local zulu64-1.8.0.392
mvn package

# JDK 8
$(which java) -cp target/jni-threads-1.0-SNAPSHOT.jar org.ramaswamy.jdk17.Repro --log=true > jdk8.txt
# echo "Log file size is $(du ./logs/rocksdb-demo.log | awk '{print $1'})"
rm ./logs/rocksdb-demo.log

jenv local zulu64-17.0.9

# JDK 17
$(which java) -cp target/jni-threads-1.0-SNAPSHOT.jar org.ramaswamy.jdk17.Repro --log=true 2> /dev/null > jdk17.txt
# echo "Log file size is $(du ./logs/rocksdb-demo.log | awk '{print $1'})"
rm ./logs/rocksdb-demo.log
