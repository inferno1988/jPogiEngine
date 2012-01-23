package ua.com.ifno.pogi.LayerEngine;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import ua.com.ifno.pogi.CachedLoop;
import ua.com.ifno.pogi.ImageSettings;
import ua.com.ifno.pogi.JobGenerator;
import ua.com.ifno.pogi.Scaler;
import ua.com.ifno.pogi.WorkerPool;

public class BackgroundMapLayer extends AbstractLayer implements Layer {
	private CachedLoop<String, Image> cachedLoop = new CachedLoop<String, Image>();
	private ImageSettings settings;
	private Scaler scaler;
	private BufferedImage bi = null;
	private GraphicsEnvironment ge = GraphicsEnvironment
			.getLocalGraphicsEnvironment();
	private GraphicsDevice gs = ge.getDefaultScreenDevice();
	private GraphicsConfiguration gc = gs.getDefaultConfiguration();
	private Rectangle oldPosition = new Rectangle();
	private Rectangle newPosition = new Rectangle();
	private Thread tt;

	public BackgroundMapLayer(String name, ImageSettings settings, Dimension size,
			Scaler scaler, boolean visible) {
		super(name, visible);
		this.settings = settings;
		this.scaler = scaler;
		this.bi = gc.createCompatibleImage(size.width, size.height);
	}
	
	boolean runned = false;	
	@Override
	public void setPosition(Rectangle viewPort) {
		if (WorkerPool.hasWorkers())
			WorkerPool.interruptAll();
		newPosition.setBounds(viewPort);
		tt = new JobGenerator(viewPort, bi, this.settings,
				this.scaler, cachedLoop);
		tt.start();
		Graphics2D g2d = bi.createGraphics();
		g2d.copyArea(0, 0, viewPort.width, viewPort.height, newPosition.width-oldPosition.width, newPosition.height-oldPosition.height);
		g2d.dispose();
		oldPosition.setBounds(newPosition);
	}

	@Override
	public Rectangle getPosition() {
		return newPosition;
	}

	@Override
	public int getCacheSize() {
		return cachedLoop.size();
	}

	@Override
	public Object getDrawable() {
		return bi;
	}

	@Override
	public void setSize(int width, int height) {
		bi = gc.createCompatibleImage(width, height);
	}

}
