package ca.hjalmionlabs.warehouse.entities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

import ca.hjalmionlabs.warehouse.WarehouseBot;
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
	
	/**
	 * Creates a new Crate object. Warehouse defaults to the BotProfile's Warehouse. OwnerID defaults to -1
	 * @param name - Name of the Crate
	 * @param value - Value of the Crate
	 */
	public Crate(String name, long value)
	{
		this.name = name;
		this.value = value;
		this.warehouse = BotProfile.getWarehouse();
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

	public static List<Crate> getListOfCrates()
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
	
	public static Crate getCrateByName(String name) throws NoSuchElementException
	{
		String regex = "(?=\\p{Upper})";
		List<String> regName = Arrays.asList(name.split(regex));
		regName.forEach(e -> System.out.println(e));
		String crateToFind = String.join(" ", regName.get(0), regName.get(1));
		Set<Crate> crate = getListOfCrates().stream()
				 .filter(e -> e.getName().equals(crateToFind))
				 .collect(Collectors.toSet());
		Iterator<Crate> it = crate.iterator();
		if(it.hasNext())
			return it.next();
		else
			throw new NoSuchElementException("Could not find the crate " + crateToFind + "!");
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
	
	public void transferTo(Profile newOwner)
	{
		this.ownerID = newOwner.getID();
		Warehouse firstWarehouse = newOwner.getFirstAvailableWarehouse();
		this.warehouse = firstWarehouse;
		firstWarehouse.addCrates(Arrays.asList(this));
	}
	
	public String toString(boolean name, boolean value, boolean warehouse, boolean owner)
	{
		StringBuilder build = new StringBuilder();
		if(name)
			build.append("\nName: " + this.name);
		if(value)
			build.append("\nValue: " + this.value);
		if(warehouse)
			build.append("\nWarehouse: " + this.warehouse.getName());
		if(owner)
		{
			if(this.warehouse.equals(BotProfile.getWarehouse()))
				build.append("\nOwner: BotProfile");
			else
				build.append("\nOwner: " + WarehouseBot.getJDA().getUserById(ownerID).getName());
		}
		return build.toString();
	}
	
	@Override
	public String toString()
	{
		StringBuilder build = new StringBuilder();
		build.append("\nName: " + name);
		build.append("\nValue: " + value);
		build.append("\nWarehouse: " + warehouse.getName());
		if(warehouse.equals(BotProfile.getWarehouse()))
			build.append("\nOwner: BotProfile");
		else
			build.append("\nOwner: " + WarehouseBot.getJDA().getUserById(ownerID).getName());
		return build.toString();
	}
	
}
