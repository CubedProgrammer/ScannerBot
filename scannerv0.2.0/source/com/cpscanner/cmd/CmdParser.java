package com.cpscanner.cmd;
import java.util.*;
import net.dv8tion.jda.api.entities.*;
public class CmdParser
{
	public static final String SKIP_TOKEN_SIGNIFIER="_skip_";
	private HashMap<String,ScCmd>cmds;
	public CmdParser(ScCmd[]parsers,String...names)
	{
		this.cmds=new HashMap<String,ScCmd>();
		for(int i=0;i<parsers.length;i++)
		{
			this.cmds.put(names[i],parsers[i]);
		}
	}
	public String parse(Guild guild,User author,long channel,String[]tokens)
	{
		String[]args=null;
		ArrayList<String>argsal=new ArrayList<String>();
		Stack<String>stackCMD=new Stack<String>();
		Stack<Integer>stack=new Stack<Integer>();
		String name="";
		String result="";
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
					result=this.cmds.get(name).parse(guild,author,channel,args);
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
		return result;
	}
}