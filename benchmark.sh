#!/bin/bash

# Build with JDK 8: we'll use it for both JDK 8 and 17 runs
jenv local zulu64-1.8.0.392
mvn package

# Run App for JDK 8
echo "Computing $(jenv local) flush times"
java -cp target/jni-threads-1.0-SNAPSHOT.jar org.ramaswamy.jdk17.App

# Run App for JDK 17
jenv local zulu64-17.0.9
echo "Computing $(jenv local) flush times"
echo "JDK 17 flush times"
java -cp target/jni-threads-1.0-SNAPSHOT.jar org.ramaswamy.jdk17.App


