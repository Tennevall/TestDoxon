package testdoxon.utils;

import java.io.File;

public class DoxonUtils {
	
	public String createTestPath(File file) {
		String[] parts = file.getAbsolutePath().split("\\\\");
		String newFile = "";
		for (int i = 0; i < parts.length - 2; i++) {
			newFile += parts[i] + "\\";
		}
		newFile += "tests\\";
		return newFile;
	}
	
	public String findTestFolder (String filepath) {
		String[] parts = filepath.split("\\\\");
		String newFilepath = "";
		
		for(String part : parts) {
			if(part.equals("main")) {
				newFilepath += "test";
				break;
			}
			newFilepath += part + "/";
		}
		
		return newFilepath;
	}
	
}
