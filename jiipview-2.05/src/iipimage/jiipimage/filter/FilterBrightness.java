package iipimage.jiipimage.filter;
import iipimage.jiipimage.ViewImage;

import java.awt.image.RGBImageFilter;

public class FilterBrightness extends RGBImageFilter {
	
	public FilterBrightness () {
		canFilterIndexColorModel = true;
	}
	
	public int filterRGB (int x, int y, int rgb) {
		int r = (rgb & 0xff0000) >> 16;
		int g = (rgb & 0x00ff00) >> 8;
		int b = (rgb & 0x0000ff);
		
		double contrast = ViewImage.Contrb.getValue() / 10;
				int brigh = ViewImage.Brightb.getValue();
				if (contrast < 1) contrast = 1;
		
		r = (int)(r * contrast + brigh);
		if (r > 255) r = 255;
			else if ( r < 0) r = 0;
		g = (int)(g * contrast + brigh);
		if (g > 255) g = 255;
			else if ( g < 0) g = 0;
		b = (int)(b * contrast + brigh);
		if (b > 255) b = 255;
			else if ( b < 0) b = 0;
			
		return (0xff000000 | r << 16 | g << 8 | b );
	}
}

