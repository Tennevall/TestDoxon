package testdoxon.handlers;

import java.io.File;

import testdoxon.exceptionHandlers.TDException;
import testdoxon.repositories.FileRepository;

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
	public String[] getMethodsFromFile(String filePath) throws TDException {
		if(this.fileExists(filePath)) {
			return fileRepository.fetchMethodNames(filePath);
		} else {
			throw new TDException(TDException.FILE_NOT_FOUND);
		}
	}
	
	/**
	 * Checks if filePath is valid or not
	 * @param filePath
	 * @return boolean
	 */
	public boolean fileExists(String filePath) {
		File file = new File(filePath);
		return file.isFile();
	}
	
	/**
	 * 
	 * @param filePath
	 * @param methodName
	 * @return int
	 * @throws TDException
	 */
	public int getLineNumberOfSpecificMethod (String filePath, String methodName) throws TDException {
		return fileRepository.findLineNumberOfMethod(filePath, methodName);
	}
	
}
