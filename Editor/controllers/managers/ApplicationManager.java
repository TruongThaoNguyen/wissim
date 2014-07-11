package controllers.managers;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import models.Project;
import models.DialogResult.CreateNodeSetResult;
import models.DialogResult.CreateProjectResult;
import models.DialogResult.CreateSingleNodeResult;
import models.DialogResult.EditNetworkSizeResult;
import models.DialogResult.ImportLocationDataResult;
import models.DialogResult.NodeLocationResult;
import models.converter.ParseException;
import models.networkcomponents.Node;
import models.networkcomponents.WirelessNode;
import models.networkcomponents.features.Area;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.printing.PrintDialog;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.printing.PrinterData;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;

import controllers.Configure;
import controllers.graphicscomponents.GGroup;
import controllers.graphicscomponents.GObstacle;
import controllers.graphicscomponents.GSelectableObject;
import controllers.graphicscomponents.GWirelessNode;
import controllers.graphicscomponents.GraphicPath;
import controllers.helpers.Helper;
import views.Editor;
import views.SearchNodeDialog;
import views.Workspace;
import views.dialogs.CreateNodeDialog;
import views.dialogs.CreateNodeSetDialog;
import views.dialogs.CreateProjectDialog;
import views.dialogs.EditNetworkSizeDialog;
import views.dialogs.ExportImageDialog;
import views.dialogs.ImportLocationDataDialog;
import views.dialogs.LabelDialog;
import views.dialogs.NetworkPropertiesDialog;
import views.dialogs.NodeLocationDialog;
import views.dialogs.NodePropertiesDialog;
import views.dialogs.TrafficFlowManagerDialog;
import views.dialogs.ViewPathInfo;
import views.helpers.PrintHelper;

/**
 * ApplicationManager.java
 * 
 * @Copyright (C) 2014, Sedic Laboratory, Hanoi University of Science and
 *            Technology
 * @Author Duc-Trong Nguyen
 * @Version 2.0
 */
public class ApplicationManager {
	private static int newProjectCount = 1;
	
	/**
	 * Create new project
	 * @param editor : Editor that project belong to
	 * @return a project simulation
	 */
	public static Project newProject(Editor editor) {
		String defaultName = "Untitled" + newProjectCount;
		CreateProjectResult result = (CreateProjectResult)new CreateProjectDialog(editor.getShell(), SWT.SHEET, defaultName).open();				
		
		if (result == null) return null;
		
		// set up path to save file		
		File dir = new File(result.path);
		if (!dir.exists()) dir.mkdir();		
		
		String path = Configure.getFilePath(result.path, Helper.getFileNameWithExt(result.name, "tcl"));
		File f = new File(path);
		
		boolean confirmed = true;
		
		if (f.exists() == true)	confirmed = MessageDialog.openQuestion(editor.getShell(), "File Existed", "File existed in the selected directory. Do you want to override the existing file by the new one?");

		if (confirmed == true) 
		{
			Configure.setTclFile(path);												
			Project project = null;
			try 
			{
				project = ProjectManager.createProject(path, Helper.getFileNameWithoutExt(result.name, "tcl"), result.width, result.height, result.time);
			}
			catch (ParseException e) 
			{
				MessageDialog.openError(editor.getShell(), "line " + e.getLine(), e.getMessage());				
			}
			ApplicationSettings.applyDefaultSettingsToProject(project);
			newProjectCount++;			
			return project;
		}
					
		return null;
	}

	/**
	 * Open project from tcl scenario
	 * @param mainWindow 
	 * @return Project simulation
	 */
	public static Project openProject(Editor mainWindow) {
		// open Open Dialog
		FileDialog openDialog = new FileDialog(mainWindow.getShell(), SWT.OPEN);
		openDialog.setText("Open");
		openDialog.setFilterPath(Configure.getHomePath());
		String[] filterExt = { "*.tcl" };
		openDialog.setFilterExtensions(filterExt);
		openDialog.setFilterNames(new String[] { "Tcl Scripts" });

		String path = openDialog.open();
		if (path == null) return null;		

		try {								
			Configure.setTclFile(path);
			Project project = ProjectManager.loadProject(path);
			ApplicationSettings.applyDefaultSettingsToProject(project);
			return project;
		} catch (Exception e) {}
		
		return null;
	}

	/**
	 * Save project or scenario
	 * @param mainWindow
	 * @throws IOException
	 * @throws ParseException
	 */
	public static void saveWorkspace(Editor mainWindow) throws IOException, ParseException {		
		ProjectManager.saveProject();			
	}

	/**
	 * Save project to inform path
	 * @param workspace
	 */
	public static void saveWorkspaceAs(Workspace workspace) {	
		if (workspace == null) return;
		FileDialog saveDialog = new FileDialog(workspace.getShell(), SWT.SAVE);
		saveDialog.setText("Save");
		saveDialog.setFileName(Workspace.getProject().getNetwork().getName());
		saveDialog.setFilterExtensions(new String[] { "*.wis" });
		saveDialog.setFilterNames(new String[] {"Wissim Project"});
		String path = saveDialog.open();

		if (path == null || path == "")
			return;
		
		try {
			Workspace.getProject();
			Project.setPath(path);
			ProjectManager.saveProject();
		} catch (Exception e) {
			MessageDialog.openError(workspace.getShell(), "Error", "Some errors happened. Can not save file. Please try again later");
		}
	}
	

	public static void exportToImage(Workspace workspace) {	
		if (workspace == null) return;
		new ExportImageDialog(workspace.getShell(), SWT.SHEET, workspace).open();
	}
	
	/**
	 * Export scenario to image
	 * @param workspace
	 */	
	public static BufferedImage exportToImage(Workspace workspace, int imageMaxSize) {
		Project project = Workspace.getProject();
		
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
        g2d.setColor(ApplicationSettings.backgroundColor);
        g2d.fillRect(0, 0, width, height);
        
        //draw edge of image with black
        g2d.setColor(Color.BLACK);
        g2d.drawRect(0, 0, width - 1, height - 1);
        
        // draw nodes
        int x, y;
        int nodeSize = ApplicationSettings.nodeSize;
        for (Node n : project.getNetwork().getNodeList()) {
        	WirelessNode wn = (WirelessNode) n;
        	
        	// get x, y of nodes in image
        	x = (int) (wn.getX() * scale);
        	y = (int) (wn.getY() * scale);
        	
        	g2d.setColor(ApplicationSettings.nodeColor);
        	g2d.fillOval(x - nodeSize / 2, y - nodeSize / 2, nodeSize, nodeSize);
        	
        	g2d.setColor(Color.BLACK);
        	g2d.drawOval(x - nodeSize / 2, y - nodeSize / 2, nodeSize, nodeSize);
        }
		
        // Disposes of this graphics context and releases any system resources that it is using.
        g2d.dispose();
		
        return bufferedImage;		
	}

	public static void closeWorkspace(Workspace workspace) {
		if (workspace == null) return;		
		CTabFolder tabFolder = (CTabFolder) workspace.getParent().getParent();
		
		try {
//			ProjectManager.project = null;
			
			// remove CtabItem object
			int i = 0;
			for (CTabItem item : tabFolder.getItems()) {
				Control c = item.getControl();
				
				if (c.equals(workspace.getParent()))
					break;
				
				i++;
			}			
			tabFolder.getItem(i).dispose();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (tabFolder.getItemCount() == 0) {
			tabFolder.dispose();
		}		
	}

	/**
	 * Create a node
	 * @param workspace
	 * @param editor
	 */
	public static void createASingleNode(Workspace workspace, Editor editor) {
		if (workspace == null) return;
		
		CreateSingleNodeResult result = (CreateSingleNodeResult)new CreateNodeDialog(workspace.getShell(), SWT.SHEET).open();
		if (result != null) {
			WirelessNode wnode = ProjectManager.createSingleNode(result.posX, result.posY);
			
			if (wnode != null) {
//				workspace.getCareTaker().save(workspace.getProject(), "Create a single node");
				workspace.updateLayout();
				
				// handle select- deselect
				workspace.deselectGraphicObjects();				
//				workspace.getGraphicNodeByNode(wnode).setSelect(true);
				workspace.getPropertyManager().setMouseMode(WorkspacePropertyManager.CURSOR);				
				workspace.getGraphicNetwork().redraw();
				editor.getActSave().setEnabled(true);
			} else {
				MessageDialog.openInformation(workspace.getShell(), "Cannot create node", "Cannot create node at specified location.\r\n" +
						"Some reasons may happen:\r\n" +
						"- Location is out of network boundary\r\n" +
						"- There is existing node at that location, or \r\n" +
						"- Location is inside some obstacles");
			}
		}
	}

	/**
	 * Create a set of node to topology.
	 * Method of create a set of node has 2 way: grid topo and random topo
	 * @param workspace
	 * @param editor
	 */
	public static void createASetOfNodes(Workspace workspace,Editor editor) {
		Date s = new Date();
		
		if (workspace == null) return;
		
		CreateNodeSetResult result = (CreateNodeSetResult) new CreateNodeSetDialog(workspace.getShell(), SWT.SHEET, null).open();
		if(result != null) {
			if (result.creationType == CreateNodeSetResult.RANDOM && result.areaType == CreateNodeSetResult.WHOLE_NETWORK) {
				ProjectManager.createRandomNodes(result.numOfNodes, 0, null);
				workspace.updateLayout();			
				workspace.getPropertyManager().setMouseMode(WorkspacePropertyManager.CURSOR);
				workspace.getGraphicNetwork().redraw();
				editor.getActSave().setEnabled(true);
				
			}
			if (result.creationType == CreateNodeSetResult.GRID && result.areaType == CreateNodeSetResult.WHOLE_NETWORK) {
				ProjectManager.createGridNodes(null, result.x_range, result.y_range);
				workspace.updateLayout();			
				workspace.getPropertyManager().setMouseMode(WorkspacePropertyManager.CURSOR);
				workspace.getGraphicNetwork().redraw();
				editor.getActSave().setEnabled(true);
			}
		}
		
		System.out.println(new Date().getTime() - s.getTime());
	}

	/**
	 * Manage Traffic anf Flow, contain add event sending from source node to destination node
	 * @param workspace
	 */
	public static void manageTrafficFlow(Workspace workspace) {
		if (workspace != null)
			if (Workspace.getProject().getNetwork().getNodeList().size() < 2) {
				MessageDialog.openInformation(workspace.getShell(), "Two nodes required", "Please add more nodes to set up traffic flow");
			} else {
				new TrafficFlowManagerDialog(workspace.getShell(), SWT.SHEET).open();
			}
	}
	
	/**
	 * Delete a node from topology
	 * @param gwnode : node to delete
	 */
	public static void deleteNode(GWirelessNode gwnode) {
		ProjectManager.deleteNode(gwnode.getWirelessNode());
		gwnode.dispose();
		gwnode.getParent().layout(true);
	}

	/**
	 * Delete a set of node
	 * @param workspace
	 * @return
	 */
	public static boolean deleteNodes(Workspace workspace) {
		if (workspace == null) return false;
		boolean rlt = false;
		
		boolean answer = MessageDialog.openConfirm(workspace.getShell(), "Delete node(s)", "Are you sure to delete the node(s)?");

		if (answer) {
			try {
				for (GSelectableObject o : workspace.getSelectedObject()) {
					if (o instanceof GWirelessNode) {
						GWirelessNode gn = (GWirelessNode)o;
						ProjectManager.deleteNode(gn.getWirelessNode());
						gn.dispose();
						rlt = true;
					}
				}
				
//				workspace.getCareTaker().save(workspace.getProject(), "Delete Node(s)");
			} catch (Exception e) {
				MessageDialog.openError(workspace.getShell(), "Error", "Delete a set of node not completed!");
				e.printStackTrace();
			}
		}
		return rlt;
	}
	
	/**
	 * Copy every node is selected
	 * @param workspace
	 * @return
	 */
	public static GWirelessNode copyNode(Workspace workspace) {
		GWirelessNode gn = null;
		for (GSelectableObject o : workspace.getSelectedObject()) {
			if (o instanceof GWirelessNode) {
				gn = (GWirelessNode)o;
			}
		}
		return gn;
	}
	
	/**
	 * Paste nodes has copied to workspace
	 * @param workspace
	 * @param x
	 * @param y
	 */
	public static void pasteNode(Workspace workspace, int x, int y) {
		WirelessNode wnode = ProjectManager.createSingleNode(x, y);
		
		if (wnode != null) {			
			workspace.updateLayout();
			
			// handle select- deselect
			workspace.deselectGraphicObjects();				
			workspace.getGraphicNodeByNode(wnode).setSelect(true);
			workspace.getPropertyManager().setMouseMode(WorkspacePropertyManager.CURSOR);				
			workspace.getGraphicNetwork().redraw();
		} else {
			MessageDialog.openInformation(workspace.getShell(), "Cannot create node", "Cannot create node at specified location.\r\n" +
					"Some reasons may happen:\r\n" +
					"- Location is out of network boundary\r\n" +
					"- There is existing node at that location, or \r\n" +
					"- Location is inside some obstacles");
		}
	}
	
	/**
	 * Delete all node of topology
	 * @param w
	 * @return true or false
	 */
	public static boolean deleteAllNodes(Workspace w) {
		if (w == null) return false;
		boolean b = false;
		if (MessageDialog.openConfirm(w.getShell(), "Delete All nodes?", "Are you sure to delete all nodes?")) {		
			Control[] cs = w.getChildren();
			List<Control> tempControls = new ArrayList<Control>();
	
			for (Control c : cs) {
				if (c instanceof GWirelessNode) {
					ProjectManager.deleteNode(((GWirelessNode) c).getWirelessNode());
					tempControls.add(c);
				}
			}
	
			while (tempControls.size() != 0) {
				Control c = tempControls.get(0);
				tempControls.remove(c);
				c.dispose();
			}		
			b = true;
		}
		return b;
	}

	/**
	 * View information of network
	 * @param workspace
	 */
	public static void viewNetworkInfo(Workspace workspace) {
		new NetworkPropertiesDialog(workspace.getShell(), SWT.SHEET).open();
	}

	/**
	 * View information of selected node
	 * @param workspace
	 * @param wnode
	 */
	public static void viewNodeInfo(Workspace workspace, GWirelessNode wnode) {
		new NodePropertiesDialog(workspace.getShell(), SWT.SHEET, wnode).open();		
	}

	/**
	 * Show obstacle on network
	 * @param workspace
	 */
	public static void showObstacles(Workspace workspace) {	
		if (workspace == null) return;
		
		boolean enable = workspace.getPropertyManager().isShowObstacles();
		for (GObstacle obs : workspace.getGraphicObstacles())
			obs.setVisible(enable);
			
		workspace.getPropertyManager().setShowObstacles(enable);			
	}
	
	/**
	 * Show groups that selected by user.
	 * @param workspace workspace to show
	 */
	public static void showGroups(Workspace workspace) {
		if (workspace == null) return;
		WorkspacePropertyManager pm = workspace.getPropertyManager();
		
		boolean enable = pm.isShowGroups();		
		for (GGroup g : workspace.getGraphicGroups())
		{
			g.setVisible(enable);
		}
			
		pm.setShowGroups(enable);
	}
	
	/**
	 * Show neighbor of all node has selected
	 * @param workspace
	 */
	public static void showNeighbors(Workspace workspace) {
		if (workspace == null) return;
		if(workspace.getSelectedObject().size() == 0) {
			MessageDialog.openWarning(workspace.getShell(), "Warning", "No node is selected!");
			return;
		}
		Control c;
		int len = 0;
		int[] n = new int[3000];
		for (int i = 0; i< workspace.getSelectedObject().size(); i++) {
			c = (Control) workspace.getSelectedObject().get(i);
			if (c instanceof GWirelessNode) {
				n[len] = ((GWirelessNode) c).getWirelessNode().getId();
				len++;
			}
		}
		workspace.getPropertyManager().setNeighborLen(len);
		workspace.getPropertyManager().setNodeIdShowedNeighbors(n);
		workspace.getGraphicNetwork().redraw();
	}

	/**
	 * Search a node on network
	 * @param workspace
	 */
	public static void searchNodes(Workspace workspace) {
		if (workspace != null) {
			new SearchNodeDialog(workspace.getShell(), SWT.DIALOG_TRIM, workspace).open();
		}
	}

	/**
	 * Move a node to new location
	 * @param gWirelessNode : node has to move
	 */
	public static void moveNodeTo(GWirelessNode gWirelessNode) {		
		NodeLocationResult result = (NodeLocationResult) new NodeLocationDialog(gWirelessNode.getShell(), SWT.SHEET, gWirelessNode.getWirelessNode()).open();

		if (result != null) 
		{			
			Workspace workspace = (Workspace) gWirelessNode.getParent();				
			if (Project.getObstacleList() != null) 
			{					
				for (Area a : Project.getObstacleList())
				{
					if (a.contains(result.x, result.y)) 
					{
						MessageDialog.openInformation(workspace.getShell(), "Cannot move node",	"Cannot move node to the new location because it is inside the obstacle");
						return;
					}
				}
			}						
											
			gWirelessNode.getWirelessNode().setPosition(result.x, result.y); 			
		}
	}

	/**
	 * Calculate the shortest path connect two node
	 * @param gStartNode : node start of path
	 * @param gEndNode : node end of path
	 */
	public static void findShortestPath(GWirelessNode gStartNode, GWirelessNode gEndNode) {
		Workspace workspace = (Workspace) gStartNode.getParent();
		
		// make sure this path is not created before
		for (GraphicPath path : workspace.getGraphicNetwork().getVisualizeManager().getShortestPaths()) {
			if (path.getStartNode().getWirelessNode().getId() == gStartNode.getWirelessNode().getId() &&
					path.getEndNode().getWirelessNode().getId() == gEndNode.getWirelessNode().getId()) {
//				workspace.getPropertyManager().setShowShortestPath(true);
				workspace.getPropertyManager().showShortestPath();
				workspace.getGraphicNetwork().redraw();
				return;
			}								
		}
		
		List<WirelessNode> nodeList = ProjectManager.shortestPath(gStartNode.getWirelessNode(), gEndNode.getWirelessNode());

		GraphicPath spath = new GraphicPath(GraphicPath.SHORTEST);
		spath.setStartNode(gStartNode);
		spath.setEndNode(gEndNode);

		for (WirelessNode n : nodeList)
			spath.addNode(workspace.getGraphicNodeById(n.getId()));					
		workspace.getGraphicNetwork().getVisualizeManager().addPath(spath);

//		workspace.getPropertyManager().setShowShortestPath(true);
		workspace.getPropertyManager().showShortestPath();
		workspace.getGraphicNetwork().redraw();		
	}

	/**
	 * Calculate the greedy path connect two node
	 * @param gStartNode : node start of path
	 * @param gEndNode : node end of path
	 */
	public static void findGreedyPath(GWirelessNode gStartNode, GWirelessNode gEndNode) {
		Workspace workspace = (Workspace) gStartNode.getParent();
		
		// make sure this path is not created before
		for (GraphicPath path : workspace.getGraphicNetwork().getVisualizeManager().getGreedyPaths()) {
			if (path.getStartNode().getWirelessNode().getId() == gStartNode.getWirelessNode().getId() &&
					path.getEndNode().getWirelessNode().getId() == gEndNode.getWirelessNode().getId()) {
//				workspace.getPropertyManager().setShowGreedyPath(true);
				workspace.getPropertyManager().showGreedyPath();
				workspace.getGraphicNetwork().redraw();
				return;
			}								
		}
		
		List<WirelessNode> nodeList = ProjectManager.greedyPath(gStartNode.getWirelessNode(), gEndNode.getWirelessNode());

		GraphicPath greedyPath = new GraphicPath(GraphicPath.GREEDY);
		
		greedyPath.setStartNode(gStartNode);
		greedyPath.setEndNode(gEndNode);
		for (WirelessNode n : nodeList)
			greedyPath.addNode(workspace.getGraphicNodeById(n.getId()));					
		workspace.getGraphicNetwork().getVisualizeManager().addPath(greedyPath);

//		workspace.getPropertyManager().setShowGreedyPath(true);
		workspace.getPropertyManager().showGreedyPath();
		workspace.getGraphicNetwork().redraw();
		
	}

	/**
	 * Calculate voironoi diagram
	 * @param workspace
	 */
	public static void showVoronoiDiagram(Workspace workspace) {
        workspace.getPropertyManager().showVoronoiDiagram();
		workspace.getGraphicNetwork().getVisualizeManager().calculateVoronoiDiagram();
		workspace.getGraphicNetwork().redraw();
	}

	/**
	 * Calculate delaunay triangulation
	 * @param workspace
	 */
	public static void showDelaunayTriangulation(Workspace workspace) {
        workspace.getPropertyManager().showDelaunayTriangulation();
		workspace.getGraphicNetwork().getVisualizeManager().calculateVoronoiDiagram();
		workspace.getGraphicNetwork().redraw();
	}
	
	/**
	 * Show RNG graph
	 * @param workspace
	 * @return
	 */
	public static boolean showRNG(Workspace workspace) {
		workspace.getGraphicNetwork().getVisualizeManager().calculateRNGGraph();		
		if (workspace.getGraphicNetwork().getVisualizeManager().getRNGGraph() == null) {
			MessageDialog.openInformation(workspace.getShell(), "RNG Graph", "Cannot draw RNG graph. Maybe the network is not connected");
			return false;
		} else {
	        workspace.getPropertyManager().showRNG();
			workspace.getGraphicNetwork().redraw();
			return true;
		}
	}
	
	/**
	 * Show GG graph
	 * @param workspace
	 * @return
	 */
	public static boolean showGG(Workspace workspace) {
		workspace.getGraphicNetwork().getVisualizeManager().calculateGGGraph();
		
		if (workspace.getGraphicNetwork().getVisualizeManager().getGGGraph() == null) {
			MessageDialog.openInformation(workspace.getShell(), "GG Graph", "Cannot draw GG graph. Maybe the network is not connected");
			return false;
		} else {
	        workspace.getPropertyManager().showGG();
			workspace.getGraphicNetwork().redraw();
			return true;
		}
	}

	/**
	 * Create a random node 
	 * @param w
	 * @return
	 */
	public static boolean createARandomNode(Workspace w) {
		boolean rlt = false;
		if (w != null) {
			// create a random node
			WirelessNode wnode = ProjectManager.createARandomNode();
			
			if (wnode != null) {				
				w.updateLayout();
				
				w.deselectGraphicObjects();
				w.getGraphicNodeByNode(wnode).setSelect(true);
				w.getPropertyManager().setMouseMode(WorkspacePropertyManager.CURSOR);
				
				w.getGraphicNetwork().redraw();
				rlt = true;
			}
		}
		return rlt;
	}

	/**
	 * Show range of nodes that has selected
	 * @param workspace
	 */
	public static void showRange(Workspace workspace) {
		Control c;
		if(workspace.getSelectedObject().size() == 0) {
			MessageDialog.openWarning(workspace.getShell(), "Error", "No node is selected!");
			return;
		} 
		int len = 0;
		int[] n = new int[3000];
		for (int i = 0; i< workspace.getSelectedObject().size(); i++) {
			c = (Control) workspace.getSelectedObject().get(i);
			if (c instanceof GWirelessNode) {
				n[len] = ((GWirelessNode) c).getWirelessNode().getId();
				len++;
			}
		}
		workspace.getPropertyManager().setRangeLen(len);
		workspace.getPropertyManager().setNodeIdShowedRange(n);
		workspace.getGraphicNetwork().redraw();
		
//		if (workspace.getSelectedObject().size() == 1) {
//			c = (Control) workspace.getSelectedObject().get(0);
//
//			if (c instanceof GWirelessNode) {
//				workspace.getPropertyManager().setNodeIdShowedRange(((GWirelessNode) c).getWirelessNode().getId());
//				workspace.getGraphicNetwork().redraw();
//			}
//		}
	}

	/**
	 * Show network connection
	 * @param workspace
	 * @param isShow
	 */
	public static void showConnection(Workspace workspace, boolean isShow) {
		if (workspace != null) {
			workspace.getPropertyManager().setShowConnection(isShow);
			workspace.getGraphicNetwork().redraw();
		}
	}	
	
	/**
	 * Check network has connectivity or else
	 * @param workspace
	 */
	public static void checkConnectivity(Workspace workspace) {
		if (workspace != null) {
			boolean isConnect = ProjectManager.checkConnectivity();
			
			if (isConnect)
				MessageDialog.openInformation(workspace.getShell(), "Network Connectivity", 
						"The network " + Workspace.getProject().getNetwork().getName() + " with " +
								Workspace.getProject().getNetwork().getNodeList().size() + " node(s) is connected");
			else
				MessageDialog.openInformation(workspace.getShell(), "Network Connectivity", 
						"The network " + Workspace.getProject().getNetwork().getName() + " with " +
								Workspace.getProject().getNetwork().getNodeList().size() + " node(s) is not connected");				
		}
	}

	/**
	 * Manage label 
	 * @param workspace
	 */
	public static void manageLabels(Workspace workspace) {
		if (workspace != null)
			new LabelDialog(workspace.getShell(), SWT.SHEET).open();		
	}

	/**
	 * Manage path
	 * @param workspace
	 */
	public static void managePaths(Workspace workspace) {
		if (workspace != null)
			new ViewPathInfo(workspace.getShell(), SWT.SHEET, workspace.getGraphicNetwork()).open();
	}

	public static boolean showPaths(Workspace parent) {
		if (parent.getPropertyManager().isShowGreedyPath() || parent.getPropertyManager().isShowShortestPath()) {
			parent.getPropertyManager().setShowGreedyPath(false);
			parent.getPropertyManager().setShowShortestPathTree(false);
			parent.getGraphicNetwork().redraw();
			return false;
		} else {
			parent.getPropertyManager().setShowGreedyPath(true);
			parent.getPropertyManager().setShowShortestPathTree(true);
			parent.getGraphicNetwork().redraw();
			return true;			
		}			
	}
	
	public static void importLocationData(Workspace workspace) {
		if (workspace != null) {
			ImportLocationDataResult result = (ImportLocationDataResult) new ImportLocationDataDialog(workspace.getShell(), SWT.SHEET).open();
			
			if (result != null) {
				for (Point p : result.pointList) {
					ProjectManager.createSingleNode( p.x, p.y);
				}
				
				workspace.updateLayout();
				workspace.getGraphicNetwork().redraw();				
			}
		}
	}

	/**
	 * Change size of network
	 * @param workspace
	 */
	public static void changeNetworkSize(Workspace workspace) {
		if (workspace == null) return;
		
		EditNetworkSizeResult result = (EditNetworkSizeResult)new EditNetworkSizeDialog(workspace.getShell(), SWT.SHEET).open();

		if (result == null) return;			
		
		ProjectManager.changeNetworkSize(result.width, result.height,result.time, result.wType, result.lType);
		workspace.updateLayout();
		workspace.getGraphicNetwork().redraw();
	}

	/**
	 * Print scenario to pdf file
	 * @param w
	 */
	public static void print(Workspace w) {
		if (w == null) return;		
		Project p = Workspace.getProject();		
		PrinterData printerData = new PrintDialog(w.getShell(), SWT.SHEET).open();
		
		if (!(printerData == null)) {
			Printer printer = new Printer(printerData);
			Rectangle rec = printer.getClientArea();
			org.eclipse.swt.graphics.Point dpi = printer.getDPI();			
			
			if (!printer.startJob(p.getNetwork().getName())) {
				System.err.println("Failed to start print job!");
				printer.dispose();
				return;
			}
			
			GC gc = new GC(printer);
			
			if (!printer.startPage()) {
				System.err.println("Failed to start print page!");
				gc.dispose();
				return;
			}
			
			PrintHelper pHelper = new PrintHelper(dpi.y, rec.width, rec.height);
			int hOffset = pHelper.marginTop;		// height offset
			int wOffset = pHelper.marginLeft;		// width offset
			
			// draw title
			pHelper.drawTitle(gc, p.getNetwork().getName());
			hOffset += pHelper.titleStyle.getFont().getFontData()[0].getHeight() * 2.5;
			
			// draw network image
			int maxSize = rec.width - pHelper.marginLeft - pHelper.marginRight > pHelper.height - hOffset - pHelper.marginBottom ? pHelper.height - hOffset - pHelper.marginBottom : pHelper.width - pHelper.marginLeft - pHelper.marginRight;
			double scale = 1d;
			int actualWidth = w.getGraphicNetwork().getSize().x;
			int actualHeight = w.getGraphicNetwork().getSize().y;
			int width, height;
			if (actualWidth > actualHeight) {
				width = maxSize;
				scale = (double) width / actualWidth;
				height = (int) (actualHeight * scale);
			} else {
				height = maxSize;
				scale = (double) height / actualHeight;
				width = (int) (actualWidth * scale);
			}
			gc.setBackground(ApplicationSettings.colorAWTtoSWT(ApplicationSettings.backgroundColor));
			gc.fillRectangle(wOffset, hOffset, width, height);
			gc.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
			gc.drawRectangle(wOffset, hOffset, width, height);
			
			int nodeSize = (int) (ApplicationSettings.nodeSize * scale);
			int x, y;
			gc.setBackground(ApplicationSettings.colorAWTtoSWT(ApplicationSettings.nodeColor));
			gc.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
			for (GWirelessNode n : w.getGraphicNodes()) {
				x = (int) ((n.getBounds().x - w.getGraphicNetwork().getBounds().x) * scale);
				y = (int) ((n.getBounds().y - w.getGraphicNetwork().getBounds().y) * scale);
				
				gc.fillOval(wOffset + x - nodeSize / 2, hOffset + y - nodeSize / 2, nodeSize, nodeSize);
				gc.drawOval(wOffset + x - nodeSize / 2, hOffset + y - nodeSize / 2, nodeSize, nodeSize);
			}			
			hOffset += height + 0.2 * pHelper.getUnit();
			
			gc.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
			
			int h2Spacing = pHelper.h2Style.getFont().getFontData()[0].getHeight() * 2;
			int paragraphSpacing = pHelper.paragraphStyle.getFont().getFontData()[0].getHeight() * 2;
			
			pHelper.h2Style.draw(gc, "General Information", wOffset, hOffset);
			hOffset += h2Spacing;
			
			pHelper.paragraphStyle.draw(gc, "Network Size", wOffset, hOffset);
			pHelper.paragraphStyle.draw(gc, p.getNetwork().getWidth() + " x " + p.getNetwork().getLength() + " m", wOffset + 2 * pHelper.getUnit(), hOffset);
			hOffset += paragraphSpacing;
			
			pHelper.paragraphStyle.draw(gc, "Simulation Time", wOffset, hOffset);
			pHelper.paragraphStyle.draw(gc, p.getNetwork().getTime() + " seconds", wOffset + 2 * pHelper.getUnit(), hOffset);
			hOffset += paragraphSpacing;
			
			pHelper.paragraphStyle.draw(gc, "Node Count", wOffset, hOffset);
			pHelper.paragraphStyle.draw(gc, p.getNetwork().getNodeList().size() + " nodes", wOffset + 2 * pHelper.getUnit(), hOffset);
			hOffset += paragraphSpacing;
			
			pHelper.paragraphStyle.draw(gc, "Node Range", wOffset, hOffset);
			pHelper.paragraphStyle.draw(gc, p.getNodeRange() + " m", wOffset + 2 * pHelper.getUnit(), hOffset);
			hOffset += paragraphSpacing;
			
			pHelper.paragraphStyle.draw(gc, "Routing Protocol", wOffset, hOffset);
			pHelper.paragraphStyle.draw(gc, p.getSelectedRoutingProtocol(),	wOffset + 2 * pHelper.getUnit(), hOffset);
			hOffset += paragraphSpacing;
			
			hOffset += 0.1 * pHelper.getUnit();
			pHelper.h2Style.draw(gc, "Node Configuration", wOffset, hOffset);
			hOffset += h2Spacing;
			
			// TODO
			
			pHelper.drawFooter(gc, "Created by Wissim Editor - " + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime()));
			
			gc.dispose();
			
			printer.endPage();
			printer.endJob();
			printer.dispose();
		}
	}
}
