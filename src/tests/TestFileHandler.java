package tests;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import exceptions.TDException;
import handlers.FileHandler;

public class TestFileHandler {
	
	@Test
	public void testDoesFileExists() {
		FileHandler fileHandler = new FileHandler();
		
		assertTrue(fileHandler.fileExists("C:\\Users\\eschras\\eclipse-workspace\\TestDoxon\\TestInputs.java"));
		assertFalse(fileHandler.fileExists(".\\TestInputs.text"));
	}
	
	@Test
	public void testGetContentOfFile() throws TDException {
		FileHandler fileHandler = new FileHandler();
		
		String[] expectedMethodNames = {"ReadFile(String input)", "Hej Svejs Hej Svejs ", "Work Fine ", "AlsoWorkFine(String bing)"};		
		String[] methodNames = fileHandler.getMethodsFromFile("C:\\Users\\eschras\\eclipse-workspace\\TestDoxon\\TestInputs.java");
		
		assertArrayEquals(expectedMethodNames, methodNames);
	}
	
}
