import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import org.postgis.Geometry;
import org.postgis.LineString;
import org.postgis.PGbox3d;
import org.postgis.PGgeometry;
import org.postgis.Point;
import org.postgis.Polygon;
import org.postgresql.PGConnection;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class SimplePogiTest {

	private JFrame frame;
	private static GeoWindow gw;
	private JToggleButton moveToggle = new JToggleButton("Move");
	private JToggleButton selectToggle = new JToggleButton("Select");
	private final JPanel panel = new JPanel();
	private final JButton btnNewButton_1 = new JButton("Print");
	private final JButton btnNewButton_2 = new JButton("Map");
	private final JButton btnNewButton_3 = new JButton("Clear");

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SimplePogiTest window = new SimplePogiTest();
					window.frame.setVisible(true);
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
		frame = new JFrame();
		gw = new GeoWindow(loadSettings());
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				gw.init();
				Thread loop = new Thread(new Runnable() {
					@Override
					public void run() {
						while (true) {
							try {
								if (WorkerPool.hasWorkers()) {
									Thread.sleep(10);
									gw.paintAll();
								} else {
									Thread.sleep(20);
									gw.paintAll();
								}
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
				});
				loop.start();
			}
		});
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.getContentPane().setLayout(new BorderLayout());

		JToolBar toolBar = new JToolBar();
		frame.getContentPane().add(toolBar, BorderLayout.NORTH);
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
				gw.loadMap();
			}
		});

		toolBar.add(btnNewButton_2);
		btnNewButton_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
			}
		});
		toolBar.add(btnNewButton_3);

		frame.getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(new BorderLayout(0, 0));
		panel.add(gw);
		gw.setSelected(false);
	}
	
	private HashMap<String, String> loadSettings() {
		HashMap<String, String> settings = new HashMap<String, String>();
		try {
			String request = new String(
					"http://192.168.33.110/yTiles/tiles.info");
			URL url;
			url = new URL(request);
			InputStreamReader isr = new InputStreamReader(url
					.openStream());
			BufferedReader br = new BufferedReader(isr);
			String line;

			while ((line = br.readLine()) != null) {
				int i = line.indexOf('=');
				String obj, res;
				if (i != -1) {
					obj = line.substring(0, i);
					res = line.substring(i + 1);
				} else {
					obj = "(error)";
					res = "(error)";
				}
				settings.put(obj, res);
			}
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		return settings;
	}

	public static void test(double dx, double dy, double w, double h,
			double sx, double sy) {
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
			Point xv = new Point(-dx, -dy);
			Point yv = new Point((-dx + w), (-dy + h));
			//Point xv = new Point(-dx * sx, -dy * sy);
			//Point yv = new Point((-dx + w) * sx, (-dy + h) * sy);
			System.out.println("XV: " + xv.toString());
			System.out.println("YV: " + yv.toString());
			PGbox3d b3d = new PGbox3d(xv, yv);
			String selectAllIn = "select ST_TransScale(roads_geom, ?, ?, ?, ?) as geom, road_id, z from (select * from roads WHERE roads_geom && SetSRID(?::box3d,-1)) as box";
			PreparedStatement s = conn.prepareStatement(selectAllIn);
			s.setDouble(1, dx);
			s.setDouble(2, dy);
			if (sx <= 0 && sy <= 0) {
				sx = sy = 1;
			}
			s.setDouble(3, 1.0);
			s.setDouble(4, 1.0);
			System.out.println("Scale X: " + sx);
			s.setString(5, b3d.getValue());
			ResultSet r = s.executeQuery();

			int z = 0;
			while (r.next()) {
				/*
				 * Восстанавливаем геометрию как объект, какого то из
				 * геометрических типов. Выводим его.
				 */
				PGgeometry geom = (PGgeometry) r.getObject(1);
				z = r.getInt(3);
				int geoType = geom.getGeoType();
				if (geoType == Geometry.LINESTRING) {
					LineString ls = (LineString) geom.getGeometry();
					gw.addLine(r.getInt(2), z, ls.getFirstPoint(),
							ls.getLastPoint());
				}
				if (geoType == Geometry.POLYGON) {
					Polygon poly = (Polygon) geom.getGeometry();
					int num = poly.numPoints();
					int[] x = new int[num];
					int[] y = new int[num];
					for (int i = 0; i < num; i++) {
						x[i] = new Double(poly.getPoint(i).x).intValue();
						y[i] = new Double(poly.getPoint(i).y).intValue();
					}
					gw.addPolygon(r.getInt(2), z, x, y, num);
				}
			}
			gw.repaint();
			s.close();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
