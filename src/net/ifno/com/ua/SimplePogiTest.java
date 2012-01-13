package net.ifno.com.ua;

import java.awt.BorderLayout;
import java.awt.DisplayMode;
import java.awt.EventQueue;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import net.ifno.com.ua.geoObjects.PostgisParser;

import org.apache.commons.configuration.ConfigurationException;
import org.postgis.Geometry;
import org.postgis.PGbox3d;
import org.postgis.Point;
import org.postgresql.PGConnection;
import java.awt.event.WindowFocusListener;

public class SimplePogiTest {

	private JFrame frmJpogiengine;
	private static GeoWindow gw;
	private JToggleButton moveToggle = new JToggleButton("Move");
	private JToggleButton selectToggle = new JToggleButton("Select");
	private final JPanel panel = new JPanel();
	private final JButton btnNewButton_1 = new JButton("Print");
	private final JButton btnNewButton_2 = new JButton("Map");
	private final JButton btnNewButton_3 = new JButton("Clear");
	private boolean focused = false;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SimplePogiTest window = new SimplePogiTest();
					window.frmJpogiengine.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public SimplePogiTest() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		try {
			frmJpogiengine = new JFrame();
			frmJpogiengine.addWindowFocusListener(new WindowFocusListener() {
				public void windowGainedFocus(WindowEvent e) {
					focused = true;
				}

				public void windowLostFocus(WindowEvent e) {
					focused = false;
				}
			});
			frmJpogiengine.setTitle("jPogiEngine");
			ImageSettings is = ImageSettings.parseXML(new URL(
					"http://192.168.33.110/config.xml"));
			gw = new GeoWindow(is);
			frmJpogiengine.addWindowListener(new WindowAdapter() {
				@Override
				public void windowOpened(WindowEvent e) {
					gw.init();
					Thread loop = new Thread(new Runnable() {
						@Override
						public void run() {
							while (true) {
								try {
									if (WorkerPool.hasWorkers() && focused) {
										gw.paintAll();
									} else if (focused) {
										Thread.sleep(50);
										gw.paintAll();
									} else {
										Thread.sleep(500);
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}
					});
					loop.start();
				}
			});

			GraphicsEnvironment ge = GraphicsEnvironment
					.getLocalGraphicsEnvironment();
			GraphicsDevice gs = ge.getDefaultScreenDevice();
			DisplayMode dm = gs.getDisplayMode();

			frmJpogiengine.setBounds(100, 100, (int) (dm.getWidth() * 0.75),
					(int) (dm.getHeight() * 0.75));
			frmJpogiengine.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frmJpogiengine.setLocationRelativeTo(null);
			frmJpogiengine.getContentPane().setLayout(new BorderLayout());

			JToolBar toolBar = new JToolBar();
			frmJpogiengine.getContentPane().add(toolBar, BorderLayout.NORTH);
			toolBar.setRollover(true);
			toolBar.setFloatable(false);

			JButton btnNewButton = new JButton("Load");
			toolBar.add(btnNewButton);

			selectToggle.addActionListener(new ActionListener() {
				Object src = new Object();

				public void actionPerformed(ActionEvent e) {
					src = e.getSource();
					if (src instanceof JToggleButton) {
						if (((JToggleButton) src).isSelected()) {
							moveToggle.setSelected(false);
							gw.setSelected(true);
							gw.setMove(false);
						} else {
							gw.setSelected(false);
						}
					}
				}
			});

			toolBar.add(selectToggle);
			moveToggle.addActionListener(new ActionListener() {
				Object src = new Object();

				public void actionPerformed(ActionEvent e) {
					src = e.getSource();
					if (src instanceof JToggleButton) {
						if (((JToggleButton) src).isSelected()) {
							selectToggle.setSelected(false);
							gw.setMove(true);
							gw.setSelected(false);
						} else {
							gw.setMove(false);
						}
					}
				}
			});

			toolBar.add(moveToggle);
			btnNewButton_1.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					PrinterJob job = PrinterJob.getPrinterJob();
					PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
					PageFormat pf = job.pageDialog(aset);
					job.setPrintable(gw, pf);
					boolean ok = job.printDialog(aset);
					if (ok) {
						try {
							job.print(aset);
						} catch (PrinterException ex) {
							/* The job did not successfully complete */
						}
					}
				}
			});

			toolBar.add(btnNewButton_1);
			btnNewButton_2.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// gw.loadMap();
				}
			});

			toolBar.add(btnNewButton_2);
			btnNewButton_3.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					System.out.println(e.getActionCommand());
				}
			});
			toolBar.add(btnNewButton_3);

			frmJpogiengine.getContentPane().add(panel, BorderLayout.CENTER);
			panel.setLayout(new BorderLayout(0, 0));
			panel.add(gw);
			gw.setSelected(false);
			
		} catch (MalformedURLException e) {
			System.out.println(e.getMessage());
			System.exit(0);
		} catch (ConfigurationException e) {
			System.out.println(e.getMessage());
			System.exit(0);
		}
	}

	public static void test(double dx, double dy, double w, double h) {
		java.sql.Connection conn;

		try {
			/*
			 * Загрузка драйвера JDBC и установление соединения.
			 */
			Class.forName("org.postgresql.Driver");
			String url = "jdbc:postgresql://192.168.33.110:5432/lines";
			conn = DriverManager.getConnection(url, "postgres", "pa1la2ma3");
			/*
			 * Добавляем геометрические типы в соединение. Заметим, что вы
			 * должны установить специальное соединение с pgsql до вызова метода
			 * addDataType().
			 */
			((PGConnection) conn).addDataType("geometry",
					org.postgis.PGgeometry.class);
			/*
			 * Создаем объект запроса и выполняем запрос select.
			 */
			gw.clearLines();
			Point xv = new Point(dx, dy);
			Point yv = new Point((dx + w), (dy + h));
			PGbox3d b3d = new PGbox3d(xv, yv);
			String selectAllIn = "select ST_TransScale(roads_geom, ?, ?, ?, ?) as geom, road_id, z from (select * from roads WHERE roads_geom && SetSRID(?::box3d,-1)) as box";
			PreparedStatement s = conn.prepareStatement(selectAllIn);
			s.setDouble(1, -dx);
			s.setDouble(2, -dy);

			s.setDouble(3, 1.0);
			s.setDouble(4, 1.0);
			s.setString(5, b3d.getValue());
			ResultSet r = s.executeQuery();

			PostgisParser pp = new PostgisParser("geom", r);
			ArrayList<Shape> shapes = pp.parseAll(Geometry.LINESTRING);
			System.out.println(shapes.size());
			
			s.close();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
