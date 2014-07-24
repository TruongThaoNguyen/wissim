package models;

import java.util.List;

public class Event {

	/**
	 * Type of event.
	 * @author trongnguyen
	 *
	 */
	public static enum Type { SEND, RECEIVE, FORWARD, SLEEP, WAKE }
	
	/**
	 * identify number.
	 */
	public int id;
	
	/**
	 * Type of event.
	 */
	public Type type;	
	
	/**
	 * Happen time.
	 */
	public double time;
		
	/**
	 * node hold this event.
	 */
	public Node node;
	
	/**
	 * hold layer of node.
	 */
	public String layer;

	/**
	 * message.
	 */
	public String message;

	/**
	 * type of packet.
	 */
	public String packetType;
	
	/**
	 * packet size.
	 */
	public int packetSize;
	
	/**
	 * list of extend header.
	 */
	public List<Header> headerList;
}
