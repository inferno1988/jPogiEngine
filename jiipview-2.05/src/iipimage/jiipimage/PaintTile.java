package iipimage.jiipimage;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.ImageObserver;
/**
 * A single tile in our canvas, while it is being repainted. Create one of
 * these puppies when we start the paint, and it watches the image arriving
 * for us. 
 */
public final class PaintTile implements ImageObserver{
	/* Base parameters for this image. 
	 */
	private final PaintArea parea; // Repainter running us
	private final JIIPImage source; // Source of pixels
	private final CanvasImage can; // Where we paint
	private final Rectangle pos; // Position we paint this tile to
	private final int sub; // With this subsample
	/* State.
	 */
	private final Graphics g; // GC we should paint with
	private final Tile tile; // Tile we are painting
	/* Flag ... set this to kill off this repaint.
	 */
	private boolean kill;
	public PaintTile(
		PaintArea parea,
		JIIPImage source,
		CanvasImage can,
		int x,
		int y,
		int sub) {
		/* Save parameters.
		 */
		this.parea = parea;
		this.source = source;
		this.can = can;
		Dimension tileSize = source.mTileSize;
		pos = new Rectangle(x, y, tileSize.width, tileSize.height);
		this.sub = sub;
		/* Init state.
		 */
		g = can.getSharedGraphics();
		kill = false;
		/* Start image fetch.
		 */
		tile = source.fetchTile(new Tile(source, pos, sub, source.getXFrame()));
	}
	/* Debugging ....
	 */
	public String toString() {
		return ("PaintTile[" + pos + "," + sub + "]");
	}
	/* Do the paint operation. Return true if we were able to paint the
	 * whole tile immediately, false if we've had to queue the request.
	 */
	public boolean doPaint() {
		/* Error on this tile? Do nothing.
		 */
		if (tile.getErr())
			return (true);
		synchronized (can) {
			g.setClip(pos);
			return (g.drawImage(tile.getImage(), pos.x, pos.y, this));
		}
	}
	/* Call this to abandon repaint.
	 */
	public void killRepaint() {
		kill = true;
	}
	/* ImageObserver interface method.
	 */
	public boolean imageUpdate(
		Image im,
		int flgs,
		int x,
		int y,
		int width,
		int height) {
		if ((flgs & ABORT) != 0 || (flgs & ERROR) != 0) {
			/* Help! Tell our caller we are done, note this tile
			 * as containing an error.
			 */
			tile.setErr();
			parea.paintTileDone(this);
			return (false);
		}
		/* Is this tile still visible? If not, kill off this load
		 * immediately.
		 */
		if (!pos.intersects(can.getViewPort())) {
			parea.paintTileDone(this);
			return (false);
		}
		/* Is the subsample still correct? Abandon if not.
		 */
		if (sub != can.getSubsample()) {
			parea.paintTileDone(this);
			return (false);
		}
		/* Has kill been set? Abandon.
		 */
		if (kill) {
			parea.paintTileDone(this);
			return (false);
		}
		/* Just the header? Ask for more.
		 */
		if ((flgs & WIDTH) != 0
			|| (flgs & HEIGHT) != 0
			|| (flgs & PROPERTIES) != 0)
			return (true);
		/* Just part of it.
		 */
		if ((flgs & SOMEBITS) != 0) {
			/* A few bits .. don't bother with part repaints, wait
			 * for the whole lot to come.
			 */
			return (true);
		}
		/* The whole thing!
		 */
		if ((flgs & ALLBITS) != 0 || (flgs & FRAMEBITS) != 0) {
			/* The rest of the image has arrived, paint it. 
			 */
			synchronized (can) {
				g.setClip(pos);
				g.drawImage(im, pos.x, pos.y, null);
			}
			/* Don't need any more updates from this image.
			 */
			parea.paintTileDone(this);
			return (false);
		}
		/* Something wierd!
		 */
		parea.paintTileDone(this);
		return (false);
	}
}
