android-performance
===================

抓取android手机cpu、内存、流量信息，使用jschart生成html报表.

使用shell脚本抓取android手机信息，脚本路径src/main/resources  
##cpu##


	adb shell top -n 1| awk '{print $3" "$10}' >> cpu.dat
	
##mem##


	adb shell ps | awk '{print $5" "$9}' >> mem.dat
	
##流量##

+ android sdk level 大于16


		adb shell cat /proc/uid_stat/$uid/tcp_rcv >> $uid"_recv.dat"
		adb shell cat /proc/uid_stat/$uid/tcp_snd >> $uid"_snd.dat"
+ android sdk level 小于16


		adb shell cat /proc/$pid/net/dev | grep wlan | awk '{print $2" "$10}' >> $pid"_net.dat"
		
过程就是通过循环调用上面的命令，将结果写到dat文件，然后再解析dat文件，最后使用jscharts绘出走势图。  

##使用方法##
1.  执行`mvn clean`，清除上次数据
2.  执行**get-android-info.sh**,开始收集数据
3. 一段时间后执行**kill-scripts.sh**，结束收集
4. 执行`mvn test`解析dat，生成html报表。报表在target/android-info下。