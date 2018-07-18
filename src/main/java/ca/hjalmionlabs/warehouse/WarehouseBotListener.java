package ca.hjalmionlabs.warehouse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.hjalmionlabs.warehouse.entities.Crate;
import ca.hjalmionlabs.warehouse.entities.Profile;
import ca.hjalmionlabs.warehouse.entities.Warehouse;
import ca.hjalmionlabs.warehouse.entities.WarehouseSize;
import ca.hjalmionlabs.warehouse.handlers.JSONReader;
import ca.hjalmionlabs.warehouse.handlers.JSONWriter;
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
		List<Profile> profiles = new ArrayList<Profile>();
		try {
			profiles = JSONReader.readJsonStream(Files.newInputStream(Paths.get("dat\\profiles.dat")));
			profiles.forEach(e -> {
				System.out.println(e);
				userProfiles.put(e.getID(), e);
				e.getWarehouses().forEach(v -> {
					warehouseHandler.addWarehouse(v);
				});
			});
			/* Check profile list and if a profile for a user is not present, create one */
			for(Guild g : guilds)
			{
				for(Member m : g.getMembers())
				{
					User user = m.getUser();
					long key = user.getIdLong();
					if(!userProfiles.containsKey(key))
						userProfiles.put(key, new Profile(m));
				}
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		Crate.getCrateList().addAll(Crate.populateCrates());
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
//					FileHandler.writeFile("dat\\profiles.dat", userProfiles);
					WarehouseBot.getJDA().shutdown();
				}
			} else if(rawMsg.equals(PRECURSOR + "help"))
			{
				
			} else if(rawMsg.equals(PRECURSOR + "profile"))
			{
				Profile profile = userProfiles.get(author.getIdLong());
				System.out.println("DEBUG: " + profile);
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
			} else if(rawMsg.equals(PRECURSOR + "getAllProfiles"))
			{
				EmbedBuilder embed = new EmbedBuilder();
				embed.setTitle("User Profiles\n-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=");
				for(Guild g : guilds)
				{
					embed.appendDescription("\n-------------------------");
					embed.appendDescription("\n" + g.getName() + "\n-------------------------");
					for(Member m : g.getMembers())
					{
/*						if(embed.length() <= 2000)
						{
							embed.appendDescription("\n=========================");
							embed.appendDescription("\n" + m.getUser().getName() + "#" + m.getUser().getDiscriminator() + "\n=========================");
							embed.appendDescription("\n" + userProfiles.get(m.getUser().getIdLong()).toString());
							embed.appendDescription("\n=========================");
						} else	//embed.length() >= 2000
						{
							sendEmbedMessage(e, embed);
							embed = new EmbedBuilder();
							embed.setTitle("User Profiles Cont'd... \n-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=");
						}
*/						
						/**************** OR *******************/
						Profile profile = userProfiles.get(m.getUser().getIdLong());
						if(profile.getMoney() != 0 && !profile.getWarehouses().isEmpty())	// Profile used
						{
							embed.appendDescription("\n=========================");
							embed.appendDescription("\n" + m.getUser().getName() + "#" + m.getUser().getDiscriminator() + "\n=========================");
							embed.appendDescription("\n" + userProfiles.get(m.getUser().getIdLong()).toString());
							embed.appendDescription("\n=========================");
						}
					}
					embed.appendDescription("\n-------------------------");
				}
				sendEmbedMessage(e, embed);
			} else if(rawMsg.equals(PRECURSOR + "writeTest"))
			{
				Profile profile1 = new Profile(12345678910111213L, 6969L);	// Create bogus profile
				Profile profile2 = new Profile(987654321L, 314159L);	// Create another bogus profile
				// Print the profiles out
				System.out.println(profile1);
				System.out.println(profile2);
				if(JSONWriter.writeToJSON(Arrays.asList(profile1, profile2), Paths.get("dat\\profilesTest.dat").toFile()))
				{
					try {
						List<Profile> profiles = JSONReader.readJsonStream(Files.newInputStream(Paths.get("dat\\profilesTest.dat")));
						String combinedProfiles = profiles.get(0).toString() + "\n-=-=-=-=-=-=-=-=-=-=-\n" + profiles.get(1).toString();
						sendMessage(e, combinedProfiles);
						sendMessage(e, combinedProfiles);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				} else
				{
					sendMessage(e, "Write test failed - Unable to write to file!");
				}
				
			} else if(rawMsg.equals(PRECURSOR + "createNewProfile"))
			{
				Profile profile = new Profile(author.getIdLong());
				profile.setMoney(1500);
				profile.addWarehouse(new Warehouse(WarehouseSize.SMALL, author));
				userProfiles.put(author.getIdLong(), profile);
				
				if(JSONWriter.writeToJSON(Arrays.asList(profile), Paths.get("dat\\profilesTest.dat").toFile()))
					sendMessage(e, "Write successful!");
				else
					sendMessage(e, "Unable to write!");
				
				EmbedBuilder embed = new EmbedBuilder();
				embed.setTitle("New Profile Created!");
				embed.appendDescription("\n" + profile);
				sendEmbedMessage(e, embed);
			} else if(rawMsg.equals(PRECURSOR + "crates"))
			{
				List<Crate> userCrates = userProfiles.get(author.getIdLong()).getCratesAsList();
				EmbedBuilder embed = new EmbedBuilder();
				embed.setTitle(author.getName() + "'s Crates");
				userCrates.forEach(c -> {
					embed.appendDescription(c.toString());
				});
				sendEmbedMessage(e, embed);
			} else if(rawMsg.startsWith(PRECURSOR + "buy"))
			{
				String wholeMsg = rawMsg;
				List<String> pieces = Arrays.asList(wholeMsg.split(" "));
				String buying = pieces.get(0);	// Crates or a Warehouse?
				String name = pieces.get(1);	// Name of crate or warehouse
				String amt = pieces.get(2);		// Amount User is buying
				if(buying.equals("crates"))
				{
					Crate toBuy = Crate.getCrateByName(name);
					
				} else if(buying.equals("warehouse"))
				{
					
				} else
				{
					sendMessage(e, "Invalid argument!");	// DEBUG
				}
			}
		}
	}
	
	public static WarehouseHandler getWarehouseHandler()
	{
		return warehouseHandler;
	}
	
}
