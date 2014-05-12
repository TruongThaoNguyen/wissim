/**
 * WirelessNetwork.java
 * 
 * @Copyright (C) 2014, Sedic Laboratory, Hanoi University of Science and
 *            Technology
 * @Author Duc-Trong Nguyen
 * @Version 2.0
 */

package models.networkcomponents;

import java.util.List;

public abstract class WirelessNetwork extends Network {
	/**
	 * size of network
	 */
	private static int xSize, ySize;
	
	// options for network size changing
	public static final int LEFT = 0, CENTER = 1, RIGHT = 2, TOP = 0, BOTTOM = 2;
	
	// region ------------------- Size ------------------- //

	public int getWidth() { return xSize; }
	public int getLength() { return ySize; }
	public int setWidth(int value) { return xSize = value; }
	public int setLength(int value) { return ySize = value; }
	
	/**
	 * Set size for wireless network. Size changes also lead to changes in nodes
	 * @param width
	 * @param length
	 * @param wType
	 * @param lType
	 */
	public boolean setSize(int width, int length, int wType, int lType) {
		int ew = width - xSize;
		int el = length - ySize;
		
		if (width <= 0 || length <= 0) return false;

		xSize = width;
		ySize = length;
		
		// default option is (right, bottom)
		if ((wType != LEFT && wType != CENTER && wType != RIGHT) || (lType != TOP && lType != CENTER && lType != BOTTOM)) {
			wType = RIGHT;
			lType = BOTTOM;
		}
		
		List<Node> nodeList = getNodeList();
		int x, y;	// storing expected location of node
		for (Node n : nodeList) {
			WirelessNode wn = (WirelessNode) n;
			
			switch (wType) {
			case LEFT:
				x = wn.getX() + ew;
				break;
			case CENTER:
				x = wn.getY() + ew / 2;
			case RIGHT:
			default:
				x = wn.getX();
			}
			
			switch (lType) {
			case TOP:
				y = wn.getY() + el;
				break;
			case CENTER:
				y = wn.getY() + el / 2;
				break;
			case BOTTOM:
			default:
				y = wn.getY();
			}
			
			if (x > 0 && y > 0) {
				// check whether there existed a not with (x, y) in network
				WirelessNode existedNode = getNodeByPosition(x, y);
				if (existedNode != null)
					wn.setPosition(x, y);
			}
		}
		
		setChanged();
		notifyObservers("SetSize");
		
		return true;		
	}
	
	// endregion Size

	// region ------------------- update Neighbors ------------------- //

	/**
	 * Update neighbors of all nodes in the networks.
	 * Complexity O(n^2)
	 */
	public void updateNeighbors() {
		// remove all neighbors lists of nodes in the network
		for (int i = 0; i < getNodeList().size(); i++)
			((WirelessNode)getNodeList().get(i)).getNeighborList().clear();
		
		for (int i = 0; i < getNodeList().size(); i++) {			
			for (int j = i + 1; j < getNodeList().size(); j++) {	
				WirelessNode nodei = (WirelessNode)getNodeList().get(i);
				WirelessNode nodej = (WirelessNode)getNodeList().get(j);
				
				int dist2 = (nodei.getX() - nodej.getX())*(nodei.getX() - nodej.getX()) + (nodei.getY() - nodej.getY())*(nodei.getY() - nodej.getY());
				
				if (dist2 <= nodei.getRange() * nodei.getRange())
					nodej.addNeighbor(nodei);
				if (dist2 <= nodej.getRange() * nodej.getRange())
					nodei.addNeighbor(nodej);
			}
		}
	}
	
	/**
	 * Update neighbors when a node n is added or changed.
	 * @param n
	 */
	public void updateNeighbors(WirelessNode n) {
		if (getNodeList().contains(n))	// clear neighbor
		{			
			// first remove this node in neighbor lists of other nodes
			// because the neighbor list of each node usually has small size, this would ensure low cost
			for (int i = 0; i < getNodeList().size(); i++) 
			{
				WirelessNode node = (WirelessNode) getNodeList().get(i);
				for (int j = 0; j < node.getNeighborList().size(); j++) 
				{
					if (node.getNeighborList().get(j).getId() == n.getId())
					{
						node.getNeighborList().remove(j);
					}
				}
			}
			
			// clear all nodes in neighbor list of mNode
			n.getNeighborList().clear();
		}
		
		for (int i = 0; i < getNodeList().size(); i++) 
		{
			WirelessNode node = (WirelessNode)getNodeList().get(i);
			
			if (node.getId() != n.getId()) 
			{
				int dist2 = (node.getX() - n.getX())*(node.getX() - n.getX()) + (node.getY() - n.getY())*(node.getY() - n.getY());
				
				if (dist2 <= node.getRange()*node.getRange())
					n.addNeighbor(node);
				if (dist2 <= n.getRange()*n.getRange())
					node.addNeighbor(n);				
			}
		}		
	}
	
	/**
	 * Remove neighbor relationship when a node n is removed from the network
	 * @param n
	 */
	private void removeNeighbor(WirelessNode n) {
		for (Node node : getNodeList()) 
		{
			((WirelessNode)node).removeNeighbor(n);
		}		
	}
	
	// endregion update Neighbors
	
	// region ------------------- add/remove/get node ------------------- //
		
	@Override
	public final Node addNode(int x, int y, int range) {
		WirelessNode node = addnode(x, y, range);
		updateNeighbors(node);
		return node;
	}
		
	/**
	 * Remove node out of network, including update neighbor list
	 */
	@Override
	public final boolean removeNode(Node n) {
		removeNeighbor((WirelessNode) n);
		return removenode(n);			
	}

	protected abstract WirelessNode addnode(int x, int y, int range);
	protected abstract boolean removenode(Node n);

	private WirelessNode getNodeByPosition(int x, int y) {
		for (Node n : getNodeList()) {
			WirelessNode wn  = (WirelessNode) n;
			
			if (wn.getX() == x && wn.getY() == y)
				return wn;
		}
		
		return null;
	}

	// endregion add/remove node
}
