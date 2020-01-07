package com.seqart.appstudio;

import static javax.swing.JFileChooser.APPROVE_OPTION;
import static javax.swing.JFileChooser.CANCEL_OPTION;
import static javax.swing.JFileChooser.ERROR_OPTION;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import org.apache.http.ParseException;

import com.opencsv.bean.CsvToBeanBuilder;
import com.seqart.appstudio.model.SampleList;

public class SampleSubmission extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;
	public static final String NIMBUS_LAF_NAME = "Nimbus";
	// JLabel buttonLabel;
	JFrame frame;
	JPanel mainPanel, uploadPanel, samplesPanel, platesPanel, submissionPanel;
	JTextPane messagesArea;
	JButton uploadButton, deleteButton, uploadtoDb, addPlateButton;
	JTable samplesTable, platesTable;
	JFileChooser chooser;
	JScrollPane table1Aggregate, table2Aggregate, messagesPane;
	SamplesAdapter samplesAdapter = new SamplesAdapter();
	PlatesAdapter platesAdapter = new PlatesAdapter();
	TableSorter sorter, platesSorter;
	int returnValue;
	GridBagLayout gridbag = new GridBagLayout();
	JTextField barcodeField, IDField;
	HashMap<String, String> barcodeMap = new HashMap<>();
	HashMap<String, String> IDMap = new HashMap<>();

	JTabbedPane tabbedPane = new JTabbedPane();

	ImageIcon icon = createImageIcon("/copy.gif");
	JTextField clientTextField, sampleTypeField;
	private static String message = null;

	public SampleSubmission() {

		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

		/*
		 * buttonLabel = new JLabel("File Upload"); buttonLabel.setPreferredSize(new
		 * Dimension(175, 100));
		 */

		uploadButton = new JButton("Upload Sample File");
		uploadButton.addActionListener(this);

		deleteButton = new JButton("Delete Plates");
		uploadtoDb = new JButton("UploadToDatabase");
		addPlateButton = new JButton("Add Plate");

		deleteButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				deleteRows();

			}
		});

		addPlateButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				tabbedPane.setSelectedIndex(1);
				/*
				 * platesTable.setModel(platesAdapter);
				 * platesTable.setAutoCreateRowSorter(true);
				 */

				platesSorter = new TableSorter(platesAdapter);
				platesSorter.addMouseListenerToHeaderInTable(platesTable);

				platesTable.setModel(platesSorter);

				if (returnValue == 0) {
					message = "Upload the Samples File First";
					appendToPane(messagesArea, message, Color.GREEN);
				} else if (platesAdapter.getRowCount() >= returnValue) {
					message = "Error: Cannot add another plate, No of plates Exceeds the samples";
					appendToPane(messagesArea, message, Color.RED);
				}

				else {
					platesAdapter.addRows();
					message = "Added " + platesAdapter.getRowCount() + " plate(s)";
					appendToPane(messagesArea, message, Color.BLUE);
				}

			}
		});

		final JPopupMenu popupMenu = new JPopupMenu();

		JMenuItem deleteItem = new JMenuItem("Delete");

		deleteItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				deleteRows();

			}
		});

		uploadtoDb.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String clientName = clientTextField.getText();
				String sampleType = sampleTypeField.getText();
				if (!clientName.isEmpty() & !sampleType.isEmpty()) {

					if (platesAdapter.getRowCount() == returnValue & returnValue != 0) {

						try {
							String response = samplesAdapter.convertToJson(platesAdapter.getPlates(), clientName,
									sampleType);
							message = "Submission sucess, submission ID is " + response;
							appendToPane(messagesArea, message, Color.RED);
							clientTextField.setText("");
							sampleTypeField.setText("");

						} catch (ParseException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					} else {
						message = "Error: Plates added and Samples Uploaded do not match";
						tabbedPane.setSelectedIndex(1);
						appendToPane(messagesArea, message, Color.RED);
					}

				} else {
					message = "Error: Fill out the submission details";
					appendToPane(messagesArea, message, Color.RED);
					tabbedPane.setSelectedIndex(2);
				}
			}
		});
		mainPanel = new JPanel();
		mainPanel.setBorder(BorderFactory.createTitledBorder("Main section"));
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.add(Box.createRigidArea(new Dimension(1, 5)));
		uploadPanel = new JPanel();
		uploadPanel.setBorder(BorderFactory.createTitledBorder("Actions"));
		uploadPanel.setLayout(new BoxLayout(uploadPanel, BoxLayout.Y_AXIS));
		// uploadPanel.add(Box.createRigidArea(new Dimension(1, 20)));

		// uploadPanel.add(buttonLabel);
		uploadPanel.add(Box.createRigidArea(new Dimension(1, 7)));
		uploadPanel.add(uploadButton);
		uploadPanel.add(Box.createRigidArea(new Dimension(1, 7)));
		uploadPanel.add(addPlateButton);
		uploadPanel.add(Box.createRigidArea(new Dimension(1, 7)));
		uploadPanel.add(deleteButton);

		add(uploadPanel);

		samplesTable = new JTable(new SamplesAdapter());
		platesTable = new JTable(new PlatesAdapter());
		table1Aggregate = new JScrollPane(samplesTable);
		table2Aggregate = new JScrollPane(platesTable);

		popupMenu.add(deleteItem);
		samplesTable.setComponentPopupMenu(popupMenu);

		samplesPanel = new JPanel();

		// samplesPanel.setBorder(BorderFactory.createTitledBorder("Result Section"));
		samplesPanel.setLayout(new BoxLayout(samplesPanel, BoxLayout.Y_AXIS));
		samplesPanel.add(Box.createRigidArea(new Dimension(1, 5)));
		samplesPanel.add(table1Aggregate);

		platesPanel = new JPanel();

		platesPanel.setLayout(new BoxLayout(platesPanel, BoxLayout.Y_AXIS));
		platesPanel.add(Box.createRigidArea(new Dimension(1, 5)));
		platesPanel.add(table2Aggregate);
		// platesPanel.add(Box.createRigidArea(new Dimension(1, 5)));
		// platesPanel.add(addPlateButton);

		submissionPanel = new JPanel();
		submissionPanel.setLayout(new GridBagLayout());
		submissionPanel.setBorder(BorderFactory.createTitledBorder("Enter submission details Here"));
		generateTextFields();
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 2;
		c.insets = new Insets(10, 0, 0, 0);
		submissionPanel.add(uploadtoDb, c);

		tabbedPane.addTab("Samples", icon, samplesPanel, "Samples Info");
		tabbedPane.addTab("Plates", icon, platesPanel, "Plates Info");
		tabbedPane.addTab("Submission Details", icon, submissionPanel, "Fill out submission details");
		mainPanel.add(tabbedPane);
		mainPanel.add(Box.createRigidArea(new Dimension(1, 5)));
		messagesArea = new JTextPane();
		messagesArea.setMaximumSize(new Dimension(2147483647, 30000));
		// System.out.println(messagesArea.getMaximumSize());

		messagesPane = new JScrollPane(messagesArea);

		mainPanel.add(messagesPane);
		add(mainPanel);

		frame = new JFrame("Sample Submission App");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		frame.getContentPane().add("Center", this);
		// frame.setPreferredSize(new Dimension(400, 300));
		frame.setBounds(200, 200, 900, 480);

		frame.pack();
		frame.setVisible(true);

	}

	public static void main(String[] args) {

		try {
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (Exception ex) {
			Logger.getLogger(SampleSubmission.class.getName()).log(Level.SEVERE, "Failed to apply Nimbus look and feel",
					ex);
		}

		try {
			SwingUtilities.invokeAndWait(new Runnable() {

				@Override
				public void run() {

					new SampleSubmission();

				}
			});
		} catch (Exception e) {

		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		chooser = new JFileChooser();
		// chooser.showOpenDialog(this);
		FileNameExtensionFilter filter = new FileNameExtensionFilter("takes csv's only", "csv");
		chooser.addChoosableFileFilter(filter);
		chooser.setAcceptAllFileFilterUsed(false);
		int retval = chooser.showDialog(frame, null);
		if (retval == APPROVE_OPTION) {
			tabbedPane.setSelectedIndex(0);

			String path = null;
			File file = chooser.getSelectedFile();
			path = file.getPath();

			String[] data = readHeaders(path);
			String csvType = compareHeaders(data);

			if (csvType.equals("samples")) {

				samplesAdapter.getSamplesList(getSamples(path));
				platesAdapter.deletePlates();
				messagesArea.setText("");
				clientTextField.setText("");
				sampleTypeField.setText("");

				sorter = new TableSorter(samplesAdapter);
				sorter.addMouseListenerToHeaderInTable(samplesTable);

				samplesTable.setModel(sorter);
				// table.setAutoCreateRowSorter(true);

				double noOfSamples = samplesTable.getRowCount() / (double) 94;
				if (noOfSamples % 1 == 0) {
					returnValue = (int) noOfSamples;
				}

				else {
					returnValue = ((int) noOfSamples) + 1;
				}

				message = "Detected  " + samplesTable.getRowCount() + " samples amounting to " + returnValue
						+ " plate(s)";

				appendToPane(messagesArea, message, Color.BLUE);

				// generateTextBarcodes(returnValue);

			}

			else {
				message = "Error: Wrong file format Uploaded";
				appendToPane(messagesArea, message, Color.RED);

			}

		}

		else if (retval == CANCEL_OPTION) {
			JOptionPane.showMessageDialog(frame, "User cancelled operation. No file was chosen.");
		} else if (retval == ERROR_OPTION) {
			JOptionPane.showMessageDialog(frame, "An error occurred. No file was chosen.");
		} else {
			JOptionPane.showMessageDialog(frame, "Unknown operation occurred.");
		}
	}

	private void generateTextFields() {

		GridBagConstraints c = new GridBagConstraints();

		JLabel clientLabel = new JLabel("Client Name");
		c.gridx = 0;
		c.gridy = 0;
		// Top padding
		c.insets = new Insets(10, 0, 0, 0);
		submissionPanel.add(clientLabel, c);

		clientTextField = new JTextField(15);
		c.gridx = 1;
		c.gridy = 0;
		c.insets = new Insets(10, 0, 0, 0);
		submissionPanel.add(clientTextField, c);

		JLabel sampleTypeLabel = new JLabel("Sample Type");
		c.gridx = 0;
		c.gridy = 1;
		c.insets = new Insets(10, 0, 0, 0);
		submissionPanel.add(sampleTypeLabel, c);
		sampleTypeField = new JTextField(15);
		c.gridx = 1;
		c.gridy = 1;
		c.insets = new Insets(10, 0, 0, 0);
		submissionPanel.add(sampleTypeField, c);

	}

	private void deleteRows() {

		if (platesTable.getSelectedRowCount() > 0) {

			// pass the table and sorter to csvAdapter
			platesAdapter.removeSelectedRows(platesTable, platesSorter);

		} else {
			System.err.println("No plate rows selected");
			// messagesArea.setText("No rows selected");
			message = "Error: No plate rows selected";
			appendToPane(messagesArea, message, Color.RED);
		}
	}

	private List<SampleList> getSamples(String path) {

		List<SampleList> beans = new ArrayList<SampleList>();

		try {
			beans = new CsvToBeanBuilder<SampleList>(new BufferedReader(new FileReader(path)))
					.withType(SampleList.class).build().parse();

		} catch (IllegalStateException | FileNotFoundException e) {

			e.printStackTrace();
		}

		return beans;
	}

	/*
	 * private List<PlateList> getPlates(String path) {
	 * 
	 * List<PlateList> beans = new ArrayList<PlateList>();
	 * 
	 * try { beans = new CsvToBeanBuilder<PlateList>(new BufferedReader(new
	 * FileReader(path))).withType(PlateList.class) .build().parse();
	 * 
	 * } catch (IllegalStateException | FileNotFoundException e) {
	 * 
	 * e.printStackTrace(); }
	 * 
	 * return beans; }
	 */

	private String compareHeaders(String[] firstRow) {

		if (Arrays.equals(firstRow, samplesAdapter.getColumnNames())) {

			return "samples";
		}

		else if (Arrays.equals(firstRow, platesAdapter.getColumnNames())) {
			return "plates";
		} else {
			return "none";
		}

	}

	private String[] readHeaders(String path) {

		BufferedReader csvReader = null;
		try {
			csvReader = new BufferedReader(new FileReader(path));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String row;
		try {
			row = csvReader.readLine();
			String[] data = row.split(",");
			return data;

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			csvReader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	protected static ImageIcon createImageIcon(String path) {
		java.net.URL imgURL = SampleSubmission.class.getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL);
		} else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}

	private void appendToPane(JTextPane tp, String msg, Color c) {
		StyleContext sc = StyleContext.getDefaultStyleContext();
		AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);

		aset = sc.addAttribute(aset, StyleConstants.FontFamily, "Lucida Console");
		aset = sc.addAttribute(aset, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);
		// Simulate a keyboard press enter event

		KeyEvent event = new KeyEvent(tp, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_ENTER,
				KeyEvent.CHAR_UNDEFINED);

		tp.setCharacterAttributes(aset, false);
		// tp.setEditable(false);
		tp.replaceSelection(msg);
		tp.dispatchEvent(event);
	}

}
