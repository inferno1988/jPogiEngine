/*
 * Created by JFormDesigner on Tue Feb 28 17:13:54 EET 2012
 */

package ua.com.ifno.pogi;

import org.apache.commons.configuration.ConfigurationException;
import ua.com.ifno.pogi.LayerEngine.IconedCheckbox;
import ua.com.ifno.pogi.LayerEngine.LayerManager;
import ua.com.ifno.pogi.LayerEngine.LayerVisibilityTableRenderer;

import java.awt.*;
import java.awt.event.*;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.swing.*;
import javax.swing.table.TableModel;

/**
 * @author unknown
 */
public class JPogiEngine extends JFrame {

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                JPogiEngine pogiEngine = new JPogiEngine();
                pogiEngine.setVisible(true);
            }
        });
    }

    public JPogiEngine() {
        initComponents();
        try {
            ImageSettings is = ImageSettings.parseXML(new URL(
                    "http://192.168.33.110/config.xml"));
            Scaler scaler = new Scaler(is);
            layerManager = new LayerManager(is, scaler);
            geoWindow = new GeoWindow(layerManager, scaler);

        } catch (ConfigurationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (MalformedURLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        geoWindow.setIgnoreRepaint(true);
        geoWindow.setSelected(false);
        panel2.add(geoWindow, BorderLayout.CENTER);
        addWindowFocusListener(new Focused());
        addWindowListener(new Loop());
        GraphicsEnvironment ge = GraphicsEnvironment
                .getLocalGraphicsEnvironment();
        GraphicsDevice gs = ge.getDefaultScreenDevice();
        DisplayMode dm = gs.getDisplayMode();

        setBounds(100, 100, (int) (dm.getWidth() * 0.75),
                (int) (dm.getHeight() * 0.75));
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        TableModel tableModel = layerManager.getLayerTableModel();
        layerList.setModel(tableModel);
        layerList.setDefaultRenderer(Boolean.class, new LayerVisibilityTableRenderer());
        layerList.setDefaultEditor(Boolean.class, new DefaultCellEditor(new IconedCheckbox()));
        mapToolBar.add(new JButton("mama"));
    }

    private void mntmExitActionPerformed(ActionEvent e) {
        System.exit(0);
    }

    private void btnMoveActionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src instanceof JToggleButton) {
            if (((JToggleButton) src).isSelected()) {
                geoWindow.setMove(true);
                geoWindow.setSelected(false);
            } else {
                geoWindow.setMove(false);
            }
        }
    }

    private void btnPrintMouseClicked(MouseEvent e) {
        PrinterJob job = PrinterJob.getPrinterJob();
        PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
        PageFormat pf = job.pageDialog(aset);
        job.setPrintable(geoWindow, pf);
        boolean ok = job.printDialog(aset);
        if (ok) {
            try {
                job.print(aset);
            } catch (PrinterException ex) {
                JOptionPane.showMessageDialog(getParent(), "Print error");
            }
        }
    }

    private void button3ActionPerformed(ActionEvent e) {

     }

    class Loop extends WindowAdapter {
        @Override
        public void windowOpened(WindowEvent e) {
            geoWindow.init();
            Thread loop = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            if (WorkerPool.hasWorkers() && focused) {
                                Thread.yield();
                                Thread.sleep(5);
                                geoWindow.paint();
                            } else if (focused) {
                                Thread.sleep(50);
                                geoWindow.paint();
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
    }

    class Focused implements WindowFocusListener {
        @Override
        public void windowLostFocus(WindowEvent e) {
            focused = false;
        }

        @Override
        public void windowGainedFocus(WindowEvent e) {
            focused = true;
        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - Max Palamarchuk
        ResourceBundle bundle = ResourceBundle.getBundle("ua.com.ifno.pogi.locale");
        menuBar1 = new JMenuBar();
        menu1 = new JMenu();
        mntmExit = new JMenuItem();
        menu2 = new JMenu();
        menuItem2 = new JMenuItem();
        splitPane1 = new JSplitPane();
        splitPane2 = new JSplitPane();
        tabbedPane2 = new JTabbedPane();
        panel2 = new JPanel();
        mapToolBar = new JToolBar();
        button2 = new JButton();
        btnEditMode = new JToggleButton();
        btnMove = new JToggleButton();
        btnPrint = new JButton();
        panel3 = new JPanel();
        scrollPane2 = new JScrollPane();
        layerList = new JTable();
        panel4 = new JPanel();
        tabbedPane1 = new JTabbedPane();
        scrollPane1 = new JScrollPane();
        table1 = new JTable();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle(bundle.getString("Frame.title"));
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout(10, 10));

        //======== menuBar1 ========
        {

            //======== menu1 ========
            {
                menu1.setText(bundle.getString("Menu.File"));

                //---- mntmExit ----
                mntmExit.setText(bundle.getString("Menu.Exit"));
                mntmExit.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        mntmExitActionPerformed(e);
                    }
                });
                menu1.add(mntmExit);
            }
            menuBar1.add(menu1);

            //======== menu2 ========
            {
                menu2.setText(bundle.getString("Menu.Help"));

                //---- menuItem2 ----
                menuItem2.setText(bundle.getString("Menu.About"));
                menu2.add(menuItem2);
            }
            menuBar1.add(menu2);
        }
        setJMenuBar(menuBar1);

        //======== splitPane1 ========
        {
            splitPane1.setOrientation(JSplitPane.VERTICAL_SPLIT);
            splitPane1.setResizeWeight(0.9);

            //======== splitPane2 ========
            {
                splitPane2.setResizeWeight(0.8);

                //======== tabbedPane2 ========
                {
                    tabbedPane2.setTabPlacement(SwingConstants.BOTTOM);
                    tabbedPane2.setAlignmentX(0.0F);
                    tabbedPane2.setAlignmentY(0.0F);

                    //======== panel2 ========
                    {
                        panel2.setVerifyInputWhenFocusTarget(false);
                        panel2.setRequestFocusEnabled(false);
                        panel2.setFocusable(false);

                        // JFormDesigner evaluation mark
                        panel2.setBorder(new javax.swing.border.CompoundBorder(
                            new javax.swing.border.TitledBorder(new javax.swing.border.EmptyBorder(0, 0, 0, 0),
                                "JFormDesigner Evaluation", javax.swing.border.TitledBorder.CENTER,
                                javax.swing.border.TitledBorder.BOTTOM, new java.awt.Font("Dialog", java.awt.Font.BOLD, 12),
                                java.awt.Color.red), panel2.getBorder())); panel2.addPropertyChangeListener(new java.beans.PropertyChangeListener(){public void propertyChange(java.beans.PropertyChangeEvent e){if("border".equals(e.getPropertyName()))throw new RuntimeException();}});

                        panel2.setLayout(new BorderLayout());

                        //======== mapToolBar ========
                        {
                            mapToolBar.setPreferredSize(new Dimension(128, 32));
                            mapToolBar.setFocusable(false);
                            mapToolBar.setAlignmentX(0.0F);
                            mapToolBar.setAlignmentY(0.0F);
                            mapToolBar.setRollover(true);
                            mapToolBar.setFloatable(false);

                            //---- button2 ----
                            button2.setText(bundle.getString("JPogiEngine.button2.text"));
                            mapToolBar.add(button2);

                            //---- btnEditMode ----
                            btnEditMode.setIcon(new ImageIcon(getClass().getResource("/ua/com/ifno/pogi/resources/size16/gtk-edit.png")));
                            btnEditMode.setToolTipText(bundle.getString("JPogiEngine.btnEditMode.tooltip"));
                            btnEditMode.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    button3ActionPerformed(e);
                                }
                            });
                            mapToolBar.add(btnEditMode);

                            //---- btnMove ----
                            btnMove.setIcon(new ImageIcon(getClass().getResource("/ua/com/ifno/pogi/resources/size16/gtk-fullscreen.png")));
                            btnMove.setToolTipText(bundle.getString("JPogiEngine.btnMove.text"));
                            btnMove.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    btnMoveActionPerformed(e);
                                }
                            });
                            mapToolBar.add(btnMove);

                            //---- btnPrint ----
                            btnPrint.setToolTipText(bundle.getString("JPogiEngine.btnPrint.text"));
                            btnPrint.setIcon(new ImageIcon(getClass().getResource("/ua/com/ifno/pogi/resources/size16/gtk-print.png")));
                            btnPrint.addMouseListener(new MouseAdapter() {
                                @Override
                                public void mouseClicked(MouseEvent e) {
                                    btnPrintMouseClicked(e);
                                }
                            });
                            mapToolBar.add(btnPrint);
                            mapToolBar.addSeparator();
                        }
                        panel2.add(mapToolBar, BorderLayout.NORTH);
                    }
                    tabbedPane2.addTab(bundle.getString("JPogiEngine.panel2.tab.title"), panel2);


                    //======== panel3 ========
                    {
                        panel3.setLayout(new BorderLayout());
                    }
                    tabbedPane2.addTab(bundle.getString("JPogiEngine.panel3.tab.title"), panel3);

                }
                splitPane2.setLeftComponent(tabbedPane2);

                //======== scrollPane2 ========
                {
                    scrollPane2.setPreferredSize(new Dimension(100, 174));
                    scrollPane2.setMinimumSize(new Dimension(100, 25));

                    //---- layerList ----
                    layerList.setPreferredSize(new Dimension(100, 63));
                    layerList.setMinimumSize(new Dimension(100, 63));
                    layerList.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
                    scrollPane2.setViewportView(layerList);
                }
                splitPane2.setRightComponent(scrollPane2);
            }
            splitPane1.setTopComponent(splitPane2);

            //======== panel4 ========
            {
                panel4.setLayout(new BorderLayout());

                //======== tabbedPane1 ========
                {
                    tabbedPane1.setPreferredSize(new Dimension(455, 100));
                    tabbedPane1.setToolTipText(bundle.getString("Tab.Map"));
                    tabbedPane1.setMinimumSize(new Dimension(42, 100));
                    tabbedPane1.setMaximumSize(new Dimension(32767, 100));
                    tabbedPane1.setFocusable(false);

                    //======== scrollPane1 ========
                    {
                        scrollPane1.setPreferredSize(new Dimension(456, 100));
                        scrollPane1.setMinimumSize(new Dimension(25, 100));

                        //---- table1 ----
                        table1.setPreferredSize(new Dimension(150, 100));
                        table1.setMinimumSize(new Dimension(30, 100));
                        table1.setMaximumSize(new Dimension(2147483647, 1024));
                        scrollPane1.setViewportView(table1);
                    }
                    tabbedPane1.addTab(bundle.getString("JPogiEngine.scrollPane1.tab.title"), scrollPane1);

                }
                panel4.add(tabbedPane1, BorderLayout.CENTER);
            }
            splitPane1.setBottomComponent(panel4);
        }
        contentPane.add(splitPane1, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    private boolean focused = false;
    private GeoWindow geoWindow;
    private LayerManager layerManager;
    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - Max Palamarchuk
    private JMenuBar menuBar1;
    private JMenu menu1;
    private JMenuItem mntmExit;
    private JMenu menu2;
    private JMenuItem menuItem2;
    private JSplitPane splitPane1;
    private JSplitPane splitPane2;
    private JTabbedPane tabbedPane2;
    private JPanel panel2;
    private JToolBar mapToolBar;
    private JButton button2;
    private JToggleButton btnEditMode;
    private JToggleButton btnMove;
    private JButton btnPrint;
    private JPanel panel3;
    private JScrollPane scrollPane2;
    private JTable layerList;
    private JPanel panel4;
    private JTabbedPane tabbedPane1;
    private JScrollPane scrollPane1;
    private JTable table1;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
