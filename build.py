import os
import subprocess
import sys
import zipfile



if (os.path.exists("build")):
	dl=[]
	for r,ndl,fl in os.walk("build"):
		r=r.replace("\\","/").strip("/")+"/"
		dl=[r+k for k in ndl]+dl
		for f in fl:
			os.remove(r+f)
	for k in dl:
		if (k not in ["build/client","build/server"]):
			os.rmdir(k)
else:
	os.mkdir("build")
if (not os.path.exists("build/client")):
	os.mkdir("build/client")
if (not os.path.exists("build/server")):
	os.mkdir("build/server")
cfl=[]
sfl=[]
for r,_,fl in os.walk("src"):
	r=r.replace("\\","/").strip("/")+"/"
	for f in fl:
		(cfl if r[-7:]=="client/" else sfl).append(r+f)
if (subprocess.run(["javac","-d","build/client"]+cfl).returncode!=0 or subprocess.run(["javac","-d","build/server"]+sfl).returncode!=0):
	sys.exit(1)
with zipfile.ZipFile("build/client.jar","w") as zf:
	print("Writing: META-INF/MANIFEST.MF -> client.jar")
	zf.write("manifest_client.mf",arcname="META-INF/MANIFEST.MF")
	for r,_,fl in os.walk("build/client"):
		for f in fl:
			if (f[-6:]==".class"):
				print(f"Writing: {os.path.join(r,f)[13:].replace(chr(92),'/')} -> client.jar")
				zf.write(os.path.join(r,f),os.path.join(r,f)[13:])
with zipfile.ZipFile("build/server.jar","w") as zf:
	print("Writing: META-INF/MANIFEST.MF -> server.jar")
	zf.write("manifest_server.mf",arcname="META-INF/MANIFEST.MF")
	for r,_,fl in os.walk("build/server"):
		for f in fl:
			if (f[-6:]==".class"):
				print(f"Writing: {os.path.join(r,f)[13:].replace(chr(92),'/')} -> server.jar")
				zf.write(os.path.join(r,f),os.path.join(r,f)[13:])
if ("--run" in sys.argv):
	subprocess.run(["java","-jar","build/client.jar"])
