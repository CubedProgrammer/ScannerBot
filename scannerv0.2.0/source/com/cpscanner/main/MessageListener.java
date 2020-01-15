package com.cpscanner.main;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.*;
public class MessageListener extends ListenerAdapter
{
	private Message last;
	private boolean newm;
	public MessageListener()
	{
	}
	public void onMessageReceived(MessageReceivedEvent evt)
	{
		if(evt.getAuthor().getIdLong()!=ScannerV0_2_0.ID)
		{
			this.last=evt.getMessage();
			this.newm=true;
		}
	}
	public Message getLastMessage()
	{
		this.newm=false;
		return this.last;
	}
	public boolean isMessageNew()
	{
		return this.newm;
	}
	public boolean hasLastMessage()
	{
		return this.last!=null;
	}
}