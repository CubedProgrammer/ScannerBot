package cp.scanner.cmd;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

public interface CmdFunction
{
	public abstract String parse(Message message,Guild guild,MessageChannel channel,User author,String[]args);
}