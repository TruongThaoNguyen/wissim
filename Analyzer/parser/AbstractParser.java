package parser;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public abstract class AbstractParser implements IFileParser {
		
	public static String resultofheader = "";
	public static String getHeaderFileParser(String mFilePathEvents) throws IOException {
		/**
		 * get the header of trace file to know if file contains energy or not
		 */
		
		BufferedReader br = new BufferedReader(new FileReader(mFilePathEvents));
		String currentline = "";
		String retval[];
		String result = "N";
		while((currentline = br.readLine())!= null){
			retval = currentline.split(" ");
			if(retval[0].equals("N"))
			{
				result = "Y"; // contain energy
				resultofheader = result;
				br.close();
				return result;
			}
		}
		resultofheader = result;
		br.close();
		return result;
	}

	public ArrayList<Event> getListEvents() {
		// TODO Auto-generated method stub
		return null;
	}

	public ArrayList<NodeTrace> getListNodes() {
		// TODO Auto-generated method stub
		return null;
	}

	public ArrayList<Packet> getListPacket() {
		// TODO Auto-generated method stub
		return null;
	}

	public Packet getPacketFromID(String packetId) {
		// TODO Auto-generated method stub
		return null;
	}
	public String getmaxEnergyFromNodeID(String nodeID){
		return null;
	}
	public ArrayList<ArrayList<NodeEnergy>> getListEnergy() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public int getNumberNodeDead(){
		return 0;
	}
	public void setNumberNodeDead(int n){
		
	}
	public double getEnergyNodeDead(){
		return 0;
	}
	public void setEnergyNodeDead(double e){
		// TODO Auto-generated method stub
	}
	public String getLifeTime(){
		return null;
	}
	public LinkedHashMap<Integer,Double> getListNodeDead(){
		return null;
	}
	public void setNetworkLifeTime() {
		// TODO Auto-generated method stub
	}
	/*Sort map by value*/
	public  Map<Integer,Double> sortByValue(Map<Integer,Double> map) {
		return null;
	} 
	public String getResultofheader() {
		return resultofheader;
	}

}
