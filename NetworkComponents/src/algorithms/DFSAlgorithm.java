package algorithms;

import networkcomponents.WirelessNetwork;
import networkcomponents.WirelessNode;

public class DFSAlgorithm extends Algorithm {
	private Graph graph;
	private TreeNode tree;
	private WirelessNode rootNode;
	
	public DFSAlgorithm(Object input) {
		super(input);

		graph = new Graph((WirelessNetwork) input);
	}

	@Override
	public Object doAlgorithm() {
		if (rootNode == null) return null;
		
		// reset label
		graph.resetLabel();
		
		// initialize result tree object with Node map to root
		tree = new TreeNode(rootNode);
		
		// create result tree
		proc(tree);
		
		// return result
		return tree;
	}
	
	private void proc(TreeNode roodNode)
	{
		// mark StartNode as explored		
		graph.label.put(roodNode.wirelessNode, true);
		
		// look for each NeighborNode of startNode
		// for (WirelessNode wirelessNode : roodNode.WirelessNode.getNeighborList())
		for (WirelessNode wirelessNode : graph.getAdjacentNodes(roodNode.wirelessNode))
		{
			if (graph.label.get(wirelessNode) == false)
			{
				// create tree node
				TreeNode treeNode = new TreeNode(wirelessNode);
				
				// Add Node to roodNode's child list
				roodNode.childList.add(treeNode);
				
				// Process with child node
				proc(treeNode);
			}
		}
	}
	
	public void setRootNode(WirelessNode node) {
		this.rootNode = node;
	}
}
