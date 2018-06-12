package handlers;

import java.io.File;

import exceptionHandlers.TDException;
import repositories.FileRepository;

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
}
