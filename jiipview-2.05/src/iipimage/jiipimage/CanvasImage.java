package iipimage.jiipimage;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

/**
 * A Canvas which displays part of a large virtual image. Attach
 * scrollbars to this to get a ScrollImage
 */
public class CanvasImage extends Canvas {
	/**
	 * Source of pixels.
	 */
	private final JIIPImage source;
	/**
	 * Use this to paint.
	 */
	private final PaintArea parea;
	/**
	 * Size of image we are displaying.
	 */
	private Dimension csize;
	/**
	 * Size and position of viewport.
	 */
	Rectangle vp;
	/**
	 * GC we paint with.
	 */
	private Graphics g;
	/**
	 * Current subsample factor.
	 */
	private int sub = 0;
	/**
	 * Stored start position - can't use until we have a gc
	 */
	private int mStartSub = -1;
	private int mStartX;
	private int mStartY;
	public CanvasImage(JIIPImage source) {
		// Static state stuff.
		this.source = source;
		// Dynamic state init.
		csize = new Dimension(source.getSize());
		vp = new Rectangle();
		sub = 1;
		g = null;
		setBackground(Color.black);
		parea = new PaintArea(source, this);
	}
	/**
	 * Pick a subsample which will fit the image inside the viewport.
	 */
	public int fitview() {
		int s;
		// Just double sub until the image fits inside vport.
		for (s = 1; (source.getSize().width / s > vp.width) || (source.getSize().height / s > vp.height); s *= 2);
		return s;
	}
	/**
	 * Set a new subsample factor, but don't repaint.
	 */
	public void setsub(int sub) {
		/* 
		 * Special case ... zero means shrink-to-fit.
		 */
		if (sub == 0)
			sub = fitview();
		/* 
		 * Change scale.
		 */
		this.sub = sub;
		/* 
		 * Calculate new geometry.
		 */
		csize.width = source.getSize().width / sub;
		csize.height = source.getSize().height / sub;
	}

	/* 
	 * Set a position, but don't repaint.
	 */
	private void setpos(int x, int y) {
		/* 
		 * Translate our graphics origin.
		 */
		if (g != null) {
			synchronized (this) {
				g.translate(vp.x - x, vp.y - y);
			}
		} else {
			DebugWin.printcerr("didn't have graphics context");
		}
		vp.x = x;
		vp.y = y;
	}
	private void setpos(Point p) {
		setpos(p.x, p.y);
	}
	/**
	 * Look at pos (proposed viewport position), and move it
	 * left/right/whatever to try to get the image visible.
	 */
	private Point normalise(Point pos) {
		Point np = new Point(pos);
		/* Too far right/down? Move it left/up.
		 */
		np.x = Math.min(csize.width - vp.width, np.x);
		np.y = Math.min(csize.height - vp.height, np.y);
		/* Make sure it's >0.
		 */
		np.x = Math.max(0, np.x);
		np.y = Math.max(0, np.y);
		return (np);
	}
	/**
	 * Set a new subsample factor and position.
	 */
	public void setViewParameters(int sub, int x, int y) {
		if (g == null) {
			//DebugWin.printc("CanvasImage: setViewParameters - waiting for GC");
			mStartSub = sub;
			mStartX = x;
			mStartY = y;
			return;
		}
		if (sub != this.sub) {
			//DebugWin.printc("CanvasImage: doing complete repaint");
			setsub(sub);
			setpos(normalise(new Point(x, y)));
			// should probably just call redraw to do this...
			parea.clearRepaint();
			if (g != null) {
				Dimension d = getSize();
				g.setClip(0, 0, d.width, d.height);
				g.clearRect(0, 0, d.width, d.height);
			}
			repaint();
		} else {
			/* Just setPosition().
			 */
			setPosition(x, y);
		}
	}
	/************************************************************/
	/**
	 * Scroll to position (x,y).
	 */
	public void setPosition(int x, int y) {
		// Compute new viewport position.
		Point np = normalise(new Point(x, y));
		// No change? Easy!
		if (np.equals(vp))
			return;
		// Any pixels in common between the two views?
		Rectangle nvp = new Rectangle(np.x, np.y, vp.width, vp.height);
		Rectangle in = nvp.intersection(vp);
		if (in.isEmpty()) {
			// No pixels in common ... total repaint, easy.
			setpos(np);
			parea.clearRepaint();
			//repaint();
			return;
		}
		// Blit the common pixels to their new spot.
		synchronized (this) {
			g.setClip(vp);
			g.copyArea(
				in.x,
				in.y,
				in.width,
				in.height,
				vp.x - nvp.x,
				vp.y - nvp.y);
		}
		// Shift the graphics origin.
		setpos(np);
		/* 
		 * Now ... repaint four patches around the bit of new image.
		 * The vertical strips to the left and right of in, and the
		 * horizontal strips above and below in.
		 */
		Rectangle r = new Rectangle();
		r.x = nvp.x;
		r.y = nvp.y;
		r.width = in.x - nvp.x;
		r.height = in.height;
		paint_area(r);
		r.x = in.x + in.width;
		r.y = in.y;
		r.width = (nvp.x + nvp.width) - (in.x + in.width);
		r.height = in.height;
		paint_area(r);
		r.x = nvp.x;
		r.y = nvp.y;
		r.width = nvp.width;
		r.height = in.y - nvp.y;
		paint_area(r);
		r.x = nvp.x;
		r.y = in.y + in.height;
		r.width = nvp.width;
		r.height = (nvp.y + nvp.height) - (in.y + in.height);
		paint_area(r);
	}
	/**
	 * Intercept all resize requests for this canvas, and update our vars.
	 */
	public void setBounds(int x, int y, int width, int height) {
		/* Do the real setBounds().
		 */
		super.setBounds(x, y, width, height);
		/* Update our viewport size. 
		 */
		vp.width = width;
		vp.height = height;
		/* Bounce the image back into view.
		 */
		setpos(normalise(new Point(vp.x, vp.y)));
	}
	/* Read stuff from state.
	 */
	public Point getPosition() {
		return (new Point(vp.x, vp.y));
	}
	public Rectangle getViewPort() {
		return (new Rectangle(vp));
	}
	public Dimension getImSize() {
		return (source.getSize());
	}
	public Dimension getCSize() {
		return (new Dimension(csize));
	}
	public int getSubsample() {
		return (sub);
	}
	public Graphics getSharedGraphics() {
		return (g);
	}
	/**
	 * Paint a rect of the canvas.
	 */
	private void paint_area(Rectangle dirty) {
		// Clip against image size.
		Rectangle d2 =
			dirty.intersection(new Rectangle(0, 0, csize.width, csize.height));
		// Clip against viewport.
		Rectangle d3 = d2.intersection(vp);
		// Anything left?
		if (d3.isEmpty())
			return;
		// And paint that part.
		parea.repaintArea(d3, sub);
	}
	public void update(Graphics g) {
		paint(g);
	}
	/**
	 * Override paint to do this bit of the virtual image.
	 */
	public void paint(Graphics sysg) {
		//super.paint(sysg);
		DebugWin.printc("CanvasImage: paint");
		/* Extract dirty area.
		 */
		Rectangle r = sysg.getClipBounds();
		/* Is this our first expose? Set up our graphics state.
		 */
		if (g == null) {
			DebugWin.printc("CanvasImage: creating gc");
			/* Make our own GC ... we will use this for all 
			 * paints to this canvas.
			 *
			 * We have to synchronize access to this GC, since it
			 * is shared with background paint processes. Use this
			 * CanvasImage as the synchronisation lock.
			 */
			g = (Graphics) sysg.create();
			setsub(fitview());
			if (mStartSub != -1) {
				DebugWin.printc("CanvasImage: setting start position");
				setViewParameters(mStartSub, mStartX, mStartY);
				mStartSub = -1;
			}
		}
		// Translate to virtual canvas coordinates.
		 	r.translate(vp.x, vp.y);
		// And call our repainter.
		paint_area(r);
	}
	public void redraw() {
		parea.clearRepaint();
		repaint();
	}
}
