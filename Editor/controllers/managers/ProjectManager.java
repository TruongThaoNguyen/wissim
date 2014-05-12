package controllers.managers;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Random;

import controllers.WorkSpace;
import controllers.converter.Converter;
import models.Project;
import models.converter.ParseException;
import models.networkcomponents.Node;
import models.networkcomponents.WirelessNetwork;
import models.networkcomponents.WirelessNode;
import models.networkcomponents.algorithms.BFSAlgorithm;
import models.networkcomponents.algorithms.DijikstraAlgorithm;
import models.networkcomponents.algorithms.GreedyAlgorithm;
import models.networkcomponents.events.AppEvent;
import models.networkcomponents.events.NodeEvent;
import models.networkcomponents.features.*;
import models.networkcomponents.protocols.ApplicationProtocol;

/**
 * Acts as a proxy between models and the program
 * @author leecom
 *
 */
public class ProjectManager {
	public static final int IMAGE_SMALL_SIZE = 600, IMAGE_MEDIUM_SIZE = 800, IMAGE_LARGE_SIZE = 1000;	
	
	/**
	 *  available project in the program
	 */	
	public static Project getProject() {
		return Converter.global;
	}
	
	private static WirelessNetwork getNetwork() {
		return Converter.global.getNetwork();
	}
	
	/**
	 * Create new project with default script.
	 * @param path path to store Tcl script file
	 * @param name name of project ??
	 * @param width xSize of network
	 * @param length ySize of network
	 * @param time Simulation time
	 * @return new project	
	 */
	public static Project createProject(String path, String name, int width, int length, int time) {
		// checks input validity
		if (width <= 0 || length <= 0 || time <= 0) return null;
		
		// create default script
		List<String> defaulscript = Converter.DefaultScript();
		StringBuilder script = new StringBuilder();
		for (String s : defaulscript) {
			script.append(s);
		}
		
		Project project;		
		try {
			project = Converter.CTD(script.toString());
		} catch (ParseException e) {
			project = null;
		}
				
		// Initialize wireless network
		WirelessNetwork network = project.getNetwork();
		network.setName(name);
		network.setTime(time);
		network.setWidth(width);
		network.setLength(length);		
		
		WorkSpace.setDirectory(path);
		
		// save project
		try {
			saveProject();
		} catch(IOException e) {}
				
		return project;
	}

	/**
	 * Store Tcl script to file
	 * @throws IOException
	 */
	public static void saveProject() throws IOException {				
		String fileName = WorkSpace.getDirectory() + "simulate.tcl";
		
		BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));		
		for (String string : Converter.DTC()) {
			bw.write(string);
		}
		bw.close();				
	}
	
	/**
	 * Open Tcl script file
	 * @param path path to tcl script file
	 * @return new project 
	 * @throws ParsingException
	 * @throws IOException
	 */
	public static Project loadProject(String path) throws IOException, ParseException {
		WorkSpace.setDirectory(path);		
		BufferedReader br = new BufferedReader(new FileReader(path));
		StringBuilder sb = new StringBuilder();
	    String line = br.readLine();		
		while (line != null) {
			sb.append(line);
		    sb.append(System.lineSeparator());
		    line = br.readLine();		    
		}
	    br.close();						
		return Converter.CTD(sb.toString());
	}
	
	public static boolean changeTime(int time) {
		if (time <= 0) return false;
		
		getProject().getNetwork().setTime(time);
		
		return true;
	}
	
	/**
	 * Export network to image
	 * @param project
	 * @return
	 */
	@Deprecated
	public static BufferedImage exportNetworkToImage(Project project, int imageMaxSize) {
		int actualWidth = project.getNetwork().getWidth();
		int actualLength = project.getNetwork().getLength();		
		int width = 0, height = 0;
		double scale = 0;
		
		// calculate width, height of image and the scale value 
		if (actualWidth > actualLength) {
			width = imageMaxSize;
			scale = (double) width / actualWidth; 
			height = (int) (actualLength * scale);
		} else {
			height = imageMaxSize;
			scale = (double) height / actualLength;
			width = (int) (actualWidth * scale);
		}
		
        // Constructs a BufferedImage of one of the predefined image types.
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        // Create a graphics which can be used to draw into the buffered image
        Graphics2D g2d = bufferedImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // fill all the image with white
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);
        
        //draw edge of image with black
        g2d.setColor(Color.BLACK);
        g2d.drawRect(0, 0, width-5, height-5);
        
        // draw nodes
        int x, y;
        for (Node n : project.getNetwork().getNodeList()) {
        	WirelessNode wn = (WirelessNode) n;
        	
        	// get x, y of nodes in image
        	x = (int) (wn.getX() * scale);
        	y = (int) (wn.getY() * scale);
        	
        	g2d.setColor(Color.GRAY);
        	g2d.fillOval(x - 5, y - 5, 10, 10);
        	
        	g2d.setColor(Color.BLACK);
        	g2d.drawOval(x - 5, y - 5, 10, 10);
        }
		
        // Disposes of this graphics context and releases any system resources that it is using.
        g2d.dispose();
		
        return bufferedImage;
	}

	/**
	 * Create new single node
	 * @param x 
	 * @param y
	 * @param range
	 * @return new node
	 */
	public static WirelessNode createSingleNode(int x, int y, int range) {
		WirelessNetwork network = getNetwork();
		
		if (network == null) return null;		
		if (x < 0 || x > network.getWidth() || y < 0 || y > network.getLength() || range <= 0) return null;
		
		getProject();
		// check if the location is inside a hole
		if(Project.getObstacleList() != null)
		{
			getProject();
			for (Area obstacle : Project.getObstacleList()) {
				if (obstacle.contains(x, y)) return null;
			}
		}
		
		// check existing node at (x, y)
		List<Node> nodeList = network.getNodeList();
		for (int i = 0; i < nodeList.size(); i++) {
			WirelessNode w = (WirelessNode) nodeList.get(i);
			
			if (w.getX() == x && w.getY() == y)	return null;
		}			
		
		// create new node and add to network
		return (WirelessNode) network.addNode(x, y, range);
	}

	/**
	 * Create a node with random position
	 * @param project
	 * @return
	 */
	public static WirelessNode createARandomNode() {
		WirelessNetwork network = getNetwork();		
		if (network == null) return null;
		
		Random rand = new Random();
		WirelessNode node = null;
		while (node == null) node = createSingleNode(rand.nextInt(network.getWidth()), rand.nextInt(network.getLength()), getProject().getNodeRange());							
		return node;
	}
	
	/**
	 * Create a set of nodes with random position
	 * @param numOfNodes
	 * @param range
	 * @param area
	 * @return
	 */
	public static boolean createRandomNodes(int numOfNodes, int range, Area area) {
		if (range == 0) range = getProject().getNodeRange();

		WirelessNetwork nw = getNetwork();		
		if (nw == null) return false;
		
		Random rand = new Random();		

		int i = 0;
		
		if (area == null)
		{
			while (i < numOfNodes)
			{
				WirelessNode wnode = ProjectManager.createSingleNode(rand.nextInt(nw.getWidth()), rand.nextInt(nw.getLength()), range);
				if (wnode != null) i++;
			}
		}
		else
		{
			while (i < numOfNodes)
			{
				WirelessNode wnode = null;				
				int x, y;
				do {
					x = rand.nextInt(area.getBounds().width) + area.getBounds().x;
					y = rand.nextInt(area.getBounds().height) + area.getBounds().y;
				} while (area.contains(x, y)); 
			
				wnode = ProjectManager.createSingleNode(x, y, range);
				
				if (wnode != null) i++;
			}
		}
		
		return true;
	}
	
	/**
	 * Create a set of nodes with position is in grid
	 * @param area
	 * @param gSizeX
	 * @param gSizeY
	 * @param range
	 * @return
	 */
	public static boolean createGridNodes(Area area, int gSizeX, int gSizeY, int range) {							
		WirelessNetwork nw = getNetwork();
		
		int x = gSizeX, y = gSizeY;
		
		if (area != null)
		{
			while (y < nw.getLength()) {
				while (x < nw.getWidth()) {
					if (area.contains(x, y)) ProjectManager.createSingleNode(x, y, range);				
					x+= gSizeX;
				}
				y += gSizeY;
			}
		}
		else
		{
			while (y < nw.getLength()) {
				while (x < nw.getWidth()) {
					ProjectManager.createSingleNode(x, y, range);				
					x+= gSizeX;
				}
				y += gSizeY;
			}
		}
		
		return true;
	}

	/**
	 * Create new event
	 * @param node
	 * @param eventType
	 * @param raisedTime
	 * @return
	 */
	public static boolean createEvent(WirelessNode node, int eventType, int raisedTime) { 
		switch (eventType) {
			case NodeEvent.ON:
			case NodeEvent.OFF:
				node.addEvent(eventType, raisedTime);				
				return true;
			default:
				return false;
		}
	}

	/**
	 * Create new event
	 * @param app
	 * @param eventType
	 * @param raisedTime
	 * @return
	 */
	public static boolean createEvent(ApplicationProtocol app, int eventType, int raisedTime) { 
		switch(eventType) {
			case AppEvent.START:
			case AppEvent.STOP:
				app.addEvent(eventType, raisedTime);				
				return true;
			default:
				return false;
		}
	}
	
	public static boolean deleteNode(WirelessNode node) { 
		WirelessNetwork nw = (WirelessNetwork) node.getNetwork();
		return nw.removeNode(node);
	}
	
	public static boolean deleteAllNodes() {
		// TODO: create new method to remove all nodes to improve performance
		
		WirelessNetwork network = getNetwork();
		
		for (int i = 0; i < network.getNodeList().size(); i++) {
			network.removeNode(network.getNodeList().get(i));
		}
		
		return true;
	}
	
	//TODO need to code Pattern and Area first
	//public static Area createObstacle(Pattern pattern, double scale) { return null; }
	
	// create Obstacle 
	public static boolean addObstacle(Area area) { return false; }
	
	public static List<Area> getObstacles() { getProject();
	return Project.getObstacleList(); }
	
	public static List<WirelessNode> getNeighbors(WirelessNode node) { return node.getNeighborList(); }
	
	public static WirelessNode getNodeWithId(int id) { 
		WirelessNetwork nw = getNetwork();
		List<Node> nodeList = nw.getNodeList();
		
		for (int i = 0; i < nodeList.size(); i++) {
			if (nodeList.get(i).getId() == id)
				return (WirelessNode) nodeList.get(i);
		}
		
		return null;
	}
	
	public static boolean setLabel(Project project, WirelessNode node, String labelName) {
		if (labelName == "") return false;
		node.setName(labelName);
		return true;
	}
	
	public static WirelessNode getNodeWithName(String name) { 
		WirelessNetwork nw = getNetwork();
		List<Node> nodeList = nw.getNodeList();
		
		for (int i = 0; i < nodeList.size(); i++) {
			if (nodeList.get(i).getName() == name)
				return (WirelessNode) nodeList.get(i);
		}
		
		return null;
	}
	
	/**
	 * Change the network size. There are total 9 types of changing, based on what direction it needs to adapt.
	 * Nodes that are out of network will be deleted. The labels of project will be affected, too.
	 * The default type is (wType, lType) = (right, bottom)
	 * @param project The current working project
	 * @param width The new width of network
	 * @param length The new length of network
	 * @param wType Options for width changing: extend(trim) to left, center or right 
	 * @param lType Options for length changing: extend(trin) to top, center or bottom
	 * @return
	 */
	public static boolean changeNetworkSize(int width, int length, int wType, int lType) {
		WirelessNetwork network = getNetwork();
		
		// changing network size
		return network.setSize(width, length, wType, lType);
	}
	
	public static boolean checkConnectivity() { 
		if (getProject() == null) return false;
		
		WirelessNetwork wn = getNetwork();
		if (wn == null || wn.getNodeList() == null || wn.getNodeList().size() == 0) return false;
		
		BFSAlgorithm algorithm = new BFSAlgorithm(wn);
		return algorithm.checkConnectivity();
	}
	
	public static boolean moveNode(WirelessNode node, int newX, int newY) {
		return node.setPosition(newX, newY);
	}

	public static boolean addNewLabel(Label label) {
		getProject();
		List<Label> labelList = Project.getLabelList();
		
		// check for existing label
		for (int i = 0; i < labelList.size(); i++) {
			if (labelList.get(i).getName().equals(label.getName()))
				return false;
		}
		
		getProject();
		// add new label
		Project.getLabelList().add(label);
		return true;
	}
	
	public static boolean removeLabel(String labelName) {
		getProject();
		List<Label> labelList = Project.getLabelList();
		Label lbl = null;
		
		// check for existing label
		for (int i = 0; i < labelList.size(); i++) {
			if (labelList.get(i).getName().equals(labelName)) {				
				lbl = labelList.get(i);
				break;
			}
		}		
		
		if (lbl == null)
			return false;
		
		labelList.remove(lbl);
		return true;
	}
	
	public static boolean setNodeLabel(WirelessNode node, Label label) {
		if (label == null) return false;
		
		for (int i = 0; i < label.getNodeList().size(); i++) {
			if (label.getNodeList().contains(node))
				return false;
		}
		
		return label.getNodeList().add(node);
	}

	@SuppressWarnings("unchecked")
	public static List<WirelessNode> greedyPath(WirelessNode startNode, WirelessNode endNode) {
		GreedyAlgorithm algorithm = new GreedyAlgorithm(getNetwork());
		algorithm.setSourceNode(startNode);
		algorithm.setDestNode(endNode);
		
		return (List<WirelessNode>) algorithm.doAlgorithm();
	}
	
	@SuppressWarnings("unchecked")
	public static List<WirelessNode> shortestPath(WirelessNode startNode, WirelessNode endNode) {
		DijikstraAlgorithm algorithm = new DijikstraAlgorithm(getNetwork());
		algorithm.setSourceNode(startNode);
		
		return (List<WirelessNode>) algorithm.doAlgorithm(endNode);
	}
}
