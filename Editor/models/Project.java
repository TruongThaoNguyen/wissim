package models;

import java.util.ArrayList;
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
public class Project {
	// path of the project
	private String path;
	
	// created date
	private Date createdDate;
	
	// last saved date
	private Date lastSavedDate;
	
	/**
	 *  the wireless network this project works with
	 */
	protected WirelessNetwork network;
	
	// list of labels
	private List<Label> labelList;
	
	// list of obstacles
	private List<Area> obstacleList;
	private int obstacleIndex;
	
	// general settings apply to all nodes
	private int nodeRange;
	private int queueLength;
	private HashMap<String, HashMap<String, String>> routingProtocols = new HashMap<String, HashMap<String, String>>();
	private HashMap<String, HashMap<String, String>> transportProtocols = new HashMap<String, HashMap<String, String>>();
	private HashMap<String, HashMap<String, String>> applicationProtocols = new HashMap<String, HashMap<String, String>>();
	private HashMap<String, HashMap<String, String>> linkLayers = new HashMap<String, HashMap<String, String>>();
	private HashMap<String, HashMap<String, String>> macs = new HashMap<String, HashMap<String, String>>();
	private HashMap<String, HashMap<String, String>> channels = new HashMap<String, HashMap<String, String>>();
	private HashMap<String, HashMap<String, String>> propagationModels = new HashMap<String, HashMap<String, String>>();
	private HashMap<String, HashMap<String, String>> networkInterfaces = new HashMap<String, HashMap<String, String>>();
	private HashMap<String, HashMap<String, String>> antennas = new HashMap<String, HashMap<String, String>>();
	private HashMap<String, HashMap<String, String>> interfaceQueues = new HashMap<String, HashMap<String, String>>();
	
	private String selectedRoutingProtocol;
	private String selectedTransportProtocol;
	private String selectedApplicationProtocol;
	private String selectedLinkLayer;
	private String selectedMac;
	private String selectedChannel;
	private String selectedPropagationModel;
	private String selectedNetworkInterface;
	private String selectedAntenna;
	private String selectedInterfaceQueue;
	
	private double iddleEnergy;
	private double receptionEnergy;
	private double transmissionEnergy;
	private double sleepEnergy;	
	
	/**
	 * Create new project.
	 */
	public Project()
	{		
		labelList = new ArrayList<Label>();
		obstacleList = new ArrayList<Area>();
		obstacleIndex = 0;
		
		// set current time as created date
		createdDate = new Date();
		lastSavedDate = new Date();
	}
	
	/**
	 * Create new project from path
	 * @param path Path of project
	 */
	public Project(String path, WirelessNetwork network) {
		this.path = path;
		this.network = network;
		
		labelList = new ArrayList<Label>();
		obstacleList = new ArrayList<Area>();
		obstacleIndex = 0;
		
		// set current time as created date
		createdDate = new Date();
		lastSavedDate = new Date();
	}
	
	public void setRoutingProtocols(HashMap<String, HashMap<String, String>> ps) {
		this.routingProtocols.putAll(ps);
	}
	
	public void setTransportProtocols(HashMap<String, HashMap<String, String>> ps) {
		this.transportProtocols.putAll(ps);
	}
	
	public void setLinkLayers(HashMap<String, HashMap<String, String>> ps) {
		this.linkLayers.putAll(ps);
	}

	public int getNodeRange() {
		return nodeRange;
	}

	public void setNodeRange(int nodeRange) {
		this.nodeRange = nodeRange;
	}

	public int getQueueLength() {
		return queueLength;
	}

	public void setQueueLength(int queueLength) {
		this.queueLength = queueLength;
	}

	public HashMap<String, HashMap<String, String>> getApplicationProtocols() {
		return applicationProtocols;
	}

	public void setApplicationProtocols(
			HashMap<String, HashMap<String, String>> applicationProtocols) {
		this.applicationProtocols.putAll(applicationProtocols);
	}

	public HashMap<String, HashMap<String, String>> getMacs() {
		return macs;
	}

	public void setMacs(HashMap<String, HashMap<String, String>> macs) {
		this.macs.putAll(macs);
	}

	public HashMap<String, HashMap<String, String>> getChannels() {
		return channels;
	}

	public void setChannels(HashMap<String, HashMap<String, String>> channels) {
		this.channels.putAll(channels);
	}

	public HashMap<String, HashMap<String, String>> getPropagationModels() {
		return propagationModels;
	}

	public void setPropagationModels(
			HashMap<String, HashMap<String, String>> propagationModels) {
		this.propagationModels.putAll(propagationModels);
	}

	public HashMap<String, HashMap<String, String>> getNetworkInterfaces() {
		return networkInterfaces;
	}

	public void setNetworkInterfaces(
			HashMap<String, HashMap<String, String>> networkInterfaces) {
		this.networkInterfaces.putAll(networkInterfaces);
	}

	public HashMap<String, HashMap<String, String>> getAntennas() {
		return antennas;
	}

	public void setAntennas(HashMap<String, HashMap<String, String>> antennas) {
		this.antennas.putAll(antennas);
	}

	public HashMap<String, HashMap<String, String>> getInterfaceQueues() {
		return interfaceQueues;
	}

	public void setInterfaceQueues(
			HashMap<String, HashMap<String, String>> interfaceQueues) {
		this.interfaceQueues.putAll(interfaceQueues);
	}

	public double getIddleEnergy() {
		return iddleEnergy;
	}

	public void setIddleEnergy(double iddleEnergy) {
		this.iddleEnergy = iddleEnergy;
	}

	public double getReceptionEnergy() {
		return receptionEnergy;
	}

	public void setReceptionEnergy(double receptionEnergy) {
		this.receptionEnergy = receptionEnergy;
	}

	public double getTransmissionEnergy() {
		return transmissionEnergy;
	}

	public void setTransmissionEnergy(double transmissionEnergy) {
		this.transmissionEnergy = transmissionEnergy;
	}

	public double getSleepEnergy() {
		return sleepEnergy;
	}

	public void setSleepEnergy(double sleepEnergy) {
		this.sleepEnergy = sleepEnergy;
	}

	public HashMap<String, HashMap<String, String>> getRoutingProtocols() {
		return routingProtocols;
	}

	public HashMap<String, HashMap<String, String>> getTransportProtocols() {
		return transportProtocols;
	}

	public HashMap<String, HashMap<String, String>> getLinkLayers() {
		return linkLayers;
	}

	public WirelessNetwork getNetwork() { return network; }
	
	public String getPath() { return path; }
	
	public void setPath(String path) { this.path = path; }
	
	public void setCreatedDate(Date date) {		
		createdDate = date;
	}
	
	public Date getCreatedDate() { return createdDate; }
	
	public void setLastSavedDate(Date date) { 
		lastSavedDate = date;
	}
	
	public Date getLastSavedDate() { return lastSavedDate; }

	public List<Label> getLabelList() {
		return labelList;
	}

	public void setLabelList(List<Label> labelList) {
		this.labelList = labelList;
	}

	public List<Area> getObstacleList() {
		return obstacleList;
	}

	public void setObstacleList(List<Area> obstacleList) {
		this.obstacleList = obstacleList;
	}

	public void setNetwork(WirelessNetwork network) {
		this.network = network;
	}

	public String getSelectedRoutingProtocol() {
		return selectedRoutingProtocol;
	}

	public void setSelectedRoutingProtocol(String selectedRoutingProtocol) {
		this.selectedRoutingProtocol = selectedRoutingProtocol;
	}

	public String getSelectedTransportProtocol() {
		return selectedTransportProtocol;
	}

	public void setSelectedTransportProtocol(String selectedTransportProtocol) {
		this.selectedTransportProtocol = selectedTransportProtocol;
	}

	public String getSelectedApplicationProtocol() {
		return selectedApplicationProtocol;
	}

	public void setSelectedApplicationProtocol(String selectedApplicationProtocol) {
		this.selectedApplicationProtocol = selectedApplicationProtocol;
	}

	public String getSelectedLinkLayer() {
		return selectedLinkLayer;
	}

	public void setSelectedLinkLayer(String selectedLinkLayer) {
		this.selectedLinkLayer = selectedLinkLayer;
	}

	public String getSelectedMac() {
		return selectedMac;
	}

	public void setSelectedMac(String selectedMac) {
		this.selectedMac = selectedMac;
	}

	public String getSelectedChannel() {
		return selectedChannel;
	}

	public void setSelectedChannel(String selectedChannel) {
		this.selectedChannel = selectedChannel;
	}

	public String getSelectedPropagationModel() {
		return selectedPropagationModel;
	}

	public void setSelectedPropagationModel(String selectedPropagationModel) {
		this.selectedPropagationModel = selectedPropagationModel;
	}

	public String getSelectedAntenna() {
		return selectedAntenna;
	}

	public void setSelectedAntenna(String selectedAntenna) {
		this.selectedAntenna = selectedAntenna;
	}

	public String getSelectedInterfaceQueue() {
		return selectedInterfaceQueue;
	}

	public void setSelectedInterfaceQueue(String selectedInterfaceQueue) {
		this.selectedInterfaceQueue = selectedInterfaceQueue;
	}
	
	
	public String getSelectedNetworkInterface() {
		return selectedNetworkInterface;
	}

	public void setSelectedNetworkInterface(String selectedNetworkInterface) {
		this.selectedNetworkInterface = selectedNetworkInterface;
	}

	public void addObstacle(Area area) {
		area.setId(obstacleIndex++);		
		obstacleList.add(area);
	}
}
