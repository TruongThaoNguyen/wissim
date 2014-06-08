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
		public String maxEnergy;
		public String listIDNeighbors;
		public int groupID;
		public boolean isStartPath = false;
		public boolean isEndPath = false;
		public boolean isMediatePath = false;
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

		public float getY() {
			return y;
		}

		public void setY(float y) {
			this.y = y;
		}

		public float getZ() {
			return z;
		}

		public void setZ(float z) {
			this.z = z;
		}

		public int getId() {
			return id;
		}

		public float getX() {
			return x;
		}

		public int getPacketId(){
			return this.packetId;
		}
		
		public boolean isStartPath() {
			return isStartPath;
		}

		public void setStartPath(boolean isStartPath) {
			this.isStartPath = isStartPath;
		}

		public boolean isEndPath() {
			return isEndPath;
		}

		public void setEndPath(boolean isEndPath) {
			this.isEndPath = isEndPath;
		}

		public boolean isMediatePath() {
			return isMediatePath;
		}

		public void setMediatePath(boolean isMediatePath) {
			this.isMediatePath = isMediatePath;
		}

		public void setPacketId(int packetId){
			this.packetId=packetId;
		}
		
		@Override
		public String toString() {
			return "NodeTrace [id=" + id + ", x=" + x + ", y=" + y + ", z=" + z
					+ ", packetId=" + packetId + ", time=" + time + ", energy="
					+ energy + ", listIDNeighbors=" + listIDNeighbors + "]";
		}
}
