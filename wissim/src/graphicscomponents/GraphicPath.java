package graphicscomponents;

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
	
	public GraphicPath(int type) {
		this.type = type;
		
		initialize();
	}
	
	public void setStartNode(GWirelessNode node) {
		this.startNode = node;
	}
	
	public void setEndNode(GWirelessNode node) {
		this.endNode = node;
	}
	
	public void addNode(GWirelessNode node) {
		nodeList.add(node);
	}
	
	public void initialize() {
		startNode = null;
		endNode = null;
		nodeList = new LinkedList<GWirelessNode>();		
	}
	
	public GWirelessNode getStartNode() {
		return startNode;
	}
	
	public GWirelessNode getEndNode() {
		return endNode;
	}
	
	public List<GWirelessNode> getNodeList() {
		return nodeList;
	}
	
	public int getType() {
		return type;
	}
}
