package ua.com.ifno.pogi.GeoObjects;

import java.awt.*;

class GeoObjShape {
	private Shape shape = null;
	private boolean isFillable = false;
	
	public GeoObjShape(Shape aShape, boolean isFillable) {
		this.shape = aShape;
		this.isFillable = isFillable;
	}

	public void setFillable(boolean isFillable) {
		this.isFillable = isFillable;
	}

	public boolean isFillable() {
		return isFillable;
	}

	public void setShape(Shape shape) {
		this.shape = shape;
	}

	public Shape getShape() {
		return shape;
	}
}
