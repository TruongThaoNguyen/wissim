package wissim.ui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
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

import TraceFileParser.wissim.NodeTrace;
import TraceFileParser.wissim.Packet;

import wissim.controller.filters.gui.AutoChoices;
import wissim.controller.filters.gui.IFilterEditor;
import wissim.controller.filters.gui.TableFilterHeader;
import wissim.controller.filters.gui.TableFilterHeader.Position;
import wissim.object.table.ButtonColumn;
import wissim.object.table.FloatRenderer;
import wissim.object.table.NodeTableModel;
import wissim.object.table.PacketTableModel;
import wissim.object.table.TableColumnAdjuster;

public class PacketPanel extends JPanel implements IObserver,ActionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1831946606113601145L;

	/**
	 * Create the panel.
	 */
	private MainViewPanel objMainView;
	private JTable pTable;
	private TableColumnAdjuster tca;
	private TableFilterHeader header;
	private JPanel searchPanel;
	private JPanel viewPanel;
	private JButton search;
	private JButton view;
	private JTextField idFrom;
	private JTextField sourceFrom;
	private JTextField destFrom;
	private JTextField idTo;
	private JTextField sourceTo;
	private JTextField destTo;
	private JButton export;
	private JTextField typeTxt;
	private JTextField pathContain;
	private JTextField sentFrom;
	private JTextField sentTo;
	private ArrayList<Integer> filtered;
	private JComboBox copyCbox;
	private JLabel resultInfo;
	private JButton copy;
	private EventPanel evtPanel;
	private NodePanel nodePanel;
	private long timeSearch;
	
	
	private static final Insets WEST_INSETS = new Insets(0, 5, 0, 100);
	private static final Insets EAST_INSETS = new Insets(0, 5, 0, 0);
	
	public PacketPanel(MainViewPanel inp_objMainView) {
		objMainView = inp_objMainView;
		setLayout(new BorderLayout(5,5));
		pTable = new JTable(PacketTableModel.createPacketTableModel(objMainView.mParser)){

			/**
			 * 
			 */
			private static final long serialVersionUID = -7178305051122569939L;
			private static final int SPACE_BUFFER = 10;
			
			private String splitToolTip(String tip,int length)
		    {
		        if(tip.length()<=length + SPACE_BUFFER )
		        {
		            return tip;
		        }

		        List<String>  parts = new ArrayList<>();

		        int maxLength = 0;
		        String overLong = tip.substring(0, length + SPACE_BUFFER);
		        int lastSpace = overLong.lastIndexOf(' ');
		        if(lastSpace >= length)
		        {
		            parts.add(tip.substring(0,lastSpace));
		            maxLength = lastSpace;
		        }
		        else
		        {
		            parts.add(tip.substring(0,length));
		            maxLength = length;
		        }

		        while(maxLength < tip.length())
		        {
		            if(maxLength + length < tip.length())
		            {
		                parts.add(tip.substring(maxLength, maxLength + length));
		                maxLength+=maxLength+length;
		            }
		            else
		            {
		                parts.add(tip.substring(maxLength));
		                break;
		            }
		        }

		        StringBuilder  sb = new StringBuilder("<html>");
		        for(int i=0;i<parts.size() - 1;i++)
		        {
		            sb.append(parts.get(i)+"<br>");
		        }
		        sb.append(parts.get(parts.size() - 1));
		        sb.append(("</html>"));
		        return sb.toString();
		    }

			@Override
			public String getToolTipText(MouseEvent e) {
				String tip = null;
                java.awt.Point p = e.getPoint();
                int rowIndex = rowAtPoint(p);
                int colIndex = columnAtPoint(p);

                try {
                    
                    if(colIndex == 7){
                      tip = splitToolTip(getValueAt(rowIndex, colIndex).toString(),50);
                    }
                } catch (RuntimeException e1) {
                    //catch null pointer exception if mouse is over an empty line
                }

                return tip;
			}
			
		};
		pTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		pTable.getTableHeader().setFont( new Font( "Arial" , Font.BOLD, 15 ));
		add(new JScrollPane(pTable),BorderLayout.CENTER);
		setBorder(BorderFactory.createEmptyBorder(8,8,8,8));
		
		header = new TableFilterHeader(pTable,AutoChoices.ENABLED);
		header.setPosition(Position.TOP);
		tca = new TableColumnAdjuster(pTable,20);
		tca.adjustColumns();
		
		objMainView.register(this);
		
		searchPanel = new JPanel(new GridBagLayout());

		JLabel idLabel = new JLabel("-PacketID :  From ");
		searchPanel.add(idLabel,createGbc(0, 0));
		JLabel xLabel = new JLabel("-Source nodeID  :  From ");
		searchPanel.add(xLabel,createGbc(0, 1));
		JLabel yLabel = new JLabel("-Destination nodeID  :  From ");
		searchPanel.add(yLabel,createGbc(0, 2));
		
		idFrom = new JTextField(10);
		searchPanel.add(idFrom,createGbc(1, 0));
		sourceFrom = new JTextField(10);
		searchPanel.add(sourceFrom,createGbc(1, 1));
		destFrom = new JTextField(10);
		searchPanel.add(destFrom,createGbc(1, 2));
		
		searchPanel.add(new JLabel("To"),createGbc(2, 0));
		searchPanel.add(new JLabel("To"),createGbc(2, 1));
		searchPanel.add(new JLabel("To"),createGbc(2, 2));
		
		idTo = new JTextField(10);
		searchPanel.add(idTo,createGbc(3, 0));
		sourceTo = new JTextField(10);
		searchPanel.add(sourceTo,createGbc(3, 1));
		destTo = new JTextField(10);
		searchPanel.add(destTo,createGbc(3, 2));
		
		searchPanel.add(new JLabel("-Type:"),createGbc(5, 0));
		typeTxt = new JTextField(10);
		searchPanel.add(typeTxt,createGbc(6, 0));
		
		searchPanel.add(new JLabel("-Path contain nodeID :"),createGbc(5, 1));
		pathContain = new JTextField(10);
		searchPanel.add(pathContain,createGbc(6, 1));
		
		searchPanel.add(new JLabel("-Sent time : From "),createGbc(5, 2));
		sentFrom = new JTextField(10);
		sentTo = new JTextField(10);
		searchPanel.add(sentFrom,createGbc(6, 2));
		searchPanel.add(new JLabel("to"),createGbc(7, 2));
		searchPanel.add(sentTo,createGbc(8, 2));
		
		searchPanel.setBorder(BorderFactory.createTitledBorder("Search"));
		search = new JButton("Search");
		search.setMargin(new Insets(0, 0, 0, 0));
		
		search.addActionListener(this);
		searchPanel.add(search,createGbc(9, 2));
		
		view = new JButton("View");
		view.addActionListener(this);
		export = new JButton("Export");
		export.addActionListener(this);
		viewPanel = new JPanel(new GridBagLayout());
		
		String[] items = {"Copy to event table as packet id"};
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
		c.gridy = 0;
		viewPanel.add(copyCbox,c);
		c.gridwidth = 1;
		c.weightx = 0.1;
		c.gridx = 6;
		viewPanel.add(copy,c);
		c.gridx = 7;
		viewPanel.add(view, c);
		c.gridx = 8;
		viewPanel.add(export);
		
		
		add(searchPanel,BorderLayout.NORTH);
		add(viewPanel,BorderLayout.SOUTH);
		
	}
	public void setEvtPanel(EventPanel ePanel) {
		evtPanel =ePanel;
	}
	
	public void setNodePanel(NodePanel nPanel){
		nodePanel = nPanel;
	}
	
	public TableFilterHeader getFilterHeader(){
		return this.header;
	}
	
	public void updateTable(int item,ArrayList<Integer> filteredNode){
		filtered = filteredNode;
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
					Object value = entry.getValue(3);
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
			
			header.getFilterEditor(3).directFilter(destFilter);
		}
	}
	
	private GridBagConstraints createGbc(int x, int y) {
	      GridBagConstraints gbc = new GridBagConstraints();
	      gbc.gridx = x;
	      gbc.gridy = y;
	      gbc.gridwidth = 1;
	      gbc.gridheight = 1;

	      gbc.anchor = (x < 5) ? GridBagConstraints.WEST : GridBagConstraints.EAST;
	      gbc.fill = (x == 0) ? GridBagConstraints.BOTH
	            : GridBagConstraints.HORIZONTAL;

	      gbc.insets = (x == 3) ? WEST_INSETS : EAST_INSETS;
//	      gbc.ipadx = (x == 1) ? 70 : 0 ;
	      gbc.weightx = (x == 0||x == 2||x == 4|| x==5||x==7) ? 0.1 : 1.0;
	      gbc.weighty = 1.0;
	      return gbc;
	   }
	
	private void setupHeader(TableFilterHeader header) {
		getEditor(header, PacketTableModel.TIMESENT, new FloatRenderer());
		getEditor(header, PacketTableModel.TIMERECEIVED, new FloatRenderer());
	}
	
	private IFilterEditor getEditor(TableFilterHeader header,String name,TableCellRenderer renderer) {
		TableColumnModel model = header.getTable().getColumnModel();
		for(int i = model.getColumnCount(); --i>=0;) {
			TableColumn tc = model.getColumn(i);
			if(name.equals(tc.getHeaderValue())) {
				if(renderer!=null) {
					tc.setCellRenderer(renderer);
					//System.err.println("----"+tc.getHeaderValue()+"set render");
				}
				return header.getFilterEditor(i);
			}
		}
		return null;
	}

	@Override
	public void update(IObservable subject) {
		// TODO Auto-generated method stub
		if(subject == objMainView){
			pTable.setModel(PacketTableModel.createPacketTableModel(objMainView.mParser));
//			tca.adjustColumns();
			setupHeader(header);
			
			Action view = new AbstractAction() {
				
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {

					JTable table = (JTable)e.getSource();
					int modelRow = Integer.valueOf(e.getActionCommand());
					Packet pd = ((PacketTableModel)table.getModel()).getRow(modelRow);
					
					JTabbedPane tabPane = (JTabbedPane)SwingUtilities.getAncestorOfClass(JTabbedPane.class, table);
					objMainView.ea.reset(objMainView.mGraph);
					objMainView.ea.getDrawer().onDrawPacketPath(pd, objMainView.mGraph);
					objMainView.view.getCamera().setViewPercent(0.4);
//					System.err.println(objMainView.mParser.getListNodes().size());					
					objMainView.vmm.moveGraph(new Point((int)pd.listNode.get(0).x,(int)pd.listNode.get(0).y));
					
					tabPane.setSelectedIndex(4);
					for (NodeTrace node : pd.listNode){
						
			        	node.isEndPath = false;
			        	node.isMediatePath = false;
			        	node.isStartPath = false;
					}
					
				}
			};
			ButtonColumn buttonColumn = new ButtonColumn(pTable, view, 8);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource() == search){
			long startTime = System.nanoTime();
			RowFilter idFilter = new RowFilter(){
				@Override
				public boolean include(Entry entry) {
					Object value = entry.getValue(0);
	                if (value instanceof Integer) {
	                    int id = (Integer) value;
	                    if(!idFrom.getText().isEmpty() && !idTo.getText().isEmpty() ){
	                    	return (id >= Integer.valueOf(idFrom.getText().trim())) && (id <= Integer.valueOf(idTo.getText().trim()));
	                    }
                    	if(idFrom.getText().isEmpty() && !idTo.getText().isEmpty()){
                    		return (id <= Integer.valueOf(idTo.getText().trim()));
                    	}
                    	if(!idFrom.getText().isEmpty() && idTo.getText().isEmpty()){
                    		return (id >= Integer.valueOf(idFrom.getText().trim()));
                    	}
                    	if(idFrom.getText().isEmpty() && idTo.getText().isEmpty()){
                    		return true;
                    	}
	                }

	                return false;
				}
				
			};	
			header.getFilterEditor(0).directFilter(idFilter);
			//sourceID search
			RowFilter sourceFilter = new RowFilter(){
				@Override
				public boolean include(Entry entry) {
					Object value = entry.getValue(2);
	                if (value instanceof Integer) {
	                    int source = (Integer) value;
	                    if(!sourceFrom.getText().isEmpty() && !sourceTo.getText().isEmpty() ){
	                    	return (source >= Integer.valueOf(sourceFrom.getText().trim())) && (source <= Integer.valueOf(sourceTo.getText().trim()));
	                    }
                    	if(sourceFrom.getText().isEmpty() && !sourceTo.getText().isEmpty()){
                    		return (source <= Integer.valueOf(sourceTo.getText().trim()));
                    	}
                    	if(!sourceFrom.getText().isEmpty() && sourceTo.getText().isEmpty()){
                    		return (source >= Integer.valueOf(sourceFrom.getText().trim()));
                    	}
                    	if(sourceFrom.getText().isEmpty() && sourceTo.getText().isEmpty()){
                    		return true;
                    	}
	                }

	                return false;
				}
				
			};
			
			header.getFilterEditor(2).directFilter(sourceFilter);
			//destID search
			RowFilter destFilter = new RowFilter(){
				@Override
				public boolean include(Entry entry) {
					Object value = entry.getValue(3);
	                if (value instanceof Integer) {
	                    int dest = (Integer) value;
	                    if(!destFrom.getText().isEmpty() && !destTo.getText().isEmpty() ){
	                    	return (dest >= Integer.valueOf(destFrom.getText().trim())) && (dest <= Integer.valueOf(destTo.getText().trim()));
	                    }
                    	if(destFrom.getText().isEmpty() && !destTo.getText().isEmpty()){
                    		return (dest <= Integer.valueOf(destTo.getText().trim()));
                    	}
                    	if(!destFrom.getText().isEmpty() && destTo.getText().isEmpty()){
                    		return (dest >= Integer.valueOf(destFrom.getText().trim()));
                    	}
                    	if(destFrom.getText().isEmpty() && destTo.getText().isEmpty()){
                    		return true;
                    	}
	                }

	                return false;
				}
				
			};
			
			header.getFilterEditor(3).directFilter(destFilter);
			//type search
			RowFilter typeFilter = new RowFilter(){
				@Override
				public boolean include(Entry entry) {
					Object value = entry.getValue(1);
	                if (value instanceof String) {
	                    String type = (String)value;
	                    if(!typeTxt.getText().isEmpty() ){
	                    	return type.equals(typeTxt.getText().trim());
	                    }
                    	if(typeTxt.getText().isEmpty()){
                    		return true;
                    	}
	                }
	                return false;
				}
				
			};
			
			header.getFilterEditor(1).directFilter(typeFilter);
			//sentTime search
			RowFilter sentFilter = new RowFilter(){

				@Override
				public boolean include(Entry entry) {
					Object value = entry.getValue(5);
	                if (value instanceof Double) {
	                    Double sentTime = (Double) value;
	                    if(!sentFrom.getText().isEmpty() && !sentTo.getText().isEmpty()){
	                    	return (sentTime >= Double.parseDouble(sentFrom.getText().trim())) && (sentTime <= Double.parseDouble(sentTo.getText().trim()));
	                    }
	                    if(sentFrom.getText().isEmpty() && sentTo.getText().isEmpty()){
	                    	return true;
	                    }
	                    if(!sentFrom.getText().isEmpty() && sentTo.getText().isEmpty()){
	                    	return (sentTime >= Double.parseDouble(sentFrom.getText().trim()));
	                    }
	                    if(sentFrom.getText().isEmpty() && !sentTo.getText().isEmpty()){
	                    	return (sentTime <= Double.parseDouble(sentTo.getText().trim()));
	                    }
	                }

	                return false;
				}
				
			};				
			header.getFilterEditor(5).directFilter(sentFilter);
			//path contain search
			RowFilter pathFilter = new RowFilter(){
				@Override
				public boolean include(Entry entry) {
					Object value = entry.getValue(7);
	                if (value instanceof String) {
	                    String path = (String)value;
	                    
	                    if(!pathContain.getText().isEmpty() ){
//	                    	System.err.println("Text field :"+typeTxt.getText().trim()+",");
	                    	if(!pathContain.getText().trim().contains(",")){
////	                    		System.err.println("Not contain ,");
//	                    		System.err.println(path.contains(","+pathContain.getText().trim()+","));
	                    		return path.contains(","+pathContain.getText().trim()+",");
	                    		
	                    	}else{
	                    		String contain[] = pathContain.getText().trim().split(",");
	                    		for(String s : contain){
	                    			if(!path.contains(","+s+",")){
	                    				return false;
	                    			}
	                    		}
	                    		return true;
	                    	}
	                    }
                    	if(pathContain.getText().isEmpty()){
                    		return true;
                    	}
	                }
	                return false;
				}
				
			};
			header.getFilterEditor(7).directFilter(pathFilter);
			long endTime = System.nanoTime();
			timeSearch = endTime-startTime;
			//print result info
			int result = 0;
			for (int j = 0; j < pTable.getModel().getRowCount(); j++) {
				if (pTable.getRowSorter().convertRowIndexToView(j) != -1) {
					result ++;
				}
			}
			
			resultInfo.setText("Result : "+result+ " packets in "+timeSearch/1000000+" ms");
			
		}
		
		if(e.getSource() == view){
			if(objMainView.mGraph != null){
			for (NodeTrace node : objMainView.mParser.getListNodes()){
	        	node.setEndPath(false);
	        	node.setMediatePath(false);
	        	node.setStartPath(false);
	        }
			objMainView.ea.reset(objMainView.mGraph);
			for (int i=0;i<pTable.getModel().getRowCount();i++){
	        	if(pTable.getRowSorter().convertRowIndexToView(i)!= -1){
	        		objMainView.ea.getWsd().onDrawPacketPath(((PacketTableModel)pTable.getModel()).getRow(i), objMainView.mGraph);
	        	}
	        }
			JTabbedPane tabPane = (JTabbedPane)SwingUtilities.getAncestorOfClass(JTabbedPane.class, pTable);
			tabPane.setSelectedIndex(4);
			for (int i=0;i<pTable.getModel().getRowCount();i++){
	        	if(pTable.getRowSorter().convertRowIndexToView(i)!= -1){
	        		for(NodeTrace node: ((PacketTableModel)pTable.getModel()).getRow(i).listNode){
	        			node.isEndPath = false;
	    	        	node.isMediatePath = false;
	    	        	node.isStartPath = false;
	        		}
	        	}
	        }
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
			int userSelection = fileChooser.showSaveDialog(PacketPanel.this);

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
					fRow.createCell(0).setCellValue("PacketID");
					fRow.createCell(1).setCellValue("Type");
					fRow.createCell(2).setCellValue("Source node ID");
					fRow.createCell(3).setCellValue("Destination node ID");
					fRow.createCell(4).setCellValue("Hops-count");
					fRow.createCell(5).setCellValue("Sent time");
					fRow.createCell(6).setCellValue("Receeved time");
					fRow.createCell(7).setCellValue("Path");
					int r = 1;
					for (int j = 0; j < pTable.getModel().getRowCount(); j++) {
						if (pTable.getRowSorter().convertRowIndexToView(j) != -1) {
							Packet p = ((PacketTableModel) pTable.getModel()).getRow(j);
							Row newRow = newSheet.createRow(r);r++;
							Cell newCell = newRow.createCell(0);
							newCell.setCellValue(p.id);
							newCell.setCellStyle(sheetsCellStyle);
							newCell = newRow.createCell(1);
							newCell.setCellValue(p.type);
							newCell.setCellStyle(sheetsCellStyle);
							newCell = newRow.createCell(2);
							newCell.setCellValue(p.sourceID);
							newCell.setCellStyle(sheetsCellStyle);
							newCell = newRow.createCell(3);
							newCell.setCellValue(p.destID);
							newCell.setCellStyle(sheetsCellStyle);
							newCell = newRow.createCell(4);
							newCell.setCellValue(p.listNode.size());
							newCell.setCellStyle(sheetsCellStyle);
							newCell = newRow.createCell(5);
							newCell.setCellValue(p.startTime);
							newCell.setCellStyle(sheetsCellStyle);
							newCell = newRow.createCell(6);
							newCell.setCellValue(p.endTime);
							newCell.setCellStyle(sheetsCellStyle);
							newCell = newRow.createCell(7);
							if (p.listNode.isEmpty()) {
								newCell.setCellValue("");
							} else {
								StringBuilder path = new StringBuilder();
//								path.append(",");
								for (NodeTrace n : p.listNode) {
									path.append(n.id);
									path.append(",");
								}
								newCell.setCellValue(path.toString().substring(0,path.toString().length()-1));
							}						
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
						fw.append("PacketID");
						fw.append(',');
						fw.append("Type");
						fw.append(',');
						fw.append("Source node ID");
						fw.append(',');
						fw.append("Destination node ID");
						fw.append(',');
						fw.append("Hops count");
						fw.append(',');
						fw.append("Sent time");
						fw.append(',');
						fw.append("Received time");
						fw.append(',');
						fw.append("Path");
						fw.append(System.getProperty("line.separator"));
						
						for (int j = 0; j < pTable.getModel().getRowCount(); j++) {
							if (pTable.getRowSorter().convertRowIndexToView(j) != -1) {
								Packet p = ((PacketTableModel) pTable.getModel()).getRow(j);
								fw.append(String.valueOf(p.id));
								fw.append(',');
								fw.append(String.valueOf(p.type));
								fw.append(',');
								fw.append(String.valueOf(p.sourceID));
								fw.append(',');
								fw.append(String.valueOf(p.destID));
								fw.append(',');
								fw.append(String.valueOf(p.listNode.size()));
								fw.append(',');
								fw.append(String.valueOf(p.startTime));
								fw.append(',');
								fw.append(String.valueOf(p.endTime));
								fw.append(',');
								if (p.listNode.isEmpty()) {
									fw.append("");
								} else {
									StringBuilder path = new StringBuilder();
//									path.append(",");
									for (NodeTrace n : p.listNode) {
										path.append(n.id);
										path.append("->");
									}
									fw.append(path.toString().substring(0, path.toString().length()-2));
								}		
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
			ArrayList<Integer> filtered = new ArrayList<>();
			for (int i=0;i<pTable.getModel().getRowCount();i++){
	        	if(pTable.getRowSorter().convertRowIndexToView(i)!= -1){
	        		Packet p = ((PacketTableModel)pTable.getModel()).getRow(i);
	        		filtered.add(Integer.valueOf(p.id));
	        		}
	        	}
			if(copyCbox.getSelectedIndex() == 0 ){
				evtPanel.updateTable(2, filtered);
				JTabbedPane tabPane = (JTabbedPane)SwingUtilities.getAncestorOfClass(JTabbedPane.class, pTable);
				tabPane.setSelectedIndex(3);
			}
		}
	}
}
