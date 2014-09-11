#!/bin/sh

#android api level great than 14(android 4.0)


#path--target/android-info/net/dat
cd ../../../
mkdir -p target/android-info/net/dat
mkdir -p target/android-info/net/html
cp src/main/resources/jscharts.js target/android-info/net/html
cd target/android-info/net/dat

cd ..
echo "adb pull/data/system/packages.list--start"
adb pull /data/system/packages.list
cd dat

while true
do

    echo "get net info from /proc/uid-stat/$uid"
    for i in `adb shell ls /proc/uid_stat`
    do
        #delete the Enter character
        uid=`echo $i | tr -d ["\r\n"]`
        adb shell cat /proc/uid_stat/$uid/tcp_rcv >> $uid"_recv.dat"
        adb shell cat /proc/uid_stat/$uid/tcp_snd >> $uid"_snd.dat"
    done
    sleep 15
done