package ua.com.ifno.pogi;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.EventQueue;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableModel;

import org.apache.commons.configuration.ConfigurationException;
import org.postgis.PGbox3d;
import org.postgis.Point;
import org.postgresql.PGConnection;

import ua.com.ifno.pogi.LayerEngine.LayerFactory;

public class SimplePogiTest {
	private JFrame frmJpogiengine;
	private GeoWindow gw;
	private JToggleButton moveToggle = new JToggleButton("");
	private JToggleButton selectToggle = new JToggleButton("");
	private final JPanel panel = new JPanel();
	private final JButton btnNewButton_1 = new JButton("");
	private boolean focused = false;
	private final JSplitPane topSplitPane = new JSplitPane();
	private JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
	private final JPanel bottomInformationalPane = new JPanel();
	JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
            topSplitPane, bottomInformationalPane);
	private final JList list = new JList();
	private final JPanel panel_1 = new JPanel();
	private final JPanel panel_2 = new JPanel();
	private final JPanel panel_3 = new JPanel();
	private final JLabel lblNewLabel = new JLabel("0 items");
	private final JScrollPane scrollPane = new JScrollPane();
	private final JTable table = new JTable();
	private final JPanel topInformationalPane = new JPanel();
	private final JTabbedPane tabbedPane_1 = new JTabbedPane(JTabbedPane.BOTTOM);
	private final JPanel panel_4 = new JPanel();
	private final JMenuBar menuBar = new JMenuBar();
	private final JMenu mnFile = new JMenu("File");
	private final JMenuItem mntmExit = new JMenuItem("Exit");

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
			Scaler scaler = new Scaler(is);
			LayerFactory layerFactory = new LayerFactory(is, scaler);
			gw = new GeoWindow(is, layerFactory, scaler);
			gw.setIgnoreRepaint(true);
			gw.setSelected(false);
			
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
										Thread.yield();
										Thread.sleep(5);
										gw.paint();
									} else if (focused) {
										Thread.sleep(50);
										gw.paint();
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
			toolBar.setFloatable(false);
			frmJpogiengine.getContentPane().add(toolBar, BorderLayout.NORTH);

			JButton btnNewButton = new JButton("");
			btnNewButton.setToolTipText("Load");
			btnNewButton.setSize(new Dimension(32, 32));
			btnNewButton.setIcon(new ImageIcon(SimplePogiTest.class.getResource("/ua/com/ifno/pogi/resources/size32/fileopen.png")));
			toolBar.add(btnNewButton);
			selectToggle.setToolTipText("Select");
			selectToggle.setIcon(new ImageIcon(SimplePogiTest.class.getResource("/ua/com/ifno/pogi/resources/size32/gtk-yes.png")));

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
			moveToggle.setToolTipText("Move");
			moveToggle.setIcon(new ImageIcon(SimplePogiTest.class.getResource("/ua/com/ifno/pogi/resources/size32/gtk-fullscreen.png")));
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
			btnNewButton_1.setToolTipText("Print");
			btnNewButton_1.setIcon(new ImageIcon(SimplePogiTest.class.getResource("/ua/com/ifno/pogi/resources/size32/gtk-print.png")));
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

			frmJpogiengine.getContentPane().add(panel, BorderLayout.CENTER);
			panel.setLayout(new BorderLayout(0, 0));
			topSplitPane.setFocusTraversalKeysEnabled(false);
			topSplitPane.setFocusable(false);
			topSplitPane.setResizeWeight(0.75);
			topSplitPane.setRequestFocusEnabled(false);
			splitPane.setResizeWeight(1.0);
			
			panel.add(splitPane, BorderLayout.CENTER);
			panel_1.setMaximumSize(new Dimension(1024, 768));
			panel_1.setSize(new Dimension(1024, 768));
			panel_1.setPreferredSize(new Dimension(1024, 768));
			panel_1.setLayout(new BorderLayout(0, 0));
			topInformationalPane.setFocusable(false);
			topInformationalPane.setFocusTraversalKeysEnabled(false);
			topInformationalPane.setPreferredSize(new Dimension(800, 600));
			topInformationalPane.setMinimumSize(new Dimension(800, 600));
			topSplitPane.setLeftComponent(topInformationalPane);
			topSplitPane.setRightComponent(list);
			list.setModel(layerFactory.getLayerListModel());
			list.setSelectedIndex(0);
			topInformationalPane.setLayout(new BorderLayout(0, 0));
			tabbedPane_1.setFocusTraversalKeysEnabled(false);
			tabbedPane_1.setFocusable(false);
			topInformationalPane.add(tabbedPane_1);
			tabbedPane_1.addTab("Map ", new ImageIcon(SimplePogiTest.class.getResource("/javax/swing/plaf/metal/icons/ocean/menu.gif")), gw, null);
			panel_4.setFocusTraversalKeysEnabled(false);
			panel_4.setFocusable(false);
			
			tabbedPane_1.addTab("Tools ", new ImageIcon(SimplePogiTest.class.getResource("/javax/swing/plaf/metal/icons/ocean/computer.gif")), panel_4, null);
			panel_4.setLayout(null);
			
			frmJpogiengine.setJMenuBar(menuBar);
			
			menuBar.add(mnFile);
			mntmExit.setIcon(new ImageIcon(SimplePogiTest.class.getResource("/javax/swing/plaf/metal/icons/ocean/paletteClose.gif")));
			mntmExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_MASK));
			
			mnFile.add(mntmExit);
			bottomInformationalPane.setMinimumSize(new Dimension(10, 100));
			bottomInformationalPane.setLayout(new BorderLayout(0, 0));
			bottomInformationalPane.add(lblNewLabel, BorderLayout.NORTH);
			
			tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
			tabbedPane.addTab("Info", null, panel_3, null);
			panel_3.setLayout(new BorderLayout(0, 0));
			scrollPane.setViewportBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
			
			panel_3.add(scrollPane, BorderLayout.CENTER);
			bottomInformationalPane.add(tabbedPane);
			table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			table.setFillsViewportHeight(true);
			table.setModel(new DefaultTableModel(
				new Object[][] {
					{null, null},
					{null, null},
					{null, null},
					{null, null},
					{null, null},
				},
				new String[] {
					"Name", "Value"
				}
			));
			
			scrollPane.setViewportView(table);
			
			tabbedPane.addTab("Debug", null, panel_2, null);
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
			//ResultSet r = s.executeQuery();

			//PostgisParser pp = new PostgisParser("geom", r);
			//ArrayList<Shape> shapes = pp.parseAll();
			s.close();
			conn.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
