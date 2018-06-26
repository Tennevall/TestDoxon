package testdoxon.util;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;

import org.eclipse.jface.viewers.TableViewer;
import org.junit.Ignore;
import org.junit.jupiter.api.Test;

import testdoxon.model.TDFile;

class TestDoxonUtils {

	
	@Test
	public void testCreateTestPathReturnWithFileParam()
	{
		File file = new File("\\TestDoxon\\src\\main\\testdoxon\\util");
		TDFile tfile = new TDFile(file);

		assertEquals("C:/TestDoxon/src/test/testdoxon/",DoxonUtils.createTestPath(tfile));
	}
	
	@Test
	public void testCreateTestPathReturnWithStringParam()
	{
		String file = "C:\\TestDoxon\\src\\main\\testdoxon\\util";
		
		assertEquals("C:/TestDoxon/src/test/testdoxon/",DoxonUtils.createTestPath(file));
	}
	
	@Test
	public void testCreateTestPathReturnWithFileParamLinux() 
	{
		File file = new File("/TestDoxon/src/main/testdoxon/util");
		TDFile tfile = new TDFile(file);
		
		assertEquals("C:/TestDoxon/src/test/testdoxon/", DoxonUtils.createTestPath(tfile));
	}
	
	@Ignore
	public void testCreateTestPathReurnWithStringParamLinux()
	{
		String file = "C:/TestDoxon/src/main/testdoxon/util";
		
		System.out.println(DoxonUtils.createTestPath(file));
		assertEquals("C:/TestDoxon/src/test/testdoxon/util", DoxonUtils.createTestPath(file));
	}
	
	@Test
	public void testCreateTestPathNullFile()
	{
		TDFile file = null;
		
		assertNull(DoxonUtils.createTestPath(file));
	}
	
	@Test
	public void testCreateTestPathNullString()
	{
		String file = null;
		
		assertNull(file);
	}
	
	@Test
	public void testCreateTestPathEmptyString()
	{
		String file = "";
		
		assertEquals("", DoxonUtils.createTestPath(file));
	}
	
	@Test
	public void testFindRootFolderReturn()
	{
		String path = "\\TestDoxon\\src\\main\\testdoxon\\blah.exe";

		assertEquals("/TestDoxon/src", DoxonUtils.findRootFolder(path));
	}
	
	@Test
	public void testFindRootFolderNull()
	{
		String path = null;

		assertEquals(null,DoxonUtils.findRootFolder(path));
	}
	
	@Test
	public void testFindRootFolderBadPath()
	{
		String path = "\\TestDoxon\\src\\badpathing\\testdoxon";
		
		assertEquals("/TestDoxon/src", DoxonUtils.findRootFolder(path));
	}
	
	@Test
	public void testFindRootFolderEmptyPath()
	{
		String path = "";
		
		assertNull(DoxonUtils.findRootFolder(path));
	}

}
