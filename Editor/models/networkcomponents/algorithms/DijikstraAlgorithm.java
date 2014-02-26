package models.networkcomponents.algorithms;

import java.util.ArrayList;
import java.util.List;

import models.networkcomponents.Node;
import models.networkcomponents.WirelessNetwork;
import models.networkcomponents.WirelessNode;

public class DijikstraAlgorithm extends Algorithm {
	private Graph graph;
	private List<Node> list;
	private WirelessNode sourceNode;

	public DijikstraAlgorithm(WirelessNetwork input) {
		super(input);
		
		graph = new Graph((WirelessNetwork) input);
	}

	@Override
	public Object doAlgorithm() {
		// initialize Graph label
		graph.resetLabel();
		graph.resetCost();
		
		// set rootNode cost = 0
		graph.cost.put(sourceNode, 0.0); 
		
		// initialize List
		//List = Graph.Network.getNodeList();
		list = new ArrayList<Node>();		
		list.add(sourceNode);		
		
		// create result tree
		proc();
		
		list = graph.network.getNodeList();
		TreeNode tree = new TreeNode(sourceNode);
		makeTree(tree);
		
		return tree;
	}
	
	public Object doAlgorithm(WirelessNode destNode) {
		// initialize Graph label		
		graph.resetLabel();
		graph.resetCost();
		
		// set rootNode cost = 0
		graph.cost.put(destNode, 0.0); 
		
		// initialize List
		//List = Graph.Network.getNodeList();
		list = new ArrayList<Node>();		
		list.add(destNode);		
		
		// create result tree
		proc();
		
		// create part
		List<WirelessNode> result = new ArrayList<WirelessNode>();
		WirelessNode node = sourceNode;
		while (node != destNode)
		{
			result.add(node);
			node = graph.prev.get(node);
			if (node == null) return null;
		}		
		result.add(destNode);
		
		return result;		
	}
	
	private void proc()
	{
		while (!list.isEmpty())
		{
			// find Node with minimum distance from source node
			double min = Double.MAX_VALUE;
			WirelessNode minNode = null;
			
			for (Node Node : list) 
			{
				if (graph.cost.get(Node) < min)
				{
					min = graph.cost.get(Node);
					minNode = (WirelessNode) Node;
				}				
			}						
			
			// remove minNode
			list.remove(minNode);
			
			// recalculate adj node of minNode
			min += 1;	// distance = 1 with adj node
			for (WirelessNode Node : graph.getAdjacentNodes(minNode))
			{			
				if (graph.cost.get(Node) == null || graph.cost.get(Node) > min)
				{
					graph.cost.put(Node, min);
					graph.prev.put(Node, minNode);
					if (!list.contains(Node)) list.add(Node);				
				}
			}
		}
	}

	private void makeTree(TreeNode node)
	{
		for (Node wNode : list) 
		{
			if (graph.prev.get(wNode) == node.wirelessNode)
			{
				TreeNode newNode = new TreeNode((WirelessNode)wNode);
				node.childList.add(newNode);
				makeTree(newNode);
			}
		}
	}
	
	public void setSourceNode(WirelessNode sourceNode) {
		this.sourceNode = sourceNode;
	}
}
