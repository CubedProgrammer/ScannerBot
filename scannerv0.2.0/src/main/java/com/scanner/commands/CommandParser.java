package com.scanner.commands;
import net.dv8tion.jda.core.entities.Guild;
public interface CommandParser {
	public abstract String parse(Guild guild,long author,long channel,String...args);
}