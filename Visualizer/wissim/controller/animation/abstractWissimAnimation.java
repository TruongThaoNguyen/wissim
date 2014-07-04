package wissim.controller.animation;

import java.util.ArrayList;

import org.graphstream.graph.Graph;

import TraceFileParser.wissim.AbstractParser;
import TraceFileParser.wissim.Packet;
import TraceFileParser.wissim.FullParser;

public abstract class abstractWissimAnimation {
	/**
	 * 
	 * @param fromTime
	 * @param toTime
	 * @param mGraph
	 */
	public abstract void animationEvent(String fromTime, String toTime,
			Graph mGraph);
	/**
	 * 
	 * @param fromTime
	 * @param toTime
	 * @param mGraph
	 * @param percentage
	 */
	public abstract void animationEnergy(String fromTime,String toTime, Graph mGraph,boolean percentage);
	/**
	 * 
	 * @param fromTime
	 * @param toTime
	 * @param groupID1
	 * @param groupID2
	 * @param parser
	 * @param mGraph
	 */
	public abstract void animationEventbyGroupID(String fromTime,
			String toTime, ArrayList<String> groupID1,
			ArrayList<String> groupID2,AbstractParser parser,Graph mGraph);
	/**
	 * 
	 * @param packet
	 * @param mGraph
	 */
	public abstract void animationPacket(Packet packet, Graph mGraph);
	/**
	 * 
	 * @param packet
	 * @param mGraph
	 */
	public abstract void resetanimationPacket(Packet packet, Graph mGraph);
	/**
	 * 
	 * @param mGraph
	 */
	public abstract void reset(Graph mGraph);
}
