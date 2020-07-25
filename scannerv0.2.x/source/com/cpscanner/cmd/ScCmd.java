package com.cpscanner.cmd;
import net.dv8tion.jda.api.entities.*;
/**
 * Interface for commands parsing functions.
 * @author CubedProgrammer
 */
public interface ScCmd
{
	/**
	 * Parses a command.
	 * @param guild The guild the command was sent from.
	 * @param author The user who sent the command.
	 * @param channel The ID of the channel that the command was sent from.
	 * @param args The list of arguments for this command.
	 * @return The message that is to be printed or sent over to discord.
	 */
	public abstract String parse(Guild guild,User author,long channel,String...args);
}