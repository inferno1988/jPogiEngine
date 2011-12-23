package net.ifno.com.ua;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputListener;

import net.ifno.com.ua.AnimationEngine.Animation;
import net.ifno.com.ua.AnimationEngine.AnimationCache;
import net.ifno.com.ua.AnimationEngine.Animator;
import net.ifno.com.ua.geoObjects.GeoLineMaker;
import net.ifno.com.ua.geoObjects.GeoObjMaker;
import net.ifno.com.ua.geoObjects.GeoObjShape;
import net.ifno.com.ua.geoObjects.GeoPolyMaker;

public class GeoWindow extends Canvas implements MouseMotionListener,
		MouseInputListener, ComponentListener, MouseWheelListener, Printable {

	private static final long serialVersionUID = -5210242861777162258L;
	private Rectangle2D mouseRect = new Rectangle2D.Double(0, 0, 5, 5);
	private CopyOnWriteArrayList<GeoObjMaker> geoBuffer = new CopyOnWriteArrayList<GeoObjMaker>();
	private ImageSettings settings;
	private boolean select = false;
	private boolean move = false;
	private GeoObjMaker selected;
	private double deltaX = 0, startX = 0, sx = 0, scaleX = 18.0;
	private double deltaY = 0, startY = 0, sy = 0, scaleY = 18.0;
	private Rectangle viewPort = new Rectangle();
	private BufferedImage bi = null;
	private BufferStrategy buffer = null;
	private Scaler scaler;
	private int LINE_WIDTH = 2;
	private Dimension INFO_RECT_SIZE = new Dimension(200, 200);
	private Animator animator = new Animator();
	private AnimationCache animationCache = new AnimationCache();
	private Point mouse = new Point(0, 0);

	public GeoWindow(ImageSettings imageSettings) {
		addComponentListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
		setVisible(true);
		setIgnoreRepaint(true);
		setBackground(new Color(145, 188, 236));
		setSize(getSize());
		setIgnoreRepaint(true);
		setVisible(true);
		this.settings = imageSettings;
		scaler = new Scaler(this.settings);
		ScriptEngineManager mgr = new ScriptEngineManager();
		ScriptEngine jsEngine = mgr.getEngineByName("JavaScript");
		try {
			File file = new File("bin/scripts/animations/");
			File listList[] = file.listFiles();
			for (File f : listList) {
				jsEngine.eval(new FileReader(f));
				Object obj = jsEngine.get("result");
				if (obj instanceof Animation)
					animationCache.addAnimation(f.getName(), (Animation)obj);
				System.out.println(f.getName());
			}
		} catch (ScriptException ex) {
			ex.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public double getStartX() {
		return startX;
	}

	public void setStartX(double startX) {
		this.startX = startX;
	}

	public double getSx() {
		return sx;
	}

	public void setSx(double sx) {
		this.sx = sx;
	}

	public double getScaleX() {
		return scaleX;
	}

	public void setScaleX(double scaleX) {
		this.scaleX = scaleX;
	}

	public double getStartY() {
		return startY;
	}

	public void setStartY(double startY) {
		this.startY = startY;
	}

	public double getSy() {
		return sy;
	}

	public void setSy(double sy) {
		this.sy = sy;
	}

	public double getScaleY() {
		return scaleY;
	}

	public void setScaleY(double scaleY) {
		this.scaleY = scaleY;
	}

	public void addLine(int dbId, int z, org.postgis.Point fp,
			org.postgis.Point lp) {
		GeoObjMaker l2d = new GeoLineMaker(dbId, z, fp, lp);
		geoBuffer.add(l2d);
	}

	public void addPolygon(int aDbId, int z, int[] xpoints, int[] ypoints,
			int npoints) {
		GeoObjMaker poly = new GeoPolyMaker(aDbId, z, xpoints, ypoints, npoints);
		geoBuffer.add(poly);
	}

	public void createGraphics2D(int w, int h) {
		GraphicsEnvironment ge = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		GraphicsDevice gd = ge.getDefaultScreenDevice();
		GraphicsConfiguration gc = gd.getDefaultConfiguration();
		if (bi == null || bi.getWidth() != w || bi.getHeight() != h) {
			bi = (BufferedImage) gc.createCompatibleImage(w, h);
		}
	}

	private void drawObjects(Graphics2D g2) { // Draw objects
		for (int z = 0; z < 2; z++) {
			for (GeoObjMaker lines : geoBuffer) {
				for (GeoObjShape shape : lines.getgShapes()) {
					if (shape.getZ() == z) {
						if (shape.isFillable()) {
							g2.setPaint(shape.getFillColor());
							g2.fill(shape.getShape());
						}
						g2.setColor(shape.getStrokeColor());
						g2.setStroke(shape.getStrokeStyle());
						g2.draw(shape.getShape());
					}
				}
			}
		}
	}

	public void init() {
		Dimension d = getSize();
		createGraphics2D(d.width, d.height);
		createBufferStrategy(2);
		buffer = getBufferStrategy();
	}

	int fps = 0;
	int frames = 0;
	long totalTime = 0;
	long curTime = System.currentTimeMillis();
	long lastTime = curTime;
	float alpha = 0.0f;

	public synchronized void paintAll() {
		Graphics2D graphics = null;
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
			graphics = (Graphics2D) buffer.getDrawGraphics();
			graphics.drawImage(bi, 0, 0, null);
			Rectangle2D rect = new Rectangle2D.Double(LINE_WIDTH, LINE_WIDTH,
					INFO_RECT_SIZE.getWidth() - LINE_WIDTH,
					INFO_RECT_SIZE.getHeight() - LINE_WIDTH);
			Rectangle2D bound = new Rectangle2D.Double(LINE_WIDTH / 2,
					LINE_WIDTH / 2, INFO_RECT_SIZE.getWidth(),
					INFO_RECT_SIZE.getHeight());
			graphics.setColor(Color.WHITE);
			graphics.fill(rect);
			graphics.setColor(Color.BLACK);
			graphics.setStroke(new BasicStroke(LINE_WIDTH));
			graphics.draw(bound);
			// drawObjects(graphics);
			graphics.drawString(String.format("FPS: %s", fps), 20, 20);
			graphics.drawString(
					String.format("Cache size: %s", CachedLoop.size()), 20, 40);
			graphics.drawString(String.format(
					"Image size: %s",
					settings.getImageSize().width + " | "
							+ settings.getImageSize().height), 20, 60);
			int offset = 20;
			for (Integer sc : settings.getScales()) {
				if (scaler.getPointer() == sc) {
					graphics.setColor(Color.RED);
					graphics.drawString(sc.toString(), offset, 80);
					graphics.setColor(Color.BLACK);
				} else {
					graphics.setColor(Color.BLACK);
					graphics.drawString(sc.toString(), offset, 80);
				}
				offset += 20;
			}

			graphics.drawString(
					String.format("Animation cache: %s", animator.getAnimations().size()), 20, 100);
			if (animator.hasAnimations())
			for (Animation i : animator.getAnimations()) {
				BufferedImage frame = i.getFrame();
				if (frame != null) {
					graphics.drawImage(frame, null, mouse.x-(frame.getWidth()/2), mouse.y-(frame.getHeight()/2));
				} else {
					animator.setPlayed(i);
				}
			}
			
			if (!buffer.contentsLost())
				buffer.show();
			// Let the OS have a little time...
			Thread.yield();
		} finally {
			// release resources
			if (graphics != null)
				graphics.dispose();
		}
	}

	public void clearLines() {
		geoBuffer.clear();
	}

	private GeoObjMaker find(Point2D p) {
		for (GeoObjMaker lines : geoBuffer) {
			if (lines.intersects(mouseRect))
				return lines;
		}
		return null;
	}

	public void setSelected(boolean state) {
		select = state;
	}

	public boolean isSelect() {
		return select;
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

	@Override
	public void mouseClicked(MouseEvent e) {
		if (isSelect()) {
			selected = find(e.getPoint());
			if (selected != null && e.getClickCount() == 1
					&& e.getButton() == MouseEvent.BUTTON1) {
				selected.setSelected();
				repaint();
			}
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

	@Override
	public void mouseDragged(MouseEvent e) {
		Graphics2D graphics = null;
		graphics = (Graphics2D) bi.getGraphics();
		if (isMove() && SwingUtilities.isLeftMouseButton(e)) {
			if (WorkerPool.hasWorkers())
				WorkerPool.interruptAll();
			double dx = 0, dy = 0;
			dx = e.getPoint().getX() - sx;
			dy = e.getPoint().getY() - sy;
			for (GeoObjMaker lines : geoBuffer) {
				lines.move(new Point2D.Double(dx, dy));
			}
			sx = e.getPoint().getX();
			sy = e.getPoint().getY();
			graphics.copyArea(0, 0, getWidth(), getHeight(), (int) dx, (int) dy);
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (isMove() && SwingUtilities.isLeftMouseButton(e)) {
			setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			startX = e.getPoint().getX();
			startY = e.getPoint().getY();
			sx = e.getPoint().getX();
			sy = e.getPoint().getY();
			if (WorkerPool.hasWorkers())
				WorkerPool.interruptAll();
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (isMove() && SwingUtilities.isLeftMouseButton(e)) {
			deltaX -= startX - e.getPoint().getX();
			deltaY -= startY - e.getPoint().getY();
			setCursor(Cursor.getDefaultCursor());
			loadMap();
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
		createGraphics2D(d.width, d.height);
		loadMap();
	}

	@Override
	public void componentShown(ComponentEvent e) {

	}

	public void setMove(boolean move) {
		this.move = move;
	}

	public boolean isMove() {
		return move;
	}

	public void setDeltaX(double deltaX) {
		this.deltaX = deltaX;
	}

	public double getDeltaX() {
		return deltaX;
	}

	public void setDeltaY(double deltaY) {
		this.deltaY = deltaY;
	}

	public double getDeltaY() {
		return deltaY;
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if (e.getWheelRotation() < 0) {
			zoomIn();
		} else {
			zoomOut();
		}
		loadMap();
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

		g2d.drawImage(bi, 0, 0, this);
		/* tell the caller that this page is part of the printed document */
		return PAGE_EXISTS;
	}

	public void loadMap() {
		if (WorkerPool.hasWorkers())
			WorkerPool.interruptAll();
		Thread pt = new Thread(new JobGenerator(this, bi, settings, scaler));
		pt.start();
	}

	private void zoomIn() {
		Animation animation = animationCache.getAnimation("zoomIn.js");
		animation.reset();
		animator.addAnimation(animation);
		scaler.zoomIn();

	}

	private void zoomOut() {
		scaler.zoomOut();

	}
}