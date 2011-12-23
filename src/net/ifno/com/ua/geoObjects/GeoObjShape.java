package net.ifno.com.ua.geoObjects;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;

public class GeoObjShape {
	private Shape shape = null;
	private Color strokeColor = Color.BLACK;
	private boolean isFillable = false;
	private Color fillColor = Color.LIGHT_GRAY;
	private BasicStroke strokeStyle = new BasicStroke(1.0f);
	private int z = 0;
	
	
	public GeoObjShape(Shape aShape, int zD, Color aStrokeColor, BasicStroke aStrokeStyle, boolean isFillable, Color aFillColor) {
		this.shape = aShape;
		this.setZ(zD);
		this.strokeColor = aStrokeColor;
		if (!isFillable) {
			this.fillColor = null;
		} else {
			this.isFillable = isFillable;
			this.fillColor = aFillColor;
		}
		if (aStrokeStyle != null)
			this.strokeStyle = aStrokeStyle;
	}
	
	public void setStrokeColor(Color strokeColor) {
		this.strokeColor = strokeColor;
	}

	public Color getStrokeColor() {
		return strokeColor;
	}

	public void setFillable(boolean isFillable) {
		this.isFillable = isFillable;
	}

	public boolean isFillable() {
		return isFillable;
	}

	public void setFillColor(Color fillColor) {
		this.fillColor = fillColor;
	}

	public Color getFillColor() {
		return fillColor;
	}

	public void setShape(Shape shape) {
		this.shape = shape;
	}

	public Shape getShape() {
		return shape;
	}

	public void setStrokeStyle(BasicStroke strokeStyle) {
		this.strokeStyle = strokeStyle;
	}

	public BasicStroke getStrokeStyle() {
		return strokeStyle;
	}

	public void setZ(int z) {
		this.z = z;
	}

	public int getZ() {
		return z;
	}
}
