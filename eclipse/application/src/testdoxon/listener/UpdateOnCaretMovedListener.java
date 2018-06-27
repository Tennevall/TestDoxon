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

package testdoxon.listener;

import java.io.File;

import org.eclipse.core.runtime.AssertionFailedException;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.custom.CaretEvent;
import org.eclipse.swt.custom.CaretListener;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Display;

import testdoxon.handler.FileCrawlerHandler;
import testdoxon.model.TDFile;
import testdoxon.util.DoxonUtils;
import testdoxon.views.View;

public class UpdateOnCaretMovedListener implements CaretListener {
	private FileCrawlerHandler fileCrawlerHandler;

	private StyledText text;
	private TableViewer viewer;
	private ComboViewer testClassPathsComboBox;
	
	private String firstTestFilepath, secondTestFilepath;

	public UpdateOnCaretMovedListener(TableViewer viewer, StyledText text, FileCrawlerHandler fileCrawlerHandler,
			ComboViewer testClassPathsComboBox) {
		super();
		this.viewer = viewer;
		this.text = text;
		this.fileCrawlerHandler = fileCrawlerHandler;
		this.testClassPathsComboBox = testClassPathsComboBox;
	}

	@Override
	public void caretMoved(CaretEvent arg0) {
		String word = DoxonUtils.getWordUnderCaret(arg0.caretOffset, text);

		if (word.length() > 0 && Character.isUpperCase(word.charAt(0))) {
			String firstFileToLookFor = "Test" + word + ".java";
			String secondFileToLookFor = word + "Test.java";

			if (View.currentOpenFile != null) {
				this.firstTestFilepath = fileCrawlerHandler.getTestFilepathFromFilename(firstFileToLookFor,
						View.currentOpenFile.getAbsolutePath(), View.currentOpenFile.getName(),
						this.testClassPathsComboBox);

				this.secondTestFilepath = fileCrawlerHandler.getTestFilepathFromFilename(secondFileToLookFor,
						View.currentOpenFile.getAbsolutePath(), View.currentOpenFile.getName(),
						this.testClassPathsComboBox);
				
				checkFileExists();

				if ((this.firstTestFilepath != null && !this.firstTestFilepath.equals(View.currentTestFile.getAbsolutePath())) || 
						(this.secondTestFilepath != null && !this.secondTestFilepath.equals(View.currentTestFile.getAbsolutePath())) ) {
					
					if(this.firstTestFilepath != null && !this.firstTestFilepath.equals(View.currentTestFile.getAbsolutePath())) {
						View.currentTestFile = new TDFile(new File(firstTestFilepath));
						View.currentTestFile.setHeaderFilepath(View.currentTestFile.getAbsolutePath());
					
					}else if(this.secondTestFilepath != null && !this.secondTestFilepath.equals(View.currentTestFile.getAbsolutePath())) {
						View.currentTestFile = new TDFile(new File(this.secondTestFilepath));
						View.currentTestFile.setHeaderFilepath(View.currentTestFile.getAbsolutePath());
					}

					if (View.currentTestFile != null) {
						Display.getDefault().syncExec(new Runnable() {
							@Override
							public void run() {
								try {
									viewer.setInput(View.currentTestFile);
								} catch (AssertionFailedException e) {
									// Do nothing
								}
							}
						});
					}
				}
			}
		}
		
		else {
			if (View.currentTestFile != null && View.currentOpenFile != null && !View.currentTestFile.getPath().equals(View.currentOpenFile.getPath())) {

				// Show current class test class.
				this.findFileToOpen();

				// Update combo viewer to show all test classes
				Display.getDefault().syncExec(new Runnable() {
					@Override
					public void run() {
						try {
							testClassPathsComboBox.setInput(fileCrawlerHandler.getAllTestClassesAsTestFileArray());
						} catch (AssertionFailedException e) {
							// Do nothing
						}
					}
				});
			}
		}
	}

	private void findFileToOpen() {
		if (View.currentOpenFile != null) {
			// If a test class already is open
			if (View.currentOpenFile.getName().matches("^Test.*")) {
				View.currentTestFile = View.currentOpenFile;
				// If a regular class is open
			} else {
				String newTestFilepath = DoxonUtils.createTestPath(View.currentOpenFile) + "Test"
						+ View.currentOpenFile.getName();
				View.currentTestFile = new TDFile(new File(newTestFilepath));
			}
			View.currentTestFile.setHeaderFilepath(View.currentOpenFile.getAbsolutePath());

			if (View.currentTestFile != null) {
				Display.getDefault().syncExec(new Runnable() {
					@Override
					public void run() {
						try {
							viewer.setInput(View.currentTestFile);
						} catch (AssertionFailedException e) {
							// Do nothing
						}

					}
				});
			}
		}
	}
	
	private void checkFileExists() {
		
		if(this.firstTestFilepath != null && this.secondTestFilepath != null) {
			File first = new File(this.firstTestFilepath);
			File second = new File(this.secondTestFilepath);
			if(first.exists() || second.exists()) {
				if(first.exists() && second.exists()) {
					this.secondTestFilepath = null;
				}
				else if(!first.exists()) {
					this.firstTestFilepath = null;
					if(!second.exists()) {
						this.secondTestFilepath = null;
					}
				}
				else {
					this.secondTestFilepath = null;
				}
			}
		}
		else if(this.firstTestFilepath == null) {
			File second = new File(this.secondTestFilepath);
			if(!second.exists()) {
				this.secondTestFilepath = null;
			}
		}
		else {
			File first = new File(this.firstTestFilepath);
			if(!first.exists()) {
				this.firstTestFilepath = null;
			}
		}
	}
}
