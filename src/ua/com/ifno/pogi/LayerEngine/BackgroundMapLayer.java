package ua.com.ifno.pogi.LayerEngine;

import ua.com.ifno.pogi.ImageSettings;
import ua.com.ifno.pogi.JobGenerator;
import ua.com.ifno.pogi.Scaler;
import ua.com.ifno.pogi.WorkerPool;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class BackgroundMapLayer extends AbstractLayer implements Layer {
	private final ImageSettings settings;
	private final Scaler scaler;
	private BufferedImage bi = null;
	private final GraphicsEnvironment ge = GraphicsEnvironment
			.getLocalGraphicsEnvironment();
	private final GraphicsDevice gs = ge.getDefaultScreenDevice();
	private final GraphicsConfiguration gc = gs.getDefaultConfiguration();
	private final Rectangle oldPosition = new Rectangle();
	private final Rectangle newPosition = new Rectangle();
	private ScheduledRepaint repaint = null;

	public BackgroundMapLayer(ImageSettings settings, Dimension size,
                              Scaler scaler) {
		super("Background", true);
		this.settings = settings;
		this.scaler = scaler;
		this.bi = gc.createCompatibleImage(size.width, size.height);
		repaint = new ScheduledRepaint();
		Timer timer = new Timer("Background repaint", false);
		timer.schedule(repaint, 0, 200);
	}
	
	@Override
	public void setViewPort(Rectangle viewPort) {
		if (WorkerPool.hasWorkers())
			WorkerPool.interruptAll();
		repaint.setViewPort(viewPort);
		newPosition.setBounds(viewPort);
		Graphics2D g2d = bi.createGraphics();
		int dx = oldPosition.x-newPosition.x;
		int dy = oldPosition.y-newPosition.y;
		g2d.copyArea(0, 0, viewPort.width, viewPort.height, dx, dy);
		g2d.setColor(Color.LIGHT_GRAY);
		if (dx < 0)
			g2d.fillRect(viewPort.width+dx, 0, Math.abs(dx), viewPort.height);
		else
			g2d.fillRect(0, 0, Math.abs(dx), viewPort.height);
		if (dy < 0)
			g2d.fillRect(0, viewPort.height+dy, viewPort.width, Math.abs(dy));
		else
			g2d.fillRect(0, 0, viewPort.width, Math.abs(dx));
		g2d.dispose();
		oldPosition.setBounds(newPosition);
	}

	@Override
	public Rectangle getViewPort() {
		return newPosition;
	}

	@Override
	public Object getDrawable() {
		return bi;
	}

	@Override
	public void setSize(int width, int height) {
		bi = gc.createCompatibleImage(width, height);
	}
	
	private class ScheduledRepaint extends TimerTask {
		private Rectangle viewPort;
		private final Rectangle oldViewPort;
		private Thread tt;
		
		public ScheduledRepaint() {
			this.viewPort = new Rectangle(800, 600);
			this.oldViewPort = new Rectangle(800, 600);
		}

		@Override
		public void run() {
			if (!viewPort.equals(oldViewPort)) {
			tt = new JobGenerator(this.viewPort, bi, settings,
					scaler);
			tt.start();
			}
			oldViewPort.setBounds(viewPort);
		}
		
		public void setViewPort(Rectangle viewPort) {
			this.viewPort = viewPort;
		}
		
	}

	@Override
	public ArrayList<Shape> getData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setData(ArrayList<Shape> data) {
		// TODO Auto-generated method stub
	}
}
