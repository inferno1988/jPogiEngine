package ua.com.ifno.pogi.GeoObjects;

import org.postgresql.util.PGobject;

import java.awt.*;

public class PGColor extends PGobject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 192625579797479333L;
	
	public Color getColor() {
		return Color.decode(getValue());
	}
	
}
