package cp.scanner.main;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.math.MathContext;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;
import java.util.TimeZone;
import javax.security.auth.login.LoginException;

import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import org.apache.commons.math3.special.Erf;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import cp.scanner.algo.MathAlgs;
import cp.scanner.cmd.CmdFunction;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
/**
 * Main class that contains the main method.
 * @author CubedProgrammer
 */
public class ScannerV_0_3 extends ListenerAdapter
{
	/**
	 * Token for bot authentication.
	 */
	public static final String TOKEN="MzY3NDI 	2MTc4MDE5MjI5Njk3.Wd0-Sw.F5OiDKEp1YiBMlf-GRO_J86d2ho";
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
	 * Discord epoch in milliseconds from Unix epoch
	 */
	public static final long EPOCH=1420070400000L;
	/**
	 * Files to create for storing data of each server
	 */
	public static final String files = "automuteroles.dat macros.txt roles.dat prefix.txt banwords.txt replies.txt";
	/**
	 * Open weather map application programming interface key
	 */
	public static final String owmapik = "ab76d12edd1bf3728192764d47b2a990";
	/**
	 * Path for open weather map
	 */
	public static final String owmrq = "data/2.5/weather?lat=42.20965&lon=-83.042463&appid=";
	/**
	 * Macro names
	 */
	public static final String[]MACROS = "E PI TAU SQRT2 SQRT3".split(" ");
	/**
	 * Macro definitions
	 */
	public static final String[]MDEFS = "2.7182818284590452353602874713527 3.1415926535897932384626433832795 6.283185307179586476925286766559 1.4142135623730950488016887242097 1.7320508075688772935274463415059".split(" ");
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
			if(cs[i]==ScannerV_0_3.BRACKET&&!escaped)
			{
				open=!open;
			}
			else if(cs[i]==ScannerV_0_3.DELIMITER&&!open)
			{
				argsal.add(builder.toString());
				builder.delete(0,builder.length());
			}
			else if(!escaped&&cs[i]==ScannerV_0_3.ESCAPE)
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
			n=n.divide(new BigDecimal(parts[i],MathContext.DECIMAL128),MathContext.DECIMAL128);
		}
		return n;
	}
	/**
	 * Finds a role from a string.
	 * @param guild The guild to search in.
	 * @param s Some string.
	 * @return The matching role.
	 */
	public static final Role findRole(Guild guild, String s)
	{
		String id = s.replaceAll("[^0-9]","");
		var role = "".equals(id) ? null : guild.getRoleById(Long.parseLong(id));
		if(role == null)
		{
			var roles = guild.getRoles();
			for(Role r:roles)
			{
				if(r.getName().equalsIgnoreCase(s))
					role = r;
			}
		}
		return role;
	}
	/**
	 * Finds a member from a string.
	 * @param guild The guild to search in.
	 * @param s Some string.
	 * @return The matching member.
	 */
	public static final Member findMember(Guild guild, String s)
	{
		String id = s.replaceAll("[^0-9]","");
		var member = "".equals(id) ? null : guild.getMemberById(Long.parseLong(id));
		if(member == null)
		{
			var members = guild.getMembers();
			for(Member m:members)
			{
				if(m.getEffectiveName().equalsIgnoreCase(s))
					member = m;
			}
		}
		if(member == null)
		{
			var members = guild.getMembers();
			for(Member m:members)
			{
				if(m.getUser().getName().equalsIgnoreCase(s))
					member = m;
			}
		}
		return member;
	}
	/**
	 * Checks if a char is alphanumeric.
	 * @param c Just a character.
	 * @return True if it is alphanumeric or an underscore.
	 */
	public static final boolean isAlphanumeric(char c)
	{
		return"0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrztuvwxyz_".contains(Character.toString(c));
	}
	private JDA jda;
	private User self;
	private long ID;
	private String mention;
	private CommandParser parser;
	private HashMap<Long, Role>autoroles;
	private HashMap<Long, Role>muteroles;
	private HashMap<Long, String>prefixes;
	private HashMap<Long, String>banwords;
	private HashMap<Long, HashMap<String, String>>macros;
	private HashMap<Long, HashMap<String, String>>replies;
	private boolean ready;
	public ScannerV_0_3() throws LoginException, InterruptedException
	{
		this.jda = JDABuilder.create(ScannerV_0_3.TOKEN,EnumSet.allOf(GatewayIntent.class)).build().awaitReady();
		this.jda.addEventListener(this);
		this.self = this.jda.getSelfUser();
		this.ID = this.self.getIdLong();
		this.mention = "<@!" + this.ID + ">";
		this.parser = new CommandParser();
		this.autoroles = new HashMap<Long, Role>();
		this.muteroles = new HashMap<Long, Role>();
		this.prefixes = new HashMap<Long, String>();
		this.banwords = new HashMap<Long, String>();
		this.macros = new HashMap<Long, HashMap<String, String>>();
		this.replies = new HashMap<Long, HashMap<String, String>>();
		this.parser.put("sum", "Adds numbers together.", this::parseSum).put("product", "Multiplies numbers together.", this::parseProduct);
		this.parser.put("average", "Computes arithmetic mean of a list.", this::parseArithmeticMean).put("gmean", "Computes geometric mean of a list.", this::parseGeometricMean);
		this.parser.put("addrole", "Adds a role to a member.", this::parseAddRole).put("rmrole", "Removes a role from a member.", this::parseRemoveRole);
		this.parser.put("kick", "Kicks a member.", this::parseKickMember).put("ban", "Bans a member.", this::parseBanMember);
		this.parser.put("nickname", "Changes the nickname of a member.", this::parseChangeNickname);
		this.parser.put("toggleselfrole", "Adds or removes a selfrole.", this::parseToggleSelfrole).put("toggleselfroles", "Adds or removes a list of selfroles.", this::parseToggleSelfroles).put("listselfroles", "Lists all the selfroles.", this::parseListSelfroles).put("selfrole", "Gives or removes a selfrole to or from you.", this::parseGetSelfrole);
		this.parser.put("autorole", "Sets the autorole for this server.", this::parseSetAutorole);
		this.parser.put("info", "Gets the information of an entity, or the server, by mentioning the entity.", this::parseGetInformation);
		this.parser.put("prefix", "Sets the prefix of the bot.", this::parseSetPrefix);
		this.parser.put("zprob", "Calculates the probablility of a z-score being less than a given z-score.", this::parseZProb).put("probz", "Calculates what z-score has a certain probability of having z-scores less than them.", this::parseProbZ);
		this.parser.put("get_mute_words", "Gets the words that could get you muted.", this::parseGetBanWords).put("set_mute_words", "Sets the words that could get you muted.", this::parseSetBanWords);
		this.parser.put("add_mute_words", "Adds words that could get you muted.", this::parseAddBanWords).put("remove_mute_words", "Removes words that could get you muted.", this::parseRemoveBanWords);
		this.parser.put("weather", "Gets the weather.", this::parseGetWeather).put("weatherraw", "Gets the raw weather data.", this::parseRawWeather);
		this.parser.put("define", "Defines a macro.", this::parseDefineMacro);
		this.parser.put("macros", "Gets all the macros.", this::parseGetMacros);
		this.parser.put("undef", "Undefines macros, put a letter in front of every macro you are undefining.", this::parseRemoveMacros);
		this.parser.put("set_mute_role", "Sets the server role for a muted member.", this::parseSetMuteRole);
		this.parser.put("solve_linear_equations", "The first argument is the number of unknowns, then comes the matrix entries.", this::parseSolveEquations);
		this.parser.put("sqrt", "Square roots a number, duh.", this::parseSquareRoot);
		this.parser.put("sin", "Gets the sine of a number.", this::parseSineTheta).put("cos", "Gets the cosine of a number.", this::parseCosineTheta);
		this.parser.put("asin", "Gets the inverse sine", this::parseInverseSine).put("acos", "Gets the inverse cosine", this::parseInverseCosine);
		this.parser.put("solvet", "Solves for a triangle given known sides and angles, represent unknown sides and angles with -1, give the three angles first.", this::parseSolveTriangle);
		this.parser.put("vector_polar_addition", "Adds 2D vectors in polar form.", this::parseAddVectors);
		this.parser.put("timestamp", "Get readable date from time since epoch in milliseconds.", this::parseGetTime);
		this.parser.put("add_reply_msg", "Adds messages the bot will reply to with a reply message.", this::parseAddReply);
		this.parser.put("remove_reply_msg", "Removes messages the bot will reply to.", this::parseRemoveReplies);
		//this.parser.put("pow", "Computes one number raised to the power of another.", this::parseComputePower);
		var guilds = this.jda.getGuilds();
		File f = null;
		File ff = null;
		String[]fn = ScannerV_0_3.files.split(" ");
		for(Guild guild:guilds)
		{
			this.prefixes.put(guild.getIdLong(), "--");
			f = new File(Long.toHexString(guild.getIdLong()));
			f.mkdir();
			for(String string:fn)
			{
				ff = new File(f.getAbsolutePath() + "/" + string);
				if(!ff.exists())
				{
					try
					{
						ff.createNewFile();
					}
					catch(IOException e)
					{
						e.printStackTrace();
					}
				}
			}
		}
		Scanner sc = null;
		FileInputStream fin=null;
		long r=0;
		byte[]rbs=null;
		for(int i=0;i<this.jda.getGuilds().size();i++)
		{
			try
			{
				this.macros.put(this.jda.getGuilds().get(i).getIdLong(),new HashMap<String, String>());
				this.replies.put(this.jda.getGuilds().get(i).getIdLong(),new HashMap<String, String>());
				for(int j=0;j<ScannerV_0_3.MACROS.length;j++)
				{
					this.macros.get(this.jda.getGuilds().get(i).getIdLong()).put(ScannerV_0_3.MACROS[j],ScannerV_0_3.MDEFS[j]);
				}
				sc = new Scanner(new FileInputStream(Long.toHexString(this.jda.getGuilds().get(i).getIdLong()) + "/prefix.txt"));
				if(sc.hasNext())
					this.prefixes.put(this.jda.getGuilds().get(i).getIdLong(), sc.next());
				sc.close();
				sc = new Scanner(new FileInputStream(Long.toHexString(this.jda.getGuilds().get(i).getIdLong()) + "/banwords.txt"));
				if(sc.hasNextLine())
					this.banwords.put(this.jda.getGuilds().get(i).getIdLong(), sc.nextLine());
				sc.close();
				sc = new Scanner(new FileInputStream(Long.toHexString(this.jda.getGuilds().get(i).getIdLong()) + "/macros.txt"));
				while(sc.hasNextLine())
					this.macros.get(this.jda.getGuilds().get(i).getIdLong()).put(sc.next(),sc.nextLine().strip());
				sc = new Scanner(new FileInputStream(Long.toHexString(this.jda.getGuilds().get(i).getIdLong()) + "/replies.txt"));
				while(sc.hasNextLine())
					this.replies.get(this.jda.getGuilds().get(i).getIdLong()).put(sc.nextLine().strip(),sc.nextLine().strip());
				fin=new FileInputStream(Long.toHexString(this.jda.getGuilds().get(i).getIdLong()) + "/automuteroles.dat");
				if(fin.available()==16)
				{
					rbs=new byte[16];
					fin.read(rbs);
					for(int j=0;j<8;j++)
						r+=(long)rbs[j]<<j*8;
					if(r!=-1)
						this.autoroles.put(this.jda.getGuilds().get(i).getIdLong(),this.jda.getGuilds().get(i).getRoleById(r));
					for(int j=0;j<8;j++)
						r+=(long)rbs[j+8]<<j*8;
					if(r!=-1)
						this.muteroles.put(this.jda.getGuilds().get(i).getIdLong(),this.jda.getGuilds().get(i).getRoleById(r));
				}
				fin.close();
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
		String[]ccn="message kick ban member role nickname ls".split("\\s+");
		String[]ccd="Sends a message to the current channel.____Kicks a member.____Bans a member.____Gets the information of a member.____Add a role to a member if the member doesn't have it, removes it otherwise.____Changes the nickname of a member.____Lists information about servers.".split("____");
		CmdFunction[]funcs={this::parseSendMessage,this::parseConsoleKick,this::parseConsoleBan,this::parseMemberInfo,this::parseToggleRole,this::parseConsoleNickname,this::parseConsoleList};
		ConsoleController gay=new ConsoleController(this.jda,ccn,ccd,funcs,this);
		Thread thread = new Thread(gay::run);
		thread.start();
		this.ready = true;
	}
	public void save()
	{
		PrintStream ps = null;
		Iterator<String>it = null;
		String macro = null;
		byte[]rbs = null;
		Role role = null;
		long r = 0;
		for(int i=0;i<this.jda.getGuilds().size();i++)
		{
			try
			{
				ps = new PrintStream(new FileOutputStream(Long.toHexString(this.jda.getGuilds().get(i).getIdLong()) + "/prefix.txt"));
				ps.print(this.prefixes.get(this.jda.getGuilds().get(i).getIdLong()));
				ps.close();
				ps = new PrintStream(new FileOutputStream(Long.toHexString(this.jda.getGuilds().get(i).getIdLong()) + "/banwords.txt"));
				ps.print(this.banwords.get(this.jda.getGuilds().get(i).getIdLong()));
				ps.close();
				ps = new PrintStream(new FileOutputStream(Long.toHexString(this.jda.getGuilds().get(i).getIdLong()) + "/macros.txt"));
				it = this.macros.get(this.jda.getGuilds().get(i).getIdLong()).keySet().iterator();
				while(it.hasNext())
				{
					macro = it.next();
					ps.println(macro + " " + this.macros.get(this.jda.getGuilds().get(i).getIdLong()).get(macro));
				}
				ps.close();
				ps = new PrintStream(new FileOutputStream(Long.toHexString(this.jda.getGuilds().get(i).getIdLong()) + "/replies.txt"));
				it = this.replies.get(this.jda.getGuilds().get(i).getIdLong()).keySet().iterator();
				while(it.hasNext())
				{
					macro = it.next();
					ps.println(macro + System.getProperty("line.separator") + this.replies.get(this.jda.getGuilds().get(i).getIdLong()).get(macro));
				}
				ps.close();
				ps = new PrintStream(new FileOutputStream(Long.toHexString(this.jda.getGuilds().get(i).getIdLong()) + "/automuteroles.dat"));
				rbs=new byte[16];
				role=this.autoroles.get(this.jda.getGuilds().get(i).getIdLong());
				r=role==null?-1:role.getIdLong();
				for(int j=0;j<8;j++)
					rbs[j]=(byte)(r>>j*8);
				role=this.muteroles.get(this.jda.getGuilds().get(i).getIdLong());
				r=role==null?-1:role.getIdLong();
				for(int j=0;j<8;j++)
					rbs[8+j]=(byte)(r>>j*8);
				ps.write(rbs);
				ps.close();
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	public void onMessageUpdate(MessageUpdateEvent evt)
	{
		if(this.ready)
		{
			if(this.jda.getGuilds().size()!=this.prefixes.size())
				System.out.println(this.jda.getGuilds().size()+" "+this.prefixes.size());
			if(evt.getAuthor().getIdLong()!=this.ID)
			{
				Message message = evt.getMessage();
				String raw = message.getContentRaw();
				if(evt.getGuild()!=null)
				{
					if(raw.startsWith(this.mention))
					{
						raw = raw.substring(this.mention.length()).strip();
					}
					else if(raw.startsWith(this.prefixes.get(evt.getGuild().getIdLong())))
					{
						raw = raw.substring(this.prefixes.get(evt.getGuild().getIdLong()).length()).strip();
					}
					else
					{
						if(this.muteroles.containsKey(evt.getGuild().getIdLong())&&this.banwords.containsKey(evt.getGuild().getIdLong()))
						{
							String[]banwords = ScannerV_0_3.getCmdArgs(this.banwords.get(evt.getGuild().getIdLong()).toLowerCase());
							String[]words=raw.toLowerCase().replaceAll("[^\\s\\w]","").split("\\s+");
							for(int i=0;i<banwords.length;i++)
							{
								for(int j=0;j<words.length;j++)
								{
									if(banwords[i].equals(words[j]))
									{
										evt.getGuild().addRoleToMember(evt.getMember(), this.muteroles.get(evt.getGuild().getIdLong())).queue();
										i = banwords.length;
										j = words.length;
									}
								}
							}
						}
						var it = this.replies.get(evt.getGuild().getIdLong()).keySet().iterator();
						String msg = null;
						boolean replied = false;
						while(it.hasNext()&&!replied)
						{
							msg = it.next();
							if(Normalizer.normalize(raw, Normalizer.Form.NFD).replaceAll("\\p{M}", "").toLowerCase().replaceAll("[^\\s\\w]","").contains(msg.toLowerCase()))
							{
								evt.getChannel().sendMessage(this.replies.get(evt.getGuild().getIdLong()).get(msg)).queue();
								replied = true;
							}
						}
						return;
					}
					Iterator<String>it=this.macros.get(evt.getGuild().getIdLong()).keySet().iterator();
					String macro = null;
					int index=0;
					while(it.hasNext())
					{
						macro = it.next();
						index = raw.indexOf(macro);
						while(index!=-1)
						{
							if((index==0||!ScannerV_0_3.isAlphanumeric(raw.charAt(index-1)))&&(index+macro.length()==raw.length()||!ScannerV_0_3.isAlphanumeric(raw.charAt(index+macro.length()))))
								raw=raw.substring(0,index)+this.macros.get(evt.getGuild().getIdLong()).get(macro)+raw.substring(index+macro.length());
							index = raw.indexOf(macro,index+macro.length());
						}
					}
					System.out.println(raw);
					String[]args = ScannerV_0_3.getCmdArgs(raw);
					if(args.length > 0)
					{
						String response = this.parser.parse(message,evt.getGuild(),evt.getChannel(),evt.getAuthor(),args);
						evt.getChannel().sendMessage(response).queue();
						System.out.println("Sent message to channel " + evt.getChannel().getId() + " in guild " + (evt.getGuild()==null?"null":evt.getGuild().getId()));
						System.out.println(response);
					}
				}
			}
		}
	}
	public void onMessageReceived(MessageReceivedEvent evt)
	{
		if(this.ready)
		{
			if(this.jda.getGuilds().size()!=this.prefixes.size())
				System.out.println(this.jda.getGuilds().size()+" "+this.prefixes.size());
			if(evt.getAuthor().getIdLong()!=this.ID)
			{
				Message message = evt.getMessage();
				String raw = message.getContentRaw();
				if(evt.getGuild()!=null)
				{
					if(raw.startsWith(this.mention))
					{
						raw = raw.substring(this.mention.length()).strip();
					}
					else if(raw.startsWith(this.prefixes.get(evt.getGuild().getIdLong())))
					{
						raw = raw.substring(this.prefixes.get(evt.getGuild().getIdLong()).length()).strip();
					}
					else
					{
						if(this.muteroles.containsKey(evt.getGuild().getIdLong())&&this.banwords.containsKey(evt.getGuild().getIdLong()))
						{
							String[]banwords = ScannerV_0_3.getCmdArgs(this.banwords.get(evt.getGuild().getIdLong()).toLowerCase());
							String[]words=raw.toLowerCase().replaceAll("[^\\s\\w]","").split("\\s+");
							for(int i=0;i<banwords.length;i++)
							{
								for(int j=0;j<words.length;j++)
								{
									if(banwords[i].equals(words[j]))
									{
										evt.getGuild().addRoleToMember(evt.getMember(), this.muteroles.get(evt.getGuild().getIdLong())).queue();
										i = banwords.length;
										j = words.length;
									}
								}
							}
						}
						var it = this.replies.get(evt.getGuild().getIdLong()).keySet().iterator();
						String msg = null;
						boolean replied = false;
						while(it.hasNext()&&!replied)
						{
							msg = it.next();
							if(Normalizer.normalize(raw, Normalizer.Form.NFD).replaceAll("\\p{M}", "").toLowerCase().replaceAll("[^\\s\\w]","").contains(msg.toLowerCase()))
							{
								evt.getChannel().sendMessage(this.replies.get(evt.getGuild().getIdLong()).get(msg)).queue();
								replied = true;
							}
						}
						return;
					}
					Iterator<String>it=this.macros.get(evt.getGuild().getIdLong()).keySet().iterator();
					String macro = null;
					int index=0;
					while(it.hasNext())
					{
						macro = it.next();
						index = raw.indexOf(macro);
						while(index!=-1)
						{
							if((index==0||!ScannerV_0_3.isAlphanumeric(raw.charAt(index-1)))&&(index+macro.length()==raw.length()||!ScannerV_0_3.isAlphanumeric(raw.charAt(index+macro.length()))))
								raw=raw.substring(0,index)+this.macros.get(evt.getGuild().getIdLong()).get(macro)+raw.substring(index+macro.length());
							index = raw.indexOf(macro,index+macro.length());
						}
					}
					System.out.println(raw);
					String[]args = ScannerV_0_3.getCmdArgs(raw);
					if(args.length > 0)
					{
						String response = this.parser.parse(message,evt.getGuild(),evt.getChannel(),evt.getAuthor(),args);
						evt.getChannel().sendMessage(response).queue();
						System.out.println("Sent message to channel " + evt.getChannel().getId() + " in guild " + (evt.getGuild()==null?"null":evt.getGuild().getId()));
						System.out.println(response);
					}
				}
			}
		}
	}
	public void onGuildMemberJoin(GuildMemberJoinEvent evt)
	{
		System.out.println(evt.getMember().getUser().getIdLong());
		System.out.println(evt.getGuild());
		if(this.autoroles.containsKey(evt.getGuild().getIdLong()))
		{
			evt.getGuild().addRoleToMember(evt.getMember(),this.autoroles.get(evt.getGuild().getIdLong())).queue();
		}
	}
	public String parseSum(Message message,Guild guild,MessageChannel channel,User author,String[]args)
	{
		BigDecimal sum = BigDecimal.ZERO;
		for(int i=0;i<args.length;i++)
			sum = sum.add(ScannerV_0_3.strToNum(args[i]));
		return sum.toString();
	}
	public String parseProduct(Message message,Guild guild,MessageChannel channel,User author,String[]args)
	{
		BigDecimal product = BigDecimal.ONE;
		for(int i=0;i<args.length;i++)
			product = product.multiply(ScannerV_0_3.strToNum(args[i]));
		return product.toString();
	}
	public String parseArithmeticMean(Message message,Guild guild,MessageChannel channel,User author,String[]args)
	{
		BigDecimal sum = BigDecimal.ZERO;
		for(int i=0;i<args.length;i++)
			sum = sum.add(ScannerV_0_3.strToNum(args[i]));
		return sum.divide(BigDecimal.valueOf(args.length), MathContext.DECIMAL128).toString();
	}
	public String parseGeometricMean(Message message,Guild guild,MessageChannel channel,User author,String[]args)
	{
		BigDecimal product = BigDecimal.ONE;
		for(int i=0;i<args.length;i++)
			product = product.multiply(ScannerV_0_3.strToNum(args[i]));
		return MathAlgs.yroot(product, args.length).toString();
	}
	public String parseGetEpoch(Message message,Guild guild,MessageChannel channel,User author,String[]args)
	{
		return Long.toString(ScannerV_0_3.EPOCH);
	}
	public String parseAddRole(Message message,Guild guild,MessageChannel channel,User author,String[]args)
	{
		var ans = "Name a member and a role stupid.";
		if(args.length == 2)
		{
			var member = ScannerV_0_3.findMember(guild,args[0]);
			var role = ScannerV_0_3.findRole(guild,args[1]);
			guild.addRoleToMember(member,role).queue();
			ans = "Added the role.";
		}
		return ans;
	}
	public String parseRemoveRole(Message message,Guild guild,MessageChannel channel,User author,String[]args)
	{
		var ans = "Name a member and a role stupid.";
		if(args.length == 2)
		{
			var member = ScannerV_0_3.findMember(guild,args[0]);
			var role = ScannerV_0_3.findRole(guild,args[1]);
			guild.removeRoleFromMember(member,role).queue();
			ans = "Removed the role.";
		}
		return ans;
	}
	public String parseKickMember(Message message,Guild guild,MessageChannel channel,User author,String[]args)
	{
		var ans = "Name a member to kick and a reason.";
		if(args.length == 2)
		{
			Member kicker = guild.getMember(author);
			if(kicker.hasPermission(Permission.KICK_MEMBERS))
			{
				var victim=ScannerV_0_3.findMember(guild,args[0]);
				boolean capable = kicker.getRoles().size() > 0 && (victim.getRoles().size() == 0 || kicker.getRoles().get(0).compareTo(victim.getRoles().get(0)) > 0);
				if(capable)
				{
					guild.kick(victim, args[1]).queue();
					ans = "Successfully kicked that member.";
				}
				else
					ans = "You cannot kick a member higher than you.";
			}
			else
			{
				ans = "You do not have permission to use this command.";
			}
		}
		return ans;
	}
	public String parseBanMember(Message message,Guild guild,MessageChannel channel,User author,String[]args)
	{
		var ans = "Name a member to ban and a reason.";
		if(args.length == 2)
		{
			Member banner = guild.getMember(author);
			if(banner.hasPermission(Permission.BAN_MEMBERS))
			{
				var victim=ScannerV_0_3.findMember(guild,args[0]);
				boolean capable = banner.getRoles().size() > 0 && (victim.getRoles().size() == 0 || banner.getRoles().get(0).compareTo(victim.getRoles().get(0)) > 0);
				if(capable)
				{
					guild.ban(victim, 0, args[1]).queue();
					ans = "Successfully banned that member.";
				}
				else
					ans = "You cannot ban a member higher than you.";
			}
			else
			{
				ans = "You do not have permission to use this command.";
			}
		}
		return ans;
	}
	public String parseChangeNickname(Message message,Guild guild,MessageChannel channel,User author,String[]args)
	{
		var ans = "Name a player and a name.";
		if(args.length == 2)
		{
			var victim = ScannerV_0_3.findMember(guild, args[0]);
			var aggressor = guild.getMember(author);
			boolean capable = aggressor.getRoles().size() > 0 && (victim.getRoles().size() == 0 || aggressor.getRoles().get(0).compareTo(victim.getRoles().get(0)) > 0);
			if(capable)
			{
				victim.modifyNickname(args[1]).queue();
				ans = "Successfully changed nickname to " + args[1] + ".";
			}
			else
				ans = "You can only do that to someone lower rank than you.";
		}
		return ans;
	}
	public String parseToggleSelfrole(Message message,Guild guild,MessageChannel channel,User author,String[]args)
	{
		var ans = "Name a role stupid.";
		if(args.length == 1)
		{
			Role role = ScannerV_0_3.findRole(guild,args[0]);
			if(role == null)
				ans = "Role does not exist you dummy.";
			else
			{
				try
				{
					FileInputStream in = new FileInputStream(Long.toHexString(guild.getIdLong())+"/roles.dat");
					byte[]bs = new byte[in.available()];
					in.read(bs);
					long id = 0;
					ArrayList<Long>rids = new ArrayList<Long>();
					for(int i=0;i<bs.length/8;i++)
					{
						id = 0;
						for(int j=0;j<8;j++)
						{
							id += ((long)bs[i*8+j] & 0xff) << j*8;
						}
						rids.add(id);
					}
					in.close();
					id = role.getIdLong();
					if(!rids.remove(id))
					{
						rids.add(id);
					}
					BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(Long.toHexString(guild.getIdLong())+"/roles.dat"));
					for(Long rid:rids)
					{
						id = rid.longValue();
						for(int i=0;i<8;i++)
						{
							out.write((int)(id >> i*8));
						}
					}
					out.close();
					ans = "Toggled selfrole.";
				}
				catch(IOException e)
				{
					e.printStackTrace();
				}
			}
		}
		return ans;
	}
	public String parseToggleSelfroles(Message message,Guild guild,MessageChannel channel,User author,String[]args)
	{
		String ans = null;
		if(args.length == 0)
			ans = "Name some roles you idiot.";
		else
		{
			Role role = null;
			try
			{
				FileInputStream in = new FileInputStream(Long.toHexString(guild.getIdLong())+"/roles.dat");
				byte[]bs = new byte[in.available()];
				in.read(bs);
				long id = 0;
				ArrayList<Long>rids = new ArrayList<Long>();
				int gay = 0;
				for(int i=0;i<bs.length/8;i++)
				{
					id = 0;
					for(int j=0;j<8;j++)
					{
						id += ((long)bs[i*8+j] & 0xff) << j*8;
					}
					rids.add(id);
				}
				in.close();
				for(int i=0;i<args.length;i++)
				{
					role = ScannerV_0_3.findRole(guild,args[i]);
					if(role == null)
						gay++;
					else
					{
						id = role.getIdLong();
						if(!rids.remove(id))
						{
							rids.add(id);
						}
					}
				}
				System.out.println(rids);
				BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(Long.toHexString(guild.getIdLong())+"/roles.dat"));
				for(Long rid:rids)
				{
					id = rid.longValue();
					System.out.println(Long.toHexString(id));
					for(int i=0;i<8;i++)
					{
						out.write((int)(id >> i*8));
					}
				}
				out.close();
				ans = gay == 0 ? "Successfully toggled all roles." : "There were " + gay +" roles not found.";
			}
			catch(IOException e)
			{
				e.printStackTrace();
				ans = e.toString();
			}
		}
		return ans;
	}
	public String parseListSelfroles(Message message,Guild guild,MessageChannel channel,User author,String[]args)
	{
		var ans = "This is the list of selfroles.";
		try
		{
			FileInputStream in = new FileInputStream(Long.toHexString(guild.getIdLong())+"/roles.dat");
			byte[]bs = new byte[in.available()];
			in.read(bs);
			long id = 0;
			ArrayList<Long>rids = new ArrayList<Long>();
			for(int i=0;i<bs.length/8;i++)
			{
				id = 0;
				for(int j=0;j<8;j++)
				{
					id += ((long)bs[i*8+j] & 0xff) << j*8;
				}
				rids.add(id);
			}
			in.close();
			for(var iterator=rids.iterator();iterator.hasNext();)
			{
				ans += System.getProperty("line.separator") + guild.getRoleById(iterator.next()).getName();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			ans = e.toString();
		}
		return ans;
	}
	public String parseGetSelfrole(Message message,Guild guild,MessageChannel channel,User author,String[]args)
	{
		String ans = null;
		Role role = ScannerV_0_3.findRole(guild, args[0]);
		if(role == null)
			ans = "The role doesn't exist you idiot.";
		else
		{
			try
			{
				FileInputStream in = new FileInputStream(Long.toHexString(guild.getIdLong())+"/roles.dat");
				byte[]bs = new byte[in.available()];
				in.read(bs);
				long id = 0;
				ArrayList<Long>rids = new ArrayList<Long>();
				for(int i=0;i<bs.length/8;i++)
				{
					id = 0;
					for(int j=0;j<8;j++)
					{
						id += ((long)bs[i*8+j] & 0xff) << j*8;
					}
					rids.add(id);
				}
				in.close();
				if(rids.contains(role.getIdLong()))
				{
					if(guild.getMember(author).getRoles().contains(role))
					{
						guild.removeRoleFromMember(author.getIdLong(),role).queue();
						ans = "Successfully removed role from you.";
					}
					else
					{
						guild.addRoleToMember(author.getIdLong(),role).queue();
						ans = "Successfully added role to you.";
					}
				}
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
		return ans;
	}
	public String parseSetAutorole(Message message,Guild guild,MessageChannel channel,User author,String[]args)
	{
		var ans = "Specify a role you idiot.";
		if(args.length == 1)
		{
			if("__clear__".equals(args[0]))
			{
				this.autoroles.remove(guild.getIdLong());
				ans = "Cleared autorole.";
			}
			else
			{
				Role role = ScannerV_0_3.findRole(guild, args[0]);
				if(role == null)
					ans = "Can't find the role.";
				else
				{
					this.autoroles.put(guild.getIdLong(), role);
					ans = "Set the autorole of this server";
				}
			}
		}
		return ans;
	}
	public String parseGetInformation(Message message,Guild guild,MessageChannel channel,User author,String[]args)
	{
		var ans = "Put the snowflake ID of the entity, or server.";
		if(args.length == 1)
		{
			if("server".equalsIgnoreCase(args[0]))
			{
				ans = "This server was created on " + guild.getTimeCreated().toString() + ", by " + guild.getOwner().getAsMention() + ", has snowflake ID of " + guild.getId();
				ans += ". This server has " + guild.getMemberCount() + " members, " + guild.getChannels().size() + " channels, and " + guild.getEmotes().size() + " emotes.";
				ans += "The default channel is " + guild.getDefaultChannel().getAsMention() + ", join message channel is " + guild.getSystemChannel().getAsMention() + ", and ";
				ans += guild.getIconUrl() + " is the server icon.";
			}
			else
			{
				long snowflake = Long.parseLong(args[0].replaceAll("\\D", ""));
				Date date = new Date((snowflake >> 22) + ScannerV_0_3.EPOCH);
				ans = "This entity was created on " + date.toString() + ".";
				if(guild.getMemberById(snowflake)!=null)
					ans += " This user joined the server on " + guild.getMemberById(snowflake).getTimeJoined().toString() + ".";
			}
		}
		return ans;
	}
	public String parseSetPrefix(Message message,Guild guild,MessageChannel channel,User author,String[]args)
	{
		var ans = "Give a prefix you idiot.";
		if(args.length == 1)
		{
			if(args[0].length()>3)
				ans = "Prefix must be no more than three characters long.";
			else
			{
				this.prefixes.put(guild.getIdLong(), args[0]);
				ans = "Successfully set the new prefix.";
			}
		}
		return ans;
	}
	public String parseZProb(Message message,Guild guild,MessageChannel channel,User author,String[]args)
	{
		var ans="You need at least one argument";
		if(args.length>0)
		{
			double z=Double.parseDouble(args[0]);
			ans=Double.toString(Erf.erf(z / Math.sqrt(2))/2 + 0.5);
		}
		return ans;
	}
	public String parseProbZ(Message message,Guild guild,MessageChannel channel,User author,String[]args)
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
	public String parseGetBanWords(Message message,Guild guild,MessageChannel channel,User author,String[]args)
	{
		return this.banwords.getOrDefault(guild.getIdLong(), "There are no mute words.");
	}
	public String parseSetBanWords(Message message,Guild guild,MessageChannel channel,User author,String[]args)
	{
		var ans = "You need the manage server permission to do this.";
		if(guild.getMember(author).hasPermission(Permission.MANAGE_SERVER))
		{
			String s = "";
			for(int i=0;i<args.length;i++)
			{
				s += "\"" + args[i] + "\" ";
			}
			this.banwords.put(guild.getIdLong(), s.stripTrailing());
			if(args.length==0)
				this.banwords.remove(guild.getIdLong());
			ans = "Set the new mute words.";
		}
		return ans;
	}
	public String parseAddBanWords(Message message,Guild guild,MessageChannel channel,User author,String[]args)
	{
		var ans = "You need the manage server permission to do this.";
		if(guild.getMember(author).hasPermission(Permission.MANAGE_SERVER))
		{
			String s = "";
			for(int i=0;i<args.length;i++)
			{
				s += "\"" + args[i] + "\" ";
			}
			this.banwords.put(guild.getIdLong(), s+this.banwords.getOrDefault(guild.getIdLong(), ""));
			if(this.banwords.get(guild.getIdLong()).length()==0)
				this.banwords.remove(guild.getIdLong());
			ans = "Added new mute words.";
		}
		return ans;
	}
	public String parseRemoveBanWords(Message message,Guild guild,MessageChannel channel,User author,String[]args)
	{
		var ans = "You need the manage server permission to do this.";
		if(guild.getMember(author).hasPermission(Permission.MANAGE_SERVER))
		{
			String s = this.banwords.get(guild.getIdLong());
			for(int i=0;i<args.length;i++)
			{
				s=s.replaceAll("\"" + args[i] + "\"","");
			}
			this.banwords.put(guild.getIdLong(), s.strip());
			if(this.banwords.get(guild.getIdLong()).length()==0)
				this.banwords.remove(guild.getIdLong());
			ans = "Removed the mute words.";
		}
		return ans;
	}
	public String parseComputePower(Message message,Guild guild,MessageChannel channel,User author,String[]args)
	{
		var ans = "You need one or two arguments.";
		if(args.length == 1 || args.length == 2)
		{
			BigDecimal base = args.length == 1 ? new BigDecimal("2.7182818284590452353602874713527") : ScannerV_0_3.strToNum(args[0]);
			BigDecimal exponent = args.length == 1 ? ScannerV_0_3.strToNum(args[0]) : ScannerV_0_3.strToNum(args[1]);
			ans = MathAlgs.pow(base, exponent).toString();
			System.out.println("faggot");
		}
		return ans;
	}
	public String parseGetWeather(Message message,Guild guild,MessageChannel channel,User author,String[]args)
	{
		var ans = "";
		try
		{
			HttpURLConnection connection = (HttpURLConnection)new URL("http://api.openweathermap.org/" + ScannerV_0_3.owmrq + ScannerV_0_3.owmapik).openConnection();
			JSONObject obj = (JSONObject)new JSONParser().parse(new InputStreamReader(connection.getInputStream()));
			JSONObject main = (JSONObject)obj.get("main");
			JSONObject wind = (JSONObject)obj.get("wind");
			JSONObject sys = (JSONObject)obj.get("sys");
			float temp = ((Double)main.get("temp")).floatValue() - 273.15f;
			float temps = ((Double)main.get("temp_min")).floatValue() - 273.15f;
			float tempb = ((Double)main.get("temp_max")).floatValue() - 273.15f;
			float pressure = ((Long)main.get("pressure")).floatValue() / 10;
			float humidity = ((Long)main.get("humidity")).floatValue() / 100;
			float speed = ((Double)wind.get("speed")).floatValue();
			int angle = ((Long)wind.get("deg")).intValue() - 180;
			String direction = "";
			if(angle == -180)
				direction = "north";
			else if(angle == 0)
				direction = "south";
			else if(angle == 90)
				direction = "west";
			else if(angle == -90)
				direction = "east";
			else if(angle > 0 && angle < 90)
				direction = "southwest";
			else if(angle > 90 && angle < 180)
				direction = "northwest";
			else if(angle > -90 && angle < 0)
				direction = "southeast";
			else if(angle > -180 && angle < -90)
				direction = "northeast";
			long sunrise = ((Long)sys.get("sunrise")).longValue();
			long sunset = ((Long)sys.get("sunset")).longValue();
			Calendar ca = Calendar.getInstance(TimeZone.getTimeZone("America/Toronto"));
			Calendar cb = Calendar.getInstance(TimeZone.getTimeZone("America/Toronto"));
			ca.setTimeInMillis(1000 * sunrise);
			cb.setTimeInMillis(1000 * sunset);
			ans = String.format("The temperature today will be around %.2f degrees, with a minimum of %.2f and maximum of %.2f, pressure is %.2f kPa, humidity is %.2f. Wind blows at speed of %.1f m/s, at angle %d, which is direction %s. Sunrise happens at %d:%d, sunset happens at %d:%d.", temp, temps, tempb, pressure, humidity, speed, angle, direction, ca.get(Calendar.HOUR), ca.get(Calendar.MINUTE), cb.get(Calendar.HOUR), cb.get(Calendar.MINUTE));
		}
		catch(Exception e)
		{
			e.printStackTrace();
			ans = e.toString();
		}
		return ans;
	}
	public String parseRawWeather(Message message,Guild guild,MessageChannel channel,User author,String[]args)
	{
		var ans = "";
		try
		{
			HttpURLConnection connection = (HttpURLConnection)new URL("http://api.openweathermap.org/" + ScannerV_0_3.owmrq + ScannerV_0_3.owmapik).openConnection();
			var in = connection.getInputStream();
			BufferedReader reader=new BufferedReader(new InputStreamReader(in));
			ans=reader.readLine();
			reader.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			ans = e.toString();
		}
		return ans;
	}
	public String parseDefineMacro(Message message,Guild guild,MessageChannel channel,User author,String[]args)
	{
		var ans = "You need to put two arguments, the macro name and macro definition.";
		if(args.length==2)
		{
			if(args[1].contains(args[0]))
				ans = "You may not define a recursive macro.";
			else
			{
				this.macros.get(guild.getIdLong()).put(args[0],args[1]);
				ans = "Successfully defined macro.";
			}
		}
		return ans;
	}
	public String parseGetMacros(Message message,Guild guild,MessageChannel channel,User author,String[]args)
	{
		return this.macros.get(guild.getIdLong()).toString();
	}
	public String parseRemoveMacros(Message message,Guild guild,MessageChannel channel,User author,String[]args)
	{
		var ans="";
		if(args.length==0)
			ans="Specify the name of the macros to remove.";
		else
		{
			for(int i=0;i<args.length;i++)
				this.macros.get(guild.getIdLong()).remove(args[i].substring(1));
			ans="Removed specified macros.";
		}
		return ans;
	}
	public String parseSetMuteRole(Message message,Guild guild,MessageChannel channel,User author,String[]args)
	{
		var ans="Name a role, stupid.";
		if(args.length==1)
		{
			this.muteroles.put(guild.getIdLong(),ScannerV_0_3.findRole(guild,args[0]));
			ans="Set the new mute role.";
		}
		return ans;
	}
	public String parseSolveEquations(Message message,Guild guild,MessageChannel channel,User author,String[]args)
	{
		var ans="You must give the number of unknowns as the first argument, then all the entries of the augmented matrix.";
		if(1<args.length)
		{
			int unknowns = Integer.parseInt(args[0]);
			BigDecimal[][]mat = new BigDecimal[unknowns][unknowns+1];
			for(int i=0;i<mat.length;i++)
			{
				for(int j=0;j<mat[i].length;j++)
				{
					mat[i][j]=new BigDecimal(args[1+i*mat.length+i+j]);
				}
			}
			MathAlgs.solveLinearEquation(mat);
			System.out.println(java.util.Arrays.deepToString(mat));
			ans = "This list is your solution.";
			for(int i=0;i<mat.length;i++)
			{
				ans+=System.getProperty("line.separator")+mat[i][mat.length];
			}
		}
		return ans;
	}
	public String parseSquareRoot(Message message,Guild guild,MessageChannel channel,User author,String[]args)
	{
		var ans="Give a non-negative number to square root.";
		if(args.length==1)
		{
			ans=ScannerV_0_3.strToNum(args[0]).sqrt(MathContext.DECIMAL128).toString();
		}
		return ans;
	}
	public String parseSineTheta(Message message,Guild guild,MessageChannel channel,User author,String[]args)
	{
		var ans="Give an angle.";
		if(args.length==1)
		{
			ans=MathAlgs.sin(ScannerV_0_3.strToNum(args[0]).multiply(MathAlgs.PI_BY_180,MathContext.DECIMAL128)).toString();
		}
		return ans;
	}
	public String parseCosineTheta(Message message,Guild guild,MessageChannel channel,User author,String[]args)
	{
		var ans="Give an angle.";
		if(args.length==1)
		{
			ans=MathAlgs.cos(ScannerV_0_3.strToNum(args[0]).multiply(MathAlgs.PI_BY_180,MathContext.DECIMAL128)).toString();
		}
		return ans;
	}
	public String parseInverseSine(Message message,Guild guild,MessageChannel channel,User author,String[]args)
	{
		var ans="Give an number.";
		if(args.length==1)
		{
			ans=MathAlgs.asin(ScannerV_0_3.strToNum(args[0])).divide(MathAlgs.PI_BY_180,MathContext.DECIMAL128).toString();
		}
		return ans;
	}
	public String parseInverseCosine(Message message,Guild guild,MessageChannel channel,User author,String[]args)
	{
		var ans="Give an number.";
		if(args.length==1)
		{
			ans=MathAlgs.acos(ScannerV_0_3.strToNum(args[0])).divide(MathAlgs.PI_BY_180,MathContext.DECIMAL128).toString();
		}
		return ans;
	}
	public String parseSolveTriangle(Message message,Guild guild,MessageChannel channel,User author,String[]args)
	{
		var ans="Give six numbers, the three angles and their corresponding sides.";
		if(args.length==6)
		{
			BigDecimal[]dat=new BigDecimal[args.length];
			for(int i=0;i<dat.length;i++)
			{
				dat[i]=new BigDecimal(args[i]);
			}
			MathAlgs.solvet(dat);
			ans="";
			for(int i=0;i+1<dat.length;i++)
				ans+=dat[i].toString()+", ";
			ans+=dat[5];
		}
		return ans;
	}
	public String parseAddVectors(Message message,Guild guild,MessageChannel channel,User author,String[]args)
	{
		var ans="Give an even number of numbers, in pairs of angle and magnitude.";
		if(args.length%2==0)
		{
			BigDecimal[]angles=new BigDecimal[args.length/2];
			BigDecimal[]mags=new BigDecimal[args.length/2];
			for(int i=0;i<angles.length;i++)
			{
				angles[i]=new BigDecimal(args[2*i]).multiply(MathAlgs.PI_BY_180);
				mags[i]=new BigDecimal(args[2*i+1]);
			}
			BigDecimal[]result=new BigDecimal[2];
			MathAlgs.vectorPolarAddition(angles,mags,result);
			ans=result[0].divide(MathAlgs.PI_BY_180,MathContext.DECIMAL128)+"�, "+result[1];
		}
		return ans;
	}
	public String parseAddReply(Message message,Guild guild,MessageChannel channel,User author,String[]args)
	{
		var ans="Give a list of messages, and then the reply.";
		if(args.length>1)
		{
			for(int i=0;i<args.length-1;i++)
				this.replies.get(guild.getIdLong()).put(args[i], args[args.length-1]);
			ans = "Added new replies.";
		}
		return ans;
	}
	public String parseRemoveReplies(Message message,Guild guild,MessageChannel channel,User author,String[]args)
	{
		var ans="Give a list of messages that would be replied to to remove.";
		if(args.length>0)
		{
			for(int i=0;i<args.length;i++)
				this.replies.get(guild.getIdLong()).remove(args[i]);
			ans = "Removed replies.";
		}
		return ans;
	}
	public String parseGetTime(Message message,Guild guild,MessageChannel channel,User author,String[]args)
	{
		Calendar c = Calendar.getInstance(TimeZone.getTimeZone("America/Toronto"));
		String ans = c.getTime().toString();
		if(args.length>0)
		{
			ans = "";
			for(int i=0;i<args.length;i++)
			{
				c.setTimeInMillis(Long.parseLong(args[i]));
				ans += c.getTime() + System.getProperty("line.separator");
			}
		}
		return ans;
	}
	public String parseSendMessage(Message message,Guild guild,MessageChannel channel,User author,String[]args)
	{
		String s="";
		for(int i=0;i<args.length;i++)
		{
			s+=args[i]+" ";
		}
		channel.sendMessage(s.substring(0,s.length()-1)).queue();
		return"Successfully sent message to channel "+channel.getName()+" in guild "+guild.getName();
	}
	public String parseConsoleKick(Message message,Guild guild,MessageChannel channel,User author,String[]args)
	{
		var ans="Name a member to be kicked.";
		if(args.length==1)
		{
			guild.kick(ScannerV_0_3.findMember(guild,args[0]),"Kicked from console.").queue();
			ans="Successfully kicked member from "+guild.getName();
		}
		return ans;
	}
	public String parseConsoleBan(Message message,Guild guild,MessageChannel channel,User author,String[]args)
	{
		var ans="Name a member to be banned.";
		if(args.length==1)
		{
			guild.ban(ScannerV_0_3.findMember(guild,args[0]),0,"Banned from console.").queue();
			ans="Successfully banned member from "+guild.getName();
		}
		return ans;
	}
	public String parseMemberInfo(Message message,Guild guild,MessageChannel channel,User author,String[]args)
	{
		var ans="Name a member.";
		if(args.length==1)
		{
			var mem=ScannerV_0_3.findMember(guild,args[0]);
			ans=mem.getId()+System.getProperty("line.separator")+mem.getRoles()+mem.getEffectiveName()+System.getProperty("line.separator")+mem.getUser().getName()+System.getProperty("line.separator")+mem.getTimeJoined();
		}
		return ans;
	}
	public String parseToggleRole(Message message,Guild guild,MessageChannel channel,User author,String[]args)
	{
		var ans="Name a member and a role please.";
		if(args.length==2)
		{
			var mem=ScannerV_0_3.findMember(guild,args[0]);
			var role=ScannerV_0_3.findRole(guild,args[1]);
			if(mem==null||role==null)
			{
				ans="Please name something that exists.";
			}
			else
			{
				if(!mem.getRoles().contains(role))
				{
					guild.addRoleToMember(mem,role).queue();
					ans="Successfully added that role to the member.";
				}
				else
				{
					guild.removeRoleFromMember(mem,role).queue();
					ans="Successefully removed that role from the member.";
				}
			}
		}
		return ans;
	}
	public String parseConsoleNickname(Message message,Guild guild,MessageChannel channel,User author,String[]args)
	{
		var ans="Name a member and a nickname.";
		if(args.length==2)
		{
			ScannerV_0_3.findMember(guild,args[0]).modifyNickname(args[1]).queue();
			ans="Successfully changed nickname to "+args[1];
		}
		return ans;
	}
	public String parseConsoleList(Message message,Guild guild,MessageChannel channel,User author,String[]args)
	{
		var ans="Argument must be either server, channel, members, or roles.";
		if(args.length==1)
		{
			if("server".equals(args[0]))
			{
				ans = "This is the list of servers";
				var guilds = this.jda.getGuilds();
				var it = guilds.iterator();
				Guild g = null;
				while(it.hasNext())
				{
					g = it.next();
					ans += System.getProperty("line.separator") + String.format("%d is the ID, %s is the name, %s is the creator name, and %d is the creator ID.", g.getIdLong(), g.getName(), g.getOwner().getUser().getName(), g.getOwner().getIdLong());
				}
			}
			else if("channel".equals(args[0]))
			{
				ans = "This is the list of roles";
				var guilds = this.jda.getGuilds();
				var it = guilds.iterator();
				Guild g = null;
				Iterator<GuildChannel>rit = null;
				GuildChannel r = null;
				while(it.hasNext())
				{
					g = it.next();
					ans += System.getProperty("line.separator") + String.format("%s (%s)", g.getId(), g.getName());
					rit = g.getChannels().iterator();
					while(rit.hasNext())
					{
						r = rit.next();
						ans += System.getProperty("line.separator") + String.format("    %s, %s", r.getId(), r.getName());
					}
				}
			}
			else if("members".equals(args[0]))
			{
				ans = "This is the list of members";
				var guilds = this.jda.getGuilds();
				var it = guilds.iterator();
				Guild g = null;
				Iterator<Member>rit = null;
				Member r = null;
				while(it.hasNext())
				{
					g = it.next();
					ans += System.getProperty("line.separator") + String.format("%s (%s)", g.getId(), g.getName());
					rit = g.getMembers().iterator();
					while(rit.hasNext())
					{
						r = rit.next();
						ans += System.getProperty("line.separator") + String.format("    %s, %s, %s#%s", r.getId(), r.getEffectiveName(), r.getUser().getName(), r.getUser().getDiscriminator());
					}
				}
			}
			else if("roles".equals(args[0]))
			{
				ans = "This is the list of roles";
				var guilds = this.jda.getGuilds();
				var it = guilds.iterator();
				Guild g = null;
				Iterator<Role>rit = null;
				Role r = null;
				while(it.hasNext())
				{
					g = it.next();
					ans += System.getProperty("line.separator") + String.format("%s (%s)", g.getId(), g.getName());
					rit = g.getRoles().iterator();
					while(rit.hasNext())
					{
						r = rit.next();
						ans += System.getProperty("line.separator") + String.format("    %s, %s, colour is 0x%x, permission is 0x%x", r.getId(), r.getName(), r.getColorRaw(), r.getPermissionsRaw());
					}
				}
			}
		}
		return ans;
	}
	public static void main(String[] args)
	{
		try
		{
			if(args.length==1)
				System.setOut(new PrintStream(new FileOutputStream(args[0])));
			new ScannerV_0_3();
			System.out.println("done");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}