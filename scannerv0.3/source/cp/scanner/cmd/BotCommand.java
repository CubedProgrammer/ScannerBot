package cp.scanner.cmd;
public class BotCommand
{
	public final CmdFunction action;
	public final String description;
	public BotCommand(String description,CmdFunction action)
	{
		this.action = action;
		this.description = description;
	}
}