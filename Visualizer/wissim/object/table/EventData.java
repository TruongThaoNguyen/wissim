package wissim.object.table;

import java.util.ArrayList;

import TraceFileParser.wissim.Event;
import TraceFileParser.wissim.FullParser;

public class EventData {
	
	public String type;
	public Float time;
	public String timeReceive;
	public Integer sourcePort;
	public Integer sourceId;
	public String remainEnergy;
	public Integer packetId;
	public String packetType;
	public String message;
	public Integer destPort;
	public Integer destId;
	public String bufferLength;
	public Integer nodeId;
	public String reason;
	public String view;
	
	public static ArrayList<EventData> getEventData() {
		ArrayList<EventData> edList = new ArrayList<>();
		for(Event e : FullParser.listEvents){
			EventData ed = new EventData();
			ed.type = e.type;
			ed.time = Float.parseFloat(e.time);
			ed.timeReceive = e.timeReceive;
			ed.sourceId = Integer.valueOf(e.sourceId);
			ed.sourcePort = Integer.valueOf(e.sourcePort);
			ed.remainEnergy = e.remainEnergy;
			ed.packetId = Integer.valueOf(e.packetId);
			ed.packetType = e.packetType;
			ed.message = e.Message;
			ed.destId = Integer.valueOf(e.destId);
			ed.destPort = Integer.valueOf(e.destPort);
			ed.bufferLength = e.bufferLength;
			ed.reason = e.reason;
			ed.view = e.time;
			
			edList.add(ed);
		}
		return edList;
	}
}
