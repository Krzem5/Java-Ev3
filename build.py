import os
import shlex
import subprocess



os.system("echo off&&echo NUL>_.class&&del /s /f /q *.class&&cls&&rm -rf server-build&&mkdir server-build")
o=subprocess.Popen(["javac","com/krzem/ev3/client/Main.java"],stdout=subprocess.PIPE,stderr=subprocess.PIPE).communicate()[1]
if (len(o)>0):
	print(o.decode("utf-8"))
	quit()
o=subprocess.Popen(["javac","-d","./server-build/","com/krzem/ev3/server/Main.java"],stdout=subprocess.PIPE,stderr=subprocess.PIPE).communicate()[1]
if (len(o)>0):
	print(o.decode("utf-8"))
	quit()
os.system("cd server-build&&jar cv0fm ./java-server.jar ../manifest.mf com/krzem/ev3/server/Main.class * &&pscp -load ev3 -l robot -pw maker java-server.jar robot@KrzemEv3:/home/robot/.tmp/")
if (b"run_java_server.sh\n" not in subprocess.Popen(shlex.split("plink -ssh robot@KrzemEv3 -pw maker -batch cd ~/.tmp/&&ls"),stdout=subprocess.PIPE).communicate()[0]):
	os.system("dos2unix ./run_java_server.sh&&pscp -load \"ev3\" -l robot -pw maker ./run_java_server.sh robot@KrzemEv3:/home/robot/.tmp/&&plink -ssh robot@KrzemEv3 -pw maker -batch chmod u+x .tmp/run_java_server.sh")
os.system("cls&&java com/krzem/ev3/client/Main&&start /min cmd /c \"echo NUL>_.class&&del /s /f /q *.class&&rm -rf server-build\"")
