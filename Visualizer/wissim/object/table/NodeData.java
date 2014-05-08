package wissim.object.table;

import java.util.ArrayList;

import TraceFileParser.wissim.AbstractParser;
import TraceFileParser.wissim.FullParser;
import TraceFileParser.wissim.NodeTrace;

public class NodeData {
	public int id;
	public float x;
	public float y;
	private int packetId;
	public String time;
	public String energy;
	public String listIDNeighbors;
	public int groupID;
	
	public static ArrayList<NodeData> getNodeData(AbstractParser mParser){
		ArrayList<NodeData> ndList = new ArrayList<>();
		if(mParser != null){
			for(NodeTrace n : mParser.getListNodes()) {
				NodeData nd = new NodeData();
				nd.id = n.id;
				nd.x = n.x;
				nd.y = n.y;
				
				ndList.add(nd);
			}
		}
		return ndList;
	}
}
