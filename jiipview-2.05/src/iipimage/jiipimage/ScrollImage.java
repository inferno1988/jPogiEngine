package iipimage.jiipimage;
import iipimage.jiipimage.ViewImage;

import java.awt.Adjustable;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputListener;
public class ScrollImage
	extends JPanel
	implements AdjustmentListener, MouseInputListener, MouseWheelListener {
	JPanel colRules;
	private JToggleButton isMetric;
	// Magic scrollbar thickness constant.
	private static final int scrollbarWidth = 18;
	// Static parameters.
	private final JIIPImage source; // Pixel source
	private final ViewImage view;
	// For mouse drag ... use these during a drag scroll.
	private boolean dscroll; // Set on mouse down
	private Point mstart; // Start mouse position
	private Point vpstart; // Start viewport position
	// Our widgets.
	final CanvasImage can; // Handle repaints
	//private JScrollBar hbar; // Scrollbars
	//private JScrollBar vbar;
	private final Cursor defc; // Cursors
	private final Cursor movec;
	private boolean mFirstPaint = true;
	private int mStartPosition[];
	private boolean mMultiFrame;
	private Rule columnView;
	private Rule rowView;
	//
	public ScrollImage(JIIPImage source, ViewImage view) {
		//
		// Static state stuff.
		this.source = source;
		this.view = view;
		/* Init state.
		 */
		defc = new Cursor(Cursor.DEFAULT_CURSOR);
		movec = new Cursor(Cursor.MOVE_CURSOR);
		dscroll = false;
		mstart = new Point();
		vpstart = null;
		mMultiFrame = (source.mNumXFrames > 1);
//		vbar = null;
//		hbar = null;
		/* Set our layout style.
		 */
		setLayout(new BorderLayout());
		setBackground(Color.BLACK);
		/* Make widgets.
		 */
		can = new CanvasImage(source);
		can.addMouseListener(this);
		can.addMouseMotionListener(this);
		can.addMouseWheelListener(this);
		/*
		//Horizontal bar
		hbar = new JScrollBar(JScrollBar.HORIZONTAL);
		hbar.setBackground(can.getBackground());
		hbar.addAdjustmentListener(this);
		/* 
		 * We need to move the right end of the hbar left by the width
		 * of the vbar ... this is horrible. Add a tiny canvas to use
		 * as padding.
		 */
		/*
		JPanel p1 = new JPanel(new BorderLayout());
		p1.setBackground(can.getBackground());
		Canvas padding = new Canvas();
		//JPanel padding = new JPanel();
		padding.setSize(scrollbarWidth + 3, 1);
		p1.add(padding, BorderLayout.EAST);
		p1.add(hbar, BorderLayout.CENTER);
		add(p1, BorderLayout.SOUTH);
		//Vertical bar
		vbar = new JScrollBar(JScrollBar.VERTICAL);
		vbar.setBackground(can.getBackground());
		vbar.addAdjustmentListener(this);
		add(vbar, BorderLayout.EAST);
		*/
		add(can, BorderLayout.CENTER);
		//set the rule, I hope...
		colRules = new JPanel(new BorderLayout());
		columnView = new Rule(Rule.HORIZONTAL, true);
		columnView.setPreferredWidth(can.getCSize().width);
		//Create the corner.
		JPanel buttonCorner = new JPanel(); //use FlowLayout
		buttonCorner.setBackground(can.getBackground());
		buttonCorner.setForeground(Color.white);
		isMetric = new JToggleButton("inc", true);
		isMetric.setFont(new Font("SansSerif", Font.PLAIN, 11));
		isMetric.setMargin(new Insets(2, 2, 2, 2));
		isMetric.setBackground(can.getBackground());
		isMetric.setForeground(Color.WHITE);
		isMetric.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					//Turn it to metric.
					rowView.setIsMetric(true);
					columnView.setIsMetric(true);
					isMetric.setText("inc");
				} else {
					//Turn it to inches.
					rowView.setIsMetric(false);
					columnView.setIsMetric(false);
					isMetric.setText("cm");
				}
				//Rule.setMaxUnitIncrement(rowView.getIncrement());
				DebugWin.printc("Changing scale unit");
			}
		});
		buttonCorner.add(isMetric);
		colRules.add(buttonCorner, BorderLayout.WEST);
		colRules.add(columnView, BorderLayout.CENTER);
		JPanel rowRules = new JPanel(new BorderLayout()); 
		rowView = new Rule(Rule.VERTICAL, true);
		rowView.setPreferredHeight(can.getCSize().height);
		//add(colRules, BorderLayout.NORTH);
		//add(rowView, BorderLayout.WEST);
		/* 
		 * Set the bars to their start state.
		 */
		refresh_bars();
	}
	/* Set the scrollbars from our variables.
	 */
	private void refresh_bars() {
		// Read Canvas state.
		Point p = can.getPosition();
		Dimension cs = can.getCSize();
		Dimension vp = can.getSize();
		//hbar.setValues(p.x, 0, 0, cs.width);
		//vbar.setValues(p.y, 0, 0, cs.height);
	}
	/* Intercept all resize requests for this panel, and update the scroll
	 * bars etc.
	 */
	public void setBounds(int x, int y, int width, int height) {
		/* 
		 * Do the real setBounds().
		 */
		super.setBounds(x, y, width, height);
		/* Update our viewport size. Do a layout before we read the
		 * canvas size, to make sure the canvas knows about our new
		 * dimensions.
		 */
		doLayout();
		refresh_bars();
	}
	/* Set a new scale and position.
	 */
	public void setViewParameters(int sub, int x, int y) {
		/* Tell the canvas, then update the scroll bars.
		 */
		can.setViewParameters(sub, x, y);
		refresh_bars();
	}
	/* Just set a new position.
	 */
	public void setPosition(int x, int y) {
		/* Tell the canvas, then update the scroll bars.
		 */
		can.setPosition(x, y);
		refresh_bars();
	}
	// Start of a mouse-scroll event.
	private void scrollStart(Point p) {
		// Into scroll mode.
		dscroll = true;
		// Save the start point of the mouse in screen coordinates.
		mstart.x = p.x;
		mstart.y = p.y;
		// Save the start point of the viewport.
		vpstart = can.getPosition();
		// Set a drag cursor.
		can.setCursor(movec);
	}
	/* 
	 * End of a mouse-scroll event.
	 */
	private void scrollStop() {
		/* 
		 * Out of scroll mode.
		 */
		dscroll = false;
		/* 
		 * Unset the cursor.
		 */
		can.setCursor(defc);
	}
	/* A drag event .. scroll the view. 
	 */
	private void scrollMotion(Point p) {
		/* Make sure we're in scroll mode.
		 */
		if (!dscroll)
			return;
		/* How much has the mouse moved?
		 */
		int dx = p.x - mstart.x;
		int dy = p.y - mstart.y;
		/* Offset viewport by that much from start position.
		 */
		setPosition(vpstart.x - dx, vpstart.y - dy);
	}
	/* Read stuff from state. Just pass down to our CanvasImage. We can't
	 * inherit these, as we can't do multiple inheritiance :(
	 */
	public Point getPosition() {
		return (can.getPosition());
	}
	public Rectangle getViewPort() {
		return (can.getViewPort());
	}
	public Dimension getImSize() {
		return (can.getImSize());
	}
	public Dimension getCsize() {
		return (can.getCSize());
	}
	public int getSubsample() {
		return (can.getSubsample());
	}
	public void redraw() {
		can.redraw();
		view.whatimage.setText(
			"H: " + source.getXFrame() + " V: " + source.getYFrame());
	}
	// Listener callbacks.
	// Scrollbar event handler.
	public void adjustmentValueChanged(AdjustmentEvent e) {
		int v = e.getValue();
		Adjustable adj = e.getAdjustable();
		Point p = can.getPosition();
		if (adj.getOrientation() == Adjustable.HORIZONTAL)
			setPosition(v, p.y);
		else
			setPosition(p.x, v);
	}
	// MouseMotion interface.
	public void mouseClicked(MouseEvent e) {
		if (SwingUtilities.isLeftMouseButton(e)) {
			view.popNewView();
		}
	}
	public void mouseEntered(MouseEvent e) {
	}
	public void mouseExited(MouseEvent e) {
	}
	public void mouseMoved(MouseEvent e) {
	}
	public void mouseDragged(MouseEvent e) {
		if (mMultiFrame && (SwingUtilities.isRightMouseButton(e))) {
			Point p = e.getPoint();
			if (p.x > mstart.x) {
				source.setXFrame(source.getXFrame() + 1);
			} else if (p.x < mstart.x) {
				source.setXFrame(source.getXFrame() - 1);
			}
			mstart.x = p.x;
			mstart.y = p.y;
			//redraw();
		} else if (SwingUtilities.isLeftMouseButton(e)) {
			scrollMotion(e.getPoint());
		}
	}
	public void mouseReleased(MouseEvent e) {
		if (mMultiFrame && (SwingUtilities.isRightMouseButton(e))) {
		} else if (SwingUtilities.isLeftMouseButton(e)) {
			scrollStop();
			redraw();
		}
	}
	public void mousePressed(MouseEvent e) {
		if (mMultiFrame && (SwingUtilities.isRightMouseButton(e))) {
			Point p = e.getPoint();
			mstart.x = p.x;
			mstart.y = p.y;
		} else if (SwingUtilities.isLeftMouseButton(e)) {
			scrollStart(e.getPoint());
		}
	}
	public void paint(Graphics g) {
		//super.paint(g);
		if (mFirstPaint) {
			mFirstPaint = false;
			DebugWin.printc("ScollImage - first paint");
			if (mStartPosition == null) {
				DebugWin.printc("ScrollImage: setViewParameters(0, 0, 0)");
				setViewParameters(0, 0, 0);
			} else {
				DebugWin.printc(
					"ScrollImage: setViewParameters("
						+ mStartPosition[0]
						+ ", "
						+ mStartPosition[1]
						+ ", "
						+ mStartPosition[2]
						+ ")");
				setViewParameters(
					mStartPosition[0],
					mStartPosition[1],
					mStartPosition[2]);
				mStartPosition = null;
			}
		}
	}
	public int[] getRealPosition() {
		Point p = can.getPosition();
		int r[] = new int[3];
		r[0] = can.getSubsample();
		r[1] = p.x / r[0];
		r[2] = p.y / r[0];
		return r;
	}
	public void mouseWheelMoved(MouseWheelEvent e) {
		if (e.getWheelRotation() > 0) {
			view.zoomIn();
		} else {
			view.zoomOut();
		}
	}
	public void setBg(Color color){
		can.setBackground(color);
		redraw();
		}
}