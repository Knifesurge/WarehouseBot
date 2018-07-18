package ca.hjalmionlabs.warehouse.handlers;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import ca.hjalmionlabs.warehouse.WarehouseBot;
import ca.hjalmionlabs.warehouse.WarehouseBotListener;
import ca.hjalmionlabs.warehouse.entities.Crate;
import ca.hjalmionlabs.warehouse.entities.Profile;
import ca.hjalmionlabs.warehouse.entities.Warehouse;
import ca.hjalmionlabs.warehouse.entities.WarehouseSize;
import net.dv8tion.jda.core.entities.User;

public class JSONReader
{
	
	/**
	 * Reads a JSON file as a Stream.
	 * @param in
	 * @return
	 */
	public static List<Profile> readJsonStream(InputStream in)
	{
		try {
			JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
			reader.setLenient(true);
			
			return readObjectArray(reader);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Reads a JSON Array Object and all JSON Item Objects inside of the array
	 * @param reader
	 * @return
	 */
	public static List<Profile> readObjectArray(JsonReader reader)
	{
		List<Profile> list = new ArrayList<Profile>();
		
		try {
			reader.beginArray();
			while(reader.hasNext())
				list.add(readProfile(reader));
			reader.endArray();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	public static List<Warehouse> readWarehouseArray(JsonReader reader)
	{
		List<Warehouse> list = new ArrayList<Warehouse>();
		
		try
		{
			reader.beginArray();
			while(reader.hasNext())
				list.add(readWarehouse(reader));
			reader.endArray();
		} catch(IOException ioe)
		{
			ioe.printStackTrace();
		}
		return list;
	}
	
	public static Warehouse readWarehouse(JsonReader reader)
	{
		String name = "";
		WarehouseSize size = WarehouseSize.UNDEFINED;
		List<Crate> crates = new ArrayList<Crate>();
		User owner = null;
		Warehouse warehouse = new Warehouse();	// Create an empty Warehouse
		try
		{
			reader.beginObject();
			while(reader.hasNext())
			{
				String objName = reader.nextName();
				if(objName.equals("name"))
					name = reader.nextString();
				else if(objName.equals("size"))
				{
					String nextString = reader.nextString();
					size = WarehouseSize.parseSize(nextString);
					System.out.println("SIZE: " + nextString);
				}
				else if(objName.equals("owner"))
				{
					owner = WarehouseBot.getJDA().getUserById(Long.parseLong(reader.nextString()));
				} else if(objName.equals("crates"))
				{
					warehouse = new Warehouse(name, size, owner);
					crates = readCratesArray(reader, warehouse);
				}
			}
			reader.endObject();
		} catch(IOException ioe)
		{
			ioe.printStackTrace();
		}
		warehouse.setName(name);
		warehouse.setSize(size);
		warehouse.setOwner(owner);
		warehouse.addCrates(crates);
		// Add this Warehouse to the WarehouseHandler for global registration
		WarehouseBotListener.getWarehouseHandler().addWarehouse(warehouse);
		return warehouse;
	}
	
	public static List<Crate> readCratesArray(JsonReader reader, Warehouse warehouse)
	{
		List<Crate> crates = new ArrayList<Crate>();
		
		try
		{
			reader.beginArray();
			while(reader.hasNext())
				crates.add(readCrate(reader, warehouse));
			reader.endArray();
		} catch(IOException ioe)
		{
			ioe.printStackTrace();
		}
		return crates;
	}
	
	public static Crate readCrate(JsonReader reader, Warehouse warehouse)
	{
		String name = "";
		long value = 0L;
		long ownerID = -1L;
		
		try
		{
			reader.beginObject();
			while(reader.hasNext())
			{
				String key = reader.nextName();
				if(key.equals("name"))
					name = reader.nextString();
				else if(key.equals("value"))
					value = Long.parseLong(reader.nextString());
				else if(key.equals("owner"))
					ownerID = Long.parseLong(reader.nextString());
				else
					reader.skipValue();
				
			}
			reader.endObject();
		} catch(IOException ioe)
		{
			ioe.printStackTrace();
		}
		
		return new Crate(name, value, warehouse, ownerID);
	}
	
	/**
	 * Reads a Profile in JSON format and returns a new Profile Object that represents the 
	 * data in JSON format
	 * @param reader
	 * @return
	 */
	public static Profile readProfile(JsonReader reader)
	{
		long id = -1L;
		List<Warehouse> warehouses = new ArrayList<Warehouse>();
		Map<Warehouse, List<Crate>> crates = new HashMap<Warehouse, List<Crate>>();
		long money = 0L;
		try
		{
			reader.beginObject();
			while(reader.hasNext())
			{
				String name = reader.nextName();
				if(name.equals("id"))
				{
					id = Long.parseLong(reader.nextString());
				} else if(name.equals("money"))
				{
					money = Long.parseLong(reader.nextString());
				} else if(name.equals("warehouses"))
				{
					warehouses = readWarehouseArray(reader);
				} else
				{
					reader.skipValue();
				}
			}
			reader.endObject();
			
			warehouses.forEach(e -> {
				crates.put(e, e.getCrates());
			});
		} catch(IOException ioe)
		{
			ioe.printStackTrace();
		}
		
		return new Profile(id, money, warehouses, crates);
	}
	
}
