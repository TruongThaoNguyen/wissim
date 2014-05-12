package wissim.object.table;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import TraceFileParser.wissim.AbstractParser;

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
		Integer.class,Integer.class,Integer.class,String.class
	};
	
	private static int expectedColumns = 4;
	private ArrayList<NodeData> data;
	private int columnsOrder[];
	
	public static void setModelWidth(int w){
		expectedColumns = w;
	}
	
	public static NodeTableModel createNodeTableModel(AbstractParser mParser){
		return new NodeTableModel(getNodeData(mParser));
	}
	
	private static ArrayList<NodeData> getNodeData(AbstractParser mParser){
		return NodeData.getNodeData(mParser);
	}
	
	public NodeTableModel(ArrayList<NodeData> data){
		this.data = data;
		columnsOrder = new int[]{0,1,2,3};
	}
	
	public NodeData getRow(int rowIndex){
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
		NodeData nd = data.get(rowIndex);
		
		switch(columnIndex){
		case 0:
			return nd.id;
		case 1:
			return nd.x;
		case 2:
			return nd.y;
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

}
