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
		return this.readFileContent(filePath);
	}
	
	private String[] readFileContent(String filePath) throws TDException {
		ArrayList<String> input = new ArrayList<>();

		try {
			BufferedReader br = new BufferedReader(new FileReader(filePath));

			String line = br.readLine();
			while (line != null) {
				line = this.extractMethodNames(line);
				if (line != null) {
					input.add(line);
				}
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

	private String extractMethodNames(String lineContent) {

		// 1. Filter out all method names
		Pattern pattern = Pattern.compile("^[ \t]*public.*void.*(test|should)(.*[^ ] *\\(.*\\))");
		Matcher matcher = pattern.matcher(lineContent);

		if (matcher.find()) {
			String _strMatch = matcher.group(2);

			// 2. Check if method name have arguments If not - do not continue
			if (!_strMatch.matches(".*\\(\\)")) {
				return _strMatch;
			}

			// 3. Extract method name and separate every word with a space and return
			pattern = Pattern.compile("(.*[^ ]).*\\(");
			matcher = pattern.matcher(_strMatch);

			if (matcher.find()) {
				return matcher.group(1).replaceAll("([A-Z0-9][a-z0-9]*)", "$0 ");
			}
		}

		return null;
	}
}
