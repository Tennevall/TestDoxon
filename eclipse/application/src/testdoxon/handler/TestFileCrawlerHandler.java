package testdoxon.handler;

import static org.junit.jupiter.api.Assertions.*;

import org.eclipse.jface.viewers.ComboViewer;
import org.junit.Ignore;
import org.junit.jupiter.api.Test;

class TestFileCrawlerHandler {

	@Ignore
	void testGetTestFilepathFromFilenameReturn() {
		
		//setting combobox to null during tests
		FileCrawlerHandler FCH = new FileCrawlerHandler();
		String filename = "TestDoxonUtils.java";
		String currfilepath = System.getProperty("user.dir")+ "\\src\\testdoxon\\util\\DoxonUtils.java";
		String currfilename = "DoxonUtils.java";
		String result = System.getProperty("user.dir") + "\\src\\testdoxon\\util\\";
		ComboViewer CV = null;
		
		System.out.println(FCH.getTestFilepathFromFilename(filename, currfilepath, currfilename, CV));
		assertEquals(result, FCH.getTestFilepathFromFilename(filename, currfilepath, currfilename, CV));
	}
	
	@Test
	void testGetTestFilepathFromFilenameNullFileName() {
		
		FileCrawlerHandler FCH = new FileCrawlerHandler();
		String filename = null;
		String currfilepath = System.getProperty("user.dir") + "\\src\\testdoxon\\util\\DoxonUtils.java";
		String currfilename = "DoxonUtils.java";
		
		assertNull(FCH.getTestFilepathFromFilename(filename, currfilepath, currfilename, null));
	}
	
	@Test
	void testGetTestFilepathFromFilenameNullCurrFilePath() {
		
		FileCrawlerHandler FCH = new FileCrawlerHandler();
		String filename = "TestDoxonUtils.java";
		String currfilepath = null;
		String currfilename = "DoxonUtils.java";
		
		assertNull(FCH.getTestFilepathFromFilename(filename, currfilepath, currfilename, null));
	}
	
	@Test
	void testGetTestFilepathFromFilenameNullCurrFileName() {
		
		FileCrawlerHandler FCH = new FileCrawlerHandler();
		String filename = "TestDoxonUtils.java";
		String currfilepath = System.getProperty("user.dir") + "\\src\\testdoxon\\util\\DoxonUtils.java";
		String currfilename = null;
		
		assertNull(FCH.getTestFilepathFromFilename(filename, currfilepath, currfilename, null));		
	}
	
	@Test
	public void testGetTestFilepathFromFilenameNullAll() {
		
		FileCrawlerHandler FCH = new FileCrawlerHandler();
		String filename = null;
		String currfilepath = null;
		String currfilename = null;
		
		assertNull(FCH.getTestFilepathFromFilename(filename, currfilepath, currfilename, null));	
	}

	@Test
	void testGetTestFilepathFromFilenameEmptyFileName() {
		
		FileCrawlerHandler FCH = new FileCrawlerHandler();
		String filename = "";
		String currfilepath = System.getProperty("user.dir") + "\\src\\testdoxon\\util\\DoxonUtils.java";
		String currfilename = "DoxonUtils.java";
		
		assertNull(FCH.getTestFilepathFromFilename(filename, currfilepath, currfilename, null));
	}
	
	@Test
	void testGetTestFilepathFromFilenameEmptyCurrFilePath() {
		
		FileCrawlerHandler FCH = new FileCrawlerHandler();
		String filename = "TestDoxonUtils.java";
		String currfilepath = "";
		String currfilename = "DoxonUtils.java";
		
		assertNull(FCH.getTestFilepathFromFilename(filename, currfilepath, currfilename, null));	
	}
	
	@Test
	void testGetTestFilepathFromFilenameEmptyCurrFileName() {
		
		FileCrawlerHandler FCH = new FileCrawlerHandler();
		String filename = "TestDoxonUtils.java";
		String currfilepath = System.getProperty("user.dir") + "\\src\\testdoxon\\util\\DoxonUtils.java";
		String currfilename = "";
		
		assertNull(FCH.getTestFilepathFromFilename(filename, currfilepath, currfilename, null));
	}
	
	@Test
	void testGetTestFilepathFromFilenameEmptyAll() {
		
		FileCrawlerHandler FCH = new FileCrawlerHandler();
		String filename = "";
		String currfilepath = "";
		String currfilename = "";
		
		assertNull(FCH.getTestFilepathFromFilename(filename, currfilepath, currfilename, null));
	}
}
