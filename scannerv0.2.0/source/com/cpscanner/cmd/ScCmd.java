package com.cpscanner.cmd;
import net.dv8tion.jda.api.entities.*;
public interface ScCmd
{
	public abstract String parse(Guild guild,User author,long channel,String...args);
}