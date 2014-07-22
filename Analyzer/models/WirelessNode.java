package models;

import java.util.HashMap;

/**
 * Describe a node.
 * @author trongnguyen
 *
 */
public class WirelessNode {
	
	/**
	 * id of this node.
	 */
	public int id;
	
	/**
	 * position.
	 */
	public float x, y, z;
	
	public HashMap<Double, Double> Energy;
	
	@Deprecated
	private int packetId;
	@Deprecated
	public String time;
	@Deprecated
	public String energy;
	@Deprecated
	public String maxEnergy;
	@Deprecated
	public String listIDNeighbors;
	@Deprecated
	public int groupID;
	
	public String getEnergy() {
		return energy;
	}
	
	public void setEnergy(String energy) {
		this.energy = energy;
	}
	
	public String getListNeighbors() {
		return listIDNeighbors;
	}
	
	public void setListNeighbors(String listNeighbors) {
		this.listIDNeighbors = listNeighbors;
	}
	
	public WirelessNode(int id,String time){
		this.id=id;
		this.time=time;
	}
	
	public WirelessNode(int id, float x, float y, float z, String time, String energy,
			String listNeighbors) {
		super();
		this.id = id;
		this.x = x;
		this.y = y;
		this.z = z;
		this.time = time;
		this.energy = energy;
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
	@Override
	public String toString() {
		return "NodeTrace [id=" + id + ", x=" + x + ", y=" + y + ", z=" + z
				+ ", packetId=" + packetId + ", time=" + time + ", energy="
				+ energy + ", listIDNeighbors=" + listIDNeighbors + "]";
	}
}
