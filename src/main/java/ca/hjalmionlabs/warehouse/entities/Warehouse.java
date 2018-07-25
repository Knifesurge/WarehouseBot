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
	private List<Crate> crates;	// List of Crates currently in this Warehouse
	
	public Warehouse()
	{
		this.name = "NULL";
		this.size = WarehouseSize.UNDEFINED;
		this.owner = null;
		this.capacity = 0;
		this.usage = 0;
		crates = new ArrayList<Crate>();
	}
	
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
		this.size = size;
		
		if(size.toString().trim().equals("SMALL")) 		this.name = "Basic Warehouse";
		else if(size.toString().trim().equals("MEDIUM")) 	this.name = "Medium Warehouse";
		else 										this.name = "Large Warehouse";
		
		this.owner = user;
		this.capacity = calculateCapacity(size);
		this.usage = 0;
		crates = new ArrayList<Crate>(capacity);
	}
	
	public Warehouse(String name, WarehouseSize size, User owner, List<Crate> crates)
	{
		this.name = name;
		this.size = size;
		this.capacity = calculateCapacity(size);
		this.usage = crates.size();
		this.owner = owner;
		this.crates = crates;
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
			case INFINITE:
				return 1_000_000;
			default:
				return -1;
		}
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public WarehouseSize getSize()
	{
		return size;
	}
	
	public void setSize(WarehouseSize size)
	{
		this.size = size;
		this.capacity = calculateCapacity(this.size);
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
	
	public void setOwner(User owner)
	{
		this.owner = owner;
	}
	
	public void addCrates(List<Crate> crates) throws IllegalStateException
	{
		int newUsage = this.usage + crates.size();
		if(newUsage > this.capacity)
			throw new IllegalStateException(); 
		else
		{
			this.crates.addAll(crates);
			this.usage += crates.size();
		}
	}
	
	public List<Crate> getCrates()
	{
		return crates;
	}
	
	@Override
	public String toString()
	{
		StringBuilder build = new StringBuilder();
		build.append(getName() + "\n" + getSize() + "\n" + getUsage() + "/" + getCapacity() + "\n=========================\n");
		crates.forEach(e -> {
			build.append(e.getName());
		});
		return build.toString();
	}
}
