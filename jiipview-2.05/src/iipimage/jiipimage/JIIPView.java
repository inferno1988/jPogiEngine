package iipimage.jiipimage;
import iipimage.jiipimage.ViewImage;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JFrame;
/**
 * This is the IIPimage Java client
 * in applet we need this parameters:
 * <param name="serverName" value="http://my.server.com/fcgi-bin/iipsrv.fcgi">
 * <param name="imageName" value="/my/path/image/image.tif">
 * as application:
 * java JIIPView http://servername /image/name</p>
 */
public class JIIPView extends JApplet {
	public final int CACHESIZE = 200;
	private static String mServerName;
	private static String serverName = null;
	private static String mImageName = null;
	static String debug = "true";
	private ViewImage vi; // Image view widget
	private JIIPImage iipImage; // Tile source
	public final static String Version = "2.05";
	public static String mName = null;
	private TileCache tileCache;
	// Initialise and set up view.
	public void init() {
		if (mName != "app") {
			try {
				serverName = getParameter("serverName");
				mImageName = getParameter("imageName");
			} catch (NullPointerException e) {
				usage();
			}
		}
		if (serverName == null || mImageName == null) {
			if (mName == "app") {
				System.out.println(
					"fatal error: need serverName "
						+ "and imageName parameters");
			} else {
				usage();
			}
		} else {
			if (serverName.startsWith("http://")) {
				mServerName = serverName;
			} else {
				URL codeBase = getCodeBase();
				mServerName =
					codeBase.getProtocol() + "://" + codeBase.getHost();
				if (codeBase.getPort() != -1) {
					mServerName += ":" + codeBase.getPort();
				}
				mServerName += "/";
				mServerName += serverName;
			}
			getContentPane().setLayout(new BorderLayout());
			// Make tile cache ... this is shared between all views we show. Cache the most recent 250 tiles.
			tileCache = new TileCache(CACHESIZE);
			iipImage = new JIIPImage(this, mServerName, mImageName, tileCache);
			if (iipImage.mNumXFrames > 1) {
				try {
					JIIPImage image = (JIIPImage) iipImage.clone();
					TilePrefetcher fetcher =
						new TilePrefetcher(this, image, tileCache);
					Thread mythread = new Thread(fetcher);
					mythread.setPriority(Thread.MIN_PRIORITY);
					mythread.start();
				} catch (CloneNotSupportedException e) {
					DebugWin.printcerr("Ops! I can't clone...");
				}
			}
			vi = new ViewImage(this, null, iipImage);
			//Build image display whatsit -- fill our Component.
			getContentPane().add(vi, BorderLayout.CENTER);
		}
	} //init()
	private static void usage() {
		final JFrame usage = new JFrame("JIIP view - " + Version + " - Error!");
		usage.getContentPane().setLayout(new BorderLayout());
		JButton c =
			new JButton("<html><h2><center><font color=#FF0000>JIIPView "+ Version +"</center></h2><font color=#000000 size=2><p><center> The IIP viewer applet and application</center></p><p></p><p><center>Usage:</center></p><p>Application -> java JIIPView http://servername /image/name</p><p>Applet -> </p><hr><p>&#169;2004 Denis Pitzalis - denis.pitzalis@gmail.com</p><p>&#169;2001 Ruven Pillay - ruven@free.fr</p><p>&#169;1999 John Cupitt, Steve Perry, Kirk Martinez</p>");
		c.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (mName == null) {
					System.exit(1);
				} else {
					usage.setVisible(false);
				}
			}
		});
		usage.getContentPane().add(c, BorderLayout.CENTER);
		usage.setSize(500, 300);
		usage.setVisible(true);
	}
	public static void main(String[] args) {
		if (args.length != 2) {
			usage();
		} else {
			JFrame frame = new JFrame("JIIP view - " + Version + " - Main");
			serverName = args[0];
			if (!serverName.startsWith("http://")) {
				serverName = "http://" + serverName;
			}
			mImageName = args[1];
			mName = "app";
			JIIPView jv = new JIIPView();
			jv.init();
			frame.setIconImage(
				Toolkit.getDefaultToolkit().createImage("images/jiip.png"));
			frame.setSize(400, 400);
			frame.getContentPane().add(jv);
			frame.pack();
			frame.setVisible(true);
		}
	}
	/**
	 * @param text
	 */
	public void setCache(int text) {
		tileCache.setSize(text);
	}
}
