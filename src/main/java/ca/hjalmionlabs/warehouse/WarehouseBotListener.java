package ca.hjalmionlabs.warehouse;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import ca.hjalmionlabs.warehouse.entities.BotProfile;
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
	private static BotProfile botProfile;
	
	public WarehouseBotListener()
	{
		warehouseHandler = new WarehouseHandler();
		
		botProfile = new BotProfile();
		
		guilds = WarehouseBot.getGuilds();
		userProfiles = new HashMap<Long, Profile>();
		List<Profile> profiles = new ArrayList<Profile>();
		try {
			if(System.getProperty("os.name").toLowerCase().contains("win"))
				profiles = JSONReader.readJsonStream(Files.newInputStream(Paths.get("dat\\profiles.dat")));
			else
				profiles = JSONReader.readJsonStream(Files.newInputStream(Paths.get("dat/profiles.dat")));
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
		System.out.println("List returned by Crate#populateCrates(): ");
		Crate.getListOfCrates().forEach(e -> System.out.println(e));
	}
	
	public void sendMessage(MessageReceivedEvent event, String msg)
	{
		event.getChannel().sendMessage(msg).queue();
	}
	
	public void sendEmbedMessage(MessageReceivedEvent event, EmbedBuilder embed)
	{
		event.getChannel().sendMessage(embed.build()).queue();
	}
	
	private String executeCommand(String cmd, boolean wait, boolean output)
	{
		StringBuffer strbuff = new StringBuffer();
		Process p;

		try
		{
			p = Runtime.getRuntime().exec(cmd);
			if(wait)
				p.waitFor();
			if(output)
			{
				BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
				String line = "";
				while((line = reader.readLine()) != null)
				{
					strbuff.append(line + "\n");
				}
			}
		} catch(Exception e)
		{
			e.printStackTrace();
		}
		return strbuff.toString();
	}
/*	private boolean checkPerms(MessageReceivedEvent event, String perm)
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
*/	
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
			} else if(rawMsg.equals(PRECURSOR + "restart"))
			{
				if(isKnifesurge(e))
				{
					String command = "sudo bash run.sh";
					executeCommand(command, false, false);
					sendMessage(e, "Restarting!");
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
						profiles.forEach(p ->{
							sendMessage(e, p.toString());
						});
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
			} else if(rawMsg.equals(PRECURSOR + "buy"))	// Buying but with no arguments
			{
				EmbedBuilder build = new EmbedBuilder();
				build.setTitle("Buy Command Arguments");
				build.setDescription("Command Usage:\n\n$buy (crate/warehouse/c/w) (name) [amt]\n");
				build.appendDescription("crate/warehouse/w/c \t- Buy a crate or a warehouse\n");
				build.appendDescription("name \t- Name of the crate or warehouse to buy (can be found" +
												 " by using the $listCrates or $listWarehouses commands\n");
				build.appendDescription("amt \t- Number of crates that you want to buy.\n\n");
				build.appendDescription("Examples:\n");
				build.appendDescription("$buy crate 'BasicCrate' 5\n");
				build.appendDescription("$buy c 'BobCrate' 69\n");
				build.appendDescription("$buy warehouse 'MediumWarehouse' 2\n");
				build.appendDescription("$buy w 'LargeWarehouse'\t- If amt is left out, defaults to 1\n");
				sendEmbedMessage(e, build);
			} else if(rawMsg.startsWith(PRECURSOR + "buy"))	// Buying but with actual arguments
			{
				User user = e.getAuthor();
				Profile profile = userProfiles.get(user.getIdLong());
				String wholeMsg = rawMsg;
				List<String> pieces = Arrays.asList(wholeMsg.split(" "));
				pieces.forEach(w -> w = w.trim());
				String buying = pieces.get(1);	// Crates or a Warehouse?
				String name = pieces.get(2);	// Name of crate or warehouse
				String amt;
				if(pieces.size() == 4)	// User entered a custom amount to buy
					amt = pieces.get(3);		// Amount User is buying
				else					// Default to 1
					amt = "1";
				pieces.forEach(p -> System.out.println(p));
				if(buying.equals("crates") || buying.equals("c"))
				{
					Crate toBuy = null;
					try
					{
						toBuy = Crate.getCrateByName(name);
					} catch(NoSuchElementException nse)
					{
						System.err.println(nse.getMessage());
						nse.printStackTrace();
						sendMessage(e, "Unable to find the crate " + name);
					}

					int cost = (int) (toBuy.getValue() * Integer.parseInt(amt));
					
					if(profile.getMoney() >= cost && profile.hasWarehouseSpace())
					{
						toBuy.transferTo(profile);
						sendMessage(e, "Purchase successful! Enjoy your new " + toBuy.getName() + "!");
					} else if(profile.getMoney() >= cost && !profile.hasWarehouseSpace())
					{
						sendMessage(e, "Enough money, but not enough warehouse space!");
						return;
					} else if(!(profile.getMoney() >= cost) && profile.hasWarehouseSpace())
					{
						sendMessage(e, "Enough warehouse space, but not enough money!");
						return;
					} else if(!(profile.getMoney() >= cost) && !profile.hasWarehouseSpace())
					{
						sendMessage(e, "Not enough money or warehouse space!");
						return;
					} else
					{
						sendMessage(e, "An error occurred during the transactions, please notify an administrator");
						return;
					}
					
				} else if(buying.equals("warehouse") || buying.equals("w"))
				{
					
				} else
				{
					sendMessage(e, "Invalid argument!");	// DEBUG
				}
			} else if(rawMsg.equals(PRECURSOR + "crateList"))
			{
				EmbedBuilder embed = new EmbedBuilder();
				embed.setTitle("List of Currently Crates");
				Crate.getListOfCrates().forEach(c -> embed.appendDescription(c.toString(true, true, false, false)));
				sendEmbedMessage(e, embed);
			}
		}
	}
	
	public static WarehouseHandler getWarehouseHandler()
	{
		return warehouseHandler;
	}
	
}
