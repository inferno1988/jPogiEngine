package ua.com.ifno.pogi.GeoObjects;

import java.awt.Color;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public abstract class GeoObjMaker {
	private int dbId;
	private ArrayList<GeoObjShape> gShapes = new ArrayList<GeoObjShape>();
	private Color lineColor = Color.RED;
	private Color selectRectColor = Color.BLACK;
	private Color selectRectFillColor = Color.LIGHT_GRAY;

	public GeoObjMaker(int aDbId) {
		dbId = aDbId;
	}
	
	public int getDbId() {
		return dbId;
	}
	
	public String toString() {
		return getClass().getName();
	}
	
	
	public void addShape(GeoObjShape shape) {
		gShapes.add(shape);
	}

	public void setgShapes(ArrayList<GeoObjShape> gShapes) {
		this.gShapes = gShapes;
	}

	public ArrayList<GeoObjShape> getgShapes() {
		return gShapes;
	}
	
	public boolean intersects(Rectangle2D rect) {
		for (GeoObjShape shapes: gShapes)
			if (shapes.getShape().intersects(rect))
				return true;
		return false;
	}

	public void setLineColor(Color lineColor) {
		this.lineColor = lineColor;
	}

	public Color getLineColor() {
		return lineColor;
	}

	public void setSelectRectColor(Color selectRectColor) {
		this.selectRectColor = selectRectColor;
	}

	public Color getSelectRectColor() {
		return selectRectColor;
	}

	public void setSelectRectFillColor(Color selectRectFillColor) {
		this.selectRectFillColor = selectRectFillColor;
	}

	public Color getSelectRectFillColor() {
		return selectRectFillColor;
	}
	
	public void setSelected(){

	}
	
	public void move(Point2D offset) {
		for (GeoObjShape geoSshape : getgShapes()) {
			Shape shape = geoSshape.getShape();
			if (shape instanceof Line2D) {
				Line2D l = (Line2D) shape;
				l.setLine(l.getX1()+offset.getX(), l.getY1()+offset.getY(), l.getX2()+offset.getX(), l.getY2()+offset.getY());
			} else if (shape instanceof Rectangle2D) {
				Rectangle2D r = (Rectangle2D) shape;
				r.setRect(r.getX()+offset.getX(), r.getY()+offset.getY(), 10, 10);
			} else if (shape instanceof Polygon) {
				Polygon r = (Polygon) shape;
				r.translate(new Double(offset.getX()).intValue(), new Double(offset.getY()).intValue());
			}
		}
	}
}
