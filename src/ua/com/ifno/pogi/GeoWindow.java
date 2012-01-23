package ua.com.ifno.pogi;

import java.awt.BasicStroke;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputListener;

import ua.com.ifno.pogi.AnimationEngine.Animation;
import ua.com.ifno.pogi.AnimationEngine.AnimationCache;
import ua.com.ifno.pogi.AnimationEngine.Animator;
import ua.com.ifno.pogi.LayerEngine.LayerFactory;


public class GeoWindow extends Canvas implements MouseMotionListener,
		MouseInputListener, ComponentListener, MouseWheelListener, Printable {

	private static final long serialVersionUID = -5210242861777162258L;
	private AnimationCache animationCache = new AnimationCache();
	private ImageSettings settings;
	private LayerFactory layerFactory;
	private Scaler scaler;
	
	public GeoWindow(ImageSettings imageSettings, LayerFactory layerFactory, Scaler scaler) {
		addComponentListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
		setVisible(true);
		setBackground(new Color(145, 188, 236));
		setIgnoreRepaint(true);
		setVisible(true);
		this.settings = imageSettings;
		this.scaler = scaler;
		this.layerFactory = layerFactory;
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

	public static CopyOnWriteArrayList<Shape> geoBuffer = new CopyOnWriteArrayList<Shape>();

	public void init() {
		Timer timer = new Timer(true);
		TimerTask tt = new TimerTask() {

			@Override
			public void run() {
				SimplePogiTest.test(viewPort.getX(), viewPort.getY(),
						viewPort.getWidth(), viewPort.getHeight());
			}
		};

		timer.scheduleAtFixedRate(tt, 500, 500);
		Dimension d = getSize();
		initOffscreen(d.width, d.height);
		resetRestoreVolatileImages(d.width, d.height);
	}

	private void drawMap() {
		Graphics2D graphics = (Graphics2D) backBuffer.getGraphics();
		for (Shape shape : GeoWindow.geoBuffer) {
			graphics.setPaint(Color.GREEN);
			graphics.fill(shape);
			graphics.setPaint(Color.BLACK);
			graphics.setStroke(new BasicStroke(2.0f));
			graphics.draw(shape);
		}
		graphics.dispose();
	}

	int fps = 0;
	int frames = 0;
	long totalTime = 0;
	long curTime = System.currentTimeMillis();
	long lastTime = curTime;
	float alpha = 0.0f;
	private Point mouse = new Point(0, 0);
	private Animator animator = new Animator();
	private Dimension INFO_RECT_SIZE = new Dimension(200, 200);
	private int LINE_WIDTH = 2;
	private Rectangle viewPort = new Rectangle();

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
			++frames;

			if (!((VolatileImage) backBuffer).contentsLost()) {
				resetRestoreVolatileImages(d.width, d.height);
				Graphics2D gBB = (Graphics2D) backBuffer.getGraphics();
				for (Object drawing : layerFactory.getScene()) {
					if (drawing instanceof BufferedImage)
						gBB.drawImage((BufferedImage)drawing, 0, 0, null);
				}
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
						String.format("Cache size: %s", layerFactory.getCacheSize("Background")), 20,
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
			// release resources
			if (graphics != null)
				graphics.dispose();
		}
	}

	private Rectangle2D mouseRect = new Rectangle2D.Double(0, 0, 5, 5);

	private Shape find(Point2D p) {
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

	public boolean isSelect() {
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
			if (find(e.getPoint()) == null) {
				setCursor(Cursor.getDefaultCursor());
			} else {
				setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}
		}
	}

	private Shape selected;

	@Override
	public void mouseClicked(MouseEvent e) {
		if (isSelect()) {
			selected = find(e.getPoint());
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
			int dx = 0, dy = 0;
			dx = sx - e.getX();
			dy = sy - e.getY();
			sx = e.getX();
			sy = e.getY();
			viewPort.translate(dx, dy);
			layerFactory.setPosition(viewPort);
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
		layerFactory.setPosition(viewPort);
	}

	@Override
	public void componentShown(ComponentEvent e) {
	}

	private boolean move = false;

	public void setMove(boolean move) {
		this.move = move;
	}

	public boolean isMove() {
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
		layerFactory.setPosition(viewPort);
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
		if (!animator.contains(animation)) {
			animation.reset();
			animator.addAnimation(animation);
		}
		viewPort.setLocation(scaler.zoomInTo(p, viewPort));
	}

	private void zoomOut(Point p) {
		Animation animation = animationCache.getAnimation("zoomOut.js");
		if (!animator.contains(animation)) {
			animation.reset();
			animator.addAnimation(animation);
		}
		viewPort.setLocation(scaler.zoomOutFrom(p, viewPort));
	}

	private int invertSign(int number) {
		return number * -1;
	}

	Image backBuffer;

	/**
	 * For any of our images that are volatile, if the contents of the image
	 * have been lost since the last reset, reset the image and restore the
	 * contents.
	 */
	public void resetRestoreVolatileImages(int w, int h) {
		GraphicsConfiguration gc = this.getGraphicsConfiguration();
		int valCode = ((VolatileImage) backBuffer).validate(gc);
		if (valCode == VolatileImage.IMAGE_INCOMPATIBLE) {
			backBuffer = gc.createCompatibleVolatileImage(w, h);
			layerFactory.setSize(w, h);
		}
	}

	/**
	 * Load the duke.gif image, create the sprite and back buffer images, and
	 * render the content into the sprite.
	 */
	public synchronized void initOffscreen(int width, int height) {
		Dimension d = layerFactory.getLayersSize();
		if (backBuffer == null || width != d.width || height != d.height) {
			width = getWidth();
			height = getHeight();
			backBuffer = createVolatileImage(width, height);
			layerFactory.setSize(width, height);
		}
	}
}