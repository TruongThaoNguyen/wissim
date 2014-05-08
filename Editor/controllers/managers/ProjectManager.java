package controllers.managers;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

import controllers.converter.Converter;
import controllers.converter.shadow.SNode;

//import org.apache.pdfbox.pdmodel.PDDocument;
//import org.apache.pdfbox.pdmodel.PDPage;
//import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
//import org.apache.pdfbox.pdmodel.font.PDType1Font;
//import org.apache.pdfbox.pdmodel.graphics.xobject.PDXObjectImage;
//import org.apache.pdfbox.pdmodel.graphics.xobject.PDJpeg;




















import models.Project;
import models.managers.Parser;
import models.managers.ScriptGenerator;
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
import nu.xom.ParsingException;
import nu.xom.ValidityException;

/**
 * Acts as a proxy between models and the program
 * @author leecom
 *
 */
public class ProjectManager {
	public static final int IMAGE_SMALL_SIZE = 600, IMAGE_MEDIUM_SIZE = 800, IMAGE_LARGE_SIZE = 1000;
	public static final int TCL = 0, OMNET = 1;
	
	// list of projects available in the program
	public static Project project = null;
	public static Project getProject() {
		return project;
	}

	/**
	 *  Create initial project with specified path and network
	 * @param path Path of project
	 * @param name Name of network
	 * @param width Network width
	 * @param length Network length
	 * @param time Total network simualtion time
	 * @return The created project
	 * @throws IOException Path not found or not valid
	 */
	public static Project createProject(String path, String name, int width, int length, int time) throws IOException {
		// checks input validity
		if (width <= 0 || length <= 0 || time <= 0)
			return null;
		
		String defaulscript = Converter.defaultScript();
		
		Project project = Converter.CTD(defaulscript);
		
		
		// create intial wireless network
		WirelessNetwork network = new WirelessNetwork(name, time, width, length);
		
		// create project
		Project project = new Project(path, network);

		
		// save project
		saveProject(project);		
		
		return project;
	}

	public static boolean saveProject(Project project) throws IOException {		
		return Parser.saveProject(project);
	}
	
	public static Project loadProject(String path) throws ValidityException, ParsingException, IOException {
		Project project = Parser.loadProject(path);		
		
		return project;
	}
	
	public static boolean changeTime(Project project, int time) {
		if (time <= 0) return false;
		
		project.getNetwork().setTime(time);
		
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

	public static WirelessNode createSingleNode(Project project, int x, int y, int range) {
		WirelessNetwork network = project.getNetwork();
		
		if (network == null) return null;		
		if (x < 0 || x > network.getWidth() || y < 0 || y > network.getLength() || range < 0) return null;
		
		// check if the location is inside a hole
		if(project.getObstacleList() != null)
		for (Area obstacle : project.getObstacleList()) {
			if (obstacle.contains(x, y))
				return null;
		}
		
		// check existing node at (x, y)
		List<Node> nodeList = network.getNodeList();
		for (int i = 0; i < nodeList.size(); i++) {
			WirelessNode w = (WirelessNode) nodeList.get(i);
			
			if (w.getX() == x && w.getY() == y)
				return null;
		}			
		
		// create new node and add to network
//		return new SNode((SNetwork)network, x, y, range);
		//return new WirelessNode(network, x, y, range);
		WirelessNode wn = (WirelessNode) network.addNode(x, y, range);
		return wn;
	}
	
	public static WirelessNode createARandomNode(Project project) {
		WirelessNetwork network = project.getNetwork();		
		if (network == null) return null;
		
		Random rand = new Random();		
		return ProjectManager.createSingleNode(project, rand.nextInt(network.getWidth()), rand.nextInt(network.getLength()), project.getNodeRange());
	}
	
	public static boolean createRandomNodes(Project project, int numOfNodes, int range, Area area) {
		if (range == 0) range = 40;

		WirelessNetwork nw = project.getNetwork();
		
		if (nw == null) return false;
		
		Random rand = new Random();
		
		int i = 0;
		while (i < numOfNodes) {
			WirelessNode wnode = null;
			
			if (area == null)
				wnode = ProjectManager.createSingleNode(project, rand.nextInt(nw.getWidth()), rand.nextInt(nw.getLength()), range);
			else {
				int x = rand.nextInt(area.getBounds().width) + area.getBounds().x;
				int y = rand.nextInt(area.getBounds().height) + area.getBounds().y;
				
				if (area.contains(x, y))
					wnode = ProjectManager.createSingleNode(project, x, y, range);
			}
			
			if (wnode != null)
				i++;
		}
		
		return true;
	}
	
	public static boolean createGridNodes(Project project, Area area, int gSizeX, int gSizeY, int range) {
		//TODO handle area
		
		WirelessNetwork nw = project.getNetwork();
		
		if (nw == null) return false;
		
		int x = gSizeX, y = gSizeY;
		
		while (y < nw.getLength()) {
			while (x < nw.getWidth()) {
				ProjectManager.createSingleNode(project, x, y, range);				
				x+= gSizeX;
			}
			y += gSizeY;
		}
		
		return true;
	}
	
	public static boolean createEvent(WirelessNode node, int eventType, int raisedTime) { 
		switch (eventType) {
		case NodeEvent.ON:
		case NodeEvent.OFF:			
			new NodeEvent(eventType, raisedTime, node);
			return true;
		default:
			return false;
		}
	}
	
	public static boolean createEvent(ApplicationProtocol app, int eventType, int raisedTime) { 
		switch(eventType) {
		case AppEvent.START:
		case AppEvent.STOP:
			new AppEvent(eventType, raisedTime, app);
			return true;
		default:
			return false;
		}
	}
	
	public static boolean deleteNode(WirelessNode node) { 
		WirelessNetwork nw = (WirelessNetwork) node.getNetwork();
		return nw.removeNode(node);
	}
	
	public static boolean deleteAllNodes(Project project) {
		WirelessNetwork network = project.getNetwork();
		
		for (int i = 0; i < network.getNodeList().size(); i++) {
			network.removeNode(network.getNodeList().get(i));
		}
		
		return true;
	}
	
	//TODO need to code Pattern and Area first
	//public static Area createObstacle(Pattern pattern, double scale) { return null; }
	
	// create Obstacle 
	public static boolean addObstacle(Project project, Area area) { return false; }
	
	public static List<Area> getObstacles(Project project) { return null; }
	
	public static List<WirelessNode> getNeighbors(WirelessNode node) { return null; }
	
	public static WirelessNode getNodeWithId(Project project, int id) { 
		WirelessNetwork nw = project.getNetwork();
		List<Node> nodeList = nw.getNodeList();
		
		for (int i = 0; i < nodeList.size(); i++) {
			if (nodeList.get(i).getId() == id)
				return (WirelessNode) nodeList.get(i);
		}
		
		return null;
	}
	
	public static boolean setLabel(Project project, WirelessNode node, String labelName) {
		if (labelName == "") return false;
		
		return true;
	}
	
	public static WirelessNode getNodeWithName(Project project, String name) { 
		WirelessNetwork nw = project.getNetwork();
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
	public static boolean changeNetworkSize(Project project, int width, int length, int wType, int lType) {
		WirelessNetwork network = project.getNetwork();
		
		// changing network size
		return network.setSize(width, length, wType, lType);
	}
	
	public static boolean generateNodeLocationData(Project project, String path, int mode) throws IOException { 
		return Parser.generateNodeLocationData(project, path, mode);
	}
	
	public static boolean checkConnectivity(Project project) { 
		if (project == null) return false;
		
		WirelessNetwork wn = project.getNetwork();
		if (wn == null || wn.getNodeList() == null || wn.getNodeList().size() == 0) return false;
		
		BFSAlgorithm algorithm = new BFSAlgorithm(wn);
		return algorithm.checkConnectivity();
	}
	
	public static boolean generateScript(Project project, String path, int type) throws FileNotFoundException {
		switch (type) {
		case TCL:
			return ScriptGenerator.generateTcl(project, path, false, false);
		case OMNET:
		default:
			return false;
		}
	}
	
	public static boolean moveNode(WirelessNode node, int newX, int newY) {
		return node.setPosition(newX, newY);
	}

	public static boolean addNewLabel(Project project, Label label) {
		List<Label> labelList = project.getLabelList();
		
		// check for existing label
		for (int i = 0; i < labelList.size(); i++) {
			if (labelList.get(i).getName().equals(label.getName()))
				return false;
		}
		
		// add new label
		project.getLabelList().add(label);
		return true;
	}
	
	public static boolean removeLabel(Project project, String labelName) {
		List<Label> labelList = project.getLabelList();
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
	
	public static void main(String[] args) {
		WirelessNetwork network = new WirelessNetwork("leecom", 200, 500, 500);
		new WirelessNode(network, 100, 100, 40);
		new WirelessNode(network, 125, 200, 40);
		new WirelessNode(network, 157, 320, 40);
		new WirelessNode(network, 11, 400, 40);
		new WirelessNode(network, 205, 376, 40);
		new WirelessNode(network, 125, 80, 40);
		
		Project project = new Project("", network);
		
		//BufferedImage image = exportNetworkToImage(project, IMAGE_SMALL_SIZE);
		
//		exportNetworkToPdf(project, "");
		
		//File file = new File("D:\\myimage.png");
//		try {
//			ImageIO.write(image, "png", file);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	
	@SuppressWarnings("unchecked")
	public static List<WirelessNode> greedyPath(Project project, WirelessNode startNode, WirelessNode endNode) {
		GreedyAlgorithm algorithm = new GreedyAlgorithm(project.getNetwork());
		algorithm.setSourceNode(startNode);
		algorithm.setDestNode(endNode);
		
		return (List<WirelessNode>) algorithm.doAlgorithm();
	}
	
	@SuppressWarnings("unchecked")
	public static List<WirelessNode> shortestPath(Project project, WirelessNode startNode, WirelessNode endNode) {
		DijikstraAlgorithm algorithm = new DijikstraAlgorithm(project.getNetwork());
		algorithm.setSourceNode(startNode);
		
		return (List<WirelessNode>) algorithm.doAlgorithm(endNode);
	}
}
