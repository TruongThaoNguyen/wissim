package controllers.managers;

import java.awt.Color;
import java.io.IOException;
import java.util.HashMap;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;

import controllers.helpers.Helper;
import models.Project;
import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

/**
 * Manage tasks for general application
 * @author leecom
 *
 */
public class ApplicationSettings {
	public static final int CIRCLE = 0, SQUARE = 1;
	public static final boolean RULER_SHOW = true, RULER_HIDE = false;
	
	// configuration path of application
	public static String SETTINGS_PATH = ApplicationSettings.class.getResource("settings.xml").getPath();
	public static String GRAPHICS_SETTINGS_PATH = ApplicationSettings.class.getResource("graphics-settings.xml").getPath();
	
	private static Document settingsDoc;
	
	// application variables for settings
	public static Point networkSize;
	public static int nodeRange;
	public static int queueLength;
	public static HashMap<String, HashMap<String, String>> routingProtocols = new HashMap<String, HashMap<String, String>>();
	public static StringBuilder defaultRoutingProtocol = new StringBuilder();
	public static HashMap<String, HashMap<String, String>> transportProtocols = new HashMap<String, HashMap<String, String>>();
	public static StringBuilder defaultTransportProtocol = new StringBuilder();
	public static HashMap<String, HashMap<String, String>> applicationProtocols = new HashMap<String, HashMap<String, String>>();
	public static StringBuilder defaultApplicationProtocol = new StringBuilder();	
	public static HashMap<String, HashMap<String, String>> linkLayers = new HashMap<String, HashMap<String, String>>();
	public static StringBuilder defaultLinkLayer = new StringBuilder();
	public static HashMap<String, HashMap<String, String>> macs = new HashMap<String, HashMap<String, String>>();
	public static StringBuilder defaultMac = new StringBuilder();
	public static HashMap<String, HashMap<String, String>> channels = new HashMap<String, HashMap<String, String>>();
	public static StringBuilder defaultChannel = new StringBuilder();
	public static HashMap<String, HashMap<String, String>> propagationModels = new HashMap<String, HashMap<String, String>>();
	public static StringBuilder defaultPropagationModel = new StringBuilder();
	public static HashMap<String, HashMap<String, String>> networkInterfaces = new HashMap<String, HashMap<String, String>>();
	public static StringBuilder defaultNetworkInterface = new StringBuilder();
	public static HashMap<String, HashMap<String, String>> antennas = new HashMap<String, HashMap<String, String>>();
	public static StringBuilder defaultAntenna = new StringBuilder();
	public static HashMap<String, HashMap<String, String>> interfaceQueues = new HashMap<String, HashMap<String, String>>();
	public static StringBuilder defaultInterfaceQueue = new StringBuilder();
	public static double iddleEnergy;
	public static double receptionEnergy;
	public static double transmissionEnergy;
	public static double sleepEnergy;
	
	// application variables for graphics settings
	public static Color backgroundColor;	
	public static Color nodeColor;
	public static int nodeSize;
	public static int nodeBorderType;	
	public static Color greedyPathColor;
	public static int greedyPathThickness;
	public static Color shortestPathColor;
	public static int shortestPathThickness;
	public static Color userDefinedPathColor;
	public static int userDefinedPathThickness;
	public static Color obstacleBackgroundColor;
	public static int obstacleBorderThickness;
	public static boolean isRulerShown;
	
	private static final Color defaultBackgroundColor = Helper.hex2Rgb("#ffffff");
	private static final Color defaultNodeColor = Helper.hex2Rgb("#dcdcdc");
	private static final int defaultNodeSize = 10;
	private static final int defaultNodeBorderType = ApplicationSettings.CIRCLE;
	private static final Color defaultGreedyPathColor = Helper.hex2Rgb("#660033");
	private static final int defaultGreedyPathThickness = 2;
	private static final Color defaultShortestPathColor = Helper.hex2Rgb("#330066");
	private static final int defaultShortestPathThickness = 2;
	private static final Color defaultUserDefinedPathColor = Helper.hex2Rgb("#ff3d3d");
	private static final int defaultUserDefinedPathThickness = 2;
	private static final Color defaultObstacleBackgroundColor = Helper.hex2Rgb("#cc9999");
	private static final int defaultObstacleBorderThickness = 1;
	private static final boolean defaultIsRulerShown = ApplicationSettings.RULER_SHOW;

	/**
	 * Load application configuration
	 * @throws IOException 
	 * @throws ParsingException 
	 * @throws ValidityException 
	 */
	public static void loadConfig() throws ValidityException, ParsingException, IOException {
		settingsDoc = loadNetworkConfig();
		loadGraphicConfig();
	}
	
	public static void saveConfig() throws IOException {
		saveNetworkConfig();
		saveGraphicConfig();
	}
	
	public static void saveNetworkConfig() throws IOException {
		Element root = settingsDoc.getRootElement();
		
		// sets default node range
		Element eNodeSettings = root.getFirstChildElement("node-settings");
		Element eRange = eNodeSettings.getFirstChildElement("range");
		eRange.removeChildren();
		eRange.appendChild(nodeRange + "");	
		
		
		// sets default queue length
		Element eQueueLength = eNodeSettings.getFirstChildElement("queue-length");
		eQueueLength.removeChildren();
		eQueueLength.appendChild(queueLength + "");
		
		// sets default energy-model
		Element eEnergyModel = root.getFirstChildElement("energy-model");
		Element eIddle = eEnergyModel.getFirstChildElement("iddle");
		eIddle.removeChildren();
		eIddle.appendChild(iddleEnergy + "");		
		Element eReception = eEnergyModel.getFirstChildElement("reception");
		eReception.removeChildren();
		eReception.appendChild(receptionEnergy + "");
		Element eTransmission = eEnergyModel.getFirstChildElement("transmission");
		eTransmission.removeChildren();
		eTransmission.appendChild(transmissionEnergy + "");		
		Element eSleep = eEnergyModel.getFirstChildElement("sleep");
		eSleep.removeChildren();
		eSleep.appendChild(sleepEnergy + "");			
		
		// sets default protocols
		setDefaultProtocol(root, "routing-protocols", defaultRoutingProtocol);
		setDefaultProtocol(root, "transport-protocols", defaultTransportProtocol);
		setDefaultProtocol(root, "apps", defaultApplicationProtocol);
		setDefaultProtocol(root, "link-layers", defaultLinkLayer);
		setDefaultProtocol(root, "macs", defaultMac);
		setDefaultProtocol(root, "channels", defaultChannel);
		setDefaultProtocol(root, "propagation-models", defaultPropagationModel);
		setDefaultProtocol(root, "network-interfaces", defaultNetworkInterface);
		setDefaultProtocol(root, "antennas", defaultAntenna);
		setDefaultProtocol(root, "interface-queues", defaultInterfaceQueue);
		
		Parser.saveXml(settingsDoc, SETTINGS_PATH);
	}

	public static void saveGraphicConfig() throws IOException {
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
		
		Parser.saveXml(doc, GRAPHICS_SETTINGS_PATH);
	}

	private static void loadGraphicConfig() throws ValidityException, ParsingException, IOException {
		Document doc = null;
		
		try {
			doc = Parser.parse(GRAPHICS_SETTINGS_PATH);		
		} catch (Exception e) {
			try {
				doc = Parser.parse("graphics-settings.xml");
			} catch (Exception ex) {
				ex.printStackTrace();
			}
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

	private static Document loadNetworkConfig() throws ValidityException, ParsingException, IOException {
		Document doc = null;
		
		try {
			doc = Parser.parse(SETTINGS_PATH);
		} catch (Exception e) {
			try {
				doc = Parser.parse("settings.xml");
			} catch (Exception ex) {
				ex.printStackTrace();
			}
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
		Element eIddle = eEnergyModel.getFirstChildElement("iddle");
		iddleEnergy = Double.parseDouble(eIddle.getValue());
		Element eReception = eEnergyModel.getFirstChildElement("reception");
		receptionEnergy = Double.parseDouble(eReception.getValue());
		Element eTransmission = eEnergyModel.getFirstChildElement("transmission");
		transmissionEnergy = Double.parseDouble(eTransmission.getValue());
		Element eSleep = eEnergyModel.getFirstChildElement("sleep");
		sleepEnergy = Double.parseDouble(eSleep.getValue());
		
		// gets default protocols
		getProtocols(root, "routing-protocols", routingProtocols, defaultRoutingProtocol);		
		getProtocols(root, "transport-protocols", transportProtocols, defaultTransportProtocol);			
		getProtocols(root, "apps", applicationProtocols, defaultApplicationProtocol);		
		getProtocols(root, "link-layers", linkLayers, defaultLinkLayer);
		getProtocols(root, "macs", macs, defaultMac);
		getProtocols(root, "channels", channels, defaultChannel);
		getProtocols(root, "propagation-models", propagationModels, defaultPropagationModel);
		getProtocols(root, "network-interfaces", networkInterfaces, defaultNetworkInterface);
		getProtocols(root, "antennas", antennas, defaultAntenna);
		getProtocols(root, "interface-queues", interfaceQueues, defaultInterfaceQueue);
		
		return doc;
	}
	
	private static void getProtocols(Element root, String type, HashMap<String, HashMap<String, String>> protocols, StringBuilder defaultProtocol) {		
		Element eProtocols = root.getFirstChildElement(type);
		Elements ps = eProtocols.getChildElements();
		for (int i = 0; i < ps.size(); i++) {
			Element p = ps.get(i);
			
			String pType = p.getAttributeValue("type");
			if (p.getAttributeValue("default") != null && p.getAttributeValue("default").equals("true"))
				defaultProtocol.append(pType);
			
			HashMap<String, String> paramMap = new HashMap<String, String>();
			Element param = p.getFirstChildElement("params");
			if (param != null)
			{
				Elements params = param.getChildElements();			
				for (int j = 0; j < params.size(); j++) {
					Element pa = params.get(j);
					paramMap.put(pa.getAttributeValue("property"), pa.getAttributeValue("value"));
				}
			}
			
			protocols.put(pType, paramMap);
		}
	}
	
	public static void setDefaultProtocol(Element root, String type, StringBuilder defaultProtocol) {
		Element eProtocols = root.getFirstChildElement(type);
		Elements ps = eProtocols.getChildElements();
		
		for (int i = 0; i < ps.size(); i++) {
			Element p = ps.get(i);
			
			if (p.getAttributeValue("default") != null && p.getAttributeValue("default").equals("true"))
				p.removeAttribute(p.getAttribute("default"));
			
			if (p.getAttributeValue("type") != null && p.getAttributeValue("type").equals(defaultProtocol.toString()))
				p.addAttribute(new Attribute("default", "true"));
		}
	}
	
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
