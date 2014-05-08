package parser;


import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;



public class FullParser2 {
	public static FileOutputStream fout;
	public static OutputStreamWriter out;
	public static String sCurrentLine;
	public static ArrayList<Packet> listPacket;
	public static ArrayList<NodeTrace> listNodesWithNeighbors;
	public static ArrayList<Event> listEvents;
	public static String listNeighbors;
	public static Event mEvent;
	public static HashMap<String, Integer> timeLine;
	public static Packet currentPacket = null;
	public static boolean isPacketParsed = false;
	public static String mFilePathNodes;
	public static String mFilePathEvent;
	public static ArrayList<ArrayList<NodeEnergy>> listEnergy;
	public static int numberNodeDead;
	public static double energyNodeDead;
	public static String lifeTime;
	public static Map<Integer,Double> listNodeDead;
	public static int[] countEnergy;

	public static void ConvertTraceFile(String mFilePathNodes,String mFilePathEvent) throws IOException {

		/**
		 * 
		 * Input : trace file path, xml file path Output : write data into xml
		 * file
		 */

		listPacket = new ArrayList<Packet>();
		//mFilePathNodes = "Neighbors.txt";
		//mFilePathEvent = "Trace_Energy.tr";
		listNodesWithNeighbors = new ArrayList<NodeTrace>();
		listNodesWithNeighbors = new ArrayList<NodeTrace>();
		listEnergy= new ArrayList<ArrayList<NodeEnergy>>();
		
		mEvent = new Event();
		timeLine = new HashMap<>();
		listEvents = new ArrayList<Event>();
		
		parseNodes(mFilePathNodes);
		countEnergy=new int[getListNodes().size()];
		parseEvents(mFilePathEvent);

		//System.out.println("Finish,packet size=" + getListPacket().size());
	}

	/**
	 * Main for test, run directly
	 * 
	 * @param args
	 * @throws IOException
	 */
	/*
	public static void main(String[] args) throws IOException {

		ConvertTraceFile();

		
		ArrayList<NodeEnergy> testList = new ArrayList<NodeEnergy>();
		//NodeTrace testNode = listNodesWithNeighbors.get(1);
		//testList = getNodeEnergy(testNode);
		testList=listEnergy.get(7);
		for (NodeEnergy ne : testList)
			//System.out.println(ne.energy + " " + ne.time + " id= " + testNode.id);
			System.out.println(ne.energy + " " + ne.time );
			
		energyNodeDead = 999;
		int No=1;
		setNetworkLifeTime();
		 for(Integer i : listNodeDead.keySet()){
			 System.out.print(No++);
			 System.out.print("  "+i);
			 System.out.print("  "+listNodeDead.get(i));
			 System.out.println();
		 }
	}
	 */
	/**
	 * Nodes and NB parser
	 * 
	 * @throws IOException
	 */
	public static void parseNodes(String mNodesTraceFile) {

		try {
			String currentLine;
			BufferedReader br = new BufferedReader(new FileReader(
					mNodesTraceFile));
			//String dataPar[];
			System.out.println("Parsing nodes...");

			while ((currentLine = br.readLine()) != null) {
				String[] retval = currentLine.split("\\s+");

				String[] neighborsData = retval[3].split(",");

				listNeighbors = "";
				for (int i = 0; i < neighborsData.length; i++) {
					listNeighbors += neighborsData[i] + " ";
				}

				NodeTrace nodeElement = new NodeTrace(
						Integer.parseInt(retval[0]),
						Float.parseFloat(retval[1]),
						Float.parseFloat(retval[2]), 0, "0", "200",
						listNeighbors);

				listNodesWithNeighbors.add(nodeElement);
				ArrayList<NodeEnergy> listEnergyOfNode=new ArrayList<NodeEnergy>();
				listEnergy.add(listEnergyOfNode);
				
			}
			br.close();
		} catch (Exception e) {
			System.out.println("catch Exception :(( Message=" + e.getMessage());
			e.printStackTrace();
		}

	}

	public static void parseEvents(String mFileTraceEvent) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(mFileTraceEvent));
		String retval[];
		//int line = 0;
		System.out.println("Running...");
		while ((sCurrentLine = br.readLine()) != null) {

			retval = sCurrentLine.split(" ");
			//line++;
			if (retval[0].equals("N")) {
				setEnergyOfNode(retval[4], retval[2], retval[6]);
				//setLifeTime(retval[2], retval[6]);
			//	if (!timeLine.containsKey(retval[2]))
			//		timeLine.put(retval[2], line);
						
			} else {
				
				if(!retval[0].equals("M")){
					if (!retval[13].equals("[energy")){
						setEnergyOfNode(retval[2].substring(1, retval[2].length() - 1),retval[1], retval[13]);
						//setLifeTime(retval[1], retval[13]);
					}
					else{
						setEnergyOfNode(retval[2].substring(1, retval[2].length() - 1),retval[1], retval[14]);
						//setLifeTime(retval[1], retval[14]);
					}
				}
				
				/**
				 * parse event
				 */
				if (retval[7].equals("cbr")) {
					Event event = null;

					event = new Event(convertType(retval[0]), retval[1], "",
							"0",
							retval[2].substring(1, retval[2].length() - 1),
							retval[14], retval[6], "0", retval[27].substring(0,
									retval[27].length() - 1), "", "", "");
					event.packetType = retval[3];

					listEvents.add(event);

					if (retval[0].equals("s") && retval[3].equals("RTR")) {

						Packet newpacket = new Packet(retval[6], "cbr",
								retval[2].substring(1, retval[2].length() - 1),
								"0", retval[27].substring(0,
										retval[27].length() - 1), "0",
								retval[8], retval[1], retval[1]);
						newpacket.listNode = new ArrayList<NodeTrace>();

						listPacket.add(newpacket);
					}
					if (retval[0].equals("r") && retval[3].equals("RTR")) {
						for (int i = 0; i < listPacket.size(); i++) {
							if (listPacket.get(i).id.equals(retval[6])) {
								listPacket.get(i).listNode.add(new NodeTrace(
										Integer.parseInt(retval[2].substring(1,
												retval[2].length() - 1)),
										retval[1]));
								listPacket.get(i).endTime = retval[1];
								listPacket.get(i).destID = retval[27]
										.substring(0, retval[27].length());
								break;
							}

						}
					}
					if (retval[0].equals("D") && retval[3].equals("RTR")) {
						for (int i = 0; i < listPacket.size(); i++) {
							if (listPacket.get(i).id.equals(retval[6])) {
								listPacket.get(i).listNode.add(new NodeTrace(
										Integer.parseInt(retval[2].substring(1,
												retval[2].length() - 1)),
										retval[1]));
								listPacket.get(i).endTime = retval[1];
								listPacket.get(i).destID = retval[27]
										.substring(0, retval[27].length() - 1);
								listPacket.get(i).isSuccess = false;
							}
						}
					}
					if (retval[0].equals("r") && retval[3].equals("AGT")) {
						for (int i = 0; i < listPacket.size(); i++) {
							if (listPacket.get(i).id.equals(retval[6])) {

								listPacket.get(i).listNode.add(new NodeTrace(
										Integer.parseInt(retval[2].substring(1,
												retval[2].length() - 1)),
										retval[1]));
								listPacket.get(i).endTime = retval[1];
								listPacket.get(i).destID = retval[27]
										.substring(0, retval[27].length() - 1);
								listPacket.get(i).isSuccess = true;
							}
						}
					}

				} else if (retval[6].equals("cbr")) {
					Event event = null;

					event = new Event(convertType(retval[0]), retval[1], "",
							"0",
							retval[2].substring(1, retval[2].length() - 1),
							retval[13], retval[5], "0", retval[26].substring(0,
									retval[26].length() - 1), "", "", "");
					event.packetType = retval[3];
					listEvents.add(event);

					if (retval[0].equals("s") && retval[3].equals("AGT")) {
						Packet newpacket = new Packet(retval[5], "cbr",
								retval[2].substring(1, retval[2].length() - 1),
								"0", retval[26].substring(0,
										retval[26].length() - 1), "0",
								retval[7], retval[1], retval[1]);
						newpacket.listNode = new ArrayList<NodeTrace>();
						listPacket.add(newpacket);
					}
					if (retval[0].equals("r") && retval[3].equals("RTR")) {
						for (int i = 0; i < listPacket.size(); i++) {
							if (listPacket.get(i).id.equals(retval[5])) {
								listPacket.get(i).listNode.add(new NodeTrace(
										Integer.parseInt(retval[2].substring(1,
												retval[2].length() - 1)),
										retval[1]));
								listPacket.get(i).endTime = retval[1];
								listPacket.get(i).destID = retval[26]
										.substring(0, retval[26].length() - 1);
								break;
							}

						}
					}

					if (retval[0].equals("D") && retval[3].equals("RTR")) {
						for (int i = 0; i < listPacket.size(); i++) {
							if (listPacket.get(i).id.equals(retval[5])) {
								listPacket.get(i).listNode.add(new NodeTrace(
										Integer.parseInt(retval[2].substring(1,
												retval[2].length() - 1)),
										retval[1]));
								listPacket.get(i).endTime = retval[1];
								listPacket.get(i).destID = retval[26]
										.substring(0, retval[26].length() - 1);
								listPacket.get(i).isSuccess = false;
							}
						}
					}
					if (retval[0].equals("r") && retval[3].equals("AGT")) {
						for (int i = 0; i < listPacket.size(); i++) {
							if (listPacket.get(i).id.equals(retval[5])) {

								listPacket.get(i).listNode.add(new NodeTrace(
										Integer.parseInt(retval[2].substring(1,
												retval[2].length() - 1)),
										retval[1]));
								listPacket.get(i).endTime = retval[1];
								listPacket.get(i).destID = retval[26]
										.substring(0, retval[26].length() - 1);
								listPacket.get(i).isSuccess = true;
							}
						}
					}

				}

				/**
				 * BOUNDHOLE
				 */
				else if (retval[7].equals("BOUNDHOLE")) {
					Event event = null;
					event = new Event(convertType(retval[0]), retval[1], "",
							"0",
							retval[2].substring(1, retval[2].length() - 1),
							retval[14], retval[6], "0", retval[27].substring(0,
									retval[27].length() - 1), "", "", "");
					event.packetType = retval[3];

				} else if (retval[6].equals("BOUNDHOLE")) {
					Event event = null;
					event = new Event(convertType(retval[0]), retval[1], "",
							"0",
							retval[2].substring(1, retval[2].length() - 1),
							retval[13], retval[5], "0", retval[26].substring(0,
									retval[26].length() - 1), "", "", "");
					event.packetType = retval[3];
				}

			}

		}
		//System.out.println("Finishing...listEventSize= " + listEvents.size());
		br.close();
	}

	/**
	 * 
	 * @param node
	 * @return ArrayList<NodeEnergy>
	 * @throws IOException
	 */
	
	public static ArrayList<NodeEnergy> getNodeEnergy(NodeTrace node) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(mFilePathEvent));
		String retval[];
		NodeEnergy nE = new NodeEnergy("","", "0");
		ArrayList<NodeEnergy> listNE = new ArrayList<>();
		while ((sCurrentLine = br.readLine()) != null) {
			retval = sCurrentLine.split(" ");
			if (retval[4].equals(node.id + "")) {
				nE = new NodeEnergy(retval[4],retval[2], retval[6]);
				listNE.add(nE);
			}  
			if (retval[2].substring(1, retval[2].length() - 1).equals(node.id + "") && !retval[0].equals("M") ) {
				if (!retval[13].equals("[energy"))
					nE = new NodeEnergy(retval[2].substring(1, retval[2].length() - 1),retval[1], retval[13]);
				else
					nE = new NodeEnergy(retval[2].substring(1, retval[2].length() - 1),retval[1], retval[14]);
				listNE.add(nE);
			}

		}
		br.close();
		if (listNE.size() == 0)
			return null;
		else
			return listNE;
	}
	 
	
	public static void setEnergyOfNode(String nodeID,String time,String energy)	throws IOException {
		//ArrayList<NodeEnergy> listEnergyOfNode=new ArrayList<NodeEnergy>();
		if(++countEnergy[Integer.parseInt(nodeID)] % 50 ==1){
			NodeEnergy nE = new NodeEnergy(nodeID,time,energy);
			listEnergy.get(Integer.parseInt(nodeID)).add(nE);
		}
		
		/*NodeEnergy nE = new NodeEnergy(time,energy);
		if(listEnergy.get(Integer.parseInt(nodeID)).size()==0 || listEnergy.get(Integer.parseInt(nodeID)).size()==1)
			listEnergy.get(Integer.parseInt(nodeID)).add(nE);
		else{
			listEnergy.get(Integer.parseInt(nodeID)).remove(1);
			listEnergy.get(Integer.parseInt(nodeID)).add(nE);
		}*/
	}
	
	/*Sort map by value*/
	public static Map<Integer,Double> sortByValue(Map<Integer,Double> map) {
		List<Map.Entry<Integer,Double>> entries = new ArrayList<Map.Entry<Integer,Double>>(map.entrySet());
		Collections.sort(entries, new Comparator<Map.Entry<Integer , Double>>() {
				  public int compare(Map.Entry<Integer,Double> a, Map.Entry<Integer,Double> b){
				    return a.getValue().compareTo(b.getValue());
				  }
		});
		Map<Integer,Double> sortedMap = new LinkedHashMap<Integer,Double>();
			for (Map.Entry<Integer,Double> entry : entries) {
				  sortedMap.put(entry.getKey(), entry.getValue());
			}
		return 	sortedMap;	
	} 
	
	public static void setNetworkLifeTime() {
		listNodeDead = new LinkedHashMap<Integer,Double>();
		ArrayList<NodeEnergy> listNodeEnergy= new ArrayList<NodeEnergy>();
		
		for(int i = 0 ;i < listEnergy.size(); i++) {
			listNodeEnergy = listEnergy.get(i);
			
			for(int j = 0;j < listNodeEnergy.size(); j++ ) {
				NodeEnergy node=listNodeEnergy.get(j);
				if(Double.parseDouble(node.getEnergy()) <= energyNodeDead) {
					listNodeDead.put(i,Double.parseDouble(node.getTime()));
					break;
				}
			}  
			
		}
		listNodeDead =sortByValue(listNodeDead);
		
	}
	
	/*
	public static void setNetworkLifeTime() throws  IOException {
		listNodeDead = new LinkedHashMap<String,String>();
		lifeTime="Not die";
		BufferedReader br = new BufferedReader(new FileReader(mFilePathEvent));
		String retval[];
		int countNodeDead=0;
		double energyOfNode;
		while ((sCurrentLine = br.readLine()) != null) {
			retval = sCurrentLine.split(" ");
			
			if (retval[0].equals("N")  ) {
				if(!listNodeDead.containsKey(retval[4]) && (Double.parseDouble(retval[6]) <= energyNodeDead) ){
					listNodeDead.put(retval[4],retval[2]);
					countNodeDead++;
					if(countNodeDead==numberNodeDead){
						lifeTime=retval[2];
						break;
					}
				}
			}  
			else
				if ( !retval[0].equals("M") && !listNodeDead.containsKey(retval[2].substring(1, retval[2].length() - 1)) ) {
					if (!retval[13].equals("[energy"))
						energyOfNode=Double.parseDouble(retval[13]);
					else
						energyOfNode=Double.parseDouble(retval[14]);
					
					if(energyOfNode <= energyNodeDead){
						listNodeDead.put(retval[2].substring(1, retval[2].length() - 1),retval[1]);
						countNodeDead++;
						if(countNodeDead==numberNodeDead){
							lifeTime=retval[1];
							break;
						}
					}
				}

		}
		br.close();
	}
	*/
	/*
	public static void setLifeTime(String time,String energy){
		
	}
	*/
	public static String convertType(String type) {
		switch (type) {
		case "s":
			return "send";
		case "r":
			return "receive";
		case "f":
			return "forward";
		case "D":
			return "Drop";
		default:
			return "";
		}
	}

	public static ArrayList<Packet> getListPacket() {
		return listPacket;
	}

	public static void setListPacket(ArrayList<Packet> listPacket) {
		FullParser2.listPacket = listPacket;
	}

	public static ArrayList<NodeTrace> getListNodes() {
		return listNodesWithNeighbors;
	}

	public static void setListNodes(ArrayList<NodeTrace> listNodesWithNeighbors) {
		FullParser2.listNodesWithNeighbors = listNodesWithNeighbors;
	}

	public static ArrayList<Event> getListEvents() {
		return listEvents;
	}

	public static void setListEvents(ArrayList<Event> listEvents) {
		FullParser.listEvents = listEvents;
	}
	public static String getTimesFromEvent(int eventCount) {
		if (listEvents != null && eventCount < listEvents.size()) {
			return listEvents.get(eventCount).time;
		} else {
			return null;
		}
	}

	public static Packet getPacketFromID(String packetID) {
		if (listPacket != null && listPacket.size() > 0) {
			for (Packet p : listPacket) {
				if (p.getId().equals(packetID)) {
					return p;
				}
			}
		}

		return null;
	}
}
