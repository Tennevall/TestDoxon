package tests;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Test;

//import Inputs;

public class TestInputs {
	
	@Test
	public void testDoesFileExists() {
		/*Inputs inputs = new Inputs();
		assertTrue(inputs.fileExist(".", "TestInputs.java"));
		assertFalse(inputs.fileExist(".", "TestInputs.text"));*/
	}
	
	@Test
	public void testGetContentOfFile() {
		String[] expectedMethodNames = {"ReadFile(String input)", "Hej Svejs Hej Svejs ", "Work Fine ", "AlsoWorkFine(String bing)"};
		
		/*Inputs inputs = new Inputs();
		ArrayList<String> methods = inputs.getMethodNamesFromFile("TestInputs.java");

		String[] actual = new String[methods.size()];
		actual = methods.toArray(actual);
		
		assertArrayEquals(expectedMethodNames, actual);*/
	}
	
}
