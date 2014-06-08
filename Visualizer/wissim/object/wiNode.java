package wissim.object;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

public class wiNode {

	Node node;
	WiGraph graph;

	public wiNode(WiGraph g, Node n) {
		graph = g;
		node = n;

	}

	public String getId() {
		return node.getId();
	}

	public void setLabel(String lb) {
		node.setAttribute("ui.label", lb);
	}

	public Graph getGraph() {
		return graph;
	}

	public enum State {

		Send, Broadcast, Receive, Forward, Wake, Sleep, Default
	}

	public void setCoordinate(double x, double y) {
		node.addAttribute("xy", x, y);
	}

	public NodeData getData() {
		return node.getAttribute("data");
	}

	public void pinNode() {
		node.addAttribute("layout.frozen");
	}

	public void bookMark() {
	}

	public void setAttribute(String uiclass, String attributes) {
		node.setAttribute(uiclass, attributes);
	}

	public void setAttribute(String attributes) {
		node.setAttribute(attributes);
	}

	public void setState(State s) {
		if (getState() == s) {
			return;
		}
		State currState = getData().state;
		switch (s) {
		case Default:
			node.changeAttribute("ui.class", "init");
			break;
		case Broadcast:
			node.changeAttribute("ui.class", "broadcast");
			break;
		case Forward:
			node.changeAttribute("ui.class", "forward");
			break;
		case Receive:
			if (currState == State.Forward) {
			} else if (currState == State.Send) {
				node.changeAttribute("ui.class", "forward");
			} else {
				node.changeAttribute("ui.class", "receive");
			}

			break;
		case Send:
			if (currState == State.Forward) {
			} else if (currState == State.Receive) {
				node.changeAttribute("ui.class", "forward");
			} else {
				node.changeAttribute("ui.class", "receive");
			}

			break;
		case Sleep:
			node.changeAttribute("ui.class", "sleep");
			break;
		case Wake:
			node.changeAttribute("ui.class", "wake");
			break;
		}
		currState = s;
	}

	public State getState() {
		return getData().state;
	}
	// <editor-fold defaultstate="collapsed" desc="Not supported">
	// </editor-fold>
}
