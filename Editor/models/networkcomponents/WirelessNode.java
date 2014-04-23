package models.networkcomponents;

import java.util.ArrayList;
import java.util.List;

import models.networkcomponents.events.NodeEvent;

public abstract class WirelessNode extends Node {	
	
	// radio range
	private int range;
	
	// list of neighbors
	private List<WirelessNode> neighborList;
	
	// list of events
	private List<NodeEvent> eventList;
	
	/**
	 * Create a wireless node in a network with position x, y and radius range.
	 * The id of node will be generated by the network to guarantee its unique.
	 * @param network
	 */
	public WirelessNode(WirelessNetwork network)
	{
		super(network);
		neighborList = new ArrayList<WirelessNode>();
		eventList = new ArrayList<NodeEvent>();
	}
	
	/**
	 * Create a wireless node in a network with position x, y and radius range.
	 * The id of node will be generated by the network to guarantee its unique.
	 * @param network
	 * @param x
	 * @param y
	 * @param range
	 * @throws NullPointerException If network is null
	 */
	public WirelessNode(WirelessNetwork network, int x, int y, int range) throws NullPointerException {
		super(network);
		
		neighborList = new ArrayList<WirelessNode>();
		eventList = new ArrayList<NodeEvent>();
		
		setRange(range);
		setPosition(x, y);
	}
	
	/**
	 * Create a wireless node in a network with id, name, (position x,y) and range.
	 * The network will check for valid id, position before adding this node.
	 * @param network
	 * @param id
	 * @param name
	 * @param x
	 * @param y
	 * @param range
	 * @throws NullPointerException If network is null
	 */
	public WirelessNode(WirelessNetwork network, int id, String name, int x, int y, int range) throws NullPointerException {
		super(network, id, name);
		
		neighborList = new ArrayList<WirelessNode>();
		eventList = new ArrayList<NodeEvent>();			
		
		this.range = range;
		this.setPosition(x, y);
	}
	
	public void addNeighbor(WirelessNode node) {
		if (!neighborList.contains(node))
			neighborList.add(node);
	}
	
	public void removeNeighbor(WirelessNode node) {
		if (neighborList.contains(node))
			neighborList.remove(node);
	}
	
	public void addEvent(NodeEvent event) {
		if (!eventList.contains(event))
			eventList.add(event);
	}
	
	public void removeEvent(NodeEvent event) {
		if (eventList.contains(event))
			eventList.remove(event);
	}

	public int getRange() { return range; }
	
	public void setRange(int range) {
		if (range > 0) {
			this.range = range;
			((WirelessNetwork) network).onUpdateNeighbors(this);
			
			setChanged();
			notifyObservers(range);
		}
	}
	
	public 	  abstract int  getX();
	protected abstract void setX(int x);
	
	public    abstract int  getY();
	protected abstract void setY(int y);
	
	/**
	 * Set position for node. If new position is set successfully, the neighbor list is also updated
	 * @param x
	 * @param y
	 */
	public boolean setPosition(int x, int y) {
		WirelessNetwork wn = (WirelessNetwork)network;
		
		// do not allow negative number
		if (x < 0 || y < 0) 
			return false;
		
		// anonymous node
		if (wn.getWidth() > x && wn.getLength() > y) {
			setX(x);
			setY(y);
			
			wn.onUpdateNeighbors(this);
			
			setChanged();
			notifyObservers("Position");
			
			return true;
		}		
		
		return false;
	}
		
	public List<NodeEvent> getEventList() { return eventList; }
	public List<WirelessNode> getNeighborList() { return neighborList; }
}