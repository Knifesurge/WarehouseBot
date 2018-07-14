package ca.hjalmionlabs.warehouse.entities;

import java.util.ArrayList;
import java.util.List;

import ca.hjalmionlabs.warehouse.WarehouseBot;
import net.dv8tion.jda.core.entities.Member;

public class Crate
{

	private String name;
	private long value;
	private Warehouse warehouse;
	private long ownerID;
	
	private static List<Crate> crateList;
	
	
	public Crate(String name)
	{
		this.name = name;
		this.value = 0;
		this.warehouse = null;
		this.ownerID = -1;
		crateList = new ArrayList<Crate>();
	}
	
	public Crate(String name, long value)
	{
		this.name = name;
		this.value = value;
		this.warehouse = null;
		this.ownerID = -1;
		crateList = new ArrayList<Crate>();
	}
	
	public Crate(String name, long value, Warehouse warehouse, long ID)
	{
		this.name = name;
		this.value = value;
		this.warehouse = warehouse;
		this.ownerID = ID;
		crateList = new ArrayList<Crate>();
	}
	
	public Crate(String name, long value, Warehouse warehouse, Member member)
	{
		this.name = name;
		this.value = value;
		this.warehouse = warehouse;
		this.ownerID = member.getUser().getIdLong();
		crateList = new ArrayList<Crate>();
	}

	public static List<Crate> populateCrates()
	{
		List<Crate> crates = new ArrayList<Crate>();
		crates.add(new Crate("Basic Crate", 100));
		crates.add(new Crate("Uncommon Crate", 200));
		crates.add(new Crate("Rare Crate", 500));
		crates.add(new Crate("Super Rare Crate", 750));
		crates.add(new Crate("Epic Crate", 1000));
		crates.add(new Crate("Legendary Crate", 1500));
		crates.add(new Crate("Mythical Crate", 2500));
		return crates;
	}
	
	public static List<Crate> getCrateList()
	{
		return crateList;
	}
	
	public static Crate getCrateByName(String name)
	{
		List<Crate> crate = new ArrayList<Crate>();
		crateList.forEach(e -> {
			if(e.getName().equals(name))
				crate.add(e);
		});
		return crate.get(0);
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
	
	@Override
	public String toString()
	{
		StringBuilder build = new StringBuilder();
		build.append("\nName: " + name);
		build.append("\nValue: " + value);
		build.append("\nWarehouse: " + warehouse.getName());
		build.append("\nOwner: " + WarehouseBot.getJDA().getUserById(ownerID).getName());
		return build.toString();
	}
	
}
