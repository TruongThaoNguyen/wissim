package models;

public class Event {

	public static final int TYPE_SEND = 0x1;
	public static final int TYPE_RECEIVE = 0x10;
	public static final int TYPE_FORWARD = 0x100;
	public static final int TYPE_SLEEP = 0x1000;
	public static final int TYPE_WAKE = 0x10000;
	
	public String type = "";
	public String time = "";
	public String timeReceive = "";
	public String sourcePort = "";
	public String sourceId = "";
	public String remainEnergy = "";
	public String packetId = "";
	public String packetType = "";
	public String Message = "";
	public String destPort = "";
	public String destId = "";
	public String bufferLength = "";
	public String nodeId = "";
	public String reason = "";

	// move
	public String currX = "";
	public String currY = "";
	public String currZ = "";
	public String destX = "";
	public String destY = "";
	public String destZ = "";
	public String speed = "";
	
	public Event() {
		super();
	}

	public Event(String type, String time, String timeReceive,
			String sourcePort, String sourceId, String remainEnergy,
			String packetId, String destPort, String destId,
			String bufferLength, String nodeId, String reason) {
		super();
		this.type = type;
		this.time = time;
		this.timeReceive = timeReceive;
		this.sourcePort = sourcePort;
		this.sourceId = sourceId;
		this.remainEnergy = remainEnergy;
		this.packetId = packetId;
		this.destPort = destPort;
		this.destId = destId;
		this.bufferLength = bufferLength;
		this.nodeId = nodeId;
		this.reason = reason;
	}
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getTimeReceive() {
		return timeReceive;
	}

	public void setTimeReceive(String timeReceive) {
		this.timeReceive = timeReceive;
	}

	public String getSourcePort() {
		return sourcePort;
	}

	public void setSourcePort(String sourcePort) {
		this.sourcePort = sourcePort;
	}

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public String getRemainEnergy() {
		return remainEnergy;
	}

	public void setRemainEnergy(String remainEnergy) {
		this.remainEnergy = remainEnergy;
	}

	public String getPacketId() {
		return packetId;
	}

	public void setPacketId(String packetId) {
		this.packetId = packetId;
	}

	public String getPacketType() {
		return packetType;
	}

	public void setPacketType(String packetType) {
		this.packetType = packetType;
	}

	public String getMessage() {
		return Message;
	}

	public void setMessage(String message) {
		Message = message;
	}

	public String getNodeId() {
		return nodeId;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getSpeed() {
		return speed;
	}

	public void setSpeed(String speed) {
		this.speed = speed;
	}

	public String getDestId() {
		return destId;
	}

	@Override
	public String toString() {
		return "Event [type=" + type + ", time=" + time + ", timeReceive="
				+ timeReceive + ", sourcePort=" + sourcePort + ", sourceId="
				+ sourceId + ", remainEnergy=" + remainEnergy + ", packetId="
				+ packetId + ", destPort=" + destPort + ", destId=" + destId
				+ ", bufferLength=" + bufferLength + ", nodeId=" + nodeId
				+ ", reason=" + reason + "]";
	}
	public String infor(){
		return "<html>Event Type: "+getType()  + "<br>Time: "+ getTime() + "<br>Source ID:"+getSourceId()+"<br>Destination ID:"+getDestId() + "<br>Remaining Energy:"+getRemainEnergy()+"<br>";
	}
	public String summaryInfor(){
		return "Event Type "+ getType() + " Packet ID " + getPacketId() + "\n";
	}

}
