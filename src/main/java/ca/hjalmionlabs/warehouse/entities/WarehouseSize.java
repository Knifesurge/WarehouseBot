package ca.hjalmionlabs.warehouse.entities;

public enum WarehouseSize
{

	UNDEFINED("UNDEFINED"), SMALL("SMALL"), MEDIUM("MEDIUM"), LARGE("LARGE");
	
	String size;
	
	WarehouseSize(String size)
	{
		this.size = size;
	}
	
	public static WarehouseSize parseSize(String size)
	{
		// DEBUG
		System.out.println("untrimmed: " + size);
		size = size.trim();
		System.out.println("trimmed: " + size);
		if(size.equals("SMALL"))
			return SMALL;
		else if(size.equals("MEDIUM"))
			return MEDIUM;
		else if(size.equals("LARGE"))
			return LARGE;
		else
			return UNDEFINED;
	}
	
	@Override
	public String toString()
	{
		return this.size;
	}
}
