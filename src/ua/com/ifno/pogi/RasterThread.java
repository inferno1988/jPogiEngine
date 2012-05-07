package ua.com.ifno.pogi;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.MalformedURLException;
import java.net.URL;

class RasterThread extends PaintThread {
	private final ImageSettings is;
	private final Rectangle vp;
	private BufferedImage bi = null;

	public RasterThread(Rectangle viewport, BufferedImage bi,
			ImageSettings imageSettings) {
		this.is = imageSettings;
		this.vp = viewport;
		this.bi = bi;
	}

    @Override
	public void run() {
		Graphics2D g2d = bi.createGraphics();
		try {
			WorkerPool.addWorker(getId(), this);
			while (!JobGenerator.getJobList().isEmpty()) {
				if (isInterrupted()) {
					Thread.yield();
					WorkerPool.removeWorker(getId());
					return;
				}
				TileInfo tileInfo = JobGenerator.getJobList().take();
				ImageIcon ic = new ImageIcon(tileInfo.getUrl());
                Image image;
                if (ic.getImageLoadStatus() == MediaTracker.COMPLETE) {
					image = ic.getImage();
				} else {
					image = new ImageIcon(new URL(is.getHost() + "/404.png"))
							.getImage();
				}
				int x = (is.getTileSize() * tileInfo.getI()) - vp.x;
				int y = (is.getTileSize() * tileInfo.getJ()) - vp.y;
				g2d.drawImage(image, x, y, null);
				Thread.yield();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			g2d.dispose();
			WorkerPool.removeWorker(getId());
		}
	}
}