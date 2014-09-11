#!/bin/sh

#path--target/android-info/mem/dat
#for windows start
#for windows end
cd ../../../
mkdir -p target/android-info/cpu/dat
mkdir -p target/android-info/cpu/html

cp src/main/resources/jscharts.js target/android-info/cpu/html
cd target/android-info/cpu/dat

while true
do
    adb shell top -n 1| awk '{print $3" "$10}' >> cpu.dat
sleep 15
done