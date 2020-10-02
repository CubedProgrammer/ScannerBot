package cp.scanner.main;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
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
			ans = "0.3.0 (First beta release)";
		else if("help".equalsIgnoreCase(cmd))
		{
			ans = "This is the list of commands.";
			BotCommand bc = null;
			String name = null;
			for(var it = this.cmds.keySet().iterator();it.hasNext();)
			{
				bc = this.cmds.get(name = it.next());
				ans += System.getProperty("line.separator") + name + " - " + bc.description;
			}
		}
		else if(this.cmds.containsKey(cmd))
		{
			int start = 0;
			ArrayList<String>stack = new ArrayList<String>();
			ArrayList<Integer>pos = new ArrayList<Integer>();
			ArrayList<String>argsl = new ArrayList<String>();
			for(int i=0;i<args.length;i++)
			{
				cmd = args[i];
				if(this.cmds.containsKey(cmd))
				{
					stack.add(cmd);
					pos.add(i);
				}
				else if("__end__".equals(cmd) || i+1 == args.length)
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
						args[start] = this.cmds.get(args[start]).action.parse(message, guild, channel, author, argsl.toArray(new String[argsl.size()]));
						args[start + 1] = CommandParser.SKIP_SIGNAL_PREFIX + Integer.toHexString(Math.min(i+1, args.length));
						if(i == args.length)
							i--;
					}
					while(i+1 == args.length && stack.size() > 0);
				}
				System.out.println(Arrays.toString(args));
			}
			ans = args[0];
		}
		else
			ans = "Unknown command.";
		return ans;
	}
}