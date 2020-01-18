package com.cpscanner.main;
import static java.lang.System.out;
import java.io.*;
import java.util.*;
import javax.security.auth.login.LoginException;
import com.cpscanner.cmd.*;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.*;
public class ScannerV0_2_0
{
	public static final String TOKEN="MzY3NDI2MTc4MDE5MjI5Njk3.DuSQ2w.hjmrZBrDSrRZRibXbls_rccXBM0";
	public static final long ID=367426178019229697L;
	public static final String[]getCmdArgs(String s)
	{
		return s.split("\\s");
	}
	public static final boolean isValidNumber(String s)
	{
		boolean valid=true;
		for(int i=0;i<s.length()&&valid;i++)
		{
			valid=s.charAt(i)>=48&&s.charAt(i)<58;
		}
		return valid;
	}
	private JDA jda;
	private Thread thread1;
	private Thread thread2;
	private volatile MessageListener msgs;
	private volatile MessageChannel channel;
	private volatile Guild guild;
	private boolean running;
	private LinkedHashMap<Long,Guild>guilds;
	private CmdParser consoleCommandParser;
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
		this.channel=(this.guild=it.next()).getDefaultChannel();
		out.println(this.channel.getName());
		String[]names="list change".split(" ");
		ScCmd[]parsers={this::parseListGuildsAndChannels,this::parseChangeChannel};
		this.consoleCommandParser=new CmdParser(parsers,names);
	}
	public String parseChangeChannel(Guild guild,User author,long channel,String...args)
	{
		String result="";
		if(args.length>=2)
		{
			Guild targetg=null;
			String sguild=args[0];
			String schannel=args[1];
			if(!ScannerV0_2_0.isValidNumber(sguild))
			{
				out.println("a string");
				Iterator<Guild>_it_=this.guilds.values().iterator();
				boolean found=false;
				while(!found&&_it_.hasNext())
				{
					targetg=_it_.next();
					if(targetg.getName().equals(sguild))
					{
						found=true;
					}
				}
				if(!found)
				{
					targetg=null;
				}
			}
			else
			{
				targetg=this.guilds.get(Long.parseLong(sguild));
			}
			if(targetg==null)
			{
				result="Error: Guild was not found!";
			}
			else
			{
				ArrayList<GuildChannel>channels=new ArrayList<GuildChannel>();
				channels.addAll(targetg.getChannels());
				result="Error: Channel was not found!";
				for(int i=0;i<channels.size();i++)
				{
					if(channels.get(i).getId().equals(schannel)||channels.get(i).getName().equals(schannel))
					{
						if(channels.get(i).getType()==ChannelType.TEXT)
						{
							this.guild=targetg;
							this.channel=(TextChannel)channels.get(i);
							result="Sucessfully changed to channel %d (%s) in guild %d (%s).";
							result=String.format(result,targetg.getIdLong(),targetg.getName(),this.channel.getIdLong(),this.channel.getName());
							i=channels.size();
						}
					}
				}
			}
		}
		return result;
	}
	public String parseListGuildsAndChannels(Guild guild,User user,long channel,String[]args)
	{
		String result="Usage: list <all|guilds|channels>";
		if(args.length>=1)
		{
			Iterator<Guild>_it_=null;
			result="";
			switch(args[0])
			{
				case"all":
					ArrayList<GuildChannel>channels=new ArrayList<GuildChannel>();
					_it_=this.guilds.values().iterator();
					while(_it_.hasNext())
					{
						guild=_it_.next();
						result+=guild.getId()+", Name: "+guild.getName()+"\r\n";
						channels.clear();
						channels.addAll(guild.getChannels());
						for(int i=0;i<channels.size();i++)
						{
							result+="\t"+channels.get(i).getId()+", Name: "+channels.get(i).getName()+"\r\n";
						}
					}
					break;
				case"guilds":
					_it_=this.guilds.values().iterator();
					while(_it_.hasNext())
					{
						guild=_it_.next();
						result+=guild.getId()+", Name: "+guild.getName()+"\r\n";
					}
					break;
				case"channels":
					Iterator<GuildChannel>it=this.guild.getChannels().iterator();
					GuildChannel ch=null;
					while(it.hasNext())
					{
						ch=it.next();
						result+=ch.getId()+", Name: "+ch.getName()+"\r\n";
					}
					break;
				default:
					result="Invalid argument. Valid arguments are all, guilds, and channels.";
			}
		}
		return result;
	}
	public void run1()
	{
		BufferedReader reader=new BufferedReader(new InputStreamReader(System.in));
		try
		{
			String s=reader.readLine();
			while(s!=null)
			{
				out.println(this.consoleCommandParser.parse(null,null,0,ScannerV0_2_0.getCmdArgs(s)));
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
					out.printf("%x (%s AKA %s): %s"+System.getProperty("line.separator"),msg.getAuthor().getIdLong(),msg.getAuthor().getName(),msg.getGuild().getMember(msg.getAuthor()).getEffectiveName(),msg.getContentDisplay());
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