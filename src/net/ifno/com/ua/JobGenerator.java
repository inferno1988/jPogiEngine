package net.ifno.com.ua;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ArrayBlockingQueue;

public class JobGenerator extends PaintThread {

	private Scaler scaler;
	private final int TILE_SIZE;
	private Rectangle viewport;
	private BufferedImage bi;
	private ImageSettings is;


	public JobGenerator(Rectangle viewport, BufferedImage bi,
			ImageSettings imageSettings, Scaler scaler) {
		this.scaler = scaler;
		this.viewport = viewport;
		this.bi = bi;
		this.is = imageSettings;
		TILE_SIZE = imageSettings.getTileSize();
	}

	private static ArrayBlockingQueue<TileInfo> jobList = new ArrayBlockingQueue<TileInfo>(
			200);

	public static ArrayBlockingQueue<TileInfo> getJobList() {
		return jobList;
	}

	@Override
	public void run() {
		try {
			jobList.clear();
			WorkerPool.addWorker(getId(), this);
			int fx, lx = 0; // first&last tile index  
			int fy, ly = 0; // first&last tile index
			
			double tmp = viewport.getX() / TILE_SIZE;
			if (tmp < 0) {
				fx = (int)Math.ceil(tmp);
			} else {
				fx = (int)Math.floor(tmp);
			}
			
			tmp = viewport.getY() / TILE_SIZE;
			if (tmp < 0) {
				fy = (int)Math.ceil(tmp);
			} else {
				fy = (int)Math.floor(tmp);
			}
			
			tmp = viewport.getMaxX() / TILE_SIZE;
			if (tmp < 0) {
				lx = (int)Math.ceil(tmp);
			} else {
				lx = (int)Math.floor(tmp);
			}
			
			tmp = viewport.getMaxY() / TILE_SIZE;
			if (tmp < 0) {
				ly = (int)Math.ceil(tmp);
			} else {
				ly = (int)Math.floor(tmp);
			}
			
			for (int i = fx-1; i < lx+1; i++) {
				for (int j = fy-1; j < ly+1; j++) {
					if (isInterrupted()) {
						Thread.yield();
						WorkerPool.removeWorker(getId());
						jobList.clear();
						return;
					}
					String fileUrl = new String(String.format(
							is.getHost()
									+ is.getTilesPath() + "/%d"
									+ is.getTileName(),
							scaler.getPointer(), i, j));
					URL url = new URL(fileUrl);
					TileInfo ti = new TileInfo(url, i, j);
					jobList.add(ti);
				}
			}
			Thread.yield();
			WorkerPool.removeWorker(getId());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		RasterThread a = new RasterThread(viewport, bi, is);
		RasterThread b = new RasterThread(viewport, bi, is);
		a.start();
		b.start();
		try {
			a.join();
			b.join();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
