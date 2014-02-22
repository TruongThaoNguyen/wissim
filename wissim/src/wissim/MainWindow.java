package wissim;

import graphicscomponents.GSelectableObject;
import graphicscomponents.GWirelessNode;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.Separator;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabFolder2Adapter;
import org.eclipse.swt.custom.CTabFolderEvent;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.wb.swt.ResourceManager;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.jface.dialogs.MessageDialog;

import control.manager.ApplicationManager;
import control.manager.ApplicationSettings;
import control.manager.ProjectManager;
import view.Workspace;
import appmanagers.*;
import managers.CareTaker;
import model.Project;
import dialogs.AboutWindow;
import dialogs.ConfigNodeDialog;
import dialogs.GenerateNodeLocationDataDialog;
import dialogs.GenerateSimulationScriptsDialog;
import dialogs.PreferencesDialog;

public class MainWindow extends ApplicationWindow implements Observer {
	private Action actNew;
	private Action actOpen;
	private Action actMouseHand;
	private Action actSave;
	private Action actSaveAs;
	private Action actToImage;
	private Action actClose;
	private Action actExit;
	private Action actUndo;
	private Action actRedo;
	private Action actConfigureNodes;
	private Action actCreateASingleNode;
	private Action actCreateASetOfNodes;
	private Action actManageTrafficFlow;
	private Action actDeleteNodes;
	private Action actViewNetworkInfo;
	private Action actViewNodeInfo;
	private Action actShowObstacles;
	private Action actShowNeighbors;
	private Action actSearchNode;
	private Action actIdentifyBoundary;
	private Action actGenerateNodeLocationData;
	private Action actCheckConnectivity;
	private Action actFindConnectivityParts;
	private Action actChangeNetworkSize;
	private Action actGenerateSimulationScripts;
	private Action actViewDelaunayTriangulation;
	private Action actViewVoronoiDiagram;
	private Action actViewShortestPathTree;
	private Action actViewShortestPath;
	private Action actFindPathByGreedy;
	private Action actViewRNGGraph;
	private Action actViewGGGraph;
	private Action actVisualizeSettings;
	private Action actDefaultConfiguration;
	private Action actDocumentation;
	private Action actDemos;
	private Action actAbout;
	private Action actPrint;

	private Composite container;
	private CTabFolder tabFolder;
	private Action actSaveAll;
	private Action actZoomIn;
	private Action actZoomOut;

	private Action actCreateARandomNode;
	private Action actDeleteAllNodes;
	private Action actShowRange;
	private Action actShowConnection;
	private Action actMouseCursor;
	private Action actMouseCreateNode;
	private Action actShowRulers;
	private Action actMouseCreateArea;

	private final FormToolkit formToolkit = new FormToolkit(Display.getDefault());
	private Action actManagePaths;
	private Action actManageLabels;
	private Action actImport;
	
	private StatesHandler statesHandler;	private MenuManager menuManager;	private MenuManager menuManager_File;	private MenuManager menuManager_Edit;	private MenuManager menuManager_View;	private MenuManager menuManager_Feature;	private MenuManager menuManager_Manage;	private MenuManager menuManager_Generate;	private MenuManager menuMenager;	private MenuManager menuManager_1;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String args[]) {
		try {			
			// load settings
			ApplicationSettings.loadConfig();
	
			// open the main window
			MainWindow window = new MainWindow();
			window.setBlockOnOpen(true);
			window.open();
	
			Display.getCurrent().dispose();
	
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the application window.
	 */
	public MainWindow() {
		super(null);
		createActions();
		addToolBar(SWT.FLAT | SWT.WRAP);
		addMenuBar();
		addStatusLine();

		tabFolder = null;
		
		Display.getCurrent().addFilter(SWT.KeyDown, new Listener() {			
			@Override
			public void handleEvent(Event arg0) {
				Workspace workspace = getActiveWorkspace();
				
				if (workspace != null) {
					switch (arg0.keyCode) {
					case SWT.CTRL:
						workspace.getPropertyManager().setControlKeyPressed(true);
						break;
					case SWT.SHIFT:
						workspace.getPropertyManager().setShiftKeyPressed(true);
						break;
					}
				}
			}
		});
		
		Display.getCurrent().addFilter(SWT.KeyUp,  new Listener() {			
			@Override
			public void handleEvent(Event arg0) {
				Workspace workspace = getActiveWorkspace();
				
				if (workspace != null) {
					if (arg0.keyCode == SWT.CTRL)
						workspace.getPropertyManager().setControlKeyPressed(false);
					if (arg0.keyCode == SWT.SHIFT)
						workspace.getPropertyManager().setShiftKeyPressed(false);
				}
			}
		});
		
		Display.getCurrent().addFilter(SWT.MouseWheel, new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				Workspace workspace = getActiveWorkspace();
				
				if (workspace != null) {
					if (workspace.getPropertyManager().isControlKeyPressed() == true) {
						if ((workspace.getMode() == Workspace.OVERVIEW && arg0.count > 0) || workspace.getMode() == Workspace.EXTEND) {												
							if (workspace.getScale() >= 1 && workspace.getScale() <= 3) {
								workspace.setScale(workspace.getScale() + ((double) arg0.count) / 100);
								
								if (workspace.getScale() < 1)
									workspace.setScale(1);
								if (workspace.getScale() > 3)
									workspace.setScale(3);
								
								int x = (int)(workspace.getGraphicNetwork().getInitLocation().x * 2 + workspace.getGraphicNetwork().getInitSize().x * workspace.getScale());
								int y = (int)(workspace.getGraphicNetwork().getInitLocation().y * 2 + workspace.getGraphicNetwork().getInitSize().y * workspace.getScale());
								
								ScrolledComposite sc = (ScrolledComposite) workspace.getParent();
								
								if (x < sc.getClientArea().width || y < sc.getClientArea().height) {							
									workspace.setSize(sc.getClientArea().width + sc.getVerticalBar().getSize().x, 
											sc.getClientArea().height + sc.getHorizontalBar().getSize().y);
									workspace.setScale(1);
								} else
									workspace.setSize(x, y);
							}
						}						
					}
					
				}				
			}
		});		

		statesHandler = new StatesHandler(this);
		statesHandler.initialize();
	}

	/**
	 * Create contents of the application window.
	 * @param parent
	 */
	@Override
	protected Control createContents(Composite parent) {
		setStatus("Good");
		container = new Composite(parent, SWT.NONE);
		container.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_DARK_SHADOW));
		container.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		getShell().addDisposeListener(new DisposeListener() {
			
			@Override
			public void widgetDisposed(DisposeEvent arg0) {
				List<Workspace> wList = getWorkspaceList();
				for (int i = wList.size() - 1; i >= 0; i--) {
					ApplicationManager.closeWorkspace(wList.get(i));
				}
			}
		});

		return container;
	}

	/**
	 * Create the actions.
	 */
	private void createActions() {
		// Create the actions		
		actNew = new Action("New") {			public void run() {
				Project project = ApplicationManager.newProject(MainWindow.this.getShell()); 
				if (project != null)
					showProject(project);
			}
		};
		actNew.setToolTipText("Create a new project");
		actNew.setAccelerator(SWT.CTRL | 'N');
		actNew.setImageDescriptor(ResourceManager.getImageDescriptor(MainWindow.class, "/icons/application_add.png"));


		actOpen = new Action("Open") {
			public void run() {
				//System.out.println("ssas");
				Project project = ApplicationManager.openProject(MainWindow.this);
				if (project != null)
					showProject(project);
				
				getActiveWorkspace().getSelectableObject().get(getActiveWorkspace().getSelectableObject().size() - 1).moveAbove(null);
			}
		};
		actOpen.setToolTipText("Open existing project");
		actOpen.setAccelerator(SWT.CTRL | 'O');
		actOpen.setImageDescriptor(ResourceManager.getImageDescriptor(MainWindow.class, "/icons/folder.png"));
		
		actMouseHand = new Action("Mouse Hand") {
			public void run() {
				Workspace workspace = getActiveWorkspace();

				if (workspace != null) {
					workspace.getPropertyManager().setMouseMode(WorkspacePropertyManager.HAND);
					workspace.disableNetwork();
				}				
			}
		};
		actMouseHand.setChecked(false);
		actMouseHand.setImageDescriptor(ResourceManager.getImageDescriptor(MainWindow.class, "/icons/closed_hand.png"));


		actSave = new Action("Save") {
			public void run() {						
				ApplicationManager.saveWorkspace(MainWindow.this);
			}
		};
		actSave.setToolTipText("Save current project");
		actSave.setAccelerator(SWT.CTRL | 'S');
		actSave.setImageDescriptor(ResourceManager.getImageDescriptor(MainWindow.class, "/icons/disk.png"));


		actSaveAs = new Action("Save As") {
			public void run() {
				Workspace workspace = getActiveWorkspace();
				ApplicationManager.saveWorkspaceAs(workspace);
			}
		};
		actSaveAs.setAccelerator(SWT.ALT | SWT.CTRL | 'S');


		actToImage = new Action("To Image") {
			public void run() {
				Workspace workspace = getActiveWorkspace();
				ApplicationManager.exportToImage(workspace);
			}
		};


		actClose = new Action("Close") {
			public void run() {
				Workspace workspace = getActiveWorkspace();				
				ApplicationManager.closeWorkspace(workspace);			
			}
		};
		actClose.setAccelerator(SWT.CTRL | SWT.F4);
		actClose.setImageDescriptor(ResourceManager.getImageDescriptor(MainWindow.class, "/icons/cross.png"));


		actExit = new Action("Exit") {
			public void run() {
				getShell().close();
			}
		};	

		actUndo = new Action("Undo") {
			public void run() {
				Workspace workspace = getActiveWorkspace();
				ApplicationManager.undoState(workspace);
			}
		};
		actUndo.setAccelerator(SWT.CTRL | 'Z');
		actUndo.setImageDescriptor(ResourceManager.getImageDescriptor(MainWindow.class, "/icons/arrow_undo.png"));

		actRedo = new Action("Redo") {
			public void run() {
				Workspace workspace = getActiveWorkspace();
				ApplicationManager.redoState(workspace);
			}
		};
		actRedo.setAccelerator(SWT.CTRL | 'Y');
		actRedo.setImageDescriptor(ResourceManager.getImageDescriptor(MainWindow.class, "/icons/arrow_redo.png"));

		actConfigureNodes = new Action("Configure Node(s)") {
			public void run() {
				Workspace workspace = getActiveWorkspace();
				
				if (workspace != null)
					new ConfigNodeDialog(getShell(), SWT.SHEET, ConfigNodeDialog.PROJECT_CONFIG, workspace).open();				
			}
		};

		actCreateASingleNode = new Action("A Single Node") {
			public void run() {
				Workspace workspace = getActiveWorkspace();
				ApplicationManager.createASingleNode(workspace);
			}
		};
		actCreateASingleNode.setAccelerator(SWT.ALT | SWT.CTRL | SWT.SHIFT | 'N');

		actCreateASetOfNodes = new Action("A Set of Nodes") {
			public void run() {
				Workspace workspace = getActiveWorkspace();
				ApplicationManager.createASetOfNodes(workspace);
			}
		};

		actCreateASetOfNodes.setAccelerator(SWT.ALT | SWT.CTRL | SWT.SHIFT | 'M');

		actManageTrafficFlow = new Action("Traffic Flow Manager") {
			public void run() {
				Workspace workspace = getActiveWorkspace();
				ApplicationManager.manageTrafficFlow(workspace);
			}
		};

		actDeleteNodes = new Action("Delete Node(s)") {
			public void run() {
				Workspace workspace = getActiveWorkspace();
				ApplicationManager.deleteNodes(workspace);
			}
		};
		actDeleteNodes.setAccelerator(SWT.DEL);


		actViewNetworkInfo = new Action("Network Properties") {
			public void run() {
				Workspace workspace = getActiveWorkspace();
				ApplicationManager.viewNetworkInfo(workspace);
			}
		};


		actViewNodeInfo = new Action("Node Properties") {
			public void run() {
				Workspace workspace = getActiveWorkspace();
				if (workspace == null) return;
				
				List<GSelectableObject> objList = workspace.getSelectedObject();
				if (objList.size() == 1 && objList.get(0) instanceof GWirelessNode)
					ApplicationManager.viewNodeInfo(workspace, (GWirelessNode) objList.get(0));
			}
		};
		actViewNodeInfo.setAccelerator(SWT.CTRL | 'I');


		actShowObstacles = new Action("Show Obstacle(s)") {
			public void run() {
				Workspace workspace = getActiveWorkspace();
				ApplicationManager.showObstacles(workspace);
			}
		};		
		actShowObstacles.setToolTipText("Show obstacles");
		actShowObstacles.setAccelerator(SWT.ALT | SWT.CTRL | 'O');

		actShowNeighbors = new Action("Show Neighbors") {
			public void run() {
				Workspace workspace = getActiveWorkspace();
				ApplicationManager.showNeighbors(workspace);
			}
		};	
		actShowNeighbors.setToolTipText("Show neighbors of the selected node(s)");
		actShowNeighbors.setAccelerator(SWT.ALT | SWT.CTRL | 'N');

		actSearchNode = new Action("Search Node") {
			public void run() {
				Workspace workspace = getActiveWorkspace();
				ApplicationManager.searchNodes(workspace);
			}
		};	
		actSearchNode.setAccelerator(SWT.CTRL | 'F');

		actIdentifyBoundary = new Action("Boundary Identification") {
			public void run() {
				MessageDialog.openInformation(getShell(), "Unavailable feature", "This feature is currently unavailable. Please wait for the next version");
			}
		};	

		actGenerateNodeLocationData = new Action("Node Location Data") {
			public void run() {
				Workspace workspace = getActiveWorkspace();

				if (workspace != null)
					new GenerateNodeLocationDataDialog(getShell(), SWT.SHEET, workspace.getProject()).open();
			}
		};	
		actGenerateNodeLocationData.setAccelerator(SWT.ALT | SWT.CTRL | 'D');

		actCheckConnectivity = new Action("Check Connectivity") {
			public void run() {
				Workspace workspace = getActiveWorkspace();
				ApplicationManager.checkConnectivity(workspace);
			}
		};
		actCheckConnectivity.setToolTipText("Check the connectivity of network");

		actFindConnectivityParts = new Action("Connectivity Parts") {
		};
		actFindConnectivityParts.setToolTipText("Find out connectivity parts in the network");

		actChangeNetworkSize = new Action("Change Network Size") {
			public void run() {
				Workspace workspace = getActiveWorkspace();
				ApplicationManager.changeNetworkSize(workspace);
			}
		};
		actChangeNetworkSize.setAccelerator(SWT.ALT | SWT.CTRL | SWT.SHIFT | 'D');

		actGenerateSimulationScripts = new Action("Simulation Scripts") {
			public void run() {
				new GenerateSimulationScriptsDialog(getShell(), SWT.SHEET, getActiveWorkspace()).open();
			}
		};
		actGenerateSimulationScripts.setAccelerator(SWT.ALT | SWT.CTRL | 'G');

		actViewDelaunayTriangulation = new Action("Delaunay Triangulation") {
			public void run() {
				Workspace workspace = getActiveWorkspace();
				if (workspace != null) {
					ApplicationManager.showDelaunayTriangulation(workspace);
					
				}
			}
			
		};	

		actViewVoronoiDiagram = new Action("Voronoi Diagram") {
			public void run() {
				Workspace workspace = getActiveWorkspace();
				if (workspace != null)
					ApplicationManager.showVoronoiDiagram(workspace);
			}
		};	

		actViewShortestPathTree = new Action("Shortest Path Tree") {
			
		};	

		actViewShortestPath = new Action("Shortest Path") {
			public void run() {
				Workspace workspace = getActiveWorkspace();
				if (workspace == null) return;

				if (workspace.getSelectedObject().size() == 2) {				
					GWirelessNode gStartNode = (GWirelessNode) workspace.getSelectedObject().get(0);
					GWirelessNode gEndNode = (GWirelessNode) workspace.getSelectedObject().get(1);
					ApplicationManager.findShortestPath(gStartNode, gEndNode);
				}
			}
		};

		actFindPathByGreedy = new Action("Path by Greedy") {
			public void run() {
				Workspace workspace = getActiveWorkspace();

				if (workspace == null) return;				

				if (workspace.getSelectedObject().size() == 2) {
					
					GWirelessNode gStartNode = (GWirelessNode) workspace.getSelectedObject().get(0);
					GWirelessNode gEndNode = (GWirelessNode) workspace.getSelectedObject().get(1);	
					ApplicationManager.findGreedyPath(gStartNode, gEndNode);
				}
			}
		};	

		actViewRNGGraph = new Action("RNG") {
			public void run() {
				Workspace workspace = getActiveWorkspace();
				if (workspace != null)
					ApplicationManager.showRNG(workspace);
			}
		};

		actViewGGGraph = new Action("GG") {
			public void run() {
				Workspace workspace = getActiveWorkspace();
				if (workspace != null)
					ApplicationManager.showGG(workspace);
			}
		};	

		actVisualizeSettings = new Action("Preferences") {
			public void run() {
				new PreferencesDialog(getShell(), SWT.SHEET, MainWindow.this).open();
			}
		};
		actVisualizeSettings.setImageDescriptor(null);

		actDefaultConfiguration = new Action("Node Default Configuration") {
			public void run() {
				new ConfigNodeDialog(getShell(), SWT.SHEET, ConfigNodeDialog.APP_CONFIG, null).open();
			}
		};	
		actDefaultConfiguration.setImageDescriptor(ResourceManager.getImageDescriptor(MainWindow.class, "/icons/cog.png"));

		actDocumentation = new Action("Documentation") {
			public void run() {
				Desktop dt = Desktop.getDesktop();
				File docFile = null;
				try {
					docFile = new File(MainWindow.class.getResource("help.chm").getPath());
					dt.open(docFile);
				} catch (Exception e) {
					docFile = new File("doc/help.chm");
					
					try {
					dt.open(docFile);
					} catch (IOException ex) {
						MessageDialog.openError(getShell(), "Cannot open documentation", "The documentation may not exist or has been deleted");
					}
				}
			}
		};	
		actDocumentation.setImageDescriptor(ResourceManager.getImageDescriptor(MainWindow.class, "/icons/book.png"));

		actDemos = new Action("Demos") {
			public void run() {
				MessageDialog.openInformation(getShell(), "Ten Ten", "Will be available in the next version.");
			}
		};	
		actDemos.setImageDescriptor(ResourceManager.getImageDescriptor(MainWindow.class, "/icons/dvd.png"));

		actAbout = new Action("About") {			public void run() {
				new AboutWindow(null).open();					
			}
		};		
		actAbout.setImageDescriptor(ResourceManager.getImageDescriptor(MainWindow.class, "/icons/information.png"));

		actPrint = new Action("Print") {
			public void run() {
				ApplicationManager.print(getActiveWorkspace());
			}
		};
		actPrint.setAccelerator(SWT.CTRL | 'P');
		actPrint.setImageDescriptor(ResourceManager.getImageDescriptor(MainWindow.class, "/icons/printer.png"));


		actSaveAll = new Action("Save All") {
			public void run() {
//				try {
//					for (Project project : ProjectManager.projectList()) {
//						ProjectManager.saveProject(project);
//					}
//				} catch (IOException e) {
//					MessageDialog.openError(getShell(), "Saving Error", "Something wrong happened. Cannot save all projects");
//				}
			}
		};
		actSaveAll.setToolTipText("Save all opened project");
		actSaveAll.setImageDescriptor(ResourceManager.getImageDescriptor(MainWindow.class, "/icons/disk_multiple.png"));


		actZoomIn = new Action("Zoom In") {			public void run() {
				Workspace w = getActiveWorkspace();
				w.setSize(w.getSize().x + 10, w.getSize().y + 10);
			}
		};
		actZoomIn.setImageDescriptor(ResourceManager.getImageDescriptor(MainWindow.class, "/icons/zoom_in.png"));
		actZoomIn.setAccelerator(SWT.CTRL | '=');


		actZoomOut = new Action("Zoom Out") {			public void run() {
				Workspace w = getActiveWorkspace();
				w.setSize(w.getSize().x - 10, w.getSize().y - 10);
			}
		};
		actZoomOut.setImageDescriptor(ResourceManager.getImageDescriptor(MainWindow.class, "/icons/zoom_out.png"));
		actZoomOut.setAccelerator(SWT.CTRL | '-');


		actCreateARandomNode = new Action("A Random Node") {
			public void run() {
				Workspace w = getActiveWorkspace();
				ApplicationManager.createARandomNode(w);
			}
		};
		actCreateARandomNode.setAccelerator(SWT.CTRL | 'R');


		actDeleteAllNodes = new Action("Delete All Nodes") {
			public void run() {
				Workspace w = getActiveWorkspace();
				ApplicationManager.deleteAllNodes(w);
			}
		};

		actShowRange = new Action("Show Range") {			public void run() {
				Workspace workspace = getActiveWorkspace();
				ApplicationManager.showRange(workspace);
			}
		};
		actShowRange.setToolTipText("Show radio range of the selected node(s)");
		actShowRange.setAccelerator(SWT.ALT | SWT.CTRL | 'R');

		actShowConnection = new Action("Show Connection") {
			public void run() {
				Workspace workspace = getActiveWorkspace();
				
				if (workspace.getPropertyManager().isShowConnection()) {
					ApplicationManager.showConnection(workspace, false);
					actShowConnection.setChecked(false);
				} else {
					ApplicationManager.showConnection(workspace, true);
					actShowConnection.setChecked(true);					
				}
			}
		};
		actShowConnection.setToolTipText("Show network connection");
		actShowConnection.setChecked(false);
		actShowConnection.setAccelerator(SWT.ALT | SWT.CTRL | 'C');

		actMouseCursor = new Action("Mouse Cursor") {
			public void run() {
				Workspace workspace = getActiveWorkspace();

				if (workspace != null) {
					workspace.getPropertyManager().setMouseMode(WorkspacePropertyManager.CURSOR);
					workspace.enableNetwork();
				}				
			}
		};
		actMouseCursor.setChecked(true);
		actMouseCursor.setImageDescriptor(ResourceManager.getImageDescriptor(MainWindow.class, "/icons/arrow_cursor.png"));

		actMouseCreateNode = new Action("Create New Node by Mouse Click") {
			public void run() {
				Workspace workspace = getActiveWorkspace();

				if (workspace != null) {
					workspace.getPropertyManager().setMouseMode(WorkspacePropertyManager.NODE_GEN);
					workspace.enableNetwork();
				}
			}
		};
		actMouseCreateNode.setToolTipText("Create new nodes by mouse click");
		actMouseCreateNode.setImageDescriptor(ResourceManager.getImageDescriptor(MainWindow.class, "/icons/Point-Black.png"));
		actMouseCreateNode.setChecked(false);

		actShowRulers = new Action("Show Rulers") {			public void run() {
				Workspace workspace = getActiveWorkspace();

				if (workspace != null) {
					RulerScrolledComposite sc = (RulerScrolledComposite) workspace.getParent();

					if (!sc.isRulersShown()) {
						sc.showRulers();
						actShowRulers.setChecked(true);
					}
					else {
						sc.hideRulers();
						actShowRulers.setChecked(false);
					}
				}
			}
		};
		actShowRulers.setChecked(true);

		actMouseCreateArea = new Action("Create New Area") {			public void run() {
				Workspace workspace = getActiveWorkspace();

				if (workspace != null) {
					workspace.getPropertyManager().setMouseMode(WorkspacePropertyManager.AREA);
					workspace.enableNetwork();
					
					workspace.disableNodes();
					workspace.getPropertyManager().turnVisualizeOff();
					workspace.deselectGraphicObjects();
				}		
			}
		};
		actMouseCreateArea.setToolTipText("Create new area");
		actMouseCreateArea.setChecked(false);
		actMouseCreateArea.setImageDescriptor(ResourceManager.getImageDescriptor(MainWindow.class, "/icons/PolygonSetIcon.png"));

		actManagePaths = new Action("Path Manager") {
			public void run() {
				Workspace workspace = getActiveWorkspace();
				ApplicationManager.managePaths(workspace);
			}
		};

		actManageLabels = new Action("Label Manager") {
			public void run() {
				Workspace workspace = getActiveWorkspace();
				ApplicationManager.manageLabels(workspace);
			}
		};

		{
			actImport = new Action("Import Location Data") {				public void run() {
					Workspace workspace = getActiveWorkspace();
					ApplicationManager.importLocationData(workspace);
				}
			};
		}
	}

	/**
	 * Create the menu manager.
	 * @return the menu manager
	 */
	@Override
	protected MenuManager createMenuManager() {
		menuManager = new MenuManager("menu");
		{
			menuManager_File = new MenuManager("&File");
			menuManager.add(menuManager_File);
			menuManager_File.add(actNew);
			menuManager_File.add(actOpen);
			menuManager_File.add(actSave);
			menuManager_File.add(actSaveAs);
			menuManager_File.add(actSaveAll);
			menuManager_File.add(new Separator());
			menuManager_File.add(actImport);
			{
				MenuManager menuManager_2 = new MenuManager("Export");
				menuManager_File.add(menuManager_2);
				menuManager_2.add(actToImage);
//				menuManager_2.add(actToPDF);
			}
			menuManager_File.add(actPrint);
			menuManager_File.add(new Separator());
			menuManager_File.add(actClose);
			menuManager_File.add(actExit);
		}
		{
			menuManager_Edit = new MenuManager("&Edit");
			menuManager.add(menuManager_Edit);
			menuManager_Edit.add(actUndo);
			menuManager_Edit.add(actRedo);
			menuManager_Edit.add(new Separator());
			menuManager_Edit.add(actChangeNetworkSize);
			menuManager_Edit.add(actConfigureNodes);
			{
				MenuManager menuManager_2 = new MenuManager("Create");
				menuManager_Edit.add(menuManager_2);
				menuManager_2.add(actCreateASingleNode);
				menuManager_2.add(actCreateASetOfNodes);
				menuManager_2.add(new Separator());
				menuManager_2.add(actCreateARandomNode);
			}
			menuManager_Edit.add(new Separator());
			menuManager_Edit.add(actDeleteNodes);
			menuManager_Edit.add(actDeleteAllNodes);
		}
		{
			menuManager_View = new MenuManager("&View");
			menuManager.add(menuManager_View);
			menuManager_View.add(actZoomIn);
			menuManager_View.add(actZoomOut);
			menuManager_View.add(new Separator());
			menuManager_View.add(actViewNetworkInfo);
			menuManager_View.add(actViewNodeInfo);
			menuManager_View.add(new Separator());
			menuManager_View.add(actShowRange);
			menuManager_View.add(actShowNeighbors);
			menuManager_View.add(new Separator());
			menuManager_View.add(actShowConnection);
			menuManager_View.add(actShowObstacles);
			menuManager_View.add(new Separator());
			menuManager_View.add(actShowRulers);
		}
		{
			menuManager_Feature = new MenuManager("Features");
			menuManager.add(menuManager_Feature);
			menuManager_Feature.add(actSearchNode);
			menuManager_Feature.add(actIdentifyBoundary);
			menuManager_Feature.add(new Separator());
			menuManager_Feature.add(actCheckConnectivity);
			{
				MenuManager menuManager_2 = new MenuManager("Planar Graph");
				menuManager_Feature.add(menuManager_2);
				menuManager_2.add(actViewRNGGraph);
				menuManager_2.add(actViewGGGraph);
			}
			menuManager_Feature.add(actViewVoronoiDiagram);
			menuManager_Feature.add(actViewDelaunayTriangulation);
			menuManager_Feature.add(new Separator());
			menuManager_Feature.add(actFindPathByGreedy);
			menuManager_Feature.add(actViewShortestPath);
			menuManager_Feature.add(actViewShortestPathTree);
		}
		menuManager_Manage = new MenuManager("Manager");
		menuManager.add(menuManager_Manage);
		menuManager_Manage.add(actManageLabels);
		menuManager_Manage.add(actManagePaths);
		menuManager_Manage.add(actManageTrafficFlow);
		{
			menuManager_Generate = new MenuManager("&Generate");
			menuManager.add(menuManager_Generate);
			menuManager_Generate.add(actGenerateSimulationScripts);
			menuManager_Generate.add(actGenerateNodeLocationData);
		}
		{
			menuMenager = new MenuManager("&Settings");
			menuManager.add(menuMenager);
			menuMenager.add(actDefaultConfiguration);
			menuMenager.add(actVisualizeSettings);
		}
		{
			menuManager_1 = new MenuManager("&Help");
			menuManager.add(menuManager_1);
			menuManager_1.add(actDocumentation);
			menuManager_1.add(actDemos);
			menuManager_1.add(new Separator());
			menuManager_1.add(actAbout);
		}
		return menuManager;
	}

	/**
	 * Create the toolbar manager.
	 * @return the toolbar manager
	 */
	@Override
	protected ToolBarManager createToolBarManager(int style) {
		ToolBarManager toolBarManager = new ToolBarManager(SWT.WRAP);
		toolBarManager.add(actNew);
		toolBarManager.add(actOpen);
		toolBarManager.add(actSave);
		toolBarManager.add(actSaveAll);
		Separator separator = new Separator();
		toolBarManager.add(separator);
		toolBarManager.add(actMouseHand);
		toolBarManager.add(actMouseCursor);
		toolBarManager.add(actMouseCreateNode);
		toolBarManager.add(actMouseCreateArea);
		return toolBarManager;
	}

	/**
	 * Create the status line manager.
	 * @return the status line manager
	 */
	@Override
	protected StatusLineManager createStatusLineManager() {
		StatusLineManager statusLineManager = new StatusLineManager();
		statusLineManager.setCancelEnabled(true);
		statusLineManager.setErrorMessage("Something Wrong");
		return statusLineManager;
	}

	/**
	 * Configure the shell.
	 * @param newShell
	 */
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("WiSSim Editor");
	}

	/**
	 * Return the initial size of the window.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(0, 0);
	}

	/**
	 * Get active workspace
	 * @return
	 */
	public Workspace getActiveWorkspace() {
		if (tabFolder == null || tabFolder.isDisposed() == true)
			return null;
		
		return (Workspace)((ScrolledComposite)tabFolder.getSelection().getControl()).getContent();		
	}
	
	public List<Workspace> getWorkspaceList() {
		List<Workspace> wList = new LinkedList<Workspace>();
		
		if (tabFolder != null && !tabFolder.isDisposed()) {
			for (CTabItem item : tabFolder.getItems()) {
				wList.add((Workspace) ((ScrolledComposite) item.getControl()).getContent());
			}
		}			
					
		return wList;			
	}
	
	public void showProject(Project project) {	
		// create the TabFolder if it doesn't exist
		if (tabFolder == null || tabFolder.isDisposed() == true) {
			tabFolder = new CTabFolder(container, SWT.BORDER);
			try {
				formToolkit.adapt(tabFolder);
			} catch (Exception e) {}

			tabFolder.setBounds(0, 0, container.getSize().x, container.getSize().y);
			tabFolder.setSelectionBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
			tabFolder.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));

			tabFolder.addCTabFolder2Listener(new CTabFolder2Adapter() {
				@Override
				public void close(CTabFolderEvent arg0) {
					Workspace workspace = getActiveWorkspace();
					if (workspace != null) {
						// if workspace is not saved yet, try to save it before closing
								if (workspace.isChanged()) {
									MessageBox dialog = new MessageBox(workspace.getShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO | SWT.CANCEL);
									dialog.setText("Saving Confirmation");
									dialog.setMessage("Do you want to save change(s) applied to project " + workspace.getProject().getNetwork().getName() + "?");
									
									int result = dialog.open();
									
									switch (result) {
									case SWT.YES:
										try {
											ProjectManager.saveProject(workspace.getProject());
										} catch (IOException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
										ApplicationManager.closeWorkspace(workspace);
										break;
									case SWT.NO:
										ApplicationManager.closeWorkspace(workspace);
										break;
									case SWT.CANCEL:
									    arg0.doit=false;
										break;
									}			
								}
								else
									ApplicationManager.closeWorkspace(workspace);
					}
				  }
			});
			
			tabFolder.addDisposeListener(new DisposeListener() {				
				@Override
				public void widgetDisposed(DisposeEvent arg0) {
					statesHandler.initialize();
				}
			});
			
		}
		

		// add a new tab item to the TabFolder
		final CTabItem newItem = new CTabItem(tabFolder, SWT.NONE);
		newItem.setText(project.getNetwork().getName());
		newItem.setShowClose(true);
		tabFolder.setSelection(newItem);
		for(int i=0;i<tabFolder.getItemCount()-1;i++) { 
			tabFolder.getItems()[i].setShowClose(false);
		 }
		tabFolder.addSelectionListener(new SelectionAdapter() {
			  public void widgetSelected(org.eclipse.swt.events.SelectionEvent event) {
				//  System.out.println(tabFolder.getItems()[0].getText() + " selected"); // This should be your TabItem/CTabItem  
				 for(int i=0;i<tabFolder.getItemCount();i++) {
					if(tabFolder.getItems()[i].equals(tabFolder.getSelection()))
						tabFolder.getItems()[i].setShowClose(true);
					else 
						tabFolder.getItems()[i].setShowClose(false);
				 }
			  }
			});

		// add a scrolled composite item
		final RulerScrolledComposite scrolledComposite = new RulerScrolledComposite(tabFolder, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		newItem.setControl(scrolledComposite);

		final Workspace workspace = new Workspace(scrolledComposite, SWT.NONE, project);
		workspace.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_DARK_SHADOW));
		workspace.setSize(scrolledComposite.getClientArea().width, scrolledComposite.getClientArea().height);
		scrolledComposite.setContent(workspace);

		workspace.setFocus();	
		scrolledComposite.initializeRulers();
		
		// add MainWindow as an observer to track changes of workspace property
		WorkspacePropertyManager propManager = workspace.getPropertyManager();
		propManager.addObserver(this);
		
		workspace.getCareTaker().addObserver(this);
		
		statesHandler.activeProject();
	}		

	@Override
	public void update(Observable arg0, Object arg1) {
		Workspace workspace = getActiveWorkspace();
		
		if (arg0 instanceof WorkspacePropertyManager) {
			String desc = (String) arg1;
			
			switch (desc) {
			case "SetMouseModeCursor":
				statesHandler.activeMouseCursor();
				break;
			case "SetMouseModeHand":
				statesHandler.activeMouseHand();
				break;
			case "SetMouseModeNodeGen":
				statesHandler.activeMouseCreateNode();
				break;
			case "SetMouseModeCreateArea":
				statesHandler.activeMouseCreateArea();
				break;
			case "ShowVisualization":
				workspace.setObstaclesVisible(false);
				break;
			case "TurnVisualizeOff":
				workspace.getGraphicNetwork().redraw();
				break;
			}
		}
		
		if (arg0 instanceof CareTaker) {
			// get current state index
			
			int currState = (int) arg1;
			
			// check whether it can be undo or redo
			if (currState == 0) 
				actUndo.setEnabled(false); 
			else 
				actUndo.setEnabled(true);
			
			if (currState == workspace.getCareTaker().getSize() - 1) 
				actRedo.setEnabled(false); 
			else 
				actRedo.setEnabled(true);
			
			// check save state
			if (!workspace.isChanged()) {
				actSave.setEnabled(false);				
				tabFolder.getSelection().setText(getActiveWorkspace().getProject().getNetwork().getName());
			} else {
				actSave.setEnabled(true);
				tabFolder.getSelection().setText(getActiveWorkspace().getProject().getNetwork().getName() + "*");
			}
		}
	}

	public Action getActMouseHand() {
		return actMouseHand;
	}

	public Action getActClose() {
		return actClose;
	}

	public Action getActConfigureNodes() {
		return actConfigureNodes;
	}

	public Action getActCreateASingleNode() {
		return actCreateASingleNode;
	}

	public Action getActCreateASetOfNodes() {
		return actCreateASetOfNodes;
	}

	public Action getActManageTrafficFlow() {
		return actManageTrafficFlow;
	}

	public Action getActDeleteNodes() {
		return actDeleteNodes;
	}

	public Action getActIdentifyBoundary() {
		return actIdentifyBoundary;
	}

	public Action getActGenerateNodeLocationData() {
		return actGenerateNodeLocationData;
	}

	public Action getActCheckConnectivity() {
		return actCheckConnectivity;
	}

	public Action getActFindConnectivityParts() {
		return actFindConnectivityParts;
	}

	public Action getActChangeNetworkSize() {
		return actChangeNetworkSize;
	}

	public Action getActGenerateSimulationScripts() {
		return actGenerateSimulationScripts;
	}

	public Action getActFindPathByGreedy() {
		return actFindPathByGreedy;
	}

	public Action getActCreateARandomNode() {
		return actCreateARandomNode;
	}

	public Action getActDeleteAllNodes() {
		return actDeleteAllNodes;
	}

	public Action getActMouseCursor() {
		return actMouseCursor;
	}

	public Action getActMouseCreateNode() {
		return actMouseCreateNode;
	}

	public Action getActManagePaths() {
		return actManagePaths;
	}

	public Action getActManageLabels() {
		return actManageLabels;
	}

	public Action getActImport() {
		return actImport;
	}

	public Action getActSave() {
		return actSave;
	}

	public Action getActSaveAs() {
		return actSaveAs;
	}

	public Action getActToImage() {
		return actToImage;
	}

//	public Action getActToPDF() {
//		return actToPDF;
//	}

	public Action getActUndo() {
		return actUndo;
	}

	public Action getActRedo() {
		return actRedo;
	}

	public Action getActViewNetworkInfo() {
		return actViewNetworkInfo;
	}

	public Action getActViewNodeInfo() {
		return actViewNodeInfo;
	}

	public Action getActShowObstacles() {
		return actShowObstacles;
	}

	public Action getActShowNeighbors() {
		return actShowNeighbors;
	}

	public Action getActSearchNode() {
		return actSearchNode;
	}

	public Action getActViewDelaunayTriangulation() {
		return actViewDelaunayTriangulation;
	}

	public Action getActViewVoronoiDiagram() {
		return actViewVoronoiDiagram;
	}

	public Action getActViewShortestPathTree() {
		return actViewShortestPathTree;
	}

	public Action getActViewShortestPath() {
		return actViewShortestPath;
	}

	public Action getActViewRNGGraph() {
		return actViewRNGGraph;
	}

	public Action getActViewGGGraph() {
		return actViewGGGraph;
	}

	public Action getActPrint() {
		return actPrint;
	}

	public Action getActSaveAll() {
		return actSaveAll;
	}

	public Action getActZoomIn() {
		return actZoomIn;
	}

	public Action getActZoomOut() {
		return actZoomOut;
	}

	public Action getActShowRange() {
		return actShowRange;
	}

	public Action getActShowConnection() {
		return actShowConnection;
	}

	public Action getActShowRulers() {
		return actShowRulers;
	}

	public Action getActMouseCreateArea() {
		return actMouseCreateArea;
	}
	
	
}