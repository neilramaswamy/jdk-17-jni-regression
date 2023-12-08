#!/bin/bash

if [ "$#" -lt 1 ]; then
    echo "Error: No arguments provided. Please provide the full path to the RocksDB jar you intend to use."
    exit 1
fi

# Set variable foo to the first argument
path_to_rocksdb_jar="$1"

# Link in the custom-built RocksDB binary
jenv local 1.8
mvn clean
mvn install:install-file \
	-Dfile=$path_to_rocksdb_jar  \
	-DgroupId=com.ramaswamy.jdk17 \
	-DartifactId=my-rocksdb \
	-Dversion=0.0.1 \
	-Dpackaging=jar

# Build with Java 8 so that the binary works with both JDK 8 and 17
jenv local 1.8
mvn package

echo JAVA 8 TIMES
echo ----------------------------------------------
for i in {1..1}
do
    $(which java) -cp target/jni-threads-1.0-SNAPSHOT.jar org.ramaswamy.jdk17.Repro --log=true > jdk8.txt
    echo "Log file size is $(du ./logs/rocksdb-demo.log | awk '{print $1'})"
    rm ./logs/rocksdb-demo.log
done
echo ----------------------------------------------

jenv local 17.0
echo JAVA 17 TIMES
echo ----------------------------------------------
for i in {1..1}
do
    $(which java) -cp target/jni-threads-1.0-SNAPSHOT.jar org.ramaswamy.jdk17.Repro --log=true > jdk17.txt
    echo "Log file size is $(du ./logs/rocksdb-demo.log | awk '{print $1'})"
    rm ./logs/rocksdb-demo.log
done
echo ----------------------------------------------