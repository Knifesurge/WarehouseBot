package ca.hjalmionlabs.warehouse;

import java.util.List;

import ca.hjalmionlabs.warehouse.entities.Warehouse;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.EventListener;

public class WarehouseBotListener implements EventListener
{
	
	public static final String PRECURSOR = "$";
	public Guild currentGuild;
	
	public void sendMessage(MessageReceivedEvent event, String msg)
	{
		event.getChannel().sendMessage(msg).queue();
	}
	
	public void sendEmbedMessage(MessageReceivedEvent event, EmbedBuilder embed)
	{
		event.getChannel().sendMessage(embed.build()).queue();
	}
	
	private boolean checkPerms(MessageReceivedEvent event, String perm)
	{
		Guild guild = event.getGuild();
		Role role = guild.getRoleById(perm);
		if(role != null)
		{
			List<Member> membersInRole = guild.getMembersWithRoles(role);
			for(Member m : membersInRole)
			{
				if(m.getRoles().contains(role))
					return true;
			}
			return false;
		} else
		{
			System.err.println("Role: " + perm + " not found!");
			sendMessage(event, "Role: " + perm + " not found!");
			return false;
		}
	}
	
	@Override
	public void onEvent(Event event)
	{
		if(event instanceof MessageReceivedEvent)
		{
			MessageReceivedEvent e = (MessageReceivedEvent) event;
			User author = e.getAuthor();
			if(author.isBot() || author.isFake())
				return;
			currentGuild = e.getGuild();
			String rawMsg = e.getMessage().getContentRaw();
			System.out.println(currentGuild.getName() + ": " + author.getName() + ">> " + rawMsg);
			if(rawMsg.equals(PRECURSOR + "hello"))
			{
				sendMessage(e, "Hello, " + author.getAsMention() + "! I am WarehouseBot, nice to meet you! :^)");
				return;
			} else if(rawMsg.equals(PRECURSOR + "shutdown"))
			{
				if(checkPerms(e, "STAFF"))
				{
					WarehouseBot.getJDA().shutdown();
				}
			} else if(rawMsg.equals(PRECURSOR + "help"))
			{
				
			} else if(rawMsg.equals(PRECURSOR + "warehouse"))
			{
				List<Warehouse> userWarehouses = Warehouse.getUserWarehouseList(author);
				EmbedBuilder embed = new EmbedBuilder();
				embed.setDescription(author.getName() + "'s Warehouses\n-----------------------------------\n");
				userWarehouses.forEach(v -> {
					embed.appendDescription(v.getName() + "\n" + v.getUsage() + "/" + v.getCapacity() + "\n");
				});
				sendEmbedMessage(e, embed);
			}
		}
	}
	
}
