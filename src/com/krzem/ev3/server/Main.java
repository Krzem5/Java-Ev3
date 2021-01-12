package com.krzem.ev3.server;



import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.Exception;
import java.net.ServerSocket;
import java.net.Socket;



public class Main{
	private static final String BRICK_NAME_PATH="/etc/hostname";
	private static final String[] LED_PATHS=new String[]{"/sys/class/leds/led0:green:brick-status/","/sys/class/leds/led0:red:brick-status/","/sys/class/leds/led1:green:brick-status/","/sys/class/leds/led1:red:brick-status/"};
	private ServerSocket ss;
	private Socket cs;
	private PrintWriter out;
	private BufferedReader in;
	private int[] _lb=new int[]{-1,-1,-1,-1};
	private String[] _lt=new String[4];



	public static void main(String[] args){
		new Main();
	}



	public Main(){
		try{
			this.ss=new ServerSocket(8000);
			System.out.println("Started socket server on port 8000");
			this.cs=this.ss.accept();
			System.out.printf("Connected to %s\n",this.cs.getInetAddress().toString());
			this.out=new PrintWriter(this.cs.getOutputStream(),true);
			this.in=new BufferedReader(new InputStreamReader(this.cs.getInputStream()));
			while (true){
				String cmd=this._read_input();
				String o=this._process(cmd);
				if (o==null){
					this.ss.close();
					return;
				}
				this.out.println(o);
				this.out.flush();
			}
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}



	private String _read_input(){
		try{
			String o="";
			while (true){
				String ln=this.in.readLine();
				o+="\n"+ln;
				if (o.contains("\0")){
					return o.substring(1).replace("\0","");
				}
			}
		}
		catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}



	private String _process(String cmd){
		try{
			String o="";
			String dt=cmd.substring(2);
			switch (cmd.substring(0,2)){
				case "rs":
					for (int i=0;i<4;i++){
						if (this._lt[i]!=null){
							this._write_file(Main.LED_PATHS[3-i]+"trigger",this._lt[i]);
						}
						if (this._lb[i]!=-1){
							System.out.println(Main.LED_PATHS[3-i]+"brightness => "+Integer.toString(this._lb[i]));
							this._write_file(Main.LED_PATHS[3-i]+"brightness",Integer.toString(this._lb[i]));
						}
					}
					this._lb=new int[]{-1,-1,-1,-1};
					this._lt=new String[4];
					break;
				case "cl":
					out.close();
					return null;
				case "nm":
					o=this._read_file(Main.BRICK_NAME_PATH);
					break;
				case "sl":
					int lm=Integer.parseInt(dt.split(":")[1]);
					String[] fl=new String[4];
					if (lm>=8){
						lm%=8;
						fl[0]=Main.LED_PATHS[3];
					}
					if (lm>=4){
						lm%=4;
						fl[1]=Main.LED_PATHS[2];
					}
					if (lm>=2){
						lm%=2;
						fl[2]=Main.LED_PATHS[1];

					}
					if (lm>=1){
						fl[3]=Main.LED_PATHS[0];
					}
					int i=0;
					for (String bfp:fl){
						if (bfp==null){
							i++;
							continue;
						}
						switch (Integer.parseInt(dt.split(":")[0])){
							case 0:
								if (this._lb[i]==-1){
									this._lb[i]=Integer.parseInt(this._read_file(bfp+"brightness"));
								}
								this._write_file(bfp+"brightness",dt.split(":")[2]);
								if (dt.split(":")[3].equals("1")){
									this._lb[i]=Integer.parseInt(dt.split(":")[2]);
								}
								break;
							case 1:
								if (this._lt[i]==null){
									this._lt[i]=this._decode_trigger(this._read_file(bfp+"trigger"));
								}
								switch (Integer.parseInt(dt.split(":")[2])){
									case 0:
										this._write_file(bfp+"trigger","none");
										break;
									case 1:
										this._write_file(bfp+"trigger","mmc0");
										break;
									case 2:
										this._write_file(bfp+"trigger","timer");
										break;
									case 3:
										this._write_file(bfp+"trigger","heartbeat");
										break;
									case 4:
										this._write_file(bfp+"trigger","default-on");
										break;
									case 5:
										this._write_file(bfp+"trigger","rfkill0");
										break;
								}
								if (dt.split(":")[3].equals("1")){
									this._lt[i]=this._decode_trigger(this._read_file(bfp+"trigger"));
								}
								break;
						}
						i++;
					}
					break;
				case "gl":
					lm=Integer.parseInt(dt.split(":")[1]);
					fl=new String[4];
					if (lm>=8){
						lm%=8;
						fl[0]=Main.LED_PATHS[3];
					}
					if (lm>=4){
						lm%=4;
						fl[1]=Main.LED_PATHS[2];
					}
					if (lm>=2){
						lm%=2;
						fl[2]=Main.LED_PATHS[1];

					}
					if (lm>=1){
						fl[3]=Main.LED_PATHS[0];
					}
					for (String bfp:fl){
						if (bfp==null){
							continue;
						}
						switch (Integer.parseInt(dt.split(":")[0])){
							case 0:
								o+=":"+this._read_file(bfp+"brightness");
								break;
							case 1:
								o+=":"+this._decode_trigger(this._read_file(bfp+"trigger"));
						}
					}
					if (o.length()>0){
						o=o.substring(1);
					}
					break;
			}
			return o+"\0";
		}
		catch (Exception e){
			e.printStackTrace();
		}
		return "\0";
	}



	private String _run(String[] cmd){
		try{
			Process p=new ProcessBuilder(cmd).start();
			p.waitFor();
			BufferedReader in=new BufferedReader(new InputStreamReader(p.getInputStream()));
			String ln;
			String o="";
			while ((ln=in.readLine())!=null){
				o+=ln;
			}
			in.close();
			return o;
		}
		catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}



	private void _write_file(String fp,String dt){
		try{
			BufferedWriter out=new BufferedWriter(new FileWriter(new File(fp)));
			out.write(dt);
			out.close();
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}



	private String _read_file(String fp){
		return this._run(new String[]{"cat",fp});
	}



	private String _decode_trigger(String ts){
		String o="";
		int i=1;
		while (ts.charAt(i-1)!='['){
			i++;
		}
		while (ts.charAt(i)!=']'){
			o+=String.valueOf(ts.charAt(i));
			i++;
		}
		return o;
	}
}