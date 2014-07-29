package models;

import java.util.ArrayList;
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
	private int id;
	
	/**
	 * list of events that belong to this packet.
	 * all the feature base on this this.
	 */
	private List<Event> event = new ArrayList<Event>();
	
	/**
	 * nodes that packet cross.
	 */
	private List<Node> node;
	
	/**
	 * @param id
	 */
	public Packet(int id) {
		this.id = id;
	}

	/**
	 * @return Event list
	 */
	public List<Event> getEvent() {
		return event;
	}
	
	/**
	 * identify of this packet.
	 * @return packet's id
	 */
	public int getId() {
		return id;
	}

	/**
	 * get initialize type of this packet.
	 * @return packet's initialize type
	 */
	public String getType() {
		return event.get(0).getPacketType();		
	}

	/**
	 * get type of this packet in special time.
	 * packet's type can change during active time.
	 * @param time check time
	 * @return packet's type
	 */
	public String getType(int time) {
		// get nearest event by time, return packet's type in this event
		
		String type = "";
		for (Event e : event) {
			if (e.getTime() > time) return type;
			else type = e.getPacketType();
		}

		return type;
	}
	
	/**
	 * get source node that sent this packet.
	 * @return source node
	 */
	public Node getSourceNode() {
		// get source node of from first event		
		return event.get(0).getNode();
	}

	/**
	 * get source node's layer.
	 * @return source layer
	 */
	public String getSourceLayer() {
		// get source layer form first event
		return event.get(0).getLayer();
	}	

	/**
	 * get destination node.
	 * @return destination node
	 */
	public Node getDestNode() {
		// TODO: get destination node form IP_header of first event
		return event.get(event.size() - 1).getNode();
	}

	/**
	 * get the last node packet passing.
	 * @return node
	 */
	public Node getLastNode() {
		// get last node from last event
		return event.get(event.size() - 1).getNode();
	}
	
	/**
	 * get list of all node that packet passing.
	 * @return
	 */
	public List<Node> getListNodes() {
		if (node == null)
		{
			node = new ArrayList<>();
			for (Event e : event) {
				if (!node.contains(e.getNode())) node.add(e.getNode());
			}
		}
		return node;
	}

	/**
	 * get initialize size of this packet.
	 * packet's size can be change during active time.
	 * @return initialize size of packet.
	 */
	public int getSize() {
		// get initialize size of packet from first event		
		return event.get(0).getPacketSize();
	}

	/**
	 * get time this packet was sent.
	 * @return start time
	 */
	public double getStartTime() {
		// get start time from first event
		return event.get(0).getTime();
	}

	/**
	 * get time this packet stop be transfered.
	 * @return end time
	 */
	public double getEndTime() {
		// get end time from last event
		return event.get(event.size() - 1).getTime();
	}

	/**
	 * a packet is success if:
	 * * pass to destination node
	 * * pass to same layer with source layer
	 * @return this packet is transfer success of not.
	 */
	public boolean isSuccess() {
		// TODO: check the conditional to success
		return getLastNode() == getDestNode() && event.get(0).getLayer() == event.get(event.size() - 1).getLayer();
	}
}
