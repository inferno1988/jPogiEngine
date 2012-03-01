package ua.com.ifno.pogi.AnimationEngine;

import java.awt.image.BufferedImage;

public class Frame {
	private long showTime = 0;
	private BufferedImage frame;
	
	/** Creates frame with specified image and show time in milis
     * @param bi
     * @param showTime*/
	public Frame(BufferedImage bi, long showTime) {
		this.frame = bi;
		this.showTime = showTime;
	}

	public long getShowTime() {
		return showTime;
	}

	public void setShowTime(long showTime) {
		this.showTime = showTime;
	}

	public BufferedImage getFrame() {
		return frame;
	}

	public void setFrame(BufferedImage frame) {
		this.frame = frame;
	}
	
	

}
