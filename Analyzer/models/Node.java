package models;

import java.util.HashMap;
import java.util.List;

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
	private HashMap<Double, Double> energy;
	
	/**
	 * list of events that belong to this node.
	 */
	private List<Event> event;
	
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
	
	public int getId() {
		return id;
	}
	
	public float getX() {
		return x;
	}
	
	public float getY() {
		return y;
	}	
}
