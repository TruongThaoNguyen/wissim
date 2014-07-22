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
	 * position.
	 */
	private float x, y, z;
	
	/**
	 * Energy.
	 */
	private HashMap<Double, Double> energy;
	
	private List<Event> event;
	
	public Node(int id, float x, float y) {
		this.id = id;
		this.x = x;
		this.y = y;
	}


	@Deprecated
	private int packetId;
	@Deprecated
	public String time;
	@Deprecated
	public String listIDNeighbors;
	@Deprecated
	public int groupID;
	
	public String getListNeighbors() {
		return listIDNeighbors;
	}
	
	public void setListNeighbors(String listNeighbors) {
		this.listIDNeighbors = listNeighbors;
	}
	

	
	public float getY() {
		return y;
	}
	
	public void setY(float y) {
		this.y = y;
	}
	
	public float getZ() {
		return z;
	}
	
	public void setZ(float z) {
		this.z = z;
	}
	
	public int getId() {
		return id;
	}
	
	public float getX() {
		return x;
	}
	
	public int getPacketId(){
		return this.packetId;
	}
	public void setPacketId(int packetId){
		this.packetId=packetId;
	}
}
