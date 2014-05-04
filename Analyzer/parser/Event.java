package parser;

import java.util.ArrayList;
import java.util.HashMap;

public class Event {
	/**
	 * For Event
	 */
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

	@Override
	public String toString() {
		return "Event [type=" + type + ", time=" + time + ", timeReceive="
				+ timeReceive + ", sourcePort=" + sourcePort + ", sourceId="
				+ sourceId + ", remainEnergy=" + remainEnergy + ", packetId="
				+ packetId + ", destPort=" + destPort + ", destId=" + destId
				+ ", bufferLength=" + bufferLength + ", nodeId=" + nodeId
				+ ", reason=" + reason + "]";
	}

}
