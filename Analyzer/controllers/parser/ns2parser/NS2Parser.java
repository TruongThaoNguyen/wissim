package controllers.parser.ns2parser;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import models.Packet;
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
	
	
	
	@Deprecated
	public abstract Packet getPacketFromID(String packetId);
	
	@Deprecated
	public abstract String getmaxEnergyFromNodeID(String nodeID);
		
	@Deprecated	
	public abstract double getEnergyNodeDead();
	
	@Deprecated
	public abstract void setEnergyNodeDead(double e);
	
	@Deprecated
	public abstract String getLifeTime();
	
	@Deprecated
	public abstract LinkedHashMap<Integer,Double> getListNodeDead();
	
	
	/**
	 * Sort map by value
	 */
	@Deprecated
	public abstract  Map<Integer,Double> sortByValue(Map<Integer,Double> map);
	
	
	
	@Deprecated
	public static String resultofheader = "";
	
	@Deprecated
	public String getResultofheader() {
		return resultofheader;
	}
	
	@Deprecated
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
