package net.it_tim.jpogiengine.geoObjects;

import java.awt.BasicStroke;
import java.awt.Polygon;
import java.awt.geom.Rectangle2D;

public class GeoPolyMaker extends GeoObjMaker {

	public GeoPolyMaker(int aDbId, int z, int[] xpoints, int[] ypoints, int npoints) {
		super(aDbId);
		Polygon poly = new Polygon(xpoints, ypoints, npoints);
		BasicStroke fat = new BasicStroke(3.0f, BasicStroke.CAP_ROUND,
				BasicStroke.JOIN_MITER);
		GeoObjShape goj = new GeoObjShape(poly, z, getLineColor(), fat, true,
				getSelectRectFillColor());
		addShape(goj);
		for (int i = 0; i < npoints; i++) {
			Rectangle2D s = new Rectangle2D.Double(xpoints[i] - 5,
					ypoints[i] - 5, 10, 10);
			GeoObjShape rect = new GeoObjShape(s, z, getSelectRectColor(), null,
					true, getSelectRectFillColor());
			addShape(rect);
		}
	}

}
