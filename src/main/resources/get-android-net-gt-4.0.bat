cd ..\..\jenkins\workspace\android-info-end3
mkdir target\android-info\net\dat
mkdir target\android-info\net\html
copy src\main\resources\jscharts.js target\android-info\net\html
copy src\main\resources\packages.list target\android-info\net
copy src\main\java\com\meilishuo\android\performance\GetNetInfo.java target\android-info\net\dat

cd target\android-info\net\dat


:run

    javac GetNetInfo.java
    java GetNetInfo
    ping 127.0.0.1 -n 15 > null
goto run