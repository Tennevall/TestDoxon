package testdoxon.handlers;

import java.io.File;

import testdoxon.exceptionHandlers.TDException;
import testdoxon.repositories.FileRepository;

public class FileHandler {
	
	private FileRepository fileRepository;
	
	public FileHandler() {
		this.fileRepository = new FileRepository();
	}
	
	public String[] getMethodsFromFile(String filePath) throws TDException {
		if(this.fileExists(filePath)) {
			return fileRepository.fetchMethodNames(filePath);
		} else {
			throw new TDException(TDException.FILE_NOT_FOUND);
		}
	}
	
	public boolean fileExists(String filePath) {
		File file = new File(filePath);
		return file.isFile();
	}
	
	public int getLineNumberOfSpecificMethod (String filePath, String methodName) throws TDException {
		return fileRepository.findLineNumberOfMethod(filePath, methodName);
	}
	
}
