package iipimage.jiipimage;
import java.awt.Rectangle;
import java.awt.MediaTracker;
import java.awt.Dimension;
public final class TilePrefetcher implements Runnable {
	/**
	 * The image that the tiles are fetched from.
	 */
	private JIIPImage mImage;
	/**
	 * The tile cache that is to be filled.
	 */
	private TileCache mTileCache;
	/** The View we are part of.
	 */
	private JIIPView mView;
	/**
	 * The tile size;
	 */
	private Dimension tileSize;
	public TilePrefetcher(JIIPView view, JIIPImage image, TileCache tileCache) {
		mImage = image;
		mTileCache = tileCache;
		mView = view;
		tileSize = image.mTileSize;
	}
	// Ruven: Added reload function for angle changes
	public void Reload() {
		// mThread = new Thread(this, "TilePrefetcher");
		// mThread.start();
		int xframe, nXFrames;
		if ((nXFrames = mImage.mNumXFrames) == 0) {
			DebugWin.printc("single frame image, nothing to prefetch");
			return;
		}
		int sub = mImage.getSub(0);
		MediaTracker tracker = new MediaTracker(mView);
		Tile t;
		int w = tileSize.width;
		int h = tileSize.height;
		for (xframe = 0; xframe < nXFrames; xframe++) {
			mImage.setXFrame(xframe);
			t = new Tile(mImage, new Rectangle(0, 0, w, h), sub, xframe);
			mImage.fetchTile(t);
			tracker.addImage(t.getImage(), xframe);
			t =
				new Tile(
					mImage,
					new Rectangle(0, 0, w, h),
					(int) ((sub / 2) + 1),
					xframe);
			mImage.fetchTile(t);
			tracker.addImage(t.getImage(), xframe + nXFrames);
			t =
				new Tile(
					mImage,
					new Rectangle(0, h, w, h),
					(int) ((sub / 2) + 1),
					xframe);
			mImage.fetchTile(t);
			tracker.addImage(t.getImage(), xframe + nXFrames + nXFrames);
			t =
				new Tile(
					mImage,
					new Rectangle(w, 0, w, h),
					(int) ((sub / 2) + 1),
					xframe);
			mImage.fetchTile(t);
			tracker.addImage(t.getImage(), xframe + nXFrames + nXFrames);
			t =
				new Tile(
					mImage,
					new Rectangle(w, h, w, h),
					(int) ((sub / 2) + 1),
					xframe);
			mImage.fetchTile(t);
			tracker.addImage(t.getImage(), xframe + nXFrames + nXFrames);
		}
		DebugWin.printc("starting media tracker...");
		try {
			tracker.waitForAll();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (tracker.isErrorAny()) {
			DebugWin.printcerr(
				"warning - was not able to load all toplevel images");
		}
		DebugWin.printc("done TilePrefetcher");
	}
	public void run() {
		DebugWin.printc("TilePrefetcher: run");
		Reload();
	}
}
