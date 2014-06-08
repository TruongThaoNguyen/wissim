package wissim.object;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.AbstractEdge;
import org.graphstream.graph.implementations.AbstractNode;

public class wiEdge {

    Edge edge;
    WiGraph graph;

    public wiEdge(WiGraph g, Edge e) {
        graph = g;
        edge = e;
    }

    public EdgeData getData() {
        return edge.getAttribute("data");
    }
    public String getId() {
        return edge.getId();
    }
    // <editor-fold defaultstate="collapsed" desc="Not supported">
    // </editor-fold>

	public Edge getEdge() {
		return edge;
	}

	public void setEdge(Edge edge) {
		this.edge = edge;
	}

	public WiGraph getGraph() {
		return graph;
	}

	public void setGraph(WiGraph graph) {
		this.graph = graph;
	}
    
}
