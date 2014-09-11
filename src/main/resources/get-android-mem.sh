#!/bin/sh

#path--target/android-info/mem/dat
cd ../../../
mkdir -p target/android-info/mem/dat
mkdir -p target/android-info/mem/html
cp src/main/resources/jscharts.js target/android-info/mem/html
cd target/android-info/mem/dat

while true
do
    adb shell ps | awk '{print $5" "$9}' >> mem.dat
sleep 15
done