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

package testdoxon.repository;

import java.io.File;
import java.util.ArrayList;

import org.eclipse.core.runtime.AssertionFailedException;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.widgets.Display;

import testdoxon.log.TDLog;
import testdoxon.model.TestFile;

public class FileCrawlerRepository {

	private ArrayList<TestFile> testFiles;
	private ArrayList<String> foldersToCheck;

	/**
	 * Constructor
	 */
	public FileCrawlerRepository() {
		this.testFiles = new ArrayList<>();
		this.foldersToCheck = new ArrayList<>();
	}

	/**
	 * Recursively walk through folders
	 * @param path
	 */
	public void checkFolderHierarchy(String path, ComboViewer testClassPathsComboBox) {
		Thread thread = new Thread(new Runnable() {
			
		public void run() {
			testFiles.clear();
			foldersToCheck.clear();

			listFolder(path);

			while (foldersToCheck.size() != 0) {
				for (String f : new ArrayList<String>(foldersToCheck)) {
					listFolder(f);
					foldersToCheck.remove(f);
				}
			}
				TDLog.info("TestClasses in memory: " + testFiles.size(), TDLog.INFORMATION);
				
				Display.getDefault().syncExec(new Runnable() {
					@Override
					public void run() {
						try {
							testClassPathsComboBox.setInput(testFiles.toArray(new TestFile[testFiles.size()]));
						} catch (AssertionFailedException e) {
							TDLog.info(e.getMessage(), TDLog.ERROR);
						}
					}
				});
			}
		
		});
		thread.start();
		
	}

	/**
	 * Checks content of folders, saves all TestXx...x.java classes
	 * @param path
	 */
	private void listFolder(String path) {
		File file = new File(path);
		if (file != null && file.isDirectory()) {
			File[] files = file.listFiles();

			for (File f : files) {
				if (f.isFile()) {
					if (f.getName().matches("^Test.*\\.java") || f.getName().matches(".*Test\\.java")) {
						this.testFiles.add(new TestFile(f.getName(), f.getAbsolutePath()));
					}
				} else if (f.isDirectory()) {
					this.foldersToCheck.add(f.getAbsolutePath());
				}
			}
		}
	}
	
	public boolean listContains(String path) {
		for(TestFile testfile : testFiles) {
			if(testfile.getFilepath().equals(path)) {
				return true;
			}
		}
		return false;
	}
	
	public void addToList (TestFile testFile) {
		this.testFiles.add(testFile);
	}

	/**
	 * 
	 * @return ArrayList<TestFile>
	 */
	public ArrayList<TestFile> getAllTestFiles() {
		return this.testFiles;
	}

}