package iipimage.jiipimage.filter;
import java.awt.image.RGBImageFilter;

public class FilterGreen extends RGBImageFilter {
	
	public FilterGreen () {
		canFilterIndexColorModel = true;
	}

	public int filterRGB (int x, int y, int rgb) {
		int g = (rgb & 0xff00) >> 8;
		return (0xff000000 | g << 16 | g << 8 | g);
	}
}

