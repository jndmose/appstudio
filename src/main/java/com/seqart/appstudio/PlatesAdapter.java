package com.seqart.appstudio;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

public class PlatesAdapter extends AbstractTableModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	List<List<Object>> rows = new ArrayList<List<Object>>();
	String[] columnNames = { "PlateNo", "clientPlateId", "clientPlateBarcode", "sampleSubmissionFormat" };

	@Override
	public int getRowCount() {

		return rows.size();
	}

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public Object getValueAt(int aRow, int aColumn) {
		List<Object> row = rows.get(aRow);
		return row.get(aColumn);
	}

	@Override
	public String getColumnName(int col) {
		return columnNames[col];
	}

	@Override
	public boolean isCellEditable(int row, int column) {

		String columnName = columnNames[column];

		switch (columnName) {

		case "clientPlateId":
			return true;

		case "clientPlateBarcode":
			return true;

		default:
			return false;
		}

	}

	@Override
	public Class<?> getColumnClass(int column) {
		String type = columnNames[column];
		switch (type) {
		case "PlateNo":
			return Integer.class;
		case "clientPlateId":
			return String.class;

		case "clientPlateBarcode":
			return String.class;

		case "sampleSubmissionFormat":
			return String.class;

		default:
			return String.class;
		}
	}

	public void removeSelectedRows(JTable table, TableSorter sorter) {

		int[] selectedrows = table.getSelectedRows();
		int[] array = new int[selectedrows.length];
		int[] sortedIndexes = sorter.getIndexes();
		for (int i = 0; i < selectedrows.length; i++) {

			int index = sortedIndexes[selectedrows[i]];

			array[i] = index;

		}

		Arrays.sort(array);
		// remove descending not to get arrays index out of bounds
		for (int i = array.length - 1; i >= 0; i--) {
			rows.remove(array[i]);
		}

		fireTableChanged(null);
	}

	public void deletePlates() {

		rows.clear();
		fireTableChanged(null);
	}

	public String[] getColumnNames() {
		return columnNames;
	}

	public List<List<Object>> getPlates() {
		return rows;
	}

	public void addRows() {

		List<Object> newRow = new ArrayList<Object>();
		int i = rows.size();

		newRow.add(Integer.sum(i, 1));
		newRow.add("plateID" + Integer.sum(i, 1));
		newRow.add("barcode" + Integer.sum(i, 1));
		newRow.add("PLATE_96");
		rows.add(newRow);

		fireTableChanged(null);

	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {

		List<Object> row = rows.get(rowIndex);
		row.set(columnIndex, aValue);

	}

}
