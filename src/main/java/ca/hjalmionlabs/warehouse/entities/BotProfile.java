package ca.hjalmionlabs.warehouse.entities;

import ca.hjalmionlabs.warehouse.WarehouseBot;

public class BotProfile
{

	private static Warehouse warehouse;
	private long money;
	
	public BotProfile()
	{
		warehouse = new Warehouse("Bot Warehouse", WarehouseSize.INFINITE, WarehouseBot.getJDA().getSelfUser());
		money = 999999;
	}
	
	
	public static Warehouse getWarehouse()
	{
		return warehouse;
	}
}
