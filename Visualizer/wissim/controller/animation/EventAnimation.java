package wissim.controller.animation;

import java.awt.Event;
import java.util.ArrayList;

import javax.swing.JTextArea;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import TraceFileParser.wissim.AbstractParser;
import TraceFileParser.wissim.NodeEnergy;
import TraceFileParser.wissim.Packet;
import TraceFileParser.wissim.FullParser;

import wissim.controller.drawer.WissimDrawer;

public class EventAnimation extends abstractWissimAnimation {
	protected static ArrayList<TraceFileParser.wissim.Event> listEvents;
	protected static ArrayList<TraceFileParser.wissim.Event> listEventsInInterval;
	protected static String mFromTime;
	protected static String mToTime;
	protected static String mConsolelogInfor = "";
	static String oneEventInfor = "";
	protected WissimDrawer wsd;
	protected AbstractParser mCurrentParser;

	public EventAnimation() {
		super();
		
		wsd = new WissimDrawer();
	}

	public void setmCurrentParser(AbstractParser mCurrentParser) {
		this.mCurrentParser = mCurrentParser;
	}
	
	public void animationEvent(String fromTime, String toTime,
			final Graph mGraph) {
		// TODO Auto-generated method stub
		if (mGraph == null || mGraph.getNodeCount() < 1 || listEvents == null
				|| listEvents.size() < 1) {
			System.out.println("got null");
			return;
		} else {
			/**
			 * Draw events animation from fromTime to toTime
			 */
			mFromTime = fromTime;
			mToTime = toTime;
			
			listEventsInInterval = getEventsinInterval(fromTime, toTime);
			if (listEventsInInterval != null) {
				new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						wsd.setNormalMode();
						wsd.onDraw(listEventsInInterval, mGraph);
					}
				}).start();

			} else
				return;
			
		}
	}
	
	public WissimDrawer getDrawer(){
		return this.wsd;
	}

	@Override
	public void animationEventbyGroupID(String fromTime, String toTime,
			ArrayList<String> groupID1, ArrayList<String> groupID2,
			AbstractParser parser, final Graph mGraph) {
		// TODO Auto-generated method stub
		if (mGraph == null || mGraph.getNodeCount() < 1 || listEvents == null
				|| listEvents.size() < 1 || parser == null
				|| groupID1.size() == 0 || groupID2.size() == 0) {
			System.out.println("got null");
			System.out.println(">>>" + (mGraph == null) + " "
					+ (listEvents == null) + " " + (parser == null));
			return;
		} else {
			final ArrayList<String> tmp1 = groupID1;
			final ArrayList<String> tmp2 = groupID2;
			mFromTime = fromTime;
			mToTime = toTime;
			listEventsInInterval = getEventsinInterval(fromTime, toTime);
			mCurrentParser = parser;
			listEventsInInterval = filterEventsbygroupID(groupID1, groupID2,
					listEventsInInterval, mCurrentParser);
			
			if (listEventsInInterval != null) {
				new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						wsd.setAreaMode();
						wsd.onDraw(listEventsInInterval, mGraph);
						for (String s : tmp1) {
							mGraph.getNode(s)
									.setAttribute("ui.class", "group1");
						}
						for (String s : tmp2) {
							mGraph.getNode(s)
									.setAttribute("ui.class", "group2");
						}
					}
				}).start();

			} else
				return;

		}
	}

	private ArrayList<TraceFileParser.wissim.Event> filterEventsbygroupID(
			ArrayList<String> groupID1, ArrayList<String> groupID2,
			ArrayList<TraceFileParser.wissim.Event> listEventsInInterval2,
			AbstractParser parser) {
		ArrayList<TraceFileParser.wissim.Event> returnList = new ArrayList<TraceFileParser.wissim.Event>();
		ArrayList<String> returnListPacketID = new ArrayList<String>();

		/**
		 * Filter packets
		 */

		for (Packet p : parser.getListPacket()) {
			if (groupID1.contains(p.getSourceID())
					&& groupID2.contains(p.getDestID())) {
				returnListPacketID.add(p.getId());
			}
		}
		/**
		 * Filter events
		 */
		for (TraceFileParser.wissim.Event e : listEventsInInterval2) {
			if (returnListPacketID.contains(e.getPacketId())) {
				returnList.add(e);
			}
		}
		if (returnListPacketID.size() == 0) {
			returnList.clear();
		}
		return returnList;

	}

	public static ArrayList<TraceFileParser.wissim.Event> getEventsinInterval(
			String fromTime, String toTime) {
		listEventsInInterval = new ArrayList<TraceFileParser.wissim.Event>();
		mConsolelogInfor = "";
		
		for (TraceFileParser.wissim.Event event : listEvents) {
			if (Double.parseDouble(event.time) >= Double.parseDouble(fromTime)
					&& Double.parseDouble(event.time) <= Double
							.parseDouble(toTime)) {
				listEventsInInterval.add(event);

				/**
				 * Add infor to console
				 */

				oneEventInfor = "+ Event Type:" + event.type + "\n"
						+ "Packet ID: " + event.packetId + "\n"
						+ "+ From Node: " + event.sourceId + "\n"
						+ "+ To Node: " + event.destId + "\n" + "Time: "
						+ event.time + "\n" + "************ \n";
				mConsolelogInfor += oneEventInfor;
			}
		}
		if (listEventsInInterval == null || listEventsInInterval.size() == 0)
			return null;
		else
			return listEventsInInterval;
	}

	public ArrayList<TraceFileParser.wissim.Event> getListEvents() {
		return listEvents;
	}

	public void setListEvents(ArrayList<TraceFileParser.wissim.Event> listEvents) {
		this.listEvents = listEvents;
	}

	public void setLogInforfromEvent(JTextArea consoleInfor) {
		
		if (mConsolelogInfor.length() > 0) {
			consoleInfor.setText("");
			consoleInfor.setText(mConsolelogInfor);
		}
	}

	public WissimDrawer getWsd() {
		return wsd;
	}

	@Override
	public void animationPacket(Packet packet, Graph mGraph) {
		// TODO Auto-generated method stub
		if (wsd != null) {
			wsd.onDrawOnePacketPath(packet, mGraph);
		}
	}

	@Override
	public void resetanimationPacket(Packet packet, Graph mGraph) {
		// TODO Auto-generated method stub
		if (wsd != null) {
			wsd.onResetDrawOnePacketPath(packet, mGraph);
		}
	}

	@Override
	public void animationEnergy(String fromTime, String toTime, Graph mGraph,boolean percentage) {
		// TODO Auto-generated method stub
		ArrayList<NodeEnergy> listNe = new ArrayList<NodeEnergy>();
		for (Node node : mGraph.getEachNode()) {
			ArrayList<NodeEnergy> listNodeEnergy = mCurrentParser
					.getListEnergy().get(Integer.parseInt(node.getId()));
			NodeEnergy tmpne = null;
			for (NodeEnergy ne : listNodeEnergy) {
				if (Double.parseDouble(ne.getTime()) > Double
						.parseDouble(toTime))
					break;
				tmpne = ne;

			}
			if (tmpne != null)
				listNe.add(tmpne);

		}
		if(wsd != null){
			wsd.onDrawEnergy(listNe,mGraph,mCurrentParser,percentage);
		}
		
	}

	@Override
	public void reset(Graph mGraph) {
		// TODO Auto-generated method stub
		if(wsd != null && mGraph != null)
			wsd.onResetDraw(mGraph);
	}

}
