package ua.com.ifno.pogi.GeoObjects;

public class WrongGeoTypeException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public WrongGeoTypeException(String msg) {
		super(msg);
	}
	
	public WrongGeoTypeException(String msg, Throwable throwable) {
		super(msg, throwable);
	}
}
