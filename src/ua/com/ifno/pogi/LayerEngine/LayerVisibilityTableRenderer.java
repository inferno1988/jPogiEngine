package ua.com.ifno.pogi.LayerEngine;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class LayerVisibilityTableRenderer extends IconedCheckbox implements TableCellRenderer {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3342347946266510912L;

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		if (value instanceof Boolean) {
			setSelected((Boolean) value);
		} else {
			setSelected(false);
		}
		return this;
	}
}
