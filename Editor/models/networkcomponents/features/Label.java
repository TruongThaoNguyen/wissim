package models.networkcomponents.features;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.UUID;

import models.networkcomponents.Node;

public class Label extends Observable {
	String uid;
	String name;
	
	List<Node> nodeList;
	
	public Label(String name) {
		uid = UUID.randomUUID().toString();
		this.name = name;
		
		nodeList = new ArrayList<Node>();
	}
	
	public void add(Node n) {
		int index = 0;
		
		for (int i = 0; i < nodeList.size(); i++) {
			// check for existing node
			if (nodeList.get(i).getId() == n.getId())
				return;
			
			// find index to insert new node
			if (nodeList.get(i).getId() > n.getId()) {
				index = i;
				break;
			}
		}
		
		nodeList.add(index, n);
	}
	
	public boolean remove(Node n) {
		return nodeList.remove(n);
	}
	
	public boolean hasNode(Node n) {
		int index = nodeList.indexOf(n);
		
		return index == -1 ? false: true;
	}
	
	public String getName() { return name; }

	public List<Node> getNodeList() {
		return nodeList;
	}

	public void setName(String name) {
		setChanged();
		notifyObservers("Name");
		this.name = name;
	}
	
	public String getUid() {
		return uid;
	}
}
