package wissim.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import wissim.controller.filters.gui.AutoChoices;
import wissim.controller.filters.gui.IFilterEditor;
import wissim.controller.filters.gui.TableFilterHeader;
import wissim.controller.filters.gui.TableFilterHeader.Position;
import wissim.object.table.ButtonColumn;
import wissim.object.table.EventTableModel;
import wissim.object.table.FloatRenderer;
import wissim.object.table.NodeData;
import wissim.object.table.NodeTableModel;
import wissim.object.table.TableColumnAdjuster;

public class NodePanel extends JPanel implements IObserver{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8607421383530713565L;
	
	private MainViewPanel objectMVP;
	private TableFilterHeader header;
	private TableColumnAdjuster tca;
	private JTable nTable;
	private NodeData nd;
	private Container tabPane;
	
	public NodePanel (MainViewPanel inp_objectMVP){
		objectMVP = inp_objectMVP;
		objectMVP.register(this);
		setLayout(new BorderLayout());
		nTable = new JTable(NodeTableModel.createNodeTableModel(objectMVP.mParser));
		nTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		nTable.getTableHeader().setFont( new Font( "Arial" , Font.BOLD, 15 ));
		add(new JScrollPane(nTable),BorderLayout.CENTER);
		setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
		
		header = new TableFilterHeader(nTable,AutoChoices.ENABLED);
		header.setPosition(Position.TOP);
		tca = new TableColumnAdjuster(nTable, 5);
		tca.adjustColumns();
		
		
	}
	
	private void setupHeader(TableFilterHeader header) {
		getEditor(header, NodeTableModel.X, new FloatRenderer());
		getEditor(header, NodeTableModel.Y, new FloatRenderer());
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
		if(subject == objectMVP){
			nTable.setModel(NodeTableModel.createNodeTableModel(objectMVP.mParser));
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
					JTable table = (JTable)e.getSource();
					int modelRow = Integer.valueOf(e.getActionCommand());
					nd = ((NodeTableModel)table.getModel()).getRow(modelRow);
					tabPane = SwingUtilities.getAncestorOfClass(JTabbedPane.class, table);
					
					
					((JTabbedPane)tabPane).setSelectedIndex(4);
				}
			};
			ButtonColumn buttonColumn = new ButtonColumn(nTable, view, 3);
		}
	}
	
	

}
