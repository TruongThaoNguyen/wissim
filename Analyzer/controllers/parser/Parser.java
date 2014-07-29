package controllers.parser;

import java.util.AbstractMap;
import java.util.List;

import models.Event;
import models.Packet;
import models.Node;

/**
 * Interface for Parser.
 * @author Trongnguyen
 * Identify general parse methods.
 */
public interface Parser {		
	
	/**
	 * get list of nodes.
	 * @return HashMap of WirelessNode by id.
	 */
	AbstractMap<Integer, Node> getNodes();
	
	/**
	 * get list of packets.
	 * @return HashMap of Packet by id.
	 */
	AbstractMap<Integer, Packet> getPackets();
	
	/**
	 * get list of events.
	 * @return HashMap of events by time.
	 */
	List<Event> getEvent();
}
