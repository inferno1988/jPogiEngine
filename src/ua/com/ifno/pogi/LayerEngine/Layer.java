package ua.com.ifno.pogi.LayerEngine;

import java.awt.Rectangle;
import java.awt.Shape;
import java.util.ArrayList;

public interface Layer {
	public final int VECTOR = 1;
	public final int RASTER = 2;
	public void setPosition(Rectangle viewPort);
	public Rectangle getPosition();
	public void setSize(int width, int height);
	public Object getDrawable();
	public String getLayerName();
	public void setLayerName(String layerName);
	public boolean isVisible();
	public void setVisible(boolean visible);
	public ArrayList<Shape> getData();
	public void setData(ArrayList<Shape> data);
}
