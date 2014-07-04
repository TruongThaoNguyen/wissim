package controllers.graphicscomponents;

import java.util.LinkedList;
import java.util.List;

/**
 * Represents a graphic path in the network
 * @author leecom
 *
 */
public class GraphicPath {
	public static final int SHORTEST = 0, GREEDY = 1, USER_DEFINED = 2;
	
	int type;
	GWirelessNode startNode;			// start node
	GWirelessNode endNode;				// end node
	List<GWirelessNode> nodeList;		// represents the path
	/**
	 * Contructor
	 * Define a graphic path on network
	 * @param type : type of path, is posible for 3 type: shortest path, greedy path, from user defined path
	 */
	public GraphicPath(int type) {
		this.type = type;
		
		initialize();
	}
	/**
	 * Setting node start path
	 * @param node : node start path
	 */
	public void setStartNode(GWirelessNode node) {
		this.startNode = node;
	}
	
	/**
	 * Setting node to end of path
	 * @param node : node to end of path
	 */
	public void setEndNode(GWirelessNode node) {
		this.endNode = node;
	}
	
	/**
	 * Add a node on path
	 * @param node : node to add
	 */
	public void addNode(GWirelessNode node) {
		nodeList.add(node);
	}
	/**
	 * initialize a path
	 */
	public void initialize() {
		startNode = null;
		endNode = null;
		nodeList = new LinkedList<GWirelessNode>();		
	}
	
	/**
	 * 
	 * @return node to start path
	 */
	public GWirelessNode getStartNode() {
		return startNode;
	}
	
	/**
	 * 
	 * @return node to end path
	 */
	public GWirelessNode getEndNode() {
		return endNode;
	}
	
	/**
	 * 
	 * @return all node on path
	 */
	public List<GWirelessNode> getNodeList() {
		return nodeList;
	}
	
	/**
	 * 
	 * @return type of path
	 */
	public int getType() {
		return type;
	}
}
