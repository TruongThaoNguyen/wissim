package models.networkcomponents;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

/**
 * Network.java
 * 
 * @Copyright (C) 2014, Sedic Laboratory, Hanoi University of Science and
 *            Technology
 * @Author Duc-Trong Nguyen
 * @Version 2.0
 */

public abstract class Network extends Observable 
{
	/**
	 * Network name.
	 */
	private String name;
	
	/**
	 * Simulation time.
	 */
	private int time;

	/**
	 * List of Node.
	 */
	protected List<Node> nodeList;
	
	/**
	 * Create network with name and total simulation time
	 * @param name
	 * @param time
	 */
	public Network(String name, int time) 
	{
		this.name = name;
		this.time = time;
		nodeList = new ArrayList<Node>();
	}
	
	/**
	 * Create network with name
	 * @param name
	 */
	public Network(String name) 
	{	
		this.name = name;
		this.time = 0;
		nodeList = new ArrayList<Node>();
	}
		
	/**
	 * Add node to this network.
	 * @param node node will be added.
	 */
	public abstract Node addNode(int x, int y, int rage);
	
	/**
	 * Remove node from network.
	 * @param node
	 */
	public abstract boolean removeNode(Node node);
	
	public String getName() {
		return name;
	}
	
	public int getTime() {
		return time;
	}
	
	/**
	 * Set time for network. Only accept positive time
	 * @param time
	 */
	public void setTime(int time) {
		if (time > 0) {
			this.time = time;
			
			setChanged();
			notifyObservers("Time");
		}
	}
	
	public List<Node> getNodeList()
	{
		return nodeList;
	}
	
	public Node getNodeById(int id) 
	{
		for (Node n : getNodeList()) 
		{
			if (n.getId() == id) return n;
		}		
		return null;
	}

	public void setName(String name) 
	{
		this.name = name;
		
		setChanged();
		notifyObservers("Name");
	}
	
	public int generateId()
	{
		int id = 0;
		boolean cont = true;
		while (cont)
		{			
			cont = false;
			for (Node n : getNodeList())
			{
				if (n.getId() == id)
				{
					id++;
					cont = true;
				}
			}
		}
		
		return id;
	}
}