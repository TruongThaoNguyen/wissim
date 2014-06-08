package models.networkcomponents.algorithms;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import models.networkcomponents.Node;
import models.networkcomponents.WirelessNetwork;
import models.networkcomponents.WirelessNode;

public class BFSAlgorithm extends Algorithm {
	private Graph graph;
	private WirelessNode rootNode;
	
	public BFSAlgorithm(Object input) {
		super(input);
		this.graph = new Graph((WirelessNetwork) input);
	}
	
	public List<TreeNode> findAllTrees()
	{
		List<TreeNode> result = new ArrayList<TreeNode>();
		graph.resetLabel();
		
		for (Node node : graph.network.getNodeList())
		{
			if (graph.label.get(node) == false)
				result.add(proc((WirelessNode) node));
		}
		
		return result;
	}
	
	@Override
	public Object doAlgorithm() {
		graph.resetLabel();
		return proc(rootNode);
	}
	
	private TreeNode proc(WirelessNode node)
	{
		// Initialize result tree. map Node to tree root
		TreeNode tree = new TreeNode(node);
		
		// mark Node as explored
		graph.label.put(tree.wirelessNode, true);	
		
		// initialize Queue
		Queue<TreeNode> queue = new LinkedList<TreeNode>();
		queue.add(tree);
		
		// create result tree	
		while (!queue.isEmpty())
		{
			TreeNode rootNode = (TreeNode) queue.poll();
			for (WirelessNode wirelessNode : graph.getAdjacentNodes(rootNode.wirelessNode)) 
			{
				// wirelessNode isn't explored
				if (graph.label.get(wirelessNode) == false)
				{
					// mark wirelessNode as explored
					graph.label.put(wirelessNode, true);
																			
					// create tree node
					TreeNode treeNode = new TreeNode(wirelessNode);
					
					// add treeNode to rootNode's child list
					rootNode.childList.add(treeNode);
					
					// add treeNode to Queue
					queue.add(treeNode);
				}
			}		
		}
		
		return tree;
	}
	
	/**
	 * Check whether network is connected
	 * @return
	 */
	public boolean checkConnectivity()	{
		if (findAllTrees().size() >= 2)
			return false;
		else
			return true;
	}	
	
	/**
	 * Check whether there is a way from u to v in the network
	 * @param node
	 */
	public boolean checkConnectable(WirelessNode u, WirelessNode v) {
		// Initialize Graph label		
		graph.resetLabel();
		
		// Initialize result tree. map Node to tree root
		TreeNode tree = new TreeNode(u);
		
		// mark Node as explored
		graph.label.put(tree.wirelessNode, true);	
		
		// initialize Queue
		Queue<TreeNode> queue = new LinkedList<TreeNode>();
		queue.add(tree);
		
		// find path to V
		while (!queue.isEmpty())
		{
			TreeNode rootNode = (TreeNode) queue.poll();
			for (WirelessNode wirelessNode : graph.getAdjacentNodes(rootNode.wirelessNode)) 
			{
				// check V
				if (wirelessNode == v) return true;
				
				// wirelessNode isn't explored
				if (graph.label.get(wirelessNode) == false)
				{
					// mark wirelessNode as explored
					graph.label.put(wirelessNode, true);
																			
					// create tree node
					TreeNode treeNode = new TreeNode(wirelessNode);
					
					// add treeNode to rootNode's child list
					rootNode.childList.add(treeNode);
					
					// add treeNode to Queue
					queue.add(treeNode);
				}
			}		
		}
		
		return false;		
	}
	
	public void setRootNode(WirelessNode node) {
		this.rootNode = node;
	}
}
