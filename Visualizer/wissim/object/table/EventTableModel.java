package wissim.object.table;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.swing.table.AbstractTableModel;

import TraceFileParser.wissim.AbstractParser;
import TraceFileParser.wissim.Event;

public class EventTableModel extends AbstractTableModel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5621767435158786203L;

	public static final String TIME = "Time";
	public static final String TYPE = "Type";
	public static final String SOUR_ID = "Source Node";
	public static final String SOUR_PORT = "Source Port";
	public static final String PACK_ID = "Packet ID";
	public static final String TIME_REC = "Time receive";
	public static final String DEST_ID = "Destination Node";
	public static final String DEST_PORT = "Destination Port";
	public static final String ACTION = "Action";
	
	public static String columnNames[] = {
		TIME,TYPE,SOUR_ID,SOUR_PORT,PACK_ID,DEST_ID,DEST_PORT,
		ACTION
	};
	
	private static final Class<?> columnTypes[] = {
		Double.class,String.class,Integer.class,
		Integer.class,Integer.class,Integer.class,Integer.class,
		String.class
	};
	
	private static int expectedColumns = 7;
	private ArrayList<Event> data;
	private Set<Event> modifiedEventData = new HashSet<Event>();
	private int columnsOrder[];
	
	public static void setModelWidth(int w){
		expectedColumns = w;
	}
	
	public static EventTableModel createEventTableModel(AbstractParser mParser){
		return new EventTableModel(getEventData(mParser));
	}
	
	public static ArrayList<Event> getEventData(AbstractParser mParser) {
		if(mParser != null)
			return mParser.getListEvents();
		else
			return new ArrayList<Event>();
	}
	
	public EventTableModel(ArrayList<Event> data){
		this.data = data;
		columnsOrder = new int []{0,1,2,3,4,5,6,7};
	}
	
	public void addEventData(Event data){
		this.data.add(0, data);
		fireTableRowsInserted(0, 0);
	}
	
	public Event removeEventData() {
		Event ret = null;
		if(data.size()>0){
			ret = data.remove(0);
			fireTableRowsDeleted(0, 0);
		}
		
		return ret;
	}
	
	public Event updateEventData(){
		Event ret = null;
		if(data.size() >0){
			ret = data.get(0);
			fireTableRowsUpdated(0, 0);
		}
		return ret;
	}
	
	public Event getRow(int row){
		return data.get(row);
	}
	
	public int getColumn(String name){
		for(int i =0;i<columnsOrder.length;i++){
			if(columnNames[columnsOrder[i]] == name){
				return i;
			}
		}
		
		return -1;
	}
	
	public boolean isModified(Event ed){
		return modifiedEventData.contains(ed);
	}
	
	
	@Override
	public int getColumnCount() {
		// TODO Auto-generated method stub
		return expectedColumns;
	}

	@Override
	public int getRowCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		// TODO Auto-generated method stub
		Event ed = data.get(rowIndex);
		
		switch (columnsOrder[columnIndex]){
		
		case 0:
			return Double.parseDouble(ed.time);
		case 1:
			return ed.type;
		case 2:
			return Integer.valueOf(ed.sourceId);
		case 3:
			return Integer.valueOf(ed.sourcePort);
		case 4:
			return Integer.valueOf(ed.packetId);
		case 5:
			return Integer.valueOf(ed.destId);
		case 6:
			return ed.destPort.length()>0 ? Integer.valueOf(ed.destPort): 0;
		case 7:
			return "View";
		}
		
		return null;
		
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		// TODO Auto-generated method stub
		return columnTypes[columnsOrder[columnIndex]];
	}

	@Override
	public String getColumnName(int column) {
		// TODO Auto-generated method stub
		return columnNames[columnsOrder[column]];
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		// TODO Auto-generated method stub
		if(columnIndex == 7)
			return true;
		else
			return false;
	}
}
