package ua.com.ifno.pogi.LayerEngine;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Timer;
import java.util.TimerTask;

import net.sf.ehcache.Cache;

import ua.com.ifno.pogi.ImageSettings;
import ua.com.ifno.pogi.JobGenerator;
import ua.com.ifno.pogi.Scaler;
import ua.com.ifno.pogi.WorkerPool;

public class BackgroundMapLayer extends AbstractLayer implements Layer {
	private Cache cache;
	private ImageSettings settings;
	private Scaler scaler;
	private BufferedImage bi = null;
	private GraphicsEnvironment ge = GraphicsEnvironment
			.getLocalGraphicsEnvironment();
	private GraphicsDevice gs = ge.getDefaultScreenDevice();
	private GraphicsConfiguration gc = gs.getDefaultConfiguration();
	private Rectangle oldPosition = new Rectangle();
	private Rectangle newPosition = new Rectangle();
	private ScheduledRepaint repaint = null;

	public BackgroundMapLayer(String name, ImageSettings settings, Dimension size,
			Scaler scaler, Cache cache, boolean visible) {
		super(name, visible);
		this.settings = settings;
		this.scaler = scaler;
		this.bi = gc.createCompatibleImage(size.width, size.height);
		this.cache = cache;
		repaint = new ScheduledRepaint();
		Timer timer = new Timer("Background repaint", false);
		timer.schedule(repaint, 0, 200);
	}
	
	boolean runned = false;	
	@Override
	public void setPosition(Rectangle viewPort) {
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
	public Rectangle getPosition() {
		return newPosition;
	}

	@Override
	public int getCacheSize() {
		return cache.getSize();
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
		private Thread tt;
		
		public ScheduledRepaint() {
			this.viewPort = new Rectangle(800, 600);
		}

		@Override
		public void run() {
			tt = new JobGenerator(this.viewPort, bi, settings,
					scaler, cache);
			tt.start();
		}
		
		public void setViewPort(Rectangle viewPort) {
			this.viewPort = viewPort;
		}
		
	}
}
