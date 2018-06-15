package testdoxon.utils;

import java.io.File;

public class DoxonUtils {
	
	public synchronized String createTestPath(File file) {
		String[] parts = file.getAbsolutePath().split("\\\\");
		String newFile = "";
	
		for (int i = 0; i < parts.length-1; i++) {
			if(parts[i].equals("main"))
			{
				newFile += "test\\";
			}
			else
			{
				newFile += parts[i] + "\\";
			}
		}
		System.out.println(newFile);
		//newFile += "tests\\";
		return newFile;
	}
	
}
