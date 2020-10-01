package cp.scanner.main;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import javax.security.auth.login.LoginException;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
/**
 * Main class that contains the main method.
 * @author CubedProgrammer
 */
public class ScannerV_0_3 extends ListenerAdapter
{
	/**
	 * Token for bot authentication.
	 */
	public static final String TOKEN="Njg2MjUxMjQ5NzkxNzk1MjE0.XmUfRw.E0tBvkSfSj1Xt0U9TpC0nsSzpVs";
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
			n=n.divide(new BigDecimal(parts[i]),MathContext.DECIMAL128);
		}
		return n;
	}
	private JDA jda;
	private User self;
	private long ID;
	private String prefix;
	private String mention;
	private CommandParser parser;
	public ScannerV_0_3() throws LoginException, InterruptedException
	{
		this.jda = new JDABuilder(AccountType.BOT).setToken(ScannerV_0_3.TOKEN).build().awaitReady();
		this.jda.addEventListener(this);
		this.self = this.jda.getSelfUser();
		this.ID = this.self.getIdLong();
		this.prefix = "--";
		this.mention = "<@!" + this.ID + ">";
		this.parser = new CommandParser();
	}
	public void onMessageReceived(MessageReceivedEvent evt)
	{
		if(evt.getAuthor().getIdLong()!=this.ID)
		{
			Message message = evt.getMessage();
			String raw = message.getContentRaw();
			if(raw.startsWith(this.mention))
			{
				raw = raw.substring(this.mention.length()).strip();
			}
			else if(raw.startsWith(this.prefix))
			{
				raw = raw.substring(this.prefix.length()).strip();
			}
			else
				return;
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
	public static void main(String[] args)
	{
		try
		{
			new ScannerV_0_3();
			Scanner scanner = new Scanner(System.in);
			String s = scanner.next();
			if("exit".equals(s))
			{
				scanner.close();
				System.exit(1);
			}
			scanner.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}