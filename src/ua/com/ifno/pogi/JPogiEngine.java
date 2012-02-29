/*
 * Created by JFormDesigner on Tue Feb 28 17:13:54 EET 2012
 */

package ua.com.ifno.pogi;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

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
    }

    private void mntmExitActionPerformed(ActionEvent e) {
        System.exit(0);
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

        //======== this ========
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
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
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - Max Palamarchuk
    private JMenuBar menuBar1;
    private JMenu menu1;
    private JMenuItem mntmExit;
    private JMenu menu2;
    private JMenuItem menuItem2;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
