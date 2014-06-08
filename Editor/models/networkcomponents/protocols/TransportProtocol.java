/**
 * TransportProtocol.java
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
import models.networkcomponents.protocols.ApplicationProtocol.ApplicationProtocolType;

public abstract class TransportProtocol implements Protocol {
	
	public static enum TransportProtocolType { Null, TCP, UDP }
	
	protected TransportProtocolType type = null;

	protected List<ApplicationProtocol> appList = new ArrayList<ApplicationProtocol>();
	
	protected TransportProtocol(TransportProtocolType type, String name) {
		setName(name);
		this.type = type;
	}

	public List<ApplicationProtocol> getAppList() { return appList; }
	
	public abstract ApplicationProtocol addApp(ApplicationProtocolType type, String name, Node destNode);
	
	public abstract boolean removeApp(ApplicationProtocol app);
	
	public TransportProtocolType getType() { return type; }
	protected void setType(TransportProtocolType type) { this.type = type; }
	
	protected abstract void setName(String name);	
}
