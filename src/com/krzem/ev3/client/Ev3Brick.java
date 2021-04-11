package com.krzem.ev3.client;



import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.Exception;
import java.lang.ProcessBuilder;
import java.lang.Runnable;
import java.lang.Thread;
import java.net.ConnectException;
import java.net.Socket;



public class Ev3Brick{
	public static final int KEEP_DEFAULT=0;
	public static final int SET_DEFAULT=1;

	public static final class LED{
		public static final int LEFT_GREEN=1;
		public static final int LEFT_RED=2;
		public static final int RIGHT_GREEN=4;
		public static final int RIGHT_RED=8;

		public static final int GREEN=5;
		public static final int RED=10;

		public static final int LEFT=3;
		public static final int RIGHT=12;

		public static final int ALL=15;

		public static final class TRIGGER{
			public static final int NONE=0;
			public static final int DISC_ACTIVITY=1;
			public static final int TIMER=2;
			public static final int HEARTBEAT=3;
			public static final int DEFAULT_ON=4;
			public static final int BLUETOOTH_ON=5;
		}

		public static final int MIN_BRIGHTNESS=0;
		public static final int MAX_BRIGHTNESS=255;
	}



	private Socket s;
	private PrintWriter out;
	private BufferedReader in;



	public Ev3Brick(){

	}



	public void init(String ssh,String passwd){
		new Thread(new Runnable(){
			@Override
			public void run(){
				try{
					new ProcessBuilder("plink","-ssh",ssh,"-pw",passwd,"-batch","bash ~/.tmp/run_java_server.sh").inheritIO().start().waitFor();
				}
				catch (Exception e){
					e.printStackTrace();
				}
			}
		}).start();
	}



	public void connect(String addr){
		while (true){
			try{
				this.s=new Socket(addr,8000);
				this.out=new PrintWriter(this.s.getOutputStream(),true);
				this.in=new BufferedReader(new InputStreamReader(this.s.getInputStream()));
			}
			catch (ConnectException ce){
				continue;
			}
			catch (Exception e){
				e.printStackTrace();
			}
			break;
		}
	}



	public void close(){
		try{
			this.out.println("cl\0");
			this.out.flush();
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}



	public void sleep(int ms){
		try{
			Thread.sleep(ms);
		}
		catch(InterruptedException e){
			e.printStackTrace();
		}
	}



	public String reset(){
		return this._send_recive("rs");
	}



	public String get_name(){
		return this._send_recive("nm");
	}



	public void set_led(int lm,int v,int d){
		this._send_recive(String.format("sl0:%d:%d:%d",lm,v,d));
	}



	public void set_led_trigger(int lm,int t,int d){
		this._send_recive(String.format("sl1:%d:%d:%d",lm,t,d));
	}



	public String[] get_led(int lm){
		return this._send_recive(String.format("gl0:%d",lm)).split(":");
	}



	public String[] get_led_trigger(int lm){
		return this._send_recive(String.format("gl1:%d",lm)).split(":");
	}



	private String _send_recive(String dt){
		try{
			this.out.println(dt+"\0");
			this.out.flush();
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
}
