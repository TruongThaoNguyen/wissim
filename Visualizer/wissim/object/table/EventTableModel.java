package wissim.object.table;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.swing.table.AbstractTableModel;

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
		Float.class,String.class,Integer.class,
		Integer.class,Integer.class,Integer.class,Integer.class,
		String.class
	};
	
	private static int expectedColumns = 8;
	private ArrayList<EventData> data;
	private Set<EventData> modifiedEventData = new HashSet<EventData>();
	private int columnsOrder[];
	
	public static void setModelWidth(int w){
		expectedColumns = w;
	}
	
	public static EventTableModel createEventTableModel(){
		return new EventTableModel(getEventData());
	}
	
	public static ArrayList<EventData> getEventData() {
		return EventData.getEventData();
	}
	
	public EventTableModel(ArrayList<EventData> data){
		this.data = data;
		columnsOrder = new int []{0,1,2,3,4,5,6,7};
	}
	
	public void addEventData(EventData data){
		this.data.add(0, data);
		fireTableRowsInserted(0, 0);
	}
	
	public EventData removeEventData() {
		EventData ret = null;
		if(data.size()>0){
			ret = data.remove(0);
			fireTableRowsDeleted(0, 0);
		}
		
		return ret;
	}
	
	public EventData updateEventData(){
		EventData ret = null;
		if(data.size() >0){
			ret = data.get(0);
			fireTableRowsUpdated(0, 0);
		}
		return ret;
	}
	
	public EventData getRow(int row){
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
	
	public boolean isModified(PacketData pd){
		return modifiedEventData.contains(pd);
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
		EventData ed = data.get(rowIndex);
		
		switch (columnsOrder[columnIndex]){
		
		case 0:
			return ed.time;
		case 1:
			return ed.type;
		case 2:
			return ed.sourceId;
		case 3:
			return ed.sourcePort;
		case 4:
			return ed.packetId;
		case 5:
			return ed.destId;
		case 6:
			return ed.destPort;
		case 7:
			return ed.view;
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
