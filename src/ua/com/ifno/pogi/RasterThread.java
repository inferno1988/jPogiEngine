package ua.com.ifno.pogi;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.ImageIcon;

public class RasterThread extends PaintThread {
	private ImageSettings is;
	private Rectangle vp;
	private CachedLoop<String, Image> cachedLoop = null;
	private BufferedImage bi = null;


	public RasterThread(Rectangle viewport, BufferedImage bi,
			ImageSettings imageSettings, CachedLoop<String, Image> cache) {
		this.is = imageSettings;
		this.vp = viewport;
		this.cachedLoop = cache;
		this.bi = bi;
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

				if (cachedLoop.containsKey(tileInfo.getUrl().toString())) {
					image = cachedLoop.get(tileInfo.getUrl().toString());
				} else {
					ImageIcon ic = new ImageIcon(tileInfo.getUrl());
					if (ic.getImageLoadStatus() == MediaTracker.COMPLETE) {
						image = ic.getImage();
						cachedLoop.put(tileInfo.getUrl().toString(), image);
					} else {
						image = new ImageIcon(new URL(is.getHost() + "/404.png"))
								.getImage();
					}
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