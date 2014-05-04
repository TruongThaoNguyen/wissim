package TraceFileParser.wissim;

public class NodeEnergy {
	protected String time = "";
	protected String energy;

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getEnergy() {
		return energy;
	}

	public void setEnergy(String energy) {
		this.energy = energy;
	}

	public NodeEnergy(String time, String energy) {
		super();
		this.time = time;
		this.energy = energy;
	}

}
