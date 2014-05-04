package wissim.controller.drawer;

import java.util.ArrayList;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import TraceFileParser.wissim.Event;

public class WissimDrawer extends abstractWissimDrawer {

	public void onDraw(ArrayList<Event> listEventinInterval, Graph mGraph) {
		// TODO Auto-generated method stub
		if (mGraph != null) {

		}

		for (Event event : listEventinInterval) {
			switch (event.type) {

			case "send":
				if (!event.packetType.equals("AGT")) {
					Node sendNode = mGraph.getNode(event.sourceId);
					Node desNode = mGraph.getNode(event.destId);
					if (sendNode != null && desNode != null) {
						if (mGraph.getEdge(sendNode.toString() + "TO"
								+ desNode.toString()) == null) {
							Edge e = mGraph.addEdge(sendNode.toString() + "TO"
									+ desNode.toString(), sendNode.getId(),
									desNode.getId(), true);
							e.addAttribute("ui.layout", "frozen");
							sendNode.setAttribute("ui.class", "send");
							desNode.setAttribute("ui.class", "receive");
							e.setAttribute("ui.class", "dark");
							System.out.println("Draw send from "
									+ sendNode.getId() + "To "
									+ desNode.getId());

						}
					}
				}
				break;
			case "receive":
				break;
			case "forward":
				if (!event.packetType.equals("AGT")) {
					Node sendNode = mGraph.getNode(event.sourceId);
					Node desNode = mGraph.getNode(event.destId);
					if (sendNode != null && desNode != null) {
						if (mGraph.getEdge(sendNode.toString() + "FOWARDTO"
								+ desNode.toString()) == null) {
							Edge e = mGraph.addEdge(sendNode.toString() + "FOWARDTO"
									+ desNode.toString(), sendNode.getId(),
									desNode.getId(), true);
							e.addAttribute("ui.layout", "frozen");
							sendNode.setAttribute("ui.class", "forward");
							desNode.setAttribute("ui.class", "receive");
							e.setAttribute("ui.class", "dark");
							System.out.println("Draw send from "
									+ sendNode.getId() + "To "
									+ desNode.getId());

						}
					}
				}
				break;
			case "Drop":
				break;
			}
		}

	}

	@Override
	public void onResetDraw(ArrayList<Event> listEventinTerval, Graph mGraph) {
		// TODO Auto-generated method stub

	}

}
