/**
Copyright 2018 Delicate Sound Of Software AB, All Rights Reserved

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package testdoxon.handler;

import java.util.ArrayList;

import testdoxon.model.TestFile;
import testdoxon.repository.FileCrawlerRepository;

public class FileCrawlerHandler {

	private FileCrawlerRepository fileCrawlerRepository;

	public FileCrawlerHandler() {
		this.fileCrawlerRepository = new FileCrawlerRepository();
	}

	/**
	 * 
	 * @param path
	 */
	public void getAllTestClasses(String path) {
		this.fileCrawlerRepository.checkFolderHierarchy(path);
	}

	/**
	 * 
	 * @param filename
	 * @param currentFilepath
	 * @param currentFilename
	 * @return String
	 */
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