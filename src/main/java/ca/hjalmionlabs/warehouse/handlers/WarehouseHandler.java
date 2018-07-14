package ca.hjalmionlabs.warehouse.handlers;

import java.util.ArrayList;
import java.util.List;

import ca.hjalmionlabs.warehouse.entities.Warehouse;
import net.dv8tion.jda.core.entities.User;

public class WarehouseHandler
{

	private List<Warehouse> allWarehouses;
	
	public WarehouseHandler()
	{
		allWarehouses = new ArrayList<Warehouse>();
	}
	
	public Warehouse getWarehouse(String name)
	{
		for(Warehouse w : allWarehouses)
		{
			if(w.getName().equals(name))
				return w;
		}
		return null;
	}
	
	public Warehouse getWarehouseByName(String name)
	{
		for(Warehouse w : allWarehouses)
		{
			if(w.getName().equals(name))
				return w;
		}
		return null;
	}
	
	public List<Warehouse> getUserWarehouseList(User user)
	{
		List<Warehouse> list = new ArrayList<Warehouse>();
		allWarehouses.forEach(e -> {
			if(e.getOwner().getIdLong() == user.getIdLong())
				list.add(e);
		});
		
		return list;
	}
	
	public void addWarehouse(Warehouse w)
	{
		allWarehouses.add(w);
	}
}
