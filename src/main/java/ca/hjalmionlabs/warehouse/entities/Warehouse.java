package ca.hjalmionlabs.warehouse.entities;

import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.core.entities.User;

public class Warehouse
{

	private String name;		// The name of the warehouse
	private WarehouseSize size;	// Represents the size of the warehouse
	private int capacity;		// How much the warehouse can hold
	private int usage;			// How much the warehouse is currently holding
	private User owner;			// User that owns this Warehouse
	private List<Crate> crates;	// List of Crates currently in this Warehose
	
	public Warehouse(String name, WarehouseSize size, User user)
	{
		this.name = name;
		this.size = size;
		this.owner = user;
		this.capacity = calculateCapacity(size);
		this.usage = 0;
		crates = new ArrayList<Crate>(capacity);
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
		crates = new ArrayList<Crate>(capacity);
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
	
	@Override
	public String toString()
	{
		StringBuilder build = new StringBuilder();
		build.append(getName() + "\n" + getUsage() + "/" + getCapacity() + "\n=========================\n");
		crates.forEach(e -> {
			build.append(e.getName());
		});
		return build.toString();
	}
}
