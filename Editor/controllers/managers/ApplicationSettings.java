package controllers.managers;

import java.awt.Color;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;

import controllers.Configure;
import controllers.XMLReader;
import controllers.helpers.Helper;
import models.Project;
import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

/**
 * ApplicationSettings.java
 * 
 * @Copyright (C) 2014, Sedic Laboratory, Hanoi University of Science and
 *            Technology
 * @Author Duc-Trong Nguyen
 * @Version 2.0
 */
public class ApplicationSettings {
	public static final int CIRCLE = 0, SQUARE = 1;
	public static final boolean RULER_SHOW = true, RULER_HIDE = false;
	
	private static String SETTINGS_PATH 			= Configure.getHomePath() + "/.wissim/editor/settings.xml";		
	private static String GRAPHICS_SETTINGS_PATH 	= Configure.getHomePath() + "/.wissim/editor/graphics-settings.xml";
		
	// region application variables for settings
	
	public static Point  networkSize 		= new Point(100, 100);
	public static int 	 nodeRange 			= 40;
	public static int 	 queueLength 		= 50;
	public static double iddleEnergy		= 0.0096;
	public static double receptionEnergy 	= 0.21;
	public static double transmissionEnergy = 0.025;
	public static double sleepEnergy		= 0.000648;
		
	public static StringBuilder defaultRoutingProtocol 		= new StringBuilder("Agent/GPSR");	
	public static StringBuilder defaultTransportProtocol 	= new StringBuilder("UDP");	
	public static StringBuilder defaultApplicationProtocol 	= new StringBuilder("CBR");		
	public static StringBuilder defaultLinkLayer 			= new StringBuilder("LL");	
	public static StringBuilder defaultMac 					= new StringBuilder("Mac/802_11");	
	public static StringBuilder defaultChannel 				= new StringBuilder("Channel/WirelessChannel");	
	public static StringBuilder defaultPropagationModel 	= new StringBuilder("Propagation/TwoRayGround");	
	public static StringBuilder defaultNetworkInterface 	= new StringBuilder("Phy/WirelessPhy");	
	public static StringBuilder defaultAntenna 				= new StringBuilder("Antenna/OmniAntenna");	
	public static StringBuilder defaultInterfaceQueue 		= new StringBuilder("Queue/DropTail/PriQueue");
	
	@SuppressWarnings("serial") public static HashMap<String, LinkedHashMap<String, String>> routingProtocols 		= new HashMap<String, LinkedHashMap<String, String>>() {{ put("Agent/GPSR",				new LinkedHashMap<String, String>()); }};
	@SuppressWarnings("serial")	public static HashMap<String, LinkedHashMap<String, String>> transportProtocols		= new HashMap<String, LinkedHashMap<String, String>>() {{ put("UDP", 						new LinkedHashMap<String, String>()); }};
	@SuppressWarnings("serial")	public static HashMap<String, LinkedHashMap<String, String>> applicationProtocols 	= new HashMap<String, LinkedHashMap<String, String>>() {{ put("CBR", 						new LinkedHashMap<String, String>()); }};
	@SuppressWarnings("serial")	public static HashMap<String, LinkedHashMap<String, String>> linkLayers 			= new HashMap<String, LinkedHashMap<String, String>>() {{ put("LL", 						new LinkedHashMap<String, String>()); }};
	@SuppressWarnings("serial")	public static HashMap<String, LinkedHashMap<String, String>> macs 					= new HashMap<String, LinkedHashMap<String, String>>() {{ put("Mac/802_11",				new LinkedHashMap<String, String>()); }};
	@SuppressWarnings("serial")	public static HashMap<String, LinkedHashMap<String, String>> channels 				= new HashMap<String, LinkedHashMap<String, String>>() {{ put("Channel/WirelessChannel",	new LinkedHashMap<String, String>()); }};
	@SuppressWarnings("serial")	public static HashMap<String, LinkedHashMap<String, String>> propagationModels 		= new HashMap<String, LinkedHashMap<String, String>>() {{ put("Propagation/TwoRayGround", new LinkedHashMap<String, String>()); }};
	@SuppressWarnings("serial")	public static HashMap<String, LinkedHashMap<String, String>> networkInterfaces 		= new HashMap<String, LinkedHashMap<String, String>>() {{ put("Phy/WirelessPhy", 			new LinkedHashMap<String, String>()); }};
	@SuppressWarnings("serial")	public static HashMap<String, LinkedHashMap<String, String>> antennas 				= new HashMap<String, LinkedHashMap<String, String>>() {{ put("Antenna/OmniAntenna", 		new LinkedHashMap<String, String>()); }};
	@SuppressWarnings("serial")	public static HashMap<String, LinkedHashMap<String, String>> interfaceQueues 		= new HashMap<String, LinkedHashMap<String, String>>() {{ put("Queue/DropTail/PriQueue", 	new LinkedHashMap<String, String>()); }};
	
	// endregion application variables for settings
	
	// region application variables for graphics settings
	
	public static int		nodeSize 					= 10;
	public static int 		nodeBorderType 				= ApplicationSettings.CIRCLE;
	public static int 		greedyPathThickness 		= 2;
	public static int 		shortestPathThickness	 	= 2;
	public static int 		userDefinedPathThickness	= 2;
	public static int 		obstacleBorderThickness 	= 1;
	public static Color 	backgroundColor 			= Helper.hex2Rgb("#ffffff");
	public static Color 	nodeColor 					= Helper.hex2Rgb("#dcdcdc");	
	public static Color 	greedyPathColor 			= Helper.hex2Rgb("#660033");	
	public static Color 	shortestPathColor 			= Helper.hex2Rgb("#330066");	
	public static Color 	userDefinedPathColor 		= Helper.hex2Rgb("#ff3d3d");	
	public static Color 	obstacleBackgroundColor 	= Helper.hex2Rgb("#cc9999");	
	public static boolean 	isRulerShown 				= ApplicationSettings.RULER_SHOW;
	
	private static final int 		defaultNodeSize 				= 10;
	private static final int 		defaultNodeBorderType 			= ApplicationSettings.CIRCLE;
	private static final int 		defaultGreedyPathThickness 		= 2;
	private static final int 		defaultShortestPathThickness 	= 2;
	private static final int 		defaultUserDefinedPathThickness	= 2;
	private static final int 		defaultObstacleBorderThickness 	= 1;
	private static final Color 		defaultBackgroundColor 			= Helper.hex2Rgb("#ffffff");
	private static final Color 		defaultNodeColor 				= Helper.hex2Rgb("#dcdcdc");	
	private static final Color 		defaultGreedyPathColor 			= Helper.hex2Rgb("#660033");	
	private static final Color 		defaultShortestPathColor 		= Helper.hex2Rgb("#330066");	
	private static final Color 		defaultUserDefinedPathColor 	= Helper.hex2Rgb("#ff3d3d");	
	private static final Color 		defaultObstacleBackgroundColor 	= Helper.hex2Rgb("#cc9999");	
	private static final boolean	defaultIsRulerShown 			= ApplicationSettings.RULER_SHOW;

	// endregion application variables for graphics settings

	
	/**
	 * Load application configuration
	 * @throws IOException 
	 * @throws ParsingException 
	 * @throws ValidityException 
	 */
	public static void loadConfig() throws Exception {
		loadGraphicConfig();
		loadNetworkConfig();		
	}
	
	/**
	 * Save application configuration
	 * @throws Exception
	 */
	public static void saveConfig() throws Exception {
		saveNetworkConfig();
		saveGraphicConfig();
	}

	
	/**
	 * Save network configuration
	 * @throws Exception
	 */
	public static void saveNetworkConfig() throws Exception {		
		Element root = new Element("network-settings");
		Document settingsDoc = new Document(root);
		
		Element e = new Element("size");
		e.addAttribute(new Attribute("x", networkSize.x + ""));
		e.addAttribute(new Attribute("y", networkSize.y + ""));
		root.appendChild(e);
		
		Element eNodeSettings = new Element("node-settings");				
		{
			// sets default node range		
			Element eRange = new Element("range");	
			eNodeSettings.appendChild(eRange);		
			eRange.appendChild(nodeRange + "");			
		
			// sets default queue length
			Element eQueueLength = new Element("queue-length");		
			eNodeSettings.appendChild(eQueueLength);		
			eQueueLength.appendChild(queueLength + "");
		}
		root.appendChild(eNodeSettings);
		
		// sets default energy-model
		Element eEnergyModel = new Element("energy-model");				
		{
			Element eIddle = new Element("iddle");
			eEnergyModel.appendChild(eIddle);		
			eIddle.appendChild(iddleEnergy + "");
			
			Element eReception = new Element("reception");
			eEnergyModel.appendChild(eReception);		
			eReception.appendChild(receptionEnergy + "");
		
			Element eTransmission = new Element("transmission");
			eEnergyModel.appendChild(eTransmission);		
			eTransmission.appendChild(transmissionEnergy + "");		
		
			Element eSleep = new Element("sleep");
			eEnergyModel.appendChild(eSleep);		
			eSleep.appendChild(sleepEnergy + "");
		}
		root.appendChild(eEnergyModel);
		
		// sets default protocols
		setDefaultProtocol(root, "routing-protocol", 	routingProtocols,		defaultRoutingProtocol);
		setDefaultProtocol(root, "transport-protocol", 	transportProtocols,		defaultTransportProtocol);
		setDefaultProtocol(root, "app", 				applicationProtocols,	defaultApplicationProtocol);
		setDefaultProtocol(root, "link-layer", 			linkLayers,				defaultLinkLayer);
		setDefaultProtocol(root, "mac", 				macs,					defaultMac);
		setDefaultProtocol(root, "channel", 			channels,				defaultChannel);
		setDefaultProtocol(root, "propagation-model", 	propagationModels,		defaultPropagationModel);
		setDefaultProtocol(root, "network-interface", 	networkInterfaces,		defaultNetworkInterface);
		setDefaultProtocol(root, "antenna", 			antennas,				defaultAntenna);
		setDefaultProtocol(root, "interface-queue", 	interfaceQueues,		defaultInterfaceQueue);
		
		XMLReader.save(settingsDoc, SETTINGS_PATH);
	}

	/**
	 * Save graphic configuration (node color, network color, label color, etc...)
	 * @throws Exception
	 */
	public static void saveGraphicConfig() throws Exception {
		Element eGraphicSettings = new Element("settings");
		Document doc = new Document(eGraphicSettings);
		
		Element eNetwork = new Element("network");
		eNetwork.addAttribute(new Attribute("bg-color", getHexValue(backgroundColor)));
		eGraphicSettings.appendChild(eNetwork);
		
		Element eNode = new Element("node");
		String type = "circle";
		switch (ApplicationSettings.nodeBorderType) {
		case ApplicationSettings.CIRCLE:
			type = "circle";
			break;
		case ApplicationSettings.SQUARE:
			type = "square";
			break;
		}		
		eNode.addAttribute(new Attribute("type", type));
		eNode.addAttribute(new Attribute("size", ApplicationSettings.nodeSize + ""));
		eNode.addAttribute(new Attribute("color", getHexValue(nodeColor)));
		eGraphicSettings.appendChild(eNode);
		
		Element eObstacle = new Element("obstacle");
		eObstacle.addAttribute(new Attribute("color", getHexValue(obstacleBackgroundColor)));
		eObstacle.addAttribute(new Attribute("border-thickness", ApplicationSettings.obstacleBorderThickness + ""));
		eGraphicSettings.appendChild(eObstacle);
		
		Element ePaths = new Element("paths");
		Element ePath = new Element("path");
		ePath.addAttribute(new Attribute("type", "greedy"));
		ePath.addAttribute(new Attribute("color", getHexValue(greedyPathColor)));
		ePath.addAttribute(new Attribute("thickness", greedyPathThickness + ""));
		ePaths.appendChild(ePath);
		
		ePath = new Element("path");
		ePath.addAttribute(new Attribute("type", "shortest"));
		ePath.addAttribute(new Attribute("color", getHexValue(shortestPathColor)));
		ePath.addAttribute(new Attribute("thickness", shortestPathThickness + ""));
		ePaths.appendChild(ePath);
		
		ePath = new Element("path");
		ePath.addAttribute(new Attribute("type", "user-defined"));
		ePath.addAttribute(new Attribute("color", getHexValue(userDefinedPathColor)));
		ePath.addAttribute(new Attribute("thickness", userDefinedPathThickness + ""));
		ePaths.appendChild(ePath);
		
		eGraphicSettings.appendChild(ePaths);
		
		XMLReader.save(doc, GRAPHICS_SETTINGS_PATH);
	}

	/**
	 * Load graphic configuration (node color, network color, label color, etc...)
	 * @throws Exception
	 */
	
	private static void loadGraphicConfig() throws Exception {
		Document doc = null;
		
		try 
		{
			doc = XMLReader.open(GRAPHICS_SETTINGS_PATH);		
		}
		catch (Exception e) 
		{
			saveGraphicConfig();
			return;
		}
		
		Element root = doc.getRootElement();
		
		Element eNetwork = root.getFirstChildElement("network");
		backgroundColor = Helper.hex2Rgb(eNetwork.getAttributeValue("bg-color"));
		
		Element eNode = root.getFirstChildElement("node");
		switch (eNode.getAttributeValue("type")) {
		case "circle":			
			nodeBorderType = ApplicationSettings.CIRCLE;
			break;
		case "square":
			nodeBorderType = ApplicationSettings.SQUARE;
			break;
		}
		nodeSize = Integer.parseInt(eNode.getAttributeValue("size"));
		nodeColor = Helper.hex2Rgb(eNode.getAttributeValue("color"));
		
		Element eObstacle = root.getFirstChildElement("obstacle");
		obstacleBackgroundColor = Helper.hex2Rgb(eObstacle.getAttributeValue("color"));
		obstacleBorderThickness = Integer.parseInt(eObstacle.getAttributeValue("border-thickness"));
		
		Element ePaths = root.getFirstChildElement("paths");
		Elements paths = ePaths.getChildElements();
		for (int i = 0; i < paths.size(); i++) {
			Element ePath = paths.get(i);
			
			switch (ePath.getAttributeValue("type")) {
			case "greedy":
				greedyPathColor = Helper.hex2Rgb(ePath.getAttributeValue("color"));
				greedyPathThickness = Integer.parseInt(ePath.getAttributeValue("thickness"));
				break;
			case "shortest":
				shortestPathColor = Helper.hex2Rgb(ePath.getAttributeValue("color"));
				shortestPathThickness = Integer.parseInt(ePath.getAttributeValue("thickness"));
				break;
			case "user-defined":
				userDefinedPathColor = Helper.hex2Rgb(ePath.getAttributeValue("color"));
				userDefinedPathThickness = Integer.parseInt(ePath.getAttributeValue("thickness"));				
				break;
			}
		}
	}

	/**
	 * Load network configuration contain size of network, range of node, application protocol, routing protocol
	 * @throws Exception
	 */
	private static void loadNetworkConfig() throws Exception {
		Document doc = null;
		
		try {
			doc = XMLReader.open(SETTINGS_PATH);
		} catch (Exception e) {
			saveNetworkConfig();
			return;
		} 
		
		Element root = doc.getRootElement();
		
		// gets default network size
		Element eSize = root.getFirstChildElement("size");
		int x = Integer.parseInt(eSize.getAttributeValue("x"));
		int y = Integer.parseInt(eSize.getAttributeValue("y"));
		networkSize = new Point(x, y);
		
		// gets default node range
		Element eNodeSettings = root.getFirstChildElement("node-settings");
		Element eRange = eNodeSettings.getFirstChildElement("range");
		nodeRange = Integer.parseInt(eRange.getValue());
		
		// gets default queue length
		Element eQueueLength = eNodeSettings.getFirstChildElement("queue-length");
		queueLength = Integer.parseInt(eQueueLength.getValue());
		
		// gets default energy-model
		Element eEnergyModel = root.getFirstChildElement("energy-model");
		iddleEnergy 		= Double.parseDouble(eEnergyModel.getFirstChildElement("iddle"			).getValue());
		receptionEnergy 	= Double.parseDouble(eEnergyModel.getFirstChildElement("reception"		).getValue());	
		transmissionEnergy 	= Double.parseDouble(eEnergyModel.getFirstChildElement("transmission"	).getValue());
		sleepEnergy 		= Double.parseDouble(eEnergyModel.getFirstChildElement("sleep"			).getValue());
		
		// gets default protocols
		getProtocols(root, "routing-protocols", 	routingProtocols, 		defaultRoutingProtocol		);		
		getProtocols(root, "transport-protocols", 	transportProtocols, 	defaultTransportProtocol	);			
		getProtocols(root, "apps", 					applicationProtocols, 	defaultApplicationProtocol	);		
		getProtocols(root, "link-layers", 			linkLayers, 			defaultLinkLayer			);
		getProtocols(root, "macs", 					macs, 					defaultMac					);
		getProtocols(root, "channels", 				channels, 				defaultChannel				);
		getProtocols(root, "propagation-models", 	propagationModels, 		defaultPropagationModel		);
		getProtocols(root, "network-interfaces", 	networkInterfaces, 		defaultNetworkInterface		);
		getProtocols(root, "antennas",				antennas, 				defaultAntenna				);
		getProtocols(root, "interface-queues", 		interfaceQueues, 		defaultInterfaceQueue		);		
	}

	/**
	 * Read protocols from xml file
	 * @param root
	 * @param type
	 * @param routingProtocols2 : all routing protocol may postible to change
	 * @param defaultProtocol : routing protocol has selected
	 */
	private static void getProtocols(Element root, String type, HashMap<String, LinkedHashMap<String, String>> routingProtocols2, StringBuilder defaultProtocol) {		
		Element eProtocols = root.getFirstChildElement(type);
		Elements ps = eProtocols.getChildElements();
		for (int i = 0; i < ps.size(); i++) 
		{
			Element p = ps.get(i);
			
			String pType = p.getAttributeValue("type");
			if (p.getAttributeValue("default") != null && p.getAttributeValue("default").equals("true"))
			{				
				defaultProtocol.delete(0, defaultProtocol.length());
				defaultProtocol.append(pType);
			}
			
			LinkedHashMap<String, String> paramMap = new LinkedHashMap<String, String>();
			Element param = p.getFirstChildElement("params");
			if (param != null)
			{
				Elements params = param.getChildElements();			
				for (int j = 0; j < params.size(); j++) {
					Element pa = params.get(j);
					paramMap.put(pa.getAttributeValue("property"), pa.getAttributeValue("value"));
				}
			}
			
			routingProtocols2.put(pType, paramMap);
		}
	}
	/**
	 * Set 
	 * @param root
	 * @param type
	 * @param routingProtocols2
	 * @param defaultProtocol
	 */
	public static void setDefaultProtocol(Element root, String type, HashMap<String, LinkedHashMap<String, String>> routingProtocols2, StringBuilder defaultProtocol) {
		Element eProtocols = new Element(type + "s");
		
		for (String key : routingProtocols2.keySet()) 
		{			
			Element e = new Element(type);
			e.addAttribute(new Attribute("type", key));
			if (defaultProtocol.toString().equals(key)) e.addAttribute(new Attribute("default", "true"));
			
			HashMap<String, String> h = routingProtocols2.get(key);
			if (h.size() > 0)
			{
				Element params = new Element("params");			
				for (String k : h.keySet())
				{
					Element p = new Element("param");
					p.addAttribute(new Attribute("property", k));
					p.addAttribute(new Attribute("value", h.get(k)));
					params.appendChild(p);
				}				
				e.appendChild(params);
			}			
			eProtocols.appendChild(e);
		}		
		root.appendChild(eProtocols);
	}
	
	/**
	 * Apply setting default for project
	 * @param project
	 */
	public static void applyDefaultSettingsToProject(Project project) {			
		Project.setAntennas(antennas);
		Project.setApplicationProtocols(applicationProtocols);
		Project.setChannels(channels);
		project.setIddleEnergy(iddleEnergy);
		Project.setInterfaceQueues(interfaceQueues);
		Project.setLinkLayers(linkLayers);
		Project.setMacs(macs);
		Project.setNetworkInterfaces(networkInterfaces);		
		Project.setPropagationModels(propagationModels);			
		Project.setRoutingProtocols(routingProtocols);		
		Project.setTransportProtocols(transportProtocols);
		
		project.setSelectedAntenna(defaultAntenna.toString());
		Project.setSelectedApplicationProtocol(defaultApplicationProtocol.toString());
		project.setSelectedChannel(defaultChannel.toString());
		project.setSelectedInterfaceQueue(defaultInterfaceQueue.toString());
		project.setSelectedLinkLayer(defaultLinkLayer.toString());
		project.setSelectedMac(defaultMac.toString());
		project.setSelectedPropagationModel(defaultPropagationModel.toString());
		project.setSelectedRoutingProtocol(defaultRoutingProtocol.toString());
		Project.setSelectedTransportProtocol(defaultTransportProtocol.toString());
		project.setSelectedNetworkInterface(defaultNetworkInterface.toString());
		
		project.setNodeRange(nodeRange);
		project.setQueueLength(queueLength);
		project.setReceptionEnergy(receptionEnergy);
		project.setSleepEnergy(sleepEnergy);
		project.setTransmissionEnergy(transmissionEnergy);
	}
	
	public static org.eclipse.swt.graphics.Color colorAWTtoSWT(Color color) {
		return new org.eclipse.swt.graphics.Color(Display.getCurrent(), color.getRed(), color.getGreen(), color.getBlue());
	}
	
	public static Color colorSWTtoAWT(org.eclipse.swt.graphics.Color color) {
		return new Color(color.getRed(), color.getGreen(), color.getBlue());
	}
	
	private static String getHexValue(Color color) {
		return Helper.rgb2Hex(color.getRed(), color.getGreen(), color.getBlue());
	}
	
	public static void restoreDefaultGraphicSettings() {
		backgroundColor = defaultBackgroundColor;
		nodeColor = defaultNodeColor;
		nodeSize = defaultNodeSize;
		nodeBorderType = defaultNodeBorderType;
		greedyPathColor = defaultGreedyPathColor;
		greedyPathThickness = defaultGreedyPathThickness;
		shortestPathColor = defaultShortestPathColor;
		shortestPathThickness = defaultShortestPathThickness;
		userDefinedPathColor = defaultUserDefinedPathColor;
		userDefinedPathThickness = defaultUserDefinedPathThickness;
		obstacleBackgroundColor = defaultObstacleBackgroundColor;
		obstacleBorderThickness = defaultObstacleBorderThickness;
		isRulerShown = defaultIsRulerShown;
	}
}
