package wissim.controller.drawer;

import java.util.ArrayList;

import org.graphstream.graph.Graph;

import wissim.controller.filters.IParser;

import TraceFileParser.wissim.AbstractParser;
import TraceFileParser.wissim.Event;
import TraceFileParser.wissim.NodeEnergy;
import TraceFileParser.wissim.Packet;

public abstract class abstractWissimDrawer {
	protected boolean isInAreaMode;

	public abstract void onDraw(ArrayList<Event> listEventinInterval,
			Graph mGraph);

	public abstract void onDrawOnePacketPath(Packet packet, Graph mGraph);
	public abstract void onDrawPacketPath(Packet packet, Graph mGraph);

	public abstract void onResetDrawOnePacketPath(Packet packet, Graph mGraph);

	public abstract void onResetDraw(
			Graph mGraph);
	public abstract void onDrawEnergy(ArrayList<NodeEnergy> listNodeEnergy,Graph mGraph,AbstractParser mParser,boolean percentage);
}