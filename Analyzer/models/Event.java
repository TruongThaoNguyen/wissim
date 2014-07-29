package models;

import java.util.List;

/**
 * describe a trace event.
 * @author Trongnguyen
 * event is basic element for other models
 */
public class Event {

	/**
	 * Type of event.
	 * @author trongnguyen
	 *
	 */
	public static enum Type { SEND, RECEIVE, FORWARD, DROP, ENERGY, UNKNOW }	
	
	/**
	 * Type of event.
	 */
	private Type type;	
	
	/**
	 * Happen time.
	 */
	private double time;
		
	/**
	 * node hold this event.
	 */
	private Node node;
	
	private Packet packet;
	
	/**
	 * hold layer of node.
	 */
	private String layer;

	/**
	 * message.
	 */
	private String message;

	/**
	 * type of packet.
	 */
	private String packetType;
	
	/**
	 * packet size.
	 */
	private int packetSize;
	
	/**
	 * list of extend header.
	 */
	private List<Header> headerList;

	/**
	 * @return the type
	 */
	public Type getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(Type type) {
		this.type = type;
	}

	/**
	 * @return the time
	 */
	public double getTime() {
		return time;
	}

	/**
	 * @param time the time to set
	 */
	public void setTime(double time) {
		this.time = time;
	}

	/**
	 * @return the node
	 */
	public Node getNode() {
		return node;
	}

	/**
	 * @param node the node to set
	 */
	public void setNode(Node node) {
		this.node = node;
		if (node == null)
		{
			System.out.println("here");
		}
		else {
			List<Event> e = node.getEvent();
			if (!e.contains(this)) e.add(this);
		}
	}

	/**
	 * @return the layer
	 */
	public String getLayer() {
		return layer;
	}

	/**
	 * @param layer the layer to set
	 */
	public void setLayer(String layer) {
		this.layer = layer;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return the packetType
	 */
	public String getPacketType() {
		return packetType;
	}

	/**
	 * @param packetType the packetType to set
	 */
	public void setPacketType(String packetType) {
		this.packetType = packetType;
	}

	/**
	 * @return the packetSize
	 */
	public int getPacketSize() {
		return packetSize;
	}

	/**
	 * @param packetSize the packetSize to set
	 */
	public void setPacketSize(int packetSize) {
		this.packetSize = packetSize;
	}

	/**
	 * @return packet
	 */
	public Packet getPacket() {
		return packet;
	}
	
	/**
	 * @param packet the packet this event belong to
	 */
	public void setPacket(Packet packet) {
		this.packet = packet;
		List<Event> e = packet.getEvent();
		if (!e.contains(this)) e.add(this);
	}
}
