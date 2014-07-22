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
	 * identify number.
	 */
	public int id;
	
	public String packetType;
	
	public int packetSize;
	
	public List<Header> headerList;
}
