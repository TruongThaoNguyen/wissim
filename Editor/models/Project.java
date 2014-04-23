package models;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import models.networkcomponents.WirelessNetwork;
import models.networkcomponents.features.Area;
import models.networkcomponents.features.Label;

/**
* Represents a project. A Project will contain the main network and everything related to the project itself (obstacles, labels,...)
* @author leecom
*
*/
public abstract class Project {

// region ------------------- Manager properties ------------------- //

/**
* path of the project
*/
private static String path;

/**
* created date
*/
private static Date createdDate;

/**
* last saved date
*/
private static Date lastSavedDate;

/**
* list of labels
*/
private static List<Label> labelList;

/**
* list of obstacles
*/
private static List<Area> obstacleList;
private static int obstacleIndex;

/**
* Communicate range
*/
private static int nodeRange;

/**
* Transport protocol
*/
private static String transportProtocol;
private static String applicationProtocol;

public static void setSelectedApplicationProtocol(String value)	{ applicationProtocol = value; }
public static void setSelectedTransportProtocol(String value)	{ transportProtocol	= value; }
public static void setNodeRange(int value)	{ nodeRange = value; }
public static void setPath(String value) { path = value; }
public static void setCreatedDate(Date value)	{ createdDate = value; }
public static void setLastSavedDate(Date value) { lastSavedDate = value; }
public static void setLabelList(List<Label> value) { labelList = value; }
public static void setObstacleList(List<Area> value)	{ obstacleList = value; }

public static String getSelectedApplicationProtocol(){ return applicationProtocol; }
public static String getSelectedTransportProtocol()	{ return transportProtocol; }
public static int	getNodeRange()	{ return nodeRange; }
public static String	getPath() { return path; }
public static Date getCreatedDate() { return createdDate; }
public static Date getLastSavedDate() { return lastSavedDate; }
public static List<Label>	getLabelList() { return labelList; }
public static List<Area> getObstacleList()	{ return obstacleList; }

public static void addObstacle(Area area) {
area.setId(obstacleIndex++);
obstacleList.add(area);
}

// endregion Manager properties

// region ------------------- Configure properties ------------------- //

private static HashMap<String, HashMap<String, String>> routingProtocols = new HashMap<String, HashMap<String, String>>();
private static HashMap<String, HashMap<String, String>> transportProtocols = new HashMap<String, HashMap<String, String>>();
private static HashMap<String, HashMap<String, String>> applicationProtocols = new HashMap<String, HashMap<String, String>>();
private static HashMap<String, HashMap<String, String>> linkLayers = new HashMap<String, HashMap<String, String>>();
private static HashMap<String, HashMap<String, String>> macs = new HashMap<String, HashMap<String, String>>();
private static HashMap<String, HashMap<String, String>> channels = new HashMap<String, HashMap<String, String>>();
private static HashMap<String, HashMap<String, String>> propagationModels = new HashMap<String, HashMap<String, String>>();
private static HashMap<String, HashMap<String, String>> networkInterfaces = new HashMap<String, HashMap<String, String>>();
private static HashMap<String, HashMap<String, String>> antennas = new HashMap<String, HashMap<String, String>>();
private static HashMap<String, HashMap<String, String>> interfaceQueues = new HashMap<String, HashMap<String, String>>();

public static void setRoutingProtocols	(HashMap<String, HashMap<String, String>> ps) { routingProtocols.putAll(ps); }	
public static void setTransportProtocols	(HashMap<String, HashMap<String, String>> ps) { transportProtocols.putAll(ps); }
public static void setLinkLayers	(HashMap<String, HashMap<String, String>> ps) { linkLayers.putAll(ps); }
public static void setApplicationProtocols	(HashMap<String, HashMap<String, String>> applicationProtocols) { applicationProtocols.putAll(applicationProtocols); }
public static void setMacs	(HashMap<String, HashMap<String, String>> macs) { macs.putAll(macs);	}
public static void setChannels	(HashMap<String, HashMap<String, String>> channels) { channels.putAll(channels);	}
public static void setPropagationModels	(HashMap<String, HashMap<String, String>> propagationModels)	{ propagationModels.putAll(propagationModels);	}
public static void setNetworkInterfaces	(HashMap<String, HashMap<String, String>> networkInterfaces)	{ networkInterfaces.putAll(networkInterfaces);	}
public static void setAntennas	(HashMap<String, HashMap<String, String>> antennas) { antennas.putAll(antennas);	}
public static void setInterfaceQueues	(HashMap<String, HashMap<String, String>> interfaceQueues) { interfaceQueues.putAll(interfaceQueues);	}

public static HashMap<String, HashMap<String, String>> getApplicationProtocols() { return applicationProtocols;	}
public static HashMap<String, HashMap<String, String>> getMacs() { return macs;	}
public static HashMap<String, HashMap<String, String>> getChannels()	{ return channels;	}
public static HashMap<String, HashMap<String, String>> getPropagationModels() { return propagationModels;	}
public static HashMap<String, HashMap<String, String>> getNetworkInterfaces() { return networkInterfaces;	}
public static HashMap<String, HashMap<String, String>> getAntennas() { return antennas;	}
public static HashMap<String, HashMap<String, String>> getInterfaceQueues() { return interfaceQueues;	}
public static HashMap<String, HashMap<String, String>> getRoutingProtocols() { return routingProtocols;	}
public static HashMap<String, HashMap<String, String>> getTransportProtocols()	{ return transportProtocols;	}
public static HashMap<String, HashMap<String, String>> getLinkLayers() { return linkLayers;	}

// endregion Configure properties

// region ------------------- Network properties ------------------- //

public abstract WirelessNetwork getNetwork();

public abstract void setQueueLength	(int queueLength);	
public abstract void setIddleEnergy	(double iddleEnergy);
public abstract void setReceptionEnergy	(double receptionEnergy);
public abstract void setSleepEnergy	(double sleepEnergy);	
public abstract void setTransmissionEnergy	(double transmissionEnergy);
public abstract void setInitialEnergy	(double initialEnergy);

public abstract int	getQueueLength(); // throws ParseException;
public abstract double getSleepEnergy(); // throws ParseException;
public abstract double getTransmissionEnergy(); // throws ParseException;
public abstract double	getIddleEnergy(); // throws ParseException;
public abstract double getReceptionEnergy(); // throws ParseException;
public abstract double	getInitialEnergy(); // throws ParseException;

public abstract void setSelectedRoutingProtocol(	String selected);
public abstract void setSelectedLinkLayer(	String selected);
public abstract void setSelectedMac(	String selected);
public abstract void setSelectedAntenna(	String selected);
public abstract void setSelectedChannel(	String selected);
public abstract void setSelectedPropagationModel(	String selected);
public abstract void setSelectedInterfaceQueue(	String selected);
public abstract void setSelectedNetworkInterface(	String selected);

public abstract String getSelectedRoutingProtocol();
public abstract String getSelectedLinkLayer();
public abstract String getSelectedMac();
public abstract String getSelectedChannel();
public abstract String getSelectedPropagationModel();
public abstract String getSelectedAntenna();
public abstract String getSelectedInterfaceQueue();
public abstract String getSelectedNetworkInterface();

// endregion Network properties
}
