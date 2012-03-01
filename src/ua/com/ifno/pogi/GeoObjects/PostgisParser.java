package ua.com.ifno.pogi.GeoObjects;

import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.postgis.Geometry;
import org.postgis.LineString;
import org.postgis.MultiLineString;
import org.postgis.PGgeometry;
import org.postgis.Point;
import org.postgis.Polygon;

public class PostgisParser {
	private ResultSet results = null;
	private String geoColumnLabel = "geom";

	public PostgisParser(String columnLabel, ResultSet results) {
		this.results = results;
		this.geoColumnLabel = columnLabel;
	}

	public ArrayList<Shape> parseAll() throws SQLException,
			WrongGeoTypeException, CantParseException {
		ArrayList<Shape> parsedObjects = new ArrayList<Shape>();
		while (results.next()) {
			PGgeometry geom = (PGgeometry) results.getObject(geoColumnLabel);
			int geoType = geom.getGeoType();
			switch (geoType) {
			case Geometry.LINESTRING:
				parsedObjects.add(parseLineString(geom));
				break;
			case Geometry.POLYGON:
				parsedObjects.add(parsePolygon(geom));
				break;
			case Geometry.MULTILINESTRING:
				parsedObjects.add(parseMultilineString(geom));
				break;
			default:
				throw new CantParseException(
						"ResultSet contains object that can't be parsed");
			}
		}
		return parsedObjects;
	}

	public ArrayList<Shape> parseAll(int geoType) throws SQLException,
			WrongGeoTypeException, CantParseException {
		ArrayList<Shape> parsedObjects = new ArrayList<Shape>();
		while (results.next()) {
			PGgeometry geom = (PGgeometry) results.getObject(geoColumnLabel);
			if (geom.getGeoType() == geoType) {
				switch (geoType) {
				case Geometry.LINESTRING:
					parsedObjects.add(parseLineString(geom));
					break;
				case Geometry.POLYGON:
					parsedObjects.add(parsePolygon(geom));
					break;
				case Geometry.MULTILINESTRING:
					parsedObjects.add(parseMultilineString(geom));
					break;
				default:
					break;
				}
			} else {
				throw new CantParseException(
						"ResultSet does not contains any parser for this type");
			}
		}
		return parsedObjects;
	}

	public static Shape parse(PGgeometry geometry) throws SQLException,
			WrongGeoTypeException, CantParseException {
		int geoType = geometry.getGeoType();
		switch (geoType) {
		case Geometry.LINESTRING:
			return parseLineString(geometry);
		case Geometry.POLYGON:
			return parsePolygon(geometry);
		case Geometry.MULTILINESTRING:
			return parseMultilineString(geometry);
		default:
			throw new CantParseException(
					"ResultSet does not contains any parser for this type");
		}
	}

	public static Line2D parseLineString(PGgeometry line)
			throws WrongGeoTypeException {
		if (line.getGeoType() != Geometry.LINESTRING)
			throw new WrongGeoTypeException("This is not LINESTRING object");
		LineString lineString = (LineString) line.getGeometry();
        return new Line2D.Double(
                postgisPointToJavaPoint(lineString.getFirstPoint()),
                postgisPointToJavaPoint(lineString.getLastPoint()));

	}

	public static GeneralPath parsePolygon(PGgeometry polygon)
			throws WrongGeoTypeException {
		if (polygon.getGeoType() != Geometry.POLYGON)
			throw new WrongGeoTypeException("This is not POLYGON object");
		Polygon poly = (Polygon) polygon.getGeometry();
		int num = poly.numPoints();
		GeneralPath resultPolygon = new GeneralPath();
		resultPolygon.moveTo(poly.getFirstPoint().x, poly.getFirstPoint().y);
		for (int i = 1; i < num; i++) {
			resultPolygon.lineTo(poly.getPoint(i).x, poly.getPoint(i).y);
		}
		// resultPolygon.closePath();
		return resultPolygon;
	}

	public static GeneralPath parseMultilineString(PGgeometry multiLineString)
			throws WrongGeoTypeException {
		if (multiLineString.getGeoType() != Geometry.MULTILINESTRING)
			throw new WrongGeoTypeException(
					"This is not MULTILINESTRING object");
		MultiLineString multiLine = (MultiLineString) multiLineString
				.getGeometry();
		int num = multiLine.numPoints();
		GeneralPath resultMultiLine = new GeneralPath();
		resultMultiLine.moveTo(multiLine.getFirstPoint().x,
				multiLine.getFirstPoint().y);
		for (int i = 1; i < num; i++) {
			resultMultiLine.lineTo(multiLine.getPoint(i).x,
					multiLine.getPoint(i).y);
		}
		resultMultiLine.closePath();
		return resultMultiLine;
	}

	private static Point2D postgisPointToJavaPoint(Point p) {
		return new Point2D.Double(p.getX(), p.getY());
	}
}
