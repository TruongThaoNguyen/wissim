package wissim.controller.drawer;

import java.util.ArrayList;

import org.graphstream.graph.Graph;

import TraceFileParser.wissim.Event;

public abstract class abstractWissimDrawer {
	public abstract void onDraw(ArrayList<Event> listEventinInterval,
			Graph mGraph);

	public abstract void onResetDraw(ArrayList<Event> listEventinTerval,
			Graph mGraph);

}
