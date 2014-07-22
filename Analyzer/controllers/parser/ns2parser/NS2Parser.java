package controllers.parser.ns2parser;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import models.Event;
import models.Packet;
import models.Node;
import controllers.parser.Parser;


public abstract class NS2Parser implements Parser {

	/**
	 * Read file Neighbors.tr to create nodes.
	 * @param mNodesTraceFile
	 */
	public abstract void parseNodes(String mNodesTraceFile);

	/**
	 * Read file Trace.tr to create events and packets.
	 * @param mFileTraceEvent
	 * @throws IOException
	 */
	public abstract void parseEvents(String mFileTraceEvent) throws IOException;
	
	public Packet getPacketFromID(String packetId) {
		// TODO Auto-generated method stub
		return null;
	}
	public String getmaxEnergyFromNodeID(String nodeID){
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
}
