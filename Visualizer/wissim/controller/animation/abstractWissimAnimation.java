package wissim.controller.animation;

import java.util.ArrayList;

import org.graphstream.graph.Graph;

import TraceFileParser.wissim.AbstractParser;
import TraceFileParser.wissim.Packet;
import TraceFileParser.wissim.FullParser;

public abstract class abstractWissimAnimation {
	
	public abstract void animationEvent(String fromTime, String toTime,
			Graph mGraph);
	public abstract void animationEnergy(String fromTime,String toTime, Graph mGraph,boolean percentage);
	public abstract void animationEventbyGroupID(String fromTime,
			String toTime, ArrayList<String> groupID1,
			ArrayList<String> groupID2,AbstractParser parser,Graph mGraph);

	public abstract void animationPacket(Packet packet, Graph mGraph);

	public abstract void resetanimationPacket(Packet packet, Graph mGraph);
	public abstract void reset(Graph mGraph);
}
