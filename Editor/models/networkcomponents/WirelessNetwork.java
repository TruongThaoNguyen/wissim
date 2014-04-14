package models.networkcomponents;

import java.util.List;

/**
 * WirelessNetwork.java
 * 
 * @Copyright (C) 2014, Sedic Laboratory, Hanoi University of Science and Technology
 * @Author Duc-Trong Nguyen
 * @Version 2.0
 */

public abstract class WirelessNetwork extends Network {
	/**
	 * Size of network
	 */
	private int xSize, ySize;
	
	/**
	 * Options for network size changing
	 */
	public static final int LEFT = 0, CENTER = 1, RIGHT = 2, TOP = 0, BOTTOM = 2;
	
	public WirelessNetwork(String name)
	{
		super(name);
	}
	
	public WirelessNetwork(String name, int xSize, int ySize) {
		super(name);
		
		this.xSize = xSize;
		this.ySize = ySize;
	}
	
	public WirelessNetwork(String name, int time, int xSize, int ySize) {
		super(name, time);
		
		this.xSize = xSize;
		this.ySize = ySize;
	}

	/**
	 * Remove node out of network, including update neighbor list
	 */
	@Override
	public final boolean removeNode(Node n)	{
		removeNeighbor((WirelessNode) n);
		removenode(n);
		return nodeList.remove(n);
	}
	
	/**
	 * Remove node out of network, including update neighbor list
	 */
	protected abstract void removenode(Node n);	
	
	@Override
	public final Node addNode(int x, int y, int rage) {
		WirelessNode newNode = addnode(x, y, rage);
		nodeList.add(newNode);
		updateNeighbors(newNode);
		return newNode;
	}
	
	protected abstract WirelessNode addnode(int x, int y, int rage);
	
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
	 * Update neighbors when a node n is added to the network.
	 * @param n added node
	 */
	private void updateNeighbors(WirelessNode n) {
		for (int i = 0; i < getNodeList().size(); i++) {
			WirelessNode node = (WirelessNode)getNodeList().get(i);
			
			if (node.getId() != n.getId()) {
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
	 * @param n removed node
	 */
	private void removeNeighbor(WirelessNode n) {
		for (int i = 0; i < getNodeList().size(); i++) {
			WirelessNode x = (WirelessNode) getNodeList().get(i);
			x.removeNeighbor(n);
		}
	}
	
	/**
	 * Handle when a node in the network change its location
	 * @param n
	 */
	void onUpdateNeighbors(WirelessNode mNode) {
		WirelessNode n;
		
		// first remove this node in neighbor lists of other nodes
		// because the neighbor list of each node usually has small size, this would ensure low cost
		for (int i = 0; i < getNodeList().size(); i++) {
			n = (WirelessNode) getNodeList().get(i);
			for (int j = 0; j < n.getNeighborList().size(); j++) {
				if (n.getNeighborList().get(j).getId() == mNode.getId())
					n.getNeighborList().remove(j);
			}
		}
		
		// clear all nodes in neighbor list of mNode
		mNode.getNeighborList().clear();
		
		// now it looks like we remove mNode, and add it again at other position
		// call the updateNeighbors() to update (O^(n))
		updateNeighbors(mNode);
	}

	public int getWidth() { return xSize; }	
	
	public int getLength() { return ySize; }	
	
	public void setSize(int width, int height)
	{
		xSize = width;
		ySize = height;
	}
	
	/**
	 * Set size for wireless network. Size changes also lead to changes in nodes
	 * @param width
	 * @param length
	 * @param wType
	 * @param lType
	 */
	public boolean setSize(int width, int length, int wType, int lType) {
		int ew = width - this.xSize;
		int el = length - this.ySize;
		
		if (width <= 0 || length <= 0) return false;

		this.xSize = width;
		this.ySize = length;
		
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
	
	private WirelessNode getNodeByPosition(int x, int y) {
		for (Node n : getNodeList()) {
			WirelessNode wn  = (WirelessNode) n;
			
			if (wn.getX() == x && wn.getY() == y)
				return wn;
		}
		
		return null;
	}
}
