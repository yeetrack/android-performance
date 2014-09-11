#!/bin/sh
#windows平台使用cywgin调用sh
#cd /cygdrive/c/Users/whuyi/Desktop/android-performance/trunk/src/main/resources/
pwd
sh get-android-cpu.sh &
sh get-android-mem.sh &
sh get-android-net.sh &