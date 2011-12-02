package iipimage.jiipimage.filter;
import java.awt.image.RGBImageFilter;

public class FilterBlue extends RGBImageFilter {
	
	public FilterBlue () {
		canFilterIndexColorModel = true;
	}

	public int filterRGB (int x, int y, int rgb) {
		int b = rgb & 0xff;
		return (0xff000000 | b << 16 | b << 8 | b);
	}
}

