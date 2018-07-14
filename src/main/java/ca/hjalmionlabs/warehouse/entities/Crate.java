package ca.hjalmionlabs.warehouse.entities;

import net.dv8tion.jda.core.entities.Member;

public class Crate
{

	private String name;
	private long value;
	private Warehouse warehouse;
	private long ownerID;
	
	public Crate(String name)
	{
		this.name = name;
		this.value = 0;
		this.warehouse = null;
		this.ownerID = -1;
	}
	
	public Crate(String name, long value, Warehouse warehouse, long ID)
	{
		this.name = name;
		this.value = value;
		this.warehouse = warehouse;
		this.ownerID = ID;
	}
	
	public Crate(String name, long value, Warehouse warehouse, Member member)
	{
		this.name = name;
		this.value = value;
		this.warehouse = warehouse;
		this.ownerID = member.getUser().getIdLong();
	}

	public String getName()
	{
		return name;
	}

	public long getValue()
	{
		return value;
	}
	
	public Warehouse getWarehouse()
	{
		return warehouse;
	}
	
	public long getOwnerID()
	{
		return ownerID;
	}
	
}
