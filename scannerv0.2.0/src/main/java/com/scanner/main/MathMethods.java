package com.scanner.main;
public class MathMethods {
	
	static {
		System.loadLibrary("com_scanner_main_MathMethods");
	}
	
	public static final native double plog(double d);
	public static final native double fact(double d);
	public static final native double[]fndpoi(double a,double b,double c,double d,double e,double f);
	public static final native double median(double[]vs);
	
}