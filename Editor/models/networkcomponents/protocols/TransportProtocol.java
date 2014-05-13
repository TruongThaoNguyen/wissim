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

public abstract class TransportProtocol implements Protocol {
	public static final int TCP = 0, UDP = 1;
	
	protected Node node;
	protected int type;

	protected List<ApplicationProtocol> appList = new ArrayList<ApplicationProtocol>();
	
	protected TransportProtocol(int type, String name, Node node) {
		setName(name);
		
		this.node = node;				
		this.type = type;
	}

	public List<ApplicationProtocol> getAppList() { return appList; }
	
	public abstract ApplicationProtocol addApp(int type, String name, Node destNode);
	
	public abstract boolean removeApp(ApplicationProtocol app);
	
	public int getType() { return type; }
	protected void setType(int type) { this.type = type; }
	
	protected abstract void setName(String name);	
}
