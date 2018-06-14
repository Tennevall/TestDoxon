package repositories;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import exceptionHandlers.TDException;

public class FileRepository {

	public FileRepository() {
	}

	public String[] fetchMethodNames(String filePath) throws TDException {
		String[] fileContent = this.readFileContent(filePath);
		String[] methodNames = this.extractMethodNames(fileContent);
		
		if(methodNames.length == 0) {
			return null;
		} else {
			return methodNames;
		}
	}
	
	public int findLineNumberOfMethod (String filePath, String methodName) throws TDException {
		String[] fileContent = this.readFileContent(filePath);
		return this.findLineNumberOfMethod(fileContent, methodName) + 1;
	}

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

	private String[] extractMethodNames(String[] fileContent) {
		ArrayList<String> methodNames = new ArrayList<>();

		for (String line : fileContent) {
			// 1. Filter out all method names
			Pattern pattern = Pattern.compile("^[ \t]*public.*void.*(test|should)(.*[^ ] *\\(.*\\))");
			Matcher matcher = pattern.matcher(line);

			if (matcher.find()) {
				String _strMatch = matcher.group(2);

				// 2. Check if method name have arguments If not - do not continue
				if (!_strMatch.matches(".*\\(\\)")) {
					methodNames.add(_strMatch);
				} else {

					// 3. Extract method name and separate every word with a space and return
					pattern = Pattern.compile("(.*[^ ]).*\\(");
					matcher = pattern.matcher(_strMatch);

					if (matcher.find()) {
						methodNames.add(matcher.group(1).replaceAll("([A-Z0-9][a-z0-9]*)", "$0 "));
					}
				}
			}
		}
		
		String[] retVal = new String[methodNames.size()];
		retVal = methodNames.toArray(retVal);

		return retVal;
	}
	
	private int findLineNumberOfMethod (String[] fileContent, String methodName) {
		methodName = methodName.replaceAll(" ", "");
		final String regex = "^[ \\t]*public.*void.*(test|should)" + methodName + ".*";
		
		int result = -1;
		for(int i = 0; i < fileContent.length - 1; i++) {
			if(fileContent[i].matches(regex)) {
				result = i;
				break;
			}
		}
		
		return result;
	}
}
