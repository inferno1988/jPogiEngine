package ua.com.ifno.pogi.LayerEngine;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

public class LayerListModel extends AbstractTableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4190524999423519140L;
	private final ArrayList<Layer> list = new ArrayList<Layer>();
	
	private final String[] headers = { "Visibility", "Description" };

	@SuppressWarnings("rawtypes")
    private final
    Class[] columnClasses = { Boolean.class, String.class };
	
	@Override
	public Class<?> getColumnClass(int arg0) {
		return columnClasses[arg0];
	}

	@Override
	public int getColumnCount() {
		return headers.length;
	}

	@Override
	public String getColumnName(int arg0) {
		return headers[arg0];
	}

	@Override
	public int getRowCount() {
		if (list == null)
			return 0;
		return list.size();
	}
	
	public void addElement(Layer layer) {
		if (layer != null)
			list.add(layer);
		fireTableDataChanged();
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 0;
    }
	
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (list == null)
			return null;
		switch (columnIndex) {
		case 0:
			return list.get(rowIndex).isVisible();
		case 1:
			return list.get(rowIndex).getLayerName();
		}
		return "<None>";
	}
	
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		switch (columnIndex) {
		case 0:
			list.get(rowIndex).setVisible((Boolean)aValue);
			break;
		case 1:
			list.get(rowIndex).setLayerName((String)aValue);
			break;
		}
		fireTableCellUpdated(rowIndex, columnIndex);
	}
}
