package testdoxon.exceptionHandlers;

public class TDException extends Exception {

	final public static int FILE_NOT_FOUND = 0;

	private int exceptionType;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private TDException() {
	}

	/**
	 * constructor
	 * @param exceptionType
	 */
	public TDException(int exceptionType) {
		this();
		this.exceptionType = exceptionType;
	}

	/**
	 * Returns error message based on exceptionType
	 */
	public String getMessage() {
		switch (exceptionType) {
		case 0:
			return "File not found";
		default:
			return "Unkown error";
		}
	}

}
