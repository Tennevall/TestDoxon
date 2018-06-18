package testdoxon.repositories;

import java.io.File;
import java.util.ArrayList;

import testdoxon.models.TestFile;

public class FileCrawlerRepository {

	private ArrayList<TestFile> testFiles;
	private ArrayList<String> foldersToCheck;

	public FileCrawlerRepository() {
		this.testFiles = new ArrayList<>();
		this.foldersToCheck = new ArrayList<>();
	}

	public void checkFolderHierarchy(String path) {
		this.testFiles.clear();
		this.foldersToCheck.clear();

		this.listFolder(path);

		while (this.foldersToCheck.size() != 0) {
			for (String f : new ArrayList<String>(this.foldersToCheck)) {
				this.listFolder(f);
				this.foldersToCheck.remove(f);
			}
		}
	}

	private void listFolder(String path) {
		File file = new File(path);
		if (file != null && file.isDirectory()) {
			File[] files = file.listFiles();

			for (File f : files) {
				if (f.isFile()) {
					if (f.getName().matches("^Test.*\\.java")) {
						this.testFiles.add(new TestFile(f.getName(), f.getAbsolutePath()));
					}
				} else if (f.isDirectory()) {
					this.foldersToCheck.add(f.getAbsolutePath());
				}
			}
		}
	}

	public ArrayList<TestFile> getAllTestFiles() {
		return this.testFiles;
	}

}
