package ca.hjalmionlabs.warehouse.handlers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public final class FileHandler
{
	
	public static List<String> readFile(String filename)
	{
		File file = new File(filename);
		if(!file.exists())
			try {
				file.createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
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
	
	public static <T> void writeFile(String filename, T toWrite)
	{
		File file = new File(filename);
		if(!file.exists())
			try
			{
				file.createNewFile();
			} catch(IOException ioe)
		{
				ioe.printStackTrace();
		}

		try
		{
			FileOutputStream fileOut = new FileOutputStream(file);
			ObjectOutputStream objOut = new ObjectOutputStream(fileOut);
			
			objOut.writeObject(toWrite);
			
			objOut.flush();
			fileOut.flush();
			
			objOut.close();
			fileOut.close();
		} catch(IOException ioe)
		{
			ioe.printStackTrace();
		}
	}
}
