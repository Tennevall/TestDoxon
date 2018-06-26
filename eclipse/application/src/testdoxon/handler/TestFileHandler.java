package testdoxon.handler;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import testdoxon.exceptionHandler.TDException;

class TestFileHandler {

	@Test
	void testGetMethodsFromFileNull() {
		
		FileHandler FH = new FileHandler();
		String path = null;
		
		try {
			assertNull(FH.getMethodsFromFile(path));
		} catch (TDException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	void testGetMethodsFromFileEmptyPath() {
		
		FileHandler FH = new FileHandler();
		String path = "";
		
		try {
			assertEquals(TDException.FILE_NOT_FOUND, FH.getMethodsFromFile(path));
		} catch (TDException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	void testFileExistsReturn() {
		
		FileHandler FH = new FileHandler();
		String path = System.getProperty("user.dir") + "\\src\\testdoxon\\util\\TestDoxonUtils.java";
		
		assertEquals(true, FH.fileExists(path));
	}
	
	@Test
	void testFileExistsNull() {
		
		FileHandler FH = new FileHandler();
		String path = null;
		
		assertEquals(false, FH.fileExists(path));
	}

	@Test
	void testFileExistsEmptyPath() {
		
		FileHandler FH = new FileHandler();
		String path = "";
		
		assertEquals(false, FH.fileExists(path));
	}
	
	@Test
	void testFileExistsBadPath() {
		
		FileHandler FH = new FileHandler();
		String path = System.getProperty("user.dir") + "\\src\\badpath\\util\\TestDoxonUtils.java";
		
		assertEquals(false, FH.fileExists(path));
	}
	
	@Test
	void testGetLineNumberOfSpecificMethodReturn() {
		
		FileHandler FH = new FileHandler();
		String path = System.getProperty("user.dir") + "\\src\\testdoxon\\util\\TestDoxonUtils.java";
		String method = "CreateTestPathNullFile";
		
		try {
			assertEquals(52, FH.getLineNumberOfSpecificMethod(path, method));
		} catch (TDException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	void testGetLineNumberOfSpecificMethodNullPath() {
		
		FileHandler FH = new FileHandler();
		String path = null;
		String method = "CreateTestPathNull";
		
		try {
			assertEquals(0, FH.getLineNumberOfSpecificMethod(path, method));
		} catch (TDException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	void testGetLineNumberOfSpecificMethodNullMethod() {
		
		FileHandler FH = new FileHandler();
		String path = System.getProperty("user.dir") + "\\src\\testdoxon\\util\\TestDoxonUtils.java";
		String method = null;
		
		try {
			assertEquals(0, FH.getLineNumberOfSpecificMethod(path, method));
		} catch (TDException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	void testGetLineNumberOfSpecificMethodEmptyPath() {
		
		FileHandler FH = new FileHandler();
		String path = "";
		String method = "CreateTestPathNull";
		
		try {
			assertEquals(0, FH.getLineNumberOfSpecificMethod(path, method));
		} catch (TDException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	void testGetLineNumberOfSpecificMethodEmptyMethod() {
		
		FileHandler FH = new FileHandler();
		String path = System.getProperty("user.dir") + "\\src\\testdoxon\\util\\TestDoxonUtils.java";
		String method = "";
		
		try {
			assertEquals(0,FH.getLineNumberOfSpecificMethod(path, method));
		} catch (TDException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	void testGetLineNumberOfSpecificMethodEmptyBoth() {
		
		FileHandler FH = new FileHandler();
		String path = "";
		String method = "";
		
		try {
			assertEquals(0, FH.getLineNumberOfSpecificMethod(path, method));
		} catch (TDException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	void testGetLineNumberOfSpecificMethodNullBoth() {
		
		FileHandler FH = new FileHandler();
		String path = null;
		String method = null;
		
		try {
			assertEquals(0, FH.getLineNumberOfSpecificMethod(path, method));
		} catch (TDException e) {
			e.printStackTrace();
		}
	}
}
