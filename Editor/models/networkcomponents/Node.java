package models.networkcomponents;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import models.networkcomponents.protocols.TransportProtocol;
import models.networkcomponents.protocols.TransportProtocol.TransportProtocolType;

/**
 * a network node.
 * @author Trongnguyen
 */
public abstract class Node extends Observable {	
	/**
	 * name of node
	 */
	private String name;
	
	/**
	 * the network this node is included in (assign one time, never change)
	 */
	protected Network network;
	
	/**
	 * list of applications directly attached to this node
	 */
	protected List<TransportProtocol> transportProtocolList = new ArrayList<TransportProtocol>();	
	
	/**
	 * Create an unassigned-id node and add to the network. 
	 * The network will have the responsibility to generate the id for this node.
	 * @param network Must be not null
	 */
	protected Node(Network network) {
		if (network == null) throw new NullPointerException("Network must be not null");
		
		this.network = network;		
		
		// node name is set as id as default
		this.name = getId() + "";
		
	//	network.getNodeList().add(this);
		network.getNodeList().add(0, this);
	}
		
	public Network	getNetwork()		{ return network; }
	public String 	getName() 			{ return name; }
	public void 	setName(String name){ this.name = name; }
	
	/**
	 * get current node's id.
	 * id of node is its index in nodes list of network.
	 * @return node's id
	 */
	public int getId() { return network.getNodeList().indexOf(this) + 1; }
	
	public List<TransportProtocol>	getTransportPrototolList()	{ return transportProtocolList; }
	
	public abstract TransportProtocol addTransportProtocol(TransportProtocolType type, String name);
	
	public abstract boolean removeTransportProtocol(TransportProtocol transproc);
}
