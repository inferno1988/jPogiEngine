package ua.com.ifno.pogi.LayerEngine;

import javax.swing.*;
import java.awt.*;

public class IconedCheckbox extends JCheckBox {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2078070389568953990L;
	private final Image redIcon = new ImageIcon(IconedCheckbox.class.getResource("/ua/com/ifno/pogi/resources/size16/package-purge.png")).getImage();
	private final Image greenIcon = new ImageIcon(IconedCheckbox.class.getResource("/ua/com/ifno/pogi/resources/size16/package-reinstall.png")).getImage();
	
	@Override
	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		if (isSelected())
			g2d.drawImage(greenIcon, 0, 0, null);
		else
			g2d.drawImage(redIcon, 0, 0, null);
		g2d.dispose();
	}
	
	@Override
	public Dimension getSize() {
		return new Dimension(16, 16);
	}
}
