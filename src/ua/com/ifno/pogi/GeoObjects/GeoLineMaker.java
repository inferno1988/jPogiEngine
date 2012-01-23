package ua.com.ifno.pogi.GeoObjects;

import java.awt.BasicStroke;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import org.postgis.Point;

public class GeoLineMaker extends GeoObjMaker {

	public GeoLineMaker(int aDbId, int z, Point p1, Point p2) {
		super(aDbId);
		Line2D line = new Line2D.Double(p1.x, p1.y, p2.x, p2.y);
		BasicStroke fat = new BasicStroke(3.0f, BasicStroke.CAP_ROUND,
				BasicStroke.JOIN_MITER);
		GeoObjShape goj = new GeoObjShape(line, z, getLineColor(), fat, false, null);
		addShape(goj);
		Rectangle2D s1 = new Rectangle2D.Double(p1.getX() - 5, p1.getY() - 5,
				10, 10);
		GeoObjShape rect1 = new GeoObjShape(s1, z, getSelectRectColor(), null, true,
				getSelectRectFillColor());
		addShape(rect1);
		Rectangle2D s2 = new Rectangle2D.Double(p2.getX() - 5, p2.getY() - 5,
				10, 10);
		GeoObjShape rect2 = new GeoObjShape(s2, z, getSelectRectColor(), null, true,
				getSelectRectFillColor());
		addShape(rect2);
	}
}
