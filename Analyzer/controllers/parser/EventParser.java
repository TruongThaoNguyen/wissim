package controllers.parser;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;

import models.Event;
import models.NodeTrace;
import models.Packet;

public class EventParser extends AbstractParser {
	public static FileOutputStream fout;
	public static OutputStreamWriter out;
	public static String sCurrentLine;
	public  ArrayList<Packet> listPacket = new ArrayList<Packet>();
	public  ArrayList<NodeTrace> listNodesWithNeighbors;
	public static ArrayList<Event> listEvents = new ArrayList<Event>();
	public static String listNeighbors;
	public static Event mEvent;
	public static HashMap<String, Integer> timeLine;
	public static Packet currentPacket = null;
	public static boolean isPacketParsed = false;

	public static String mFilePathNodes = "D://GR/GR3Material/GR3TestFile/T/gridonline/Neighbors.txt";

	public static String mFilePathEvent = "D://GR/GR3Material/GR3TestFile/T/gridonline/Trace.tr";

	public  ArrayList<Packet> getListPacket() {
		return listPacket;
	}

	public  void setListPacket(ArrayList<Packet> listPacket) {
		this.listPacket = listPacket;
	}

	public ArrayList<Event> getListEvents() {
		return listEvents;
	}

	public static void setListEvents(ArrayList<Event> listEvents) {
		EventParser.listEvents = listEvents;
	}

	@Override
	public void ConvertTraceFile(String filePathNodes, String filePathEvent)
			throws IOException {
		// TODO Auto-generated method stub
		listPacket = new ArrayList<Packet>();
		listNodesWithNeighbors = new ArrayList<NodeTrace>();

		mEvent = new Event();
		listEvents = new ArrayList<Event>();

		parseNodes(filePathNodes);
		parseEvents(filePathEvent);
	}

	public  ArrayList<NodeTrace> getListNodes() {
		return listNodesWithNeighbors;
	}

	@Override
	public void parseNodes(String mNodesTraceFile) {
		// TODO Auto-generated method stub

		try {
			String currentLine;
			BufferedReader br = new BufferedReader(new FileReader(
					mNodesTraceFile));
			System.out.println("Parsing nodes...");

			while ((currentLine = br.readLine()) != null) {
				String[] retval = currentLine.split("\\s+");

//				String[] neighborsData = retval[3].split(",");
//
//				listNeighbors = "";
//				for (int i = 0; i < neighborsData.length; i++) {
//					listNeighbors += neighborsData[i] + " ";
//				}

				NodeTrace nodeElement = new NodeTrace(
						Integer.parseInt(retval[0]),
						Float.parseFloat(retval[1]),
						Float.parseFloat(retval[2]), 0, "0", "200",
						listNeighbors);

				listNodesWithNeighbors.add(nodeElement);

			}
			br.close();
		} catch (Exception e) {
			System.out.println("catch Exception :(( Message=" + e.getMessage());
			e.printStackTrace();
		}

	}

	@Override
	public void parseEvents(String mFileTraceEvent) throws IOException {
		// TODO Auto-generated method stub
		BufferedReader br = new BufferedReader(new FileReader(mFileTraceEvent));
		String retval[];
		// int line = 0;
		System.out.println("Running...");
		while ((sCurrentLine = br.readLine()) != null) {
			sCurrentLine = sCurrentLine.trim().replaceAll(" +", " ");
			retval = sCurrentLine.split(" ");
			if(retval[0].equals("M") == false && retval[6].equals("HELLO") == false ){
			switch (retval[0]) {
			case "s":
				if (retval[3].equals("RTR")) {
					/**
					 * read packet , checking if packet is broadcast or single path
					 */
					Packet newPacket = new Packet(retval[5], retval[6],
							retval[2].substring(1, retval[2].length() - 1),
							"255", retval[16].substring(0,
									retval[16].length() - 1), "255", retval[7],
							retval[1], retval[1]);
					newPacket.listNode = new ArrayList<NodeTrace>();
					newPacket.isSuccess = true;
					listPacket.add(newPacket);
					/**
					 * read event data
					 */
					Event event = new Event(convertType(retval[0]), retval[1],
							"", "255", retval[2].substring(1,
									retval[2].length() - 1), "", retval[5], "",
							retval[16].substring(0, retval[16].length() - 1),
							"", "", retval[2].substring(1,
									retval[2].length() - 1));
					listEvents.add(event);

				}
				break;
			case "r":
				if (retval[3].equals("RTR")) {

					for (int i = 0; i < listPacket.size(); i++) {
						if (listPacket.get(i).id.equals(retval[5])) {
							listPacket.get(i).listNode
									.add(new NodeTrace(Integer
											.parseInt(retval[2].substring(1,
													retval[2].length() - 1)),
											retval[1]));
							listPacket.get(i).endTime = retval[1];
							listPacket.get(i).destID = retval[16].substring(0,
									retval[16].length() - 1);
							break;
						}

					}
					Event event = new Event(convertType(retval[0]), retval[1],
							"", "255", retval[2].substring(1,
									retval[2].length() - 1), "", retval[5], "",
							retval[16].substring(0, retval[16].length() - 1),
							"", "", retval[2].substring(1,
									retval[2].length() - 1));
					listEvents.add(event);

				}
				break;
			case "f":
				/**
				 * read packet data
				 */

				/**
				 * read event data
				 */
				Event event = new Event(convertType(retval[0]), retval[1], "",
						"255", retval[2].substring(1, retval[2].length() - 1),
						"", retval[5], "", retval[16].substring(0,
								retval[16].length() - 1), "", "",
						retval[2].substring(1, retval[2].length() - 1));
				listEvents.add(event);
				break;

			case "D":
				/**
				 * read packet data
				 */
				for (int i = 0; i < listPacket.size(); i++) {
					if (listPacket.get(i).id.equals(retval[5])) {
						listPacket.get(i).listNode.add(new NodeTrace(Integer
								.parseInt(retval[2].substring(1,
										retval[2].length() - 1)), retval[1]));
						listPacket.get(i).endTime = retval[1];
						listPacket.get(i).destID = retval[16].substring(0,
								retval[16].length() - 1);
						listPacket.get(i).isSuccess = false;

					}
				}
				/**
				 * read event data
				 */
				Event e = new Event(convertType(retval[0]), retval[1], "",
						"255", retval[2].substring(1, retval[2].length() - 1),
						"", retval[5], "", retval[16].substring(0,
								retval[16].length() - 1), "", "",
						retval[2].substring(1, retval[2].length() - 1));
				listEvents.add(e);
				break;

			}
			}
			/* broadcast packet*/
			if(retval[6].equals("HELLO")){
				if (retval[0].equals("s")){
					Packet newpacket = new Packet(retval[5], retval[6],
							retval[2].substring(1, retval[2].length() - 1),
							"0", "-1", "255",
							retval[7], retval[1], retval[1]);
					newpacket.listNode = new ArrayList<NodeTrace>();
					
					listPacket.add(newpacket);
				}
				if (retval[0].equals("D")) {
					for (int i = 0; i < listPacket.size(); i++) {
						if (listPacket.get(i).id.equals(retval[5])) {
							listPacket.get(i).listNode.add(new NodeTrace(
									Integer.parseInt(retval[2].substring(1,
											retval[2].length() - 1)),
									retval[1]));								
							listPacket.get(i).isSuccess = false;
						}
					}
				}
				
			}
		}
		br.close();
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
	public static void main(String args[]) throws IOException {
		EventParser ep = new EventParser();
		ep.ConvertTraceFile(mFilePathNodes, mFilePathEvent);
		
	}
}
