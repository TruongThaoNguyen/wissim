/**
 * WirelessNode.java
 * 
 * @Copyright (C) 2014, Sedic Laboratory, Hanoi University of Science and
 *            Technology
 * @Author Duc-Trong Nguyen
 * @Version 2.0
 */

package models.networkcomponents;

import java.util.ArrayList;
import java.util.List;

import models.networkcomponents.events.NodeEvent;

public abstract class WirelessNode extends Node {

	// radio range
	protected int range;
	
	// list of neighbors
	private List<WirelessNode> neighborList = new ArrayList<WirelessNode>();
	
	// list of events
	private List<NodeEvent> eventList = new ArrayList<NodeEvent>();
	
	protected WirelessNode(WirelessNetwork network)
	{
		super(network);
	}
	
	public List<WirelessNode> getNeighborList() { return neighborList; }
	public void addNeighbor		(WirelessNode node) { if (!neighborList.contains(node)) neighborList.add(node); }	
	public void removeNeighbor	(WirelessNode node) { if (neighborList.contains(node))	neighborList.remove(node); }
	
	public List<NodeEvent> getEventList() { return eventList; }	
	public void addEvent(NodeEvent event) 	{	if (!eventList.contains(event))	eventList.add(event); }	
	public void removeEvent(NodeEvent event){
		if (eventList.contains(event))
			eventList.remove(event);
	}
	
	public int getRange() { return range; }
	
	public void setRange(int range) {
		if (range > 0) {
			this.range = range;
			((WirelessNetwork) network).updateNeighbors(this);
			
			setChanged();
			notifyObservers(range);
		}
	}

	
	public abstract int getX();
	public abstract int getY();
	protected abstract void setX(int x);
	protected abstract void setY(int y);
	
	/**
	 * Set position for node. If new position is set successfully, the neighbor list is also updated
	 * @param x
	 * @param y
	 */
	public final boolean setPosition(int x, int y) {
		WirelessNetwork wn = (WirelessNetwork)network;
		
		// do not allow negative number
		if (x < 0 || y < 0) 
			return false;
		
		// anonymous node
		if (wn.getWidth() > x && wn.getLength() > y) {
			setX(x);
			setY(y);
			
			wn.updateNeighbors(this);
			
			setChanged();
			notifyObservers("Position");
			
			return true;
		}		
		return false;
	}

}
