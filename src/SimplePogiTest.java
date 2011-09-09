import java.awt.EventQueue;

import javax.swing.JFrame;
import java.awt.BorderLayout;
import java.sql.*;

import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.JButton;
import org.postgresql.*;
import org.postgis.*;

import java.awt.event.*;
import javax.swing.JToggleButton;

public class SimplePogiTest {

	private JFrame frame;
	private static GeoWindow gw = new GeoWindow();
	private JToggleButton moveToggle = new JToggleButton("Move");
	private JToggleButton selectToggle = new JToggleButton("Select");
	private final JPanel panel = new JPanel();

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
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);

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

		frame.getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(new BorderLayout(0, 0));
		panel.add(gw);
		gw.setSelected(false);
		gw.setLayout(new BorderLayout(0, 0));
	}

	public static void test(double dx, double dy, double w, double h, double sx, double sy) {
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
			Point xv = new Point(-dx*sx, -dy*sy);
			Point yv = new Point((-dx + w)*sx, (-dy + h)*sy);
			PGbox3d b3d = new PGbox3d(xv, yv);
			System.out.printf("xv: %s, xy: %s\n", xv.getValue(), yv.getValue());
			System.out.printf("sx: %6.2f, sy: %6.2f\n", sx, sy);
			String selectAllIn = "select ST_TransScale(roads_geom, ?, ?, ?, ?) as geom, road_id, z from (select * from roads WHERE roads_geom && SetSRID(?::box3d,-1)) as box";
			PreparedStatement s = conn.prepareStatement(selectAllIn);
			s.setDouble(1, dx*sx);
			s.setDouble(2, dy*sy);
			if ( sx <= 0 && sy <= 0) {
				sx = sy = 1;
			}
			s.setDouble(3, 1/sx);
			s.setDouble(4, 1/sy);
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
					gw.addLine(r.getInt(2), z, ls.getFirstPoint(), ls
							.getLastPoint());
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
