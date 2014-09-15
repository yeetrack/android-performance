cd ..\..\jenkins\workspace\android-info-end3
mkdir target\android-info\cpu\dat
mkdir target\android-info\cpu\html

copy src\main\resources\jscharts.js target\android-info\cpu\html
cd target\android-info\cpu\dat

:run
	adb shell top -n 1 | awk "{print $3\" \"$10}" >> cpu.dat
	ping 127.0.0.1 -n 15 > null
goto run