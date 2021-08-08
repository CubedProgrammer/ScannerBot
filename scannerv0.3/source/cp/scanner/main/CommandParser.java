package cp.scanner.main;
import java.util.*;
import cp.scanner.cmd.BotCommand;
import cp.scanner.cmd.CmdFunction;
import net.dv8tion.jda.api.entities.*;
public class CommandParser
{
	public static final String SKIP_SIGNAL_PREFIX = "__skip__";
	private LinkedHashMap<String,BotCommand>cmds;
	public CommandParser()
	{
		this.cmds = new LinkedHashMap<String,BotCommand>();
	}
	public CommandParser put(String name, String description, CmdFunction cmd)
	{
		this.cmds.put(name, new BotCommand(description, cmd));
		return this;
	}
	public String parse(Message message,Guild guild,MessageChannel channel,User author,String[]args)
	{
		String ans = "error";
		String cmd = args[0];
		if("version".equalsIgnoreCase(cmd))
			ans = "0.3.8 (Nineth beta release)";
		else if("help".equalsIgnoreCase(cmd))
		{
			if(args.length == 1)
			{
				ans = "This is the list of commands, arguments must be **SPACE SEPARATED**, if an argument must have spaces in them, use quotes. Use help *command* to show information about a specific command." + System.getProperty("line.separator");
				String name = null;
				for(var it = this.cmds.keySet().iterator();it.hasNext();)
				{
					name = it.next();
					ans += name + "\r\n";
				}
			}
			else
			{
				if(this.cmds.containsKey(args[1]))
					ans = this.cmds.get(args[1]).description;
				else
					ans = "Command " + args[1] + " could not be found.";
			}
		}
		else if(this.cmds.containsKey(cmd.toLowerCase()))
		{
			int start = 0;
			ArrayList<String>stack = new ArrayList<String>();
			ArrayList<Integer>pos = new ArrayList<Integer>();
			ArrayList<String>argsl = new ArrayList<String>();
			System.out.println(cmd);
			for(int i=0;i<args.length;i++)
			{
				cmd = args[i];
				if(this.cmds.containsKey(cmd.toLowerCase()))
				{
					stack.add(cmd);
					pos.add(i);
				}
				if("__end__".equals(cmd) || i+1 == args.length)
				{
					do
					{
						stack.remove(stack.size() - 1);
						start = pos.remove(pos.size() - 1);
						if(i+1 == args.length)
							i++;
						argsl.clear();
						for(int j=start+1;j<i;j++)
						{
							if(args[j].startsWith(CommandParser.SKIP_SIGNAL_PREFIX))
							{
								j = Integer.parseInt(args[j].substring(CommandParser.SKIP_SIGNAL_PREFIX.length()), 16);
								j--;
							}
							else
								argsl.add(args[j]);
						}
						try
						{
							args[start] = this.cmds.get(args[start]).action.parse(message, guild, channel, author, argsl.toArray(new String[argsl.size()]));
						}
						catch(Exception e)
						{
							ans = "Error occured for command "+args[start]+" which was token "+start+", it has produced the following message."+e.toString();
							e.printStackTrace();
						}
						if(start + 1 < args.length)
							args[start + 1] = CommandParser.SKIP_SIGNAL_PREFIX + Integer.toHexString(Math.min(i+1, args.length));
						if(i == args.length)
							i--;
					}
					while(i+1 == args.length && stack.size() > 0);
				}
			}
			ans = args[0];
		}
		else
			ans = "Unknown command.";
		return ans;
	}
}