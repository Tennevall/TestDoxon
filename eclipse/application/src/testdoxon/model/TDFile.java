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

package testdoxon.model;

import java.io.File;

import testdoxon.util.DoxonUtils;

public class TDFile {
	File file;
	String headerFilepath;
	String filepath;

	public TDFile(File file) {
		this.file = file;
		this.headerFilepath = null;
		
		if(this.file.getName().matches("^Test.*")) {
			this.filepath = this.file.getAbsolutePath().replaceAll("\\\\", "/");
		} else {
			this.filepath = DoxonUtils.createTestPath(file.getAbsolutePath()) + "Test" + file.getName();
		}
	}

	public String getAbsolutePath() {
		return this.file.getAbsolutePath();
	}
	
	public String getPath() {
		return this.filepath;
	}

	public String getName() {
		return this.file.getName();
	}

	public void setHeaderFilepath(String filepath) {
		this.headerFilepath = filepath;
	}

	/**
	 * 
	 * @return String
	 */
	public String getHeaderName() {
		String packageName = "Package name not found.";
		if (this.headerFilepath != null) {
			String[] parts;
			if(System.getProperty("os.name").contains("Windows")) {
				parts = this.headerFilepath.split("\\\\");
			} else {
				String filepath = this.headerFilepath;
				filepath.replaceAll("( )", "\\$0");
				parts = filepath.split("/");
			}
;
			boolean startCopy = false;
			for (int i = 0; i < parts.length; i++) {
				if (parts[i].equals("src")) {
					startCopy = true;
					packageName = "";
					//i += 3;
					continue;
				} else if (parts[i].equals("main")) {
					continue;
				} else if(parts[i].equals("java")) {
					continue;
				} else if(parts[i].equals("resources")) {
					continue;
				}

				if (startCopy) {
					packageName += parts[i];
					if (i != (parts.length - 1)) {
						packageName += ".";
					}
				}
			}
		}
		return packageName;
	}

}
