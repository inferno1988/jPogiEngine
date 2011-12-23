package net.ifno.com.ua;
import java.util.ArrayList;
/**
 * @author Palamarchuk Maksym
 *
 */
public class Scaler {
	private ArrayList<Integer> scales;
	private int pointer = 0;

	/**
	 * @return the pointer
	 */
	public int getPointer() {
		return pointer;
	}

	/** Creates Scaler instance */
	public Scaler(ImageSettings imageSettings) {
		this.setScales(imageSettings.getScales());
		pointer = scales.get(0);
	}

	/**
	 * @return the scales
	 */
	public ArrayList<Integer> getScales() {
		return scales;
	}

	/**
	 * @param scales the scales to set
	 */
	public void setScales(ArrayList<Integer> scales) {
		this.scales = scales;
	}
	
	public void zoomIn() {
		if(pointer < scales.get(scales.size()-1))
			pointer++;
	}

	public void zoomOut() {
		if(pointer > scales.get(0))
			pointer--;
	}
}
