package com.cpscanner.main;
import static java.lang.System.out;
import java.io.*;
import java.util.*;
import javax.security.auth.login.LoginException;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.*;
public class ScannerV0_2_0
{
	private JDA jda;
	public static final String TOKEN="MzY3NDI2MTc4MDE5MjI5Njk3.DuSQ2w.hjmrZBrDSrRZRibXbls_rccXBM0";
	public static final long ID=367426178019229697L;
	private Thread thread1;
	private Thread thread2;
	private volatile MessageListener msgs;
	private volatile MessageChannel channel;
	private boolean running;
	private LinkedHashMap<Long,Guild>guilds;
	public ScannerV0_2_0()throws LoginException
	{
		this.jda=new JDABuilder(AccountType.BOT).setToken(ScannerV0_2_0.TOKEN).build();
		try
		{
			this.jda.awaitReady();
		}
		catch(InterruptedException e)
		{
			e.printStackTrace();
		}
		this.jda.addEventListener(this.msgs=new MessageListener());
		this.thread1=new Thread(this::run1);
		this.thread2=new Thread(this::run2);
		this.guilds=new LinkedHashMap<Long,Guild>();
		out.println(this.jda.getGuilds());
		Iterator<Guild>it=this.jda.getGuilds().iterator();
		Guild g=null;
		while(it.hasNext())
		{
			g=it.next();
			this.guilds.put(g.getIdLong(),g);
		}
		out.println(this.guilds);
		it=this.guilds.values().iterator();
		this.channel=it.next().getDefaultChannel();
		out.println(this.channel.getName());
	}
	public void run1()
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
	public void run2()
	{
		Message msg=null;
		boolean printed=false;
		while(this.running)
		{
			if(this.msgs.isMessageNew())
			{
				msg=this.msgs.getLastMessage();
				printed=false;
			}
			if(this.channel!=null&&msg!=null)
			{
				if(!printed&&msg.getChannel().equals(this.channel))
				{
					out.printf("Server: %x (%s), Channel: %x (%s)"+System.getProperty("line.separator"),msg.getGuild().getIdLong(),msg.getGuild().getName(),msg.getChannel().getIdLong(),msg.getChannel().getName());
					out.printf("%x (%s AKA %s): %s"+System.getProperty("line.separator"),msg.getAuthor().getIdLong(),msg.getAuthor().getName(),msg.getGuild().getMember(msg.getAuthor()).getNickname(),msg.getContentDisplay());
					printed=true;
				}
			}
		}
	}
	public synchronized void start()
	{
		this.thread1.start();
		this.thread2.start();
		this.running=true;
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