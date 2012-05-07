package ua.com.ifno.pogi;

import java.awt.image.RGBImageFilter;

class FilterInvert extends RGBImageFilter {
    public FilterInvert() {
        canFilterIndexColorModel = true;
    }

    public int filterRGB(int x, int y, int rgb) {
        return ((rgb & 0xff000000) | (~rgb & 0x00ffffff));
    }
}

 