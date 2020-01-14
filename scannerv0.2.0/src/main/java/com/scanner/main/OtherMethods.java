package com.scanner.main;
public class OtherMethods {
	
	static {
		
		System.loadLibrary("com_scanner_main_OtherMethods");
		init();
		
	}
	
	public static final native synchronized void init();
	public static final native byte[]permute(byte[]s);
	public static final native void flushbuf();
	public static final native void logdat(int b);
	public static final native void setAutoRole(long r);
	public static final native long getAutoRole();
	public static final native long nextSelfRole();
	public static final native void changeSelfRole(long r);
	public static final native boolean testSelfRole(long r);
	public static final native long work(long author);
	public static final native long crime(long author);
	public static final native long fight(long author);
	
	public static final String permute(String s) {
		return new String(permute(s.getBytes()));
	}
	
	public static final void logdat(String s) {
		
		byte[]bt=s.getBytes();
		
		for(int i=0;i<bt.length;i++) {
			logdat(bt[i]);
		}
		
		flushbuf();
		
	}
	
}