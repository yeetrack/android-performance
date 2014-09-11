#!/bin/sh
#android 4.0以上和4.0以下方法不同
#get android sdk level
apileveltemp=`adb shell getprop | grep ro.build.version.sdk`
apilevel=${apileveltemp:25:2}
chmod +x *.sh

echo "android api level:"$apilevel
if [ $apilevel -gt 14 ]
then
    ./get-android-net-gt-4.0.sh
elif [ $apilevel -lt 14 ]
then
    ./get-android-net-lt-4.0.sh
fi
