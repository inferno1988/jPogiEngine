package ua.com.ifno.pogi;

import ua.com.ifno.pogi.AnimationEngine.Animation;
import ua.com.ifno.pogi.AnimationEngine.AnimationCache;
import ua.com.ifno.pogi.AnimationEngine.Animator;
import ua.com.ifno.pogi.LayerEngine.Layer;
import ua.com.ifno.pogi.LayerEngine.LayerManager;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.concurrent.CopyOnWriteArrayList;


@SuppressWarnings("ConstantConditions")
public class GeoWindow extends Canvas implements MouseMotionListener,
		MouseInputListener, ComponentListener, MouseWheelListener, KeyListener, Printable {

	private static final long serialVersionUID = -5210242861777162258L;
	private final AnimationCache animationCache = new AnimationCache();
	private final LayerManager layerManager;
	private final Scaler scaler;
	
	public GeoWindow(LayerManager layerManager, Scaler scaler) {
		addComponentListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
		addKeyListener(this);
		setFocusable(true);
		setVisible(true);
		setBackground(new Color(145, 188, 236));
		setIgnoreRepaint(true);
		setVisible(true);
		this.scaler = scaler;
		this.layerManager = layerManager;
		ScriptEngineManager mgr = new ScriptEngineManager();
		ScriptEngine jsEngine = mgr.getEngineByName("JavaScript");
		try {
			String basePath = getClass().getProtectionDomain().getCodeSource()
					.getLocation().getPath();
			File file = new File(basePath + "/ua/com/ifno/pogi/scripts/animations/");
			File listList[] = file.listFiles();
			for (File f : listList) {
				jsEngine.eval(new FileReader(f));
				Object obj = jsEngine.get("result");
				if (obj instanceof Animation)
					animationCache.addAnimation(f.getName(), (Animation) obj);
			}
		} catch (ScriptException ex) {
			ex.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private static final CopyOnWriteArrayList<Shape> geoBuffer = new CopyOnWriteArrayList<Shape>();

	public void init() {
		Dimension d = getSize();
		initOffscreen(d.width, d.height);
		resetRestoreVolatileImages(d.width, d.height);
	}

	private int fps = 0;
	private int frames = 0;
	private long totalTime = 0;
	private long curTime = System.currentTimeMillis();
	private long lastTime = curTime;
	float alpha = 0.0f;
	private Point mouse = new Point(0, 0);
	private final Animator animator = new Animator();
	private final Rectangle viewPort = new Rectangle();
	private final Color backgroundColor = new Color(145, 188, 236);

	public void paint() {
		Dimension d = getSize();
		Graphics2D graphics = (Graphics2D) getGraphics();
		graphics.setRenderingHint(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_SPEED);
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_OFF);
		initOffscreen(d.width, d.height);
		try {
			// count Frames per second...
			lastTime = curTime;
			curTime = System.currentTimeMillis();
			totalTime += curTime - lastTime;
			if (totalTime > 1000) {
				totalTime -= 1000;
				fps = frames;
				frames = 0;
			}

			if (!((VolatileImage) backBuffer).contentsLost()) {
				resetRestoreVolatileImages(d.width, d.height);
				Graphics2D gBB = (Graphics2D) backBuffer.getGraphics();
				if (layerManager.getLayer(0).isVisible())
					gBB.drawImage((BufferedImage) layerManager.getLayer(0).getDrawable(), 0, 0, null);
				else {
					gBB.setColor(backgroundColor);
					gBB.fill(new Rectangle2D.Double(0, 0, getWidth(), getHeight()));
				}
				for (Layer layer : layerManager.getLayersFrom(1)) {
					if (layer.isVisible()) {
						for (Shape shape : layer.getData()) {
							gBB.setColor(Color.WHITE);
							gBB.fill(shape);
							gBB.setColor(Color.BLACK);
							gBB.draw(shape);
						}
					}
				}
				/*
				Rectangle2D rect = new Rectangle2D.Double(LINE_WIDTH,
						LINE_WIDTH, INFO_RECT_SIZE.getWidth() - LINE_WIDTH,
						INFO_RECT_SIZE.getHeight() - LINE_WIDTH);
				Rectangle2D bound = new Rectangle2D.Double(LINE_WIDTH / 2,
						LINE_WIDTH / 2, INFO_RECT_SIZE.getWidth(),
						INFO_RECT_SIZE.getHeight());
				gBB.setColor(Color.WHITE);
				gBB.fill(rect);
				gBB.setColor(Color.BLACK);
				gBB.setStroke(new BasicStroke(LINE_WIDTH));
				gBB.draw(bound);
				gBB.drawString(String.format("FPS: %s", fps), 20, 20);

				gBB.drawString(
						String.format("Cache size: %s", layerManager.getCacheSize("Background")), 20,
						40);
				gBB.drawString(
						String.format("Image size: %s",
								scaler.getScaledImageSize().width + " | "
										+ scaler.getScaledImageSize().height),
						20, 60);
				int offset = 20;
				for (Integer sc : settings.getScales()) {
					if (scaler.getPointer() == sc) {
						gBB.setColor(Color.RED);
						gBB.drawString(sc.toString(), offset, 80);
						gBB.setColor(Color.BLACK);
					} else {
						gBB.setColor(Color.BLACK);
						gBB.drawString(sc.toString(), offset, 80);
					}
					offset += 20;
				}
				gBB.drawString(String.format("View fx,fy: %sx%s",
						viewPort.getX(), viewPort.getY()), 20, 100);
				gBB.drawString(String.format("View lx,ly: %sx%s",
						viewPort.getMaxX(), viewPort.getMaxY()), 20, 120);
				*/
				if (animator.hasAnimations())
					for (Animation i : animator.getAnimations()) {
						BufferedImage frame = i.getFrame();
						if (frame != null) {
							gBB.drawImage(frame, null,
									mouse.x - (frame.getWidth() / 2), mouse.y
											- (frame.getHeight() / 2));
						} else {
							animator.setPlayed(i);
						}
					}
				gBB.dispose();
				getGraphics().drawImage(backBuffer, 0, 0, this);
			}
			// Let the OS have a little time...
			Thread.yield();
		} finally {
			++frames;
			if (graphics != null)
				graphics.dispose();
		}
	}

	private final Rectangle2D mouseRect = new Rectangle2D.Double(0, 0, 5, 5);

	private Shape find() {
		for (Shape lines : geoBuffer) {
			if (lines.intersects(mouseRect))
				return lines;
		}
		return null;
	}

	private boolean select = false;

	public void setSelected(boolean state) {
		select = state;
	}

	boolean isSelect() {
		return select;
	}

	private final Dimension MINIMUM_SIZE = new Dimension(820, 615);

	@Override
	public Dimension getMinimumSize() {
		return MINIMUM_SIZE;
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		mouse = e.getPoint();
		if (isSelect()) {
			mouseRect.setRect(e.getX() - 5, e.getY() - 5, 10, 10);
			if (find() == null) {
				setCursor(Cursor.getDefaultCursor());
			} else {
				setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}
		}
	}

    @Override
	public void mouseClicked(MouseEvent e) {
		if (isSelect()) {
            Shape selected = find();
			if (selected != null && e.getClickCount() == 1
					&& e.getButton() == MouseEvent.BUTTON1) {

			}
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	private int sx, sy = 0; // start x and y coordinate, used to calculate
							// deltas
	@Override
	public void mouseDragged(MouseEvent e) {
		if (isMove() && SwingUtilities.isLeftMouseButton(e)) {
			if (WorkerPool.hasWorkers())
				WorkerPool.interruptAll();
			int dx, dy;
			dx = sx - e.getX();
			dy = sy - e.getY();
			sx = e.getX();
			sy = e.getY();
			viewPort.translate(dx, dy);
			layerManager.setViewPort(viewPort);
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (isMove() && SwingUtilities.isLeftMouseButton(e)) {
			setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			if (WorkerPool.hasWorkers())
				WorkerPool.interruptAll();
			sx = e.getX();
			sy = e.getY();
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (isMove() && SwingUtilities.isLeftMouseButton(e)) {
			setCursor(Cursor.getDefaultCursor());
		}
	}

	@Override
	public void componentHidden(ComponentEvent e) {
	}

	@Override
	public void componentMoved(ComponentEvent e) {
	}

	@Override
	public void componentResized(ComponentEvent e) {
		Dimension d = getSize();
		viewPort.setSize(d);
		initOffscreen(d.width, d.height);
		resetRestoreVolatileImages(d.width, d.height);
		layerManager.setViewPort(viewPort);
	}

	@Override
	public void componentShown(ComponentEvent e) {
	}

	private boolean move = false;

	public void setMove(boolean move) {
		this.move = move;
	}

	boolean isMove() {
		return move;
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if (WorkerPool.hasWorkers())
			WorkerPool.interruptAll();
		mouse = e.getPoint();
		if (e.getWheelRotation() < 0) {
			zoomIn(e.getPoint());
		} else {
			zoomOut(e.getPoint());
		}
		layerManager.setViewPort(viewPort);
	}

	@Override
	public int print(Graphics graphics, PageFormat pageFormat, int pageIndex)
			throws PrinterException {
		if (pageIndex > 0) { /* We have only one page, and 'page' is zero-based */
			return NO_SUCH_PAGE;
		}

		/*
		 * User (0,0) is typically outside the imageable area, so we must
		 * translate by the X and Y values in the PageFormat to avoid clipping
		 */
		Graphics2D g2d = (Graphics2D) graphics;
		g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
		
		
		g2d.drawImage(backBuffer, 0, 0, this);
		/* tell the caller that this page is part of the printed document */
		return PAGE_EXISTS;
	}

	private void zoomIn(Point p) {
		Animation animation = animationCache.getAnimation("zoomIn.js");
		if (animator.contains_invert(animation)) {
			animation.reset();
			animator.addAnimation(animation);
		}
		viewPort.setLocation(scaler.zoomInTo(p, viewPort));
	}

	private void zoomOut(Point p) {
		Animation animation = animationCache.getAnimation("zoomOut.js");
		if (animator.contains_invert(animation)) {
			animation.reset();
			animator.addAnimation(animation);
		}
		viewPort.setLocation(scaler.zoomOutFrom(p, viewPort));
	}

	private Image backBuffer;

	/**
	 * For any of our images that are volatile, if the contents of the image
	 * have been lost since the last reset, reset the image and restore the
	 * contents.
     * @param w
     * @param h
     */
    void resetRestoreVolatileImages(int w, int h) {
		GraphicsConfiguration gc = this.getGraphicsConfiguration();
		int valCode = ((VolatileImage) backBuffer).validate(gc);
		if (valCode == VolatileImage.IMAGE_INCOMPATIBLE) {
			backBuffer = gc.createCompatibleVolatileImage(w, h);
			layerManager.setSize(w, h);
		}
	}

	/**
	 * Load the duke.gif image, create the sprite and back buffer images, and
	 * render the content into the sprite.
     * @param width
     * @param height
     */
	synchronized void initOffscreen(int width, int height) {
		Dimension d = layerManager.getLayersSize();
		if (backBuffer == null || width != d.width || height != d.height) {
			width = getWidth();
			height = getHeight();
			backBuffer = createVolatileImage(width, height);
			layerManager.setSize(width, height);
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_W)
			viewPort.translate(0, -10);
		if (e.getKeyCode() == KeyEvent.VK_S)
			viewPort.translate(0, 10);
		if (e.getKeyCode() == KeyEvent.VK_D)
			viewPort.translate(10, 0);
		if (e.getKeyCode() == KeyEvent.VK_A)
			viewPort.translate(-10, 0);
		layerManager.setViewPort(viewPort);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		Point p = new Point((int)getBounds().getCenterX(),(int)getBounds().getCenterY());
		mouse.setLocation(p);
		if (e.getKeyCode() == KeyEvent.VK_PAGE_UP)
			zoomIn(p);
		if (e.getKeyCode() == KeyEvent.VK_PAGE_DOWN)
			zoomOut(p);
	}

	@Override
	public void keyTyped(KeyEvent e) {

	}
}