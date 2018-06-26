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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import testdoxon.exceptionHandler.TDException;
import testdoxon.model.TDTableItem;

public class FileRepository {

	/**
	 * constructor
	 */
	public FileRepository() {
	}

	/**
	 * Returns null if method name is not found
	 * 
	 * @param filePath
	 * @return String[]
	 * @throws TDException
	 */
	public TDTableItem[] fetchMethodNames(String filePath) throws TDException {
		if (filePath == null || filePath.isEmpty()) {
			return null;
		} else {
			String[] fileContent = this.readFileContent(filePath);
			TDTableItem[] methodNames = this.extractMethodNames(fileContent);

			if (methodNames.length == 0) {
				return null;
			}
			return methodNames;
		}
	}

	/**
	 * 
	 * @param filePath
	 * @param methodName
	 * @return int
	 * @throws TDException
	 */
	public int findLineNumberOfMethod(String filePath, String methodName) throws TDException {
		if (filePath == null || filePath.isEmpty()) {
			return -1;
		} else {
			String[] fileContent = this.readFileContent(filePath);
			return this.findLineNumberOfMethod(fileContent, methodName) + 1;
		}
	}

	/**
	 * 
	 * @param filePath
	 * @return String[]
	 * @throws TDException
	 */
	private String[] readFileContent(String filePath) throws TDException {
		ArrayList<String> input = new ArrayList<>();

		try {
			BufferedReader br = new BufferedReader(new FileReader(filePath));

			String line = br.readLine();
			while (line != null) {
				input.add(line);
				line = br.readLine();
			}

			br.close();
		} catch (IOException e) {
			throw new TDException(TDException.FILE_NOT_FOUND);
		}

		String[] retVal = new String[input.size()];
		retVal = input.toArray(retVal);

		return retVal;
	}

	/**
	 * Gives the name of a method with parameters. If parameters are not present the
	 * method name will be returned separated with spaces.
	 * 
	 * @param fileContent
	 * @return String[]
	 */
	private TDTableItem[] extractMethodNames(String[] fileContent) {
		ArrayList<TDTableItem> methodNames = new ArrayList<>();

		for (int i = 0; i < fileContent.length; i++) {
			// 1. Filter out all method names
			Pattern pattern = Pattern.compile("^[ \t\n]*public[ \t\n]+void[ \t\n]+(test|should)([A-Z0-9]+.*\\(.*\\))");
			Matcher matcher = pattern.matcher(fileContent[i]);

			if (matcher.find()) {
				String _strMatch = matcher.group(2);
				boolean hasTest = lookForAtTest(fileContent, i);
				boolean hasIgnore = lookForAtIgnore(fileContent, i);

				// 2. Check if method name have arguments If not - do not continue
				if (!_strMatch.matches(".*\\(\\)")) {
					methodNames.add(new TDTableItem(_strMatch, hasTest, hasIgnore));
				} else {
					// 3. Extract method name and separate every word with a space and return
					pattern = Pattern.compile("(.*[^ ]).*\\(");
					matcher = pattern.matcher(_strMatch);

					if (matcher.find()) {
						methodNames.add(new TDTableItem(matcher.group(1).replaceAll("([A-Z0-9][a-z0-9]*)", "$0 "),
								hasTest, hasIgnore));
					}
				}
			}
		}

		TDTableItem[] retVal = new TDTableItem[methodNames.size()];
		retVal = methodNames.toArray(retVal);

		return retVal;
	}

	private boolean lookForAtTest(String[] fileContent, int lineNumber) {
		if ((lineNumber - 1) < 0) {
			return false;
		}
		if ((lineNumber - 2) < 0) {
			return fileContent[lineNumber - 1].matches("[ \t\n]*@Test.*");
		}

		return fileContent[lineNumber - 1].matches("[ \t\n]*@Test.*")
				|| fileContent[lineNumber - 2].matches("[ \t\n]*@Test.*");
	}

	private boolean lookForAtIgnore(String[] fileContent, int lineNumber) {
		if ((lineNumber - 1) < 0) {
			return false;
		}
		if ((lineNumber - 2) < 0) {
			return fileContent[lineNumber - 1].matches("[ \t\n]*@Ignore.*");
		}
		return fileContent[lineNumber - 1].matches("[ \t\n]*@Ignore.*")
				|| fileContent[lineNumber - 2].matches("[ \t\n]*@Ignore.*");
	}

	/**
	 * 
	 * @param fileContent
	 * @param methodName
	 * @return int
	 */
	private int findLineNumberOfMethod(String[] fileContent, String methodName) {
		if (!methodName.matches(".*\\(.*\\).*")) {
			methodName = methodName.replaceAll(" ", "");
			methodName += "[ \t\n]*\\([ \t\n]*\\)";

		} else {
			methodName = methodName.replaceAll("([\\(\\)])", "\\\\$0");
		}

		final String regex = "^[ \t\n]*public.*void.*(test|should)" + methodName + ".*";

		int result = -1;
		for (int i = 0; i < fileContent.length - 1; i++) {
			if (fileContent[i].matches(regex)) {
				result = i;
				break;
			}
		}

		return result;
	}
}