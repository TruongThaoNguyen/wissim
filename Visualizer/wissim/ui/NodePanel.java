package wissim.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
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
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.SwingUtilities;
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

import TraceFileParser.wissim.NodeTrace;

import wissim.controller.filters.gui.AutoChoices;
import wissim.controller.filters.gui.IFilterEditor;
import wissim.controller.filters.gui.TableFilterHeader;
import wissim.controller.filters.gui.TableFilterHeader.Position;
import wissim.object.table.ButtonColumn;
import wissim.object.table.FloatRenderer;
import wissim.object.table.NodeTableModel;
import wissim.object.table.PacketTableModel;
import wissim.object.table.TableColumnAdjuster;

public class NodePanel extends JPanel implements IObserver, ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8607421383530713565L;

	private MainViewPanel objectMVP;
	private TableFilterHeader header;
	private TableColumnAdjuster tca;
	private JTable nTable;
	private Container tabPane;
	private JPanel searchPanel;
	private JPanel viewPanel;
	private JButton search;
	private JButton view;
	private JTextField idFrom;
	private JTextField xFrom;
	private JTextField yFrom;
	private JTextField idTo;
	private JTextField xTo;
	private JTextField yTo;
	private JButton export;
	private JComboBox copyCbox;
	private JButton copy;
	private ArrayList<NodeTrace> filteredNode = new ArrayList<>();
	private PacketPanel pPanel;
	private EventPanel evtPanel;
	private JLabel resultInfo;
	private long timeSearch;

	private static final Insets WEST_INSETS = new Insets(0, 5, 0, 450);
	private static final Insets EAST_INSETS = new Insets(0, 5, 0, 0);

	public NodePanel(MainViewPanel inp_objectMVP) {
		objectMVP = inp_objectMVP;
		objectMVP.register(this);
		setLayout(new BorderLayout(5, 5));
		nTable = new JTable(
				NodeTableModel.createNodeTableModel(objectMVP.mParser));
		nTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		nTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 15));
		add(new JScrollPane(nTable), BorderLayout.CENTER);
		setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

		header = new TableFilterHeader(nTable, AutoChoices.ENABLED);
		header.setPosition(Position.TOP);
		tca = new TableColumnAdjuster(nTable, 5);
		tca.adjustColumns();

		searchPanel = new JPanel(new GridBagLayout());

		JLabel idLabel = new JLabel("-NodeID :          From ");
		searchPanel.add(idLabel, createGbc(0, 0));
		JLabel xLabel = new JLabel("-XCoordinate :  From ");
		searchPanel.add(xLabel, createGbc(0, 1));
		JLabel yLabel = new JLabel("-YCoordinate :  From ");
		searchPanel.add(yLabel, createGbc(0, 2));

		idFrom = new JTextField(10);
		searchPanel.add(idFrom, createGbc(1, 0));
		xFrom = new JTextField(10);
		searchPanel.add(xFrom, createGbc(1, 1));
		yFrom = new JTextField(10);
		searchPanel.add(yFrom, createGbc(1, 2));

		searchPanel.add(new JLabel("To"), createGbc(2, 0));
		searchPanel.add(new JLabel("To"), createGbc(2, 1));
		searchPanel.add(new JLabel("To"), createGbc(2, 2));

		idTo = new JTextField(10);
		searchPanel.add(idTo, createGbc(3, 0));
		xTo = new JTextField(10);
		searchPanel.add(xTo, createGbc(3, 1));
		yTo = new JTextField(10);
		searchPanel.add(yTo, createGbc(3, 2));

		searchPanel.setBorder(BorderFactory.createTitledBorder("Search"));
		search = new JButton("Search");
		search.setMargin(new Insets(0, 0, 0, 0));
		search.addActionListener(this);
		searchPanel.add(search, createGbc(4, 2));

		view = new JButton("View");
		view.addActionListener(this);
		export = new JButton("Export");
		export.addActionListener(this);
		viewPanel = new JPanel(new GridBagLayout());
		
		String[] items = {"Copy to packet table as source node","Copy to packet table as destination node",
				"Copy to event table as source node","Copy to event table as destination node"};
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
		viewPanel.add(resultInfo);
		c.anchor = GridBagConstraints.EAST;
		c.gridx = 3;
		viewPanel.add(copyCbox,c);
		c.gridwidth = 1;
		c.weightx = 0.1;
		c.gridx = 6;
		viewPanel.add(copy,c);
		c.gridx = 7;
		viewPanel.add(view, c);
		c.gridx = 8;
		viewPanel.add(export);

		add(searchPanel, BorderLayout.NORTH);
		add(viewPanel, BorderLayout.SOUTH);
	}
	
	public void setPacketPanel(PacketPanel pPanel){
		this.pPanel = pPanel;
	}
	
	public TableFilterHeader getFilterHeader(){
		return this.header;
	}
	
	public void setEvtPanel(EventPanel ePanel){
		this.evtPanel = ePanel;
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

		gbc.insets = (x == 4) ? WEST_INSETS : EAST_INSETS;
		// gbc.ipadx = (x == 1) ? 70 : 0 ;
		gbc.weightx = (x == 0 || x == 2 || x == 4) ? 0.1 : 1.0;
		gbc.weighty = 1.0;
		return gbc;
	}

	private void setupHeader(TableFilterHeader header) {
		getEditor(header, NodeTableModel.X, new FloatRenderer());
		getEditor(header, NodeTableModel.Y, new FloatRenderer());
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
		if (subject == objectMVP) {
			nTable.setModel(NodeTableModel
					.createNodeTableModel(objectMVP.mParser));
			tca.adjustColumns();
			setupHeader(header);

			Action view = new AbstractAction() {

				/**
				 * 
				 */
				private static final long serialVersionUID = 7277732012190402116L;

				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					JTable table = (JTable) e.getSource();
					int modelRow = Integer.valueOf(e.getActionCommand());
					tabPane = SwingUtilities.getAncestorOfClass(
							JTabbedPane.class, table);

					((JTabbedPane) tabPane).setSelectedIndex(4);
				}
			};
			// ButtonColumn buttonColumn = new ButtonColumn(nTable, view, 3);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource() == search) {
			long startTime = System.nanoTime();
			RowFilter idFilter = new RowFilter() {
				@Override
				public boolean include(Entry entry) {
					Object value = entry.getValue(0);
					if (value instanceof Integer) {
						int id = (Integer) value;
						if (!idFrom.getText().isEmpty()
								&& !idTo.getText().isEmpty()) {
							return (id >= Integer.valueOf(idFrom.getText()
									.trim()))
									&& (id <= Integer.valueOf(idTo.getText()
											.trim()));
						}
						if (idFrom.getText().isEmpty()
								&& !idTo.getText().isEmpty()) {
							return (id <= Integer
									.valueOf(idTo.getText().trim()));
						}
						if (!idFrom.getText().isEmpty()
								&& idTo.getText().isEmpty()) {
							return (id >= Integer.valueOf(idFrom.getText()
									.trim()));
						}
						if (idFrom.getText().isEmpty()
								&& idTo.getText().isEmpty()) {
							return true;
						}
					}

					return false;
				}

			};

			header.getFilterEditor(0).directFilter(idFilter);

			RowFilter xFilter = new RowFilter() {

				@Override
				public boolean include(Entry entry) {
					Object value = entry.getValue(1);
					if (value instanceof Double) {
						Double x = (Double) value;
						if (!xFrom.getText().isEmpty()
								&& !xTo.getText().isEmpty()) {
							return (x >= Double.parseDouble(xFrom.getText()
									.trim()))
									&& (x <= Double.parseDouble(xTo.getText()
											.trim()));
						}
						if (xFrom.getText().isEmpty()
								&& xTo.getText().isEmpty()) {
							return true;
						}
						if (!xFrom.getText().isEmpty()
								&& xTo.getText().isEmpty()) {
							return (x >= Double.parseDouble(xFrom.getText()
									.trim()));
						}
						if (xFrom.getText().isEmpty()
								&& !xTo.getText().isEmpty()) {
							return (x <= Double.parseDouble(xTo.getText()
									.trim()));
						}
					}

					return false;
				}

			};
			header.getFilterEditor(1).directFilter(xFilter);

			RowFilter yFilter = new RowFilter() {

				@Override
				public boolean include(Entry entry) {
					Object value = entry.getValue(2);
					if (value instanceof Double) {
						Double y = (Double) value;
						if (!yFrom.getText().isEmpty()
								&& !yTo.getText().isEmpty()) {
							return (y >= Double.parseDouble(yFrom.getText()
									.trim()))
									&& (y <= Double.parseDouble(yTo.getText()
											.trim()));
						}
						if (yFrom.getText().isEmpty()
								&& yTo.getText().isEmpty()) {
							return true;
						}
						if (!yFrom.getText().isEmpty()
								&& yTo.getText().isEmpty()) {
							return (y >= Double.parseDouble(yFrom.getText()
									.trim()));
						}
						if (yFrom.getText().isEmpty()
								&& !yTo.getText().isEmpty()) {
							return (y <= Double.parseDouble(yTo.getText()
									.trim()));
						}
					}

					return false;
				}

			};
			header.getFilterEditor(2).directFilter(yFilter);
			long endTime = System.nanoTime();
			timeSearch = endTime-startTime;
			//print result info
			int result = 0;
			for (int j = 0; j < nTable.getModel().getRowCount(); j++) {
				if (nTable.getRowSorter().convertRowIndexToView(j) != -1) {
					result ++;
				}
			}
			
			resultInfo.setText("Result : "+result+ " nodes in "+timeSearch/1000000+" ms");

		}
		if (e.getSource() == view) {
			if (objectMVP.mGraph != null) {
				objectMVP.ea.reset(objectMVP.mGraph);
				objectMVP.view.getCamera().setViewPercent(0.5);
				for (int i = 0; i < nTable.getModel().getRowCount(); i++) {
					if (nTable.getRowSorter().convertRowIndexToView(i) != -1) {
						NodeTrace nt = ((NodeTableModel) nTable.getModel())
								.getRow(i);
						// if(i==0){
						// objectMVP.vmm.moveGraph(new
						// Point((int)nt.x,(int)nt.y));
						// }
						objectMVP.mGraph.getNode(String.valueOf(nt.getId()))
								.addAttribute("ui.class", "search");
						// JPopupMenu nodePop= new JPopupMenu();
						// JMenuItem je = new JMenuItem("Node ID : "+
						// nt.getId());
						// nodePop.add(je);
						// nodePop.show(objectMVP.GraphViewPanel,
						// Float.floatToIntBits(nt.getX()),
						// Float.floatToIntBits(nt.getY()));
						// objectMVP.ea.animationPacket(((PacketTableModel)nTable.getModel()).getRow(i),
						// objectMVP.mGraph);
					}
				}
				JTabbedPane tabPane = (JTabbedPane) SwingUtilities
						.getAncestorOfClass(JTabbedPane.class, nTable);
				tabPane.setSelectedIndex(4);
			}
		}

		if (e.getSource() == export) {
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
			int userSelection = fileChooser.showSaveDialog(NodePanel.this);

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
					fRow.createCell(0).setCellValue("NodeID");
					fRow.createCell(1).setCellValue("X");
					fRow.createCell(2).setCellValue("Y");
					int r = 1;
					for (int j = 0; j < nTable.getModel().getRowCount(); j++) {
						if (nTable.getRowSorter().convertRowIndexToView(j) != -1) {
							NodeTrace nt = ((NodeTableModel) nTable.getModel()).getRow(j);
							Row newRow = newSheet.createRow(r);r++;
							Cell newCell = newRow.createCell(0);
							newCell.setCellValue(nt.id);
							newCell.setCellStyle(sheetsCellStyle);
							newCell = newRow.createCell(1);
							newCell.setCellValue(nt.x);
							newCell.setCellStyle(sheetsCellStyle);
							newCell = newRow.createCell(2);
							newCell.setCellValue(nt.y);
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
						fw.append("NodeID");
						fw.append(',');
						fw.append("X");
						fw.append(',');
						fw.append("Y");
						fw.append(System.getProperty("line.separator"));
						
						for (int j = 0; j < nTable.getModel().getRowCount(); j++) {
							if (nTable.getRowSorter().convertRowIndexToView(j) != -1) {
								NodeTrace nt = ((NodeTableModel) nTable.getModel()).getRow(j);
								fw.append(String.valueOf(nt.id));
								fw.append(',');
								fw.append(String.valueOf(nt.x));
								fw.append(',');
								fw.append(String.valueOf(nt.y));
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
			filteredNode.clear();
			for (int i = 0; i < nTable.getModel().getRowCount(); i++) {
				if (nTable.getRowSorter().convertRowIndexToView(i) != -1) {
					NodeTrace nt = ((NodeTableModel) nTable.getModel())
							.getRow(i);
					filteredNode.add(nt);
				}
			}
			ArrayList<Integer> nodeIdFiltered = new ArrayList<>();
			for(NodeTrace nt : filteredNode){
				nodeIdFiltered.add(Integer.valueOf(nt.id));
			}
			if(copyCbox.getSelectedIndex() == 0){
//				System.err.println(copyCbox.getSelectedItem().toString());
				pPanel.updateTable(0, nodeIdFiltered);
				tabPane = SwingUtilities.getAncestorOfClass(
						JTabbedPane.class, nTable);

				((JTabbedPane) tabPane).setSelectedIndex(2);
			}
			if(copyCbox.getSelectedIndex() == 1) {
				pPanel.updateTable(1, nodeIdFiltered);
				tabPane = SwingUtilities.getAncestorOfClass(
						JTabbedPane.class, nTable);

				((JTabbedPane) tabPane).setSelectedIndex(2);
			}
			if(copyCbox.getSelectedIndex() == 2){
				evtPanel.updateTable(0, nodeIdFiltered);
				tabPane = SwingUtilities.getAncestorOfClass(
						JTabbedPane.class, nTable);

				((JTabbedPane) tabPane).setSelectedIndex(3);
			}
			if(copyCbox.getSelectedIndex() == 3){
				evtPanel.updateTable(1, nodeIdFiltered);
				tabPane = SwingUtilities.getAncestorOfClass(
						JTabbedPane.class, nTable);

				((JTabbedPane) tabPane).setSelectedIndex(3);
			}
		}
	}

}
