package TraceFileParser.wissim;

import java.awt.Point;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.text.DefaultEditorKit.CutAction;

public class TraceFile {
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

	public static void ConvertTraceFile(String mFileTraceNodes,
			String mFileTraceEvent) throws IOException {

		/**
		 * 
		 * Input : trace file path, xml file path Output : write data into xml
		 * file
		 */

		listPacket = new ArrayList<Packet>();
		mFilePathNodes = mFileTraceNodes;
		mFilePathEvent = mFileTraceEvent;
		listNodesWithNeighbors = new ArrayList<NodeTrace>();
		listNodesWithNeighbors = new ArrayList<NodeTrace>();
		mEvent = new Event();
		timeLine = new HashMap<>();
		listEvents = new ArrayList<Event>();
		parseNodes(mFileTraceNodes);
		parseEvents(mFileTraceEvent);

		System.out.println("Finish,packet size=" + getListPacket().size());
	}

	/**
	 * Main for test, run directly
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		ConvertTraceFile("D://GR/GR3Material/Neighbors.txt",
				"D://GR/GR3Material/Trace_Energy.tr");

		/**
		 * Test energy
		 */
		ArrayList<NodeEnergy> testList = new ArrayList<NodeEnergy>();
		NodeTrace testNode = listNodesWithNeighbors.get(1);
		testList = getNodeEnergy(testNode);

		System.out.println("size of list energy node " + testList.size());

	}

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
			String dataPar[];
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
			}

		} catch (Exception e) {
			System.out.println("catch Exception :(( Message=" + e.getMessage());
			e.printStackTrace();
		}

	}

	public static void parseEvents(String mFileTraceEvent) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(mFileTraceEvent));
		String retval[];
		NodeTrace node = null;
		Packet packet = null;
		int line = 0;
		boolean inArray = false;
		System.out.println("Running...");
		while ((sCurrentLine = br.readLine()) != null) {

			retval = sCurrentLine.split(" ");
			line++;
			inArray = false;
			if (retval[0].equals("N")) {
				if (!timeLine.containsKey(retval[2]))

					timeLine.put(retval[2], line);
			} else {
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

					if (retval[0].equals("s") && retval[3].equals("AGT")) {

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
		System.out.println("Finishing...listEventSize= " + listEvents.size());

	}

	/**
	 * 
	 * @param node
	 * @return ArrayList<NodeEnergy>
	 * @throws IOException
	 */
	public static ArrayList<NodeEnergy> getNodeEnergy(NodeTrace node)
			throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(mFilePathEvent));
		String retval[];
		NodeEnergy nE = new NodeEnergy("", "0");
		int line = 0;
		ArrayList<NodeEnergy> listNE = new ArrayList<>();
		while ((sCurrentLine = br.readLine()) != null) {
			retval = sCurrentLine.split(" ");
			line++;
			if (retval[4].equals(node.id + "") && retval[0].equals("N")) {
				nE = new NodeEnergy(retval[2], retval[6]);
				listNE.add(nE);
			} else if (retval[2].substring(1, retval[2].length() - 1).equals(
					node.id + "") && !retval[0].equals("M")) {

				if (!(retval[13].equals("[energy")))
				{	
					nE = new NodeEnergy(retval[1], retval[13]);
				}
				else
				{
					nE = new NodeEnergy(retval[1], retval[14]);
				listNE.add(nE);
				}
				
			}

		}
		if (listNE.size() == 0)
			return null;
		else
			return listNE;
	}

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
		TraceFile.listPacket = listPacket;
	}

	public static ArrayList<NodeTrace> getListNodes() {
		return listNodesWithNeighbors;
	}

	public static void setListNodes(ArrayList<NodeTrace> listNodesWithNeighbors) {
		TraceFile.listNodesWithNeighbors = listNodesWithNeighbors;
	}

	public static ArrayList<Event> getListEvents() {
		return listEvents;
	}

	public static void setListEvents(ArrayList<Event> listEvents) {
		TraceFile.listEvents = listEvents;
	}

}
