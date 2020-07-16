package com.cpscanner.main;
import static java.lang.System.out;
import java.io.*;
import java.math.*;
import java.util.*;
import java.util.regex.Pattern;
import javax.security.auth.login.LoginException;
import org.apache.commons.math3.special.Erf;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import com.cpscanner.cmd.*;
import com.cpscanner.algorithm.*;
/**
 * Main class that contains the main method.
 * @author CubedProgrammer
 */
public class ScannerV0_2_1
{
	/**
	 * Token for bot authentication.
	 */
	public static final String TOKEN="MzY3NDI2MTc4MDE5MjI5Njk3.XtkugQ.Z7o1Wj5JoBRr2Cz7WnJzh9-VLP8";
	/**
	 * Bot snowflake identifier.
	 */
	public static final long ID=367426178019229697L;
	/**
	 * Delimiter character.
	 */
	public static final char DELIMITER=' ';
	/**
	 * Escape character.
	 */
	public static final char ESCAPE='\\';
	/**
	 * Quotation character.
	 */
	public static final char BRACKET=34;
	/**
	 * Parse a string into space-separated arguments.
	 * @param s The string to parse.
	 * @return An array of strings where each string is an argument.
	 */
	public static final String[]getCmdArgs(String s)
	{
		StringBuilder builder=new StringBuilder();
		boolean open=false;
		boolean escaped=false;
		char[]cs=s.toCharArray();
		ArrayList<String>argsal=new ArrayList<String>();
		for(int i=0;i<cs.length;i++)
		{
			if(cs[i]==ScannerV0_2_1.BRACKET&&!escaped)
			{
				open=!open;
			}
			else if(cs[i]==ScannerV0_2_1.DELIMITER&&!open)
			{
				argsal.add(builder.toString());
				builder.delete(0,builder.length());
			}
			else if(!escaped&&cs[i]==ScannerV0_2_1.ESCAPE)
			{
				escaped=true;
			}
			else
			{
				escaped=false;
				builder.append(cs[i]);
			}
		}
		if(builder.length()>0)
		{
			argsal.add(builder.toString());
		}
		return argsal.toArray(new String[argsal.size()]);
	}
	/**
	 * Checks if a string is a valid number.
	 * @param The string to check.
	 * @return True if the string is a valid non-negative integer, false otherwise.
	 */
	public static final boolean isValidNumber(String s)
	{
		boolean valid=true;
		for(int i=0;i<s.length()&&valid;i++)
		{
			valid=s.charAt(i)>=48&&s.charAt(i)<58;
		}
		return valid;
	}
	/**
	 * Converts a string that is potentially a fraction into a BigDecimal
	 * @param s The string representation of a number.
	 * @return The BigDecimal that the string represents.
	 */
	public static final BigDecimal strToNum(String s)
	{
		String[]parts=s.split("/");
		BigDecimal n=new BigDecimal(parts[0]);
		for(int i=1;i<parts.length;i++)
		{
			n=n.divide(new BigDecimal(parts[i]),MathContext.DECIMAL128);
		}
		return n;
	}
	/**
	 * The main JDA object of the bot.
	 */
	private JDA jda;
	/**
	 * The thread for parsing console commands.
	 */
	private Thread thread1;
	/**
	 * The thread for reading messages and parsing discord commands.
	 */
	private Thread thread2;
	/**
	 * The message listener that listens for messages.
	 */
	private volatile MessageListener msgs;
	/**
	 * Channel to print messages from to the console.
	 */
	private volatile MessageChannel channel;
	/**
	 * Guild the selected channel is in.
	 */
	private volatile Guild guild;
	/**
	 * Whether or not this application is running.
	 */
	private boolean running;
	/**
	 * The map of guilds this bot is in.
	 */
	private LinkedHashMap<Long,Guild>guilds;
	/**
	 * The parser for console commands.
	 */
	private CmdParser consoleCommandParser;
	/**
	 * The parser for discord commands.
	 */
	private CmdParser discordCommandParser;
	/**
	 * Prefix for commands on discord.
	 */
	private String prefix;
	/**
	 * Words that get people banned.
	 */
	private String[]banwords;
	/**
	 * Data for economy.
	 */
	@SuppressWarnings("rawtypes")
	private LinkedHashMap<Long,LinkedHashMap>economy;
	/**
	 * Constructor for the bot's main class.
	 * @throws LoginException
	 */
	@SuppressWarnings("rawtypes")
	public ScannerV0_2_1()throws LoginException
	{
		this.jda=new JDABuilder(AccountType.BOT).setToken(ScannerV0_2_1.TOKEN).build();
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
		File f=null;
		File ff=null;
		FileWriter writer=null;
		BufferedReader reader=null;
		String regex="";
		ArrayList<String>regexes=new ArrayList<String>();
		while(it.hasNext())
		{
			g=it.next();
			this.guilds.put(g.getIdLong(),g);
			f=new File(g.getId());
			if(!f.exists())
			{
				f.mkdir();
				try
				{
					ff=new File(f.getAbsolutePath()+"/roleinfo.dat");
					ff.createNewFile();
					writer=new FileWriter(ff);
					writer.append("{}");
					writer.close();
					ff=new File(f.getAbsolutePath()+"/ecogame.dat");
					ff.createNewFile();
					writer=new FileWriter(ff);
					writer.append("{}");
					writer.close();
					ff=new File(f.getAbsolutePath()+"/names.dat");
					ff.createNewFile();
					writer=new FileWriter(ff);
					writer.append("{}");
					writer.close();
					ff=new File(f.getAbsolutePath()+"/banwords.txt");
					ff.createNewFile();
				}
				catch(IOException e)
				{
					e.printStackTrace();
				}
			}
			try
			{
				reader=new BufferedReader(new FileReader(g.getId()+"/banwords.txt"));
				regex=reader.readLine();
				while(regex!=null)
				{
					regexes.add(regex);
					regex=reader.readLine();
				}
				this.banwords=regexes.toArray(new String[regexes.size()]);
				reader.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		out.println(this.guilds);
		it=this.guilds.values().iterator();
		this.channel=(this.guild=it.next()).getDefaultChannel();
		out.println(this.channel.getName());
		String[]names="current list change message info sum product average gmean work addrole rmrole vname autorole toggleselfrole toggleselfroles selfrole listselfroles solvet zprob probz nickname solve_linear_equation ban_by_msg get_ban_words changelog".split(" ");
		ScCmd[]parsers={this::parseCurrentChannel,this::parseListGuildsAndChannels,this::parseChangeChannel,this::parseSendMsg,this::parseEntityInfo,this::parseSum,this::parseProduct,this::parseListAverage,this::parseGeometricMean,this::parseWork,this::parseAddRole,this::parseRemoveRole,this::parseVerifyName,this::parseAutorole,this::parseToggleSelfrole,this::parseToggleSelfroles,this::parseSelfrole,this::parseListSelfroles,this::parseSolveTriangle,this::parseZProb,this::parseProbZ,this::parseChangeNickname,this::parseSolveEquation,this::parseSetBanWords,this::parseGetBanWords,this::parseGetChangelog};
		this.consoleCommandParser=new CmdParser(parsers,names);
		this.discordCommandParser=new CmdParser(Arrays.copyOfRange(parsers,4,parsers.length),Arrays.copyOfRange(names,4,names.length));
		this.prefix="--";
		this.economy=new LinkedHashMap<Long,LinkedHashMap>();
		JSONParser parser=new JSONParser();
		Object o=null;
		try
		{
			it=this.guilds.values().iterator();
			while(it.hasNext())
			{
				g=it.next();
				reader=new BufferedReader(new InputStreamReader(new FileInputStream(g.getId()+"/ecogame.dat")));
				parser.parse(reader);
				if(o!=null)
				{
					out.println(o.getClass());
					out.println(o);
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * Gets the current guild and channel.
	 * @param guild The guild the command was sent from.
	 * @param author The user who sent the command.
	 * @param channel The ID of the channel that the command was sent from.
	 * @param args The list of arguments for this command.
	 * @return The current guild and channel.
	 */
	public String parseCurrentChannel(Guild guild,User author,long channel,String...args)
	{
		return this.guild.toString()+", "+this.channel.toString();
	}
	/**
	 * Command for changing the current channel to print the messages from. Requires two arguments, guild the channel is in, and then the channel, either name or snowflake ID.
	 * @param guild The guild the command was sent from.
	 * @param author The user who sent the command.
	 * @param channel The ID of the channel that the command was sent from.
	 * @param args The list of arguments for this command.
	 * @return A message saying whether or not the change was successful.
	 */
	public String parseChangeChannel(Guild guild,User author,long channel,String...args)
	{
		String result="";
		if(args.length>=2)
		{
			Guild targetg=null;
			String sguild=args[0];
			String schannel=args[1];
			if(!ScannerV0_2_1.isValidNumber(sguild))
			{
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
	/**
	 * Displays a list of guilds, channels in current guild, or all.
	 * @param guild The guild the command was sent from.
	 * @param author The user who sent the command.
	 * @param channel The ID of the channel that the command was sent from.
	 * @param args The list of arguments for this command.
	 * @return A string that represents the list of guilds or channels as requested by the user.
	 */
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
	/**
	 * Sends a message to currently selected channel.
	 * @param guild The guild the command was sent from.
	 * @param author The user who sent the command.
	 * @param channel The ID of the channel that the command was sent from.
	 * @param args The list of arguments for this command.
	 * @return A message indicating whether or not the message could be sent.
	 */
	public String parseSendMsg(Guild guild,User user,long channel,String...args)
	{
		String result="Error, no message to be sent!";
		if(args.length>=1)
		{
			this.channel.sendMessage(args[0]).queue();
			result="Sent message to channel "+this.channel.getName()+" in guild "+this.guild.getName()+".";
		}
		return result;
	}
	/**
	 * Gets the information on an entity, whether it be a guild, channel, or user.
	 * @param guild The guild the command was sent from.
	 * @param author The user who sent the command.
	 * @param channel The ID of the channel that the command was sent from.
	 * @param args The list of arguments for this command.
	 * @return The information on the requested entity.
	 */
	public String parseEntityInfo(Guild guild,User user,long channel,String...args)
	{
		String result="Usage: info <{name}|{id}|server>";
		if(args.length>=1)
		{
			long id=0;
			if("server".equals(args[0]))
			{
				result=String.format("Guild has %d members, %d roles, %d channels and %d categories, system channel is %s, with snowflake ID of 0x%x.",guild.getMemberCount(),guild.getRoles().size(),guild.getChannels().size(),guild.getCategories().size(),guild.getSystemChannel().getName(),guild.getSystemChannel().getIdLong());
			}
			else if(args[0].length()>4&&args[0].charAt(0)=='<'&&args[0].charAt(args[0].length()-1)=='>'&&args[0].charAt(1)=='@')
			{
				id=Long.parseLong(args[0].substring(3,args[0].length()-1));
				Date join=new Date((id>>22)*1000);
				switch(args[0].charAt(2))
				{
					case'!':
						user=this.jda.getUserById(id);
						result=String.format("User %s was created on %s, snowflake id is 0x%x, avatar is %s.",user.getName(),join.toString(),id,user.getAvatarUrl());
						break;
					case'#':
						GuildChannel ch=this.jda.getGuildChannelById(id);
						result=String.format("Channel %s was created on %s, snowflake id is 0x%x, typ is %s.",ch.getName(),join.toString(),id,ch.getType());
						break;
					case'&':
						Role r=this.jda.getRoleById(id);
						result=String.format("Role %s was created on %s, snowflake id is 0x%x, permission value is 0x%x.",r.getName(),join.toString(),id,r.getPermissionsRaw());
				}
			}
		}
		return result;
	}
	/**
	 * Gets the sum of a list of numbers.
	 * @param guild The guild the command was sent from.
	 * @param author The user who sent the command.
	 * @param channel The ID of the channel that the command was sent from.
	 * @param args The list of arguments for this command.
	 * @return The string representation of the arithmetic mean of the arguments.
	 */
	public String parseSum(Guild guild,User user,long channel,String...args)
	{
		BigDecimal n=BigDecimal.ZERO;
		for(int i=0;i<args.length;i++)
		{
			n=n.add(ScannerV0_2_1.strToNum(args[i]));
		}
		return n.toString();
	}
	/**
	 * Gets the product of a list of numbers.
	 * @param guild The guild the command was sent from.
	 * @param author The user who sent the command.
	 * @param channel The ID of the channel that the command was sent from.
	 * @param args The list of arguments for this command.
	 * @return The string representation of the arithmetic mean of the arguments.
	 */
	public String parseProduct(Guild guild,User user,long channel,String...args)
	{
		BigDecimal n=BigDecimal.ONE;
		for(int i=0;i<args.length;i++)
		{
			n=n.multiply(ScannerV0_2_1.strToNum(args[i]));
		}
		return n.toString();
	}
	/**
	 * Gets the average of a list of numbers, more precisely the arithmetic mean.
	 * @param guild The guild the command was sent from.
	 * @param author The user who sent the command.
	 * @param channel The ID of the channel that the command was sent from.
	 * @param args The list of arguments for this command.
	 * @return The string representation of the arithmetic mean of the arguments.
	 */
	public String parseListAverage(Guild guild,User user,long channel,String...args)
	{
		BigDecimal n=BigDecimal.ZERO;
		for(int i=0;i<args.length;i++)
		{
			n=n.add(ScannerV0_2_1.strToNum(args[i]));
		}
		return args.length==0?"0":n.divide(new BigDecimal(args.length),MathContext.DECIMAL128).toString();
	}
	/**
	 * Gets the geometric mean of a list of numbers.
	 * @param guild The guild the command was sent from.
	 * @param author The user who sent the command.
	 * @param channel The ID of the channel that the command was sent from.
	 * @param args The list of arguments for this command.
	 * @return The string representation of the geometric mean of the arguments.
	 */
	public String parseGeometricMean(Guild guild,User user,long channel,String...args)
	{
		BigDecimal n=BigDecimal.ONE;
		for(int i=0;i<args.length;i++)
		{
			n=n.multiply(ScannerV0_2_1.strToNum(args[i]));
		}
		return args.length==0?"0":MathAlgs.yroot(n,args.length).toString();
	}
	public String parseAddRole(Guild guild,User user,long channel,String...args)
	{
		String ans="Usage: addrole <role> <member>";
		if(args.length>=2)
		{
			ans="You do not have permission to use this command!";
			Role role=guild.getRoleById(args[0].substring(3,args[0].length()-1));
			Member target=guild.getMemberById(args[1].substring(3,args[1].length()-1));
			Member author=user==null?null:guild.getMember(user);
			if(author==null||author.hasPermission(Permission.MANAGE_ROLES))
			{
				guild.addRoleToMember(target,role).queue();
				ans="Added that role to the member.";
			}
		}
		return ans;
	}
	public String parseRemoveRole(Guild guild,User user,long channel,String...args)
	{
		String ans="Usage: <role> <member>";
		if(args.length>=2)
		{
			ans="You do not have permission to use this command!";
			Role role=guild.getRoleById(args[0].substring(3,args[0].length()-1));
			Member target=guild.getMemberById(args[1].substring(3,args[1].length()-1));
			Member author=user==null?null:guild.getMember(user);
			if(author==null||author.hasPermission(Permission.MANAGE_ROLES))
			{
				guild.removeRoleFromMember(target,role).queue();
				ans="Removed that role to the member.";
			}
		}
		return ans;
	}
	@SuppressWarnings("unchecked")
	public String parseVerifyName(Guild guild,User user,long channel,String...args)
	{
		if(args.length == 2)
		{
			String fname = args[0];
			String lname = args[1];
			try
			{
				FileReader reader=new FileReader(guild.getId()+"/names.dat");
				JSONParser parser=new JSONParser();
				var obj = (JSONObject)parser.parse(reader);
				var name = new JSONObject();
				name.put("First Name",fname);
				name.put("Last Name",lname);
				obj.put(user.getIdLong(),name);
				reader.close();
				FileWriter writer = new FileWriter(guild.getId()+"/names.dat");
				writer.append(obj.toJSONString());
				writer.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			return"Verified your name.";
		}
		else
		{
			return"Type in your first name and last name.";
		}
	}
	/**
	 * Parses work command on discord, currently only gives 100 dollars.
	 * @param guild The guild the command was sent from.
	 * @param author The user who sent the command.
	 * @param channel The ID of the channel that the command was sent from.
	 * @param args The list of arguments for this command.
	 * @return The string representation of the geometric mean of the arguments.
	 */
	@SuppressWarnings("rawtypes")
	public String parseWork(Guild guild,User user,long channel,String...args)
	{
		if(!this.economy.containsKey(guild.getIdLong()))
		{
			File file=new File(guild.getId());
			File ff=null;
			if(!file.exists())
			{
				file.mkdirs();
				try
				{
					ff=new File(file.getAbsolutePath()+"/roleinfo.dat");
					ff.createNewFile();
					ff=new File(file.getAbsolutePath()+"/ecogame.dat");
					ff.createNewFile();
				}
				catch(IOException e)
				{
					e.printStackTrace();
				}
			}
			this.economy.put(guild.getIdLong(),new LinkedHashMap<Long,LinkedHashMap>());
		}
		return"You worked for $100.";
	}
	@SuppressWarnings("unchecked")
	public String parseAutorole(Guild guild,User user,long channel,String...args)
	{
		String ans="First argument must be add or erase.";
		if(args.length==2)
		{
			if("add".equals(args[0]))
			{
				if(args[1].length()>4)
				{
					Role role=guild.getRoleById(args[1].substring(3,args[1].length()-1));
					if(role==null)
					{
						ans="Invalid role";
					}
					else
					{
						try
						{
							FileReader reader=new FileReader(guild.getId()+"/roleinfo.dat");
							JSONObject object=(JSONObject)new JSONParser().parse(reader);
							reader.close();
							JSONArray autoroles=null;
							if(object.containsKey("autoroles"))
							{
								autoroles = (JSONArray)object.get("autoroles");
							}
							else
							{
								autoroles = new JSONArray();
								object.put("autoroles",autoroles);
							}
							if(autoroles.contains(role.getIdLong()))
							{
								ans="It's already an autorole, use erase as first parameter to erase it.";
							}
							else
							{
								autoroles.add(role.getIdLong());
								FileWriter writer=new FileWriter(guild.getId()+"/roleinfo.dat");
								writer.append(object.toJSONString());
								writer.close();
								ans="Successfully added autorole.";
							}
						}
						catch(Exception e)
						{
							e.printStackTrace();
						}
					}
				}
				else
				{
					ans = "Invalid role.";
				}
			}
			else if("erase".equals(args[0]))
			{
				if(args[1].length()>4)
				{
					Role role=guild.getRoleById(args[1].substring(3,args[1].length()-1));
					if(role==null)
					{
						ans="Invalid role";
					}
					else
					{
						try
						{
							FileReader reader=new FileReader(guild.getId()+"/roleinfo.dat");
							JSONObject object=(JSONObject)new JSONParser().parse(reader);
							reader.close();
							JSONArray autoroles=null;
							if(object.containsKey("autoroles"))
							{
								autoroles = (JSONArray)object.get("autoroles");
							}
							else
							{
								autoroles = new JSONArray();
								object.put("autoroles",autoroles);
							}
							if(autoroles.contains(role.getIdLong()))
							{
								autoroles.remove(Long.valueOf(role.getIdLong()));
								FileWriter writer=new FileWriter(guild.getId()+"/roleinfo.dat");
								writer.append(object.toJSONString());
								writer.close();
								ans="Successfully erased autorole.";
							}
							else
							{
								ans="It's not an autorole, use add as first parameter to add it.";
							}
						}
						catch(Exception e)
						{
							e.printStackTrace();
						}
					}
				}
			}
		}
		else if(args.length==1&&"list".equals(args[0]))
		{
			try
			{
				FileReader reader=new FileReader(guild.getId()+"/roleinfo.dat");
				JSONObject object=(JSONObject)new JSONParser().parse(reader);
				reader.close();
				JSONArray autoroles=null;
				if(object.containsKey("autoroles"))
				{
					autoroles = (JSONArray)object.get("autoroles");
				}
				else
				{
					autoroles = new JSONArray();
					object.put("autoroles",autoroles);
				}
				ans="These are the current autoroles.";
				for(Object arole:autoroles)
				{
					ans+="\r\n"+guild.getRoleById((Long)arole).getName();
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			ans="Usage: autorole <add/erase> <@role>";
		}
		return ans;
	}
	@SuppressWarnings("unchecked")
	public String parseToggleSelfrole(Guild guild,User author,long channel,String...args)
	{
		String ans = "Mention a role.";
		if(args.length>=1)
		{
			var srole = args[0].replaceAll("[^0-9]","");
			var role = guild.getRoleById(srole);
			if(role!=null)
			{
				try
				{
					FileReader reader = new FileReader(guild.getId()+"/roleinfo.dat");
					JSONObject obj = (JSONObject)new JSONParser().parse(reader);
					reader.close();
					if(!obj.containsKey("selfroles"))
					{
						obj.put("selfroles",new JSONArray());
					}
					JSONArray arr = (JSONArray)obj.get("selfroles");
					if(arr.contains(role.getIdLong()))
					{
						arr.remove(role.getIdLong());
						ans = "Removed selfrole successfully.";
					}
					else
					{
						arr.add(role.getIdLong());
						ans = "Added selfrole successfully.";
					}
					PrintStream ps = new PrintStream(new FileOutputStream(guild.getId()+"/roleinfo.dat"));
					ps.print(obj.toJSONString());
					ps.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
					ans = e.toString();
				}
			}
			else
			{
				ans = "Role is not valid";
			}
		}
		return ans;
	}
	@SuppressWarnings("unchecked")
	public String parseToggleSelfroles(Guild guild,User author,long channel,String...args)
	{
		String ans = "Mention a role.";
		if(args.length>=1)
		{
			String srole = "";
			Role role = null;
			try
			{
				FileReader reader = new FileReader(guild.getId()+"/roleinfo.dat");
				JSONObject obj = (JSONObject)new JSONParser().parse(reader);
				reader.close();
				if(!obj.containsKey("selfroles"))
				{
					obj.put("selfroles",new JSONArray());
				}
				JSONArray arr = (JSONArray)obj.get("selfroles");
				for(int i=0;i<args.length;i++)
				{
					srole = args[i].replaceAll("[^0-9]","");
					role = guild.getRoleById(srole);
					if(role!=null)
					{
						if(arr.contains(role.getIdLong()))
						{
							arr.remove(role.getIdLong());
						}
						else
						{
							arr.add(role.getIdLong());
						}
					}
				}
				PrintStream ps = new PrintStream(new FileOutputStream(guild.getId()+"/roleinfo.dat"));
				ps.print(obj.toJSONString());
				ps.close();
				ans = "Toggled all selfroles successfully.";
			}
			catch(Exception e)
			{
				e.printStackTrace();
				ans = e.toString();
			}
		}
		return ans;
	}
	@SuppressWarnings("unchecked")
	public String parseSelfrole(Guild guild,User author,long channel,String...args)
	{
		var ans = "Mention a selfrole";
		if(args.length>=1)
		{
			var srole = args[0].replaceAll("[^0-9]","");
			var role = guild.getRoleById(srole);
			if(role!=null)
			{
				try
				{
					FileReader reader = new FileReader(guild.getId()+"/roleinfo.dat");
					JSONObject obj = (JSONObject)new JSONParser().parse(reader);
					reader.close();
					if(!obj.containsKey("selfroles"))
					{
						obj.put("selfroles",new JSONArray());
					}
					JSONArray arr = (JSONArray)obj.get("selfroles");
					if(arr.contains(role.getIdLong()))
					{
						if(guild.getMember(author).getRoles().contains(role))
						{
							guild.removeRoleFromMember(author.getIdLong(),role).queue();
							ans = "Taken the role "+role.getAsMention()+" from you.";
						}
						else
						{
							guild.addRoleToMember(author.getIdLong(),role).queue();
							ans = "Given you the role "+role.getAsMention()+".";
						}
					}
					else
					{
						ans = "That role is not a selfrole.";
					}
				}
				catch(Exception e)
				{
					e.printStackTrace();
					ans = e.toString();
				}
			}
		}
		return ans;
	}
	@SuppressWarnings("unchecked")
	public String parseListSelfroles(Guild guild,User author,long channel,String...args)
	{
		var ans = "This is the lis of selfroles.";
		try
		{
			FileReader reader = new FileReader(guild.getId()+"/roleinfo.dat");
			JSONObject obj = (JSONObject)new JSONParser().parse(reader);
			reader.close();
			if(!obj.containsKey("selfroles"))
			{
				obj.put("selfroles",new JSONArray());
			}
			JSONArray arr = (JSONArray)obj.get("selfroles");
			long RID = 0;
			for(Object ORID:arr)
			{
				RID = ((Long)ORID).longValue();
				ans += "\r\n" + guild.getRoleById(RID);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			ans = e.toString();
		}
		return ans;
	}
	public String parseSolveTriangle(Guild guild,User author,long channel,String...args)
	{
		var ans = "6 arguments are required.";
		if(args.length==6)
		{
			double[]arr=new double[6];
			for(int i=0;i<arr.length;i++)
			{
				arr[i]=Double.parseDouble(args[i]);
			}
			MathAlgs.solveTriangle(arr);
			ans = "";
			for(int i=0;i<arr.length;i++)
			{
				ans+=arr[i];
				if(i+1<arr.length)
				{
					ans += ", ";
				}
			}
		}
		return ans;
	}
	public String parseZProb(Guild guild,User author,long channel,String...args)
	{
		var ans="You need at least one argument";
		if(args.length>0)
		{
			double z=Double.parseDouble(args[0]);
			ans=Double.toString(Erf.erf(z / Math.sqrt(2))/2 + 0.5);
		}
		return ans;
	}
	public String parseProbZ(Guild guild,User author,long channel,String...args)
	{
		var ans="You need at least one argument";
		if(args.length>0)
		{
			double p=Double.parseDouble(args[0]);
			double z = 1, t = Erf.erf(z / Math.sqrt(2)) / 2 + 0.5 - p;
			double g=0;
			
			while(Math.abs(t) > 0.0000000000001)
			{
			    g = Math.exp(-z * z / 2) / Math.sqrt(8);
			    z -= t / g;
			    t = Erf.erf(z / Math.sqrt(2)) / 2 + 0.5 - p;
			}
			ans=Double.toString(z);
		}
		return ans;
	}
	public String parseChangeNickname(Guild guild,User author,long channel,String...args)
	{
		var ans="You need to pick a nickname and a member.";
		if(args.length>1)
		{
			var member=guild.getMemberById(args[0].replaceAll("[^0-9]",""));
			var user=guild.getMember(author);
			if(user.getRoles().get(0).compareTo(member.getRoles().get(0))>0)
			{
				member.modifyNickname(args[1]).queue();
				ans="Successfully changed the nickname.";
			}
			else
			{
				ans="Your highest role isn't high enough.";
			}
		}
		return ans;
	}
	public String parseSolveEquation(Guild guild,User author,long channel,String...args)
	{
		var ans="If the equations is ax + by = e and cx + dy = f, then give the parameters in order, a, b, c, d, e, f.";
		if(args.length==6)
		{
			BigDecimal a = new BigDecimal(args[0]), b = new BigDecimal(args[1]), c = new BigDecimal(args[2]), d = new BigDecimal(args[3]);
			BigDecimal e = new BigDecimal(args[4]), f = new BigDecimal(args[5]);
			BigDecimal[]answer=MathAlgs.solveLinearEquation(a, b, c, d, e, f);
			ans="x is "+answer[0]+", and y is "+answer[1];
		}
		return ans;
	}
	public String parseSetBanWords(Guild guild,User author,long channel,String...args)
	{
		var ans="Provide regexes.";
		if(args.length>0)
		{
			try
			{
				this.banwords=args;
				PrintStream out=new PrintStream(new FileOutputStream(guild.getId()+"/banwords.txt"));
				for(int i=0;i<args.length;i++)
				{
					out.println(args[i]);
				}
				out.close();
				ans="Successfully changed ban regexes.";
			}
			catch(FileNotFoundException e)
			{
				e.printStackTrace();
			}
		}
		return ans;
	}
	public String parseGetBanWords(Guild guild,User author,long channel,String...args)
	{
		var ans="";
		try
		{
			for(var regex:this.banwords)
			{
				ans+=regex+" ";
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			ans=e.toString();
		}
		return ans;
	}
	public String parseGetChangelog(Guild guild,User author,long channel,String...args)
	{
		var ans="";
		try
		{
			InputStream in=this.getClass().getResourceAsStream("/changelog.txt");
			byte[]bs=new byte[in.available()];
			in.read(bs);
			in.close();
			ans=new String(bs);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			ans+=e;
		}
		return ans;
	}
	/**
	 * Run method for the thread for parsing console commands.
	 */
	public void run1()
	{
		BufferedReader reader=new BufferedReader(new InputStreamReader(System.in));
		try
		{
			String s=reader.readLine();
			while(s!=null)
			{
				out.println(this.consoleCommandParser.parse(null,null,0,ScannerV0_2_1.getCmdArgs(s)));
				s="exit".equals(s)?null:reader.readLine();
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		System.exit(0);
	}
	/**
	 * Run method for thread for parsing discord commands.
	 */
	public void run2()
	{
		Message msg=null;
		boolean printed=false;
		String[]args=null;
		while(this.running)
		{
			if(this.msgs.isMessageNew())
			{
				msg=this.msgs.getLastMessage();
				printed=false;
			}
			if(this.channel!=null&&msg!=null)
			{
				if(!printed)
				{
					if(msg.getChannel().equals(this.channel))
					{
						out.printf("Server: %x (%s), Channel: %x (%s)"+System.getProperty("line.separator"),msg.getGuild().getIdLong(),msg.getGuild().getName(),msg.getChannel().getIdLong(),msg.getChannel().getName());
						out.printf("%x (%s AKA %s): %s"+System.getProperty("line.separator"),msg.getAuthor().getIdLong(),msg.getAuthor().getName(),msg.getGuild().getMember(msg.getAuthor()).getEffectiveName(),msg.getContentRaw());
					}
					args=ScannerV0_2_1.getCmdArgs(msg.getContentRaw());
					if(args.length>0)
					{
						if(args[0].equals("<@!"+Long.toString(ScannerV0_2_1.ID)+">"))
						{
							msg.getChannel().sendMessage(this.discordCommandParser.parse(msg.getGuild(),msg.getAuthor(),msg.getChannel().getIdLong(),Arrays.copyOfRange(args,1,args.length))).queue();
						}
						else if(args[0].length()>=2&&this.prefix.equals(args[0].substring(0,2)))
						{
							args[0]=args[0].substring(2);
							msg.getChannel().sendMessage(this.discordCommandParser.parse(msg.getGuild(),msg.getAuthor(),msg.getChannel().getIdLong(),args)).queue();
						}
						for(int i=0;i<this.banwords.length;i++)
						{
							if(Pattern.matches(this.banwords[i],msg.getContentRaw()))
							{
								try
								{
									msg.getGuild().ban(msg.getGuild().getMember(msg.getAuthor()), 0, "Using a forbidden word in the chat.").queue();
								}
								catch(HierarchyException e)
								{
									e.printStackTrace();
									msg.getChannel().sendMessage("User "+msg.getAuthor().getAsMention()+" said something that was not supposed to be said.").queue();
								}
							}
						}
					}
					printed=true;
				}
			}
			try
			{
				Thread.sleep(10);
			}
			catch(InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
	/**
	 * Start both threads.
	 */
	public synchronized void start()
	{
		this.thread1.start();
		this.thread2.start();
		this.running=true;
	}
	/**
	 * The main method that is first called during program execution.
	 * @param args The command line arguments for this program.
	 */
	public static final void main(String[]args)
	{
		try
		{
			new ScannerV0_2_1().start();
		}
		catch(LoginException e)
		{
			e.printStackTrace();
		}
	}
}