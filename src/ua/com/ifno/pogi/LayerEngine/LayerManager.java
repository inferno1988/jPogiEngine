package ua.com.ifno.pogi.LayerEngine;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.table.TableModel;

import ua.com.ifno.pogi.ImageSettings;
import ua.com.ifno.pogi.Scaler;

public class LayerManager {
	private CopyOnWriteArrayList<Layer> layers = new CopyOnWriteArrayList<Layer>();
	private ImageSettings imageSettings;
	private Scaler scaler;
	private Dimension size = new Dimension(800, 600);
	private	LayerListModel listModel = new LayerListModel();
	
	/** Constructs default background map layer */
	public LayerManager(ImageSettings settings, Scaler scaler) {
		this.imageSettings = settings;
		this.scaler = scaler;
		BackgroundMapLayer bgLayer = new BackgroundMapLayer("Background", imageSettings, size, this.scaler,true);
		layers.add(bgLayer);
		listModel.addElement(bgLayer);
	}
	
	public void addLayer(Layer layer) throws NullPointerException {
		if (layer == null)
			throw new NullPointerException();
		layers.add(layer);
		listModel.addElement(layer);
	}
	
	public ImageSettings getImageSettings() {
		return imageSettings;
	}
	
	public Layer getLayer(int index) {
		return layers.get(index);
	}

	public TableModel getLayerTableModel() {
		return listModel;
	}

	public CopyOnWriteArrayList<Layer> getLayers() {
		return layers;
	}
	
	public CopyOnWriteArrayList<Layer> getLayersFrom(int index) {
		return new CopyOnWriteArrayList<Layer>(layers.subList(index, getLayersCount()));
	}

	public int getLayersCount() {
		return layers.size();
	}

	public Dimension getLayersSize() {
		return size;
	}

	public Scaler getScaler() {
		return scaler;
	}
	
	public Collection<Object> getScene() {
		Collection<Object> l = new ArrayList<Object>();
		if (!layers.isEmpty()) {
			for (Layer layer : layers) {
				l.add(layer.getDrawable());
			}
			return l;
		}
		return null;
	}
	
	public void setImageSettings(ImageSettings imageSettings) {
		this.imageSettings = imageSettings;
	}
	
	public void setLayers(CopyOnWriteArrayList<Layer> layers) {
		this.layers = layers;
	}
	
	public void setViewPort(Rectangle viewPort) {
		if (!layers.isEmpty()) {
			for (Layer layer : layers) {
				layer.setViewPort(viewPort);
			}
		}
	}

    public Rectangle getViewPort(int layerIndex) throws IndexOutOfBoundsException {
        if (!layers.isEmpty() && layerIndex >= 0)
            return layers.get(layerIndex).getViewPort();
        else
            throw new IndexOutOfBoundsException("wrong layer index");
    }
	
	public void setScaler(Scaler scaler) {
		this.scaler = scaler;
	}
	
	public void setSize(int width, int height) {
		size.setSize(width, height);
		if (!layers.isEmpty()) {
			for (Layer layer : layers) {
				layer.setSize(width, height);
			}
		}
	}
}
