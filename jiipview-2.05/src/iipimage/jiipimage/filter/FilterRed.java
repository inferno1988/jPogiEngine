package iipimage.jiipimage.filter;
import java.awt.image.RGBImageFilter;

public class FilterRed extends RGBImageFilter {
	
	public FilterRed () {
		canFilterIndexColorModel = true;
	}
	
	public int filterRGB (int x, int y, int rgb) {
		int r = (rgb & 0xff0000) >> 16;
		return (0xff000000 | r << 16 | r << 8 | r);
	}
}

