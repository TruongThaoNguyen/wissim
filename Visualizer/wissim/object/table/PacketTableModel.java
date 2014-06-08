package wissim.object.table;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import TraceFileParser.wissim.AbstractParser;
import TraceFileParser.wissim.NodeTrace;
import TraceFileParser.wissim.Packet;


public class PacketTableModel extends AbstractTableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6184684022424957338L;
	
	public static final String ID = "Packet ID";
	public static final String TYPE = "Type";
	public static final String SOURCE = "Source Node";
	public static final String DESTINATION = "Destination node";
	public static final String HOPCOUNT = "Hops-count";
	public static final String TIMESENT = "Sent time";
	public static final String TIMERECEIVED = "Received time";
//	public static final String DELAY = "Delay(ms)";
	public static final String PATH = "Path";
//	public static final String DROP = "Drop";
//	public static final String TTL = "TTL";
	public static final String ACTION = "Action";
	
	public static String columnNames[] = {
		ID,TYPE,SOURCE,DESTINATION,HOPCOUNT,TIMESENT,TIMERECEIVED,PATH,ACTION

	};
	
	private static final Class<?> columnTypes[] = {
		Integer.class,String.class,Integer.class,Integer.class,String.class,
		Double.class,Double.class,String.class,Integer.class
	};
	
	private static final int BASIC_MODEL_COLUMNS = 9;
	private static final int FULL_MODEL_COLUMNS = 12;
	
	private static int expectedColumns = BASIC_MODEL_COLUMNS;
	private ArrayList<Packet> data ;
	private Set<Packet> modifiedPacketData = new HashSet<Packet>();
	private int columnsOrder[];
	
	public static void setFullModel(){
		expectedColumns = FULL_MODEL_COLUMNS;
	}
	
	public static void setModelWidth(int w){
		expectedColumns = w;
	}
	
	public static PacketTableModel createPacketTableModel(AbstractParser mParser){
		return new PacketTableModel(getPacketData(mParser));
	}
	
	private static ArrayList<Packet> getPacketData(AbstractParser mParser){
		if(mParser != null)
			return mParser.getListPacket();
		else return new ArrayList<Packet>();
	}
	
	public PacketTableModel(ArrayList<Packet> data){
		this.data = data;
		columnsOrder = new int[]{0,1,2,3,4,5,6,7,8};
	}
	
	public void addPacketData(Packet data){
		this.data.add(0,data);
		fireTableRowsInserted(0, 0);
	}
	
	public Packet removePacketData(){
		Packet ret = null;
		if(data.size() > 0) {
			ret = data.remove(0);
			fireTableRowsDeleted(0, 0);
		}
		
		return ret;
	}
	
	public Packet updatePacketData(){
		Packet ret = null;
		if(data.size() > 0){
			ret = data.get(0);
			fireTableRowsUpdated(0, 0);
		}
		
		return ret;
	}
	
	public void changeModel(JTable table){
		
	}
	
	public Packet getRow(int row){
		return data.get(row);
	}
	
	public void updateData(int rows){
//		data = getPacketData(rows);
//		fireTableDataChanged();
	}
	
	public int getColumn(String name){
		for(int i =0;i<columnsOrder.length;i++){
			if(columnNames[columnsOrder[i]] == name){
				return i;
			}
		}
		
		return -1;
	}
	
	public boolean isModified(Packet pd){
		return modifiedPacketData.contains(pd);
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
		Packet pd = data.get(rowIndex);
		
		switch (columnsOrder[columnIndex]){
		
		case 0:
			return Integer.valueOf(pd.id);
		case 1:
			return pd.type;
		case 2:
			return Integer.valueOf(pd.sourceID);
		case 3:
			return Integer.valueOf(pd.destID);
		case 4:
				return String.valueOf(pd.listNode.size());
		case 5:
			return Double.parseDouble(pd.startTime);
		case 6:
			return Double.parseDouble(pd.endTime);
		case 7:
			if (pd.listNode.isEmpty()) {
				return "";
			} else {
				StringBuilder path = new StringBuilder();
				path.append(",");
				for (NodeTrace n : pd.listNode) {
					path.append(n.id);
					path.append(",");
				}
				return path.toString();
			}
		case 8:
			return "";
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
	public boolean isCellEditable(int row, int column) {
		// TODO Auto-generated method stub
		if(column == 8||column == 7)
			return true;
		else
			return false;
	}
	
}
