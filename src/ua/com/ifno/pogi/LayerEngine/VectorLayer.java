package ua.com.ifno.pogi.LayerEngine;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Rectangle;

import net.sf.ehcache.Cache;

/**
 * @author Palamarchuk Maksym
 * 
 */
public class VectorLayer extends AbstractLayer implements Layer {
	private BasicStroke stroke = null;
	private Color fillColor = null;
	private Cache cache;

	/**
	 * <code>VectorLayer</code> creates default Layer with 1 pixel stroke line
	 * and Color.LIGHT_GRAY color
	 */
	public VectorLayer(String name, Cache cache, boolean visible) {
		super(name, visible);
		this.cache = cache;
	}

	/**
	 * Constructs a new <code>VectorLayer</code> with specified parameters
	 * 
	 * @param name
	 *            the name of <code>VectorLayer</code>
	 * @param visible
	 *            visibility of <code>VectorLayer</code>
	 * @param stroke
	 *            Defines stroke style. Can be null. When value is null, will be
	 *            used default value
	 * @param fillColor
	 *            Defines default fill color for layer
	 */
	public VectorLayer(String name, BasicStroke stroke, Color fillColor,
			Cache cache, boolean visible) {
		super(name, visible);
		this.cache = cache;
		if (stroke != null)
			this.stroke = stroke;
		else
			this.stroke = new BasicStroke();
		if (fillColor != null)
			this.fillColor = fillColor;
		else
			this.fillColor = Color.LIGHT_GRAY;
	}

	@Override
	public void setPosition(Rectangle viewPort) {

	}

	@Override
	public Rectangle getPosition() {

		return null;
	}

	public BasicStroke getStroke() {
		return stroke;
	}

	public void setStroke(BasicStroke stroke) {
		this.stroke = stroke;
	}

	public Color getFillColor() {
		return fillColor;
	}

	public void setFillColor(Color fillColor) {
		this.fillColor = fillColor;
	}

	@Override
	public int getCacheSize() {
		return 0;
	}

	@Override
	public Object getDrawable() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setSize(int width, int height) {
		// TODO Auto-generated method stub

	}

}
