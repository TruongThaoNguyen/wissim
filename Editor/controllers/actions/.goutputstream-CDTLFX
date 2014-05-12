package controllers.actions;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import models.Project;
import models.converter.ParseException;
import models.managers.ScriptGenerator;
import models.networkcomponents.WirelessNetwork;
import models.networkcomponents.WirelessNode;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import views.Editor;
import views.RulerScrolledComposite;
import views.Workspace;
import views.dialogs.AboutWindow;
import views.dialogs.ConfigNodeDialog;
import views.dialogs.GenerateNodeLocationDataDialog;
import views.dialogs.GenerateSimulationScriptsDialog;
import views.dialogs.PreferencesDialog;
import controllers.converter.Converter;
import controllers.graphicscomponents.GSelectableObject;
import controllers.graphicscomponents.GWirelessNode;
import controllers.managers.ApplicationManager;
import controllers.managers.WorkspacePropertyManager;

public class EditorActionController {
	private Shell shellRoot;
	public EditorActionController(Shell shellRoot) {
		this.shellRoot = shellRoot;
	}
	public void ns2Config(String nsFilePath) {
		String preNS2Path = "";
		if(nsFilePath!=null){
			preNS2Path = nsFilePath;
		}
		String filePathString = "NS2_path_store";
			File file = new File(filePathString);
			
			FileDialog dialog = new FileDialog(shellRoot, SWT.MULTI);
	    dialog.open();
	    if(dialog.getFilterPath() == null || dialog.getFileName() == null) nsFilePath = preNS2Path;
	    else nsFilePath = dialog.getFilterPath() + "/" +dialog.getFileName();
	    
	    try {
	    if (!file.exists()) {
			
				file.createNewFile();
		}
	    FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(nsFilePath);
		bw.close();
	    } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void actionOpen(Editor editor){

		Project project = ApplicationManager.openProject(editor);
		if(project == null) return;
//		if (project != null)
			editor.showProject(project);
		editor.setProject(project);
		if(editor.getWorkspace() != null)
			if(editor.getWorkspace().getSelectableObject() != null)
				editor.getWorkspace().getSelectableObject().get(editor.getWorkspace().getSelectableObject().size() - 1).moveAbove(null);
		
		updateDesign(editor,editor.getStyledText());
	}
	
	public GWirelessNode actionCopy(Workspace workspace) {
		return ApplicationManager.copyNode(workspace);
	}
	
	public void actionPaste(GWirelessNode gn,Workspace workspace){
		
		WirelessNode wn = gn.getWirelessNode();
		ApplicationManager.pasteNode(workspace,wn.getX()+1 , wn.getY()+1, wn.getRange());
	}
	
	public void saveScript(String fileProject,Workspace workspace) {
		fileProject = fileProject.replace(".wis", ".tcl");
		System.out.println(fileProject);
		try {
			ScriptGenerator.generateTcl(workspace.getProject(), fileProject, false, false);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void updateDesign(Editor editor,StyledText styledText) {
		Workspace workspace = editor.getWorkspace();
		try {
			StringBuilder sb = ScriptGenerator.generateTclText(workspace.getProject(), false, false);
			styledText.setText(sb.toString());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void updateEditToDesign(Editor editor,StyledText text){
//		Converter cvrt;
		try {
//			cvrt = new Converter();
			Project pj = Converter.CTD(text.getText());
			System.out.println(pj.getSelectedAntenna());
//			editor.getWorkspace().setProject(cvrt.CTD(text.getText()));
//			editor.setProject(cvrt.CTD(text.getText()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public void updateNodeInfoLabel(Label eventLabel,Workspace workspace) {
		String str = "Node Info" + "\n";
		if (workspace == null) str += "";
		else{
			
			List<GSelectableObject> objList = workspace.getSelectedObject();
			if (objList.size() == 1 && objList.get(0) instanceof GWirelessNode) {
				WirelessNode wn = ((GWirelessNode) objList.get(0)).getWirelessNode();
				
				str += "+ID : " + wn.getId() + "+\n+Location : \n("
						+ wn.getX() + " , " + wn.getY() + ")+\n"
						+ "+Range : " + wn.getRange() + "+\n+Neighbors \nNumber:"
						+ wn.getNeighborList().size() + "+"
						;
				
			}
			else str += "";
		}
		eventLabel.setText(str);
	}
	
	public void updateNetworkInfoLabel(Label networkLabel,Workspace workspace) {
		WirelessNetwork network = workspace.getProject().getNetwork();
		String str = "Networks" + "\n" + "Size : " + network.getWidth() 
					+ "x" + network.getLength() + "\nNodes : " + network.getNodeList().size()
					+ "\n" + "Times : " + network.getTime();
		networkLabel.setText(str);
		
	}

	public void actionNew(Editor editor) {
		Project project = ApplicationManager.newProject(editor); 
		if (project == null) return;
		
			editor.showProject(project);
	}
	
	public void actionSave(Editor editor) {
		ApplicationManager.saveWorkspace(editor);
	}
	
	public void actionMouseHand(Editor editor,Workspace workspace) {
		if (workspace != null) {
			workspace.getPropertyManager().setMouseMode(WorkspacePropertyManager.HAND);
			workspace.disableNetwork();
		}
		else return;
	}
	
	public void actionToImage(Workspace workspace) {
		ApplicationManager.exportToImage(workspace);
	}
	
	public void actionClose(Workspace workspace) {
		ApplicationManager.closeWorkspace(workspace);
	}
	
	public void actionExit() {
		shellRoot.close();
	}
	
	public void actionUndo(Workspace workspace) {
		ApplicationManager.undoState(workspace);
	}
	
	public void actionRedo(Workspace workspace) {
		ApplicationManager.redoState(workspace);
	}
	
	public void actionConfigureNode(Workspace workspace) {
		
		if (workspace != null)
			new ConfigNodeDialog(shellRoot, SWT.SHEET, ConfigNodeDialog.PROJECT_CONFIG, workspace).open();
	}
	
	public void actionCreateASingleNode(Workspace workspace) {
		ApplicationManager.createASingleNode(workspace);
	}
	
	public void actionCreateASetOfNode(Workspace workspace) {
		ApplicationManager.createASetOfNodes(workspace);
	}
	
	public void actionManageTrafficFlow(Workspace workspace) {
		ApplicationManager.manageTrafficFlow(workspace);
	}
	
	public void actionDeleteNodes(Workspace workspace) {
		ApplicationManager.deleteNodes(workspace);
	}
	
	public void actionViewNetworkInfo(Workspace workspace) {
		ApplicationManager.viewNetworkInfo(workspace);
	}
	
	public void actionViewNodeInfo(Workspace workspace) {
		if (workspace == null) return;
		
		List<GSelectableObject> objList = workspace.getSelectedObject();
		if (objList.size() == 1 && objList.get(0) instanceof GWirelessNode)
			ApplicationManager.viewNodeInfo(workspace, (GWirelessNode) objList.get(0));
	}
	
	public void actionShowObstacles(Workspace workspace) {
		ApplicationManager.showObstacles(workspace);
	}
	
	public void actionShowNeighbors(Workspace workspace) {
		ApplicationManager.showNeighbors(workspace);
	}
	
	public void actionSearchNode(Workspace workspace) {
		ApplicationManager.searchNodes(workspace);
	}
	
	public void actionIdentifyBoundary() {
		MessageDialog.openInformation(shellRoot, "Unavailable feature", "This feature is currently unavailable. Please wait for the next version");
	}
	
	public void actionGenerateNodeLocationData(Workspace workspace) {

		if (workspace != null)
			new GenerateNodeLocationDataDialog(shellRoot, SWT.SHEET, workspace.getProject()).open();
	}
	
	public void actionCheckConnectivity(Workspace workspace) {
		if (workspace == null) return;
		ApplicationManager.checkConnectivity(workspace);
	}
	
	public void actionChangeNetworkSize(Workspace workspace) {
		if (workspace == null) return;
		ApplicationManager.changeNetworkSize(workspace);
	}
	
	public void actionGenerateSimulationScript(Workspace workspace) {
		if (workspace == null) return;
		new GenerateSimulationScriptsDialog(shellRoot, SWT.SHEET, workspace).open();
	}
	
	public void actionViewDelaunayTriangulation(Workspace workspace) {
		if (workspace != null) {
			ApplicationManager.showDelaunayTriangulation(workspace);
			
		}
	}
	
	public void actionViewVoronoiDiagram(Workspace workspace) {
		if (workspace != null)
			ApplicationManager.showVoronoiDiagram(workspace);
	}
	
	public void actionVisualizeSetting(Editor editor) {
		new PreferencesDialog(shellRoot, SWT.SHEET, editor).open();
	}
	
	public void actionDefaultConfiguration() {
		new ConfigNodeDialog(shellRoot, SWT.SHEET, ConfigNodeDialog.APP_CONFIG, null).open();
	}
	
	public void actionDocumentation() { 
		Desktop dt = Desktop.getDesktop();
		File docFile = null;
		try {
			docFile = new File(Editor.class.getResource("help.chm").getPath());
			dt.open(docFile);
		} catch (Exception e) {
			docFile = new File("doc/help.chm");
			
			try {
			dt.open(docFile);
			} catch (IOException ex) {
				MessageDialog.openError(shellRoot, "Cannot open documentation", "The documentation may not exist or has been deleted");
			}
		}
	}
	
	public void actionDemos() {
		MessageDialog.openInformation(shellRoot, "Ten Ten", "Will be available in the next version.");
	}
	
	public void actionAbout(){
		new AboutWindow(null).open();	
	}
	
	public void actionPrint(Workspace workspace) {
		if (workspace == null) return;
		ApplicationManager.print(workspace);
	}
	
	public void actionZoomIn(Workspace workspace) {
		if (workspace == null) return;
		workspace.setSize(workspace.getSize().x + 10, workspace.getSize().y + 10);
	}
	
	public void actionZoomOut(Workspace workspace) {
		if (workspace == null) return;
		workspace.setSize(workspace.getSize().x - 10, workspace.getSize().y - 10);
	}
	
	public void actionCreateARandomNode(Workspace workspace) {
		if (workspace == null) return;
		ApplicationManager.createARandomNode(workspace);
	}
	
	public void actionDeleteAllNodes(Workspace workspace) {
		if (workspace == null) return;
		ApplicationManager.deleteAllNodes(workspace);
	}
	
	public void actionShowRange(Workspace workspace) {
		if (workspace == null) return;
		ApplicationManager.showRange(workspace);
	}
	
	public void actionShowConnection(Action actShowConnection,Workspace workspace) {
		if (workspace == null) return;
		if (workspace.getPropertyManager().isShowConnection()) {
			ApplicationManager.showConnection(workspace, false);
			actShowConnection.setChecked(false);
		} else {
			ApplicationManager.showConnection(workspace, true);
			actShowConnection.setChecked(true);					
		}
	}
	
	public void actionMouseCreateNode(Workspace workspace) {

		if (workspace != null) {
			workspace.getPropertyManager().setMouseMode(WorkspacePropertyManager.NODE_GEN);
			workspace.enableNetwork();
		}
	}
	
	public void actionShowRulers(Action actShowRulers,Workspace workspace) {

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
	
	public void actionMouseCreateArea(Workspace workspace) {

		if (workspace != null) {
			workspace.getPropertyManager().setMouseMode(WorkspacePropertyManager.AREA);
			workspace.enableNetwork();
			
			workspace.disableNodes();
			workspace.getPropertyManager().turnVisualizeOff();
			workspace.deselectGraphicObjects();
		}
	}
	
	public void actionManagePath(Workspace workspace) {
		if (workspace == null) return;
		ApplicationManager.managePaths(workspace);
	}
	
	public void actionManageLabels(Workspace workspace) {
		if (workspace == null) return;
		ApplicationManager.manageLabels(workspace);
	}
	
	public void actionRunNS2() {
		
	}
	
	public void actionImport(Workspace workspace) {
		if (workspace == null) return;
		ApplicationManager.importLocationData(workspace);
	}

	
}
