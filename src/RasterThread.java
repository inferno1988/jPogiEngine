import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;

public class RasterThread extends PaintThread {
	private final int TILE_SIZE = 256;
	
	public RasterThread(GeoWindow parent, BufferedImage bi) {
		super(parent, bi);
	}

	@Override
	public void run() {
		Graphics2D g2d = getBi().createGraphics();
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
							setImg(CachedLoop.get("http://192.168.33.110/404.png"));
						} else {
							setImg(ImageIO.read(new URL("http://192.168.33.110/404.png")));
							CachedLoop.put("http://192.168.33.110/404.png", getImg());
						}
					}
					g2d.drawImage(getImg(), null,
							new Double(TILE_SIZE * tileInfo.getI()).intValue() + new Double(getGw().getDeltaX()).intValue(),
							new Double(TILE_SIZE * tileInfo.getJ()).intValue() + new Double(getGw().getDeltaY()).intValue());
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