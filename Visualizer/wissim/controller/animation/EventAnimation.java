package wissim.controller.animation;

import java.awt.Event;
import java.util.ArrayList;

import org.graphstream.graph.Graph;

import wissim.controller.drawer.WissimDrawer;

public class EventAnimation extends abstractWissimAnimation {
	protected static ArrayList<TraceFileParser.wissim.Event> listEvents;
	protected static ArrayList<TraceFileParser.wissim.Event> listEventsInInterval;
	protected static String mFromTime;
	protected static String mToTime;
	WissimDrawer wsd = new WissimDrawer();
	public void animationEvent(String fromTime, String toTime, Graph mGraph) {
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
			
			wsd.onDraw(listEventsInInterval, mGraph);
		}
	}

	protected static ArrayList<TraceFileParser.wissim.Event> getEventsinInterval(
			String fromTime, String toTime) {
		listEventsInInterval = new ArrayList<TraceFileParser.wissim.Event>();
		for (TraceFileParser.wissim.Event event : listEvents) {
			if (Double.parseDouble(event.time) >= Double.parseDouble(fromTime)
					&& Double.parseDouble(event.time) <= Double
							.parseDouble(toTime)) {
				listEventsInInterval.add(event);
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

}
