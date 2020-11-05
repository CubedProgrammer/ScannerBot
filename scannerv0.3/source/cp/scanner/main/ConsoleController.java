package cp.scanner.main;

import java.util.Scanner;
import cp.scanner.cmd.CmdFunction;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;

public class ConsoleController
{
	private JDA jda;
	private Guild guild;
	private MessageChannel channel;
	private CommandParser parser;
	private ScannerV_0_3 bot;
	public ConsoleController(JDA jda,String[]names,String[]descriptions,CmdFunction[]funcs,ScannerV_0_3 bot)
	{
		this.jda=jda;
		this.guild=this.jda.getGuilds().size()==0?null:this.jda.getGuilds().get(0);
		this.channel=this.guild==null?null:this.guild.getDefaultChannel();
		this.parser=new CommandParser();
		this.bot=bot;
		for(int i=0;i<funcs.length;i++)
		{
			this.parser.put(names[i],descriptions[i],funcs[i]);
		}
	}
	public void run()
	{
		Scanner scanner=new Scanner(System.in);
		String cmd=scanner.nextLine();
		String[]args=null;
		while(!"exit".equals(cmd))
		{
			if(cmd.startsWith("change "))
			{
				args=cmd.split(" ");
				if(args.length==3)
				{
					this.guild=this.jda.getGuildById(args[1]);
					this.channel=this.jda.getTextChannelById(args[2]);
					System.out.println("Successfully changed to guild "+this.guild.getName()+" and channel "+this.channel.getName());
				}
				else
				{
					System.out.println("You need the server and channel ID.");
				}
			}
			else
				System.out.println(this.parser.parse(null,this.guild,this.channel,null,ScannerV_0_3.getCmdArgs(cmd)));
			cmd=scanner.nextLine();
		}
		scanner.close();
		this.bot.save();
		System.exit(1);
	}
}