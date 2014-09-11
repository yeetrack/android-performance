#!/bin/sh


#path--target/android-info/net/dat
cd ../../../
mkdir -p target/android-info/net/dat
mkdir -p target/android-info/net/html
cp src/main/resources/jscharts.js target/android-info/net/html
cd target/android-info/net/dat

#get /proc/$pid/net/dev
while true
do
    #get pid
    for i in `adb shell ps | awk '{print $2}'`
    do
        pid=`echo $i | tr -d ["\r\n"]`
        echo $pid
        adb shell cat /proc/$pid/net/dev | grep wlan | awk '{print $2" "$10}' >> $pid"_net.dat"
    done

    sleep 15
done
