package net.ifno.com.ua;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.ImageIcon;

public class RasterThread extends PaintThread {

	private BufferedImage bi;
	private Image i;
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
				if (CachedLoop.containsKey(tileInfo.getUrl().toString())) {
					i = CachedLoop.get(tileInfo.getUrl().toString());
				} else {
					ImageIcon ic = new ImageIcon(tileInfo.getUrl());
					if (ic.getImageLoadStatus() == MediaTracker.COMPLETE) {
						i = ic.getImage();
						CachedLoop.put(tileInfo.getUrl().toString(), i);
					} else {
						i = new ImageIcon(new URL(is.getHost() + "/404.png"))
								.getImage();
					}
				}

				int x = new Double(is.getTileSize() * tileInfo.getI())
						.intValue() - vp.x;
				int y = new Double(is.getTileSize() * tileInfo.getJ())
						.intValue() - vp.y;
				g2d.drawImage(i, x, y, null);
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

	// This method returns a buffered image with the contents of an image
	public static BufferedImage toBufferedImage(Image image) {
		if (image instanceof BufferedImage) {
			return (BufferedImage) image;
		}
		// Determine if the image has transparent pixels; for this method's
		// implementation, see Determining If an Image Has Transparent Pixels

		// Create a buffered image with a format that's compatible with the
		// screen
		BufferedImage bimage = null;
		GraphicsEnvironment ge = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		try {
			// Determine the type of transparency of the new buffered image
			int transparency = Transparency.OPAQUE;

			// Create the buffered image
			GraphicsDevice gs = ge.getDefaultScreenDevice();
			GraphicsConfiguration gc = gs.getDefaultConfiguration();
			bimage = gc.createCompatibleImage(image.getWidth(null),
					image.getHeight(null), transparency);
		} catch (HeadlessException e) {
			// The system does not have a screen
		}

		if (bimage == null) {
			// Create a buffered image using the default color model
			int type = BufferedImage.TYPE_INT_RGB;

			bimage = new BufferedImage(image.getWidth(null),
					image.getHeight(null), type);
		}

		// Copy image to buffered image
		Graphics g = bimage.createGraphics();

		// Paint the image onto the buffered image
		g.drawImage(image, 0, 0, null);
		g.dispose();

		return bimage;
	}
}