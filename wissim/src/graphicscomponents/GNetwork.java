package graphicscomponents;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import control.manager.ApplicationManager;
import control.manager.ApplicationSettings;
import control.manager.ProjectManager;
import algorithms.Graph;
import algorithms.delaunay.Pnt;
import algorithms.delaunay.Triangle;
import appmanagers.WorkspacePropertyManager;
import dialogs.CreateLabelDialog;
import dialogs.CreateNodeSetDialog;
import dialogs.NetworkPropertiesDialog;
import view.Workspace;
import networkcomponents.features.Area;
import networkcomponents.features.GraphicLabel;
import networkcomponents.features.Label;
import model.Project;
import model.DialogResult.CreateNodeSetResult;
import model.networkComponent.WirelessNetwork;
import model.networkComponent.WirelessNode;


public class GNetwork extends Canvas {
	public final static int MARGIN = 17;				// the minimum margin with the workspace
	
    private Random random = new Random();       		// source of random numbers
	WirelessNetwork network;							// the network which is represented	
	private int initX, initY, initWidth, initHeight;	// initial x, y, width, height before scaling
	
	private Point mouseLeftLastDown = null;			// handle mouse state
	
	private Point mouseCurrentPoint = null;
	
	private Menu menu;									// drop down menu    
    private GC gc;										// used for painting
    
    private VisualizeManager visualizeManager;			// manage the visualization of the network    
    
    private boolean showSelectRectangle = false;		// show select rectangle
    
    private List<GSelectableObject> tempObjectList = new LinkedList<GSelectableObject>();
    private Area selectedArea;
    private double ratio;
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public GNetwork(Composite parent, int style, WirelessNetwork network) {
		super(parent, style);
		
		
		this.network = network;
		this.selectedArea = new Area();
		
		visualizeManager = new VisualizeManager(this);
		
		this.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseUp(MouseEvent arg0) {
				Workspace workspace = (Workspace) getParent();				
				WorkspacePropertyManager workspacePropertyManager = workspace.getPropertyManager();
				
				switch (workspacePropertyManager.getMouseMode()) {
				case WorkspacePropertyManager.CURSOR:					
					if (workspacePropertyManager.getNodeIdShowedNeighbors() != -1) {
						workspacePropertyManager.setNodeIdShowedNeighbors(- 1);
						redraw();
					}
					
					if (workspacePropertyManager.getNodeIdShowedRange() != -1) {
						workspacePropertyManager.setNodeIdShowedRange(-1);
						redraw();
					}				
					
					break;
				case WorkspacePropertyManager.NODE_GEN:
					if (mouseLeftLastDown != null)					
						if (mouseLeftLastDown.x <= arg0.x + 5 && mouseLeftLastDown.x >= arg0.x - 5 && 
							mouseLeftLastDown.y <= arg0.y + 5 && mouseLeftLastDown.y >= arg0.y - 5){
	
							// calculate actual location
							int x = (int) ((double) arg0.x / getRatio()); 
							// config location
							if(x<4) 
								x=4;
							if(x>(workspace.getGraphicNetwork().getNetwork().getWidth()-4))
								x=(workspace.getGraphicNetwork().getNetwork().getWidth()-4);
							
							int y = (int) ((double) arg0.y / getRatio()); 
							if(y<4) 
								y=4;
							if(y>(workspace.getGraphicNetwork().getNetwork().getLength()-4))
								y=(workspace.getGraphicNetwork().getNetwork().getLength()-4);
							
							//System.out.print(ApplicationSettings.nodeRange);
							WirelessNode node = ProjectManager.createSingleNode(workspace.getProject(), x, y, ApplicationSettings.nodeRange);
							workspace.getCareTaker().save(workspace.getProject(), "generate node");
							workspace.updateLayout();
							workspace.deselectGraphicObjects();
							workspace.getGraphicNodeByNode(node).setSelect(true);
														
						}
					break;
				case WorkspacePropertyManager.AREA:
					if (mouseLeftLastDown == null) return;
					
					if (mouseLeftLastDown.x <= arg0.x + 5 && mouseLeftLastDown.x >= arg0.x - 5 && 
					mouseLeftLastDown.y <= arg0.y + 5 && mouseLeftLastDown.y >= arg0.y - 5) {
						
						// calculate actual location
						int x = (int) ((double) arg0.x / getRatio()); 
						int y = (int) ((double) arg0.y / getRatio()); 
						
//						// check whether the new add point can make the area invalid
//						Polygon p = new Polygon();
//						for (int i = 0; i < selectedArea.npoints; i++)
//							p.addPoint(selectedArea.xpoints[i], selectedArea.ypoints[i]);
//						p.addPoint(x, y);						
//						PolygonAlgorithm algorithm = new PolygonAlgorithm(p);
//						if (algorithm.isSimple())
							selectedArea.addPoint(x, y);
						
						redraw();
					}
					break;
				}
				
				mouseLeftLastDown = null;
				tempObjectList.clear();
				
				if (showSelectRectangle == true) {
					showSelectRectangle = false;
					redraw();
				}
			}
			
			@Override
			public void mouseDown(MouseEvent arg0) {
				if (arg0.button == 1)
					mouseLeftLastDown = new Point(arg0.x, arg0.y);
				if (arg0.button == 3) {
					updateMenu();	
				}
				
				Workspace workspace = (Workspace) getParent();				
				WorkspacePropertyManager workspacePropertyManager = workspace.getPropertyManager();			
				
				switch (workspacePropertyManager.getMouseMode()) {
				case WorkspacePropertyManager.CURSOR:
					if (arg0.button == 1 && workspacePropertyManager.isShiftKeyPressed() == false)						
						workspace.deselectGraphicObjects();
					break;
				}
			}
			
			@Override
			public void mouseDoubleClick(MouseEvent arg0) {
			}
		});
		
		this.addMouseMoveListener(new MouseMoveListener() {
			
			@Override
			public void mouseMove(MouseEvent arg0) {
				// save the current point of mouse
				mouseCurrentPoint = new Point(arg0.x, arg0.y);
				
				// mouse is not at Left Down state
				if (mouseLeftLastDown == null) return;
				
				Workspace workspace = (Workspace) getParent();
				if (workspace.getPropertyManager().getMouseMode() == WorkspacePropertyManager.CURSOR) {
					if ((mouseLeftLastDown.x > arg0.x + 5 || mouseLeftLastDown.x < arg0.x - 5) && 
							(mouseLeftLastDown.y > arg0.y + 5 || mouseLeftLastDown.y < arg0.y - 5)) {
						showSelectRectangle = true;						
						redraw();								
						
						int minx = mouseLeftLastDown.x > mouseCurrentPoint.x ? mouseCurrentPoint.x : mouseLeftLastDown.x;
						int miny = mouseLeftLastDown.y > mouseCurrentPoint.y ? mouseCurrentPoint.y : mouseLeftLastDown.y;
						int width = Math.abs(mouseLeftLastDown.x - mouseCurrentPoint.x);
						int height = Math.abs(mouseLeftLastDown.y - mouseCurrentPoint.y); 						
						Rectangle rect = new Rectangle(minx, miny, width, height);
						
						
						for (GWirelessNode gnode : workspace.getGraphicNodes()) {
							if (workspace.getSelectedObject().contains(gnode))
								continue;
							
							Point p = gnode.getCenterLocation();
						
							
							if (rect.contains(p)) {
								tempObjectList.add(gnode);
								gnode.setSelect(true);
								gnode.redraw();
							}
						}
						
						List<GSelectableObject> temp = new LinkedList<GSelectableObject>();
						for (GSelectableObject object : tempObjectList) {
							if (!rect.contains(object.getCenterLocation())) {
								temp.add(object);
								object.setSelect(false);
								object.redraw();
							}
						}						
						
						for (GSelectableObject o : temp)
							tempObjectList.remove(o);
					}
				}
			}
		});
		
		this.addPaintListener(new PaintListener() {
			
			@Override
			public void paintControl(PaintEvent arg0) {
				gc = arg0.gc;				
				gc.setAntialias(SWT.ON);
				
				Workspace workspace = (Workspace) getParent();				
				setBackground(ApplicationSettings.colorAWTtoSWT(ApplicationSettings.backgroundColor));
				
				if (showSelectRectangle == true) {
					int minx = mouseLeftLastDown.x > mouseCurrentPoint.x ? mouseCurrentPoint.x : mouseLeftLastDown.x;
					int miny = mouseLeftLastDown.y > mouseCurrentPoint.y ? mouseCurrentPoint.y : mouseLeftLastDown.y;
					
					gc.drawFocus(minx, miny, 
							Math.abs(mouseLeftLastDown.x - mouseCurrentPoint.x), 
							Math.abs(mouseLeftLastDown.y - mouseCurrentPoint.y));
				}
				
				if (workspace.getPropertyManager().getMouseMode() == WorkspacePropertyManager.AREA) {
					int[] xpoints = selectedArea.xpoints;
					int[] ypoints = selectedArea.ypoints;
					int npoints = selectedArea.npoints;
					
					int[] polyline = new int[2 * npoints];
					for (int i = 0; i < npoints; i++) {
						polyline[2 * i] = (int) (xpoints[i] * getRatio());
						polyline[2 * i + 1] = (int) (ypoints[i] * getRatio());
					}
					
					gc.drawPolygon(polyline);
					
					gc.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));
					gc.fillPolygon(polyline);
				}
				
				// draw visualization for showing neighbors
				if (workspace.getPropertyManager().getNodeIdShowedNeighbors() != -1)
					showNeighbors();
				
				// draw visualization for showing range
				if (workspace.getPropertyManager().getNodeIdShowedRange() != -1)
					showRange();					
				
				// draw visualization for showing connection
				if (workspace.getPropertyManager().isShowConnection() == true)
					showConnection();
				
				if (workspace.getPropertyManager().isShowVoronoiDiagram() == true)
			        drawAllVoronoi(VisualizeManager.MONOCOLOR);
				
				if (workspace.getPropertyManager().isShowDelaunayTriangulation() == true)
					drawAllDelaunay(VisualizeManager.NO_COLOR);				
				if (workspace.getPropertyManager().isShowRNG() == true)
					drawAllRNG();
				if (workspace.getPropertyManager().isShowGG() == true)
					drawAllGG();				
				if (workspace.getPropertyManager().isShowGreedyPath() == true)
					showGreedyPaths();				
				if (workspace.getPropertyManager().isShowShortestPath() == true)
					showShortestPaths();				
			}
		});
		
		this.addControlListener(new ControlAdapter() {			
			@Override
			public void controlResized(ControlEvent arg0) {
				ratio = (double) getSize().x / getNetwork().getWidth();
				
				Workspace workspace = (Workspace) getParent();		
				
				for (GSelectableObject obj : workspace.getSelectableObject())
					obj.updateBounds();
				
				if (workspace.getPropertyManager().isShowVoronoiDiagram() == true) {
					visualizeManager.calculateVoronoiDiagram();
					redraw();
				}
				if (workspace.getPropertyManager().isShowDelaunayTriangulation() == true) {
					visualizeManager.calculateVoronoiDiagram();
					redraw();
				}
			}
		});
		
	}
	
	protected void showShortestPaths() {
		List<GraphicPath> paths = visualizeManager.getShortestPaths();
		GraphicPath p = paths.get(paths.size()-1);
//		for (GraphicPath p : paths) {
			GWirelessNode gnode;					
			int x1, y1, x2, y2;
			gc.setForeground(ApplicationSettings.colorAWTtoSWT(ApplicationSettings.shortestPathColor));
			for (int i = 0; i < p.getNodeList().size() - 1; i++) {
				gnode = p.getNodeList().get(i);
				x1 = gnode.getLocation().x + gnode.getSize().x / 2 - getLocation().x;
				y1 = gnode.getLocation().y + gnode.getSize().y / 2  - getLocation().y;
				
				gnode = p.getNodeList().get(i + 1);
				x2 = gnode.getLocation().x + gnode.getSize().x / 2 - getLocation().x;
				y2 = gnode.getLocation().y + gnode.getSize().y / 2  - getLocation().y;
				
				gc.setLineWidth(ApplicationSettings.shortestPathThickness);
				gc.drawLine(x1, y1, x2, y2);
			}										
//		}
	}
	
	public void setWirelessNetwork(WirelessNetwork network) {
		this.network = network;
	}

	protected void showGreedyPaths() {
		List<GraphicPath> paths = visualizeManager.getGreedyPaths();
//		System.out.println(paths.get(paths.size()));
		GraphicPath p = paths.get(paths.size()-1);
//		for (GraphicPath p : paths) {
			GWirelessNode gnode;					
			int x1, y1, x2, y2;
			gc.setForeground(ApplicationSettings.colorAWTtoSWT(ApplicationSettings.greedyPathColor));
			for (int i = 0; i < p.getNodeList().size() - 1; i++) {
				gnode = p.getNodeList().get(i);
				x1 = gnode.getLocation().x + gnode.getSize().x / 2 - getLocation().x;
				y1 = gnode.getLocation().y + gnode.getSize().y / 2  - getLocation().y;
				
				gnode = p.getNodeList().get(i + 1);
				x2 = gnode.getLocation().x + gnode.getSize().x / 2 - getLocation().x;
				y2 = gnode.getLocation().y + gnode.getSize().y / 2  - getLocation().y;
				
				gc.setLineWidth(ApplicationSettings.greedyPathThickness);
				gc.drawLine(x1, y1, x2, y2);
			}					
//		}	
	}

	protected void showNeighbors() {
		Workspace workspace = (Workspace) getParent();
		
		GWirelessNode targetGNode = workspace.getGraphicNodeById(workspace.getPropertyManager().getNodeIdShowedNeighbors());					
		if (targetGNode == null) return;

		WirelessNode wnode = targetGNode.getWirelessNode();
		List<WirelessNode> neighborsList = wnode.getNeighborList();
		
		GWirelessNode gneighbor;
		int x1, y1, x2, y2;
		x1 = targetGNode.getLocation().x - getLocation().x + targetGNode.getSize().x / 2;
		y1 = targetGNode.getLocation().y - getLocation().y + targetGNode.getSize().y / 2;
		for (WirelessNode neighbor : neighborsList) {
			gneighbor = workspace.getGraphicNodeById(neighbor.getId());
			x2 = gneighbor.getLocation().x - getLocation().x + gneighbor.getSize().x / 2;
			y2 = gneighbor.getLocation().y - getLocation().y + gneighbor.getSize().y / 2;
			gc.drawLine(x1, y1, x2, y2);
		}
	}

	protected void showRange() {
		Workspace workspace = (Workspace) getParent();
		
		GWirelessNode targetGNode = workspace.getGraphicNodeById(workspace.getPropertyManager().getNodeIdShowedRange());
		if (targetGNode == null) return;
		
		
		//int radius = (int) (targetGNode.getWirelessNode().getRange() * getRatio());	
		int radius = (int) (ApplicationSettings.nodeRange * getRatio());
		int x = targetGNode.getLocation().x - getLocation().x + targetGNode.getSize().x / 2 - radius;
		int y = targetGNode.getLocation().y - getLocation().y + targetGNode.getSize().y / 2 - radius;
		
		gc.setBackground(new Color(Display.getCurrent(), 0, 0, 255));
		gc.setAlpha(20);
		gc.fillOval(x, y, radius + radius, radius + radius);
		
		gc.setAlpha(200);
		gc.drawOval(x, y, radius + radius, radius + radius);	
	}

	public Point getInitSize() {
		return new Point(initWidth, initHeight);
	}
	
	public Point getInitLocation() {
		return new Point(initX, initY);
	}
	
	public void setInitSize(int width, int height) {
		this.initWidth = width;
		this.initHeight = height;
	}
	
	public void setInitLocation(int x, int y) {
		this.initX = x;
		this.initY = y;
	}
	
	public void updateBounds() {
		Workspace w = (Workspace) getParent();
		// calculate initial graphic size of the network
		double gRatio = (double)w.getClientArea().height / w.getClientArea().width;
		double ratio = (double)network.getLength() / network.getWidth();
		
		int x, y, width, height;
		if (gRatio >= ratio) {
			width = w.getClientArea().width - 2 * GNetwork.MARGIN;
			height = (int)(width * ratio);
			x = GNetwork.MARGIN;
			y = (w.getClientArea().height - height) / 2;			
		} else {
			height = w.getClientArea().height - 2 * GNetwork.MARGIN;
			width = (int)(height / ratio);
			y = GNetwork.MARGIN;
			x = (w.getClientArea().width - width) / 2;			
		}
		
		this.setBounds(x, y, width, height);
	}
	
	public WirelessNetwork getNetwork() {
		return network;
	}
	
	private void updateMenu() {
		final Workspace workspace = (Workspace) getParent();
		
		if (menu != null)
			menu.dispose();
		
		menu = new Menu(this);
		setMenu(menu);
		menu.setVisible(true);
		
		// set menu context when visualize mode of the workspace is set off
		if (workspace.getPropertyManager().getVisualizeMode() == WorkspacePropertyManager.VISUAL_OFF) {	
			if (workspace.isMultipleNodesSelected())		// when multiple nodes is selected
				updateMenuForMultipleNodesSelected();
			else {
				if (workspace.getPropertyManager().getMouseMode() == WorkspacePropertyManager.AREA)
					updateAreaMenu();
				else
					updateNormalMenu();							// when nothing is selected
			}
		} else if (workspace.getPropertyManager().getVisualizeMode() == WorkspacePropertyManager.VISUAL_ON) {
			updateMenuForVisualization();					// when visualization mode is turn on
		}
	}

    private void updateAreaMenu() {
    	// do not show menu when there is no polygon chosen
    	if (selectedArea.npoints < 3)
    		return;
    	
    	MenuItem mntmRemoveAllNodes = new MenuItem(menu, SWT.NONE);
    	mntmRemoveAllNodes.setText("Remove all nodes in selected area");
    	mntmRemoveAllNodes.addSelectionListener(new SelectionAdapter() {
    		@Override
    		public void widgetSelected(SelectionEvent arg0) {
				Workspace workspace = (Workspace) getParent();
				Project project = workspace.getProject();
				
				List<WirelessNode> nodeList = new LinkedList<WirelessNode>();
				
				// check whether this area contains nodes
				for (Node n : project.getNetwork().getNodeList()) {
					WirelessNode wn = (WirelessNode) n;
					if (selectedArea.contains(wn.getX(), wn.getY())) {
						nodeList.add(wn);
					}
				}
				
				if (nodeList.size() > 0)
					if (MessageDialog.openQuestion(
							getShell(), 
							"Remove node(s) in selected area", 
							"Do you want to remove node(s) in selected area")) {
						for (WirelessNode n : nodeList) {
							workspace.getGraphicNodeByNode(n).dispose();
						}						
					} else {
						return;
					}  
				
				workspace.getPropertyManager().setMouseMode(WorkspacePropertyManager.CURSOR);
				clearSelectedArea();
				redraw();
    		}
    	});
    	
    	MenuItem mntmSetObstacle = new MenuItem(menu, SWT.NONE);
    	mntmSetObstacle.setText("Set as Obstacle");
    	mntmSetObstacle.addSelectionListener(new SelectionAdapter() {			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				Workspace workspace = (Workspace) getParent();
				Project project = workspace.getProject();
				
				// check whether this area contains nodes
				List<WirelessNode> nodeList = new LinkedList<WirelessNode>();
				for (Node n : project.getNetwork().getNodeList()) {
					WirelessNode wn = (WirelessNode) n;
					if (selectedArea.contains(wn.getX(), wn.getY())) {
						nodeList.add(wn);
					}
				}
				
				if (nodeList.size() > 0)
					if (MessageDialog.openQuestion(getShell(), "Create Obstacle", "The selected area contains node(s)\r\n" +
							"Do you want to remove them to create obstacle")) {
						for (WirelessNode n : nodeList)
							workspace.getGraphicNodeByNode(n).dispose();
					} else {
						return;
					}				
				
				project.addObstacle(selectedArea);
				workspace.getCareTaker().save(project, "Create obstacle");
				workspace.getPropertyManager().setMouseMode(WorkspacePropertyManager.CURSOR);
				workspace.getPropertyManager().setShowObstacles(true);
				workspace.updateLayout();
				workspace.getGraphicNetwork().redraw();	
				clearSelectedArea();
			}
		});
    	
    	MenuItem mntmDeployNodesInArea = new MenuItem(menu, SWT.NONE);
    	mntmDeployNodesInArea.setText("Deploy nodes in selected area");
    	mntmDeployNodesInArea.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				CreateNodeSetResult result = (CreateNodeSetResult) new CreateNodeSetDialog(getShell(), SWT.SHEET, selectedArea).open();
				
				switch (result.creationType) {
				case CreateNodeSetResult.GRID:
					break;
				case CreateNodeSetResult.RANDOM:
					Workspace w = (Workspace) getParent();
					ProjectManager.createRandomNodes(w.getProject(), result.numOfNodes, w.getProject().getNodeRange(), selectedArea);
					w.getCareTaker().save(w.getProject(), "Deploy nodes in selected area");
					w.updateLayout();					
					clearSelectedArea();
					w.getPropertyManager().setMouseMode(WorkspacePropertyManager.CURSOR);					
					w.getGraphicNetwork().redraw();
					break;
				}
			}
		});
    	
    	MenuItem mntmCancel = new MenuItem(menu, SWT.NONE);
    	mntmCancel.setText("Cancel");
    	mntmCancel.addSelectionListener(new SelectionAdapter() {
    		@Override
    		public void widgetSelected(SelectionEvent arg0) {
    			clearSelectedArea();
    			redraw();
    		}
    	});
	}

	private void updateMenuForMultipleNodesSelected() {
    	final Workspace workspace = (Workspace) getParent();
    	Project project = workspace.getProject();
    	final List<GSelectableObject> selectedObject = workspace.getSelectedObject();
    	
    	if (project.getLabelList() != null) {		
			MenuItem setLabelMenu = new MenuItem(menu, SWT.CASCADE);
			setLabelMenu.setText("Set Label");

			Menu labelsMenu = new Menu(setLabelMenu);
			setLabelMenu.setMenu(labelsMenu);
			
			MenuItem createLabelMenu = new MenuItem(labelsMenu, SWT.NONE);
			createLabelMenu.setText("Create new label");
			createLabelMenu.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					Label l = (Label) new CreateLabelDialog(getShell(), SWT.SHEET, workspace.getProject()).open();
					
					if (l != null) {
						workspace.getShownLabels().add(l);
						for (GSelectableObject obj : selectedObject) {
							l.add(((GWirelessNode) obj).getWirelessNode());
							obj.redraw();
						}
						
						workspace.getCareTaker().save(workspace.getProject(), "Create new label");
					}
				}
			});

			MenuItem removeLabelMenu = new MenuItem(menu, SWT.CASCADE);
			removeLabelMenu.setText("Remove Label");

			Menu lblMenu = new Menu(removeLabelMenu);
			removeLabelMenu.setMenu(lblMenu);

			MenuItem mntmLabel;
			for (final Label l : project.getLabelList()) {
				boolean isLabelAllSelectedNodes = true;
				for (GSelectableObject obj : selectedObject)
					if (!l.getNodeList().contains(((GWirelessNode) obj).getWirelessNode())) {
							isLabelAllSelectedNodes = false;
							break;
					}
				
				
				if (isLabelAllSelectedNodes == false) {						// all selected nodes are not set with this label
					mntmLabel = new MenuItem(labelsMenu, SWT.NONE);
					mntmLabel.setText(l.getName());

					Image img = new Image(getParent().getDisplay(), 15, 15);
					GC gc = new GC(img);

					java.awt.Color c = ((GraphicLabel)l).getColor();
					Color color = new Color(getParent().getDisplay(), c.getRed(), c.getGreen(), c.getBlue());
					gc.setBackground(color);
					gc.fillRectangle(img.getBounds());
					gc.setBackground(new Color(getParent().getDisplay(), 31, 31, 31));
					gc.drawRectangle(0, 0, img.getBounds().width - 1, img.getBounds().height - 1);
					gc.dispose();			
					mntmLabel.setImage(img);

					mntmLabel.addSelectionListener(new SelectionAdapter() {				
						@Override
						public void widgetSelected(SelectionEvent arg0) {
							workspace.getShownLabels().add(l);
							for (GSelectableObject obj : selectedObject) {
								l.add(((GWirelessNode) obj).getWirelessNode());
								obj.redraw();
							}							
						}
					});
				} else {													// all selected nodes are set with this label
					mntmLabel = new MenuItem(lblMenu, SWT.NONE);
					mntmLabel.setText(l.getName());

					Image img = new Image(getParent().getDisplay(), 15, 15);
					GC gc = new GC(img);

					java.awt.Color c = ((GraphicLabel)l).getColor();
					Color color = new Color(getParent().getDisplay(), c.getRed(), c.getGreen(), c.getBlue());
					gc.setBackground(color);
					gc.fillRectangle(img.getBounds());
					gc.setBackground(new Color(getParent().getDisplay(), 31, 31, 31));
					gc.drawRectangle(0, 0, img.getBounds().width - 1, img.getBounds().height - 1);
					gc.dispose();			
					mntmLabel.setImage(img);

					mntmLabel.addSelectionListener(new SelectionAdapter() {				
						@Override
						public void widgetSelected(SelectionEvent arg0) {
							for (GSelectableObject obj : selectedObject) {
								l.remove(((GWirelessNode) obj).getWirelessNode());
								obj.redraw();
							}
						}
					});					
				}
			}
			
	    	if (selectedObject.size() == 2 && 
	    			(selectedObject.get(0) instanceof GWirelessNode) &&
	    			(selectedObject.get(1) instanceof GWirelessNode)) {
	    		MenuItem mntmGreedyPath = new MenuItem(menu, SWT.NONE);
	    		mntmGreedyPath.setText("Greedy Path");
	    		mntmGreedyPath.addSelectionListener(new SelectionAdapter() {
	    			@Override
	    			public void widgetSelected(SelectionEvent arg0) {
	    				ApplicationManager.findGreedyPath((GWirelessNode)selectedObject.get(0), (GWirelessNode)selectedObject.get(1));
	    			}
	    		});
	    		
	    		MenuItem mntmShortestPath = new MenuItem(menu, SWT.NONE);
	    		mntmShortestPath.setText("Shortest Path");
	    		mntmShortestPath.addSelectionListener(new SelectionAdapter() {
	    			@Override
	    			public void widgetSelected(SelectionEvent arg0) {
	    				ApplicationManager.findShortestPath((GWirelessNode)selectedObject.get(0), (GWirelessNode)selectedObject.get(1));
	    			}
	    		});
	    	}

			if (labelsMenu.getItemCount() == 0)
				setLabelMenu.dispose();

			if (lblMenu.getItemCount() == 0)
				removeLabelMenu.dispose();
    	}
    	
    	MenuItem mntmDeleteNodes = new MenuItem(menu, SWT.NONE);
    	mntmDeleteNodes.setText("Delete Nodes");
    	mntmDeleteNodes.addSelectionListener(new SelectionAdapter() {
    		@Override
    		public void widgetSelected(SelectionEvent arg0) {
    			ApplicationManager.deleteNodes((Workspace) getParent());
    		}
    	});
	}
    
    private void updateNormalMenu() {
    	final Workspace workspace = (Workspace) getParent();
    	
		MenuItem mntmInfo = new MenuItem(menu, SWT.NONE);
		mntmInfo.setText("Properties");
		mntmInfo.addSelectionListener(new SelectionAdapter() {			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				new NetworkPropertiesDialog(getShell(), SWT.SHEET, workspace).open();
			}
		});
		
		new MenuItem(menu, SWT.SEPARATOR);
		
		final MenuItem mntmShowConnection = new MenuItem(menu, SWT.CHECK);
		mntmShowConnection.setText("Show Connection");
		if (workspace.getPropertyManager().isShowConnection())
			mntmShowConnection.setSelection(true);
		else
			mntmShowConnection.setSelection(false);
		mntmShowConnection.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				Workspace workspace = (Workspace) getParent();
				if (workspace.getPropertyManager().isShowConnection()) {
					ApplicationManager.showConnection(workspace, false);
					mntmShowConnection.setSelection(false);
				} else {
					ApplicationManager.showConnection(workspace, true);
					mntmShowConnection.setSelection(true);
				}
			}
		});
		
		MenuItem mntmShowObstacles = new MenuItem(menu, SWT.CHECK);
		mntmShowObstacles.setText("Show Obstacles");
		if (workspace.getPropertyManager().isShowObstacles())			
			mntmShowObstacles.setSelection(true);
		else
			mntmShowObstacles.setSelection(false);
		mntmShowObstacles.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				ApplicationManager.showObstacles((Workspace) getParent());
			}
		});
		
		MenuItem mntmShowPaths = new MenuItem(menu, SWT.CHECK);
		mntmShowPaths.setText("Show Paths");
		
		if (workspace.getPropertyManager().isShowGreedyPath() || workspace.getPropertyManager().isShowShortestPath())
			mntmShowPaths.setSelection(true);
		else
			mntmShowPaths.setSelection(false);
		
		mntmShowPaths.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				ApplicationManager.showPaths((Workspace) getParent());
			}			
		});
		
		updateLabelsMenu(menu, workspace);
		
		new MenuItem(menu, SWT.SEPARATOR);
		
		MenuItem mntmLabels = new MenuItem(menu, SWT.NONE);
		mntmLabels.setText("Label Manager");
		mntmLabels.addSelectionListener(new SelectionAdapter() {			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				ApplicationManager.manageLabels((Workspace) getParent());
			}
		});		
		
		MenuItem mntmTrafficFlows = new MenuItem(menu, SWT.NONE);
		mntmTrafficFlows.setText("Traffic Flow Manager");
		mntmTrafficFlows.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				ApplicationManager.manageTrafficFlow((Workspace) getParent());
			}			
		});
		
		MenuItem mntmPaths = new MenuItem(menu, SWT.NONE);
		mntmPaths.setText("Path Manager");
		mntmPaths.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				ApplicationManager.managePaths((Workspace) getParent());
			}			
		});
		
		new MenuItem(menu, SWT.SEPARATOR);
		
		MenuItem mnVoronoiDiagram = new MenuItem(menu, SWT.NONE);
		mnVoronoiDiagram.setText("Voronoi Digram");
		mnVoronoiDiagram.addSelectionListener(new SelectionAdapter() {			
			@Override
			public void widgetSelected(SelectionEvent arg0) {	
				ApplicationManager.showVoronoiDiagram((Workspace) getParent());
			}
		});
		
		MenuItem mnDelaunayTriangulation = new MenuItem(menu, SWT.NONE);
		mnDelaunayTriangulation.setText("Delaunay Triangulation");
		mnDelaunayTriangulation.addSelectionListener(new SelectionAdapter() {			
			@Override
			public void widgetSelected(SelectionEvent arg0) {		        
				ApplicationManager.showDelaunayTriangulation((Workspace) getParent());				
			}
		});
		
		MenuItem mntmPlanarGraph = new MenuItem(menu, SWT.CASCADE);
		mntmPlanarGraph.setText("Planar Graph");
		
		Menu menuGraphs = new Menu(mntmPlanarGraph);
		mntmPlanarGraph.setMenu(menuGraphs);		
		
		final MenuItem mnRNG = new MenuItem(menuGraphs, SWT.CHECK);
		mnRNG.setText("RNG");
		if (workspace.getPropertyManager().isShowRNG())
			mnRNG.setSelection(true);
		else
			mnRNG.setSelection(false);
		final MenuItem mnGG = new MenuItem(menuGraphs, SWT.CHECK);
		mnGG.setText("GG");
		if (workspace.getPropertyManager().isShowGG())
			mnGG.setSelection(true);
		else
			mnGG.setSelection(false);
		
		mnRNG.addSelectionListener(new SelectionAdapter() {			
			@Override
			public void widgetSelected(SelectionEvent arg0) {		        
				ApplicationManager.showRNG((Workspace) getParent());
			}
		});		

		mnGG.addSelectionListener(new SelectionAdapter() {			
			@Override
			public void widgetSelected(SelectionEvent arg0) {		        
				ApplicationManager.showGG((Workspace) getParent());		
			}
		});			
		
		new MenuItem(menu, SWT.SEPARATOR);
		
		MenuItem mntmDeleteAllNodes = new MenuItem(menu, SWT.NONE);
		mntmDeleteAllNodes.setText("Delete All Nodes");
		mntmDeleteAllNodes.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				ApplicationManager.deleteAllNodes((Workspace) getParent());
			}
		});
		
		if (network.getNodeList().size() == 0)
			mntmDeleteAllNodes.setEnabled(false);
    }

    private void updateMenuForVisualization() {
    	final Workspace workspace = (Workspace) getParent();
    	
		MenuItem mnTurnVisualizeOff = new MenuItem(menu, SWT.NONE);
		mnTurnVisualizeOff.setText("Turn Visualize Off");
		mnTurnVisualizeOff.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				workspace.getPropertyManager().turnVisualizeOff();
				redraw();
			}
		});
		
		new MenuItem(menu, SWT.SEPARATOR);		
		
		updateLabelsMenu(menu, workspace);
		
		MenuItem mntmInfo = new MenuItem(menu, SWT.NONE);
		mntmInfo.setText("Properties");
		mntmInfo.addSelectionListener(new SelectionAdapter() {			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				new NetworkPropertiesDialog(getShell(), SWT.SHEET, workspace).open();
			}
		});
		
		new MenuItem(menu, SWT.SEPARATOR);
		
		MenuItem mntmLabels = new MenuItem(menu, SWT.NONE);
		mntmLabels.setText("Label Manager");
		mntmLabels.addSelectionListener(new SelectionAdapter() {			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				ApplicationManager.manageLabels((Workspace) getParent());
			}
		});		
		
		MenuItem mntmTrafficFlows = new MenuItem(menu, SWT.NONE);
		mntmTrafficFlows.setText("Traffic Flow Manager");
		mntmTrafficFlows.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				ApplicationManager.manageTrafficFlow((Workspace) getParent());
			}			
		});
		
		MenuItem mntmPaths = new MenuItem(menu, SWT.NONE);
		mntmPaths.setText("Path Manager");
		mntmPaths.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				ApplicationManager.managePaths((Workspace) getParent());
			}			
		});
		
		new MenuItem(menu, SWT.SEPARATOR);
		
		MenuItem mnVoronoiDiagram = new MenuItem(menu, SWT.NONE);
		mnVoronoiDiagram.setText("Voronoi Digram");
		mnVoronoiDiagram.addSelectionListener(new SelectionAdapter() {			
			@Override
			public void widgetSelected(SelectionEvent arg0) {	
				ApplicationManager.showVoronoiDiagram((Workspace) getParent());
			}
		});
		
		MenuItem mnDelaunayTriangulation = new MenuItem(menu, SWT.NONE);
		mnDelaunayTriangulation.setText("Delaunay Triangulation");
		mnDelaunayTriangulation.addSelectionListener(new SelectionAdapter() {			
			@Override
			public void widgetSelected(SelectionEvent arg0) {		        
				ApplicationManager.showDelaunayTriangulation((Workspace) getParent());				
			}
		});
		
		MenuItem mntmPlanarGraph = new MenuItem(menu, SWT.CASCADE);
		mntmPlanarGraph.setText("Planar Graph");
		
		Menu menuGraphs = new Menu(mntmPlanarGraph);
		mntmPlanarGraph.setMenu(menuGraphs);		
		
		MenuItem mnRNG = new MenuItem(menuGraphs, SWT.CHECK);
		mnRNG.setText("RNG");
		mnRNG.setSelection(false);
		mnRNG.addSelectionListener(new SelectionAdapter() {			
			@Override
			public void widgetSelected(SelectionEvent arg0) {		        
				ApplicationManager.showRNG((Workspace) getParent());	
			}
		});		
		
		MenuItem mnGG = new MenuItem(menuGraphs, SWT.CHECK);
		mnGG.setText("GG");
		mnGG.setSelection(false);
		mnGG.addSelectionListener(new SelectionAdapter() {			
			@Override
			public void widgetSelected(SelectionEvent arg0) {		        
				ApplicationManager.showGG((Workspace) getParent());	
			}
		});			
    }
    
    private void updateLabelsMenu(Menu menu, final Workspace workspace) {
		if (workspace.getProject().getLabelList().size() > 0) {
			MenuItem mntmShowLabels = new MenuItem(menu, SWT.CASCADE);
			mntmShowLabels.setText("Show Labels");
			
			Menu menuLabels = new Menu(mntmShowLabels);
			mntmShowLabels.setMenu(menuLabels);
			
			for (final Label l : workspace.getProject().getLabelList()) {
				final MenuItem mntmNewItem = new MenuItem(menuLabels, SWT.CHECK);
				if (workspace.getShownLabels().contains(l))
					mntmNewItem.setSelection(true);
				else
					mntmNewItem.setSelection(false);
				
				mntmNewItem.setText(l.getName());		
				
				Image img = new Image(getParent().getDisplay(), 15, 15);
				GC gc = new GC(img);
				java.awt.Color c = ((GraphicLabel)l).getColor();
				Color color = new Color(getParent().getDisplay(), c.getRed(), c.getGreen(), c.getBlue());
				gc.setBackground(color);
				gc.fillRectangle(img.getBounds());
				gc.setBackground(new Color(getParent().getDisplay(), 31, 31, 31));
				gc.drawRectangle(0, 0, img.getBounds().width - 1, img.getBounds().height - 1);
				gc.dispose();			
				mntmNewItem.setImage(img);
				
				mntmNewItem.addSelectionListener(new SelectionAdapter() {					
					@Override
					public void widgetSelected(SelectionEvent arg0) {
						if (mntmNewItem.getSelection() == true) {							
							workspace.getShownLabels().add(l);
							
							for (GWirelessNode gnode : workspace.getGraphicNodes())
								gnode.redraw();							
						}
						else {						
							workspace.getShownLabels().remove(l);
							
							for (GWirelessNode gnode : workspace.getGraphicNodes())
									gnode.redraw();
						}
						
					}
				});
			}
		}		    	
    }
    
	/**
     * Get the color for the spcified item; generate a new color if necessary.
     * @param item we want the color for this item
     * @return item's color
     */
    private Color getColor (Object item, int type) {
    	if (visualizeManager.colorTable.containsKey(item)) return visualizeManager.colorTable.get(item);
    	
    	Color color;    	
    	switch (type) {
    	case VisualizeManager.MULTICOLOR:
    		color = new Color(Display.getCurrent(), random.nextInt(255), random.nextInt(255), random.nextInt(255));
    		break;
    	case VisualizeManager.MONOCOLOR:
    	default:
    		int c = random.nextInt(100);
    		c+= 155;
    		color = new Color(Display.getCurrent(), c, c, c);
    		break;    		
    	}    	
        
        visualizeManager.colorTable.put(item, color);
        return color;
    }

    /* Basic Drawing Methods */

    /**
     * Draw a circle.
     * @param center the center of the circle
     * @param radius the circle's radius
     * @param fillColor null implies no fill
     */
    public void drawCircle(Pnt center, double radius, Color fillColor) {
        int x = (int) center.coord(0);
        int y = (int) center.coord(1);
        int r = (int) radius;
        if (fillColor != null) {
            Color temp = gc.getBackground();
            gc.setBackground(fillColor);
            gc.fillOval(x-r, y-r, r+r, r+r);
            gc.setBackground(temp);
        }
        gc.drawOval(x-r, y-r, r+r, r+r);
    }

    /**
     * Draw a polygon.
     * @param polygon an array of polygon vertices
     * @param fillColor null implies no fill
     */
    public void drawPolygon(Pnt[] polygon, Color fillColor) {
        int[] x = new int[2 * polygon.length];
        for (int i = 0; i < polygon.length; i++) {
            x[2 * i] = (int) polygon[i].coord(0);
            x[2 * i + 1] = (int) polygon[i].coord(1);
        }
        if (fillColor != null) {
            Color temp = gc.getBackground();
            gc.setBackground(fillColor);
            gc.fillPolygon(x);
            gc.setBackground(temp);
        }
        gc.drawPolygon(x);
    }

    /**
     * Draw all the Delaunay triangles.
     * @param withFill true iff drawing Delaunay triangles with fill colors
     */
    public void drawAllDelaunay(int fillType) {
    	
        for (Triangle triangle : visualizeManager.getTriangulation()) {
            Pnt[] vertices = triangle.toArray(new Pnt[0]);
            drawPolygon(vertices, fillType == VisualizeManager.NO_COLOR ? null : getColor(triangle, fillType));
            
        }
        
    }

    /**
     * Draw all the Voronoi cells.
     * @param monocolor true iff drawing Voronoi cells with fill colors
     * @param withSites true iff drawing the site for each Voronoi cell
     */
    public void drawAllVoronoi(int fillType) {
        // Keep track of sites done; no drawing for initial triangles sites
    	
        HashSet<Pnt> done = new HashSet<Pnt>(visualizeManager.initialTriangle);
        
        for (Triangle triangle : visualizeManager.getTriangulation())
            for (Pnt site : triangle) {
            	
                if (done.contains(site)) continue;
                done.add(site);
                List<Triangle> list = visualizeManager.getTriangulation().surroundingTriangles(site, triangle);
                
                Pnt[] vertices = new Pnt[list.size()];
                int i = 0;
                for (Triangle tri: list)
                    vertices[i++] = tri.getCircumcenter();
                drawPolygon(vertices, fillType == VisualizeManager.NO_COLOR ? null : getColor(site, fillType));
            }
        
    }

    /**
     * Draw all the empty circles (one for each triangle) of the DT.
     */
    public void drawAllCircles() {
        // Loop through all triangles of the DT
        for (Triangle triangle: visualizeManager.getTriangulation()) {
            // Skip circles involving the initial-triangle vertices
            if (triangle.containsAny(visualizeManager.initialTriangle)) continue;
            Pnt c = triangle.getCircumcenter();
            double radius = c.subtract(triangle.get(0)).magnitude();
            drawCircle(c, radius, null);
        }
    }
    
    /**
     * Draw all the Relative Neighbor Graph edges.
     * @param withSites true iff drawing the site for each point
     */
    public void drawAllRNG(){
    	Graph graph = visualizeManager.getRNGGraph();
    	if (graph == null) return;
    	
    	double ratio = getRatio();
    	for (Node n : network.getNodeList()) {
    		List<WirelessNode> wnodeList = graph.getAdjacentNodes((WirelessNode) n);
    		for (WirelessNode wn : wnodeList) {
    			if (n.getId() < wn.getId()) {
    				gc.drawLine(
    						(int)(((WirelessNode) n).getX() * ratio), 
    						(int)(((WirelessNode) n).getY() * ratio), 
    						(int)(wn.getX() * ratio), 
    						(int)(wn.getY() * ratio));
    			}
    		}
    	}
    }    
    
    /**
     * Draw all the Gabriel edges.
     * @param withSites true iff drawing the site for each point
     */
    public void drawAllGG() {
    	Graph graph = visualizeManager.getGGGraph();
    	if (graph == null) return;
    	
    	double ratio = getRatio();
    	for (Node n : network.getNodeList()) {
    		List<WirelessNode> wnodeList = graph.getAdjacentNodes((WirelessNode) n);
    		for (WirelessNode wn : wnodeList) {
    			if (n.getId() < wn.getId()) {
    				gc.drawLine(
    						(int)(((WirelessNode) n).getX() * ratio), 
    						(int)(((WirelessNode) n).getY() * ratio), 
    						(int)(wn.getX() * ratio), 
    						(int)(wn.getY() * ratio));
    			}
    		}
    	}
    }
    
    public VisualizeManager getVisualizeManager() {
    	return visualizeManager;
    }
    
    public double getRatio() {
    	return ratio;    	
    }
    
    private void showConnection() {
    	Workspace workspace = (Workspace) getParent();
    	
		List<Node> nodeList = workspace.getProject().getNetwork().getNodeList();
		
		for (Node n : nodeList) {
			if (n instanceof WirelessNode) {
				List<WirelessNode> nbList = ((WirelessNode) n).getNeighborList();
				GWirelessNode n1 = workspace.getGraphicNodeById(n.getId());
				
				int x1, y1, x2, y2;
				x1 = n1.getLocation().x - getLocation().x + n1.getSize().x / 2;
				y1 = n1.getLocation().y - getLocation().y + n1.getSize().y / 2;
				
				for (WirelessNode nb : nbList) {							
					GWirelessNode n2 = workspace.getGraphicNodeById(nb.getId());
					
					x2 = n2.getLocation().x - getLocation().x + n2.getSize().x / 2;
					y2 = n2.getLocation().y - getLocation().y + n2.getSize().y / 2;
					gc.drawLine(x1, y1, x2, y2);															
				}
			}
		}    	
    }
    
    public void clearSelectedArea() {
    	selectedArea = new Area();
    }
}
