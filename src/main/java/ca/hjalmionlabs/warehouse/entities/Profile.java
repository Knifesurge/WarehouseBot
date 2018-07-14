package ca.hjalmionlabs.warehouse.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.dv8tion.jda.core.entities.Member;

public final class Profile implements Serializable
{
	private static final long serialVersionUID = 7414311522111652829L;	
	
	private List<Warehouse> warehouses;
	private Map<List<Crate>, Warehouse> crates;
	private long money;
	private long memberID;
	
	public Profile(long id)
	{
		this.memberID = id;
		warehouses = new ArrayList<Warehouse>();
		crates = new HashMap<List<Crate>, Warehouse>();
		money = 0;
	}
	
	public Profile(Member member)
	{
		this.memberID = member.getUser().getIdLong();
		warehouses = new ArrayList<Warehouse>();
		crates = new HashMap<List<Crate>, Warehouse>();
		money = 0;
	}
	
	public Profile(long id, long money)
	{
		this.memberID = -1L;
		warehouses = new ArrayList<Warehouse>();
		crates = new HashMap<List<Crate>, Warehouse>();
		this.money = money;
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
	
	public List<Warehouse> getWarehouses()
	{
		return warehouses;
	}
	
	public Map<List<Crate>, Warehouse> getCrates()
	{
		return crates;
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
		build.append("Money: " + money);
		build.append("Warehouses: ");
		warehouses.forEach(e -> {
			build.append(e);
		});
		return build.toString();
	}
}
