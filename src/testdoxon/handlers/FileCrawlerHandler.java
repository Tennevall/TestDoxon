package testdoxon.handlers;

import java.util.ArrayList;

import testdoxon.models.TestFile;
import testdoxon.repositories.FileCrawlerRepository;

public class FileCrawlerHandler {

	private FileCrawlerRepository fileCrawlerRepository;

	public FileCrawlerHandler() {
		this.fileCrawlerRepository = new FileCrawlerRepository();
	}

	public void getAllTestClasses(String path) {
		this.fileCrawlerRepository.checkFolderHierarchy(path);
	}

	public String getTestFilepathFromFilename(String filename, String currentFilepath, String currentFilename) {
		// Get the location of where the actual testclass should be
		currentFilepath = currentFilepath.replaceAll("\\\\main\\\\", "\\\\test\\\\");
		currentFilepath = currentFilepath.replaceAll("\\\\" + currentFilename, "");
		currentFilepath += "\\" + filename;

		String testFilepath = null;
		ArrayList<String> foundedFilepaths = new ArrayList<>();

		ArrayList<TestFile> testfiles = fileCrawlerRepository.getAllTestFiles();
		for (TestFile f : testfiles) {
			// Find all files that have the same name as the file we are looking for
			if (f.compareFilename(filename)) {
				// If the location of the file is equal to the file we are looking for - return
				// Else keep finding all the files
				if (f.getFilepath().equals(currentFilepath)) {
					return f.getFilepath();
				}
				foundedFilepaths.add(f.getFilepath());
			}
		}

		// If the exact location has not been found - just return the first file in the array
		if (foundedFilepaths.size() > 0) {
			testFilepath = foundedFilepaths.get(0);
		}

		return testFilepath;
	}

	public void printAllTestfiles() {
		ArrayList<TestFile> testfiles = fileCrawlerRepository.getAllTestFiles();
		for (TestFile f : testfiles) {
			System.out.println(f.toString());
		}
	}

}
