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
import java.math.BigInteger;
import java.math.MathContext;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Base64;
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
import cp.scanner.algo.StringAlgs;
import cp.scanner.cmd.CmdFunction;
import cp.scanner.scoreboard.Scoreboard;
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
	public static final String TOKEN="Njg2MjUxMjQ5NzkxNzk1MjE0.XmUfRw.7TRUj1vY2y7sfa89u0LI7l17unI";
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
	public static final String files = "automuteroles.dat macros.txt roles.dat prefix.txt banwords.txt replies.txt whitelist.txt kills.txt";
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
			else if(cs[i]==ScannerV_0_3.DELIMITER&&!open&&builder.length()>0)
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
	private HashMap<Long, HashMap<String,ArrayList<Long>>>replyWhiteList;
	private HashMap<Long, ArrayList<Long>>playerPool;
	private HashMap<Long, HashMap<Long, ArrayList<Long>>>teams;
	private HashMap<Long,ArrayList<Scoreboard>>scoreboards;
	private HashMap<Long,ArrayList<String>>killmsgs;
	private SecureRandom dice;
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
		this.replyWhiteList = new HashMap<>();
		this.playerPool = new HashMap<>();
		this.teams = new HashMap<>();
		this.scoreboards = new HashMap<>();
		this.killmsgs = new HashMap<>();
		this.dice = new SecureRandom();
		this.parser.put("sum", "Adds numbers together.", this::parseSum).put("product", "Multiplies numbers together.", this::parseProduct);
		this.parser.put("quotient", "Calculates a number divided by another, or the reciprocal of a number.", this::parseQuotient).put("remainder", "Calculates the remainder of a number divided by another.", this::parseRemainder);
		this.parser.put("modexp", "Three numbers, the first to the second mod the third.", this::parseModExp).put("modinv", "Gets the modular inverse of the first number mod the second.", this::parseModInv);
		this.parser.put("gcd", "Get the greatest common divisor of two numbers.", this::parseGCD);
		this.parser.put("prime", "Checks if numbers are prime.", this::parseCheckPrime);
		this.parser.put("baseconv", "Convert from one base to another, parameters are from, to, numbers... in base from.", this::parseConvertBase);
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
		this.parser.put("set_mute_role", "Sets the server role for a muted member.", this::parseSetMuteRole).put("get_mute_role", "Get the mute role.", this::parseGetMuteRole);
		this.parser.put("solve_linear_equations", "The first argument is the number of unknowns, then comes the matrix entries.", this::parseSolveEquations);
		this.parser.put("sqrt", "Square roots a number, duh.", this::parseSquareRoot);
		this.parser.put("sin", "Gets the sine of a number.", this::parseSineTheta).put("cos", "Gets the cosine of a number.", this::parseCosineTheta);
		this.parser.put("tan", "Gets the tangent of a number.", this::parseTangentTheta).put("cot", "Gets the cotangent of a number.", this::parseCotangentTheta);
		this.parser.put("sec", "Gets the secant of a number.", this::parseSecantTheta).put("csc", "Gets the cosecant of a number.", this::parseCosecantTheta);
		this.parser.put("asin", "Gets the inverse sine", this::parseInverseSine).put("acos", "Gets the inverse cosine", this::parseInverseCosine);
		this.parser.put("solvet", "Solves for a triangle given known sides and angles, represent unknown sides and angles with -1, give the three angles first.", this::parseSolveTriangle);
		this.parser.put("vector_polar_addition", "Adds 2D vectors in polar form.", this::parseAddVectors);
		this.parser.put("timestamp", "Get readable date from time since epoch in milliseconds.", this::parseGetTime);
		this.parser.put("add_reply_msg", "Adds messages the bot will reply to with a reply message.", this::parseAddReply);
		this.parser.put("remove_reply_msg", "Removes messages the bot will reply to.", this::parseRemoveReplies);
		this.parser.put("replies", "Gets all messages and corresponding replies.", this::parseGetReplies);
		this.parser.put("reply_white_list", "Whitelists members from a reply, this bot will no longer reply to whitelisted users who say a certain message.", this::parseWhiteList);
		this.parser.put("remove_white_list", "Un-whitelists a member from a reply.", this::parseRemoveWhiteList);
		this.parser.put("pow", "Computes one number raised to the power of another.", this::parseComputePower).put("log", "Logarithm of a number.", this::parseComputeLog);
		this.parser.put("factor", "Factors numbers.", this::parsePrimeFactor);
		this.parser.put("encode_mime_64", "Encodes a string in base 64.", this::parseEncodeBase64).put("decode_mime_64", "Decodes a string in base 64.", this::parseDecodeBase64);
		this.parser.put("role_list_members", "Lists all members with a certain role.", this::parseListMembersWithRole).put("role_list_without", "List members without a certain role.", this::parseListMembersWithoutRole);
		this.parser.put("set_team_captains", "Sets the team captains for team drafting.", this::parseSetCaptains).put("players", "Set the players who will be drafted.", this::parseSetPlayers);
		this.parser.put("draft", "Drafts one player for your team.", this::parsePickTeammate).put("display_players_left", "Displays players that haven't been picked.", this::parseDisplayPlayers);
		this.parser.put("anagrams", "Gets all permutations of a string.", this::parseGetPermutations);
		this.parser.put("request_server_data", "Request the settings files for this server.", this::parseRequestData);
		this.parser.put("rand", "Gets a certain number of random numbers between an upper bound and lower bound, omit count for one number, omit lower bound for zero.", this::parseGetRandom);
		this.parser.put("emoji", "Get picture of emoji.", this::parseGetEmoji);
		this.parser.put("get_reply_whitelist", "Get the whitelisted members from replies.", this::parseGetWhitelist);
		this.parser.put("report", "Report a bug.", this::parseReportBug);
		this.parser.put("arithmancy", "Calculate your character, heart, and social number.", this::parseArithmancyCalculator);
		this.parser.put("scoreboard", "Server scoreboard actions.", this::parseScoreboardActions);
		this.parser.put("send_message_later", "Send a message at a specified time later.", this::parseMessageLater);
		this.parser.put("add_kill_msg", "Add a kill message, use \\\\t as placeholder for member name.", this::parseAddKillMsg).put("rm_kill_msg", "Removes a death message by its index.", this::parseRemoveKillMsg);
		this.parser.put("get_kill_msgs", "Get all kill messages.", this::parseGetKillMsgs).put("kill", "Sends a death message about a member.", this::parseSendKillMsg);
		this.parser.put("strip_non_digits", "Filters out all non-digit characters.", this::parseStripNonDigits);
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
		String[]ccn="message kick ban member role nickname ls save load".split("\\s+");
		String[]ccd="Sends a message to the current channel.____Kicks a member.____Bans a member.____Gets the information of a member.____Add a role to a member if the member doesn't have it, removes it otherwise.____Changes the nickname of a member.____Lists information about servers.____Saves information to hard drive.____Loads information from hard drive.".split("____");
		CmdFunction[]funcs={this::parseSendMessage,this::parseConsoleKick,this::parseConsoleBan,this::parseMemberInfo,this::parseToggleRole,this::parseConsoleNickname,this::parseConsoleList,this::parseConsoleSave,this::parseConsoleLoad};
		ConsoleController gay=new ConsoleController(this.jda,ccn,ccd,funcs,this);
		Thread thread = new Thread(gay::run);
		thread.start();
		this.ready = true;
		System.loadLibrary("arithmancy");
	}
	public void load()
	{
		Scanner sc = null;
		FileInputStream fin=null;
		long r=0;
		byte[]rbs=null;
		String[]tokens=null;
		for(int i=0;i<this.jda.getGuilds().size();i++)
		{
			try
			{
				this.macros.put(this.jda.getGuilds().get(i).getIdLong(),new HashMap<String, String>());
				this.replies.put(this.jda.getGuilds().get(i).getIdLong(),new HashMap<String, String>());
				this.replyWhiteList.put(this.jda.getGuilds().get(i).getIdLong(),new HashMap<String, ArrayList<Long>>());
				this.scoreboards.put(this.jda.getGuilds().get(i).getIdLong(),new ArrayList<>());
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
				sc.close();
				sc = new Scanner(new FileInputStream(Long.toHexString(this.jda.getGuilds().get(i).getIdLong()) + "/replies.txt"));
				while(sc.hasNextLine())
					this.replies.get(this.jda.getGuilds().get(i).getIdLong()).put(sc.nextLine().strip(),sc.nextLine().strip());
				sc.close();
				sc = new Scanner(new FileInputStream(Long.toHexString(this.jda.getGuilds().get(i).getIdLong()) + "/whitelist.txt"));
				while(sc.hasNextLine())
				{
					tokens = sc.nextLine().split("____next____");
					this.replyWhiteList.get(this.jda.getGuilds().get(i).getIdLong()).put(tokens[0], new ArrayList<>());
					for(int j=1;j<tokens.length;j++)
						this.replyWhiteList.get(this.jda.getGuilds().get(i).getIdLong()).get(tokens[0]).add(Long.parseLong(tokens[j]));;
				}
				sc.close();
				System.out.println(this.replyWhiteList);
				this.killmsgs.put(this.jda.getGuilds().get(i).getIdLong(),new ArrayList<>());
				sc = new Scanner(new FileInputStream(Long.toHexString(this.jda.getGuilds().get(i).getIdLong()) + "/kills.txt"));
				while(sc.hasNextLine())
					this.killmsgs.get(this.jda.getGuilds().get(i).getIdLong()).add(sc.nextLine());
				sc.close();
				fin=new FileInputStream(Long.toHexString(this.jda.getGuilds().get(i).getIdLong()) + "/automuteroles.dat");
				if(fin.available()==16)
				{
					rbs=new byte[16];
					fin.read(rbs);
					for(int j=0;j<8;j++)
						r+=((long)rbs[j]&255)<<j*8;
					if(r!=-1)
						this.autoroles.put(this.jda.getGuilds().get(i).getIdLong(),this.jda.getGuilds().get(i).getRoleById(r));
					r=0;
					for(int j=0;j<8;j++)
						r+=((long)rbs[j+8]&255)<<j*8;
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
				ps = new PrintStream(new FileOutputStream(Long.toHexString(this.jda.getGuilds().get(i).getIdLong()) + "/whitelist.txt"));
				for(String reply:this.replyWhiteList.get(this.jda.getGuilds().get(i).getIdLong()).keySet())
				{
					ps.print(reply);
					for(int j=0;j<this.replyWhiteList.get(this.jda.getGuilds().get(i).getIdLong()).get(reply).size();j++)
						ps.print("____next____" + this.replyWhiteList.get(this.jda.getGuilds().get(i).getIdLong()).get(reply).get(j));
					ps.println();
				}
				ps.close();
				ps = new PrintStream(new FileOutputStream(Long.toHexString(this.jda.getGuilds().get(i).getIdLong()) + "/kills.txt"));
				for(int j=0;j<this.killmsgs.get(this.jda.getGuilds().get(i).getIdLong()).size();j++)
					ps.println(this.killmsgs.get(this.jda.getGuilds().get(i).getIdLong()).get(j));
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
	public void mutePeople(String raw,Guild guild,Member author)
	{
		if(this.muteroles.containsKey(guild.getIdLong())&&this.banwords.containsKey(guild.getIdLong()))
		{
			String[]banwords = ScannerV_0_3.getCmdArgs(this.banwords.get(guild.getIdLong()).toLowerCase());
			String[]words=raw.toLowerCase().replaceAll("[^\\s\\w]","").split("\\s+");
			for(int i=0;i<banwords.length;i++)
			{
				for(int j=0;j<words.length;j++)
				{
					if(banwords[i].equals(words[j]))
					{
						guild.addRoleToMember(author, this.muteroles.get(guild.getIdLong())).queue();
						i = banwords.length;
						j = words.length;
					}
				}
			}
		}
	}
	public void reply(String raw,Guild guild,MessageChannel channel,Member member)
	{
		var it = this.replies.get(guild.getIdLong()).keySet().iterator();
		String msg = null;
		boolean replied = false;
		while(it.hasNext()&&!replied)
		{
			msg = it.next();
			if(Normalizer.normalize(raw, Normalizer.Form.NFD).replaceAll("\\p{M}", "").toLowerCase().replaceAll("[^\\s\\w]","").contains(msg.toLowerCase()))
			{
				if(!this.replyWhiteList.get(guild.getIdLong()).containsKey(msg)||!this.replyWhiteList.get(guild.getIdLong()).get(msg).contains(member.getIdLong()))
				{
					channel.sendMessage(this.replies.get(guild.getIdLong()).get(msg)).queue();
					replied = true;
				}
			}
		}
	}
	public void onMessageUpdate(MessageUpdateEvent evt)
	{
		if(this.ready)
		{
			if(evt.getAuthor().getIdLong()!=this.ID)
			{
				Message message = evt.getMessage();
				String raw = message.getContentRaw();
				if(evt.getGuild()!=null)
				{
					this.mutePeople(raw,evt.getGuild(),evt.getMember());
					this.reply(raw,evt.getGuild(),evt.getChannel(),evt.getMember());
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
						this.mutePeople(raw,evt.getGuild(),evt.getMember());
						this.reply(raw,evt.getGuild(),evt.getChannel(),evt.getMember());
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
						String response = "";
						if("echo".equals(args[0]))
							response = raw.substring(4).strip();
						else
							response = this.parser.parse(message,evt.getGuild(),evt.getChannel(),evt.getAuthor(),args);
						if(response.length()<2000)
							evt.getChannel().sendMessage(response).queue();
						else
							evt.getChannel().sendFile(response.getBytes(), "message.txt").queue();
						System.out.println("Sent message to channel " + evt.getChannel().getId() + " in guild " + (evt.getGuild()==null?"null":evt.getGuild().getId()));
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
	public String parseQuotient(Message message,Guild guild,MessageChannel channel,User author,String[]args)
	{
		String ans = "You need one or two arguments.";
		if(args.length==1)
			ans = BigDecimal.ONE.divide(ScannerV_0_3.strToNum(args[0]),MathContext.DECIMAL128).toString();
		else if(args.length==2)
			ans = ScannerV_0_3.strToNum(args[0]).divide(ScannerV_0_3.strToNum(args[1]),MathContext.DECIMAL128).toString();
		return ans;
	}
	public String parseRemainder(Message message,Guild guild,MessageChannel channel,User author,String[]args)
	{
		String ans = "You need two arguments.";
		if(args.length==2)
			ans = ScannerV_0_3.strToNum(args[0]).remainder(ScannerV_0_3.strToNum(args[1]),MathContext.DECIMAL128).toString();
		return ans;
	}
	public String parseModInv(Message message,Guild guild,MessageChannel channel,User author,String[]args)
	{
		String ans = "You need two arguments.";
		if(args.length==2)
			ans = new BigInteger(args[0]).modInverse(new BigInteger(args[1])).toString();
		return ans;
	}
	public String parseModExp(Message message,Guild guild,MessageChannel channel,User author,String[]args)
	{
		String ans = "You need three arguments.";
		if(args.length==3)
			ans = new BigInteger(args[0]).modPow(new BigInteger(args[1]), new BigInteger(args[2])).toString();
		return ans;
	}
	public String parseGCD(Message message,Guild guild,MessageChannel channel,User author,String[]args)
	{
		String ans = "You need two arguments.";
		if(args.length==2)
			ans = new BigInteger(args[0]).gcd(new BigInteger(args[1])).toString();
		return ans;
	}
	public String parseCheckPrime(Message message,Guild guild,MessageChannel channel,User author,String[]args)
	{
		ArrayList<String>ans = new ArrayList<>();
		BigInteger n = null;
		for(int i=0;i<args.length;i++)
		{
			n = new BigInteger(args[i]);
			if(n.compareTo(BigInteger.ONE) <= 0)
				ans.add("Neither");
			else if(n.isProbablePrime(100))
				ans.add("Prime");
			else
				ans.add("Composite");
		}
		return ans.toString();
	}
	public String parseConvertBase(Message message,Guild guild,MessageChannel channel,User author,String[]args)
	{
		String ans = "There should be at least three parameters.";
		if(args.length>=3)
		{
			int from = Integer.parseInt(args[0]);
			int to = Integer.parseInt(args[1]);
			ans = "";
			for(int i=2;i<args.length;i++)
			{
				ans += new BigInteger(args[i], from).toString(to) + " ";
			}
		}
		return ans;
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
			else if(!guild.getMember(author).hasPermission(Permission.MANAGE_SERVER))
				ans = "You do not have permission to use this command.";
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
		else if(!guild.getMember(author).hasPermission(Permission.MANAGE_SERVER))
			ans = "You do not have permission to use this command.";
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
		if(args.length == 0)
			ans = "Name at least one role.";
		else
		{
			Role[]roles = new Role[args.length];
			for(int i=0;i<roles.length;i++)
				roles[i]=ScannerV_0_3.findRole(guild,args[i]);
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
				Role role=null;
				int nfc = 0;
				for(int i=0;i<roles.length;i++)
				{
					role = roles[i];
					if(role!=null&&rids.contains(role.getIdLong()))
					{
						if(guild.getMember(author).getRoles().contains(role))
							guild.removeRoleFromMember(author.getIdLong(),role).queue();
						else
							guild.addRoleToMember(author.getIdLong(),role).queue();
					}
					else
						++nfc;
				}
				if(nfc == 0)
					ans = "Successfully added all specified roles to you or removed from you.";
				else if(nfc == roles.length)
					ans = "None of the roles were found, nothing was changed, try again.";
				else if(nfc == 1)
					ans = "One role was not found, the rest were added or removed successfully.";
				else
					ans = nfc + " roles were not found, the rest were added or removed successfully.";
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
			if(!guild.getMember(author).hasPermission(Permission.MANAGE_SERVER))
				ans = "You do not have permission to use this command.";
			else if("__clear__".equals(args[0]))
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
				{
					ans += " This user joined the server on " + guild.getMemberById(snowflake).getTimeJoined().toString() + ". ";
					ans += guild.getMemberById(snowflake).getUser().getAvatarUrl();
				}
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
		if(args.length == 1)
		{
			ans = MathAlgs.exp(ScannerV_0_3.strToNum(args[0])).toString();
		}
		else if(args.length == 2)
		{
			BigDecimal base = ScannerV_0_3.strToNum(args[0]);
			BigDecimal exponent = ScannerV_0_3.strToNum(args[1]);
			ans = MathAlgs.pow(base, exponent).toString();
		}
		return ans;
	}
	public String parseComputeLog(Message message,Guild guild,MessageChannel channel,User author,String[]args)
	{
		var ans = "You need one or two arguments.";
		if(args.length == 1)
		{
			ans = MathAlgs.log(ScannerV_0_3.strToNum(args[0])).toString();
		}
		else if(args.length == 2)
		{
			BigDecimal base = ScannerV_0_3.strToNum(args[0]);
			BigDecimal p = ScannerV_0_3.strToNum(args[1]);
			ans = MathAlgs.log(p).divide(MathAlgs.log(base), MathContext.DECIMAL128).toString();
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
			ans = String.format("The temperature today will be around %.2f degrees, with a minimum of %.2f and maximum of %.2f, pressure is %.2f kPa, humidity is %.2f. Wind blows at speed of %.1f m/s, at angle %d, which is direction %s. Sunrise happens at %d:%02d, sunset happens at %d:%02d.", temp, temps, tempb, pressure, humidity, speed, angle, direction, ca.get(Calendar.HOUR), ca.get(Calendar.MINUTE), cb.get(Calendar.HOUR), cb.get(Calendar.MINUTE));
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
		else if(guild.getMember(author).hasPermission(Permission.MANAGE_SERVER))
		{
			for(int i=0;i<args.length;i++)
				this.macros.get(guild.getIdLong()).remove(args[i].substring(1));
			ans="Removed specified macros.";
		}
		else
			ans="You do not have permission to use this command.";
		return ans;
	}
	public String parseSetMuteRole(Message message,Guild guild,MessageChannel channel,User author,String[]args)
	{
		var ans="Name a role, stupid.";
		if(args.length==1)
		{
			if(!guild.getMember(author).hasPermission(Permission.MANAGE_SERVER))
				ans = "You do not have permission to use this command.";
			else
			{
				this.muteroles.put(guild.getIdLong(),ScannerV_0_3.findRole(guild,args[0]));
				ans="Set the new mute role.";
			}
		}
		return ans;
	}
	public String parseGetMuteRole(Message message,Guild guild,MessageChannel channel,User author,String[]args)
	{
		return this.muteroles.get(guild.getIdLong()) + " is the mute role.";
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
	public String parseTangentTheta(Message message,Guild guild,MessageChannel channel,User author,String[]args)
	{
		var ans="Give an angle.";
		if(args.length==1)
		{
			ans=MathAlgs.tan(ScannerV_0_3.strToNum(args[0]).multiply(MathAlgs.PI_BY_180,MathContext.DECIMAL128)).toString();
		}
		return ans;
	}
	public String parseCotangentTheta(Message message,Guild guild,MessageChannel channel,User author,String[]args)
	{
		var ans="Give an angle.";
		if(args.length==1)
		{
			ans=MathAlgs.cot(ScannerV_0_3.strToNum(args[0]).multiply(MathAlgs.PI_BY_180,MathContext.DECIMAL128)).toString();
		}
		return ans;
	}
	public String parseSecantTheta(Message message,Guild guild,MessageChannel channel,User author,String[]args)
	{
		var ans="Give an angle.";
		if(args.length==1)
		{
			ans=MathAlgs.sec(ScannerV_0_3.strToNum(args[0]).multiply(MathAlgs.PI_BY_180,MathContext.DECIMAL128)).toString();
		}
		return ans;
	}
	public String parseCosecantTheta(Message message,Guild guild,MessageChannel channel,User author,String[]args)
	{
		var ans="Give an angle.";
		if(args.length==1)
		{
			ans=MathAlgs.csc(ScannerV_0_3.strToNum(args[0]).multiply(MathAlgs.PI_BY_180,MathContext.DECIMAL128)).toString();
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
	public String parseWhiteList(Message message,Guild guild,MessageChannel channel,User author,String[]args)
	{
		if(args.length<2)
		{
			return"Name a reply and at least one member to whitelist.";
		}
		else
		{
			Member mem=null;
			String reply=args[0];
			var ans = "Successfully whitelisted all members.";
			for(int i=1;i<args.length;i++)
			{
				mem=ScannerV_0_3.findMember(guild,args[i]);
				if(mem == null)
					ans = "One or more members could not be found.";
				else
				{
					if(!this.replyWhiteList.get(guild.getIdLong()).containsKey(reply))
						this.replyWhiteList.get(guild.getIdLong()).put(reply, new ArrayList<>());
					this.replyWhiteList.get(guild.getIdLong()).get(reply).add(mem.getIdLong());
				}
			}
			System.out.println(this.replyWhiteList);
			return ans;
		}
	}
	public String parseRemoveWhiteList(Message message,Guild guild,MessageChannel channel,User author,String[]args)
	{
		if(args.length<2)
		{
			return"Name a reply and at least one member to remove from the whitelist.";
		}
		else
		{
			Member mem=null;
			String reply=args[0];
			var ans = "Successfully un-whitelisted all members.";
			for(int i=1;i<args.length;i++)
			{
				mem=ScannerV_0_3.findMember(guild,args[i]);
				if(mem == null)
					ans = "One or more members could not be found.";
				else if(this.replyWhiteList.get(guild.getIdLong()).containsKey(reply))
					this.replyWhiteList.get(guild.getIdLong()).get(reply).remove(mem.getIdLong());
			}
			System.out.println(this.replyWhiteList);
			return ans;
		}
	}
	public String parsePrimeFactor(Message message,Guild guild,MessageChannel channel,User author,String[]args)
	{
		ArrayList<ArrayList<BigInteger>>factors=new ArrayList<>(args.length);
		for(int i=0;i<args.length;i++)
		{
			factors.add(MathAlgs.factor(new BigInteger(args[i])));
		}
		return"The list of prime factorizations are "+factors;
	}
	public String parseEncodeBase64(Message message,Guild guild,MessageChannel channel,User author,String[]args)
	{
		if(args.length==1)
			return new String(Base64.getMimeEncoder().encode(args[0].getBytes()));
		else
			return"Only one argument is accepted.";
	}
	public String parseDecodeBase64(Message message,Guild guild,MessageChannel channel,User author,String[]args)
	{
		if(args.length==1)
			return new String(Base64.getMimeDecoder().decode(args[0].getBytes()));
		else
			return"Only one argument is accepted.";
	}
	public String parseListMembersWithRole(Message message,Guild guild,MessageChannel channel,User author,String[]args)
	{
		if(args.length==0)
			return"Give me a role.";
		else
		{
			Role r = ScannerV_0_3.findRole(guild,args[0]);
			var members=guild.getMembersWithRoles(r);
			String replies = "Found the following members";
			for(var mem : members)
				replies += "\r\n" + mem.getUser() + " AKA " + mem.getEffectiveName();
			return replies;
		}
	}
	public String parseListMembersWithoutRole(Message message,Guild guild,MessageChannel channel,User author,String[]args)
	{
		if(args.length==0)
			return"Give me a role.";
		else
		{
			Role r = ScannerV_0_3.findRole(guild,args[0]);
			var has=guild.getMembersWithRoles(r);
			ArrayList<Member>members=new ArrayList<Member>(guild.getMembers());
			members.removeAll(has);
			String replies = "Found the following members";
			for(var mem : members)
				replies += "\r\n" + mem.getUser() + " AKA " + mem.getEffectiveName();
			return replies;
		}
	}
	public String parseSetCaptains(Message message,Guild guild,MessageChannel channel,User author,String[]args)
	{
		if(args.length==0)
			return"Name a team captain.";
		else
		{
			ArrayList<Member>captains=new ArrayList<Member>();
			this.teams.put(guild.getIdLong(),new HashMap<>());
			var teams = this.teams.get(guild.getIdLong());
			Member mem = null;
			for(int i=0;i<args.length;i++)
			{
				mem = ScannerV_0_3.findMember(guild, args[i]);
				if(mem != null)
				{
					teams.put(mem.getIdLong(), new ArrayList<>());
					captains.add(mem);
				}
			}
			String reply = "Team captains have been set to ";
			for(int i=0;i<captains.size();i++)
			{
				reply += captains.get(i).getAsMention();
				if(i == captains.size() - 1)
					reply += ".";
				else if(i == captains.size() - 2)
					reply += ", and ";
				else
					reply += ", ";
			}
			return reply;
		}
	}
	public String parseSetPlayers(Message message,Guild guild,MessageChannel channel,User author,String[]args)
	{
		if(args.length == 0)
			return"Name some members.";
		else
		{
			this.playerPool.put(guild.getIdLong(),new ArrayList<>());
			var players = this.playerPool.get(guild.getIdLong());
			Member mem = null;
			for(int i=0;i<args.length;i++)
			{
				mem = ScannerV_0_3.findMember(guild, args[i]);
				if(mem != null)
					players.add(mem.getIdLong());
			}
			String reply = "Players are ";
			for(int i=0;i<players.size();i++)
			{
				reply += guild.getMemberById(players.get(i)).getAsMention();
				if(i == players.size() - 1)
					reply += ".";
				else if(i == players.size() - 2)
					reply += ", and ";
				else
					reply += ", ";
			}
			return reply;
		}
	}
	public String parsePickTeammate(Message message,Guild guild,MessageChannel channel,User author,String[]args)
	{
		if(args.length != 1)
			return"You may only draft one player at a time.";
		else if(!this.teams.get(guild.getIdLong()).containsKey(author.getIdLong()))
			return"You are not a team captain.";
		else
		{
			Member mem = ScannerV_0_3.findMember(guild, args[0]);
			var players = this.playerPool.get(guild.getIdLong());
			var teams = this.teams.get(guild.getIdLong());
			String reply = "";
			if(players.remove(mem.getIdLong()))
			{
				reply = "Drafted " + mem.getEffectiveName() + " for your team.";
				teams.get(author.getIdLong()).add(mem.getIdLong());
			}
			else
				reply = "That player is not eligible to play.";
			if(players.size() == 0)
			{
				reply = "";
				for(Long captain : teams.keySet())
				{
					mem = guild.getMemberById(captain);
					reply += "Team " + mem.getAsMention() + " has the following players.\r\n";
					for(int i=0;i<teams.get(captain).size();i++)
						reply += guild.getMemberById(teams.get(captain).get(i)).getAsMention() + "\r\n";
					reply += "\r\n";
				}
				teams.clear();
			}
			return reply;
		}
	}
	public String parseDisplayPlayers(Message message,Guild guild,MessageChannel channel,User author,String[]args)
	{
		String s="";
		var players = this.playerPool.get(guild.getIdLong());
		for(int i=0;i<players.size();i++)
			s += guild.getMemberById(players.get(i)).getEffectiveName() + " ";
		return s;
	}
	public String parseGetReplies(Message message,Guild guild,MessageChannel channel,User author,String[]args)
	{
		return this.replies.get(guild.getIdLong()).toString();
	}
	public String parseGetPermutations(Message message,Guild guild,MessageChannel channel,User author,String[]args)
	{
		if(args.length==1)
			return args[0].length()<11?StringAlgs.permutes(args[0]):"Input too long.";
		else
			return"Only one argument is accepted.";
	}
	public String parseRequestData(Message message,Guild guild,MessageChannel channel,User author,String[]args)
	{
		this.save();
		File dir = new File(Long.toHexString(guild.getIdLong()));
		File[]files = dir.listFiles();
		String cmd = "zip " + dir.getName() + ".zip";
		for(int i=0;i<files.length;i++)
			cmd += " " + files[i].getPath();
		String msg = "Here's the data for your server.";
		try
		{
			Runtime.getRuntime().exec(cmd);
		}
		catch(IOException e)
		{
			e.printStackTrace();
			msg = e.getMessage();
		}
		channel.sendFile(new File(dir.getName() + ".zip")).queue();
		return msg;
	}
	public String parseGetRandom(Message message,Guild guild,MessageChannel channel,User author,String[]args)
	{
		if(args.length != 0)
		{
			BigInteger lower = args.length==1?BigInteger.ZERO:new BigInteger(args[0]);
			BigInteger upper = new BigInteger(args[args.length==1?0:1]);
			int cnt = args.length==3?Integer.parseInt(args[2]):1;
			BigInteger range = upper.subtract(lower).add(BigInteger.ONE);
			BigInteger num = null;
			int bits = range.bitLength() + 7;
			byte[]bytes = new byte[bits / 8];
			String result = "";
			for(int i=0;i<cnt;i++)
			{
				this.dice.nextBytes(bytes);
				num = new BigInteger(bytes).mod(range);
				result += num.add(lower) + ", ";
			}
			return result.substring(0, result.length() - 2);
		}
		else
			return String.valueOf(this.dice.nextDouble());
	}
	public String parseGetEmoji(Message message,Guild guild,MessageChannel channel,User author,String[]args)
	{
		if(args.length==1)
		{
			String name = args[0];
			var emotes = guild.getEmotesByName(name,false);
			if(emotes.size() == 0)
				return"Emote not found, check spelling and case.";
			else
				return emotes.get(0).getImageUrl();
		}
		else
			return"Only one argument is accepted, the name of the emoji.";
	}
	public String parseGetWhitelist(Message message,Guild guild,MessageChannel channel,User author,String[]args)
	{
		return this.replyWhiteList.get(guild.getIdLong()).toString();
	}
	public String parseReportBug(Message message,Guild guild,MessageChannel channel,User author,String[]args)
	{
		String bug = message.getContentRaw();
		try
		{
			FileOutputStream fout = new FileOutputStream("bugs.txt",true);
			fout.write(bug.getBytes());
			fout.write("____end____".getBytes());
			fout.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return e.toString();
		}
		return"Successfully reported bug.";
	}
	public String parseArithmancyCalculator(Message message,Guild guild,MessageChannel channel,User author,String[]args)
	{
		String s="";
		for(int i=0;i<args.length;i++)
		{
			s+=args[i];
		}
		return Integer.toString(StringAlgs.arithmancy(s));
	}
	public String parseScoreboardActions(Message message,Guild guild,MessageChannel channel,User author,String[]args)
	{
		String s="Name an action.";
		if(args.length>1)
		{
			String action = args[0];
			String name = args[1];
			var scoreboards = this.scoreboards.get(guild.getIdLong());
			Scoreboard scores = null;
			if("create".equals(action))
			{
				scoreboards.add(new Scoreboard(name));
				s = "Created new scoreboard objective.";
			}
			else
			{
				boolean found = false;
				for(int i=0;i<scoreboards.size();i++)
				{
					if(scoreboards.get(i).getName().equals(name))
					{
						scores = scoreboards.get(i);
						found = true;
					}
				}
				if(!found)
					s = "Scoreboard name not found, check your spelling.";
				else if("display".equals(action))
				{
					s = scores.getName();
					for(var id : scores.getScores().keySet())
					{
						s += System.lineSeparator() + guild.getMemberById(id) + " --- " + scores.getScores().get(id);
					}
				}
				else if("set".equals(action))
				{
					if(args.length != 4)
						s = "Not enough arguments, give member and value";
					else
					{
						Member target = ScannerV_0_3.findMember(guild, args[2]);
						scores.getScores().put(target.getIdLong(), Long.parseLong(args[3]));
						s = "Score has been set.";
					}
				}
				else if("get".equals(action))
				{
					if(args.length != 3)
						s = "Not enough arguments, give member";
					else
					{
						Member target = ScannerV_0_3.findMember(guild, args[2]);
						s = scores.getScores().get(target.getIdLong()) + " is the score.";
					}
				}
				else if("add".equals(action))
				{
					if(args.length != 4)
						s = "Not enough arguments, give member and value";
					else
					{
						Member target = ScannerV_0_3.findMember(guild, args[2]);
						scores.getScores().put(target.getIdLong(), scores.getScores().get(target.getIdLong()) + Long.parseLong(args[3]));
						s = "Score has been set.";
					}
				}
			}
		}
		return s;
	}
	public String parseMessageLater(Message message,Guild guild,MessageChannel channel,User author,String[]args)
	{
		if(args.length>=2)
		{
			String ts = args[0];
			int multiplier = 1;
			if('d'==ts.charAt(ts.length()-1))
				multiplier = 86400;
			else if('h'==ts.charAt(ts.length()-1))
				multiplier = 3600;
			else if('m'==ts.charAt(ts.length()-1))
				multiplier = 60;
			int ind = message.getContentRaw().indexOf(ts);
			String tosend = message.getContentRaw().substring(ind + ts.length());
			if(multiplier!=1)
				ts = ts.substring(0,ts.length()-1);
			int time = Integer.parseInt(ts) * multiplier;
			Thread thread = new Thread
			(
				()->
				{
					try
					{
						Thread.sleep(1000*time);
						channel.sendMessage(tosend).queue();
					}
					catch(InterruptedException e)
					{
						e.printStackTrace();
					}
				}
			);
			thread.start();
			return"Message has been timed.";
		}
		else
			return"Specify time after command to send message and message to send.";
	}
	public String parseAddKillMsg(Message message,Guild guild,MessageChannel channel,User author,String[]args)
	{
		if(args.length!=0)
		{
			String msg = args[0];
			for(int i=1;i<args.length;i++)
				msg += " " + args[i];
			this.killmsgs.get(guild.getIdLong()).add(msg);
			return"Successfully added kill message.";
		}
		else
			return"Specify a message to add.";
	}
	public String parseGetKillMsgs(Message message,Guild guild,MessageChannel channel,User author,String[]args)
	{
		String resp = "This is the list of kill messages.";
		for(int i=0;i<this.killmsgs.get(guild.getIdLong()).size();i++)
			resp += System.lineSeparator() + (i + 1) + ". " + this.killmsgs.get(guild.getIdLong()).get(i);
		return resp;
	}
	public String parseSendKillMsg(Message message,Guild guild,MessageChannel channel,User author,String[]args)
	{
		if(args.length!=0)
		{
			String msg = args[0];
			for(int i=1;i<args.length;i++)
				msg += " " + args[i];
			Member mem = ScannerV_0_3.findMember(guild, msg);
			var messages = this.killmsgs.get(guild.getIdLong());
			int rand = this.dice.nextInt(messages.size());
			msg = messages.get(rand).replace("\\t", mem.getEffectiveName());
			return msg;
		}
		else
			return"Name a member to kill.";
	}
	public String parseRemoveKillMsg(Message message,Guild guild,MessageChannel channel,User author,String[]args)
	{
		if(args.length!=0)
		{
			int ind = Integer.parseInt(args[0]);
			var messages = this.killmsgs.get(guild.getIdLong());
			if(ind>messages.size()||ind<=0)
				return"Index out of bounds.";
			else
			{
				messages.remove(ind - 1);
				return"Removed kill message.";
			}
		}
		else
			return"Name a message to remove.";
	}
	public String parseStripNonDigits(Message message,Guild guild,MessageChannel channel,User author,String[]args)
	{
		String s="";
		for(int i=0;i<args.length;i++)
		{
			s+=StringAlgs.stripNonDigits(args[i])+" ";
		}
		return s;
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
	public String parseConsoleSave(Message message,Guild guild,MessageChannel channel,User author,String[]args)
	{
		this.save();
		return"If no exception is thrown, the save was successful.";
	}
	public String parseConsoleLoad(Message message,Guild guild,MessageChannel channel,User author,String[]args)
	{
		this.load();
		return"If no exception is thrown, the load was successful.";
	}
	public static void main(String[] args)
	{
		try
		{
			if(args.length==1)
				System.setOut(new PrintStream(new FileOutputStream(args[0])));
			new ScannerV_0_3().load();
			System.out.println("done");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}