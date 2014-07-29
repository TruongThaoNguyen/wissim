package controllers.parser.ns2;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import models.Event;
import models.Node;
import models.Packet;
import controllers.Configure;
import controllers.parser.Parser;

/**
 * Paser for NS2 trace files.
 * @author Trongnguyen
 *
 */
public class NS2Parser implements Parser {
	
	/**
	 * node list. hash by id.
	 */
	private AbstractMap<Integer, Node> node = new LinkedHashMap<>();
	
	/**
	 * packet list. hash by id.
	 */
	private AbstractMap<Integer, Packet> packet = new LinkedHashMap<>(); 
	
	/**
	 * event list.
	 */
	private List<Event> event = new ArrayList<Event>();
	
	/**
	 * trace file with node's information.
	 * default: Neighbor.tr
	 */
	private String filePathNode;
	
	/**
	 * trace file with event's information.
	 * default: Trace.tr
	 */
	private String filePathEvent;

	/**
	 * create new NS2 parer.
	 */
	public NS2Parser() {							
//		new Thread() {
//			public void run() {

//				// try with default directory								
//				try 
//				{
//					filePathEvent	= Configure.getFilePath(Configure.getDirectory(), "Trace.tr");
//					filePathNode	= Configure.getFilePath(Configure.getDirectory(), "Neighbors.tr");
//					
//					readNodeTraceFile();
//					readEventTraceFile();
//					
//					System.out.print("done");
//				} 
//				catch (Exception e) 
				{													
					JOptionPane.showMessageDialog(null, "Let choose file! First, let choose neighbors file to get position of nodes.");
				    
			    	try 
			    	{ 
			    		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); 
			    	} catch (ClassNotFoundException | InstantiationException | IllegalAccessException	| UnsupportedLookAndFeelException ex)
			    	{ 
			    		// DO NOTHING
			    	}
					    	
					JFileChooser chooser = new JFileChooser();
					JFileChooser chooserTrace = new JFileChooser();
					
					chooser.showOpenDialog(null);
					File file = chooser.getSelectedFile();
					if (file != null)
					{
						filePathNode = file.getAbsolutePath();				
								
						JOptionPane.showMessageDialog(null, "Second, let choose trace file to get information of events.");	
								
						chooserTrace.showOpenDialog(null);
						file = chooserTrace.getSelectedFile();
						if (file != null) 
						{
							filePathEvent = file.getAbsolutePath();
						}
					}
							
					if (filePathEvent != "" && filePathNode != "")
					{
						try {
							readNodeTraceFile();
							readEventTraceFile();
						}
						catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				}
//			}
//		}.start();
	}
	
	@Override
	public synchronized AbstractMap<Integer, Node> getNodes() {		
		return node;
	}

	@Override
	public synchronized AbstractMap<Integer, Packet> getPackets() {
		return packet;
	}

	@Override
	public synchronized List<Event> getEvent() {
		return event;
	}
	
	/**
	 * read Neighbor.tr trace file
	 * @throws IOException exception when reading file
	 * @throws NumberFormatException exception when convert content
	 */
	private void readNodeTraceFile() throws NumberFormatException, IOException {
		String currentLine;
		BufferedReader br = new BufferedReader(new FileReader(filePathNode));

		while ((currentLine = br.readLine()) != null) {
			if (!currentLine.isEmpty())
			{
				String[] retval = currentLine.split("\\s+");
	
				int id = Integer.parseInt(retval[0]);
				node.put(id, new Node(id, Float.parseFloat(retval[1]), Float.parseFloat(retval[2])));
			}
		}
		br.close();		
	}
	
	/**
	 * read Trace.tr trace file
	 * @throws IOException exception when read file
	 * @throws NumberFormatException exception when convert contend
	 */
	private void readEventTraceFile() throws NumberFormatException, IOException {
		BufferedReader br = new BufferedReader(new FileReader(filePathEvent));
		String retval[];
		String sCurrentLine;

		System.out.println("Running...");		
		Event e;
		
		while ((sCurrentLine = br.readLine()) != null) {
			if (!sCurrentLine.isEmpty())
			{
				// Replace double space by one space
				retval = sCurrentLine.trim().replaceAll(" +", " ").split(" ");
							
				switch (retval[0]) {			
					case "N" :	// Energy: N -t 0.147751 -n 3 -e 999.998548
						node.get(4).getEnergy().put(Double.parseDouble(retval[2]), Double.parseDouble(retval[6]));					
						break;
					
					case "s" : // send
						int id = Integer.parseInt(retval[5]);
						Packet p = new Packet(id);
						packet.put(id, p);
						
						e = new Event();
						e.setType(Event.Type.SEND);
						e.setTime		(Double.parseDouble(retval[1]));						
						e.setNode		(node.get(Integer.parseInt(retval[2].substring(1, retval[2].length() - 1))));
						e.setLayer		(retval[3]);
						e.setMessage	(retval[4]);
						e.setPacketType	(retval[6]);
						e.setPacketSize	(Integer.parseInt(retval[7]));
						e.setPacket		(p);
						break;
						
					case "r" : // receive						
						e = new Event();
						e.setType(Event.Type.RECEIVE);
						e.setTime		(Double.parseDouble(retval[1]));
						e.setNode		(node.get(Integer.parseInt(retval[2].substring(1, retval[2].length() - 1))));
						e.setLayer		(retval[3]);
						e.setMessage	(retval[4]);
						e.setPacket		(packet.get(Integer.parseInt(retval[5])));
						e.setPacketType	(retval[6]);
						e.setPacketSize	(Integer.parseInt(retval[7]));			
						break;
						
					case "f" : // forward
						e = new Event();
						e.setType(Event.Type.FORWARD);
						e.setTime		(Double.parseDouble(retval[1]));
						e.setNode		(node.get(Integer.parseInt(retval[2].substring(1, retval[2].length() - 1))));
						e.setLayer		(retval[3]);
						e.setMessage	(retval[4]);
						e.setPacket		(packet.get(Integer.parseInt(retval[5])));
						e.setPacketType	(retval[6]);
						e.setPacketSize	(Integer.parseInt(retval[7]));					
						break;
						
					case "D" : // Drop
						e = new Event();
						e.setType(Event.Type.DROP);					
						e.setTime		(Double.parseDouble(retval[1]));
						e.setNode		(node.get(Integer.parseInt(retval[2].substring(1, retval[2].length() - 1))));
						e.setLayer		(retval[3]);
						e.setMessage	(retval[4]);
						e.setPacket		(packet.get(Integer.parseInt(retval[5])));
						e.setPacketType	(retval[6]);
						e.setPacketSize	(Integer.parseInt(retval[7]));											
						break;
						
					case  "M" : // Move:
						// do nothing
						break;			
				}
			}
		}
		br.close();
		
		System.out.println("Done.");
	}
	
//	public static void main(final String[] args) {
//		Configure.setDirectory("D:\\Work\\scripts\\Star\\goal");
//		new NS2Parser().getEvent();
//	}
}
