package models.networkcomponents.algorithms;

import java.util.ArrayList;
import java.util.List;

import models.networkcomponents.Node;
import models.networkcomponents.WirelessNetwork;
import models.networkcomponents.WirelessNode;

public class GreedyAlgorithm extends Algorithm {
	private Graph graph;
	private List<WirelessNode> list;
	private WirelessNode sourceNode;
	private WirelessNode destNode;

	public GreedyAlgorithm(Object input) {
		super(input);

		graph = new Graph((WirelessNetwork )input);
		sourceNode = null;
		destNode = null;
	}

	@Override
	public Object doAlgorithm() {	
		if (sourceNode == null || destNode == null) return null;
		
		// initialize Graph
		graph.resetLabel();
		graph.resetCost();		
		
		// calculate all node cost = distance to TagerNode
		for (Node Node : graph.network.getNodeList())		
			graph.cost.put(
				(WirelessNode)Node,
				Math.sqrt(
					(destNode.getX() - ((WirelessNode)Node).getX()) * (destNode.getX() - ((WirelessNode)Node).getX()) + 
					(destNode.getY() - ((WirelessNode)Node).getY()) * (destNode.getY() - ((WirelessNode)Node).getY())
				)
			);
		
		list = new ArrayList<WirelessNode>();
		list.add(sourceNode);
		
		process(sourceNode);
		
		return list;
	}
	
	private void process(WirelessNode Node)	{	
		// mark as explored
		graph.label.put(Node, true);
		
		// find adj node of Node with minimum cost
		double min = graph.cost.get(Node);
		WirelessNode minNode = null;
		
		for (WirelessNode n : graph.getAdjacentNodes(Node))			
			if (!graph.label.get(n) && graph.cost.get(n) < min) {
				min = graph.cost.get(n);
				minNode = n;
			}
		
		// add minNode to result list
		if (minNode != null) {
			list.add(minNode);
			if (minNode != destNode) process(minNode);
		}
	}	
	
	public void setSourceNode(WirelessNode node) {
		this.sourceNode = node;
	}
	
	public void setDestNode(WirelessNode node) {
		this.destNode = node;
	}
}
