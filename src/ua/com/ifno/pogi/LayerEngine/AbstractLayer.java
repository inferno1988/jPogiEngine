package ua.com.ifno.pogi.LayerEngine;

public abstract class AbstractLayer implements Layer {
	private String layerName = null;
	private boolean visible = true;

	AbstractLayer(String name, boolean visible) {
		this.layerName = name;
		this.visible = visible;
	}

	public String getLayerName() {
		return layerName;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setLayerName(String layerName) {
		this.layerName = layerName;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}
}
