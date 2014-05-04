package TraceFileParser.wissim;
import java.util.ArrayList;


public class NodeTrace {
		public int id;
		public float x;
		public float y;
		public float z;
		private int packetId;
		public String time;
		public String energy;
		public String listIDNeighbors;
		//public String firstAction;
		//public String secondAction;
		
		public String getEnergy() {
			return energy;
		}

		public void setEnergy(String energy) {
			this.energy = energy;
		}
		
		public String getListNeighbors() {
			return listIDNeighbors;
		}

		public void setListNeighbors(String listNeighbors) {
			this.listIDNeighbors = listNeighbors;
		}

		public NodeTrace(int id,String time){
			this.id=id;
			this.time=time;
		}
		
		public NodeTrace(int id, float x, float y, float z, String time, String energy,
				String listNeighbors) {
			super();
			this.id = id;
			this.x = x;
			this.y = y;
			this.z = z;
			this.time = time;
			this.energy = energy;
			this.listIDNeighbors = listNeighbors;
		}

		public int getPacketId(){
			return this.packetId;
		}
		public void setPacketId(int packetId){
			this.packetId=packetId;
		}
}
