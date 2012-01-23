package ua.com.ifno.pogi.LayerEngine;

import java.awt.Rectangle;

public interface Layer {
	public void setPosition(Rectangle viewPort);
	public Rectangle getPosition();
	public int getCacheSize();
	public void setSize(int width, int height);
	public Object getDrawable();
	public String getLayerName();
	public void setLayerName(String layerName);
	public boolean isVisible();
	public void setVisible(boolean visible);
}
