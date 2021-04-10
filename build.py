import os
import subprocess
import sys



if (os.path.exists("build")):
	dl=[]
	for r,ndl,fl in os.walk("build"):
		dl=[os.path.join(r,k) for k in ndl]+dl
		for f in fl:
			os.remove(os.path.join(r,f))
	for k in dl:
		os.rmdir(k)
else:
	os.mkdir("build")
os.system("echo off&&echo NUL>_.class&&del /s /f /q *.class&&cls&&rm -rf build&&mkdir build")
o=subprocess.Popen(["javac","src/com/krzem/ev3/client/Main.java"],stdout=subprocess.PIPE,stderr=subprocess.PIPE).communicate()[1]
if (len(o)>0):
	print(o.decode("utf-8"))
	quit()
o=subprocess.Popen(["javac","-d","./build/","src/com/krzem/ev3/server/Main.java"],stdout=subprocess.PIPE,stderr=subprocess.PIPE).communicate()[1]
if (len(o)>0):
	print(o.decode("utf-8"))
	quit()
os.system("cd build&&jar cv0fm ./java-server.jar ../manifest.mf com/krzem/ev3/server/Main.class * &&pscp -load ev3 -l robot -pw maker java-server.jar robot@KrzemEv3:/home/robot/.tmp/")
if (b"run_java_server.sh\n" not in subprocess.Popen(["plink","-ssh","robot@KrzemEv3","-pw","maker","-batch","cd","~/.tmp/&&ls"],stdout=subprocess.PIPE).communicate()[0]):
	os.system("dos2unix ./run_java_server.sh&&pscp -load \"ev3\" -l robot -pw maker ./run_java_server.sh robot@KrzemEv3:/home/robot/.tmp/&&plink -ssh robot@KrzemEv3 -pw maker -batch chmod u+x .tmp/run_java_server.sh")
os.system("cls&&java com/krzem/ev3/client/Main&&start /min cmd /c \"echo NUL>_.class&&del /s /f /q *.class&&rm -rf build\"")
