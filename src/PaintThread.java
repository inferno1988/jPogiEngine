import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class PaintThread extends Thread {
	private Graphics2D g2 = null;
	private BufferedImage img = new BufferedImage(256, 256,
			BufferedImage.TYPE_INT_ARGB);
	private int mx = 0, my = 0;
	private GeoWindow gw = null;
	private boolean interrupted = false;
	
	public PaintThread(GeoWindow parent, Graphics2D g2d) {
		this.setGw(parent);
		this.setG2(g2d);
	}
	
	@Override
	public void interrupt() {
		this.interrupted = true;
	}
	
	@Override
	public boolean isInterrupted() {
		return this.interrupted;
	}

	public Graphics2D getG2() {
		return g2;
	}

	public void setG2(Graphics2D g2) {
		this.g2 = g2;
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
}
