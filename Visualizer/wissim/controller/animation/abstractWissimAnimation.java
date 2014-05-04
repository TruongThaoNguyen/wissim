package wissim.controller.animation;

import org.graphstream.graph.Graph;

public abstract class abstractWissimAnimation {
	public abstract void animationEvent(String fromTime, String toTime,
			Graph mGraph);

}
