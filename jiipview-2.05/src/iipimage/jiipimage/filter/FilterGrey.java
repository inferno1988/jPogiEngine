package iipimage.jiipimage.filter;
import java.awt.image.RGBImageFilter;

public class FilterGrey extends RGBImageFilter {
	
	public FilterGrey () {
		canFilterIndexColorModel = true;
	}
	
	public int filterRGB (int x, int y, int rgb) {
		if (y == 0 && x == 0) {
		}
		int r = (rgb >> 16) & 0xff;
		int g = (rgb >> 8) & 0xff;
		int b = rgb & 0xff;
		int n = ((r + g + b) / 3) & 0xff;	
		return (0xff000000 | n << 16 | n << 8 | n);
	}
}

