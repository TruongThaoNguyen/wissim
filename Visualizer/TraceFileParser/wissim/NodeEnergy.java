package TraceFileParser.wissim;

public class NodeEnergy {
	protected String time = "";
	protected String energy;
	protected String maxEnergy;
	protected String nodeID;
	public String getNodeID() {
		return nodeID;
	}

	public void setNodeID(String nodeID) {
		this.nodeID = nodeID;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	@Override
	public String toString() {
		return "NodeEnergy [time=" + time + ", energy=" + energy + "]";
	}

	public String getEnergy() {
		return energy;
	}

	public void setEnergy(String energy) {
		this.energy = energy;
	}

	public NodeEnergy(String nodeID,String time, String energy) {
		super();
		this.nodeID = nodeID;
		this.time = time;
		this.energy = energy;
	}

}
