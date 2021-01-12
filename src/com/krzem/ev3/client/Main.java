package com.krzem.ev3.client;



public class Main{
	public static void main(String[] args){
		Ev3Brick ev3=new Ev3Brick();
		// ev3.init("robot@KrzemEv3","maker");
		ev3.connect("KrzemEv3");
		ev3.set_led(Ev3Brick.LED.GREEN,Ev3Brick.LED.MAX_BRIGHTNESS,Ev3Brick.SET_DEFAULT);
		ev3.set_led(Ev3Brick.LED.RED,Ev3Brick.LED.MIN_BRIGHTNESS,Ev3Brick.SET_DEFAULT);
		ev3.set_led_trigger(Ev3Brick.LED.ALL,Ev3Brick.LED.TRIGGER.NONE,Ev3Brick.SET_DEFAULT);
		System.out.println(ev3.get_name());
		ev3.set_led(Ev3Brick.LED.GREEN,Ev3Brick.LED.MIN_BRIGHTNESS,Ev3Brick.KEEP_DEFAULT);
		ev3.set_led(Ev3Brick.LED.RED,Ev3Brick.LED.MAX_BRIGHTNESS,Ev3Brick.KEEP_DEFAULT);
		ev3.sleep(3000);
		System.out.println(java.util.Arrays.toString(ev3.get_led(Ev3Brick.LED.ALL)));
		ev3.set_led_trigger(Ev3Brick.LED.ALL,Ev3Brick.LED.TRIGGER.HEARTBEAT,Ev3Brick.KEEP_DEFAULT);
		ev3.sleep(3000);
		ev3.reset();
		ev3.close();
	}
}