package net.ifno.com.ua;
import java.net.URL;

public class TileInfo {
	private URL url;
	private int i;
	private int j;

	public TileInfo(URL url, int i, int j) {
		this.url = url;
		this.i = i;
		this.j = j;
	}

	public URL getUrl() {
		return url;
	}

	public void setUrl(URL url) {
		this.url = url;
	}

	public int getI() {
		return i;
	}

	public void setI(int i) {
		this.i = i;
	}

	public int getJ() {
		return j;
	}

	public void setJ(int j) {
		this.j = j;
	}

}
