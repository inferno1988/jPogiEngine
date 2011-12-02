package iipimage.jiipimage;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.net.URL;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.swing.JApplet;
import javax.swing.JOptionPane;
import javax.swing.JFrame;
/**
 * A class to represent an image on an IIP server.
 * 
 * @author Stephen Perry, John Cupitt, Ruven Pillay
 */
public final class JIIPImage implements Cloneable {
	/**
	 * The Applet that we are in, or null if an application.
	 */
	private JApplet mApplet;
	/**
	 * Name of the server that we are talking to.
	 */
	private static String mServerName;
	/**
	 * Name of the image that we are requesting.
	 */
	private static String mImageName;
	/**
	 * Dimensions of the image.
	 */
	private Dimension mSize;
	/**
	 * The tile size that we are dealing with. Defaults to 64x64, but we should
	 * be able to change this where necessary.
	 */
	public Dimension mTileSize;
	/**
	 * The IIP resolution number giving the highest quality. Resolutions range
	 * <code>from</code> 0 (64x64) to <code>mMaxRes</code>.
	 */
	private int mNumRes;
	/**
	 * Number of frames in image horizontally and vertically.
	 */
	public int mNumXFrames = 1;
	public int mNumYFrames = 1;
	/**
	 * Number of the frame currently being viewed.
	 */
	private int mCurrentXFrame = 0;
	private int mCurrentYFrame = 90;
	/**
	 * List of available vertical views.
	 */
	private Vector vertical_views;
	/**
	 * List of available horizontal views.
	 */
	private Vector horizontal_views;
	/**
	 * JPEG Quality factor
	 */
	public int Q = 75;
	/**
	 * Cache of tiles that have been retrieved from the server.
	 */
	private static TileCache mTileCache;
	/**
	 * Version number used in all IIP messages.
	 */
	private static final String mVer = "OBJ=IIP,1.0&";
	private ImageFilter mImageFilter = null;
	public String colorspace;
	/**
	 * Create a new IIP server connection from an applet.
	 * 
	 * @param applet
	 *            the applet that we are in, or <code>null</code> if none.
	 * @param serverName
	 *            name of the IIP server to connect to.
	 * @param imageName
	 *            name of the image to be requested.
	 */
	public JIIPImage(JApplet applet, String serverName, String imageName,
			TileCache tileCache) {
		mServerName = serverName + "?";
		setImageName(imageName);
		servAlive();
		mTileCache = tileCache;
		// Set default tile size
		mTileSize = getTileSize();
		// Resolution number
		mNumRes = getResNumber();
		// Vertical Views
		vertical_views = getVerticalViewsList();
		mNumYFrames = vertical_views.size();
		// Horizontal Views
		horizontal_views = getHorizontalViewsList();
		mNumXFrames = horizontal_views.size();
	} // IIPImage
	private void servAlive() {
		DebugWin.printc("Attempting to connect to server: " + mServerName);
		try {
			JIIPResponse[] res = getObj(" ");
		} catch (UnknownHostException e) {
			JOptionPane.showMessageDialog(new JFrame(),
					"Can't connect to the server.", "JIIP view - "
							+ JIIPView.Version + " - Connection Error",
					JOptionPane.ERROR_MESSAGE);
		} catch (IOException e) {
		}
	}
	/**
	 * Get the number of resolution
	 * 
	 * @return
	 */
	int getResNumber() {
		try {
			JIIPResponse[] res = getObj("Resolution-number");
			StringTokenizer st = new StringTokenizer(res[1].mResponse);
			mNumRes = Integer.parseInt(st.nextToken());
			DebugWin.prints("Number of Resolution: " + mNumRes);
			return mNumRes;
		} catch (Exception e) {
			DebugWin
					.printcerr("Error getting number of resolutions from server - Setting Default...");
			DebugWin.printcerr("Number of Resolution: 1");
			return 0;
		}
	} // getResNumber
	/**
	 * Try to set the Tile Size getting from the server
	 * 
	 * @return
	 */
	Dimension getTileSize() {
			try {
				mTileSize = new Dimension();
				JIIPResponse[] res = getObj("Tile-size");
				for (int i = 0; i < res.length; i++) {
					if (res[i].mRequest.equalsIgnoreCase("Tile-size")) {
						StringTokenizer st = new StringTokenizer(
								res[i].mResponse);
						mTileSize.width = Integer.parseInt(st.nextToken());
						mTileSize.height = Integer.parseInt(st.nextToken());
					}
				}
				DebugWin.prints("Tile size: " + mTileSize.width + "x"
						+ mTileSize.height);
			} catch (Exception e) {
				DebugWin
						.printserr("Error getting tile size from server - Setting Default...");
				DebugWin.printserr("Tile size: 64x64");
				mTileSize = new Dimension(64, 64);
			}
		return mTileSize;
	} // getTileSize
	/**
	 * Returns the size of the image, or 0 if an error occurred.
	 */
	public Dimension getSize() {
		if (mSize == null) {
			try {
				mSize = new Dimension();
				JIIPResponse[] res = getObj("Max-size");
				for (int i = 0; i < res.length; i++) {
					if (res[i].mRequest.equalsIgnoreCase("Max-size")) {
						StringTokenizer st = new StringTokenizer(
								res[i].mResponse);
						mSize.width = Integer.parseInt(st.nextToken());
						mSize.height = Integer.parseInt(st.nextToken());
					}
				}
				DebugWin.prints("Max size from server: " + mSize.width + "x"
						+ mSize.height);
			} catch (Exception e) {
				DebugWin.printcerr("Error getting image size from the server");
				mSize = new Dimension(0, 0);
			}
		}
		return mSize;
	} // getSize
	/*
	 * These two new functions are for getting the suffixes as a list, rather
	 * than simply getting the number of images horizontally and vertically.
	 * This allows us to change to an angle based suffix system.
	 */
	public Vector getVerticalViewsList() {
		Vector views_list = new Vector();
		try {
			JIIPResponse[] res = getObj("Vertical-views");
			for (int i = 0; i < res.length; i++) {
				if (res[i].mRequest.equalsIgnoreCase("Vertical-views")) {
					StringTokenizer st = new StringTokenizer(res[i].mResponse);
					while (st.hasMoreTokens()) {
						int view = Integer.parseInt(st.nextToken());
						Integer mview = new Integer(view);
						views_list.addElement(mview);
					}
				}
			}
			DebugWin.prints("Number of vertical frames: " + views_list.size());
		} catch (Exception e) {
			DebugWin
					.printcerr("Error getting number of vertical frames from server - Setting Default...");
			DebugWin.printcerr("Number of vertical frames: 0");
			mSize = null;
		}
		return views_list;
	} // getVerticalViewsList
	public Vector getHorizontalViewsList() {
		Vector views_list = new Vector();
		try {
			JIIPResponse[] res = getObj("Horizontal-views");
			for (int i = 0; i < res.length; i++) {
				if (res[i].mRequest.equalsIgnoreCase("Horizontal-views")) {
					StringTokenizer st = new StringTokenizer(res[i].mResponse);
					while (st.hasMoreTokens()) {
						int view = Integer.parseInt(st.nextToken());
						Integer mview = new Integer(view);
						views_list.addElement(mview);
					}
				}
			}
			DebugWin
					.prints("Number of horizontal frames: " + views_list.size());
		} catch (Exception e) {
			DebugWin
					.printcerr("Error getting number of horizontal frames from server - Setting Default...");
			DebugWin.printcerr("Number of horizontal frames: 0");
			mSize = null;
		}
		return views_list;
	} // getHorizontalViewsList
	/**
	 * Set the name of the image to be retrieved. Make this a private function,
	 * as we don't want to be able to make any serious state changes to the
	 * image once it is created. We should then be able to have a cheap copy
	 * routine.
	 */
	private void setImageName(String imageName) {
		mImageName = "FIF=" + imageName + "&";
		mSize = null;
	} // setImageName
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	} //clone
	/**
	 * Sets the <code>TileCache</code> used for this image.
	 */
	public void setTileCache(TileCache tileCache) {
		mTileCache = tileCache;
	}
	/**
	 * Returns the IIP resolution number corresponding to the given VIPS
	 * subsample factor.
	 */
	public int getRes(int sub) {
		int res = mNumRes - (int) (Math.log(sub) / Math.log(2)) - 1;
		return res;
	}
	/**
	 * Returns the VIPS subsample factor corresponding to the given IIP
	 * resolution number.
	 */
	public int getSub(int res) {
		return (int) Math.pow(2, mNumRes - res - 1);
	}
	/**
	 * Returns the IIP tile number for a given coordinate and VIPS subsample
	 * factor.
	 */
	public int getTileNumber(int x, int y, int sub) {
		int scaledImageWidth = mSize.width / sub;
		int tileWidth = mTileSize.width;
		int tileHeight = mTileSize.height;
		int tilesPerRow = (scaledImageWidth / tileWidth)
				+ ((scaledImageWidth % tileWidth) == 0 ? 0 : 1);
		int tileX = (x - (x % tileWidth)) / tileWidth;
		int tileY = (y - (y % tileHeight)) / tileHeight;
		return (tileY * tilesPerRow) + tileX;
	}
	/** ********************************************************* */
	/**
	 * Returns an IIP CGI command that will cause the server to send an 64x64
	 * image tile in JFIF format. We should have a routine that takes the tile
	 * and resolution numbers as well...
	 * 
	 * @param subSample
	 *            VIPS subsample factor.
	 * @param x
	 *            x coordinate of the top left corner of the tile to be
	 *            extracted.
	 * @param y
	 *            y coordinate of the top left corner of the tile to be
	 *            extracted.
	 */
	public String getJFIFcmd(int subSample, int x, int y) {
		if ((mNumXFrames > 1) || (mNumYFrames > 1)) {
			return (mServerName + mImageName + "QLT=" + Q + "&JTLS="
					+ horizontal_views.elementAt(mCurrentXFrame) + ","
					+ getRes(subSample) + "," + getTileNumber(x, y, subSample)
					+ "," + mCurrentYFrame);
		} else {
			return (mServerName + mImageName + "QLT=" + Q + "&JTL="
					+ getRes(subSample) + "," + getTileNumber(x, y, subSample));
		}
	}
	public void setImageFilter(ImageFilter imageFilter) {
		DebugWin.printc("set image filter");
		emptyCache();
		mImageFilter = imageFilter;
	}
	/**
	 * Fetch an image. Pass in a tile representing the area you want, either get
	 * back that tile with an image load started, or get another tile back from
	 * the cache.
	 */
	public synchronized Tile fetchTile(Tile tile) {
		/*
		 * In the cache? Use that instead.
		 */
		Tile ntile = tile;
		if (mTileCache.IsPresent(tile.getCmd())){
			ntile = mTileCache.get(tile.getCmd());
		} else {/*
			 * Need to start a load on this tile.
			 */
			try {
				URL url = new URL(ntile.getCmd());
				Image im;
				if (mApplet != null) {
					if (mImageFilter != null) {
						im = mApplet.createImage(new FilteredImageSource(mApplet
								.getImage(url).getSource(), mImageFilter));
					} else {
						im = mApplet.getImage(url);
					}
				} else {
					Toolkit tk = Toolkit.getDefaultToolkit();
					if (mImageFilter != null) {
						im = tk.createImage(new FilteredImageSource(tk
								.getImage(url).getSource(), mImageFilter));
					} else {
						im = tk.createImage(url);
					}
				}
				ntile.setImage(im);
				mTileCache.put(ntile.getCmd(),ntile);
			} catch (MalformedURLException e) {
				DebugWin.prints("Bad URL: " + e);
			}
			//return (ntile);
		}
		return (ntile);
	}
	/**
	 * Get objects (as in OBJ requests, not tiles) from the server.
	 * <p>
	 * Need to check what the <code>IIPResponse</code> exception actually
	 * means, and if it is really needed.
	 * 
	 * @exception IIPException
	 *                not sure about this one, but it will probably be something
	 *                useful eventually.
	 * @exception IOException
	 *                an IO error occurred while talking to the server.
	 */
	public static JIIPResponse[] getObj(String[] requests) throws IOException {
		JIIPResponse responses[] = null;
		Vector results = new Vector();
		/*
		 * Build the CGI string from all the requests
		 */
		String request = new String(mServerName + mImageName + mVer);
		for (int i = 0; i < requests.length; i++) {
			request = request + "OBJ=" + requests[i] + '&';
		}
		request = request.substring(0, request.length() - 1);
		DebugWin.printc("getObj : " + request);
		try {
			URL url = new URL(request);
			InputStreamReader isr = new InputStreamReader(url.openStream());
			BufferedReader br = new BufferedReader(isr);
			String line;
			/*
			 * Read responses into vector, separating out objects and responses,
			 * and removing the length marker if there is one.
			 */
			while ((line = br.readLine()) != null) {
				int i = line.indexOf(':');
				String obj, res;
				if (i != -1) {
					obj = line.substring(0, i);
					res = line.substring(i + 1);
					if ((i = obj.indexOf('/')) != -1) {
						obj = obj.substring(0, i);
					}
				} else {
					obj = "(error)";
					res = "(error)";
				}
				results.addElement(new JIIPResponse(obj, res));
			}
			br.close();
			/*
			 * Copy results from vector into array for return
			 */
			responses = new JIIPResponse[results.size()];
			for (int i = 0; i < results.size(); i++) {
				responses[i] = (JIIPResponse) results.elementAt(i);
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
			throw new Error("Got a malformed URL");
		}
		return responses;
	}
	/**
	 * Get objects (as in OBJ requests, not tiles) from the server.
	 * 
	 * @exception IIPException
	 *                an IIP error was generated by the server.
	 * @exception IOException
	 *                an IO error occurred while talking to the server.
	 */
	public static JIIPResponse[] getObj(String request) throws IOException {
		String[] requests = new String[1];
		requests[0] = request;
		return getObj(requests);
	}
	/**
	 * Return the number of frames in the image both horizontally vertically.
	 */
	public int getNumXFrames() {
		return mNumXFrames;
	}
	public int getNumYFrames() {
		return mNumYFrames;
	}
	/**
	 * Return the index of the current frame.
	 */
	public int getXFrame() {
		return mCurrentXFrame;
	}
	public int getYFrame() {
		return mCurrentYFrame;
	}
	//public int getHorizontalViewAngle(int index) {
	//	Integer return_value = (Integer) horizontal_views.elementAt(index);
	//	return return_value.intValue();
	//}
	/**
	 * Set the index of the current frame. Should throw an exception on an error
	 * condition.
	 */
	public void setXFrame(int xframe) {
		if (xframe < 0)
			xframe = (mNumXFrames - 1);
		if (xframe == mNumXFrames)
			xframe = 0;
		DebugWin.printc("set frame x= " + xframe + " y= " + mCurrentYFrame);
		if (xframe >= 0 && xframe < mNumXFrames) {
			mCurrentXFrame = xframe;
		}
	}
	public void setYFrame(int yframe) {
		if (yframe < 0)
			yframe = (mNumYFrames - 1);
		if (yframe == mNumYFrames)
			yframe = 0;
		DebugWin.printc("set frame x= " + mCurrentXFrame + " y= " + yframe);
		if (yframe >= 0) {
			emptyCache();
			mCurrentYFrame = getVerticalViewAngle(yframe);
		}
	}
	public int getVerticalViewAngle(int index) {
		int mCurrentYFrame = (new Integer(vertical_views.elementAt(index)
				.toString())).intValue();
		return mCurrentYFrame;
	}
	/**
	 * Empty the cache of tiles received from the server.
	 */
	public void emptyCache() {
		DebugWin.printc("emptying image cache");
		Runtime rt = Runtime.getRuntime();
		mTileCache.clear();
		rt.gc();
	}
	/**
	 * @param i
	 */
	protected int findYFrame(int i) {
		int j = 0;
		for (j = 0; j < vertical_views.size(); j++) {
			if ((new Integer(vertical_views.elementAt(j).toString()))
					.intValue() == i)
				break;
		}
		return j;
	}
}