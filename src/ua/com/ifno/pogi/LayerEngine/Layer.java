package ua.com.ifno.pogi.LayerEngine;

import java.awt.*;
import java.util.ArrayList;

public interface Layer {
	public final int VECTOR = 1;
	public final int RASTER = 2;
	public void setViewPort(Rectangle viewPort);
	public Rectangle getViewPort();
	public void setSize(int width, int height);
	public Object getDrawable();
	public String getLayerName();
	public void setLayerName(String layerName);
	public boolean isVisible();
	public void setVisible(boolean visible);
	public ArrayList<Shape> getData();
	public void setData(ArrayList<Shape> data);
}
