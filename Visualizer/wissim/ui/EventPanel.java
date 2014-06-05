package wissim.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.SwingUtilities;
import javax.swing.RowFilter.Entry;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import TraceFileParser.wissim.Event;
import TraceFileParser.wissim.NodeTrace;
import TraceFileParser.wissim.Packet;

import wissim.controller.animation.EventAnimation;
import wissim.controller.filters.gui.AutoChoices;
import wissim.controller.filters.gui.IFilterEditor;
import wissim.controller.filters.gui.TableFilterHeader;
import wissim.controller.filters.gui.TableFilterHeader.Position;
import wissim.object.table.ButtonColumn;
import wissim.object.table.EventTableModel;
import wissim.object.table.FloatRenderer;
import wissim.object.table.NodeTableModel;
import wissim.object.table.TableColumnAdjuster;
import wissim.ui.sliderevent.RangeSlider;

public class EventPanel extends JPanel implements IObserver, ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6751758412065424164L;

	/**
	 * Create the panel.
	 */

	private MainViewPanel objMainView;
	private JTable eTable;
	private TableColumnAdjuster tca;
	private TableFilterHeader header;
	private JPanel searchPanel;
	private JPanel viewPanel;
	private JButton search;
	private JButton view;
	private JTextField timeFrom;
	private JTextField sourceFrom;
	private JTextField destFrom;
	private JTextField timeTo;
	private JTextField sourceTo;
	private JTextField destTo;
	private JTextField packetFrom;
	private JTextField packetTo;
	private JTextField typeTxt;
	private JButton export;
	private JComboBox copyCbox;
	private JButton copy;
	private NodePanel nodePanel;
	private PacketPanel packetPanel; 
	private JLabel resultInfo;
	private long timeSearch;
	private ArrayList<Integer> filtered = new ArrayList<>();

	private static final Insets WEST_INSETS = new Insets(0, 5, 0, 100);
	private static final Insets EAST_INSETS = new Insets(0, 5, 0, 0);

	public EventPanel(MainViewPanel inp_objMainView) {
		objMainView = inp_objMainView;
		setLayout(new BorderLayout(5, 5));
		eTable = new JTable(
				EventTableModel.createEventTableModel(objMainView.mParser));
		eTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		eTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 15));
		add(new JScrollPane(eTable), BorderLayout.CENTER);
		setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

		header = new TableFilterHeader(eTable, AutoChoices.ENABLED);
		header.setPosition(Position.TOP);
		tca = new TableColumnAdjuster(eTable, 20);
		tca.adjustColumns();

		objMainView.register(this);

		searchPanel = new JPanel(new GridBagLayout());

		JLabel idLabel = new JLabel("-Time :  From ");
		searchPanel.add(idLabel, createGbc(0, 0));
		JLabel xLabel = new JLabel("-SourceID  :  From ");
		searchPanel.add(xLabel, createGbc(0, 1));
		JLabel yLabel = new JLabel("-DestinationID  :  From ");
		searchPanel.add(yLabel, createGbc(0, 2));

		timeFrom = new JTextField(10);
		searchPanel.add(timeFrom, createGbc(1, 0));
		sourceFrom = new JTextField(10);
		searchPanel.add(sourceFrom, createGbc(1, 1));
		destFrom = new JTextField(10);
		searchPanel.add(destFrom, createGbc(1, 2));

		searchPanel.add(new JLabel("To"), createGbc(2, 0));
		searchPanel.add(new JLabel("To"), createGbc(2, 1));
		searchPanel.add(new JLabel("To"), createGbc(2, 2));

		timeTo = new JTextField(10);
		searchPanel.add(timeTo, createGbc(3, 0));
		sourceTo = new JTextField(10);
		searchPanel.add(sourceTo, createGbc(3, 1));
		destTo = new JTextField(10);
		searchPanel.add(destTo, createGbc(3, 2));

		searchPanel.add(new JLabel("-Type:"), createGbc(5, 0));
		typeTxt = new JTextField(10);
		searchPanel.add(typeTxt, createGbc(6, 0));

		searchPanel.add(new JLabel("-PacketID : From "), createGbc(5, 1));
		packetFrom = new JTextField(10);
		packetTo = new JTextField(10);
		searchPanel.add(packetFrom, createGbc(6, 1));
		searchPanel.add(new JLabel("To"), createGbc(7, 1));
		searchPanel.add(packetTo, createGbc(8, 1));

		searchPanel.setBorder(BorderFactory.createTitledBorder("Search"));
		search = new JButton("Search");
		search.setMargin(new Insets(0, 0, 0, 0));
		search.addActionListener(this);
		searchPanel.add(search, createGbc(9, 1));

		view = new JButton("View");
		view.addActionListener(this);
		export = new JButton("Export");
		export.addActionListener(this);
		viewPanel = new JPanel(new GridBagLayout());
		
		String[] items = {"Copy to node"};
		copyCbox = new JComboBox(items);
		copy = new JButton("Copy=>");
		copy.addActionListener(this);
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(0, 5, 0, 5);
		c.gridwidth = 3;
		resultInfo = new JLabel();
		viewPanel.add(resultInfo,c);
		c.gridx = 3;
		c.anchor = GridBagConstraints.EAST;
		c.gridwidth = 1;
		c.weightx = 0.1;
//		viewPanel.add(copy,c);
		viewPanel.add(view, c);
		c.gridx = 4;
		viewPanel.add(export);

		add(searchPanel, BorderLayout.NORTH);
		add(viewPanel, BorderLayout.SOUTH);
	}
	
	public void setNodePanel(NodePanel nPanel){
		nodePanel = nPanel;
	}
	
	public void setPacketPane(PacketPanel pPanel){
		packetPanel = pPanel;
	}
	
	public void updateTable(int item,ArrayList<Integer> filteredData){
		filtered = filteredData;
		if(item == 0){
			RowFilter sourceFilter = new RowFilter(){
				@Override
				public boolean include(Entry entry) {
					Object value = entry.getValue(2);
	                if (value instanceof Integer) {
	                    int source = (Integer) value;
	                    if(filtered.contains(source)){
	                    	return true;
	                    }else 
	                    	return false;
	                }

	                return false;
				}
				
			};
			
			header.getFilterEditor(2).directFilter(sourceFilter);
		}
		if(item == 1){
			RowFilter destFilter = new RowFilter(){
				@Override
				public boolean include(Entry entry) {
					Object value = entry.getValue(5);
	                if (value instanceof Integer) {
	                    int dest = (Integer) value;
	                    if(filtered.contains(dest)){
	                    	return true;
	                    }else 
	                    	return false;
	                }

	                return false;
				}
				
			};
			
			header.getFilterEditor(5).directFilter(destFilter);
		}
		if(item == 2){
			RowFilter packetFilter = new RowFilter(){
				@Override
				public boolean include(Entry entry) {
					Object value = entry.getValue(4);
	                if (value instanceof Integer) {
	                    int packetID = (Integer) value;
	                    if(filtered.contains(packetID)){
	                    	return true;
	                    }else 
	                    	return false;
	                }

	                return false;
				}
				
			};
			
			header.getFilterEditor(4).directFilter(packetFilter);
		}
	}

	private GridBagConstraints createGbc(int x, int y) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = x;
		gbc.gridy = y;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;

		gbc.anchor = (x == 0) ? GridBagConstraints.WEST
				: GridBagConstraints.EAST;
		gbc.fill = (x == 0) ? GridBagConstraints.BOTH
				: GridBagConstraints.HORIZONTAL;

		gbc.insets = (x == 3) ? WEST_INSETS : EAST_INSETS;
		// gbc.ipadx = (x == 1) ? 70 : 0 ;
		gbc.weightx = (x == 0 || x == 2 || x == 4 || x == 5 || x == 7) ? 0.1
				: 1.0;
		gbc.weighty = 1.0;
		return gbc;
	}

	private void setupHeader(TableFilterHeader header) {
		getEditor(header, EventTableModel.TIME, new FloatRenderer());
	}
	
	public TableFilterHeader getFilterHeader(){
		return this.header;
	}

	private IFilterEditor getEditor(TableFilterHeader header, String name,
			TableCellRenderer renderer) {
		TableColumnModel model = header.getTable().getColumnModel();
		for (int i = model.getColumnCount(); --i >= 0;) {
			TableColumn tc = model.getColumn(i);
			if (name.equals(tc.getHeaderValue())) {
				if (renderer != null) {
					tc.setCellRenderer(renderer);
					// System.err.println("----"+tc.getHeaderValue()+"set render");
				}
				return header.getFilterEditor(i);
			}
		}
		return null;
	}

	@Override
	public void update(IObservable subject) {
		// TODO Auto-generated method stub
		if (subject == objMainView) {
			eTable.setModel(EventTableModel
					.createEventTableModel(objMainView.mParser));
			tca.adjustColumns();
			setupHeader(header);

			Action view = new AbstractAction() {

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					JTable table = (JTable) e.getSource();
					int modelRow = Integer.valueOf(e.getActionCommand());
					Event ed = ((EventTableModel) table.getModel())
							.getRow(modelRow);
					String info = "Event time: " + ed.time + "\n"
							+ "SourceID: " + ed.sourceId + "\n"
							+ "DestinationID: " + ed.destId + "\n";
					JOptionPane.showMessageDialog(null, info, "Event info",
							JOptionPane.INFORMATION_MESSAGE);

				}
			};
			// ButtonColumn buttonColumn = new ButtonColumn(eTable, view, 7);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource() == search) {
			long startTime = System.nanoTime();
			RowFilter timeFilter = new RowFilter() {

				@Override
				public boolean include(Entry entry) {
					Object value = entry.getValue(0);
					if (value instanceof Double) {
						Double time = (Double) value;
						if (!timeFrom.getText().isEmpty()
								&& !timeTo.getText().isEmpty()) {
							return (time >= Double.parseDouble(timeFrom
									.getText().trim()))
									&& (time <= Double.parseDouble(timeTo
											.getText().trim()));
						}
						if (timeFrom.getText().isEmpty()
								&& timeTo.getText().isEmpty()) {
							return true;
						}
						if (!timeFrom.getText().isEmpty()
								&& timeTo.getText().isEmpty()) {
							return (time >= Double.parseDouble(timeFrom
									.getText().trim()));
						}
						if (timeFrom.getText().isEmpty()
								&& !timeTo.getText().isEmpty()) {
							return (time <= Double.parseDouble(timeTo.getText()
									.trim()));
						}
					}

					return false;
				}

			};
			header.getFilterEditor(0).directFilter(timeFilter);

			// type search
			RowFilter typeFilter = new RowFilter() {
				@Override
				public boolean include(Entry entry) {
					Object value = entry.getValue(1);
					if (value instanceof String) {
						String type = (String) value;
						if (!typeTxt.getText().isEmpty()) {
							return type.equals(typeTxt.getText().trim());
						}
						if (typeTxt.getText().isEmpty()) {
							return true;
						}
					}
					return false;
				}

			};
			header.getFilterEditor(1).directFilter(typeFilter);

			RowFilter sourceFilter = new RowFilter() {
				@Override
				public boolean include(Entry entry) {
					Object value = entry.getValue(2);
					if (value instanceof Integer) {
						int source = (Integer) value;
						if (!sourceFrom.getText().isEmpty()
								&& !sourceTo.getText().isEmpty()) {
							return (source >= Integer.valueOf(sourceFrom
									.getText().trim()))
									&& (source <= Integer.valueOf(sourceTo
											.getText().trim()));
						}
						if (sourceFrom.getText().isEmpty()
								&& !sourceTo.getText().isEmpty()) {
							return (source <= Integer.valueOf(sourceTo
									.getText().trim()));
						}
						if (!sourceFrom.getText().isEmpty()
								&& sourceTo.getText().isEmpty()) {
							return (source >= Integer.valueOf(sourceFrom
									.getText().trim()));
						}
						if (sourceFrom.getText().isEmpty()
								&& sourceTo.getText().isEmpty()) {
							return true;
						}
					}

					return false;
				}

			};

			header.getFilterEditor(2).directFilter(sourceFilter);

			// destID search
			RowFilter destFilter = new RowFilter() {
				@Override
				public boolean include(Entry entry) {
					Object value = entry.getValue(5);
					if (value instanceof Integer) {
						int dest = (Integer) value;
						if (!destFrom.getText().isEmpty()
								&& !destTo.getText().isEmpty()) {
							return (dest >= Integer.valueOf(destFrom.getText()
									.trim()))
									&& (dest <= Integer.valueOf(destTo
											.getText().trim()));
						}
						if (destFrom.getText().isEmpty()
								&& !destTo.getText().isEmpty()) {
							return (dest <= Integer.valueOf(destTo.getText()
									.trim()));
						}
						if (!destFrom.getText().isEmpty()
								&& destTo.getText().isEmpty()) {
							return (dest >= Integer.valueOf(destFrom.getText()
									.trim()));
						}
						if (destFrom.getText().isEmpty()
								&& destTo.getText().isEmpty()) {
							return true;
						}
					}

					return false;
				}

			};

			header.getFilterEditor(5).directFilter(destFilter);

			// packetID search
			RowFilter packetIDFilter = new RowFilter() {
				@Override
				public boolean include(Entry entry) {
					Object value = entry.getValue(4);
					if (value instanceof Integer) {
						int packetID = (Integer) value;
						if (!packetFrom.getText().isEmpty()
								&& !packetTo.getText().isEmpty()) {
							return (packetID >= Integer.valueOf(packetFrom
									.getText().trim()))
									&& (packetID <= Integer.valueOf(packetTo
											.getText().trim()));
						}
						if (packetFrom.getText().isEmpty()
								&& !packetTo.getText().isEmpty()) {
							return (packetID <= Integer.valueOf(packetTo
									.getText().trim()));
						}
						if (!packetFrom.getText().isEmpty()
								&& packetTo.getText().isEmpty()) {
							return (packetID >= Integer.valueOf(packetFrom
									.getText().trim()));
						}
						if (packetFrom.getText().isEmpty()
								&& packetTo.getText().isEmpty()) {
							return true;
						}
					}

					return false;
				}

			};

			header.getFilterEditor(4).directFilter(packetIDFilter);
			long endTime = System.nanoTime();
			timeSearch = endTime-startTime;
			//print result info
			int result = 0;
			for (int j = 0; j < eTable.getModel().getRowCount(); j++) {
				if (eTable.getRowSorter().convertRowIndexToView(j) != -1) {
					result ++;
				}
			}
			
			resultInfo.setText("Result : "+result+ " events in "+timeSearch/1000000+" ms");

		}
		if (e.getSource() == view) {
			// System.err.println("Done///");
			if (objMainView.mGraph != null) {
				objMainView.ea.reset(objMainView.mGraph);
				ArrayList<Event> filteredEvent = new ArrayList<>();
				for (int i = 0; i < eTable.getModel().getRowCount(); i++) {
					if (eTable.getRowSorter().convertRowIndexToView(i) != -1) {
						Event event = ((EventTableModel) eTable.getModel())
								.getRow(i);
						filteredEvent.add(event);
					}
				}
				// searchMVP.ea.animationEvent("0", "0", searchMVP.mGraph);
				// searchMVP.mSliderPanel.remove(searchMVP.mTimeslider);
				// searchMVP.mTimeslider = null;
				// searchMVP.mTimeslider = new
				// RangeSlider(0,filteredEvent.size());
				// searchMVP.mTimeslider.setValue(0);
				// searchMVP.mTimeslider.setUpperValue(0);
				// searchMVP.mTimeslider.setMinorTickSpacing(500);
				// searchMVP.mTimeslider.setPaintTicks(true);
				// searchMVP.mTimeslider
				// .setPreferredSize(new Dimension(
				// searchMVP.mSliderPanel.getWidth() - 10,
				// searchMVP.mSliderPanel
				// .getHeight() - 10));
				// searchMVP.mTimeslider.setBackground(Color.WHITE);
				// searchMVP.setTimeSliderListenter();

				// searchMVP.mSliderPanel.add(searchMVP.mTimeslider);
				// searchMVP.mCurrentStartTime = filteredEvent.get(0).time;
				// searchMVP.mCurrentEndTime = filteredEvent.get(0).time;

				// searchMVP.ea.setListEvents(filteredEvent);
				objMainView.ea.getDrawer().onDraw(filteredEvent,
						objMainView.mGraph);
				// objMainView.ea.animationEvent(filteredEvent.get(0).time,
				// filteredEvent.get(0).time, objMainView.mGraph);

				JTabbedPane tabPane = (JTabbedPane) SwingUtilities
						.getAncestorOfClass(JTabbedPane.class, eTable);
				tabPane.setSelectedIndex(4);

				// JFrame filteredFrame = new JFrame("Filtered event");
				// filteredFrame.setLayout(new BorderLayout());
				//
				// filteredFrame.getContentPane().add(searchMVP.visualizerPanel,BorderLayout.CENTER);
				// filteredFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				// filteredFrame.setSize(800,600);
				// // filteredFrame.pack();
				// filteredFrame.setVisible(true);
			}
		}
		if(e.getSource() == export){
			File exportFile = null;
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setDialogTitle("Specify a file to export");
			fileChooser.setFileFilter(new FileFilter() {
				
				@Override
				public String getDescription() {
					// TODO Auto-generated method stub
					return "Excel file";
				}
				
				@Override
				public boolean accept(File f) {
					// TODO Auto-generated method stub
					int i = f.getName().lastIndexOf(".");
					
					if(i >0){
						if(f.getName().substring(i+1).equals("xls")){
							return true;
						}else
							return false;
					}else
						return true;
				}
			});
			fileChooser.setFileFilter(new FileFilter() {
				
				@Override
				public String getDescription() {
					// TODO Auto-generated method stub
					return "CSV file";
				}
				
				@Override
				public boolean accept(File f) {
					// TODO Auto-generated method stub
					int i = f.getName().lastIndexOf(".");
					
					if(i >0){
						if(f.getName().substring(i+1).equals("csv")){
							return true;
						}else
							return false;
					}else
						return true;
				}
			});
			int userSelection = fileChooser.showSaveDialog(EventPanel.this);

			if (userSelection == JFileChooser.APPROVE_OPTION) {
				File fileToSave = fileChooser.getSelectedFile();
				if(fileChooser.getFileFilter().getDescription().equals("Excel file")){
					int i = fileToSave.getName().lastIndexOf(".");
					if(i>3){
						if(fileToSave.getName().substring(i+1).equals("xls")){
							exportFile = fileToSave;
						} else {
							exportFile = new File(fileChooser.getCurrentDirectory().getAbsolutePath()+File.separator+fileToSave.getName()+".xls");
						}
					}else {
						exportFile = new File(fileChooser.getCurrentDirectory().getAbsolutePath()+File.separator+fileToSave.getName()+".xls");
					}
					
					Workbook wb = new HSSFWorkbook();
					Sheet newSheet = wb.createSheet("Node");
					org.apache.poi.ss.usermodel.Font sheetFont = wb.createFont();
					CellStyle sheetsCellStyle = wb.createCellStyle();
					
					sheetFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
					
					Row fRow = newSheet.createRow(0);
					fRow.createCell(0).setCellValue("Time");
					fRow.createCell(1).setCellValue("Type");
					fRow.createCell(2).setCellValue("Source node ID");
					fRow.createCell(3).setCellValue("Source port");
					fRow.createCell(4).setCellValue("PacketID");
					fRow.createCell(5).setCellValue("Destination node ID");
					fRow.createCell(6).setCellValue("Destination port");
					int r = 1;
					for (int j = 0; j < eTable.getModel().getRowCount(); j++) {
						if (eTable.getRowSorter().convertRowIndexToView(j) != -1) {
							Event evt  = ((EventTableModel) eTable.getModel()).getRow(j);
							Row newRow = newSheet.createRow(r);r++;
							Cell newCell = newRow.createCell(0);
							newCell.setCellValue(evt.time);
							newCell.setCellStyle(sheetsCellStyle);
							newCell = newRow.createCell(1);
							newCell.setCellValue(evt.type);
							newCell.setCellStyle(sheetsCellStyle);
							newCell = newRow.createCell(2);
							newCell.setCellValue(evt.sourceId);
							newCell.setCellStyle(sheetsCellStyle);
							newCell = newRow.createCell(3);
							newCell.setCellValue(evt.sourcePort);
							newCell.setCellStyle(sheetsCellStyle);
							newCell = newRow.createCell(4);
							newCell.setCellValue(evt.packetId);
							newCell.setCellStyle(sheetsCellStyle);
							newCell = newRow.createCell(5);
							newCell.setCellValue(evt.destId);
							newCell.setCellStyle(sheetsCellStyle);
							newCell = newRow.createCell(6);
							newCell.setCellValue(evt.destPort);
							newCell.setCellStyle(sheetsCellStyle);
						}
					}
					
					try {
						FileOutputStream fout = new FileOutputStream(exportFile);
						BufferedOutputStream bos = new BufferedOutputStream(fout);
		                try {
							wb.write(bos);
							bos.close();
			                fout.close();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}      
					} catch (FileNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				if(fileChooser.getFileFilter().getDescription().equals("CSV file")){
					int i = fileToSave.getName().lastIndexOf(".");
					if(i>3){
						if(fileToSave.getName().substring(i+1).equals("csv")){
							exportFile = fileToSave;
						} else {
							exportFile = new File(fileChooser.getCurrentDirectory().getAbsolutePath()+File.separator+fileToSave.getName()+".csv");
						}
					}else {
						exportFile = new File(fileChooser.getCurrentDirectory().getAbsolutePath()+File.separator+fileToSave.getName()+".csv");
					}
					
					try {
						FileWriter fw = new FileWriter(exportFile);
						fw.append("Time");
						fw.append(',');
						fw.append("Type");
						fw.append(',');
						fw.append("Source node ID");
						fw.append(',');
						fw.append("Source port");
						fw.append(',');
						fw.append("Packet ID");
						fw.append(',');
						fw.append("Destination node ID");
						fw.append(',');
						fw.append("Destination port");
						fw.append(System.getProperty("line.separator"));
						
						for (int j = 0; j < eTable.getModel().getRowCount(); j++) {
							if (eTable.getRowSorter().convertRowIndexToView(j) != -1) {
								Event evt = ((EventTableModel) eTable.getModel()).getRow(j);
								fw.append(String.valueOf(evt.time));
								fw.append(',');
								fw.append(String.valueOf(evt.type));
								fw.append(',');
								fw.append(String.valueOf(evt.sourceId));
								fw.append(',');
								fw.append(String.valueOf(evt.sourcePort));
								fw.append(',');
								fw.append(String.valueOf(evt.packetId));
								fw.append(',');
								fw.append(String.valueOf(evt.destId));
								fw.append(',');
								fw.append(String.valueOf(evt.destPort));
								fw.append(System.getProperty("line.separator"));
							}
						}
						fw.flush();
						fw.close();
						
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
				}			
			}
		}
		if(e.getSource() == copy){
			if(copyCbox.getSelectedIndex() == 0){
				
			}
		}
	}
}
