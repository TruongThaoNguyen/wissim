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
	
//	/**
//	 * Create new project.
//	 */
//	public Project()
//	{		
//		labelList = new ArrayList<Label>();
//		obstacleList = new ArrayList<Area>();
//		obstacleIndex = 0;
//		
//		// set current time as created date
//		createdDate = new Date();
//		lastSavedDate = new Date();		
//	}
//	
//	/**
//	 * Create new project from path
//	 * @param path Path of project
//	 */
//	public Project(String path, WirelessNetwork network) {
//		this.path = path;
//		//this.network = network;
//		
//		labelList = new ArrayList<Label>();
//		obstacleList = new ArrayList<Area>();
//		obstacleIndex = 0;
//		
//		// set current time as created date
//		createdDate = new Date();
//		lastSavedDate = new Date();
//	}

	// region ------------------- Manager properties ------------------- //

	// path of the project
	private String path;
	
	// created date
	private Date createdDate;
	
	// last saved date
	private Date lastSavedDate;
	
	// list of labels
	private List<Label> labelList;
	
	// list of obstacles
	private List<Area> obstacleList;
	private int obstacleIndex;

	
	public void setPath(String path) 						{ this.path 			= path; }
	public void setCreatedDate(Date date)					{ this.createdDate 		= date; }
	public void setLastSavedDate(Date date) 				{ this.lastSavedDate 	= date; }
	public void setLabelList(List<Label> labelList) 		{ this.labelList 		= labelList; }
	public void setObstacleList(List<Area> obstacleList) 	{ this.obstacleList 	= obstacleList;	}
	
	public String		getPath() 			{ return path; }
	public Date 		getCreatedDate() 	{ return createdDate; }
	public Date 		getLastSavedDate() 	{ return lastSavedDate; }
	public List<Label>	getLabelList() 		{ return labelList; }
	public List<Area> 	getObstacleList() 	{ return obstacleList; }

	public void addObstacle(Area area) {
		area.setId(obstacleIndex++);
		obstacleList.add(area);
	}
	
	// endregion Manager properties

	// region ------------------- Configure properties ------------------- //

	public abstract WirelessNetwork getNetwork();
	
	public abstract void setNodeRange(int nodeRange);
	public abstract void setQueueLength(int queueLength);
	public abstract void setIddleEnergy(double iddleEnergy);
	public abstract void setReceptionEnergy(double receptionEnergy);
	public abstract void setSleepEnergy(double sleepEnergy);	
	public abstract void setTransmissionEnergy(double transmissionEnergy);

	public abstract double 	getSleepEnergy();
	public abstract double 	getTransmissionEnergy();	
	public abstract int		getQueueLength();
	public abstract int		getNodeRange();
	public abstract double	getIddleEnergy();
	public abstract double 	getReceptionEnergy();
	
	public abstract void setRoutingProtocols(		HashMap<String, HashMap<String, String>> ps);	
	public abstract void setTransportProtocols(		HashMap<String, HashMap<String, String>> ps);
	public abstract void setLinkLayers(				HashMap<String, HashMap<String, String>> ps);
	public abstract void setApplicationProtocols(	HashMap<String, HashMap<String, String>> ps);
	public abstract void setMacs(					HashMap<String, HashMap<String, String>> ps);
	public abstract void setChannels(				HashMap<String, HashMap<String, String>> ps);
	public abstract void setPropagationModels(		HashMap<String, HashMap<String, String>> ps);
	public abstract void setNetworkInterfaces(		HashMap<String, HashMap<String, String>> ps);
	public abstract void setAntennas(				HashMap<String, HashMap<String, String>> ps);
	public abstract void setInterfaceQueues(		HashMap<String, HashMap<String, String>> ps);
	
	public abstract HashMap<String, HashMap<String, String>> getApplicationProtocols();
	public abstract HashMap<String, HashMap<String, String>> getMacs();				
	public abstract HashMap<String, HashMap<String, String>> getChannels();
	public abstract HashMap<String, HashMap<String, String>> getPropagationModels();
	public abstract HashMap<String, HashMap<String, String>> getNetworkInterfaces();
	public abstract HashMap<String, HashMap<String, String>> getAntennas();
	public abstract HashMap<String, HashMap<String, String>> getInterfaceQueues();
	public abstract HashMap<String, HashMap<String, String>> getRoutingProtocols();
	public abstract HashMap<String, HashMap<String, String>> getTransportProtocols();
	public abstract HashMap<String, HashMap<String, String>> getLinkLayers();

	public abstract void setSelectedRoutingProtocol(	String selected);
	public abstract void setSelectedTransportProtocol(	String selected);
	public abstract void setSelectedApplicationProtocol(String selected);
	public abstract void setSelectedLinkLayer(			String selected);
	public abstract void setSelectedMac(				String selected);
	public abstract void setSelectedAntenna(			String selected);
	public abstract void setSelectedChannel(			String selected);
	public abstract void setSelectedPropagationModel(	String selected);
	public abstract void setSelectedInterfaceQueue(		String selected);
	public abstract void setSelectedNetworkInterface(	String selected);
	
	public abstract String getSelectedRoutingProtocol();
	public abstract String getSelectedTransportProtocol();
	public abstract String getSelectedApplicationProtocol();
	public abstract String getSelectedLinkLayer();
	public abstract String getSelectedMac();
	public abstract String getSelectedChannel();
	public abstract String getSelectedPropagationModel();
	public abstract String getSelectedAntenna();
	public abstract String getSelectedInterfaceQueue();
	public abstract String getSelectedNetworkInterface();
	
	// endregion Configure properties
}
