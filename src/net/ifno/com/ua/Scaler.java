package net.ifno.com.ua;

import java.awt.Dimension;
import java.util.ArrayList;

/**
 * @author Palamarchuk Maksym
 * 
 */
public class Scaler {
	private ArrayList<Integer> scales;
	private int pointer = 0;
	private int dividor = 0;
	private ImageSettings is;

	/**
	 * @return the pointer
	 */
	public int getPointer() {
		return pointer;
	}

	/** Creates Scaler instance */
	public Scaler(ImageSettings imageSettings) {
		is = imageSettings;
		this.setScales(is.getScales());
		pointer = scales.get(0);
		dividor = (int)Math.pow(2, scales.size()-1);
	}

	/**
	 * @return the scales
	 */
	public ArrayList<Integer> getScales() {
		return scales;
	}

	/**
	 * @param scales
	 *            the scales to set
	 */
	public void setScales(ArrayList<Integer> scales) {
		this.scales = scales;
	}
	
	public Dimension getScaledImageSize() {
		Dimension size = new Dimension();
		size.width = is.getImageSize().width/dividor;
		size.height = is.getImageSize().height/dividor;
		return size;
	}
	
	public void zoomIn() {
		if (pointer < scales.get(scales.size() - 1)) {
			pointer++;
			dividor/=2;
		}
	}

	public void zoomOut() {
		if (pointer > scales.get(0)) {
			pointer--;
			dividor*=2;
		}
	}
}
