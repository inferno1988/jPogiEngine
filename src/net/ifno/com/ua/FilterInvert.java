package net.ifno.com.ua;
import java.awt.image.RGBImageFilter;
public class FilterInvert extends RGBImageFilter {
	public FilterInvert() {
	canFilterIndexColorModel = true;
	}

	public int filterRGB(int x, int y, int rgb) {
	return ((rgb & 0xff000000) | (~rgb & 0x00ffffff));
	}
}

 