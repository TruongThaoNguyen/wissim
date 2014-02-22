package graphicscomponents;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import networkcomponents.Node;
import networkcomponents.WirelessNode;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;

import control.manager.ProjectManager;
import view.Workspace;
import algorithms.Graph;
import algorithms.PlanarGraphAlgorithm;
import algorithms.delaunay.Pnt;
import algorithms.delaunay.Triangle;
import algorithms.delaunay.Triangulation;

public class VisualizeManager {
	public static final int NO_COLOR =  0, MONOCOLOR = 1, MULTICOLOR = 2;

	List<GraphicPath> paths;						// storing paths using for visualization	
	List<List<GWirelessNode>> boundaryList;			// boundaries	
	
	// for Voronoi Diagram and Delaunay Triangulation
    public int pointRadius = 3;						// radius of points
    Triangulation dt;                   			// Delaunay triangulation
    Map<Object, Color> colorTable;      			// Remembers colors for display
    Triangle initialTriangle;           			// Initial triangle
    public static int initialSize = 10000;     		// Size of initial triangle
    
    Graph rngGraph;
    Graph ggGraph;
    
    private GNetwork gnetwork;
    
    public VisualizeManager(GNetwork gnetwork) {
    	this.gnetwork = gnetwork;
    	
    	initialize();
    }
    
    private void initialize() {
    	paths = new LinkedList<GraphicPath>();
    }
    
    // functions for visualizing Voronoi Diagram and Delaunay Triangulation    
    public Triangulation getTriangulation() {
    	return dt;
    }
    
    public void initializeTriangulation() {
        dt = new Triangulation(initialTriangle);
    }
    
    public void calculateVoronoiDiagram() {
    	Workspace workspace = (Workspace) gnetwork.getParent();
    	
        initialTriangle = new Triangle(
                new Pnt(-initialSize, -initialSize),
                new Pnt( initialSize, -initialSize),
                new Pnt(           0,  initialSize));
        dt = new Triangulation(initialTriangle);
        colorTable = new HashMap<Object, Color>();
        
        for (Node node : workspace.getProject().getNetwork().getNodeList()) {
        	WirelessNode wnode = (WirelessNode) node;
        	Point p = new Point(
        			(int)(wnode.getX() * gnetwork.getRatio()), 
        			(int)(wnode.getY() * gnetwork.getRatio()));
        	dt.delaunayPlace(new Pnt(p.x, p.y));
        }
    }
    
	public void calculateRNGGraph() {
		if (ProjectManager.checkConnectivity(((Workspace) gnetwork.getParent()).getProject())) {
	    	PlanarGraphAlgorithm algorithm = new PlanarGraphAlgorithm(gnetwork.getNetwork());
	    	algorithm.setType(PlanarGraphAlgorithm.RNG);
	    	rngGraph = (Graph) algorithm.doAlgorithm();
		} else
			rngGraph = null;
    }
    
	public void calculateGGGraph() {
		if (ProjectManager.checkConnectivity(((Workspace) gnetwork.getParent()).getProject())) {		
	    	PlanarGraphAlgorithm algorithm = new PlanarGraphAlgorithm(gnetwork.getNetwork());
	    	algorithm.setType(PlanarGraphAlgorithm.GG);
	    	ggGraph = (Graph) algorithm.doAlgorithm();    	
		} else
			ggGraph = null;
    }
    
    /**
     * Adds path to the path list if the path doesn't exist
     * @param path
     */
    public void addPath(GraphicPath path) {
    	if (!paths.contains(path))
    		paths.add(path);
    }
    
    /**
     * Removes path from the path list if the path does exist
     * @param path
     */
    public void removePath(GraphicPath path) {
    	if (paths.contains(path))
    		paths.remove(path);
    }
    
    /**
     * Removes all visualization paths
     */
    public void removeAllPaths() {
    	paths.clear();
    }

    public void clearVisualizeData() {
    	initialize();
    }
    
    /**
     * Gets all greedy paths
     * @return
     */
    public List<GraphicPath> getGreedyPaths() {
    	List<GraphicPath> gpaths = new LinkedList<GraphicPath>();
    	
    	for (GraphicPath p : paths)
    		if (p.type == GraphicPath.GREEDY)
    			gpaths.add(p);
    	
    	return gpaths;
    }
    
    /**
     * Gets all shortest paths
     * @return
     */
    public List<GraphicPath> getShortestPaths() {
    	List<GraphicPath> spaths = new LinkedList<GraphicPath>();
    	
    	for (GraphicPath p : paths)
    		if (p.type == GraphicPath.SHORTEST)
    			spaths.add(p);
    	
    	return spaths;
    }    
    
    public List<GraphicPath> getPaths() {
    	return paths;
    }
    
    public GraphicPath getPath(int type, GWirelessNode startNode, GWirelessNode endNode) {
    	for (GraphicPath p : paths)
    		if (p.type == type && 
    		p.getStartNode().getWirelessNode().getId() == startNode.getWirelessNode().getId() &&
    		p.getEndNode().getWirelessNode().getId() == endNode.getWirelessNode().getId())
    			return p;
    	
    	return null;
    }
    
    public GraphicPath getPath(int type, int startNodeId, int endNodeId) {
    	for (GraphicPath p: paths)
    		if (p.type == type && p.getStartNode().getWirelessNode().getId() == startNodeId &&
    		p.getEndNode().getWirelessNode().getId() == endNodeId)
    			return p;
    	
    	return null;
    }
    
    public Graph getRNGGraph() {
    	return rngGraph;
    }
    
    public Graph getGGGraph() {
    	return ggGraph;
    }
}
