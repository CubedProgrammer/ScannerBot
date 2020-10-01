package com.cpscanner.main;
import java.io.*;
import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.*;
/**
 * Class for listening to messages sent over discord.
 * @author CubedProgrammer
 */
public class MessageListener extends ListenerAdapter
{
	/**
	 * The last message that was sent in a channel that this bot can read.
	 */
	private Message last;
	/**
	 * A boolean value indicating whether or not the message is new.
	 */
	private boolean newm;
	/**
	 * Empty constructor.
	 */
	public MessageListener(){}
	/**
	 * Receives a message and reads it.
	 * @param evt The event that is received.
	 */
	public void onMessageReceived(MessageReceivedEvent evt)
	{
		if(evt.getAuthor().getIdLong()!=ScannerV0_2_6.ID)
		{
			this.last=evt.getMessage();
			this.newm=true;
		}
	}
	/**
	 * {@inheritDoc}
	 */
	public void onGuildMemberJoin(GuildMemberJoinEvent evt)
	{
		Guild guild = evt.getGuild();
		try
		{
			FileReader reader = new FileReader(guild.getId()+"/roleinfo.dat");
			JSONObject object = (JSONObject)new JSONParser().parse(reader);
			reader.close();
			if(object.containsKey("autoroles"))
			{
				JSONArray autoroles = (JSONArray)object.get("autoroles");
				for(Object r:autoroles)
				{
					guild.addRoleToMember(evt.getMember(),guild.getRoleById((Long)r)).queue();
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * {@inheritDoc}
	 */
	public void onGuildMemberLeave(GuildMemberLeaveEvent evt)
	{
		evt.getGuild().getDefaultChannel().sendMessage(evt.getMember().getAsMention()+", AKA "+evt.getMember().getId()+" has left the server.").queue();
	}
	/**
	 * Gets the last message sent to a channel this bot is able to read.
	 * @return The message that was sent.
	 */
	public Message getLastMessage()
	{
		this.newm=false;
		return this.last;
	}
	/**
	 * Tests whether or not the last message sent has already been read.
	 * @return True if the message has not been read, false otherwise.
	 */
	public boolean isMessageNew()
	{
		return this.newm;
	}
	/**
	 * Tests if the last message is not null.
	 * @return True if the last message is not null, false if it is.
	 */
	public boolean hasLastMessage()
	{
		return this.last!=null;
	}
}