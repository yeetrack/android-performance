cd ..\..\jenkins\workspace\android-info-end3
mkdir target\android-info\mem\dat
mkdir target\android-info\mem\html
copy src\main\resources\jscharts.js target\android-info\mem\html

cd target\android-info\mem\dat

:run
	adb shell ps | awk "{print $5\" \"$9}" >> mem.dat
	ping 127.0.0.1 -n 15 > null
goto run