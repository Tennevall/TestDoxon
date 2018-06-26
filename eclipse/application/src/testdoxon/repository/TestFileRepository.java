package testdoxon.repository;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.Ignore;
import org.junit.jupiter.api.Test;

import testdoxon.exceptionHandler.TDException;
import testdoxon.model.TDTableItem;

class TestFileRepository {

	@Test
	public void testFetchMethodNamesNotNull() 
	{
		
		String path = System.getProperty("user.dir") + "\\src\\testdoxon\\util\\TestDoxonUtils.java";
		FileRepository FR = new FileRepository();
		
		try {
			assertNotNull(FR.fetchMethodNames(path));
		} catch (TDException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testFetchMethodNamesTriggerNull()
	{
		String path = System.getProperty("user.dir") + "\\src\\testdoxon\\util\\DoxonUtils.java";
		FileRepository FR = new FileRepository();
		
		try {
			assertNull(FR.fetchMethodNames(path));
		} catch (TDException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testFetchMethodNamesEmptyString()
	{
		String path = "";
		FileRepository FR = new FileRepository();
		
		try {
			assertNull(FR.fetchMethodNames(path));
		} catch (TDException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testFetchMethodNamesNullParam()
	{
		String path = null;
		FileRepository FR = new FileRepository();

		try {
			assertNull(FR.fetchMethodNames(path));
		} catch (TDException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testFindLineNumberOfMethod()
	{
		String path = System.getProperty("user.dir") + "\\src\\testdoxon\\util\\TestDoxonUtils.java";
		FileRepository FR = new FileRepository();
		
		try {
			assertEquals(76,FR.findLineNumberOfMethod(path, "FindRootFolderReturn"));
		} catch (TDException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testFindLineNumberOfMethodReturnZero() 
	{
		String path = System.getProperty("user.dir") + "\\src\\testdoxon\\util\\TestDoxonUtils.java";
		FileRepository FR = new FileRepository();
		
		try {
			assertEquals(0,FR.findLineNumberOfMethod(path, "FindTestFolder"));
		} catch (TDException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testFindLineNumberOfMethodEmptyString()
	{
		String path = "";
		FileRepository FR = new FileRepository();
		
		try {
			assertEquals(-1,FR.findLineNumberOfMethod(path, "FindTestFolderReturn"));
		} catch (TDException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testFindLineNumberOfMethodPathNull()
	{
		String path = null;
		FileRepository FR = new FileRepository();
		
		try {
			assertEquals(-1,FR.findLineNumberOfMethod(path, "FindTestFolderReturn"));
		} catch (TDException e) {
			e.printStackTrace();
		}
	}

}
