package models.networkcomponents.algorithms;

import java.util.ArrayList;
import java.util.List;

import models.networkcomponents.WirelessNode;

public class TreeNode {
	public WirelessNode wirelessNode;	// wireless Node map to this node in the tree
	public List<TreeNode> childList;
	public int cost;
	
	public TreeNode(WirelessNode rootNode)
	{
		this.wirelessNode = rootNode;
		this.childList = new ArrayList<TreeNode>();
		childList.clear();
		cost = 0;
	}
}
