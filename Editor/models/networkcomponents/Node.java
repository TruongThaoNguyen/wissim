package models.networkcomponents;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import models.networkcomponents.protocols.TransportProtocol;

public abstract class Node extends Observable {
	// id of node (assign one time, never change)
	private int id;
	
	private String name;
	
	// the network this node is included in (assign one time, never change)
	protected Network network;
	
	// list of applications directly attached to this node
	private List<TransportProtocol> transportProtocolList;	
	
	/**
	 * Create an unassigned-id node and add to the network. 
	 * The network will have the responsibility to generate the id for this node.
	 * @param network Must be not null
	 */
	public Node(Network network) {
		if (network == null) throw new NullPointerException("Network must be not null");
		
		this.network = network;
		
		// call the network to generate an unique id for this node
		this.id = this.network.generateId();
		
		// node name is set as id as default
		this.name = id + "";
		
		// call the network to add me
		this.network.addNode(this);
		
		transportProtocolList = new ArrayList<TransportProtocol>();
	}
	
	/**
	 * Create a node with assigned-id and then ask the network to add
	 * @param network Must be not null
	 * @param id Id set to node
	 * @param name Name of node
	 */
	public Node(Network network, int id, String name) throws NullPointerException {
		if (network == null) 
			throw new NullPointerException("Network must be not null");
		
		if (id < 0) return;
		
		for (int i = 0; i < network.getNodeList().size(); i++) {
			Node node = network.getNodeList().get(i);
			
			if (node.id == id)
				return;
		}
		
		this.network = network;		
		this.id = id;		
		this.name = name;

		this.network.addNode(this);
		
		transportProtocolList = new ArrayList<TransportProtocol>();			
	}
	
	public int getId() { return id;	}
	
	public Network getNetwork() { return network; }
	
	public String getName() { return name; }
	
	public void setName(String name) { this.name = name; }
	
	public List<TransportProtocol> getTransportPrototolList() { return transportProtocolList; }	
	
	public void addTransportProtocol(TransportProtocol transproc) {
		if (!transportProtocolList.contains(transproc))
			transportProtocolList.add(transproc);
	}
	
	public boolean removeTransportProtocol(TransportProtocol transproc) {
		return transportProtocolList.remove(transproc);
	}
}
