package views;


public class StatesHandler {
	Editor window;
	
	public StatesHandler(Editor window) {
		this.window = window;
	}
	
	public void initialize() {
		window.getActCreateARandomNode().setEnabled(false);
		window.getActChangeNetworkSize().setEnabled(false);
		window.getActCheckConnectivity().setEnabled(false);
		window.getActClose().setEnabled(false);
		window.getActConfigureNodes().setEnabled(false);
		window.getActCreateASetOfNodes().setEnabled(false);
		window.getActCreateASingleNode().setEnabled(false);
		window.getActDeleteAllNodes().setEnabled(false);
		window.getActDeleteNodes().setEnabled(false);
		window.getActFindConnectivityParts().setEnabled(false);
		window.getActFindPathByGreedy().setEnabled(false);
		window.getActGenerateNodeLocationData().setEnabled(false);
		window.getActGenerateSimulationScripts().setEnabled(false);
		window.getActIdentifyBoundary().setEnabled(false);
		window.getActImport().setEnabled(false);
		window.getActManageLabels().setEnabled(false);
		window.getActManagePaths().setEnabled(false);
		window.getActManageTrafficFlow().setEnabled(false);
		window.getActMouseCreateArea().setEnabled(false);
		window.getActMouseCreateNode().setEnabled(false);
		window.getActMouseCursor().setEnabled(false);
		window.getActMouseHand().setEnabled(false);
		window.getActPrint().setEnabled(false);
		window.getActRedo().setEnabled(false);
		window.getActSave().setEnabled(false);
		window.getActSaveAs().setEnabled(false);
		window.getActSearchNode().setEnabled(false);
		window.getActShowConnection().setEnabled(false);
		window.getActShowNeighbors().setEnabled(false);
		window.getActShowObstacles().setEnabled(false);
		window.getActShowRange().setEnabled(false);
		window.getActShowRulers().setEnabled(false);
		window.getActToImage().setEnabled(false);
//		window.getActToPDF().setEnabled(false);
		window.getActUndo().setEnabled(false);
		window.getActViewDelaunayTriangulation().setEnabled(false);
		window.getActViewGGGraph().setEnabled(false);
		window.getActViewNetworkInfo().setEnabled(false);
		window.getActViewNodeInfo().setEnabled(false);
		window.getActViewRNGGraph().setEnabled(false);
		window.getActViewShortestPath().setEnabled(false);
		window.getActViewShortestPathTree().setEnabled(false);
		window.getActViewVoronoiDiagram().setEnabled(false);
		window.getActZoomIn().setEnabled(false);
		window.getActZoomOut().setEnabled(false);
		window.getActConfigNS2().setEnabled(false);
		window.getActRunNS2().setEnabled(false);
		window.getActNetworkReferenceRemain().setEnabled(false);
		window.getActScriptReferenceRemain().setEnabled(false);
	}
	
	public void activeProject() {
		window.getActCreateARandomNode().setEnabled(true);
		window.getActChangeNetworkSize().setEnabled(true);
		window.getActCheckConnectivity().setEnabled(true);
		window.getActClose().setEnabled(true);
		window.getActConfigureNodes().setEnabled(true);
		window.getActCreateASetOfNodes().setEnabled(true);
		window.getActCreateASingleNode().setEnabled(true);
		window.getActDeleteAllNodes().setEnabled(true);
		window.getActDeleteNodes().setEnabled(true);
		window.getActFindConnectivityParts().setEnabled(true);
		window.getActFindPathByGreedy().setEnabled(true);
		window.getActGenerateNodeLocationData().setEnabled(true);
		window.getActGenerateSimulationScripts().setEnabled(true);
		window.getActIdentifyBoundary().setEnabled(true);
		window.getActImport().setEnabled(true);
		window.getActManageLabels().setEnabled(true);
		window.getActManagePaths().setEnabled(true);
		window.getActManageTrafficFlow().setEnabled(true);
		window.getActMouseCreateArea().setEnabled(true);
		window.getActMouseCreateNode().setEnabled(true);
		window.getActMouseCursor().setEnabled(true);
		window.getActMouseHand().setEnabled(true);
		window.getActPrint().setEnabled(true);
		window.getActRedo().setEnabled(true);
		window.getActSave().setEnabled(true);
		window.getActSaveAs().setEnabled(true);
		window.getActSearchNode().setEnabled(true);
		window.getActShowConnection().setEnabled(true);
		window.getActShowNeighbors().setEnabled(true);
		window.getActShowObstacles().setEnabled(true);
		window.getActShowRange().setEnabled(true);
		window.getActShowRulers().setEnabled(true);
		window.getActToImage().setEnabled(true);
//		window.getActToPDF().setEnabled(true);
		window.getActUndo().setEnabled(true);
		window.getActViewDelaunayTriangulation().setEnabled(true);
		window.getActViewGGGraph().setEnabled(true);
		window.getActViewNetworkInfo().setEnabled(true);
		window.getActViewNodeInfo().setEnabled(true);
		window.getActViewRNGGraph().setEnabled(true);
		window.getActViewShortestPath().setEnabled(true);
		window.getActViewShortestPathTree().setEnabled(true);
		window.getActViewVoronoiDiagram().setEnabled(true);
		window.getActZoomIn().setEnabled(true);
		window.getActZoomOut().setEnabled(true);
		window.getActConfigNS2().setEnabled(true);
		window.getActRunNS2().setEnabled(true);
		window.getActNetworkReferenceRemain().setEnabled(true);
		window.getActScriptReferenceRemain().setEnabled(true);
	}

	public void activeMouseCreateArea() {
		window.getActMouseCursor().setChecked(false);
		window.getActMouseHand().setChecked(false);
		window.getActMouseCreateNode().setChecked(false);
		window.getActMouseCreateArea().setChecked(true);
		window.getWorkspace().disableNodes();
		window.getWorkspace().enableNetwork();
	}
	
	public void activeMouseHand() {
		window.getActMouseCursor().setChecked(false);
		window.getActMouseHand().setChecked(true);
		window.getActMouseCreateNode().setChecked(false);
		window.getActMouseCreateArea().setChecked(false);
		window.getWorkspace().disableNetwork();
		window.getWorkspace().getGraphicNetwork().clearSelectedArea();
	}
	
	public void activeMouseCreateNode() {
		window.getActMouseCursor().setChecked(false);
		window.getActMouseHand().setChecked(false);
		window.getActMouseCreateNode().setChecked(true);
		window.getActMouseCreateArea().setChecked(false);
		window.getWorkspace().disableNodes();
		window.getWorkspace().enableNetwork();
		window.getWorkspace().getGraphicNetwork().clearSelectedArea();
	}
	
	public void activeMouseCursor() {
		window.getActMouseCursor().setChecked(true);
		window.getActMouseHand().setChecked(false);
		window.getActMouseCreateNode().setChecked(false);
		window.getActMouseCreateArea().setChecked(false);
		window.getWorkspace().enableNodes();
		window.getWorkspace().getGraphicNetwork().clearSelectedArea();
	}
}
