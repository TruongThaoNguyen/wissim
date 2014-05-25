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
	private static List<Label> labelList = new ArrayList<Label>();
	
	/**
	 * list of obstacles
	 */
	private static List<Area> obstacleList = new ArrayList<Area>();
	private static int obstacleIndex;
	
	/**
	 * Transport protocol
	 */
	private static String transportProtocol;
	private static String applicationProtocol;
	
	public static void setSelectedApplicationProtocol(String value)	{ applicationProtocol 	= value; }
	public static void setSelectedTransportProtocol(String value)	{ transportProtocol		= value; }
	
	public static void setPath(String value) 						{ path 					= value; }
	public static void setCreatedDate(Date value)					{ createdDate 			= value; }
	public static void setLastSavedDate(Date value) 				{ lastSavedDate 		= value; }
	public static void setLabelList(List<Label> value) 				{ labelList 			= value; }
	public static void setObstacleList(List<Area> value)			{ obstacleList 			= value; }
					
	public static String 		getSelectedApplicationProtocol(){ return applicationProtocol; }
	public static String 		getSelectedTransportProtocol()	{ return transportProtocol; }	
	public static String		getPath() 						{ return path; }
	public static Date 			getCreatedDate() 				{ return createdDate; }
	public static Date 			getLastSavedDate() 				{ return lastSavedDate; }
	public static List<Label>	getLabelList() 					{ return labelList; }
	public static List<Area> 	getObstacleList()			 	{ return obstacleList; }

	public static void addObstacle(Area area) {
		area.setId(obstacleIndex++);
		obstacleList.add(area);
	}
	
	// endregion Manager properties

	// region ------------------- Configure properties ------------------- //

	public static HashMap<String, HashMap<String, HashMap<String, String>>> configure = new HashMap<String, HashMap<String, HashMap<String, String>>>(); 
		
	public static void setRoutingProtocols		(HashMap<String, HashMap<String, String>> ps) { configure.put("-adhocRouting", 			ps); }		
	public static void setLinkLayers			(HashMap<String, HashMap<String, String>> ps) { configure.put("-llType", 				ps); }	
	public static void setMacs					(HashMap<String, HashMap<String, String>> ps) { configure.put("-macType",				ps); }
	public static void setPropagationModels		(HashMap<String, HashMap<String, String>> ps) { configure.put("-propType",				ps); }
	public static void setNetworkInterfaces		(HashMap<String, HashMap<String, String>> ps) { configure.put("-phyType",				ps); }
	public static void setAntennas				(HashMap<String, HashMap<String, String>> ps) { configure.put("-antType",				ps); }
	public static void setInterfaceQueues		(HashMap<String, HashMap<String, String>> ps) { configure.put("-ifqType",				ps); }
	public static void setChannels				(HashMap<String, HashMap<String, String>> ps) { configure.put("-channel",				ps); }
	public static void setTransportProtocols	(HashMap<String, HashMap<String, String>> ps) { configure.put("transportProtocols", 	ps); }
	public static void setApplicationProtocols	(HashMap<String, HashMap<String, String>> ps) { configure.put("applicationProtocols",	ps); }
		
	public static HashMap<String, HashMap<String, String>> getConfig(String label) 	{ return configure.get(label); }
	
	public static HashMap<String, HashMap<String, String>> getRoutingProtocols() 	{ return configure.get("-adhocRouting");		}//
	public static HashMap<String, HashMap<String, String>> getLinkLayers() 			{ return configure.get("-llType");				}//
	public static HashMap<String, HashMap<String, String>> getMacs() 				{ return configure.get("-macType"); 			}//
	public static HashMap<String, HashMap<String, String>> getPropagationModels() 	{ return configure.get("-propType");			}//
	public static HashMap<String, HashMap<String, String>> getNetworkInterfaces() 	{ return configure.get("-phyType");				}//
	public static HashMap<String, HashMap<String, String>> getAntennas() 			{ return configure.get("-antType");				}//
	public static HashMap<String, HashMap<String, String>> getInterfaceQueues() 	{ return configure.get("-ifqType");				}//
	public static HashMap<String, HashMap<String, String>> getChannels()			{ return configure.get("-channel"); 			}//
	public static HashMap<String, HashMap<String, String>> getTransportProtocols()	{ return configure.get("transportProtocols");	}//
	public static HashMap<String, HashMap<String, String>> getApplicationProtocols(){ return configure.get("applicationProtocols");	}//
	
	// endregion Configure properties
	
	// region ------------------- Network properties ------------------- //

	public abstract WirelessNetwork getNetwork();

	protected int nodeRange = 40;
	public int getNodeRange() { return nodeRange; } 
	
	public abstract void setNodeRange			(int value);	
	public abstract void setQueueLength			(int queueLength);	
	public abstract void setIddleEnergy			(double iddleEnergy);
	public abstract void setReceptionEnergy		(double receptionEnergy);
	public abstract void setSleepEnergy			(double sleepEnergy);	
	public abstract void setTransmissionEnergy	(double transmissionEnergy);
	public abstract void setInitialEnergy		(double initialEnergy);
	
	public abstract int		getQueueLength(); //		throws ParseException;
	public abstract double 	getSleepEnergy(); //		throws ParseException;
	public abstract double 	getTransmissionEnergy(); //	throws ParseException;		
	public abstract double	getIddleEnergy(); //		throws ParseException;
	public abstract double 	getReceptionEnergy(); //	throws ParseException;
	public abstract double	getInitialEnergy(); //		throws ParseException;
	
	public abstract void setSelectedRoutingProtocol(	String selected);
	public abstract void setSelectedLinkLayer(			String selected);
	public abstract void setSelectedMac(				String selected);
	public abstract void setSelectedAntenna(			String selected);
	public abstract void setSelectedChannel(			String selected);
	public abstract void setSelectedPropagationModel(	String selected);
	public abstract void setSelectedInterfaceQueue(		String selected);
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