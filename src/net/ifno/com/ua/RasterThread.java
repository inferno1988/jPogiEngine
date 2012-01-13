package net.ifno.com.ua;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.ImageIcon;

public class RasterThread extends PaintThread {

	private BufferedImage bi;
	private ImageSettings is;
	private Rectangle vp;

	public RasterThread(Rectangle viewport, BufferedImage bi,
			ImageSettings imageSettings) {
		this.bi = bi;
		this.is = imageSettings;
		this.vp = viewport;
	}

	private Image image;

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

				if (CachedLoop.containsKey(tileInfo.getUrl().toString())) {
					image = CachedLoop.get(tileInfo.getUrl().toString());
				} else {
					ImageIcon ic = new ImageIcon(tileInfo.getUrl());
					if (ic.getImageLoadStatus() == MediaTracker.COMPLETE) {
						image = ic.getImage();
						CachedLoop.put(tileInfo.getUrl().toString(), image);
					} else {
						image = new ImageIcon(new URL(is.getHost() + "/404.png"))
								.getImage();
					}
				}

				int x = (is.getTileSize() * tileInfo.getI()) - vp.x;
				int y = (is.getTileSize() * tileInfo.getJ()) - vp.y;
				g2d.drawImage(image, x, y, null);
				if (isInterrupted()) {
					Thread.yield();
					WorkerPool.removeWorker(getId());
					return;
				}
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