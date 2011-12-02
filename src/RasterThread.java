import java.awt.Graphics2D;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;

public class RasterThread extends PaintThread {

	public RasterThread(GeoWindow parent, Graphics2D g2d) {
		super(parent, g2d);
	}

	@Override
	public void run() {
		try {
			WorkerPool.addWorker(getId(), this);
				while(!JobGenerator.getJobList().isEmpty()) {
					if (isInterrupted()) {
						Thread.yield();
						WorkerPool.removeWorker(getId());
						return;
					}
					TileInfo tileInfo = JobGenerator.getJobList().take();
					try {
						if (CachedLoop.containsKey(tileInfo.getUrl().toString())) {
							setImg(CachedLoop.get(tileInfo.getUrl().toString()));
						} else {
							setImg(ImageIO.read(tileInfo.getUrl()));
							CachedLoop.put(tileInfo.getUrl().toString(), getImg());
						}
					} catch (IIOException e) {
						if (CachedLoop.containsKey("http://192.168.33.110/404.png")) {
							setImg(ImageIO.read(new URL("http://192.168.33.110/404.png")));
						} else {
							setImg(ImageIO.read(new URL("http://192.168.33.110/404.png")));
							CachedLoop.put("http://192.168.33.110/404.png", getImg());
						}
					}
					//System.out.println("Drawing: " + tileInfo.getI() + " | " + tileInfo.getJ() + ", on URL: " + tileInfo.getUrl());
					getG2().drawImage(getImg(), null,
							new Double(256 * tileInfo.getI()).intValue() + new Double(getGw().getDeltaX()).intValue(),
							new Double(256 * tileInfo.getJ()).intValue() + new Double(getGw().getDeltaY()).intValue());
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
			WorkerPool.removeWorker(getId());
		}
	}
}