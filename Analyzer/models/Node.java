package models;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

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
	public int groupID;
	
	/**
	 * position.
	 */
	private float x, y;
	
	/**
	 * Energy.
	 * store as a HashMap, hash by time
	 */
	private AbstractMap<Double, Double> energy = new ConcurrentHashMap<Double, Double>();
	
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
	
	public float getX() {
		return x;
	}
	
	public float getY() {
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
	public AbstractMap<Double, Double> getEnergy() {
		return energy;
	}	
}
