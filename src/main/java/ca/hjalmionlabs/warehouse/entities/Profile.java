package ca.hjalmionlabs.warehouse.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.hjalmionlabs.warehouse.WarehouseBot;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;

public final class Profile implements Serializable
{
	private static final long serialVersionUID = 7414311522111652829L;	
	
	private List<Warehouse> warehouses;
	private Map<Warehouse, List<Crate>> crates;
	private long money;
	private long memberID;
	
	public Profile(long id)
	{
		this.memberID = id;
		warehouses = new ArrayList<Warehouse>();
		crates = new HashMap<Warehouse, List<Crate>>();
		money = 0;
	}
	
	public Profile(Member member)
	{
		this.memberID = member.getUser().getIdLong();
		warehouses = new ArrayList<Warehouse>();
		crates = new HashMap<Warehouse, List<Crate>>();
		money = 0;
	}
	
	public Profile(long id, long money)
	{
		this.memberID = id;
		warehouses = new ArrayList<Warehouse>();
		crates = new HashMap<Warehouse, List<Crate>>();
		this.money = money;
	}
	
	public Profile(long id, long money, List<Warehouse> warehouses, Map<Warehouse, List<Crate>> crates)
	{
		this.memberID = id;
		this.money = money;
		this.warehouses = warehouses;
		this.crates = crates;
	}
	
	public void setMoney(long amt)
	{
		money = amt;
	}
	
	public void addMoney(long amt)
	{
		money += amt;
	}
	
	public boolean spendMoney(long amt)
	{
		if(money - amt > 0)
		{
			money -= amt;
			return true;
		} else
		{
			System.out.println("Not enough money to spend!");
			return false;
		}
	}
	
	public void addWarehouse(Warehouse w)
	{
		warehouses.add(w);
	}
	
	public List<Warehouse> getWarehouses()
	{
		return warehouses;
	}
	
	public Map<Warehouse, List<Crate>> getCrates()
	{
		return crates;
	}
	
	public List<Crate> getCratesAsList()
	{
		List<Crate> allCrates = new ArrayList<Crate>();
		crates.forEach((key, val) -> {
			allCrates.addAll(crates.get(key));
		});
		return allCrates;
	}
	
	public long getID()
	{
		return memberID;
	}
	
	public long getMoney()
	{
		return money;
	}
	
	@Override
	public String toString()
	{
		StringBuilder build = new StringBuilder();
		User user = WarehouseBot.getJDA().getUserById(memberID);
		build.append("\n" + user.getName() + "#" + user.getDiscriminator());
		build.append("\nMoney: " + money);
		build.append("\nWarehouses: ");
		if(!warehouses.isEmpty())
		{
			warehouses.forEach(e -> {
				build.append(e);
			});
		} else
		{
			build.append("No Warehouses!");
		}
		return build.toString();
	}
}
