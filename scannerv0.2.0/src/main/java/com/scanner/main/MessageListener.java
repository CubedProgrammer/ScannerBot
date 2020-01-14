package com.scanner.main;
import java.util.Iterator;
import java.util.List;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
public class MessageListener extends ListenerAdapter {
	
	private String last;
	private long lastID;
	private long userID;
	private long channelID;
	private long guildID;
	
	public MessageListener() {
		this.last="";
	}
	
	public void onMessageReceived(MessageReceivedEvent event) {
		
		User author=event.getAuthor();
		Message message=event.getMessage();
		MessageChannel channel=event.getChannel();
		Guild guild=event.getGuild();
		
		this.lastID=message.getIdLong();
		this.channelID=channel.getIdLong();
		this.userID=author.getIdLong();
		this.guildID=guild.getIdLong();
		this.last=message.getContentRaw();
		
		if(OtherMethods.getAutoRole()!=0) {
			
			List<Member>members=guild.getMembers();
			Iterator<Member>itmb=members.iterator();
			Member m=null;
			
			while(itmb.hasNext()) {
				
				m=itmb.next();
				
				if(!m.getRoles().contains(guild.getRoleById(OtherMethods.getAutoRole()))) {
					guild.getController().addSingleRoleToMember(m,guild.getRoleById(OtherMethods.getAutoRole()));
				}
				
			}
			
		}
		
		OtherMethods.logdat(Long.toString(this.guildID)+" "+guild.getName()+" "+Long.toString(this.channelID)+" "+channel.getName()+" "+Long.toString(this.userID)+" "+author.getName()+":"+this.last);
		
	}
	
	public String getLastMessage() {
		return this.last;
	}
	
	public long getLastMessageId() {
		return this.lastID;
	}
	
	public long getLastChannelId() {
		return this.channelID;
	}
	
	public long getLastUserId() {
		return this.userID;
	}
	
	public long getLastGuildId() {
		return this.guildID;
	}
	
	public void setLastMessage(String last) {
		this.last=last;
	}
	
}