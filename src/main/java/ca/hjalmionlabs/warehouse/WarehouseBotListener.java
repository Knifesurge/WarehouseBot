package ca.hjalmionlabs.warehouse;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.hjalmionlabs.warehouse.entities.Profile;
import ca.hjalmionlabs.warehouse.entities.Warehouse;
import ca.hjalmionlabs.warehouse.handlers.FileHandler;
import ca.hjalmionlabs.warehouse.handlers.JSONReader;
import ca.hjalmionlabs.warehouse.handlers.WarehouseHandler;
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
	
	private static final String PRECURSOR = "$";
	private Guild currentGuild;
	private List<Guild> guilds;
	private Map<Long, Profile> userProfiles = null;
	private static WarehouseHandler warehouseHandler;
	
	public WarehouseBotListener()
	{
		warehouseHandler = new WarehouseHandler();
		guilds = WarehouseBot.getGuilds();
		userProfiles = new HashMap<Long, Profile>();
		List<Profile> profiles = JSONReader.readJsonStream(WarehouseBotListener.class.getClassLoader().getResourceAsStream("dat\\profiles.dat"));
		profiles.forEach(e -> {	// Iterate over each Profile
			for(Guild g : guilds)	// Iterate over each Guild
			{
				for(Member m : g.getMembers())	// Iterate over each Member in the Guild
				{
					if(m.getUser().getIdLong() == e.getID())	// If IDs match b/w Member and Profile ID
						userProfiles.put(e.getID(), e);			// Map ID to Profile
				}
			}
		});
	}
	
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
	
	/**
	 * Checks if the User that sent this message is Knifesurge by checking the User's
	 * snowflake id (which is unique to every entity and will never change)
	 * @param e - MessageReceivedEvent in order to get the author
	 * @return true if User is Knifesurge, false otherwise
	 */
	private boolean isKnifesurge(MessageReceivedEvent e)
	{
		return e.getAuthor().getId().equals("205166483284819969");
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
				if(isKnifesurge(e))
				{
					FileHandler.writeFile("dat\\profiles.dat", userProfiles);
					WarehouseBot.getJDA().shutdown();
				}
			} else if(rawMsg.equals(PRECURSOR + "help"))
			{
				
			} else if(rawMsg.equals(PRECURSOR + "profile"))
			{
				Profile profile = userProfiles.get(e.getMember().getUser().getIdLong());
				EmbedBuilder build = new EmbedBuilder();
				build.setTitle(author.getName() + "'s Profile");
				build.setDescription(profile.toString());
				sendEmbedMessage(e, build);
			} else if(rawMsg.equals(PRECURSOR + "warehouse"))
			{
				List<Warehouse> userWarehouses = warehouseHandler.getUserWarehouseList(author);
				EmbedBuilder embed = new EmbedBuilder();
				embed.setDescription(author.getName() + "'s Warehouses\n-----------------------------------\n");
				userWarehouses.forEach(v -> {
					embed.appendDescription(v.getName() + "\n" + v.getUsage() + "/" + v.getCapacity() + "\n");
				});
				sendEmbedMessage(e, embed);
			} else if(rawMsg.equals(PRECURSOR + "genProfiles"))
			{
				if(userProfiles == null)
				{
					System.out.println("User Profiles is null!");
					userProfiles = new HashMap<Long, Profile>();
					for(Guild g : guilds)
					{
						for(Member m : g.getMembers())
						{
							userProfiles.put(m.getUser().getIdLong(), new Profile(m));
						}
					}
				}
			}
		}
	}
	
	public static WarehouseHandler getWarehouseHandler()
	{
		return warehouseHandler;
	}
	
}
