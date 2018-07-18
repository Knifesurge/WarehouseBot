package ca.hjalmionlabs.warehouse.handlers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;

import ca.hjalmionlabs.warehouse.entities.Profile;

public class JSONWriter
{

	private static FileWriter output;
	private static Gson gson;
	private static JsonWriter writer;
	
	public static boolean writeToJSON(List<Profile> list, File file)
	{
		gson = new Gson();
		try
		{
			output = new FileWriter(file);
			
			if(!file.exists())
				file.createNewFile();
			
			writer = gson.newJsonWriter(output);
		
			writer.beginArray();	// Begin the Profile.dat array
			
			list.forEach(e -> {
				
				// DEBUG
				System.out.println(e);
				
				try
				{
					writer.beginObject();	// Begin Profile object
					writer.name("id").value(e.getID());
					writer.name("money").value(e.getMoney());
					writer.name("warehouses");
					writer.beginArray();	// Warehouses
					e.getWarehouses().forEach(w -> {						
						try {
							writer.beginObject();
							writer.name("name").value(w.getName());
							writer.name("size").value(w.getSize().toString());
							writer.name("owner").value(w.getOwner().getIdLong());
							writer.beginArray();	// Crates
							w.getCrates().forEach(c -> {
								try
								{
									writer.beginObject();
									writer.name("name").value(c.getName());
									writer.name("value").value(c.getValue());
									writer.name("warehouse").value(c.getWarehouse().getName());
									writer.name("owner").value(c.getOwnerID());
									writer.endObject();	// End the current crate
								} catch(IOException ioe)
								{
									ioe.printStackTrace();
								}
							});	// End of crates
							writer.endArray();	// END OF CRATES ARRAY
						} catch (IOException ioe) {
							ioe.printStackTrace();
						}
						try {
							writer.endObject();	// End the current warehouse
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					});	// End of warehouses
					writer.endArray();	// END OF WAREHOUSES ARRAY
				} catch(IOException ioe)
				{
					ioe.printStackTrace();
				}
				try {
					writer.endObject();
				} catch (IOException e1) {
					e1.printStackTrace();
				}	// End of current Profile
			});	// End of Profiles
			writer.endArray();	// End of Profiles.dat array
		} catch(IOException ioe)
		{
			ioe.printStackTrace();
			return false;
		}
		
		return true;
	}
}
