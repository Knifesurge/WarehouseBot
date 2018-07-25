package ca.hjalmionlabs.warehouse.entities;

public enum WarehouseSize
{

	UNDEFINED("UNDEFINED"), SMALL("SMALL"), MEDIUM("MEDIUM"), LARGE("LARGE"), INFINITE("INFINITE");
	
	String size;
	
	WarehouseSize(String size)
	{
		this.size = size;
	}
	
	public static WarehouseSize parseSize(String size)
	{
		if(size.equals("SMALL"))
			return SMALL;
		else if(size.equals("MEDIUM"))
			return MEDIUM;
		else if(size.equals("LARGE"))
			return LARGE;
		else if(size.equals("INFINITE"))
			return INFINITE;
		else
			return UNDEFINED;
	}
	
	@Override
	public String toString()
	{
		return this.size;
	}
}
