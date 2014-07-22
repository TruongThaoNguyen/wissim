package controllers.parser;

import java.util.HashMap;
import java.util.List;

import models.Event;
import models.Packet;
import models.Node;

public interface Parser {		
	
	/**
	 * get list of nodes.
	 * @return HashMap of WirelessNode by id.
	 */
	public HashMap<Integer, Node> getNodes();
	
	/**
	 * get list of packets.
	 * @return HashMap of Packet by id.
	 */
	public HashMap<Integer, Packet> getPackets();
	
	/**
	 * get list of events.
	 * @return HashMap of events by time.
	 */
	public List<Event> getEvent();
}
