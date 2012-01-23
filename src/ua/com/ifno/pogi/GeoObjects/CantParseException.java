package ua.com.ifno.pogi.GeoObjects;

public class CantParseException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CantParseException(String msg) {
		super(msg);
	}
	
	public CantParseException(String msg, Throwable throwable) {
		super(msg, throwable);
	}
}
