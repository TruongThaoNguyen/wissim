package models.networkcomponents.protocols;

import java.util.ArrayList;
import java.util.List;

import models.networkcomponents.Node;

public class TransportProtocol extends Protocol {
	public static final int TCP = 0, UDP = 1;
	
	Node node;
	int type;

	List<ApplicationProtocol> appList;
	
	public TransportProtocol(int type, String name, Node node) {
		super(name);
		
		this.node = node;
		node.addTransportProtocol(this);
		appList = new ArrayList<ApplicationProtocol>();
		this.type = type;
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
}
