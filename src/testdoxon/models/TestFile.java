package testdoxon.models;

public class TestFile {

	String filepath;
	String filename;
	
	public TestFile(String filename, String filepath) {
		this.filepath = filepath;
		this.filename = filename;
	}
	
	public void setFilepath (String filepath) {
		 this.filepath = filepath;
	}
	
	public void setFilename (String filename) {
		this.filename = filename;
	}
	
	public String getFilepath () {
		return this.filepath;
	}
	
	public String getFilename () {
		return this.filename;
	}
	
	public boolean compareFilename (String filename) {
		return this.filename.matches(filename);
	}
	
	public String toString() {
		return this.filename + " -> " + this.filepath;
	}
	
}
