package views;

import java.awt.Container;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import models.Project;
import models.managers.CareTaker;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
//import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.wb.swt.ResourceManager;
import org.eclipse.wb.swt.SWTResourceManager;

import controllers.actions.EditorActionController;
import controllers.converter.Converter;
import controllers.graphicscomponents.GWirelessNode;
import controllers.managers.ApplicationManager;
import controllers.managers.ApplicationSettings;
import controllers.managers.ProjectManager;
import controllers.managers.WorkspacePropertyManager;
import views.MainContent;
import views.RulerScrolledComposite;
import views.Workspace;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

/*
 * Author: Trong Nguyen
 * 
 */

public class Editor extends MainContent implements Observer {

	/**
	 * Create the composite.
	 * @param parent
	 * @param menuManager
	 * @param statusLineManager
	 */	
	public Editor(Composite parent, MenuManager menuManager, StatusLineManager statusLineManager) {
		super(parent, menuManager, statusLineManager);
		
		try {
			ApplicationSettings.loadConfig();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		createContent();
		createAction();
		createMenu();	
		createToolBar();
		
		actionGetTab();
		actionEvent();
		
		Display.getCurrent().addFilter(SWT.KeyDown, new Listener() {			
			@Override
			public void handleEvent(Event arg0) {
				Workspace workspace = getWorkspace();
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
				Workspace workspace = getWorkspace();
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
				Workspace workspace = getWorkspace();
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
	 * createContent
	 */
	private void createContent() {
		setLayout(new GridLayout(1, false));
		

		// ------------- toolbar ------------- //
		
//		Composite toolbarComposite = new Composite(this,  SWT.BORDER);
//		GridData gd_toolbarComposite = new GridData(SWT.LEFT, SWT.TOP, true, false, 1, 1);
//		gd_toolbarComposite.heightHint = 33;
//		toolbarComposite.setLayoutData(gd_toolbarComposite);
		
		toolBar = new ToolBar(this, SWT.RIGHT);	
//		toolBar.setBounds(0, 0, 727, 53);
		toolBar.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		
		toolBarManager = new ToolBarManager(toolBar);
		
				
				
		
		// ------------- main composite ------------- //
		
		sashForm = new SashForm(this, SWT.HORIZONTAL);
		sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		// ------------- properties composite ------------- //
		
		
		SashForm subSashForm = new SashForm(sashForm, SWT.VERTICAL);
		
		contentComposite = new Composite(subSashForm, SWT.NONE);
		contentComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		contentComposite.setLayout(new FillLayout());
		
				tabFolder = new CTabFolder(contentComposite, SWT.BORDER | SWT.FLAT | SWT.BOTTOM);
				tabFolder.setSelectionBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
				
				CTabItem tbtmEdit = new CTabItem(tabFolder, SWT.PUSH);
				tbtmEdit.setText("Edit");
				
				Composite composite = new Composite(tabFolder, SWT.BORDER);
				tbtmEdit.setControl(composite);
				composite.setLayout(new FillLayout(SWT.HORIZONTAL));
				
				styledText = new StyledText(composite, SWT.BORDER);
				
				CTabItem tbtmDesign = new CTabItem(tabFolder, SWT.PUSH);
				tbtmDesign.setText("Design");
//				scrolledComposite = new RulerScrolledComposite(tabFolder, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
////				scrolledComposite = new RulerScrolledComposite(tabFolder, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
//				tbtmDesign.setControl(scrolledComposite);
//				scrolledComposite.setExpandHorizontal(true);
//				scrolledComposite.setExpandVertical(true);
				
				Composite bottomComposite = new Composite(subSashForm, SWT.NONE);
				bottomComposite.setLayout(new GridLayout(1, false));

								StyledText styledText = new StyledText(bottomComposite, SWT.BORDER);
								GridData gd_styledText = new GridData(SWT.FILL, SWT.FILL, true, true);
								gd_styledText.heightHint = 91;
								styledText.setLayoutData(gd_styledText);
				
				subSashForm.setWeights(new int[] {215, 83});
				propertiesComposite = new Composite(sashForm,  SWT.BORDER | SWT.H_SCROLL);
				GridData gd_propertiesComposite = new GridData(SWT.RIGHT, SWT.FILL, false, true);
				
				propertiesComposite.setLayoutData(gd_propertiesComposite);
				gd_propertiesComposite.heightHint = 50;
				
				Label lblNewLabel_1 = new Label(propertiesComposite, SWT.NONE);
				lblNewLabel_1.setBounds(0, 0, 59, 95);
				lblNewLabel_1.setText("Network");
				
				Label lblNewLabel_2 = new Label(propertiesComposite, SWT.NONE);
				lblNewLabel_2.setBounds(0, 116, 59, 14);
				lblNewLabel_2.setText("Node");
				
				Label lblSss = new Label(propertiesComposite, SWT.SEPARATOR | SWT.HORIZONTAL | SWT.CENTER);
				lblSss.setText("sss");
				lblSss.setBounds(-5, 101, 64, 22);
				sashForm.setWeights(new int[] {418, 75});


	}
	
	public void actionGetTab() {
		
		
		tabFolder.addSelectionListener(new SelectionAdapter() {
			  public void widgetSelected(org.eclipse.swt.events.SelectionEvent event) {
				  if(tabFolder.getSelectionIndex() == 0){
					  ec.updateEditToDesign(Editor.this, styledText);
				  }
				  else{
					  System.out.println("vao day ngay tab 1");
				  }
//			    tabFolder.getSelection()[0]; // This should be your TabItem/CTabItem
			  }
			});
	}

	@Override
	protected void updateMenu() {
		menuManager.add(menuManager_File);
//		menuManager.add(menuManager_Edit);
//		menuManager.add(menuManager_View);
//		menuManager.add(menuManager_Feature);
//		menuManager.add(menuManager_Manage);
//		menuManager.add(menuManager_Generate);
//		menuManager.add(menuMenager_Setting);
		menuManager.add(menuManager_Help);
	}
	
	private void createMenu() {				
	{	// ------------- File ------------- //

		menuManager_File = new MenuManager("&File");
		menuManager_File.add(actNew);
//		
	}
	
	{	// ------------- Help ------------- //
		menuManager_Help = new MenuManager("&Help");		
		//menuManager_Help.add(actDocumentation);
		//menuManager_Help.add(actDemos);
		menuManager_Help.add(new Separator());
		menuManager_Help.add(actAbout);
	}	
	}
	
	/**
	 * Create the actions.
	 */
	private void createAction() {
		ec = new EditorActionController(getParent().getShell());
		actNew = new Action("New") {
			public void run() {
				ec.actionNew(Editor.this);
			}
			
		};
		actNew.setToolTipText("Create a new project (CTRL + N)");
		actNew.setAccelerator(SWT.CTRL | 'N');
		actNew.setImageDescriptor(ResourceManager.getImageDescriptor(Editor.class, "/icons/application_add.png"));


		actOpen = new Action("Open") {
			public void run() {
				ec.actionOpen(Editor.this);
			}
		};
		actOpen.setToolTipText("Open existing project (CTRL + O)");
		actOpen.setAccelerator(SWT.CTRL | 'O');
		actOpen.setImageDescriptor(ResourceManager.getImageDescriptor(Editor.class, "/icons/folder.png"));
		
		
		actMouseHand = new Action("Mouse Hand") {
			public void run() {
				ec.actionMouseHand(Editor.this,getWorkspace());
			}
		};
		actMouseHand.setToolTipText("Mouse Hand (CTRL + U)");
		actMouseHand.setChecked(false);
		actMouseHand.setImageDescriptor(ResourceManager.getImageDescriptor(Editor.class, "/icons/closed_hand.png"));


		actSave = new Action("Save") {
			public void run() {						
				ec.actionSave(Editor.this);
			}
		};
		actSave.setToolTipText("Save current project (CTRL + S)");
		actSave.setAccelerator(SWT.CTRL | 'S');
		actSave.setImageDescriptor(ResourceManager.getImageDescriptor(Editor.class, "/icons/disk.png"));


		actSaveAs = new Action("Save As") {
			public void run() {
				Workspace workspace = getWorkspace();
				ApplicationManager.saveWorkspaceAs(workspace);
			}
		};
		actSaveAs.setToolTipText("Save As  (ALT + S)");
//		actSaveAs.setAccelerator(SWT.ALT | SWT.CTRL | 'S');


		actToImage = new Action("To Image") {
			public void run() {
				ec.actionToImage(getWorkspace());
			}
		};
		actToImage.setToolTipText("To Image (SHIFT + I)");


		actClose = new Action("Close") {
			public void run() {
				ec.actionClose(getWorkspace());			
			}
		};
//		actClose.setAccelerator(SWT.CTRL | SWT.F4);
		actClose.setToolTipText("Close (CTRL + W)");
		actClose.setImageDescriptor(ResourceManager.getImageDescriptor(Editor.class, "/icons/cross.png"));


		actExit = new Action("Exit") {
			public void run() {
				ec.actionExit();
			}
		};	
		actExit.setToolTipText("Exit (SHIFT + W)");

		actUndo = new Action("Undo") {
			public void run() {
				ec.actionUndo(getWorkspace());
			}
		};
		actUndo.setToolTipText("Undo (CTRL + Z)");
		actUndo.setAccelerator(SWT.CTRL | 'Z');
		actUndo.setImageDescriptor(ResourceManager.getImageDescriptor(Editor.class, "/icons/arrow_undo.png"));

		actRedo = new Action("Redo") {
			public void run() {
				ec.actionRedo(getWorkspace());
			}
		};
		actRedo.setToolTipText("Redo (CTRL+Y)");
		actRedo.setAccelerator(SWT.CTRL | 'Y');
		actRedo.setImageDescriptor(ResourceManager.getImageDescriptor(Editor.class, "/icons/arrow_redo.png"));

		actConfigureNodes = new Action("Configure Node(s)") {
			public void run() {
				ec.actionConfigureNode(getWorkspace());				
			}
		};
		actConfigureNodes.setToolTipText("Configure Node (ATL + F)");
		actConfigureNodes.setImageDescriptor(ResourceManager.getImageDescriptor(Editor.class, "/icons/ruby_gear.png"));

		actCreateASingleNode = new Action("A Single Node") {
			public void run() {
				ec.actionCreateASingleNode(getWorkspace());
			}
		};
		actCreateASingleNode.setToolTipText("Create a single Node (SHIFT + N)");
		actCreateASingleNode.setAccelerator(SWT.ALT | SWT.CTRL | SWT.SHIFT | 'N');
		actCreateASingleNode.setImageDescriptor(ResourceManager.getImageDescriptor(Editor.class, "/icons/bullet_blue.png"));		

		actCreateASetOfNodes = new Action("A Set of Nodes") {
			public void run() {
				ec.actionCreateASetOfNode(getWorkspace());
			}
		};
		actCreateASetOfNodes.setToolTipText("Create a set of Nodes (SHIFT + M)");
		actCreateASetOfNodes.setAccelerator(SWT.ALT | SWT.CTRL | SWT.SHIFT | 'M');
		actCreateASetOfNodes.setImageDescriptor(ResourceManager.getImageDescriptor(Editor.class, "/icons/brick_add.png"));

		actManageTrafficFlow = new Action("Traffic Flow Manager") {
			public void run() {
				ec.actionManageTrafficFlow(getWorkspace());
			}
		};
		actManageTrafficFlow.setToolTipText("Traffic Flow Manager (ALT + T)");

		actDeleteNodes = new Action("Delete Node(s)") {
			public void run() {
				ec.actionDeleteNodes(getWorkspace());
			}
		};
		actDeleteNodes.setToolTipText("Delete Nodes(s) (DELETE)");		
		actDeleteNodes.setAccelerator(SWT.DEL);
		actDeleteNodes.setImageDescriptor(ResourceManager.getImageDescriptor(Editor.class, "/icons/delete.png"));
		

		actViewNetworkInfo = new Action("Network Properties") {
			public void run() {
				ec.actionViewNetworkInfo(getWorkspace());
			}
		};
		actViewNetworkInfo.setToolTipText("View Network Infomation (ALT + I)");
		actViewNetworkInfo.setImageDescriptor(ResourceManager.getImageDescriptor(Editor.class, "/icons/world.png"));


		actViewNodeInfo = new Action("Node Properties") {
			public void run() {
				ec.actionViewNodeInfo(getWorkspace());
			}
		};
		actViewNodeInfo.setToolTipText("View Node Infomation (CTRL + I)");
		actViewNodeInfo.setAccelerator(SWT.CTRL | 'I');
		actViewNodeInfo.setImageDescriptor(ResourceManager.getImageDescriptor(Editor.class, "/icons/contrast.png"));


		actShowObstacles = new Action("Show Obstacle(s)") {
			public void run() {
				ec.actionShowObstacles(getWorkspace());
			}
		};		
		actShowObstacles.setToolTipText("Show obstacles (ALT + O)");
		actShowObstacles.setAccelerator(SWT.ALT | SWT.CTRL | 'O');
		actShowObstacles.setImageDescriptor(ResourceManager.getImageDescriptor(Editor.class, "/icons/contrast.png"));

		actShowNeighbors = new Action("Show Neighbors") {
			public void run() {
				ec.actionShowNeighbors(getWorkspace());
			}
		};	
		actShowNeighbors.setToolTipText("Show neighbors of the selected node(s) (SHIFT + S)");
		actShowNeighbors.setAccelerator(SWT.ALT | SWT.CTRL | 'N');
		actShowNeighbors.setImageDescriptor(ResourceManager.getImageDescriptor(Editor.class, "/icons/shape_ungroup.png"));

		actSearchNode = new Action("Search Node") {
			public void run() {
				ec.actionSearchNode(getWorkspace());
			}
		};
		actSearchNode.setToolTipText("Search Node (CTRL + F)");
		actSearchNode.setAccelerator(SWT.CTRL | 'F');
		actSearchNode.setImageDescriptor(ResourceManager.getImageDescriptor(Editor.class, "/icons/find.png"));

		actIdentifyBoundary = new Action("Boundary Identification") {
			public void run() {
				ec.actionIdentifyBoundary();
			}
		};	
		actIdentifyBoundary.setToolTipText("Boundary Idenfication (CTRL + B)");

		actGenerateNodeLocationData = new Action("Node Location Data") {
			public void run() {
				ec.actionGenerateNodeLocationData(getWorkspace());
			}
		};	
		actGenerateNodeLocationData.setAccelerator(SWT.ALT | SWT.CTRL | 'D');
		actGenerateNodeLocationData.setImageDescriptor(ResourceManager.getImageDescriptor(Editor.class, "/icons/script_code.png"));
		actGenerateNodeLocationData.setToolTipText("Generate location node data (CTRL + G)");
		

		actCheckConnectivity = new Action("Check Connectivity") {
			public void run() {
				ec.actionCheckConnectivity(getWorkspace());
			}
		};
		actCheckConnectivity.setToolTipText("Check the connectivity of network (SHIFT + C)");

		actFindConnectivityParts = new Action("Connectivity Parts") {
		};
		actFindConnectivityParts.setToolTipText("Find out connectivity parts in the network");

		actChangeNetworkSize = new Action("Change Network Size") {
			public void run() {
				ec.actionChangeNetworkSize(getWorkspace());
			}
		};
		actChangeNetworkSize.setToolTipText("Change network size (SHIFT + L)");
		actChangeNetworkSize.setAccelerator(SWT.ALT | SWT.CTRL | SWT.SHIFT | 'D');
		actChangeNetworkSize.setImageDescriptor(ResourceManager.getImageDescriptor(Editor.class, "/icons/arrow_inout.png"));

		actGenerateSimulationScripts = new Action("Simulation Scripts") {
			public void run() {
				ec.actionGenerateSimulationScript(getWorkspace());
			}
		};
		actGenerateSimulationScripts.setToolTipText("Generate simulation script (ALT + G)");
		actGenerateSimulationScripts.setAccelerator(SWT.ALT | SWT.CTRL | 'G');
		actGenerateSimulationScripts.setImageDescriptor(ResourceManager.getImageDescriptor(Editor.class, "/icons/script.png"));

		actViewDelaunayTriangulation = new Action("Delaunay Triangulation") {
			public void run() {
				ec.actionViewDelaunayTriangulation(getWorkspace());
			}
			
		};	

		actViewVoronoiDiagram = new Action("Voronoi Diagram") {
			public void run() {
				ec.actionViewVoronoiDiagram(getWorkspace());
			}
		};	

		actViewShortestPathTree = new Action("Shortest Path Tree") {
			
		};	

		actViewShortestPath = new Action("Shortest Path") {
			public void run() {
				Workspace workspace = getWorkspace();
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
				Workspace workspace = getWorkspace();
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
				Workspace workspace = getWorkspace();
				if (workspace != null)
					ApplicationManager.showRNG(workspace);
			}
		};

		actViewGGGraph = new Action("GG") {
			public void run() {
				Workspace workspace = getWorkspace();
				if (workspace != null)
					ApplicationManager.showGG(workspace);
			}
		};	

		actVisualizeSettings = new Action("Preferences") {
			public void run() {
				ec.actionVisualizeSetting(Editor.this);
			}
		};
		actVisualizeSettings.setImageDescriptor(null);
		actVisualizeSettings.setImageDescriptor(ResourceManager.getImageDescriptor(Editor.class, "/icons/cog.png"));
		actVisualizeSettings.setToolTipText("Preferences (SHIFT + P)");

		actDefaultConfiguration = new Action("Node Default Configuration") {
			public void run() {
				ec.actionDefaultConfiguration();
			}
		};	
		actDefaultConfiguration.setImageDescriptor(ResourceManager.getImageDescriptor(Editor.class, "/icons/wrench.png"));
		actDefaultConfiguration.setToolTipText("Configure of node at default mode (SHIFT + K)");

		actDocumentation = new Action("Documentation") {
			public void run() {
				ec.actionDocumentation();
			}
		};	
		actDocumentation.setToolTipText("Documentation (SHIFT + D)");
		actDocumentation.setImageDescriptor(ResourceManager.getImageDescriptor(Editor.class, "/icons/book.png"));

		actDemos = new Action("Demos") {
			public void run() {
				ec.actionDemos();
			}
		};	
		actDemos.setToolTipText("Demos (ALT + D)");
		actDemos.setImageDescriptor(ResourceManager.getImageDescriptor(Editor.class, "/icons/dvd.png"));

		actAbout = new Action("About") {
			public void run() {
				ec.actionAbout();					
			}
		};		
		actAbout.setToolTipText("About (ALT + A)");
		actAbout.setImageDescriptor(ResourceManager.getImageDescriptor(Editor.class, "/icons/information.png"));

		actPrint = new Action("Print") {
			public void run() {
				ec.actionPrint(getWorkspace());
			}
		};
		actPrint.setToolTipText("Print (CTRL + P)");
		actPrint.setAccelerator(SWT.CTRL | 'P');
		actPrint.setImageDescriptor(ResourceManager.getImageDescriptor(Editor.class, "/icons/printer.png"));


		actSaveAll = new Action("Save All") {
			public void run() {
				try {
					Project project = ProjectManager.getProject();
						ProjectManager.saveProject(project);
				} catch (IOException e) {
					MessageDialog.openError(getShell(), "Saving Error", "Something wrong happened. Cannot save all projects");
				}
			}
		};
		actSaveAll.setToolTipText("Save all opened project");
		actSaveAll.setImageDescriptor(ResourceManager.getImageDescriptor(Editor.class, "/icons/disk_multiple.png"));


		actZoomIn = new Action("Zoom In") {
			public void run() {
				ec.actionZoomIn(getWorkspace());
//				actionZoomIn();
			}
		};
		actZoomIn.setToolTipText("Zoom In (CTRL+)");
		actZoomIn.setImageDescriptor(ResourceManager.getImageDescriptor(Editor.class, "/icons/zoom_in.png"));
		actZoomIn.setAccelerator(SWT.CTRL | '=');


		actZoomOut = new Action("Zoom Out") {
			public void run() {
				ec.actionZoomOut(getWorkspace());
			}
		};
		actZoomOut.setToolTipText("Zoom Out (CTRL-)");
		actZoomOut.setImageDescriptor(ResourceManager.getImageDescriptor(Editor.class, "/icons/zoom_out.png"));
		actZoomOut.setAccelerator(SWT.CTRL | '-');


		actCreateARandomNode = new Action("A Random Node") {
			public void run() {
				ec.actionCreateARandomNode(getWorkspace());
			}
		};
		actCreateARandomNode.setToolTipText("Create a random node (CTRL + R)");
		actCreateARandomNode.setAccelerator(SWT.CTRL | 'R');
		actCreateARandomNode.setImageDescriptor(ResourceManager.getImageDescriptor(Editor.class, "/icons/bullet_feed.png"));


		actDeleteAllNodes = new Action("Delete All Nodes") {
			public void run() {
				ec.actionDeleteAllNodes(getWorkspace());
			}
		};
		actDeleteAllNodes.setToolTipText("Delete all nodes (SHIFT+DELETE)");
		actDeleteAllNodes.setImageDescriptor(ResourceManager.getImageDescriptor(Editor.class, "/icons/basket_delete.png"));
		
		actShowRange = new Action("Show Range") {
			public void run() {
				ec.actionShowRange(getWorkspace());
			}
		};
		actShowRange.setToolTipText("Show radio range of the selected node(s) (ALT + R)");
		actShowRange.setAccelerator(SWT.ALT | SWT.CTRL | 'R');
		actShowRange.setImageDescriptor(ResourceManager.getImageDescriptor(Editor.class, "/icons/eye.png"));

		actShowConnection = new Action("Show Connection") {
			public void run() {
				ec.actionShowConnection(actShowConnection,getWorkspace());
			}
		};
		actShowConnection.setToolTipText("Show network connection (ALT + C)");
		actShowConnection.setChecked(false);
		actShowConnection.setAccelerator(SWT.ALT | SWT.CTRL | 'C');

		actMouseCursor = new Action("Mouse Cursor") {
			public void run() {
				Workspace workspace = getWorkspace();
				if (workspace != null) {
					
					workspace.getPropertyManager().setMouseMode(WorkspacePropertyManager.CURSOR);
					workspace.enableNetwork();
				}				
			}
		};
		actMouseCursor.setToolTipText("Mouse Cursor");
		actMouseCursor.setChecked(true);
		actMouseCursor.setImageDescriptor(ResourceManager.getImageDescriptor(Editor.class, "/icons/arrow_cursor.png"));

		actMouseCreateNode = new Action("Create New Node by Mouse Click") {
			public void run() {
				ec.actionMouseCreateNode(getWorkspace());
			}
		};
		actMouseCreateNode.setToolTipText("Create new nodes by mouse click (CTRL + M)");
		actMouseCreateNode.setImageDescriptor(ResourceManager.getImageDescriptor(Editor.class, "/icons/Point-Black.png"));
		actMouseCreateNode.setChecked(false);

		actShowRulers = new Action("Show Rulers") {
			public void run() {
				ec.actionShowRulers(actShowRulers,getWorkspace());
			}
		};
		actShowRulers.setToolTipText("Show Rulers (SHIFT + L)");
		actShowRulers.setChecked(true);

		actMouseCreateArea = new Action("Create New Area") {
			public void run() {
				ec.actionMouseCreateArea(getWorkspace());	
			}
		};
		actMouseCreateArea.setToolTipText("Create new area (CTRL + A)");
		actMouseCreateArea.setChecked(false);
		actMouseCreateArea.setImageDescriptor(ResourceManager.getImageDescriptor(Editor.class, "/icons/arrow_cursor.png"));

		actManagePaths = new Action("Path Manager") {
			public void run() {
				ec.actionManagePath(getWorkspace());
			}
		};
		actManagePaths.setToolTipText("Path Manager (ALT + M)");

		actManageLabels = new Action("Label Manager") {
			public void run() {
				ec.actionManageLabels(getWorkspace());
			}
		};
		actManageLabels.setToolTipText("Label Manager (ALT + L)");
		

		{
			actImport = new Action("Import Location Data") {
				public void run() {
					ec.actionImport(getWorkspace());
				}
			};
			actImport.setToolTipText("Import Location Data (ALT + I)");
			
			
		}
		
		actNetworkReferenceRemain = new Action() {
			public void run() {
				final Menu menu = new Menu(getShell(), SWT.POP_UP);
				
			      MenuItem managerTrafficFlowItem = new MenuItem(menu, SWT.PUSH);
			      managerTrafficFlowItem.setText("Manager Traffic Flow ");
			      managerTrafficFlowItem.addSelectionListener(new SelectionListener() {
					
					@Override
					public void widgetSelected(SelectionEvent e) {
						// TODO Auto-generated method stub
//						actionManageTrafficFlow();
						ec.actionManageTrafficFlow(getWorkspace());
					}
					
					@Override
					public void widgetDefaultSelected(SelectionEvent e) {
						// TODO Auto-generated method stub
						ec.actionManageTrafficFlow(getWorkspace());
					}
				});
			      
			      MenuItem managerPathsItem = new MenuItem(menu, SWT.PUSH);
			      managerPathsItem.setText("Manager Paths ");
			      managerPathsItem.addSelectionListener(new SelectionListener() {
					
					@Override
					public void widgetSelected(SelectionEvent e) {
						// TODO Auto-generated method stub
						ec.actionManagePath(getWorkspace());
					}
					
					@Override
					public void widgetDefaultSelected(SelectionEvent e) {
						// TODO Auto-generated method stub
						ec.actionManagePath(getWorkspace());
					}
				});
			      
			      MenuItem managerLabelsItem = new MenuItem(menu, SWT.PUSH);
			      managerLabelsItem.setText("Manager Labels");
			      managerLabelsItem.addSelectionListener(new SelectionListener() {
					
					@Override
					public void widgetSelected(SelectionEvent e) {
						// TODO Auto-generated method stub
						ec.actionManageLabels(getWorkspace());
					}
					
					@Override
					public void widgetDefaultSelected(SelectionEvent e) {
						// TODO Auto-generated method stub
						ec.actionManageLabels(getWorkspace());
					}
				});
			    
			      MenuItem showRulersItem = new MenuItem(menu, SWT.PUSH);
			      showRulersItem.setText("Show Rulers");
			      showRulersItem.addSelectionListener(new SelectionListener() {
					
					@Override
					public void widgetSelected(SelectionEvent e) {
						// TODO Auto-generated method stub
						ec.actionShowRulers(actShowRulers, getWorkspace());
					}
					
					@Override
					public void widgetDefaultSelected(SelectionEvent e) {
						// TODO Auto-generated method stub
						ec.actionShowRulers(actShowRulers, getWorkspace());
					}
				});
			      
			    Rectangle rect = new Rectangle(10, 10, 20, 20);
			    Point pt = new Point(rect.x, rect.y + rect.height);
			    
			    
			    pt = toolBar.toDisplay(pt);
			    menu.setLocation(pt.x+412, pt.y);
		        menu.setVisible(true);
			}
		};
		actNetworkReferenceRemain.setToolTipText("External tools");
		actNetworkReferenceRemain.setImageDescriptor(ResourceManager.getImageDescriptor(Editor.class, "/icons/ruby.png"));
		
		actScriptReferenceRemain = new Action() {
			public void run() {
				final Menu menu = new Menu(getShell(), SWT.POP_UP);
			      MenuItem importItem = new MenuItem(menu, SWT.PUSH);
			      importItem.setText("Import");
			      importItem.addSelectionListener(new SelectionListener() {
					
					@Override
					public void widgetSelected(SelectionEvent e) {
						// TODO Auto-generated method stub
						ec.actionImport(getWorkspace());
					}
					
					@Override
					public void widgetDefaultSelected(SelectionEvent e) {
						// TODO Auto-generated method stub
						ec.actionImport(getWorkspace());
						
					}
				});
			      
			      MenuItem toImageItem = new MenuItem(menu, SWT.PUSH);
			      toImageItem.setText("Images Export");
			      toImageItem.addSelectionListener(new SelectionListener() {
					
					@Override
					public void widgetSelected(SelectionEvent e) {
						// TODO Auto-generated method stub
						
						ec.actionToImage(getWorkspace());
					}
					
					@Override
					public void widgetDefaultSelected(SelectionEvent e) {
						// TODO Auto-generated method stub
						ec.actionToImage(getWorkspace());
					}
				});
			      
			      MenuItem documentItem = new MenuItem(menu, SWT.PUSH);
			      documentItem.setText("Document Export");
			      documentItem.addSelectionListener(new SelectionListener() {
					
					@Override
					public void widgetSelected(SelectionEvent e) {
						// TODO Auto-generated method stub
						ec.actionDocumentation();
						
					}
					
					@Override
					public void widgetDefaultSelected(SelectionEvent e) {
						// TODO Auto-generated method stub
						ec.actionDocumentation();
					}
				});
			      
			      MenuItem demosItem = new MenuItem(menu, SWT.PUSH);
			      demosItem.setText("Demos");
			      demosItem.addSelectionListener(new SelectionListener() {
					
					@Override
					public void widgetSelected(SelectionEvent e) {
						// TODO Auto-generated method stub
						ec.actionDemos();
					}
					
					@Override
					public void widgetDefaultSelected(SelectionEvent e) {
						// TODO Auto-generated method stub
						ec.actionDemos();
					}
				});
			      
			      MenuItem printItem = new MenuItem(menu, SWT.PUSH);
			      printItem.setText("Print");
			      printItem.addSelectionListener(new SelectionListener() {
					
					@Override
					public void widgetSelected(SelectionEvent e) {
						// TODO Auto-generated method stub
						ec.actionPrint(getWorkspace());
					}
					
					@Override
					public void widgetDefaultSelected(SelectionEvent e) {
						// TODO Auto-generated method stub
						ec.actionPrint(getWorkspace());
					}
				});
			      
			      MenuItem aboutItem = new MenuItem(menu, SWT.PUSH);
			      aboutItem.setText("About");
			      aboutItem.addSelectionListener(new SelectionListener() {
					
					@Override
					public void widgetSelected(SelectionEvent e) {
						// TODO Auto-generated method stub
						ec.actionAbout();
						
					}
					
					@Override
					public void widgetDefaultSelected(SelectionEvent e) {
						// TODO Auto-generated method stub
						ec.actionAbout();
					}
				});
			      
			    
			    Rectangle rect = new Rectangle(10, 10, 20, 20);
			    Point pt = new Point(rect.x, rect.y + rect.height);
			    
			    
			    pt = toolBar.toDisplay(pt);
			    menu.setLocation(pt.x+672, pt.y);
		        menu.setVisible(true);
			}
		};
		actScriptReferenceRemain.setToolTipText("External tools");
		actScriptReferenceRemain.setImageDescriptor(ResourceManager.getImageDescriptor(Editor.class, "/icons/ruby.png"));
		
	}
	
	private void createToolBar()
	{
		toolBarManager.add(actNew);
		toolBarManager.add(actOpen);
		toolBarManager.add(actSave);
		
//		toolBarManager.add(actSaveAll);
		Separator separator = new Separator();
		toolBarManager.add(separator);
		toolBarManager.add(actMouseHand);
		toolBarManager.add(actMouseCursor);
		Separator separator2 = new Separator();
		toolBarManager.add(separator2);
		
		toolBarManager.add(actMouseCreateNode);
		toolBarManager.add(actMouseCreateArea);
		
		toolBarManager.add(actCreateARandomNode);
		toolBarManager.add(actCreateASingleNode);
		toolBarManager.add(actCreateASetOfNodes);
		toolBarManager.add(actDefaultConfiguration);
		toolBarManager.add(actConfigureNodes);
		toolBarManager.add(actDeleteNodes);
		toolBarManager.add(actDeleteAllNodes);
		toolBarManager.add(actShowNeighbors);
		toolBarManager.add(actShowRange);
		toolBarManager.add(actViewNodeInfo);
		toolBarManager.add(actNetworkReferenceRemain);
		
		Separator separator3 = new Separator();
		toolBarManager.add(separator3);
		toolBarManager.add(actChangeNetworkSize);
		toolBarManager.add(actVisualizeSettings);
		toolBarManager.add(actViewNetworkInfo);
		
		Separator separator1 = new Separator();
		toolBarManager.add(separator1);
		toolBarManager.add(actUndo);
		toolBarManager.add(actRedo);
		toolBarManager.add(actZoomIn);
		toolBarManager.add(actZoomOut);
		toolBarManager.add(actSearchNode);
		Separator separator4 = new Separator();
		toolBarManager.add(separator4);
		toolBarManager.add(actGenerateNodeLocationData);
		toolBarManager.add(actGenerateSimulationScripts);
		toolBarManager.add(actScriptReferenceRemain);
		
		Separator separator5 = new Separator();
		toolBarManager.add(separator5);
//		
//		
//		
//		
		toolBarManager.update(true);
	}
	
	public void actionEvent(){
		getShell().getDisplay().addFilter(SWT.KeyDown, new Listener() {
		    public void handleEvent(Event e) {
		    	if(tabFolder.getSelectionIndex() == 1){
		    		
		    		if(((e.stateMask & SWT.CTRL) == SWT.CTRL) && (e.keyCode == 'F' || e.keyCode == 'f')){
		    			
		    			ec.actionSearchNode(getWorkspace());
		    		}
			    	
			    	if(((e.stateMask & SWT.SHIFT) == SWT.SHIFT) && (e.keyCode == '=')){
			    		ec.actionZoomIn(getWorkspace());
			    	}
			    	
			    	if(((e.stateMask & SWT.CTRL) == SWT.CTRL) && (e.keyCode == '-')){
			    		ec.actionZoomOut(getWorkspace());
			    	}
			    	if(((e.stateMask & SWT.CTRL) == SWT.CTRL) && (e.keyCode == 'R' || e.keyCode == 'r')){
			    		ec.actionCreateARandomNode(getWorkspace());
			        }
			    	
			    	if(((e.stateMask & SWT.CTRL) == SWT.CTRL) && (e.keyCode == 'N' || e.keyCode == 'n')){
			    		ec.actionNew(Editor.this);
			    	}
			    	
			    	if(((e.stateMask & SWT.CTRL) == SWT.CTRL) && (e.keyCode == 'O' || e.keyCode == 'o')){
			    		ec.actionOpen(Editor.this);
			    	}
			    	
			    	if(((e.stateMask & SWT.CTRL) == SWT.CTRL) && (e.keyCode == 'S' || e.keyCode == 's')){
			    		ec.actionSave(Editor.this);
			    	}
			    	
			    	if(((e.stateMask & SWT.CTRL) == SWT.CTRL) && (e.keyCode == 'Z' || e.keyCode == 'z')){
			    		ec.actionUndo(getWorkspace());
			    	}
			    	
			    	if(((e.stateMask & SWT.CTRL) == SWT.CTRL) && (e.keyCode == 'Y' || e.keyCode == 'y')){
			    		ec.actionRedo(getWorkspace());
			    	}
			    	
			    	if(((e.stateMask & SWT.CTRL) == SWT.CTRL) && (e.keyCode == 'I' || e.keyCode == 'i')){
			    		ec.actionViewNodeInfo(getWorkspace());				
			    	}
			    	
//			    	if((e.stateMask & SWT.DEL) == SWT.DEL){
			    	if(e.character == SWT.DEL){
			    		ec.actionDeleteNodes(getWorkspace());
			    	}
		    	}	
		    }
		});
	}
	
	public void showProject(Project project)
	{
		final RulerScrolledComposite scrolledComposite = new RulerScrolledComposite(tabFolder, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
//		scrolledComposite = new RulerScrolledComposite(tabFolder, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		tabFolder.getItem(1).setControl(scrolledComposite);
		scrolledComposite.setExpandHorizontal(false);
		scrolledComposite.setExpandVertical(false);
		final Workspace workspaceInner = new Workspace(scrolledComposite, SWT.NONE, project);
		workspaceInner.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_DARK_SHADOW));
		workspaceInner.setSize(scrolledComposite.getClientArea().width, scrolledComposite.getClientArea().height);
		scrolledComposite.setContent(workspaceInner);

		workspaceInner.setFocus();	
		scrolledComposite.initializeRulers();
		
		// add MainWindow as an observer to track changes of workspace property
		WorkspacePropertyManager propManager = workspaceInner.getPropertyManager();
		propManager.addObserver(this);
		
		workspaceInner.getCareTaker().addObserver(this);
		
		statesHandler.activeProject();
	}
	
	public Workspace getWorkspace() {
		Workspace workspace = (Workspace)((ScrolledComposite)tabFolder.getItem(1).getControl()).getContent();
		if(workspace != null)
			return workspace;
		return null;
	}
	
	public StyledText getStyledText() {
		return styledText;
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
	
	public Action getActNetworkReferenceRemain() {
		return actNetworkReferenceRemain;
	}
	
	public Action getActScriptReferenceRemain() {
		return actScriptReferenceRemain;
	}
	
	public Project getProject() {
		return project;
	}
	
	public void setProject(Project projectSet) {
		this.project = projectSet;
	}
	
	public CTabFolder getCTabFolder(){
		return tabFolder;
	}
	private CTabFolder tabFolder;
	private StatesHandler statesHandler;
//	private RulerScrolledComposite scrolledComposite;
	
	private SashForm sashForm;
	private Composite contentComposite;
	private Composite propertiesComposite;
	private ToolBarManager toolBarManager;
	
	private MenuManager menuManager_File;
	private MenuManager menuManager_Edit;
	private MenuManager menuManager_View;
	private MenuManager menuManager_Feature;
	private MenuManager menuManager_Manage;
	private MenuManager menuManager_Generate;
	private MenuManager menuMenager_Setting;
	private MenuManager menuManager_Help;
	
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
	private Action actManagePaths;
	private Action actManageLabels;
	private Action actImport;
	
	private Action actScriptReferenceRemain;
	private Action actNetworkReferenceRemain;
	
	private StyledText styledText;
	private EditorActionController ec;
	
	private Project project;
	
	private RulerScrolledComposite scrolledComposite;
	
	private ToolBar toolBar;
	
	@Override
	public void update(Observable arg0, Object arg1) {
		// TODO Auto-generated method stub
		Workspace workspace = getWorkspace();
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
		}
		ec.updateDesign(this, styledText);
		actionGetTab();
//		ec.updateEditToDesign(this,styledText);
			
	}
}
