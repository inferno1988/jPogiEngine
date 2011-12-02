package iipimage.jiipimage;
import java.awt.Rectangle;
import java.util.Enumeration;

public final class PaintArea implements Runnable{
	/* Constant ... the max number of repaints we allow to happen in
	 * parallel.
	 */
	private int maxRepaints = 5;
	/* Static state for this image. 
	 */
	private final JIIPImage source; // Pixels from here
	private final CanvasImage can; // ... to here
	// State.
	private SList pending; // List of PendingRepaint
	private Thread rtid; // Background repaint thread
	private SList running; // List of PaintTile
	public PaintArea(JIIPImage IIPsource, CanvasImage IIPcan) {
		/* Save parameters.
		 */
		source = IIPsource;
		can = IIPcan;
		// Init state.
		pending = new SList();
		running = new SList();
		/* Kick off a background repaint thread for us.
		 */
		rtid = new Thread(this);
		rtid.setPriority(Thread.MIN_PRIORITY);
		rtid.start();
	}
	/**
	 * Print a summary of our state.
	 */
	private void print_state() {
		for (Enumeration e = running.elements(); e.hasMoreElements();) {
			PaintTile pt = (PaintTile) e.nextElement();
		}
	}
	/**
	 * Add a single tile to the pending list. Don't add if it's there
	 * already.
	 */
	private void add_tile(int x, int y, int sub) {
		synchronized (pending) {
			PendingRepaint pr = new PendingRepaint(x, y, sub);
			if (!pending.contains(pr)) {
				pending.pushBack(pr);
				/* Wake up the repaint thread, if it's waiting.
				 */
				pending.notify();
			}
		}
	}
	/**
	 * Add an area to the repaint list. Called from the thread above us
	 * when a new area of the image needs doing. Decompose the area into
	 * tiles, and add each tile to the pending list, if it's not there
	 * already.
	 */
	public void repaintArea(Rectangle dirty, int sub) {
		/* Get tile size.
		 */
		int tw = source.mTileSize.width;
		int th = source.mTileSize.height;
		/* How far into this tile are we?
		 */
		int xoff = dirty.x % tw;
		int yoff = dirty.y % th;
		/* First tile we have to paint.
		 */
		int left = dirty.x - xoff;
		int top = dirty.y - yoff;
		/*
		 * End of area we have to paint.
		 */
		int right = dirty.x + dirty.width;
		int bottom = dirty.y + dirty.height;
		/*
		 * Loop, adding tiles.
		 */
		//StringBuffer tiles = new StringBuffer();
		for (int y = top; y < bottom; y += th)
			for (int x = left; x < right; x += tw)
				//tiles.append("&JTL=" + x + "," + y + "," + sub);
				add_tile(x, y, sub);
	}
	/**
	 * Clear all pending and running repaints ... after a change of scale 
	 * or a big jump, f'rinstance. Called by the thread above us.
	 */
	public void clearRepaint() {
		/* Clear pending.
		 */
		synchronized (pending) {
			pending.empty();
		}
		/* And clear running too.
		 */
		synchronized (running) {
			/* Kill all running repaints. We don't clear running,
			 * we wait for the paintTileDone() callback to be
			 * triggered as the repaint dies.
			 */
			for (Enumeration e = running.elements(); e.hasMoreElements();) {
				PaintTile pt = (PaintTile) e.nextElement();
				pt.killRepaint();
			}
		}
	}
	/**
	 * Called from PaintTile when it finishes ... remove from running
	 * list. Called from the PaintTile thread below us.
	 */
	public void paintTileDone(PaintTile pt) {
		synchronized (running) {
			/* If not there, a blip.
			 */
			if (!running.remove(pt)) {
				return;
			}
			/* If someone is waiting on running, wake them up.
			 */
			running.notify();
			print_state();
		}
	}
	/**
	 * Any pending repaints?
	 */
	private boolean any_pending() {
		synchronized (pending) {
			if (pending.size() == 0)
				return (false);
			else
				return (true);
		}
	}
	/** 
	 * Take a repaintArea off the pending repaint list, wait if empty.
	 */
	private PendingRepaint get_next_repaint() {
		synchronized (pending) {
			try {
				while (!any_pending())
					pending.wait();
			} catch (InterruptedException e) {
				// Should not happen ...
			}
			return ((PendingRepaint) pending.popFront());
		}
	}
	/**
	 * Too many running repaints?
	 */
	private boolean max_running() {
		synchronized (running) {
			if (running.size() < maxRepaints) {
				return (false);
			} else {
				return (true);
			}
		}
	}
	/**
	 * Add a new PaintTile to the running set ... wait if running set is
	 * full.
	 */
	private void add_running(int x, int y, int sub) {
		/* Get tile size.
		 */
		int tw = source.mTileSize.width;
		int th = source.mTileSize.height;
		/* Is this tile still visible? Skip if not.
		 */
		if (!can.getViewPort().intersects(new Rectangle(x, y, tw, th))) {
			return;
		}
		synchronized (running) {
			try {
				while (max_running())
					running.wait();
			} catch (InterruptedException e) {
				// Should not happen ...
			}
			PaintTile pt = new PaintTile(this, source, can, x, y, sub);
			/* If we can't paint immediately, add to run list.
			 */
			if (!pt.doPaint()) {
				running.pushBack(pt);
			}
		}
	}
	/**
	 * Loop, repainting stuff.
	 */
	public void run() {
		for (;;) {
			synchronized (pending) {
				PendingRepaint pr = get_next_repaint();
				/* Process repaint.
				 */
				add_running(pr.x, pr.y, pr.sub);
			}
			print_state();
		}
	}
}
