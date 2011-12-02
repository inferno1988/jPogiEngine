package iipimage.jiipimage;
import java.awt.Image;
import java.awt.Rectangle;
public final class Tile {
	/* Parameters.
	 */
	private final JIIPImage source;
	private final Rectangle pos; // Geometry
	private final int sub; // Other params from fetch
	private final int frame;
	// private ReadRaw rr;
	/* State.
	 */
	private Image im; // Image for this tile
	private String cmd; // URL for this tile
	private boolean err; // Set if this fetch caused an error
	public Tile next; // Link LRU with these
	public Tile prev;
	public Tile(JIIPImage source, Rectangle pos, int sub, int frame) {
		/* Save parameters.
		 */
		this.source = source;
		this.sub = sub;
		this.frame = frame;
		this.im = null;
		this.cmd = null;
		this.err = false;
		this.next = null;
		this.prev = null;
		// this.rr = null;
		/* Clip tile size against image size.
		 */
		Rectangle ims = new Rectangle(source.getSize());
		ims.width /= sub;
		ims.height /= sub;
		this.pos = ims.intersection(pos);
	}
	/* Read state.
	 */
	public JIIPImage getSource() {
		return (source);
	}
	public Rectangle getPos() {
		return (pos);
	}
	public int getSub() {
		return (sub);
	}
	public int getXFrame() {
		return frame;
	}
	public boolean isLoaded() {
		return (im != null);
	}
	public Image getImage() {
		return (im);
	}
	public boolean getErr() {
		return (err);
	}
	public void setErr() {
		err = true;
	}
	/* Make up a String for this URL, if we've not made one already.
	 */
	public synchronized String getCmd() {
		if (cmd == null) {
			cmd = source.getJFIFcmd(sub, pos.x, pos.y);
		}
		return (cmd);
	}
	/* Set the image field.
	 */
	public synchronized void setImage(Image im) {
		if (this.im != null)
			DebugWin.printcerr("panic ... image already set");
		this.im = im;
	}
	// public synchronized void setReader (ReadRaw rr) {
	// Debug.assert((this.rr == null ? true : this.rr.isComplete()),
	// "this.rr == null ? true : this.rr.isComplete()");
	// this.rr = rr;
	// }
	/* Override these for use of cache.
	 */
	public int hashCode() {
		// Just eor hash for members
		return (pos.hashCode() ^ sub ^ frame ^ source.hashCode());
	}
	public boolean equals(Object obj) {
		Tile tile = (Tile) obj;
		/* Test params, don't test image.
		 */
		if (sub == tile.getSub()
			&& frame == tile.getXFrame()
			&& pos.equals(tile.getPos())
			&& source.equals(tile.getSource()))
			return (true);
		return (false);
	}
}
