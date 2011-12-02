package iipimage.jiipimage;

import iipimage.jiipimage.filter.FilterBlue;
import iipimage.jiipimage.filter.FilterBrightness;
import iipimage.jiipimage.filter.FilterContrast;
import iipimage.jiipimage.filter.FilterGreen;
import iipimage.jiipimage.filter.FilterGrey;
import iipimage.jiipimage.filter.FilterInvert;
import iipimage.jiipimage.filter.FilterRed;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedInputStream;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public final class ViewImage extends JPanel implements ActionListener {
	static JPanel ctrlpanel;

	public JLabel whatimage;

	private JIIPView mainView;

	private JFrame frame;

	private JIIPImage source;

	private ScrollImage scrl;

	//	private CanvasImage ci;
	private String fr;

	private Image jiipi;

	private DebugWin db;

	private JFrame toolbar;

	private boolean tb;

	private JButton lb;

	private JButton upb;

	private JButton downb;

	private JButton rb;

	private JButton cacheb;

	private JButton plusb;

	private JButton minusb;

	private JButton fitb;

	private JButton oob;

	private JButton advanced;

	private JButton nviewb;

	private JButton closeb;

	private JButton debug;

	private JButton back;
	private String imagepath = "images/";

	private JButton information;

	private JButton help;

	private JTextField setCache;

	public static JSlider Brightb;

	public static JSlider Contrb;

	private JComboBox JPEGQBar;

	private JComboBox Filters;

	public ViewImage(JIIPView IIPmainView, JFrame IIPFrame, JIIPImage IIPSource) {
		mainView = IIPmainView;
		source = IIPSource;
		frame = IIPFrame;
		int nXFrames = source.mNumXFrames;
		final int nYFrames = source.mNumYFrames;
		boolean newview = (frame == null) ? false : true;
		ImageIcon lefti = createAppletImageIcon(imagepath + "stock_first.png",
				"");
		ImageIcon righti = createAppletImageIcon(imagepath + "stock_last.png",
				"");
		ImageIcon upi = createAppletImageIcon(imagepath + "stock_top.png", "");
		ImageIcon downi = createAppletImageIcon(imagepath + "stock_bottom.png",
				"");
		ImageIcon closei = createAppletImageIcon(imagepath + "stock_exit.png",
				"");
		ImageIcon fiti = createAppletImageIcon(
				imagepath + "stock_zoom_fit.png", "");
		ImageIcon zini = createAppletImageIcon(imagepath + "stock_zoom_in.png",
				"");
		ImageIcon zouti = createAppletImageIcon(imagepath
				+ "stock_zoom_out.png", "");
		ImageIcon zooi = createAppletImageIcon(imagepath + "stock_zoom_1.png",
				"");
		ImageIcon newi = createAppletImageIcon(imagepath
				+ "stock_insert-image.png", "");
		ImageIcon advi = createAppletImageIcon(imagepath + "advanced.png", "");
		final ImageIcon aboutImi = createAppletImageIcon(imagepath
				+ "stock_about.png", "");
		final ImageIcon abouti = createAppletImageIcon(imagepath + "stock_about.png",
				"");
		ImageIcon helpi = createAppletImageIcon(imagepath + "stock_help.png",
				"");
		jiipi = createAppletIcon(imagepath + "jiip.png", "");
		//putting on layout
		setLayout(new BorderLayout());
		/*
		 * create the control panel
		 */
		ctrlpanel = new JPanel();
		ctrlpanel.setLayout(new FlowLayout());
		ctrlpanel.setOpaque(false);
		//
		whatimage = new JLabel("H: " + source.getXFrame() + " V: "
				+ source.getYFrame());
		whatimage.setBackground(ctrlpanel.getBackground());
		whatimage.setForeground(new Color(0xffffdd));
		whatimage.setToolTipText("Current Position");
		ctrlpanel.add(whatimage);
		//
		if (nXFrames > 1) {
			lb = new JButton(lefti);
			lb.setMnemonic(KeyEvent.VK_LEFT);
			lb.setBackground(ctrlpanel.getBackground());
			lb.setBorder(null);
			lb.setToolTipText("Turn left");
			lb.addActionListener(this);
			ctrlpanel.add(lb);
		}
		if (nYFrames > 1) {
			JPanel vert = new JPanel();
			vert.setLayout(new GridLayout(2, 0));
			//
			upb = new JButton(upi);
			upb.setMnemonic(KeyEvent.VK_UP);
			upb.setBackground(ctrlpanel.getBackground());
			upb.setBorder(null);
			upb.setToolTipText("Go Up");
			upb.addActionListener(this);
			vert.add(upb);
			downb = new JButton(downi);
			downb.setMnemonic(KeyEvent.VK_DOWN);
			downb.setBackground(ctrlpanel.getBackground());
			downb.setBorder(null);
			downb.setToolTipText("Go Down");
			downb.addActionListener(this);
			vert.add(downb);
			ctrlpanel.add(vert);
		}
		//
		if (nXFrames > 1) {
			rb = new JButton(righti);
			rb.setMnemonic(KeyEvent.VK_RIGHT);
			rb.setBackground(ctrlpanel.getBackground());
			rb.setBorder(null);
			rb.setToolTipText("Turn right");
			rb.addActionListener(this);
			ctrlpanel.add(rb);
		}
		//
		plusb = new JButton(zini);
		plusb.setToolTipText("Zoom In");
		plusb.setMnemonic(KeyEvent.VK_I);
		plusb.setBackground(ctrlpanel.getBackground());
		plusb.setBorder(null);
		plusb.addActionListener(this);
		ctrlpanel.add(plusb);
		//
		minusb = new JButton(zouti);
		minusb.setToolTipText("Zoom Out");
		minusb.setMnemonic(KeyEvent.VK_O);
		minusb.setBackground(ctrlpanel.getBackground());
		minusb.setBorder(null);
		minusb.addActionListener(this);
		ctrlpanel.add(minusb);
		//
		fitb = new JButton(fiti);
		fitb.setToolTipText("Fit Image");
		fitb.setMnemonic(KeyEvent.VK_F);
		fitb.setBackground(ctrlpanel.getBackground());
		fitb.setBorder(null);
		fitb.addActionListener(this);
		ctrlpanel.add(fitb);
		//
		oob = new JButton(zooi);
		oob.setToolTipText("Set original dimension");
		oob.setMnemonic(KeyEvent.VK_S);
		oob.setBackground(ctrlpanel.getBackground());
		oob.setBorder(null);
		oob.addActionListener(this);
		ctrlpanel.add(oob);
		/*
		 * advanced options
		 */
		advanced = new JButton(advi);
		advanced.setToolTipText("Advanced Options");
		advanced.setBorder(null);
		advanced.addActionListener(this);
		ctrlpanel.add(advanced);
		//
		nviewb = new JButton(newi);
		nviewb.setToolTipText("New window image");
		nviewb.setMnemonic(KeyEvent.VK_N);
		nviewb.setBackground(ctrlpanel.getBackground());
		nviewb.setBorder(null);
		nviewb.addActionListener(this);
		ctrlpanel.add(nviewb);
		/*
		 * open image information
		 */
		information = new JButton(abouti);
		information.setToolTipText("Image information");
		information.setMnemonic(KeyEvent.VK_A);
		information.setBorder(null);
		information.addActionListener(this);
		ctrlpanel.add(information);
		/*
		 * open help
		 */
		help = new JButton(helpi);
		help.setToolTipText("JIIPimage Help");
		help.setBorder(null);
		help.addActionListener(this);
		ctrlpanel.add(help);
		//
		closeb = new JButton(closei);
		closeb.setToolTipText("Close view");
		closeb.setMnemonic(KeyEvent.VK_C);
		closeb.setBackground(ctrlpanel.getBackground());
		closeb.setBorder(null);
		closeb.addActionListener(this);
		if (newview || JIIPView.mName == "app")
			ctrlpanel.add(closeb);
		//
		add(ctrlpanel, BorderLayout.SOUTH);
		scrl = new ScrollImage(source, this);
		add(scrl, BorderLayout.CENTER);
	}

	/**
	 * Override the first paint call so that we can set the size of the
	 * displayed image to fit in the window. Why does this only work in
	 * applications and not in applets?
	 */
	public int[] getPosition() {
		return scrl.getRealPosition();
	}

	public void changePosition(int sub, int x, int y) {
		scrl.setViewParameters(sub, x * sub, y * sub);
	}

	/**
	 * Change the scale, keeping the un-subsampled pixel in the centre of the
	 * viewport in the centre of the viewport.
	 */
	public void change_scale(int nsub) {
		int sub = scrl.getSubsample();
		Rectangle vp = scrl.getViewPort();
		DebugWin.printc("change_scale " + nsub);
		// What pixel is in the centre of the viewport? In un-subsampled
		// coordinates.
		int x = (vp.x + vp.width / 2) * sub;
		int y = (vp.y + vp.height / 2) * sub;
		// Map to new sub-sample coordinate system.
		x /= nsub;
		y /= nsub;
		// Find the top-left of the new viewport.
		x -= vp.width / 2;
		y -= vp.height / 2;
		scrl.setViewParameters(nsub, x, y);
	}

	/**
	 * Zoom in on the current position.
	 */
	public void zoomIn() {
		int sub = scrl.getSubsample();
		if (sub > 1)
			change_scale(sub / 2);
		DebugWin.printc("Scaling at " + sub / 2);
	}

	/**
	 * Zoom out from the current position.
	 */
	public void zoomOut() {
		Dimension csize = scrl.getCsize();
		int sub = scrl.getSubsample();
		if (source.getRes(sub) > 0) {
			change_scale(sub * 2);
			DebugWin.printc("Scaling at " + sub / 2);
		}
	}

	/*
	 * Create a copy of ourselves in a floating window.
	 */
	public void popNewView() {
		/*
		 * Make a frame, and an instance of us inside it.
		 */
		JFrame base = new JFrame("JIIPview - " + JIIPView.Version
				+ " - New view");
		base.setIconImage(jiipi);
		base.setResizable(true);
		base.setSize(450, 600);
		/*
		 * create the new view with a new image, and keep the same cache.
		 */
		try {
			JIIPImage newSrc = (JIIPImage) source.clone();
			ViewImage iv = new ViewImage(mainView, base, newSrc);
			base.getContentPane().add(iv);
			iv.scrl.setViewParameters(scrl.getSubsample(), 0, 0);
		} catch (CloneNotSupportedException e) {
			DebugWin.printcerr("Ooops... I can't clone!");
		}
		//Pop up the new frame.
		base.show();
	}

	public void fit() {
		Point pos = scrl.getPosition();
		scrl.setViewParameters(0, pos.x, pos.y);
	}

	public void popToolBar() {
		ImageIcon debugi = createAppletImageIcon(imagepath + "bug-buddy.png",
		"");
		ImageIcon backi = createAppletImageIcon(imagepath + "color-browser.png",
		"");
		ImageIcon refreshi = createAppletImageIcon(imagepath + "stock_refresh.png", "");
		if (!tb) {
			tb = true;
			toolbar = new JFrame("JIIPview - " + JIIPView.Version
					+ " - Advanced Options");
			toolbar.getContentPane().setLayout(new GridBagLayout());
			toolbar.setResizable(false);
			toolbar.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			//
			cacheb = new JButton(refreshi);
			cacheb.setToolTipText("Refresh");
			cacheb.setMnemonic(KeyEvent.VK_R);
			cacheb.setBackground(ctrlpanel.getBackground());
			cacheb.setBorder(null);
			cacheb.addActionListener(this);
			toolbar.getContentPane().add(cacheb);
			
			JPEGQBar = new JComboBox();
			JPEGQBar.addItem("1");
			JPEGQBar.addItem("25");
			JPEGQBar.addItem("50");
			JPEGQBar.addItem("75");
			JPEGQBar.addItem("95");
			JPEGQBar.setSelectedIndex(3);
			JPEGQBar.setToolTipText("JPEG quality compression");
			JPEGQBar.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					JComboBox cb = (JComboBox) e.getSource();
					source.Q = Integer.parseInt((String) cb.getSelectedItem());
					source.emptyCache();
					scrl.redraw();
				}
			});
			toolbar.getContentPane().add(JPEGQBar);
			/*
			 * Color Filters
			 */
			Filters = new JComboBox();
			Filters.addItem("No Filters");
			Filters.addItem("Blue");
			Filters.addItem("Green");
			Filters.addItem("Gray");
			Filters.addItem("Invert");
			Filters.addItem("Red");
			Filters.setSelectedIndex(0);
			Filters.setToolTipText("Filters");
			Filters.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					JComboBox cb = (JComboBox) e.getSource();
					int filter = cb.getSelectedIndex();
					switch (filter) {
					case 0:
						source.setImageFilter(null);
						break;
					case 1:
						source.setImageFilter(new FilterBlue());
						break;
					case 2:
						source.setImageFilter(new FilterGreen());
						break;
					case 3:
						source.setImageFilter(new FilterGrey());
						break;
					case 4:
						source.setImageFilter(new FilterInvert());
						break;
					case 5:
						source.setImageFilter(new FilterRed());
						break;
					default:
						source.setImageFilter(null);
						break;
					}
					Brightb.setValue(0);
					Contrb.setValue(0);
					scrl.redraw();
				}
			});
			toolbar.getContentPane().add(Filters);
			setCache = new JTextField(mainView.CACHESIZE + "");
			setCache.setToolTipText("Change tile cache size");
			setCache.addActionListener(this);
			toolbar.getContentPane().add(setCache);
			/*
			 * Brightness control
			 */
			Brightb = new JSlider(JSlider.VERTICAL, -127, 128, 0);
			Brightb.setToolTipText("Brightness " + Brightb.getValue());
			Brightb.setForeground(new Color(0xffffdd));
			Brightb.setBackground(ctrlpanel.getBackground());
			Brightb.setPreferredSize(new Dimension(60, 30));
			Brightb.setMajorTickSpacing(127);
			Brightb.setMinorTickSpacing(30);
			Brightb.setPaintTicks(true);
			Brightb.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					Brightb.setToolTipText("Brightness " + Brightb.getValue());
					source.setImageFilter(new FilterBrightness());
					scrl.redraw();
					DebugWin.printc("Set Brightness to: " + Brightb.getValue());
				}
			});
			toolbar.getContentPane().add(Brightb);
			/*
			 * Contrast control
			 */
			Contrb = new JSlider(JSlider.VERTICAL, 0, 100, 0);
			Contrb.setToolTipText("Contrast " + Contrb.getValue());
			Contrb.setForeground(new Color(0xffffdd));
			Contrb.setBackground(ctrlpanel.getBackground());
			Contrb.setPreferredSize(new Dimension(60, 30));
			Contrb.setMajorTickSpacing(127);
			Contrb.setMinorTickSpacing(30);
			Contrb.setPaintTicks(true);
			Contrb.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					Contrb.setToolTipText("Contrast " + Contrb.getValue());
					source.setImageFilter(new FilterContrast());
					scrl.redraw();
					DebugWin.printc("Set Contrast to: " + Contrb.getValue());
				}
			});
			toolbar.getContentPane().add(Contrb);

			/*
			 * Common part for all
			 */
			/*
			 * Open Debug Window
			 */
			debug = new JButton(debugi);
			debug.setBorder(null);
			debug.setToolTipText("Open debug window");
			debug.setMnemonic(KeyEvent.VK_D);
			debug.addActionListener(this);
			toolbar.getContentPane().add(debug);
			/*
			 * Background
			 */
			back = new JButton(backi);
			back.setBorder(null);
			back.setToolTipText("Change BackGround Color");
			back.setMnemonic(KeyEvent.VK_B);
			back.addActionListener(this);
			//
			toolbar.getContentPane().add(back);
			toolbar.pack();
			toolbar.show();
		} else {
			toolbar.setVisible(false);
			tb = false;
		}
	}

	//	Returns an ImageIcon, or null if the path was invalid.
	//	When running an applet using Java Plug-in,
	//	getResourceAsStream is more efficient than getResource.
	protected static ImageIcon createAppletImageIcon(String path,
			String description) {
		int MAX_IMAGE_SIZE = 75000;
		int count = 0;
		BufferedInputStream imgStream = new BufferedInputStream(ViewImage.class
				.getResourceAsStream(path));
		if (imgStream != null) {
			byte buf[] = new byte[MAX_IMAGE_SIZE];
			try {
				count = imgStream.read(buf);
			} catch (IOException ieo) {
				DebugWin.printcerr("Couldn't read stream from file: " + path);
			}
			try {
				imgStream.close();
			} catch (IOException ieo) {
				DebugWin.printcerr("Can't close file " + path);
			}
			if (count <= 0) {
				DebugWin.printcerr("Empty file: " + path);
				return null;
			}
			return new ImageIcon(Toolkit.getDefaultToolkit().createImage(buf),
					description);
		} else {
			DebugWin.printcerr("Couldn't find file: " + path);
			return null;
		}
	}

	protected static Image createAppletIcon(String path, String description) {
		int MAX_IMAGE_SIZE = 75000;
		int count = 0;
		BufferedInputStream imgStream = new BufferedInputStream(ViewImage.class
				.getResourceAsStream(path));
		if (imgStream != null) {
			byte buf[] = new byte[MAX_IMAGE_SIZE];
			try {
				count = imgStream.read(buf);
			} catch (IOException ieo) {
				DebugWin.printcerr("Couldn't read stream from file: " + path);
			}
			try {
				imgStream.close();
			} catch (IOException ieo) {
				DebugWin.printcerr("Can't close file " + path);
			}
			if (count <= 0) {
				DebugWin.printcerr("Empty file: " + path);
				return null;
			}
			return Toolkit.getDefaultToolkit().createImage(buf);
		} else {
			DebugWin.printcerr("Couldn't find file: " + path);
			return null;
		}
	}

	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getSource() == lb) {
			source.setXFrame(source.getXFrame() - 1);
			scrl.redraw();
			DebugWin.printc("Turning");
		} else if (arg0.getSource() == upb) {
			source.setYFrame(source.findYFrame(source.getYFrame()) + 1);
			scrl.redraw();
			DebugWin.printc("Turning");
		} else if (arg0.getSource() == downb) {
			source.setYFrame(source.findYFrame(source.getYFrame()) - 1);
			scrl.redraw();
			DebugWin.printc("Turning");
		} else if (arg0.getSource() == rb) {
			source.setXFrame(source.getXFrame() + 1);
			scrl.redraw();
			DebugWin.printc("Turning");
		} else if (arg0.getSource() == cacheb) {
			source.emptyCache();
			Brightb.setValue(0);
			Contrb.setValue(0);
			JPEGQBar.setSelectedIndex(3);
			Filters.setSelectedIndex(0);
			scrl.redraw();
			DebugWin.printc("Restoring Default Value");
		} else if (arg0.getSource() == plusb) {
			zoomIn();
		} else if (arg0.getSource() == minusb) {
			zoomOut();
		} else if (arg0.getSource() == fitb) {
			fit();
		} else if (arg0.getSource() == oob) {
			change_scale(1);
		} else if (arg0.getSource() == advanced) {
			popToolBar();
		} else if (arg0.getSource() == nviewb) {
			popNewView();
		} else if (arg0.getSource() == closeb) {
			int n = JOptionPane
					.showConfirmDialog(frame, "are You sure?", "JIIP viewer - "
							+ JIIPView.Version + " - Exit Confirmation",
							JOptionPane.YES_NO_OPTION);
			if (n == 0)
				if (JIIPView.mName == "app") {
					try {
						fr = frame.getName();
					} catch (NullPointerException e) {
						String fr = null;
					}
					if (fr == null) {
						System.exit(0);
					} else {
						frame.dispose();
					}
				} else {
					frame.dispose();
				}
		} else if (arg0.getSource() == debug) {
			if (DebugWin.debugActive == "yes") {
				DebugWin.debugActive = "no";
				db.dispose();

			} else {
				DebugWin.debugActive = "yes";
				db = new DebugWin();
			}
		} else if (arg0.getSource() == back) {
			Color newColor = JColorChooser.showDialog(new JFrame(),
					"Choose Background Color", Color.BLACK);
			scrl.setBg(newColor);
					} else if (arg0.getSource() == information){
						JOptionPane
						.showMessageDialog(
								frame,
								"<html><h2><center><font color=#FF0000>JIIPView "
										+ JIIPView.Version
										+ "</center></h2><p>Image: " + source.getXFrame() + " " + source.getYFrame() + "</p><p>Dimension: " + source.getSize().width + "x" + source.getSize().height + "</p><p>Resolution: " + source.getResNumber() + "</p><p>Tile size: " + source.getTileSize().width + "x" + source.getTileSize().height + "</p><p>Color Space: " + source.colorspace + "</p>",
								"JIIPview - " + JIIPView.Version
										+ " - Image Information",
								JOptionPane.INFORMATION_MESSAGE);
				} else if (arg0.getSource() == help){
					JOptionPane
					.showMessageDialog(
							frame,
							"<html><h2><center><font color=#FF0000>JIIPViewer "
									+ JIIPView.Version
									+ "</center></h2><h3><center>IIPimage http://iipimage.sf.net/</center></h3><font color=#000000 size=2><p><center> The IIP viewer applet and application</center></p><p></p> <p><center>Usage:</center></p> <ul> Mouse Left - Click = Open new image View </ul> <ul> Mouse Left - Drag = Move the image </ul> <ul> Mouse Right - Drag = Rotate the image </ul><ul> Mouse wheel = Zoom In/Out </ul> <hr> <p>&#169;2003 Denis Pitzalis - denics@free.fr</p><p>&#169;2001 Ruven Pillay - ruven@free.fr</p><p>&#169;1999 John Cupitt, Steve Perry, Kirk Martinez</p>",
							"JIIP view - " + JIIPView.Version
									+ " - About Information",
							JOptionPane.INFORMATION_MESSAGE);
		} else if (arg0.getSource() == setCache){
			if (Integer.parseInt(setCache.getText()) > 500){
				setCache.setText("500");
			}
			mainView.setCache(Integer.parseInt(setCache.getText()));
		}
	}
}