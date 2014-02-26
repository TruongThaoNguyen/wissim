package model.event;

import model.networkComponent.WirelessNode;


/**
 * Represents an event of a node.
 * There are 2 node-event type: node is turned off (OFF) and turn on (ON).
 * @author leecom
 *
 */
public class NodeEvent extends Event {
	/**
	 * 
	 */
	public static final int OFF = 1;
	
	/**
	 * 
	 */
	public static final int ON = 0;	
	
	// the node which this event belongs to
	private WirelessNode node;
	
	public NodeEvent(int type, int raisedTime, WirelessNode node) {
		super(type, raisedTime);
		
		this.node = node;
		
		// asks the node to add me
		this.node.addEvent(this);
	}
}
