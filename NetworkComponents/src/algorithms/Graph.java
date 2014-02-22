package algorithms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import networkcomponents.*;

public class Graph {	
	WirelessNetwork network;
	
	/**
	 * Create a graph from a wireless network
	 * @param Network
	 */
	public Graph(networkcomponents.WirelessNetwork Network)	{
		this.network = Network;
		
		// create label for each node, default value = false;
		label = new HashMap<WirelessNode, Boolean>();
		for (Node Node : Network.getNodeList()) 
			label.put((WirelessNode)Node, false);
		
		cost = new HashMap<WirelessNode, Double>();
		
		prev = new HashMap<WirelessNode, WirelessNode>();
		
		// create adjacency matrix
		size = Network.getNodeList().size();		
		adjMatrix = new Boolean[size][size];
		
		for (int i = 0; i < size; i++)
			for (int j = 0; j < size; j++) {
				// check if node[i] is the neighbor of node[j]
				if(((WirelessNode)(Network.getNodeList().get(j))).getNeighborList().indexOf((WirelessNode)(Network.getNodeList().get(i))) != -1 ) {
					// create edge i to j
					adjMatrix[i][j] = true;
				} else {
					adjMatrix[i][j] = false;
				}
			}
	}
	
	// reset all Label to false
	public void resetLabel() {
		label.clear();
		for (Node Node : network.getNodeList()) 
			label.put((WirelessNode)Node, false);
	}

	// return list of Adj vertices of Node
	public List<WirelessNode> getAdjacentNodes(WirelessNode Node) {
		// find id of Node in Network
		int id = network.getNodeList().indexOf(Node);
		
		// create list of Adj veritce of Node
		List<WirelessNode> result = new ArrayList<WirelessNode>();
		for (int i = 0; i < size; i++)
		{
			if (adjMatrix[id][i])
				result.add((WirelessNode)(network.getNodeList().get(i)));
		}
		
		return result;
	}
	
	// set all label's cost to max
	public void resetCost()	{
		if (cost != null)
			cost.clear();		
	}
	
	
	public int size;				// Size of AdjMatrix = Size x Size
	private Boolean[][] adjMatrix;	// AdjMatrix[x][y] = true : Node x can sent packet to Node y	
	
	// Label for each Node in graph
	public HashMap<WirelessNode, Boolean> label;	
	public HashMap<WirelessNode, Double> cost;	
	public HashMap<WirelessNode, WirelessNode> prev;

	public void reduce(Node u, Node v) {
		adjMatrix[((WirelessNode)u).getId()][((WirelessNode)v).getId()] = false;
		adjMatrix[((WirelessNode)v).getId()][((WirelessNode)u).getId()] = false;
	}
}
