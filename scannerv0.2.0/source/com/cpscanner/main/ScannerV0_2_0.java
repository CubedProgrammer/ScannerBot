package com.cpscanner.main;
import java.io.*;
import javax.security.auth.login.LoginException;
import net.dv8tion.jda.api.*;
public class ScannerV0_2_0
{
	private JDA jda;
	public static final String TOKEN="MzY3NDI2MTc4MDE5MjI5Njk3.DuSQ2w.hjmrZBrDSrRZRibXbls_rccXBM0";
	public static final long ID=367426178019229697L;
	private Thread thread;
	public ScannerV0_2_0()throws LoginException
	{
		this.jda=new JDABuilder(AccountType.BOT).setToken(ScannerV0_2_0.TOKEN).build();
		this.jda.addEventListener(new MessageListener());
		this.thread=new Thread(this::run);
	}
	public void run()
	{
		BufferedReader reader=new BufferedReader(new InputStreamReader(System.in));
		try
		{
			String s=reader.readLine();
			while(s!=null)
			{
				s="exit".equals(s)?null:reader.readLine();
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		System.exit(0);
	}
	public synchronized void start()
	{
		this.thread.start();
	}
	public static final void main(String[]args)
	{
		try
		{
			new ScannerV0_2_0().start();
		}
		catch(LoginException e)
		{
			e.printStackTrace();
		}
	}
}