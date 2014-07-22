package models;
import java.util.ArrayList;

public class Packet {
	public String id;
	public String type;
	public String sourceID;
	public String sourcePort;
	public String TTL;
	public String destID;
	public String destPort;
	public ArrayList<Integer> forwardNodeIDs;
	public String size;
	public String startTime;
	public String endTime;
	public boolean isSuccess;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSourceID() {
		return sourceID;
	}

	public void setSourceID(String sourceID) {
		this.sourceID = sourceID;
	}

	public String getSourcePort() {
		return sourcePort;
	}

	public void setSourcePort(String sourcePort) {
		this.sourcePort = sourcePort;
	}

	public String getDestID() {
		return destID;
	}

	public void setDestID(String destID) {
		this.destID = destID;
	}

	public String getDestPort() {
		return destPort;
	}

	public void setDestPort(String destPort) {
		this.destPort = destPort;
	}

	public ArrayList<Integer> getForwardNodeIDs() {
		return forwardNodeIDs;
	}

	public void setForwardNodeIDs(ArrayList<Integer> forwardNodeIDs) {
		this.forwardNodeIDs = forwardNodeIDs;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public boolean isSuccess() {
		return isSuccess;
	}

	public void setSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}

	public ArrayList<WirelessNode> getListNode() {
		return listNode;
	}

	public void setListNode(ArrayList<WirelessNode> listNode) {
		this.listNode = listNode;
	}

	public ArrayList<WirelessNode> getListNodeDest() {
		return listNodeDest;
	}

	public void setListNodeDest(ArrayList<WirelessNode> listNodeDest) {
		this.listNodeDest = listNodeDest;
	}

	// public HashMap<Node,String> listNode;
	public ArrayList<WirelessNode> listNode;
	public ArrayList<WirelessNode> listNodeDest;// use for HELLO packet

	public Packet(String id, String type, String sourceID, String sourcePort,
			String destID, String destPort, String size, String startTime,
			String endTime) {
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
				+ isSuccess;
	}

	public String Infor() {
		return "********** <br>"+"Packet ID:" + id + "<br>Packet Type:" + type
				+ "<br>SourceID: " + sourceID + "<br>Destination ID:" + destID
				+ "<br>Size:" + size + "<br>Start Time:" + startTime
				+ "<br>End Time:" + endTime + "<br> Success:" + isSuccess
				+ "<br></html>";
	}

}
