package wissim.ui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import wissim.controller.filters.gui.AutoChoices;
import wissim.controller.filters.gui.IFilterEditor;
import wissim.controller.filters.gui.TableFilterHeader;
import wissim.controller.filters.gui.TableFilterHeader.Position;
import wissim.object.table.ButtonColumn;
import wissim.object.table.EventData;
import wissim.object.table.EventTableModel;
import wissim.object.table.FloatRenderer;
import wissim.object.table.TableColumnAdjuster;

public class EventPanel extends JPanel implements IObserver{

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
	
	public EventPanel(MainViewPanel inp_objMainView) {
		objMainView = inp_objMainView;
		setLayout(new BorderLayout());
		eTable = new JTable(EventTableModel.createEventTableModel());
		eTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		eTable.getTableHeader().setFont( new Font( "Arial" , Font.BOLD, 15 ));
		add(new JScrollPane(eTable),BorderLayout.CENTER);
		setBorder(BorderFactory.createEmptyBorder(8,8,8,8));
		
		header = new TableFilterHeader(eTable,AutoChoices.ENABLED);
		header.setPosition(Position.TOP);
		tca = new TableColumnAdjuster(eTable);
		tca.adjustColumns();
		
		objMainView.register(this);
	}
	
	private void setupHeader(TableFilterHeader header) {
		getEditor(header, EventTableModel.TIME, new FloatRenderer());
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
			eTable.setModel(EventTableModel.createEventTableModel());
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
					JTable table = (JTable)e.getSource();
					int modelRow = Integer.valueOf(e.getActionCommand());
					EventData ed = ((EventTableModel)table.getModel()).getRow(modelRow);
					String info = "Event time: "+ed.time+"\n"+"SourceID: "+ed.sourceId+"\n"+"DestinationID: "+ed.destId+"\n";
					JOptionPane.showMessageDialog(null,info,"Event info",JOptionPane.INFORMATION_MESSAGE);
					
				}
			};
			ButtonColumn buttonColumn = new ButtonColumn(eTable, view, 7);
		}
	}

}
