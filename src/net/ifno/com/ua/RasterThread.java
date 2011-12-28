package net.ifno.com.ua;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;

public class RasterThread extends PaintThread {

	private BufferedImage bi, image;
	private ImageSettings is;
	private Rectangle vp;

	public RasterThread(Rectangle viewport, BufferedImage bi,
			ImageSettings imageSettings) {
		this.bi = bi;
		this.is = imageSettings;
		this.vp = viewport;
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
				try {
					if (CachedLoop.containsKey(tileInfo.getUrl().toString())) {
						image = CachedLoop.get(tileInfo.getUrl().toString());
					} else {
						image = ImageIO.read(tileInfo.getUrl());
						CachedLoop.put(tileInfo.getUrl().toString(), image);
					}
				} catch (IIOException e) {
					if (CachedLoop.containsKey("404")) {
						image = CachedLoop.get("404");
					} else {
						image = ImageIO.read(new URL(
								"http://192.168.33.110/404.png"));
						CachedLoop.put("404", image);
					}
				}
				int x = new Double(is.getTileSize()	* tileInfo.getI()).intValue()-vp.x;
				int y = new Double(is.getTileSize() * tileInfo.getJ()).intValue()-vp.y;
				g2d.drawImage(image, null, x, y);
				if (isInterrupted()) {
					Thread.yield();
					WorkerPool.removeWorker(getId());
					return;
				}
				Thread.yield();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			g2d.dispose();
			WorkerPool.removeWorker(getId());
		}
	}
}