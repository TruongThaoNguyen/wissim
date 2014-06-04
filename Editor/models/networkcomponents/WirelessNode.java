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

import models.networkcomponents.events.Event;
import models.networkcomponents.events.Event.EventType;

public abstract class WirelessNode extends Node {
	/**
	 * list of neighbors
	 */
	private List<WirelessNode> neighborList = new ArrayList<WirelessNode>();
	
	protected WirelessNode(WirelessNetwork network)
	{
		super(network);
	}
	
	public List<WirelessNode> 	getNeighborList() 	{ return neighborList; }
	public void addNeighbor		(WirelessNode node) { if (!neighborList.contains(node)) neighborList.add(node); }	
	public void removeNeighbor	(WirelessNode node) { if (neighborList.contains(node))	neighborList.remove(node); }
	
	public abstract List<Event> getEventList();	
	public abstract void 		addEvent(EventType type, int raiseTime);	
	public abstract void 		removeEvent(Event event);
	
	public abstract int getRange();	
	
	public abstract int getX();
	public abstract int getY();
	
	/**
	 * set X position with update neighbor 
	 * @param x
	 */
	protected final void setX(int x) {
		setx(x);
		((WirelessNetwork)network).updateNeighbors(this);
	}
	
	/**
	 * set Y position with update neighbor
	 * @param y
	 */
	protected final void setY(int y) {
		sety(y);
		((WirelessNetwork)network).updateNeighbors(this);
	}
	
	/**
	 * set X position without update neighbor
	 * @param x
	 */
	protected abstract void setx(int x);
	
	/**
	 * set Y position without update neighbor
	 * @param y
	 */
	protected abstract void sety(int y);
	
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
