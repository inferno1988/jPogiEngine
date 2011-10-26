import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.imageio.IIOException;
import javax.imageio.ImageIO;

public class PaintThread implements Runnable {
	private static Graphics2D g2 = null;
	ConcurrentLinkedQueue<Integer> list = new ConcurrentLinkedQueue<Integer>();
	private BufferedImage img = new BufferedImage(255, 255,
			BufferedImage.TYPE_INT_RGB);
	private static boolean moved = false;
	private static double deltaX = 0, scaleX = 1.0;
	private static double deltaY = 0, scaleY = 1.0;
	private int mx = 0, my = 0;
	
	@Override
	public void run() {
		try {
			WorkerPool.addWorker(1, "work");
			moved = false;
			mx = new Double(deltaX / 255.0).intValue();
			my = new Double(deltaY / 255.0).intValue();
			if (mx > 0)
				mx = 0;
			if (my > 0)
				my = 0;
			for (int i = Math.abs(mx); i < Math.abs(mx)+9; i++) {
				for (int j = Math.abs(my); j < Math.abs(my)+5; j++) {
					String fileUrl = new String(String.format(
							"http://192.168.33.110/23/tile-%d-%d.png", i, j));
					URL url = new URL(fileUrl);
					try {
						if (CachedLoop.containsKey(fileUrl)) {
							img = CachedLoop.get(fileUrl);
						} else {
							img = ImageIO.read(url);
							CachedLoop.put(fileUrl, img);
						}
					} catch (IIOException e) {
						continue;
					}
					AffineTransform at =
					AffineTransform.getScaleInstance(scaleX, scaleY);
					AffineTransformOp aop = new AffineTransformOp(at,
					AffineTransformOp.TYPE_BICUBIC);
					g2.drawImage(img, aop,
							new Double((255 * scaleX) * i).intValue()
									+ new Double(deltaX).intValue(),
							new Double((255 * scaleY) * j).intValue()
									+ new Double(deltaY).intValue());
					if (moved) {
						break;
					}
				}
				if (moved) {
					break;
				}
			}
			g2.dispose();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			WorkerPool.removeWorker(1);
		}
	}

	public static void setGraphics(Graphics2D g2d) {
		g2 = g2d;
	}

	public static void setDelta(double dx, double dy) {
		deltaX = dx;
		deltaY = dy;
	}

	public static void setScale(double sx, double sy) {
		scaleX = sx;
		scaleY = sy;
	}

	public static void stop() {
		moved = true;
	}
}
