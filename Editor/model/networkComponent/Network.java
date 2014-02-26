package model.networkComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

/**
 * A general network has a name, total simulation time and maintains a list of nodes deployed in
 * @author leecom
 *
 */
public abstract class Network extends Observable {
	// network name
	private String name;
	
	// simulation time
	private int time;
	
	// list of nodes in network
	private List<Node> nodeList;
	
	// manage the node id in this network
	private int cIndex;
	
	/**
	 * Create network with name and total simulation time
	 * @param name
	 * @param time
	 */
	public Network(String name, int time) {
		this.initialize();
		
		this.name = name;
		this.time = time;
	}
	
	/**
	 * Create network with name
	 * @param name
	 */
	public Network(String name) {
		this.initialize();
		
		this.name = name;
	}
	
	/**
	 * Add node to network
	 * @param node
	 */
	protected boolean addNode(Node node) {
		if (!nodeList.contains(node)) {
			nodeList.add(node);
			this.cIndex = this.cIndex >= node.getId() + 1 ? this.cIndex : node.getId() + 1;
			return true;
		} else
			return false;
	}
	
	/**
	 * Remove node from network
	 * @param node
	 */
	public boolean removeNode(Node node) {
		return nodeList.remove(node);
	}
	
	int generateId() {
		cIndex++;
		
		return cIndex - 1;
	}
	
	private void initialize() {	
		nodeList = new ArrayList<Node>();
		cIndex = 0;
		this.name = "";
		this.time = 0;
	}

	public String getName() {
		return name;
	}
	
	public int getTime() {
		return time;
	}
	
	/**
	 * Set time for network. Only accept positive time
	 * @param time
	 */
	public void setTime(int time) {
		if (time > 0) {
			this.time = time;
			
			setChanged();
			notifyObservers("Time");
		}
	}
	
	public List<Node> getNodeList() {
		return nodeList;
	}
	
	public Node getNodeById(int id) {
		for (Node n : nodeList) {
			if (n.getId() == id)
				return n;
		}
		
		return null;
	}

	public void setName(String name) {
		this.name = name;
		
		setChanged();
		notifyObservers("Name");
	}
}
