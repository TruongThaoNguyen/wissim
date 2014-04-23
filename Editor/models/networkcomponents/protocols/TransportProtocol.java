package models.networkcomponents.protocols;

import java.util.ArrayList;
import java.util.List;

import models.networkcomponents.Node;

public abstract class TransportProtocol extends Protocol {
	public static final int TCP = 0, UDP = 1;
	
	private Node node;

	private int type;

	List<ApplicationProtocol> appList;
	
	protected TransportProtocol() {						
		appList = new ArrayList<ApplicationProtocol>();		
	}

	public Node getNode() {
		return node;
	}

	public void setNode(Node node) {
		this.node = node;
	}
	
	public List<ApplicationProtocol> getAppList() { return appList; }
	
	public void addApp(ApplicationProtocol app) {
		if (!appList.contains(app))
			appList.add(app);
	}
	
	public boolean removeApp(ApplicationProtocol app) {
		return appList.remove(app);
	}
	
	public int getType() { return type; }

	protected void setType(int type) {
		this.type = type;
	}
}