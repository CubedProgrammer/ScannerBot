package com.cpscanner.main;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.*;
public class MessageListener extends ListenerAdapter
{
	public MessageListener()
	{
	}
	public void onMessageReceived(MessageReceivedEvent evt)
	{
		if(evt.getAuthor().getIdLong()!=ScannerV0_2_0.ID)
		{
			evt.getChannel().sendMessage("hello").queue();
		}
	}
}