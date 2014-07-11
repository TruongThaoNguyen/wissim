package controllers.managers;

import java.util.Observable;

/**
 * Manage properties of features for workspace
 * @author leecom
 *
 */
public class WorkspacePropertyManager extends Observable {
	/**
	 * values for mouse mode.
	 */
	public final static int CURSOR = 0, HAND = 1, NODE_GEN = 2, AREA = 3;	
	
	/**
	 * 	values for visualization.
	 */
	public final static int VISUAL_ON = 1, VISUAL_OFF = 0;				
	
	/**
	 * mouse node.
	 */
	private int mouseMode = CURSOR;			
	
	/**
	 * visualize mode.
	 */
	private int visualizeMode = VISUAL_OFF;
	
	/**
	 * key states.
	 */
	private boolean controlKeyPressed = false, shiftKeyPressed = false;
	
	/**
	 * visualization.
	 */
	private int 	neighborsLen = 0;
	private int[] 	nodeIdShowedNeighbors = null;
	private int 	rangeLen = 0;
	private int[] 	nodeIdShowedRange = null;
	private boolean showConnection = false;
	private boolean showVoronoiDiagram = false;
	private boolean showDelaunayTriangulation = false;
	private boolean showCirles = false;
	private boolean showShortestPath = false;
	private boolean showShortestPathTree = false;
	private boolean showGreedyPath = false;
	private boolean showBoundary = false;
	private boolean showObstacles = false;
	private boolean showGroups = false;
	private boolean showRNG = false;
	private boolean showGG = false;
	
	/**
	 * Turn visualize off
	 */
	public void turnVisualizeOff() {
		visualizeMode = VISUAL_OFF;
	
		neighborsLen = 0;
		rangeLen = 0;
		nodeIdShowedNeighbors = null;
		nodeIdShowedRange = null;
		showConnection = false;
		showVoronoiDiagram = false;
		showDelaunayTriangulation = false;
		showCirles = false;
		showShortestPath = false;
		showShortestPathTree = false;
		showGreedyPath = false;
		showBoundary = false;
		showObstacles = false;		
		showRNG = false;
		showGG = false;
		
		setChanged();
		notifyObservers("TurnVisualizeOff");
	}
	
	/**
	 * 
	 */
	public void showVoronoiDiagram() {
		visualizeMode = VISUAL_ON;
		
		neighborsLen = 0;
		rangeLen = 0;
		nodeIdShowedNeighbors = null;
		nodeIdShowedRange = null;
		showConnection = false;
		showVoronoiDiagram = true;
		showDelaunayTriangulation = false;
		showCirles = false;
		showShortestPath = false;
		showShortestPathTree = false;
		showGreedyPath = false;
		showBoundary = false;
		showObstacles = false;	
		showRNG = false;
		showGG = false;
		
		setChanged();
		notifyObservers("ShowVisualization");
	}
	
	public void showDelaunayTriangulation() {
		visualizeMode = VISUAL_ON;
		
		neighborsLen = 0;
		rangeLen = 0;
		nodeIdShowedNeighbors = null;
		nodeIdShowedRange = null;
		showConnection = false;
		showVoronoiDiagram = false;
		showDelaunayTriangulation = true;
		showCirles = false;
		showShortestPath = false;
		showShortestPathTree = false;
		showGreedyPath = false;
		showBoundary = false;
		showObstacles = false;
		showRNG = false;
		showGG = false;
		
		setChanged();
		notifyObservers("ShowVisualization");
	}
	
	public void showRNG() {
		visualizeMode = VISUAL_ON;
		
		neighborsLen = 0;
		rangeLen = 0;
		nodeIdShowedNeighbors = null;
		nodeIdShowedRange = null;
		showConnection = false;
		showVoronoiDiagram = false;
		showDelaunayTriangulation = false;
		showCirles = false;
		showShortestPath = false;
		showShortestPathTree = false;
		showGreedyPath = false;
		showBoundary = false;
		showObstacles = false;		
		showRNG = true;
		showGG = false;		
		
		setChanged();
		notifyObservers("ShowVisualization");
	}
	
	public void showGG() {
		visualizeMode = VISUAL_ON;
		
		neighborsLen = 0;
		rangeLen = 0;
		nodeIdShowedNeighbors = null;
		nodeIdShowedRange = null;
		showConnection = false;
		showVoronoiDiagram = false;
		showDelaunayTriangulation = false;
		showCirles = false;
		showShortestPath = false;
		showShortestPathTree = false;
		showGreedyPath = false;
		showBoundary = false;
		showObstacles = false;		
		showRNG = false;
		showGG = true;
		
		setChanged();
		notifyObservers("ShowVisualization");
	}
	
	public void showGreedyPath() {
		visualizeMode = VISUAL_ON;
		
		neighborsLen = 0;
		rangeLen = 0;
		nodeIdShowedNeighbors = null;
		nodeIdShowedRange = null;
		showConnection = false;
		showVoronoiDiagram = false;
		showDelaunayTriangulation = false;
		showCirles = false;
		showShortestPath = false;
		showShortestPathTree = false;
		showGreedyPath = true;
		showBoundary = false;
		showObstacles = false;		
		showRNG = false;
		showGG = false;
		
		setChanged();
		notifyObservers("ShowVisualization");
	}
	
	public void showShortestPath() {
		visualizeMode = VISUAL_ON;
		
		neighborsLen = 0;
		rangeLen = 0;
		nodeIdShowedNeighbors = null;
		nodeIdShowedRange = null;
		showConnection = false;
		showVoronoiDiagram = false;
		showDelaunayTriangulation = false;
		showCirles = false;
		showShortestPath = true;
		showShortestPathTree = false;
		showGreedyPath = false;
		showBoundary = false;
		showObstacles = false;		
		showRNG = false;
		showGG = false;
		
		setChanged();
		notifyObservers("ShowVisualization");
	}

	
	public int getMouseMode() {
		return mouseMode;
	}

	public void setMouseMode(int mouseMode) {
		this.mouseMode = mouseMode;
		
		setChanged();
		
		switch (mouseMode) {
		case CURSOR:
			notifyObservers("SetMouseModeCursor");
			break;
		case HAND:
			notifyObservers("SetMouseModeHand");
			break;			
		case NODE_GEN:
			notifyObservers("SetMouseModeNodeGen");
			break;			
		case AREA:
			notifyObservers("SetMouseModeCreateArea");
			break;
		}
	}

	public int getVisualizeMode() {
		return visualizeMode;
	}

	public void setVisualizeMode(int visualizeMode) {
		this.visualizeMode = visualizeMode;
		
		setChanged();
		notifyObservers("SetVisualizeMode");
	}

	public boolean isControlKeyPressed() {
		return controlKeyPressed;
	}

	public void setControlKeyPressed(boolean isControlKeyPressed) {
		this.controlKeyPressed = isControlKeyPressed;
		
		setChanged();
		notifyObservers("SetControlKeyPressed");
	}

	public boolean isShiftKeyPressed() {
		return shiftKeyPressed;
	}

	public void setShiftKeyPressed(boolean isShiftKeyPressed) {
		this.shiftKeyPressed = isShiftKeyPressed;
		
		setChanged();
		notifyObservers("SetShiftKeyPressed");
	}

	public int getNeighborLen() {
		return neighborsLen;
	}
	
	public int getRangeLen() {
		return rangeLen;
	}
	
	public void setNeighborLen(int neighborsLen) {
		this.neighborsLen = neighborsLen;
		
		setChanged();
		notifyObservers("NeighborLength");
	}
	
	public void setRangeLen(int rangeLen) {
		this.rangeLen = rangeLen;
		
		setChanged();
		notifyObservers("RangeLength");
	}
	
	public int[] getNodeIdShowedNeighbors() {
		return nodeIdShowedNeighbors;
	}

	public void setNodeIdShowedNeighbors(int[] nodeIdShowedNeighbors) {
		this.nodeIdShowedNeighbors = nodeIdShowedNeighbors;
		
		setChanged();
		notifyObservers("NodeIdShowedNeighbors");
	}

	public int[] getNodeIdShowedRange() {
		return nodeIdShowedRange;
	}

	public void setNodeIdShowedRange(int[] nodeIdShowedRange) {
		this.nodeIdShowedRange = nodeIdShowedRange;
		
		setChanged();
		notifyObservers("NodeIdShowRange");
	}

	public boolean isShowConnection() {
		return showConnection;
	}

	public void setShowConnection(boolean showConnection) {
		this.showConnection = showConnection;
		
		setChanged();
		notifyObservers("ShowConnection");
	}

	public boolean isShowVoronoiDiagram() {
		return showVoronoiDiagram;
	}

	public void setShowVoronoiDiagram(boolean showVoronoiDiagram) {
		this.showVoronoiDiagram = showVoronoiDiagram;
		
		setChanged();
		notifyObservers("ShowVoronoiDiagram");
	}

	public boolean isShowDelaunayTriangulation() {
		return showDelaunayTriangulation;
	}

	public void setShowDelaunayTriangulation(boolean showDelaunayTriangulation) {
		this.showDelaunayTriangulation = showDelaunayTriangulation;
		
		setChanged();
		notifyObservers("ShowDelaunayTriangulation");
	}

	public boolean isShowCirles() {
		return showCirles;
	}

	public void setShowCirles(boolean showCirles) {
		this.showCirles = showCirles;
		
		setChanged();
		notifyObservers("ShowCircles");
	}

	public boolean isShowShortestPath() {
		return showShortestPath;
	}

	public void setShowShortestPath(boolean showShortestPath) {
		this.showShortestPath = showShortestPath;
		
		setChanged();
		notifyObservers("ShowShortestPath");
	}

	public boolean isShowShortestPathTree() {
		return showShortestPathTree;
	}

	public void setShowShortestPathTree(boolean showShortestPahtTree) {
		this.showShortestPathTree = showShortestPahtTree;
		
		setChanged();
		notifyObservers("ShowShortestPathTree");
	}

	public boolean isShowGreedyPath() {
		return showGreedyPath;
	}

	public void setShowGreedyPath(boolean showGreedyPath) {
		this.showGreedyPath = showGreedyPath;
		
		setChanged();
		notifyObservers("ShowGreedyPath");
	}

	public boolean isShowBoundary() {
		return showBoundary;
	}

	public void setShowBoundary(boolean showBoundary) {
		this.showBoundary = showBoundary;
		
		setChanged();
		notifyObservers("ShowBoundary");
	}

	public boolean isShowObstacles() {
		return showObstacles;
	}

	/**
	 * hide/show obstacles.
	 * @param showObstacles enable or not
	 */
	public void setShowObstacles(boolean showObstacles) {
		this.showObstacles = showObstacles;
		
		setChanged();
		notifyObservers("ShowObstacles");
	}
	
	/**
	 * hide/show selected areas.
	 * @param showAreas enable or not
	 */
	public void setShowGroups(boolean showGroups) {
		this.showGroups = showGroups;
		
		setChanged();
		notifyObservers("ShowAreas");
	}

	/**
	 * is show Areas or not.
	 * @return
	 */
	public boolean isShowGroups() {
		return showGroups;
	}
	
	public boolean isShowRNG() {
		return showRNG;
	}

	public void setShowRNG(boolean showRNG) {
		this.showRNG = showRNG;
		
		setChanged();
		notifyObservers("ShowRNG");
	}

	public boolean isShowGG() {
		return showGG;
	}

	public void setShowGG(boolean showGG) {
		this.showGG = showGG;
		
		setChanged();
		notifyObservers("ShowGG");
	}	
}