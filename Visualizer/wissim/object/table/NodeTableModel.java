package wissim.object.table;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import TraceFileParser.wissim.AbstractParser;
import TraceFileParser.wissim.NodeTrace;

public class NodeTableModel extends AbstractTableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4972553569639087505L;

	public static final String ID = "Node ID";
	public static final String X = "X";
	public static final String Y = "Y";
	public static final String ACTION = "Action";
	
	
	public static final String columnNames[] = {
		ID,X,Y,ACTION
	};
	
	private static final Class<?> columnTypes[] = {
		Integer.class,Double.class,Double.class,String.class
	};
	
	private static int expectedColumns = 3;
	private ArrayList<NodeTrace> data;
	private int columnsOrder[];
	
	public static void setModelWidth(int w){
		expectedColumns = w;
	}
	
	public static NodeTableModel createNodeTableModel(AbstractParser mParser){
		return new NodeTableModel(getNodeData(mParser));
	}
	
	private static ArrayList<NodeTrace> getNodeData(AbstractParser mParser){
		if(mParser != null)
			return mParser.getListNodes();
		else
			return new ArrayList<NodeTrace>();
	}
	
	public NodeTableModel(ArrayList<NodeTrace> data){
		this.data = data;
		columnsOrder = new int[]{0,1,2,3};
	}
	
	public NodeTrace getRow(int rowIndex){
		return data.get(rowIndex);
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
		NodeTrace nd = data.get(rowIndex);
		
		switch(columnIndex){
		case 0:
			return nd.id;
		case 1:
			return Double.parseDouble(String.valueOf(nd.x));
		case 2:
			return Double.parseDouble(String.valueOf(nd.y));
		case 3:
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
	public String getColumnName(int columnIndex) {
		// TODO Auto-generated method stub
		return columnNames[columnsOrder[columnIndex]];
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if(columnIndex == 4)
			return true;
		else return false;
	}
	
}
