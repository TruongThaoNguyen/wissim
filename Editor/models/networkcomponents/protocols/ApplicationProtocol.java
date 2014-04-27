/**
 * ApplicationProtocol.java
 * 
 * @Copyright (C) 2014, Sedic Laboratory, Hanoi University of Science and
 *            Technology
 * @Author Duc-Trong Nguyen
 * @Version 2.0
 */

package models.networkcomponents.protocols;

import java.util.ArrayList;
import java.util.List;

import models.networkcomponents.Node;
import models.networkcomponents.events.AppEvent;

public abstract class ApplicationProtocol implements Protocol {
	public final static int CBR = 0, VBR = 1, FTP = 2, PARETO = 3, TELNET = 4;
	
	/**
	 * The transport protocol which this protocol bases on
	 */
	protected TransportProtocol transportProtocol;
	
	/**
	 * List of events on this application protocol
	 */
	protected List<AppEvent> eventList = new ArrayList<AppEvent>();
	
	/**
	 * The destination node
	 */
	protected Node destNode;
	
	protected int type; 

	/**
	 * Constructor
	 * @param name Name of this protocol
	 * @param protocol The transport protocol in the lower layer of network protocol
	 */
	protected ApplicationProtocol(int type, String name, TransportProtocol protocol, Node destNode) {						
		setName(name);
		this.transportProtocol = protocol;				
		this.type = type;
		this.destNode = destNode;
	}

	/**
	 * Adds an event to the chain of events of this application protocol
	 * @param e Event will be added
	 * @return <i>true</i> if success, else <i>false</i>
	 */
	public abstract boolean addEvent(AppEvent e);
	
	public abstract boolean removeEvent(AppEvent e);
	
	/**
	 * Gets events generated by this app
	 * @return
	 */
	public List<AppEvent> getEventList() { return eventList; }
	
	public int getType() { return type; }
	
	public Node getDestNode() { return destNode; }
	
	protected abstract void setName(String name);
}
