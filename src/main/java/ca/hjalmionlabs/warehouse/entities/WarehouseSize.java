package ca.hjalmionlabs.warehouse.entities;

public enum WarehouseSize
{

	UNDEFINED, SMALL, MEDIUM, LARGE;
	
	public static WarehouseSize parseSize(String size)
	{
		if(size.equals("SMALL"))
			return SMALL;
		if(size.equals("MEDIUM"))
			return MEDIUM;
		if(size.equals("LARGE"))
			return LARGE;
		return UNDEFINED;
	}
	
	@Override
	public String toString()
	{
		for(WarehouseSize size : values())
		{
			if(size.equals(SMALL))
				return "SMALL";
			else if(size.equals(MEDIUM))
				return "MEDIUM";
			else if(size.equals(LARGE))
				return "LARGE";
			else
				return "UNDEFINED";
		}
		return null;
	}
}
