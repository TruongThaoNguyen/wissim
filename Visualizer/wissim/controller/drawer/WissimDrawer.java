package wissim.controller.drawer;

import java.util.ArrayList;

import java.util.HashMap;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.ui.spriteManager.Sprite;
import org.graphstream.ui.spriteManager.SpriteManager;

import wissim.controller.filters.IParser;

import TraceFileParser.wissim.AbstractParser;
import TraceFileParser.wissim.Event;
import TraceFileParser.wissim.NodeEnergy;
import TraceFileParser.wissim.NodeTrace;
import TraceFileParser.wissim.Packet;

public class WissimDrawer extends abstractWissimDrawer {
	protected ArrayList<Edge> listEdge;
	protected SpriteManager mSpriteManager;
	protected HashMap<String, ArrayList<Event>> mHashSpriteEvent;

	public void onDraw(ArrayList<Event> listEventinInterval, Graph mGraph) {
		// TODO Auto-generated method stub

		mHashSpriteEvent = new HashMap<String, ArrayList<Event>>();
		if (mGraph != null && mGraph.getNodeCount() > 0) {
			/**
			 * Clear previous drawer
			 */
			for (int i = 0; i < mGraph.getNodeCount(); i++) {
				mGraph.getNode(i).removeAttribute("ui.class");
			}
			if (mSpriteManager != null) {

				for (int i = 0; i < mGraph.getEdgeCount(); i++) {
					Edge e = mGraph.getEdge(i);
					Sprite s = mSpriteManager.getSprite(e.getId());
					if (s != null) {
						s.removeAttribute("ui.class");
						mSpriteManager.removeSprite(s.getId());
					}

				}
			}
			while (mGraph.getEdgeCount() > 0)
				mGraph.removeEdge(0);
		}

		mSpriteManager = new SpriteManager(mGraph);
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
							Sprite s = mSpriteManager.addSprite(e.getId());

							s.attachToEdge(e.getId());
							s.setPosition(0.5);
							ArrayList<Event> listEventofEdge = new ArrayList<Event>();
							listEventofEdge.add(event);
							mHashSpriteEvent.put(s.getId(), listEventofEdge);
							e.addAttribute("ui.layout", "frozen");

							sendNode.setAttribute("ui.class", "send");
							desNode.setAttribute("ui.class", "receive");

							e.setAttribute("ui.class", "light");

						} else {
							Sprite s = mSpriteManager.getSprite(sendNode
									.toString() + "TO" + desNode.toString());
							if (mHashSpriteEvent.get(s.getId()).size() > 0)
								mHashSpriteEvent.get(s.getId()).add(event);
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
							final Edge e = mGraph.addEdge(sendNode.toString()
									+ "FOWARDTO" + desNode.toString(),
									sendNode.getId(), desNode.getId(), true);
							Sprite s = mSpriteManager.addSprite(e.getId());
							s.attachToEdge(e.getId());
							s.setPosition(0.5);

							ArrayList<Event> listEventofEdge = new ArrayList<Event>();
							listEventofEdge.add(event);
							mHashSpriteEvent.put(s.getId(), listEventofEdge);
							e.addAttribute("ui.layout", "frozen");
							e.addAttribute("ui.layout", "frozen");

							sendNode.setAttribute("ui.class", "forward");
							desNode.setAttribute("ui.class", "receive");

							e.setAttribute("ui.class", "forward");

						} else {
							Sprite s = mSpriteManager.getSprite(sendNode
									.toString()
									+ "FOWARDTO"
									+ desNode.toString());
							if (mHashSpriteEvent.get(s.getId()).size() > 0)
								mHashSpriteEvent.get(s.getId()).add(event);
						}
					}
				}
				break;
			case "Drop":
				if (!event.packetType.equals("AGT")) {
					Node sendNode = mGraph.getNode(event.sourceId);
					Node desNode = mGraph.getNode(event.destId);
					if (sendNode != null && desNode != null
							&& sendNode.getId().equals(desNode.getId())) {
						sendNode.removeAttribute("ui.class");
					}
				}
				break;
			}
		}
		listEventinInterval = new ArrayList<Event>();

	}

	@Override
	public void onResetDraw(ArrayList<Event> listEventinTerval, Graph mGraph) {
		// TODO Auto-generated method stub

	}

	public HashMap<String, ArrayList<Event>> getmHashSpriteEvent() {
		return mHashSpriteEvent;
	}

	@Override
	public void onDrawOnePacketPath(Packet packet, Graph mGraph) {
		if (mGraph == null) {
			return;
		} else {
			if (packet.listNode.size() >= 2) {
				if (mGraph.getEdge(packet.listNode.get(0).getId() + "TO"
						+ packet.listNode.get(1).getId()) != null) {
					Edge e = mGraph.getEdge(packet.listNode.get(0).getId()
							+ "TO" + packet.listNode.get(1).getId());
					e.setAttribute("ui.class", "focus");
				} else if (packet.listNode.get(0).getId() != packet.listNode
						.get(1).getId()) {
					Edge e = mGraph.addEdge(packet.listNode.get(0).getId()
							+ "TO" + packet.listNode.get(1).getId(),
							packet.listNode.get(0).getId(), packet.listNode
									.get(1).getId(), true);
					e.setAttribute("ui.class", "focus");

				}
				for (int i = 1; i < packet.listNode.size() - 1; i++) {
					NodeTrace startNode = packet.listNode.get(i);
					NodeTrace desNode = packet.listNode.get(i + 1);
					if (mGraph.getEdge(startNode.getId() + "FOWARDTO"
							+ desNode.getId()) != null
							&& startNode.getId() != desNode.getId()) {
						Edge e = mGraph.getEdge(startNode.getId() + "FOWARDTO"
								+ desNode.getId());
						e.setAttribute("ui.class", "focus");
					} else if (startNode.getId() != desNode.getId()) {
						Edge e = mGraph.addEdge(startNode.getId() + "FOWARDTO"
								+ desNode.getId(), startNode.getId(),
								desNode.getId());
						e.setAttribute("ui.class", "focus");
					}
				}
			}
		}

	}

	@Override
	public void onResetDrawOnePacketPath(Packet packet, Graph mGraph) {
		// TODO Auto-generated method stub

		if (mGraph == null) {
			return;
		} else {
			if (packet.listNode.size() >= 2) {
				if (mGraph.getEdge(packet.listNode.get(0).getId() + "TO"
						+ packet.listNode.get(1).getId()) != null) {
					Edge e = mGraph.getEdge(packet.listNode.get(0).getId()
							+ "TO" + packet.listNode.get(1).getId());
					System.out.println("Reset Edge " + e.getId());
					e.setAttribute("ui.class", "light");
				} else {

				}
				for (int i = 1; i < packet.listNode.size() - 2; i++) {
					NodeTrace startNode = packet.listNode.get(i);
					NodeTrace desNode = packet.listNode.get(i + 1);
					if (mGraph.getEdge(startNode.getId() + "FOWARDTO"
							+ desNode.getId()) != null
							&& startNode.getId() != desNode.getId()) {
						Edge e = mGraph.getEdge(startNode.getId() + "FOWARDTO"
								+ desNode.getId());
						e.setAttribute("ui.class", "forward");
					} else {
					}
				}
			}
			packet = null;
		}

	}

	public void setAreaMode() {
		isInAreaMode = true;
	}

	public void setNormalMode() {
		isInAreaMode = false;
	}

	@Override
	public void onDrawEnergy(ArrayList<NodeEnergy> listNodeEnergy,
			Graph mGraph, AbstractParser mParser, boolean percentage) {
		// TODO Auto-generated method stub

		if (mGraph != null && mGraph.getNodeCount() > 0) {
			/**
			 * Clear previous drawer
			 */
			
			if (mSpriteManager != null) {

				for (int i = 0; i < mGraph.getEdgeCount(); i++) {
					Edge e = mGraph.getEdge(i);
					Sprite s = mSpriteManager.getSprite(e.getId());
					if (s != null) {
						s.removeAttribute("ui.class");
						mSpriteManager.removeSprite(s.getId());
					}

				}
			}
			while (mGraph.getEdgeCount() > 0)
				mGraph.removeEdge(0);
		}
		int cnt = 0;
		for (NodeEnergy ne : listNodeEnergy) {
			Double percentagevalue = (double) Math.round(Double.parseDouble(ne
					.getEnergy())
					/ Double.parseDouble(mParser.getmaxEnergyFromNodeID(ne
							.getNodeID())) * 100*100)/100.000;
			if (Double.parseDouble(ne.getEnergy())
					/ Double.parseDouble(mParser.getmaxEnergyFromNodeID(ne
							.getNodeID())) > 0.9) {
				mGraph.getNode(ne.getNodeID()).addAttribute("ui.class",
						"fullenergy");
				if (percentage)
					mGraph.getNode(ne.getNodeID()).setAttribute("ui.label",
							percentagevalue + "%");
				else
					mGraph.getNode(ne.getNodeID()).setAttribute("ui.label",
							ne.getNodeID());
			} else if (Double.parseDouble(ne.getEnergy())
					/ Double.parseDouble(mParser.getmaxEnergyFromNodeID(ne
							.getNodeID())) <= 0.9
					&& Double.parseDouble(ne.getEnergy())
							/ Double.parseDouble(mParser
									.getmaxEnergyFromNodeID(ne.getNodeID())) > 0.8) {
				mGraph.getNode(ne.getNodeID()).addAttribute("ui.class",
						"nineper");
				if (percentage)
					mGraph.getNode(ne.getNodeID()).setAttribute("ui.label",
							percentagevalue + "%");
				else
					mGraph.getNode(ne.getNodeID()).setAttribute("ui.label",
							ne.getNodeID());
			}
			else if (Double.parseDouble(ne.getEnergy())
					/ Double.parseDouble(mParser.getmaxEnergyFromNodeID(ne
							.getNodeID())) <= 0.8
					&& Double.parseDouble(ne.getEnergy())
							/ Double.parseDouble(mParser
									.getmaxEnergyFromNodeID(ne.getNodeID())) > 0.7) {
				mGraph.getNode(ne.getNodeID()).addAttribute("ui.class",
						"eightper");
				if (percentage)
					mGraph.getNode(ne.getNodeID()).setAttribute("ui.label",
							percentagevalue + "%");
				else
					mGraph.getNode(ne.getNodeID()).setAttribute("ui.label",
							ne.getNodeID());
			}
			else if (Double.parseDouble(ne.getEnergy())
					/ Double.parseDouble(mParser.getmaxEnergyFromNodeID(ne
							.getNodeID())) <= 0.7
					&& Double.parseDouble(ne.getEnergy())
							/ Double.parseDouble(mParser
									.getmaxEnergyFromNodeID(ne.getNodeID())) > 0.6) {
				mGraph.getNode(ne.getNodeID()).addAttribute("ui.class",
						"sevenper");
				if (percentage)
					mGraph.getNode(ne.getNodeID()).setAttribute("ui.label",
							percentagevalue + "%");
				else
					mGraph.getNode(ne.getNodeID()).setAttribute("ui.label",
							ne.getNodeID());
			}
			else if (Double.parseDouble(ne.getEnergy())
					/ Double.parseDouble(mParser.getmaxEnergyFromNodeID(ne
							.getNodeID())) <= 0.6
					&& Double.parseDouble(ne.getEnergy())
							/ Double.parseDouble(mParser
									.getmaxEnergyFromNodeID(ne.getNodeID())) > 0.5) {
				mGraph.getNode(ne.getNodeID()).addAttribute("ui.class",
						"sixper");
				if (percentage)
					mGraph.getNode(ne.getNodeID()).setAttribute("ui.label",
							percentagevalue + "%");
				else
					mGraph.getNode(ne.getNodeID()).setAttribute("ui.label",
							ne.getNodeID());
			}
			else if (Double.parseDouble(ne.getEnergy())
					/ Double.parseDouble(mParser.getmaxEnergyFromNodeID(ne
							.getNodeID())) <= 0.5
					&& Double.parseDouble(ne.getEnergy())
							/ Double.parseDouble(mParser
									.getmaxEnergyFromNodeID(ne.getNodeID())) > 0.4) {
				mGraph.getNode(ne.getNodeID()).addAttribute("ui.class",
						"fiveper");
				if (percentage)
					mGraph.getNode(ne.getNodeID()).setAttribute("ui.label",
							percentagevalue + "%");
				else
					mGraph.getNode(ne.getNodeID()).setAttribute("ui.label",
							ne.getNodeID());
			}
			else if (Double.parseDouble(ne.getEnergy())
					/ Double.parseDouble(mParser.getmaxEnergyFromNodeID(ne
							.getNodeID())) <= 0.4
					&& Double.parseDouble(ne.getEnergy())
							/ Double.parseDouble(mParser
									.getmaxEnergyFromNodeID(ne.getNodeID())) > 0.3) {
				mGraph.getNode(ne.getNodeID()).addAttribute("ui.class",
						"fourper");
				if (percentage)
					mGraph.getNode(ne.getNodeID()).setAttribute("ui.label",
							percentagevalue + "%");
				else
					mGraph.getNode(ne.getNodeID()).setAttribute("ui.label",
							ne.getNodeID());
			}
			else if (Double.parseDouble(ne.getEnergy())
					/ Double.parseDouble(mParser.getmaxEnergyFromNodeID(ne
							.getNodeID())) <= 0.3
					&& Double.parseDouble(ne.getEnergy())
							/ Double.parseDouble(mParser
									.getmaxEnergyFromNodeID(ne.getNodeID())) > 0.2) {
				mGraph.getNode(ne.getNodeID()).addAttribute("ui.class",
						"threeper");
				if (percentage)
					mGraph.getNode(ne.getNodeID()).setAttribute("ui.label",
							percentagevalue + "%");
				else
					mGraph.getNode(ne.getNodeID()).setAttribute("ui.label",
							ne.getNodeID());
			}
			else if (Double.parseDouble(ne.getEnergy())
					/ Double.parseDouble(mParser.getmaxEnergyFromNodeID(ne
							.getNodeID())) <= 0.2
					&& Double.parseDouble(ne.getEnergy())
							/ Double.parseDouble(mParser
									.getmaxEnergyFromNodeID(ne.getNodeID())) > 0.15) {
				mGraph.getNode(ne.getNodeID()).addAttribute("ui.class",
						"twoper");
				if (percentage)
					mGraph.getNode(ne.getNodeID()).setAttribute("ui.label",
							percentagevalue + "%");
				else
					mGraph.getNode(ne.getNodeID()).setAttribute("ui.label",
							ne.getNodeID());
			}
			else {

				mGraph.getNode(ne.getNodeID()).addAttribute("ui.class",
						"lowenergy");
				if (percentage)
					mGraph.getNode(ne.getNodeID()).setAttribute("ui.label",
							percentagevalue + "%");
				else
					mGraph.getNode(ne.getNodeID()).setAttribute("ui.label",
							ne.getNodeID());
			}
			cnt++;
		}

	}

}
