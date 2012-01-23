package ua.com.ifno.pogi.LayerEngine;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.DefaultListModel;
import javax.swing.ListModel;

import ua.com.ifno.pogi.ImageSettings;
import ua.com.ifno.pogi.Scaler;

public class LayerFactory {
	private ConcurrentHashMap<String, Layer> layers = new ConcurrentHashMap<String, Layer>();
	private ImageSettings imageSettings;
	private Scaler scaler;
	private Dimension size = new Dimension(800, 600);
	
	/** Constructs default background map layer */
	public LayerFactory(ImageSettings settings, Scaler scaler) {
		this.imageSettings = settings;
		this.scaler = scaler;
		BackgroundMapLayer bgLayer = new BackgroundMapLayer("Background", imageSettings, size, this.scaler, true);
		layers.put(bgLayer.getLayerName(), bgLayer);
	}
	
	public void setPosition(Rectangle viewPort) {
		if (!layers.isEmpty()) {
			Collection<Layer> layrs = layers.values();
			for (Layer layer : layrs) {
				layer.setPosition(viewPort);
			}
		}
	}
	
	public int getLayersCount() {
		return layers.size();
	}
	
	public ConcurrentHashMap<String, Layer> getLayers() {
		return layers;
	}

	public void setLayers(ConcurrentHashMap<String, Layer> layers) {
		this.layers = layers;
	}

	public ImageSettings getImageSettings() {
		return imageSettings;
	}

	public void setImageSettings(ImageSettings imageSettings) {
		this.imageSettings = imageSettings;
	}

	public Scaler getScaler() {
		return scaler;
	}

	public void setScaler(Scaler scaler) {
		this.scaler = scaler;
	}
	
	public int getCacheSize(String layerName) {
		Layer layer = layers.get(layerName);
		if (layer == null)
			return 0;
		return layer.getCacheSize();
	}
	
	public Collection<Object> getScene() {
		Collection<Object> l = new ArrayList<Object>();
		if (!layers.isEmpty()) {
			Collection<Layer> layrs = layers.values();
			for (Layer layer : layrs) {
				l.add(layer.getDrawable());
			}
			return l;
		}
		return null;
	}
	
	public void setSize(int width, int height) {
		size.setSize(width, height);
		if (!layers.isEmpty()) {
			Collection<Layer> layrs = layers.values();
			for (Layer layer : layrs) {
				layer.setSize(width, height);
			}
		}
	}
	
	public Dimension getLayersSize() {
		return size;
	}
	
	public ListModel getLayerListModel() {
		if (!layers.isEmpty()) {
			DefaultListModel lm = new DefaultListModel();
			Collection<Layer> layrs = layers.values();
			for (Layer layer : layrs) {
				lm.addElement(layer.getLayerName());
			}
			return lm;
		}
		return null;
	}
}
