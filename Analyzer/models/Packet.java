package models;

import java.util.List;

/**
 * describe a packet.
 * @author trongnguyen
 *
 */
public class Packet {
	/**
	 * packet's id.
	 */
	public String id;
	
	/**
	 * list of events that belong to this packet.
	 * all the feature base on this this.
	 */
	private List<Event> event;
	
	/**
	 * identify of this packet.
	 * @return packet's id
	 */
	public String getId() {
		return id;
	}

	/**
	 * get initialize type of this packet.
	 * @return packet's initialize type
	 */
	public String getType() {
		// TODO: get initialize type from first event 
		return "";
	}

	/**
	 * get type of this packet in special time.
	 * packet's type can change during active time.
	 * @param time check time
	 * @return packet's type
	 */
	public String getType(int time) {
		// TODO: get nearest event by time, return packet's type in this event
		return null;
	}
	
	/**
	 * get source node that sent this packet.
	 * @return source node
	 */
	public Node getSourceID() {
		// TODO: get source node of from first event
		return null;
	}

	/**
	 * get source node's layer
	 * @return source layer
	 */
	public String getSourceLayer() {
		// TODO: get source layer form first event
		return null;
	}	

	/**
	 * get destination node
	 * @return destination node
	 */
	public Node getDestID() {
		// TODO: get destination node form CMN_header of first event
		return null;
	}

	/**
	 * get the last node packet passing
	 * @return node
	 */
	public Node getLastNode() {
		// TODO: get last node from last event
		return null;
	}
	
	/**
	 * get list of all node that packet passing
	 * @return
	 */
	public List<Node> getTranferNodes() {
		// TODO: get list of forward nodes form event list
		return null;
	}

	/**
	 * get initialize size of this packet.
	 * packet's size can be change during active time.
	 * @return initialize size of packet.
	 */
	public String getSize() {
		// TODO: get initialize size of packet from first event 
		return null;
	}

	/**
	 * get time this packet was sent.
	 * @return start time
	 */
	public String getStartTime() {
		// TODO: get start time from first event
		return null;
	}

	/**
	 * get time this packet stop be transfered.
	 * @return end time
	 */
	public String getEndTime() {
		// TODO: get end time from last event
		return null;
	}

	/**
	 * a packet is success if:
	 * * pass to destination node
	 * * pass to same layer with source layer
	 * @return this packet is transfer success of not.
	 */
	public boolean isSuccess() {
		// TODO: check the conditional to success
		return true;
	}
}
