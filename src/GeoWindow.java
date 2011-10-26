import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.util.*;
import javax.swing.JPanel;
import javax.swing.event.MouseInputListener;
import net.it_tim.jpogiengine.geoObjects.GeoLineMaker;
import net.it_tim.jpogiengine.geoObjects.GeoObjMaker;
import net.it_tim.jpogiengine.geoObjects.GeoPolyMaker;

import org.postgis.Point;

public class GeoWindow extends JPanel implements MouseMotionListener,
		MouseInputListener, ComponentListener, MouseWheelListener, Printable {

	private static final long serialVersionUID = -5210242861777162258L;
	private Rectangle2D mouseRect = new Rectangle2D.Double(0, 0, 5, 5);
	private ArrayList<GeoObjMaker> geoBuffer = new ArrayList<GeoObjMaker>();
	private boolean select = false;
	private boolean move = false;
	private GeoObjMaker selected;
	private double deltaX = 0, startX = 0, sx = 0, scaleX = 1.0;
	private double deltaY = 0, startY = 0, sy = 0, scaleY = 1.0;
	private BufferedImage bi = null;

	public GeoWindow() {
		super();
		setSize(new Dimension(500, 500));
		addComponentListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
		setVisible(true);

		Thread loop = new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if (WorkerPool.hasWorkers()) {
						repaint();
					}
				}
			}
		});
		loop.start();
	}

	public void addLine(int dbId, int z, Point fp, Point lp) {
		GeoObjMaker l2d = new GeoLineMaker(dbId, z, fp, lp);
		geoBuffer.add(l2d);
	}

	public void addPolygon(int aDbId, int z, int[] xpoints, int[] ypoints,
			int npoints) {
		GeoObjMaker poly = new GeoPolyMaker(aDbId, z, xpoints, ypoints, npoints);
		geoBuffer.add(poly);
	}

	public Graphics2D createGraphics2D(int w, int h) {
		Graphics2D g2 = null;
		if (bi == null || bi.getWidth() != w || bi.getHeight() != h) {
			bi = (BufferedImage) createImage(w, h);
		}
		bi.setAccelerationPriority(1.0f);
		g2 = bi.createGraphics();
		g2.setBackground(getBackground());
		g2.clearRect(0, 0, w, h);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);
		return g2;
	}

	/*
	private void drawObjects(Graphics2D g2) {
		// Draw objects
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
	*/
	
	@Override
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.drawImage(bi, null, 0, 0);
		g2.draw(new Rectangle2D.Double(4, 4, 201, 51));
		g2.setColor(Color.WHITE);
		g2.fill(new Rectangle2D.Double(5, 5, 200, 50));
		g2.setColor(Color.BLACK);
		g2.drawString("Cache size: " + Integer.toString(CachedLoop.size()), 6, 33);

		//new Integer(SimplePogiTest.imgBuffer.size()).toString()
	}

	public void clearLines() {
		geoBuffer.clear();
	}

	public GeoObjMaker find(Point2D p) {
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
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (isMove()) {
			double dx = 0, dy = 0;
			dx = e.getPoint().getX() - sx;
			dy = e.getPoint().getY() - sy;
			for (GeoObjMaker lines : geoBuffer) {
				lines.move(new Point2D.Double(dx, dy));
				repaint();
			}
			sx = e.getPoint().getX();
			sy = e.getPoint().getY();
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (isMove()) {
			setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			startX = e.getPoint().getX();
			startY = e.getPoint().getY();
			sx = e.getPoint().getX();
			sy = e.getPoint().getY();
			PaintThread.stop();
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (isMove()) {
			deltaX -= startX - e.getPoint().getX();
			deltaY -= startY - e.getPoint().getY();
			SimplePogiTest.test(deltaX, deltaY, getSize().getWidth(), getSize()
					.getHeight(), scaleX, scaleY);
			setCursor(Cursor.getDefaultCursor());
			loadMap();
		}
	}

	@Override
	public void componentHidden(ComponentEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void componentMoved(ComponentEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void componentResized(ComponentEvent e) {

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
		if ((scaleX <= 1.0 || scaleY <= 1.0) && e.getWheelRotation() < 0) {
			scaleX = scaleY = 1.0;
		} else {
			scaleX = scaleY += e.getWheelRotation();
			SimplePogiTest.test(deltaX, deltaY, getSize().getWidth(), getSize()
					.getHeight(), scaleX, scaleY);
		}
	}

	@Override
	public int print(Graphics graphics, PageFormat pageFormat, int pageIndex)
			throws PrinterException {
        if (pageIndex > 0) { /* We have only one page, and 'page' is zero-based */
            return NO_SUCH_PAGE;
        }

        /* User (0,0) is typically outside the imageable area, so we must
         * translate by the X and Y values in the PageFormat to avoid clipping
         */
        Graphics2D g2d = (Graphics2D) graphics;
        g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

        g2d.drawImage(bi, 0, 0, this);
        /* tell the caller that this page is part of the printed document */
        return PAGE_EXISTS;
	}

	public void loadMap() {
		Dimension d = getSize();
		Graphics2D g2 = createGraphics2D(d.width, d.height);
		Thread pt = new Thread(new PaintThread());
		PaintThread.setGraphics(g2);
		PaintThread.setDelta(deltaX, deltaY);
		PaintThread.setScale(scaleX, scaleY);
		pt.start();
	}
}