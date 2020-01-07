package com.seqart.appstudio;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.dart.submission.model.Plate;
import com.dart.submission.model.Sample;
import com.dart.submission.model.Submission;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seqart.appstudio.model.SampleList;

/**
 * Reads sample file from CSV using opencsv library
 */

public class SamplesAdapter extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	List<List<Object>> rows = new ArrayList<List<Object>>();
	private static String[] columnNames = { "number", "row", "clientSampleId", "comments", "organismName", "column",
			"tissueType" };
	PlatesAdapter platesAdapter = new PlatesAdapter();

	public void getSamplesList(List<SampleList> samples) {

		deleteData();

		Iterator<SampleList> csvSampleIterator = samples.iterator();
		while (csvSampleIterator.hasNext()) {
			List<Object> newRow = new ArrayList<Object>();

			SampleList sample = csvSampleIterator.next();
			newRow.add(sample.getNumber());
			newRow.add(sample.getRow());
			newRow.add(sample.getClientSampleId());
			newRow.add(sample.getComments());
			newRow.add(sample.getOrganismName());
			newRow.add(sample.getColumn());
			newRow.add(sample.getTissueType());
			rows.add(newRow);

		}
		fireTableChanged(null);

	}

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

		case "clientSampleId":
			return true;

		case "comments":
			return true;

		default:
			return false;
		}

	}

	@Override
	public Class<?> getColumnClass(int column) {
		String type = columnNames[column];
		switch (type) {
		case "number":
			return Integer.class;
		case "row":
			return String.class;

		case "clientSampleId":
			return String.class;

		case "comments":
			return String.class;

		case "organismName":
			return String.class;

		case "column":

			return Integer.class;

		case "tissueType":
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

		/*
		 * try { convertToJson(rows); } catch (ParseException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); } catch (IOException e) { //
		 * TODO Auto-generated catch block e.printStackTrace(); }
		 */
		fireTableChanged(null);
	}

	public static int findIndex(int arr[], int t) {
		int len = arr.length;
		return IntStream.range(0, len).filter(i -> t == arr[i]).findFirst() // first occurrence
				.orElse(-1); // No element found
	}

	public void deleteData() {

		if (getRowCount() == 0) {
			return;
		}
		rows.clear();
		fireTableChanged(null);
	}

	public String convertToJson(List<List<Object>> plateRows, String clientName, String sampleType)
			throws ParseException, IOException {

		CloseableHttpClient client = HttpClients.createDefault();

		Submission submission = new Submission();
		submission.setClientId(clientName);
		submission.setNumberOfSamples((long) getRowCount());
		submission.setSampleType(sampleType);

		List<Plate> plateList = new ArrayList<>();

		int partitionSize = 94;

		List<List<List<Object>>> partitions = new LinkedList<List<List<Object>>>();
		for (int i = 0; i < rows.size(); i += partitionSize) {
			partitions.add(rows.subList(i, Math.min(i + partitionSize, rows.size())));
		}
		int count = 0;

		for (List<List<Object>> partition : partitions) {
			count++;
			Plate plate = new Plate();
			List<Sample> sampleList = new ArrayList<>();
			partition.stream().forEach(dataSet -> {

				Sample sample = new Sample();
				sample.setComments(dataSet.get(3).toString());
				sample.setColumnNumber((Long) dataSet.get(5));
				sample.setClientSampleId(dataSet.get(2).toString());
				sample.setOrganismName(dataSet.get(4).toString());
				sample.setRow(dataSet.get(1).toString());
				sample.setTissueType(dataSet.get(6).toString());

				sampleList.add(sample);
			});
			plate.setClientPlateBarcode(plateRows.get(count - 1).get(2).toString());
			plate.setClientPlateId(plateRows.get(count - 1).get(1).toString());
			plate.setSampleSubmissionFormat(plateRows.get(count - 1).get(3).toString());
			plate.setSamples(sampleList);
			plateList.add(plate);
		}

		submission.setPlates(plateList);

		ObjectMapper mapper = new ObjectMapper();

		String json = mapper.writeValueAsString(submission);

		System.out.println(json);

		HttpPost post = new HttpPost("http://localhost:8090/vendor/orders");
		post.setHeader("Content-Type", "application/json");

		StringEntity entity = new StringEntity(json);
		post.setEntity(entity);

		CloseableHttpResponse response = client.execute(post);
		HttpEntity httpEntity = response.getEntity();
		String responseString = EntityUtils.toString(httpEntity, "UTF-8");

		return responseString;

	}

	public String[] getColumnNames() {
		return columnNames;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {

		List<Object> row = rows.get(rowIndex);
		row.set(columnIndex, aValue);

	}

}
