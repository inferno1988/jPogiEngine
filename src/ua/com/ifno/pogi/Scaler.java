package ua.com.ifno.pogi;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;

/**
 * @author Palamarchuk Maksym
 * 
 */
public class Scaler {
	private ArrayList<Integer> scales;
	private int pointer = 0;
	private int divider = 0;
	private ImageSettings is;

	/**
	 * @return the pointer
	 */
	public int getPointer() {
		return pointer;
	}

	/** Creates Scaler instance
     * @param imageSettings*/
	public Scaler(ImageSettings imageSettings) {
		is = imageSettings;
		this.setScales(is.getScales());
		pointer = scales.get(0);
		divider = (int) Math.pow(2, scales.size() - 1);
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
		size.width = is.getImageSize().width / divider;
		size.height = is.getImageSize().height / divider;
		return size;
	}

	public void zoomIn() {
		if (pointer < scales.get(scales.size() - 1)) {
			pointer++;
			divider /= 2;
		}
	}

	public void zoomOut() {
		if (pointer > scales.get(0)) {
			pointer--;
			divider *= 2;
		}
	}

	public Point zoomInTo(Point p, Rectangle viewPort) {
		if (pointer < scales.get(scales.size() - 1)) {
			pointer++;
			divider /= 2;
			int x = p.x + viewPort.x;
			int y = p.y + viewPort.y;
			return new Point(x * 2 - (x - viewPort.x), y * 2 - (y - viewPort.y));
		}
		return viewPort.getLocation();
	}

	public Point zoomOutFrom(Point p, Rectangle viewPort) {
		if (pointer > scales.get(0)) {
			pointer--;
			divider *= 2;
			int x = p.x + viewPort.x;
			int y = p.y + viewPort.y;
			return new Point(x / 2 - (x - viewPort.x), y / 2 - (y - viewPort.y));
		}
		return viewPort.getLocation();
	}
}
