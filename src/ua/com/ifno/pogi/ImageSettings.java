package ua.com.ifno.pogi;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;

import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Palamarchuk Maksym
 */
public class ImageSettings {

	private String tileName = null;

	/**
	 * Creates ImageSettings instance with default values:
	 * 
	 * <br>
	 * http://localhost/yTiles/tile-%d-%d.png
	 */
    private ImageSettings() {
		this.setHost("http://localhost");
		this.setTilesPath("/yTiles");
		this.tileName = "/tile-%d-%d.png";
	}
	
	private Dimension imageSize = new Dimension(256, 256);
	private final Integer[] tscales = { 0, 1, 2, 3, 4, 5 };
	private ArrayList<Integer> scales = new ArrayList<Integer>(
			Arrays.asList(tscales));
	/**
	 * Creates ImageSettings instance with specified parameters:
	 * 
	 * @param host
	 *            - Host name, with protocol (http://example.com)
	 * @param path
	 *            - Path to tiles directory on server (/yTiles)
	 * @param size
	 *            - Image size (default 256)
	 * @param scales
	 *            - List of possible scales (18, 19, 20, 21, 22, 23)
	 */
	public ImageSettings(String host, String path, Dimension size,
			ArrayList<Integer> scales) {
		this.setHost(host);
		this.setTilesPath(path);
		this.imageSize = size;
		this.scales = scales;
	}

	/**
	 * Creates ImageSettings instance with specified parameters:
	 * 
	 * @param host
	 *            - Host name, with protocol (http://example.com)
	 * @param path
	 *            - Path to tiles directory on server (/yTiles)
	 * @param size
	 *            - Image size (default 256)
	 * @param scales
	 *            - List of possible scales (18, 19, 20, 21, 22, 23)
	 * @param tileName
	 *            - Tile name mask (/tile-%d-%d.png)
	 */
	public ImageSettings(String host, String path, Dimension size,
			ArrayList<Integer> scales, String tileName) {
		this.setHost(host);
		this.setTilesPath(path);
		this.imageSize = size;
		this.scales = scales;
		this.tileName = tileName;
	}

	private int tileSize = 256;
	
	/**
	 * Creates ImageSettings instance with specified parameters:
	 * 
	 * @param host
	 *            - Host name, with protocol (http://example.com)
	 * @param path
	 *            - Path to tiles directory on server (/yTiles)
	 * @param size
	 *            - Image size (default 256)
	 * @param scales
	 *            - List of possible scales (18, 19, 20, 21, 22, 23)
	 * @param tileName
	 *            - Tile name mask (/tile-%d-%d.png)
	 * @param tileSize
	 *            - Tile size (256)
	 */
	public ImageSettings(String host, String path, Dimension size,
			ArrayList<Integer> scales, String tileName, int tileSize) {
		this.setHost(host);
		this.setTilesPath(path);
		this.imageSize = size;
		this.scales = scales;
		this.tileName = tileName;
		this.tileSize = tileSize;
	}

	/** Returns image size.
     * @return*/
	public Dimension getImageSize() {
		return imageSize;
	}

	/** Sets image size.
     * @param size*/
    void setImageSize(Dimension size) {
		this.imageSize = size;
	}

	/** Returns image scale levels as list.
     * @return*/
	public ArrayList<Integer> getScales() {
		return scales;
	}

	/** Sets image scale levels as ArrayList.
     * @param scales*/
    void setScales(ArrayList<Integer> scales) {
		this.scales = scales;
	}

	/** Returns tile size.
     * @return*/
	public int getTileSize() {
		return tileSize;
	}

	/** Sets tile size.
     * @param tileSize*/
    void setTileSize(int tileSize) {
		this.tileSize = tileSize;
	}

	/** Returns tile name mask.
     * @return*/
	public String getTileName() {
		return tileName;
	}

	/** Sets tile name mask.
     * @param name*/
    void setTileName(String name) {
		this.tileName = name;
	}
	
	private String host = null;

	/** Returns host name with protocol.
     * @return*/
	public String getHost() {
		return host;
	}

	/** Sets host name with protocol.
     * @param host*/
    void setHost(String host) {
		this.host = host;
	}

	private String path = null;
	
	/** Returns path to tiles folder.
     * @return*/
	public String getTilesPath() {
		return path;
	}

	/** Sets path to tiles folder.
     * @param path*/
    void setTilesPath(String path) {
		this.path = path;
	}

	/**
	 * Parses information from XML.
	 * 
	 * @param url
	 *            - URL for config.xml file.
	 * @throws ConfigurationException 
	 * @return*/
	@SuppressWarnings("JavaDoc")
    public static ImageSettings parseXML(URL url) throws ConfigurationException {
		XMLConfiguration xml = new XMLConfiguration(url);
		ImageSettings is = new ImageSettings();
		is.setImageSize(new Dimension(xml.getInt("image.size[@width]"), xml
				.getInt("image.size[@heigth]")));
		ArrayList<Integer> scales = new ArrayList<Integer>();
		for (Object level : xml.getList("image.scales")) {
			if (level instanceof String)
				scales.add(Integer.parseInt((String) level));
		}
		is.setScales(scales);
		is.setHost(xml.getString("host"));
		is.setTilesPath(xml.getString("tile.path"));
		is.setTileName(xml.getString("tile.name"));
		is.setTileSize(xml.getInt("tile.size"));
		return is;
	}
}