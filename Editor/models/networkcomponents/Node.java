package models.networkcomponents;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import models.networkcomponents.protocols.TransportProtocol;
import models.networkcomponents.protocols.TransportProtocol;

public abstract class Node extends Observable {
	// id of node (assign one time, never change)
	private int id;
	
	private String name;
	
	// the network this node is included in (assign one time, never change)
	protected Network network;
	
	// list of applications directly attached to this node
	protected List<TransportProtocol> transportProtocolList = new ArrayList<TransportProtocol>();	
	
	/**
	 * Create an unassigned-id node and add to the network. 
	 * The network will have the responsibility to generate the id for this node.
	 * @param network Must be not null
	 */
	protected Node(Network network) {
		if (network == null) throw new NullPointerException("Network must be not null");
		
		this.network = network;
		
		// call the network to generate an unique id for this node
		this.id = this.network.generateId();
		
		// node name is set as id as default
		this.name = id + "";		 
	}
	
	public int 		getId() 			{ return id;	}
	public Network	getNetwork()		{ return network; }
	public String 	getName() 			{ return name; }
	public void 	setName(String name){ this.name = name; }
	
	public List<TransportProtocol>	getTransportPrototolList()	{ return transportProtocolList; }
	
	public abstract TransportProtocol addTransportProtocol(int type, String name);
	
	public abstract boolean removeTransportProtocol(TransportProtocol transproc);
}
