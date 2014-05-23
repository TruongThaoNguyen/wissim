/**
 * Network.java
 * 
 * @Copyright (C) 2014, Sedic Laboratory, Hanoi University of Science and
 *            Technology
 * @Author Duc-Trong Nguyen
 * @Version 2.0
 */

package models.networkcomponents;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public abstract class Network extends Observable {

	/**
	 * Simulation Time
	 */
	private static int time;
	
	protected List<Node> nodeList = new ArrayList<Node>();
	
	/**
	 * Crease new Node.
	 * @param x x - dimension of new node
	 * @param y y - dimension of new node
	 * @param range communication range
	 * @return new Node
	 * @throws Exception 
	 */
	public abstract Node addNode(int x, int y) throws Exception;	
	
	/**
	 * Remove node from network
	 * @param node
	 */
	public abstract boolean removeNode(Node node);
	
	/**
	 * get Node List.
	 * @return list of node
	 */
	public List<Node> getNodeList() {
		return nodeList;
	}
	
	/**
	 * get node by Id.
	 * @param id
	 * @return node
	 */
	public Node getNodeById(int id) {
		for (Node n : getNodeList()) 
		{
			if (n.getId() == id) return n;
		}		
		return null;
	}

	public int generateId() {
		int id = 0;
		boolean ok = true;
		while (ok)
		{		
			ok = false;
			for (Node node : getNodeList()) 
			{
				if (node.getId() == id)
				{
					ok = true;
					id++;
				}
			}			
		}	
		return id;
	}
	
	// region ------------------- Name ------------------- //

	// network name
	private String name;
		
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		
		setChanged();
		notifyObservers("Name");
	}	

	// endregion Name
	
	// region ------------------- Time ------------------- //

	public int getTime() { return time; }
	protected abstract void settime(int time);
	
	/**
	 * Set time for network. Only accept positive time
	 * @param time
	 */
	public final void setTime(int value) {
		if (value > 0 && value != time) 
		{
			time = value;
			settime(value);			
			setChanged();
			notifyObservers("Time");
		}
	}
	
	// endregion Time
}
