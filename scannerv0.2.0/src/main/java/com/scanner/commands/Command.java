package com.scanner.commands;
import net.dv8tion.jda.core.entities.Guild;
public class Command {
	
	public final String name;
	protected CommandParser parser;
	
	public Command(String name,CommandParser parser) {
		
		this.name=name;
		this.parser=parser;
		
	}
	
	public String readCommand(Guild guild,long author,long channel,String...args) {
		return this.parser.parse(guild,author,channel,args);
	}
	
}