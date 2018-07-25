package ca.hjalmionlabs.warehouse;

import java.util.List;

import javax.security.auth.login.LoginException;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Game.GameType;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.managers.Presence;

public class WarehouseBot
{
	private static JDA jda;
	private static String BOT_TOKEN;
	
	public static void main(String[] args)
	{
			BOT_TOKEN = args[0];
			try {
				jda = new JDABuilder(AccountType.BOT).setToken(BOT_TOKEN).buildBlocking();
			} catch (LoginException | InterruptedException e) {
				e.printStackTrace();
			}
			jda.addEventListener(new WarehouseBotListener());
			Presence game = jda.getPresence();
			game.setGame(Game.of(GameType.DEFAULT, "$help"));
	}
	
	public static JDA getJDA()
	{
		return jda;
	}
	
	public static List<Guild> getGuilds()
	{
		return jda.getGuilds();
	}
}
