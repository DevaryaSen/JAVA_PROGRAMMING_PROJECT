#!/bin/bash
set -e
echo "Building Smart Study Planner Pro..."
mkdir -p out/classes
find src/main/java -name "*.java" > sources.txt
javac -encoding UTF-8 -d out/classes @sources.txt
jar --create --file smart-study-planner-pro.jar --main-class com.studyplanner.App -C out/classes .
rm -f sources.txt
echo "Build complete! Run with: java -jar smart-study-planner-pro.jar"
