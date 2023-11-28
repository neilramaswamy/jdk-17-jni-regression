#!/bin/bash

# Build with JDK 8: we'll use it for both JDK 8 and 17 runs
jenv local zulu64-1.8.0.392
mvn package 2>/dev/null

# ----------------
# Run WITH logging
# ----------------

jenv local zulu64-1.8.0.392
echo "Computing $(jenv local) flush times WITH logging" # JDK 8
for i in {1..5}
do
    java -cp target/jni-threads-1.0-SNAPSHOT.jar org.ramaswamy.jdk17.App --log=true
    echo "Log file size is $(du ./logs/rocksdb-demo.log | awk '{print $1'})"
    rm ./logs/rocksdb-demo.log
done

jenv local zulu64-17.0.9 # JDK 17
echo "Computing $(jenv local) flush times WITH logging"
for i in {1..5}
do
    java -cp target/jni-threads-1.0-SNAPSHOT.jar org.ramaswamy.jdk17.App --log=true 2>/dev/null
    echo "Log file size is $(du ./logs/rocksdb-demo.log | awk '{print $1'})"
    rm ./logs/rocksdb-demo.log
done