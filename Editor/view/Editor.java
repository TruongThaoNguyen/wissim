package view;

import java.awt.Container;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import model.Project;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.wb.swt.ResourceManager;

import control.manager.ApplicationManager;
import control.manager.ApplicationSettings;

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
	}
	
	/**
	 * createContent
	 */
	private void createContent() {
		setLayout(new GridLayout(1, false));

		// ------------- toolbar ------------- //
		
		ToolBar toolBar = new ToolBar(this, SWT.FLAT | SWT.RIGHT);
		toolBar.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		
		toolBarManager = new ToolBarManager(toolBar);
		
		// ------------- main composite ------------- //
		
		SashForm sashForm = new SashForm(this, SWT.NONE);
		sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		Composite contentComposite = new Composite(sashForm, SWT.NONE);
		contentComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		contentComposite.setLayout(new FillLayout());
		
				CTabFolder tabFolder = new CTabFolder(contentComposite, SWT.BORDER | SWT.FLAT | SWT.BOTTOM);
				tabFolder.setSelectionBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
				
				CTabItem tbtmEdit = new CTabItem(tabFolder, SWT.PUSH);
				tbtmEdit.setText("Edit");
				
				Composite composite = new Composite(tabFolder, SWT.BORDER);
				tbtmEdit.setControl(composite);
				composite.setLayout(new FillLayout(SWT.HORIZONTAL));
				
				text = new Text(composite, SWT.WRAP | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
				
				CTabItem tbtmDesign = new CTabItem(tabFolder, SWT.PUSH);
				tbtmDesign.setText("Design");

		
		// ------------- properties composite ------------- //
		Composite propertiesComposite = new Composite(sashForm,  SWT.BORDER);
		propertiesComposite.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, true));
		sashForm.setWeights(new int[] {305, 132});
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
//		menuManager_File.add(actOpen);
//		menuManager_File.add(actSave);
//		menuManager_File.add(actSaveAs);
//		menuManager_File.add(actSaveAll);
//		menuManager_File.add(new Separator());
//		menuManager_File.add(actImport);
//		{
//			MenuManager menuManager_2 = new MenuManager("Export");
//			menuManager_File.add(menuManager_2);
//			menuManager_2.add(actToImage);
//			//menuManager_2.add(actToPDF);			
//		}
//		menuManager_File.add(actPrint);
//		menuManager_File.add(new Separator());
//		menuManager_File.add(actClose);
//		menuManager_File.add(actExit);
	}
	/*
	{	// ------------- Edit ------------- //
		menuManager_Edit = new MenuManager("&Edit");		
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
	{	// ------------- View ------------- //
		menuManager_View = new MenuManager("&View");		
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
	{	// ------------- Feature ------------- //
		menuManager_Feature = new MenuManager("Features");
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
	{	// ------------- Manager ------------- //
		menuManager_Manage = new MenuManager("Manager");
		menuManager_Manage.add(actManageLabels);
		menuManager_Manage.add(actManagePaths);
		menuManager_Manage.add(actManageTrafficFlow);
	}
	{	// ------------- Generate ------------- //
		menuManager_Generate = new MenuManager("&Generate");
		menuManager_Generate.add(actGenerateSimulationScripts);
		menuManager_Generate.add(actGenerateNodeLocationData);
	}
	{	// ------------- Setting ------------- //
		menuMenager_Setting = new MenuManager("&Settings");
		menuMenager_Setting.add(actDefaultConfiguration);
		menuMenager_Setting.add(actVisualizeSettings);
	}
	*/
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
		actNew = new Action("New") {
			public void run() {
				Project project = ApplicationManager.newProject(getParent().getShell()); 
				if (project != null)
					showProject(project);
			}
		};
		actNew.setToolTipText("Create a new project");
		actNew.setAccelerator(SWT.CTRL | 'N');
		actNew.setImageDescriptor(ResourceManager.getImageDescriptor(MainWindow.class, "/icons/application_add.png"));
		
		actAbout = new Action("About") {
			public void run() {
				new AboutWindow(null).open();					
			}
		};		
	}
	
	private void createToolBar()
	{
		toolBarManager.add(actAbout);
		
		
//		toolBarManager.add(actNew);
//		toolBarManager.add(actOpen);
//		toolBarManager.add(actSave);
//		
////		toolBarManager.add(actSaveAll);
//		Separator separator = new Separator();
//		toolBarManager.add(separator);
//		toolBarManager.add(actMouseHand);
//		toolBarManager.add(actMouseCursor);
//		Separator separator2 = new Separator();
//		toolBarManager.add(separator2);
//		
//		toolBarManager.add(actMouseCreateNode);
//		toolBarManager.add(actMouseCreateArea);
//		
//		toolBarManager.add(actCreateARandomNode);
//		toolBarManager.add(actCreateASingleNode);
//		toolBarManager.add(actCreateASetOfNodes);
//		toolBarManager.add(actDefaultConfiguration);
//		toolBarManager.add(actConfigureNodes);
//		toolBarManager.add(actDeleteNodes);
//		toolBarManager.add(actDeleteAllNodes);
//		toolBarManager.add(actShowNeighbors);
//		toolBarManager.add(actShowRange);
//		toolBarManager.add(actViewNodeInfo);
//		
//		
//		Separator separator3 = new Separator();
//		toolBarManager.add(separator3);
//		toolBarManager.add(actChangeNetworkSize);
//		toolBarManager.add(actVisualizeSettings);
//		toolBarManager.add(actViewNetworkInfo);
//		
//		Separator separator1 = new Separator();
//		toolBarManager.add(separator1);
//		toolBarManager.add(actUndo);
//		toolBarManager.add(actRedo);
//		toolBarManager.add(actZoomIn);
//		toolBarManager.add(actZoomOut);
//		toolBarManager.add(actSearchNode);
//		Separator separator4 = new Separator();
//		toolBarManager.add(separator4);
//		toolBarManager.add(actGenerateNodeLocationData);
//		toolBarManager.add(actGenerateSimulationScripts);
//		
//		Separator separator5 = new Separator();
//		toolBarManager.add(separator5);
//		toolBarManager.add(actRunNS2);
//		toolBarManager.add(actConfigNS2);
//		
//		
//		
//		
		toolBarManager.update(true);
	}
	
	private void showProject(Project project)
	{
		// TODO
	}
	
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
	private Text text;
	@Override
	public void update(Observable arg0, Object arg1) {
		// TODO Auto-generated method stub
		
	}
}
