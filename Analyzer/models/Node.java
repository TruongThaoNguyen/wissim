package models;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * Describe a node.
 * @author trongnguyen
 *
 */
public class Node {
	
	/**
	 * id of this node.
	 */
	private int id;
	
	/**
	 * group id.
	 */
	private int groupID;
	
	/**
	 * position.
	 */
	private float x, y;

	/**
	 * Energy.
	 * store as a HashMap, hash by time
	 */		
	private ConcurrentSkipListMap<Double, Double> energy = new ConcurrentSkipListMap<>(); 
	
	/**
	 * list of events that belong to this node.
	 */
	private List<Event> event = new ArrayList<Event>();
	
	/**
	 * create new node.
	 * @param id id of new node
	 * @param x x position
	 * @param y y position
	 */
	public Node(int id, float x, float y) {
		this.id = id;
		this.x = x;
		this.y = y;
	}	

	/**
	 * get id of node.
	 * @return id
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * @return the groupID
	 */
	public int getGroupID() {
		return groupID;
	}

	/**
	 * @param groupID the groupID to set
	 */
	public void setGroupID(int groupID) {
		this.groupID = groupID;
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}

	public List<Event> getEvent() {
		return event;
	}
	
	/**
	 * get Energy.
	 * Energy store by map of time and value.
	 * @return
	 */
	public ConcurrentSkipListMap<Double, Double> getEnergy() {
		return energy;
	}	
}
