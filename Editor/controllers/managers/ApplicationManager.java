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
import java.util.List;

import models.Project;
import models.DialogResult.CreateNodeSetResult;
import models.DialogResult.CreateProjectResult;
import models.DialogResult.CreateSingleNodeResult;
import models.DialogResult.EditNetworkSizeResult;
import models.DialogResult.ImportLocationDataResult;
import models.DialogResult.NodeLocationResult;
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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import controllers.graphicscomponents.GObstacle;
import controllers.graphicscomponents.GSelectableObject;
import controllers.graphicscomponents.GWirelessNode;
import controllers.graphicscomponents.GraphicPath;
import controllers.helpers.Helper;
import controllers.helpers.Validator;
import views.CreateProjectDialog;
import views.Editor;
import views.SearchNodeDialog;
import views.Workspace;
import views.dialogs.CreateNodeDialog;
import views.dialogs.CreateNodeSetDialog;
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

public class ApplicationManager {
	private static int newProjectCount = 1;
	
	public static Project newProject(Editor mainWindow) {
		String defaultName = "Untitled" + newProjectCount;
		CreateProjectResult result = (CreateProjectResult)new CreateProjectDialog(mainWindow.getShell(), SWT.SHEET, defaultName).open();				
		if (result == null) return null;

		// set up path to save file
		boolean confirmed = true;
		File dir=new File(result.path);
		if(!dir.exists()) {
			dir.mkdir();
		}
		
			String path = Validator.getFilePath(result.path, Helper.getFileNameWithExt(result.name, "wis"));
	
			File f = new File(path);
	
			
			if (f.exists() == true)
				confirmed = MessageDialog.openQuestion(mainWindow.getShell(), "File Existed", "File existed in the selected directory. Do you want to override the existing file by the new one?");
	
			if (confirmed == true) {
				Project project;
				try {
					project = ProjectManager.createProject(path, Helper.getFileNameWithoutExt(result.name, "wis"), result.width, result.height, result.time);
					ApplicationSettings.applyDefaultSettingsToProject(project);
					newProjectCount++;
					
					return project;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}		
		mainWindow.setFileProject(result.path+"/"+result.name);
		return null;
	}

	
	public static Project openProject(Editor mainWindow) {
		// open Open Dialog
		FileDialog openDialog = new FileDialog(mainWindow.getShell(), SWT.OPEN);
		openDialog.setText("Open");
		openDialog.setFilterPath("D:/");
		String[] filterExt = { "*.wis" };
		openDialog.setFilterExtensions(filterExt);
		openDialog.setFilterNames(new String[] { "Wissim Project" });
		String path = openDialog.open();
     
		if (path == null) return null;		

		// open project
		try {					
			Project project = ProjectManager.loadProject(path);
			mainWindow.setFileProject(path);
			return project;
		} catch (Exception e) {}
		
		return null;
	}

	public static void saveWorkspace(Editor mainWindow) {	
		Workspace workspace = mainWindow.getWorkspace();
		
		if (workspace == null) return;
		try {					
			ProjectManager.saveProject(workspace.getProject());
			
			workspace.setSavedStateIndex(workspace.getCareTaker().getCurrentStateIndex());
			
			mainWindow.getActSave().setEnabled(false);
			CTabFolder cTabFolder = (CTabFolder) workspace.getParent().getParent();
			cTabFolder.getSelection().setText(workspace.getProject().getNetwork().getName());
		} catch (IOException e) {
			MessageDialog.openError(workspace.getShell(), "Error", "The file is currently in use");
		} 
		catch (Exception e) {
			MessageDialog.openError(workspace.getShell(), "Error", "Some errors happened. Can not save file. Please try again later");
		}
	}

	public static void saveWorkspaceAs(Workspace workspace) {	
		if (workspace == null) return;
		FileDialog saveDialog = new FileDialog(workspace.getShell(), SWT.SAVE);
		saveDialog.setText("Save");
		saveDialog.setFileName(workspace.getProject().getNetwork().getName());
		saveDialog.setFilterExtensions(new String[] { "*.wis" });
		saveDialog.setFilterNames(new String[] {"Wissim Project"});
		String path = saveDialog.open();

		if (path == null || path == "")
			return;
		
		try {
			Project project = workspace.getProject();
			project.setPath(path);
			ProjectManager.saveProject(project);
		} catch (Exception e) {
			MessageDialog.openError(workspace.getShell(), "Error", "Some errors happened. Can not save file. Please try again later");
		}
	}
	

	public static void exportToImage(Workspace workspace) {	
		if (workspace == null) return;
		new ExportImageDialog(workspace.getShell(), SWT.SHEET, workspace).open();
	}
	
	public static BufferedImage exportToImage(Workspace workspace, int imageMaxSize) {
		Project project = workspace.getProject();
		
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
			ProjectManager.project = null;
			
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

	public static void undoState(Workspace workspace) {
		if (workspace != null) {		
			Project p = workspace.getCareTaker().getLastState();
			if (p != null)
				workspace.setProject(p);
		}
	}

	public static void redoState(Workspace workspace) {
		if (workspace == null) return;
		
		Project p = workspace.getCareTaker().getNextState();
		if (p != null)
			workspace.setProject(p);
	}

	public static void createASingleNode(Workspace workspace) {
		if (workspace == null) return;
		
		CreateSingleNodeResult result = (CreateSingleNodeResult)new CreateNodeDialog(workspace.getShell(), SWT.SHEET).open();
		if (result != null) {
			WirelessNode wnode = ProjectManager.createSingleNode(workspace.getProject(), result.posX, result.posY, workspace.getProject().getNodeRange());
			
			if (wnode != null) {
//				workspace.getCareTaker().save(workspace.getProject(), "Create a single node");
				workspace.updateLayout();
				
				// handle select- deselect
				workspace.deselectGraphicObjects();				
//				workspace.getGraphicNodeByNode(wnode).setSelect(true);
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
	}

	public static void createASetOfNodes(Workspace workspace) {
		if (workspace == null) return;
		
		CreateNodeSetResult result = (CreateNodeSetResult) new CreateNodeSetDialog(workspace.getShell(), SWT.SHEET, null).open();
		
		if (result.creationType == CreateNodeSetResult.RANDOM && result.areaType == CreateNodeSetResult.WHOLE_NETWORK) {
			ProjectManager.createRandomNodes(workspace.getProject(), result.numOfNodes, 0, null);
			workspace.updateLayout();
			workspace.getCareTaker().save(workspace.getProject(), "Create a set of nodes");
			workspace.getPropertyManager().setMouseMode(WorkspacePropertyManager.CURSOR);
			workspace.getGraphicNetwork().redraw();
		}
	}

	public static void manageTrafficFlow(Workspace workspace) {
		if (workspace != null)
			if (workspace.getProject().getNetwork().getNodeList().size() < 2) {
				MessageDialog.openInformation(workspace.getShell(), "Two nodes required", "Please add more nodes to set up traffic flow");
			} else {
				new TrafficFlowManagerDialog(workspace.getShell(), SWT.SHEET, workspace).open();
			}
	}
	
	public static void deleteNode(GWirelessNode gwnode) {
		ProjectManager.deleteNode(gwnode.getWirelessNode());
		gwnode.dispose();
		gwnode.getParent().layout(true);
	}

	public static void deleteNodes(Workspace workspace) {
		if (workspace == null) return;
		
		boolean answer = MessageDialog.openConfirm(workspace.getShell(), "Delete node(s)", "Are you sure to delete the node(s)?");

		if (answer) {
			try {
				for (GSelectableObject o : workspace.getSelectedObject()) {
					if (o instanceof GWirelessNode) {
						GWirelessNode gn = (GWirelessNode)o;
						gn.dispose();
					}
				}
				
				workspace.getCareTaker().save(workspace.getProject(), "Delete Node(s)");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static GWirelessNode copyNode(Workspace workspace) {
		GWirelessNode gn = null;
		for (GSelectableObject o : workspace.getSelectedObject()) {
			if (o instanceof GWirelessNode) {
				gn = (GWirelessNode)o;
			}
		}
		return gn;
	}
	
	public static void pasteNode(Workspace workspace, int x, int y, int range) {
		WirelessNode wnode = ProjectManager.createSingleNode(workspace.getProject(), x, y, range);
		
		if (wnode != null) {
			workspace.getCareTaker().save(workspace.getProject(), "Create a single node");
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
	
	public static void deleteAllNodes(Workspace w) {
		if (w == null) return;
		
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
			
			w.getCareTaker().save(w.getProject(), "Delete all nodes");
		}
	}

	public static void viewNetworkInfo(Workspace workspace) {
		new NetworkPropertiesDialog(workspace.getShell(), SWT.SHEET, workspace).open();
	}

	public static void viewNodeInfo(Workspace workspace, GWirelessNode wnode) {
		new NodePropertiesDialog(workspace.getShell(), SWT.SHEET, workspace, wnode).open();		
	}

	public static void showObstacles(Workspace workspace) {	
		if (workspace == null) return;
		
		if (workspace.getPropertyManager().isShowObstacles()) {
			for (GObstacle obs : workspace.getGraphicObstacles())
				obs.setVisible(false);
			
			workspace.getPropertyManager().setShowObstacles(false);
		} else {
			for (GObstacle obs : workspace.getGraphicObstacles())
				obs.setVisible(true);
			
			workspace.getPropertyManager().setShowObstacles(true);
		}			
	}
	
	public static void showNeighbors(Workspace workspace) {
		if (workspace == null) return;
		
		Control c;
		if (workspace.getSelectedObject().size() == 1) {
			c = (Control) workspace.getSelectedObject().get(0);

			if (c instanceof GWirelessNode) {
				workspace.getPropertyManager().setNodeIdShowedNeighbors(((GWirelessNode) c).getWirelessNode().getId());
				workspace.getGraphicNetwork().redraw();
			}
		}
	}

	public static void searchNodes(Workspace workspace) {
		if (workspace != null) {
			new SearchNodeDialog(workspace.getShell(), SWT.DIALOG_TRIM, workspace).open();
		}
	}

	public static void moveNodeTo(GWirelessNode gWirelessNode) {
		NodeLocationResult result = (NodeLocationResult) new NodeLocationDialog(
				gWirelessNode.getShell(), SWT.SHEET, gWirelessNode.getWirelessNode()).open();

		if (result != null) {
			try {
				Workspace workspace = (Workspace) gWirelessNode.getParent();
				
				for (Area a : workspace.getProject().getObstacleList())
					if (a.contains(result.x, result.y)) {
						MessageDialog.openInformation(workspace.getShell(), "Cannot move node",
								"Cannot move node to the new location because it is inside the obstacle");
						return;
					}						
				
				gWirelessNode.getWirelessNode().setPosition(result.x, result.y);
				
				// save state
				workspace.getCareTaker().save(workspace.getProject(), "Move node");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

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
		
		List<WirelessNode> nodeList = ProjectManager.shortestPath(workspace.getProject(), gStartNode.getWirelessNode(), gEndNode.getWirelessNode());

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
		
		List<WirelessNode> nodeList = ProjectManager.greedyPath(workspace.getProject(), gStartNode.getWirelessNode(), gEndNode.getWirelessNode());

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

	public static void showVoronoiDiagram(Workspace workspace) {
        workspace.getPropertyManager().showVoronoiDiagram();
		workspace.getGraphicNetwork().getVisualizeManager().calculateVoronoiDiagram();
		workspace.getGraphicNetwork().redraw();
	}

	public static void showDelaunayTriangulation(Workspace workspace) {
        workspace.getPropertyManager().showDelaunayTriangulation();
		workspace.getGraphicNetwork().getVisualizeManager().calculateVoronoiDiagram();
		workspace.getGraphicNetwork().redraw();
	}
	
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

	public static void createARandomNode(Workspace w) {
		if (w != null) {
			// create a random node
			WirelessNode wnode = ProjectManager.createARandomNode(w.getProject());
			
			if (wnode != null) {
				// store the new state of project
				w.getCareTaker().save(w.getProject(), "Create a random node");
				w.updateLayout();
				
				w.deselectGraphicObjects();
				w.getGraphicNodeByNode(wnode).setSelect(true);
				w.getPropertyManager().setMouseMode(WorkspacePropertyManager.CURSOR);
				
				w.getGraphicNetwork().redraw();
			}
		}
	}

	public static void showRange(Workspace workspace) {
		Control c;

		if (workspace.getSelectedObject().size() == 1) {
			c = (Control) workspace.getSelectedObject().get(0);

			if (c instanceof GWirelessNode) {
				workspace.getPropertyManager().setNodeIdShowedRange(((GWirelessNode) c).getWirelessNode().getId());
				workspace.getGraphicNetwork().redraw();
			}
		}
	}

	public static void showConnection(Workspace workspace, boolean isShow) {
		if (workspace != null) {
			workspace.getPropertyManager().setShowConnection(isShow);
			workspace.getGraphicNetwork().redraw();
		}
	}	
	
	public static void checkConnectivity(Workspace workspace) {
		if (workspace != null) {
			boolean isConnect = ProjectManager.checkConnectivity(workspace.getProject());
			
			if (isConnect)
				MessageDialog.openInformation(workspace.getShell(), "Network Connectivity", 
						"The network " + workspace.getProject().getNetwork().getName() + " with " +
								workspace.getProject().getNetwork().getNodeList().size() + " node(s) is connected");
			else
				MessageDialog.openInformation(workspace.getShell(), "Network Connectivity", 
						"The network " + workspace.getProject().getNetwork().getName() + " with " +
								workspace.getProject().getNetwork().getNodeList().size() + " node(s) is not connected");				
		}
	}

	public static void manageLabels(Workspace workspace) {
		if (workspace != null)
			new LabelDialog(workspace.getShell(), SWT.SHEET, workspace).open();		
	}

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
					ProjectManager.createSingleNode(workspace.getProject(), p.x, p.y, workspace.getProject().getNodeRange());
				}
				
				workspace.updateLayout();
				workspace.getGraphicNetwork().redraw();
				workspace.getCareTaker().save(workspace.getProject(), "Import Location Data");
			}
		}
	}

	public static void changeNetworkSize(Workspace workspace) {
		if (workspace == null) return;
		
		EditNetworkSizeResult result = (EditNetworkSizeResult)new EditNetworkSizeDialog(workspace.getShell(), SWT.SHEET, workspace).open();

		if (result == null) return;			
		
		ProjectManager.changeNetworkSize(workspace.getProject(), result.width, result.height, result.wType, result.lType);
		workspace.updateLayout();
		workspace.getGraphicNetwork().redraw();
	}

	public static void print(Workspace w) {
		if (w == null) return;		
		Project p = w.getProject();		
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
