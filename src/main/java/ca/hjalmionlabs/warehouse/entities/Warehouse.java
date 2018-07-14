package ca.hjalmionlabs.warehouse.entities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ca.hjalmionlabs.warehouse.readers.FileReader;
import net.dv8tion.jda.core.entities.User;

public class Warehouse
{

	private String name;		// The name of the warehouse
	private WarehouseSize size;	// Represents the size of the warehouse
	private int capacity;		// How much the warehouse can hold
	private int usage;			// How much the warehouse is currently holding
	private User owner;
	
	public Warehouse(String name, WarehouseSize size, User user)
	{
		this.name = name;
		this.size = size;
		this.owner = user;
		this.capacity = calculateCapacity(size);
		this.usage = 0;
	}
	
	public Warehouse(WarehouseSize size, User user)
	{
		if(size.toString().equals("SMALL")) 		this.name = "Basic Warehouse";
		else if(size.toString().equals("MEDIUM")) 	this.name = "Medium Warehouse";
		else 										this.name = "Large Warehouse";
		
		this.size = size;
		this.owner = user;
		this.capacity = calculateCapacity(size);
		this.usage = 0;
	}
	
	
	private int calculateCapacity(WarehouseSize size)
	{
		switch(size)
		{
			case SMALL:
				return 16;
			case MEDIUM:
				return 32;
			case LARGE:
				return 64;
			default:
				return -1;
		}
	}
	public static List<Warehouse> getUserWarehouseList(User user)
	{
		List<Warehouse> warehouses = new ArrayList<Warehouse>();
		List<String> list = FileReader.readFile("dat\\warehouses\\" + user.getId());
		list.forEach(e -> {
			warehouses.add(parseWarehouse(e, user)); 
		});
		return warehouses;
	}
	
	private static Warehouse parseWarehouse(String house, User user)
	{
		List<String> warehouse = Arrays.asList(house.split(";"));	// Split the String we are parsing into chunks, separated by ';'
		String name = warehouse.get(0);
		WarehouseSize size = WarehouseSize.parseSize(warehouse.get(1));
		User owner = user;
		return new Warehouse(name, size, owner);
	}
	
	public String getName()
	{
		return name;
	}
	
	public WarehouseSize getSize()
	{
		return size;
	}
	
	public int getUsage()
	{
		return usage;
	}
	
	public int getCapacity()
	{
		return capacity;
	}
	public User getOwner()
	{
		return owner;
	}
}
