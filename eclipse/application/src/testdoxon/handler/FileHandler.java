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

import java.io.File;

import testdoxon.exceptionHandler.TDException;
import testdoxon.model.TDTableItem;
import testdoxon.repository.FileRepository;

public class FileHandler {

	private FileRepository fileRepository;

	/**
	 * constructor
	 */
	public FileHandler() {
		this.fileRepository = new FileRepository();
	}

	/**
	 * 
	 * @param filePath
	 * @return String []
	 * @throws TDException
	 */
	public TDTableItem[] getMethodsFromFile(String filePath) throws TDException {
		if (this.fileExists(filePath)) {
			return fileRepository.fetchMethodNames(filePath);
		} else {
			throw new TDException(TDException.FILE_NOT_FOUND);
		}
	}

	/**
	 * Checks if filePath is valid or not
	 * Returns false when filePath is null or empty
	 * 
	 * @param filePath
	 * @return boolean
	 */
	public boolean fileExists(String filePath) {
		if(filePath == null) {
			return false;
		}
		File file = new File(filePath);
		return file.isFile();
	}

	/**
	 * Return 0 if filePath or methodName is null or empty
	 * 
	 * @param filePath
	 * @param methodName
	 * @return int
	 * @throws TDException
	 */
	public int getLineNumberOfSpecificMethod(String filePath, String methodName) throws TDException {
		if(filePath == null || methodName == null || filePath.isEmpty() || methodName.isEmpty()) {
			return 0;
		}
		
		return fileRepository.findLineNumberOfMethod(filePath, methodName);
	}

}