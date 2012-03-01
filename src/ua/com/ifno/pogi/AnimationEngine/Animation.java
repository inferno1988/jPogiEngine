package ua.com.ifno.pogi.AnimationEngine;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Animation {
	private int seconds = 0;
	private long totalTime = 0;
	private long curTime = 0;
    private ArrayList<Frame> frames;
	private boolean firstFrame = true;
	private BufferedImage bi;
	private long showTime = 0;

	public Animation() {
		frames = new ArrayList<Frame>();
	}
	
	public void addFrame(Frame frame) {
		if (frame != null)
			frames.add(frame);
	}

	public BufferedImage getFrame() {
		if(firstFrame) {
			curTime = System.currentTimeMillis();
			firstFrame = false;
			bi = frames.get(0).getFrame();
			showTime = frames.get(0).getShowTime(); 
		}
        long lastTime = curTime;
		curTime = System.currentTimeMillis();
		totalTime += curTime - lastTime;
		if (totalTime > showTime) {
			totalTime -= showTime;
			++seconds;
			if (seconds < frames.size()) {
				bi = frames.get(seconds).getFrame();
				showTime = frames.get(seconds).getShowTime();
			} else {
				bi = null;
			}

		}
		return bi;
	}
	
	public void reset() {
		seconds = 0;
		firstFrame = true;
	}
}
