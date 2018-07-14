package ca.hjalmionlabs.warehouse.readers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public final class FileReader
{
	
	public static List<String> readFile(String filename)
	{
		List<String> lines = new ArrayList<String>();

		try(Stream<String> stream = Files.lines(Paths.get(filename)))
		{
			stream.forEach(e -> lines.add(e));
			stream.close();
		} catch(IOException ioe)
		{
			ioe.printStackTrace();
			System.err.println(ioe.getMessage());
		}
		
		return lines;
	}
	
}
