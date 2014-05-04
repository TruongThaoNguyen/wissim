package parser;

import java.util.ArrayList;
import java.util.HashMap;

public class Packet {
	public String id;
	public String type;
	public String sourceID;
	public String sourcePort;

	public String destID;
	public String destPort;
	public ArrayList<Integer> forwardNodeIDs;
	public String size;
	public String startTime;
	public String endTime;
	public boolean isSuccess;
	
	// public HashMap<Node,String> listNode;
	public ArrayList<NodeTrace> listNode;
	public ArrayList<NodeTrace> listNodeDest;// use for HELLO packet

	public Packet(String id, String type, String sourceID, String sourcePort,
			String destID, String destPort, String size, String startTime, String endTime) {
		super();
		this.id = id;
		this.type = type;
		this.sourceID = sourceID;
		this.sourcePort = sourcePort;
		this.destID = destID;
		this.destPort = destPort;
		this.size = size;
		this.startTime = startTime;
		this.endTime = endTime;
	}

	@Override
	public String toString() {
		return "Packet id=" + id + ", type=" + type + ", sourceID=" + sourceID
				+ ", sourcePort=" + sourcePort + ", destID=" + destID
				+ ", destPort=" + destPort + ", size=" + size + ", startTime="
				+ startTime + ", endTime=" + endTime + ", isSuccess="
				+ isSuccess ;
	}

	
	

}
