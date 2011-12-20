import java.awt.image.BufferedImage;

public class PaintThread extends Thread {
	private BufferedImage bi;
	private final int TILE_SIZE = 256;
	private BufferedImage img = new BufferedImage(TILE_SIZE, TILE_SIZE,
			BufferedImage.TYPE_INT_ARGB);
	private ImageSettings imageSettings;
	private int mx = 0, my = 0;
	private GeoWindow gw = null;
	private boolean interrupted = false;

	public PaintThread(GeoWindow parent, BufferedImage bi,
			ImageSettings imageSettings) {
		this.setGw(parent);
		this.setBi(bi);
		this.setImageSettings(imageSettings);
	}

	@Override
	public void interrupt() {
		this.interrupted = true;
	}

	@Override
	public boolean isInterrupted() {
		return this.interrupted;
	}

	public BufferedImage getImg() {
		return img;
	}

	public void setImg(BufferedImage img) {
		this.img = img;
	}

	public int getMx() {
		return mx;
	}

	public void setMx(int mx) {
		this.mx = mx;
	}

	public int getMy() {
		return my;
	}

	public void setMy(int my) {
		this.my = my;
	}

	public GeoWindow getGw() {
		return gw;
	}

	public void setGw(GeoWindow gw) {
		this.gw = gw;
	}

	public BufferedImage getBi() {
		return bi;
	}

	public void setBi(BufferedImage bi) {
		this.bi = bi;
	}

	public ImageSettings getImageSettings() {
		return imageSettings;
	}

	public void setImageSettings(ImageSettings imageSettings) {
		this.imageSettings = imageSettings;
	}
}
