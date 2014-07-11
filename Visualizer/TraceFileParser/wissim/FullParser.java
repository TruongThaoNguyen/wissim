package TraceFileParser.wissim;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import wissim.ui.MainViewPanel;

public class FullParser extends AbstractParser {
	public static FileOutputStream fout;
	public static OutputStreamWriter out;
	public static String sCurrentLine;
	public ArrayList<Packet> listPacket = new ArrayList<Packet>();
	public ArrayList<NodeTrace> listNodesWithNeighbors;
	public static ArrayList<Event> listEvents = new ArrayList<Event>();
	public static String listNeighbors;
	public static Event mEvent;
	public static HashMap<String, Integer> timeLine;
	public static Packet currentPacket = null;
	public static boolean isPacketParsed = false;
	public static String mFilePathNodes;
	public static String mFilePathEvent;
	public ArrayList<ArrayList<NodeEnergy>> listEnergy;
	public int numberNodeDead;
	public double energyNodeDead;
	public String lifeTime;
	public LinkedHashMap<Integer, Double> listNodeDead;
	public int[] countEnergy;
	public MainViewPanel mainPanel;

	public void setMainPanel(MainViewPanel mainPanel) {
		this.mainPanel = mainPanel;
	}

	public int getNumberNodeDead() {
		return this.numberNodeDead;
	}

	public void setNumberNodeDead(int n) {
		this.numberNodeDead = n;
	}

	public double getEnergyNodeDead() {
		return this.energyNodeDead;
	}

	public void setEnergyNodeDead(double e) {
		this.energyNodeDead = e;
	}

	public String getLifeTime() {
		return this.lifeTime;
	}

	public LinkedHashMap<Integer, Double> getListNodeDead() {
		return this.listNodeDead;
	}

	public void ConvertTraceFile(String filePathNodes, String filePathEvent)
			throws IOException {

		/**
		 * 
		 * Input : trace file path, xml file path Output : write data into xml
		 * file
		 */

		listPacket = new ArrayList<Packet>();
		// mFilePathNodes = "D://GR/GR3Material/Neighbors.txt";
		// mFilePathEvent = "D://GR/GR3Material/Trace_Energy.tr";
		listNodesWithNeighbors = new ArrayList<NodeTrace>();

		listEnergy = new ArrayList<ArrayList<NodeEnergy>>();

		mEvent = new Event();
		timeLine = new HashMap<>();
		listEvents = new ArrayList<Event>();

		parseNodes(filePathNodes);

		countEnergy = new int[getListNodes().size()];
		parseEvents(filePathEvent);
		mainPanel.onNotifyLoading(false);
	}

	/**
	 * Main for test, run directly
	 * 
	 * @param args
	 * @throws IOException
	 */
	/*
	 * public static void main(String[] args) throws IOException {
	 * 
	 * ConvertTraceFile();
	 * 
	 * 
	 * ArrayList<NodeEnergy> testList = new ArrayList<NodeEnergy>(); //NodeTrace
	 * testNode = listNodesWithNeighbors.get(1); //testList =
	 * getNodeEnergy(testNode); testList=listEnergy.get(7); for (NodeEnergy ne :
	 * testList) //System.out.println(ne.energy + " " + ne.time + " id= " +
	 * testNode.id); System.out.println(ne.energy + " " + ne.time ); }
	 */
	/**
	 * Nodes and NB parser
	 * 
	 * @throws IOException
	 */
	public  void parseNodes(String mNodesTraceFile) {

		try {
			String currentLine;
			BufferedReader br = new BufferedReader(new FileReader(
					mNodesTraceFile));
			// String dataPar[];
			System.out.println("Parsing nodes...");
			mainPanel.onNotifyLoading(true);
			while ((currentLine = br.readLine()) != null) {
				String[] retval = currentLine.split("\\s+");
				listNeighbors = "";
				if(retval.length > 3){
					String[] neighborsData = retval[3].split(",");
					for (int i = 0; i < neighborsData.length; i++) {
						listNeighbors += neighborsData[i] + " ";
					}
				}

			else{

			}



				NodeTrace nodeElement = new NodeTrace(
						Integer.parseInt(retval[0]),
						Float.parseFloat(retval[1]),
						Float.parseFloat(retval[2]), 0, "0", "200",
						listNeighbors);

				listNodesWithNeighbors.add(nodeElement);
				ArrayList<NodeEnergy> listEnergyOfNode = new ArrayList<NodeEnergy>();
				listEnergy.add(listEnergyOfNode);

			}
			br.close();
		} catch (Exception e) {

			e.printStackTrace();
		}

	}

	public void parseEvents(String mFileTraceEvent) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(mFileTraceEvent));
		String retval[];
		 int line = 0;
		System.out.println("Running...");

		while ((sCurrentLine = br.readLine()) != null) {
			sCurrentLine = Pattern.compile(" +").matcher(sCurrentLine.trim())
					.replaceAll(" ");
			// retval = sCurrentLine.trim().replaceAll(" +", " ").split(" ");
			retval = sCurrentLine.split(" ");
			/**
			 * Replace double space by one space
			 */

			if (retval[0].equals("N")) {
				setEnergyOfNode(retval[4], retval[2], retval[6]);
				// setLifeTime(retval[2], retval[6]);
				// if (!timeLine.containsKey(retval[2]))
				// timeLine.put(retval[2], line);

			} else {

				if (!retval[0].equals("M")) {
					if (!retval[13].equals("[energy")) {
						
						setEnergyOfNode(
								retval[2].substring(1, retval[2].length() - 1),
								retval[1], retval[13]);
						// setLifeTime(retval[1], retval[13]);
					}
				}

				/**
				 * parse event
				 */

				if (retval[6].length() > 0
						&& !retval[6].equals("HELLO")
						&& !retval[6].equals("CONVEXHULL")
						&& retval.length > 10) {

					Event event = null;

					event = new Event(convertType(retval[0]), retval[1], "",
							"0",
							retval[2].substring(1, retval[2].length() - 1),
							retval[13], retval[5], "0", retval[26].substring(0,
									retval[26].length() - 1), "", "", "");
					event.packetType = retval[3];

					listEvents.add(event);
					setEnergyOfNode(event.sourceId, event.time, retval[13]);
					if (retval[0].equals("s") && !retval[3].equals("AGT")) {
						Packet newpacket = new Packet(retval[5], retval[6],
								retval[2].substring(1, retval[2].length() - 1),
								"0", retval[26].substring(0,
										retval[26].length() - 1).replaceAll(
										"[^\\d.]", ""), retval[7], retval[7],
								retval[1], retval[1]);
						newpacket.listNode = new ArrayList<NodeTrace>();
						newpacket.listNode.add(new NodeTrace(Integer
								.parseInt(retval[2].substring(1,
										retval[2].length() - 1)), retval[1]));
						newpacket.isSuccess = true;
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
										.substring(0, retval[26].length() - 1)
										.replaceAll("[^\\d.]", "");
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
										.substring(0, retval[26].length() - 1)
										.replaceAll("[^\\d.]", "");
								listPacket.get(i).isSuccess = false;
								break;
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
										.substring(0, retval[26].length() - 1)
										.replaceAll("[^\\d.]", "");
								listPacket.get(i).isSuccess = true;
							}
						}
					}

				}

			}

		}
		br.close();

	}

	/**
	 * 
	 * @param node
	 * @return ArrayList<NodeEnergy>
	 * @throws IOException
	 */

	public static ArrayList<NodeEnergy> getNodeEnergy(NodeTrace node,
			String time) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(mFilePathEvent));
		String retval[];
		NodeEnergy nE = new NodeEnergy("", "", "0");
		ArrayList<NodeEnergy> listNE = new ArrayList<>();
		while ((sCurrentLine = br.readLine()) != null) {
			retval = sCurrentLine.split(" ");
			if (retval[4].equals(node.id + "")) {
				nE = new NodeEnergy(retval[4], retval[2], retval[6]);
				listNE.add(nE);
			}
			if (retval[2].substring(1, retval[2].length() - 1).equals(
					node.id + "")
					&& !retval[0].equals("M")) {
				if (!retval[13].equals("[energy"))
					nE = new NodeEnergy(retval[2].substring(1,
							retval[2].length() - 1), retval[1], retval[13]);
				else
					nE = new NodeEnergy(retval[2].substring(1,
							retval[2].length() - 1), retval[1], retval[14]);
				listNE.add(nE);
			}

		}
		br.close();
		if (listNE.size() == 0)
			return null;
		else {
			if (time.equals(""))
				return listNE;
			else {
			}
		}
		return listNE;
	}

	public void setEnergyOfNode(String nodeID, String time, String energy)
			throws IOException {
		
		if (countEnergy[Integer.parseInt(nodeID)] == 0) {
			System.out.println("==0");
			listNodesWithNeighbors.get(Integer.parseInt(nodeID)).maxEnergy = energy;
		}
		if (++countEnergy[Integer.parseInt(nodeID)] % 50 == 1) {
			NodeEnergy nE = new NodeEnergy(nodeID, time, energy);
			listEnergy.get(Integer.parseInt(nodeID)).add(nE);
		}

	}

	public ArrayList<ArrayList<NodeEnergy>> getListEnergy() {
		return listEnergy;
	}

	/* Sort map by value */
	public Map<Integer, Double> sortByValue(Map<Integer, Double> map) {
		List<Map.Entry<Integer, Double>> entries = new ArrayList<Map.Entry<Integer, Double>>(
				map.entrySet());
		Collections.sort(entries, new Comparator<Map.Entry<Integer, Double>>() {
			public int compare(Map.Entry<Integer, Double> a,
					Map.Entry<Integer, Double> b) {
				return a.getValue().compareTo(b.getValue());
			}
		});
		Map<Integer, Double> sortedMap = new LinkedHashMap<Integer, Double>();
		for (Map.Entry<Integer, Double> entry : entries) {
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
	}

	public void setNetworkLifeTime() {
		listNodeDead = new LinkedHashMap<Integer, Double>();
		ArrayList<NodeEnergy> listNodeEnergy = new ArrayList<NodeEnergy>();

		for (int i = 0; i < listEnergy.size(); i++) {
			listNodeEnergy = listEnergy.get(i);

			for (int j = 0; j < listNodeEnergy.size(); j++) {
				NodeEnergy node = listNodeEnergy.get(j);
				if (Double.parseDouble(node.getEnergy()) <= energyNodeDead) {
					listNodeDead.put(i, Double.parseDouble(node.getTime()));
					break;
				}
			}

		}
		listNodeDead = (LinkedHashMap<Integer, Double>) sortByValue(listNodeDead);

	}

	/*
	 * public static void setLifeTime(String time,String energy){
	 * 
	 * }
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

	public ArrayList<Packet> getListPacket() {
		return listPacket;
	}

	public void setListPacket(ArrayList<Packet> listPacket) {
		this.listPacket = listPacket;
	}

	public ArrayList<NodeTrace> getListNodes() {
		return listNodesWithNeighbors;
	}

	public void setListNodes(ArrayList<NodeTrace> listNodesWithNeighbors) {
		this.listNodesWithNeighbors = listNodesWithNeighbors;
	}

	public ArrayList<Event> getListEvents() {
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

	public Packet getPacketFromID(String packetID) {
		if (listPacket != null && listPacket.size() > 0) {
			for (Packet p : listPacket) {
				if (p.getId().equals(packetID)) {
					return p;
				}
			}
		}

		return null;
	}

	public String getmaxEnergyFromNodeID(String nodeID) {
		if (listNodesWithNeighbors.size() > 0) {
			for (int i = 0; i < listNodesWithNeighbors.size(); i++) {
				if (listNodesWithNeighbors.get(i).id == Integer
						.parseInt(nodeID))
					return listNodesWithNeighbors.get(i).maxEnergy;

			}
		}
		return "";
	}

	public void onNotifyLoadingProcess(MainViewPanel mainPanel,
			boolean isLoading) {
		if (mainPanel != null) {
			mainPanel.onNotifyLoading(isLoading);
		}
	}

}