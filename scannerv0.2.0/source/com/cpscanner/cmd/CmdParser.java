package com.cpscanner.cmd;
import java.util.*;
import net.dv8tion.jda.api.entities.*;
/**
 * Advanced nested command parser.
 * @author CubedProgrammer.
 */
public class CmdParser
{
	/**
	 * The prefix that signals to the command parser that some number of arguments should be skipped.
	 */
	public static final String SKIP_TOKEN_SIGNIFIER="_skip_";
	/**
	 * The map of commands, maps from command name to function.
	 */
	private HashMap<String,ScCmd>cmds;
	/**
	 * Constructs a new CmdParser.
	 * @param parsers The array of functions for command parsing.
	 * @param names The array of command names that correspond to the functions.
	 */
	public CmdParser(ScCmd[]parsers,String...names)
	{
		this.cmds=new HashMap<String,ScCmd>();
		for(int i=0;i<parsers.length;i++)
		{
			this.cmds.put(names[i],parsers[i]);
		}
	}
	/**
	 * Parses a command that is an array of tokens.
	 * @param guild The guild the command was sent from.
	 * @param author The user who sent the command.
	 * @param channel The ID of the channel that the command was sent from.
	 * @param tokens The list of tokens for this command.
	 * @return The message that is to be printed or sent over to discord.
	 */
	public String parse(Guild guild,User author,long channel,String[]tokens)
	{
		String[]args=null;
		ArrayList<String>argsal=new ArrayList<String>();
		Stack<String>stackCMD=new Stack<String>();
		Stack<Integer>stack=new Stack<Integer>();
		String name="";
		String result="You are gay.";
		if(tokens.length>0&&"help".equals(tokens[0]))
		{
			result="The list of commands are";
			for(var it=this.cmds.keySet().iterator();it.hasNext();)
			{
				result+="\r\n"+it.next();
			}
		}
		else
		{
			int start=0;
			int end=0;
			for(int i=0;i<tokens.length;i++)
			{
				if(this.cmds.containsKey(tokens[i]))
				{
					stackCMD.push(tokens[i]);
					stack.push(i);
				}
				if(stackCMD.size()>0&&(i+1==tokens.length||"end".equals(tokens[i])))
				{
					do
					{
						name=stackCMD.pop();
						start=stack.pop();
						argsal.clear();
						end=i+1==tokens.length?tokens.length:i;
						for(int j=start+1;j<end;j++)
						{
							if(tokens[j].length()>CmdParser.SKIP_TOKEN_SIGNIFIER.length()&&CmdParser.SKIP_TOKEN_SIGNIFIER.equals(tokens[j].substring(0,CmdParser.SKIP_TOKEN_SIGNIFIER.length())))
							{
								argsal.add(tokens[j].substring(CmdParser.SKIP_TOKEN_SIGNIFIER.length()+8));
								j+=Integer.parseInt(tokens[j].substring(CmdParser.SKIP_TOKEN_SIGNIFIER.length(),CmdParser.SKIP_TOKEN_SIGNIFIER.length()+8))-1;
							}
							else
							{
								argsal.add(tokens[j]);
							}
						}
						args=new String[argsal.size()];
						for(int j=0;j<args.length;args[j]=argsal.get(j++));
						try
						{
							result=this.cmds.get(name).parse(guild,author,channel,args);
						}
						catch(Exception e)
						{
							++start;
							result="Command `"+name+"` failed, which was the "+Integer.toString(start)+((start%100>13||start%100<11)&&start%10<=3&&start%10!=0?start%10==3?"rd":start%10==2?"nd":"st":"th")+" token, and error message is \r\n"+e.toString();
							e.printStackTrace();
							--start;
						}
						name="";
						for(int j=0;j<Integer.numberOfLeadingZeros(end-start+1)>>2;j++)
						{
							name+="0";
						}
						tokens[start]=CmdParser.SKIP_TOKEN_SIGNIFIER+name+Integer.toString(end-start+1,16)+result;
					}
					while(i+1==tokens.length&&!stack.empty());
				}
			}
		}
		return result;
	}
}