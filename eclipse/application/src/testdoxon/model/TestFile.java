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

public class TestFile {

	String filepath;
	String filename;

	public TestFile(String filename, String filepath) {
		this.filepath = filepath;
		this.filename = filename;
	}

	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getFilepath() {
		return this.filepath;
	}

	public String getFilename() {
		return this.filename;
	}

	public boolean compareFilename(String filename) {
		return this.filename.equals(filename);
	}

	public String toString() {
		return this.getPackage();
	}

	public String getPackage() {
		String packageName = "Package name not found.";
		String[] parts;
		if(System.getProperty("os.name").contains("Windows")) {
			parts = this.filepath.split("\\\\");
		} else {
			String _tmp = this.filepath;
			_tmp.replaceAll("( )", "\\$0");
			parts = _tmp.split("/");
		}
		
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

		return packageName;
	}

}