package cp.scanner.main;

import net.dv8tion.jda.api.entities.*;

public class CommandParser
{
	public CommandParser()
	{
	}
	public String parse(Message message,Guild guild,MessageChannel channel,User author,String[]args)
	{
		String ans = "error";
		String cmd = args[0];
		if("version".equalsIgnoreCase(cmd))
			ans = "0.3.0 (First beta release)";
		return ans;
	}
}