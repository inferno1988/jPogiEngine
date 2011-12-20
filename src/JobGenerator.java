import java.awt.image.BufferedImage;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ArrayBlockingQueue;

public class JobGenerator extends PaintThread {

	public JobGenerator(GeoWindow parent, BufferedImage bi,
			ImageSettings imageSettings) {
		super(parent, bi, imageSettings);
		// TODO Auto-generated constructor stub
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
			setMx(new Double(getGw().getDeltaX() / 256.0).intValue());
			setMy(new Double(getGw().getDeltaY() / 256.0).intValue());
			if (getMx() > 0)
				setMx(0);
			if (getMy() > 0)
				setMy(0);
			for (int i = Math.abs(getMx()); i < Math.abs(getMx()) + 9; i++) {
				for (int j = Math.abs(getMy()); j < Math.abs(getMy()) + 5; j++) {
					if (isInterrupted()) {
						Thread.yield();
						WorkerPool.removeWorker(getId());
						jobList.clear();
						return;
					}
					String fileUrl = new String(String.format(
							"http://192.168.33.110/yTiles/%d/tile-%d-%d.png",
							new Double(getGw().getScaleX()).intValue(), i, j));
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
		RasterThread a = new RasterThread(getGw(), getBi(), getImageSettings());
		RasterThread b = new RasterThread(getGw(), getBi(), getImageSettings());
		a.start();
		b.start();
		try {
			a.join();
			b.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
