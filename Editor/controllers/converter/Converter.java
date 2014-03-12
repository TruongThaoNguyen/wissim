package controllers.converter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;

import controllers.converter.tclObject.GlobalObject;
import controllers.converter.tclObject.TclObject;
import controllers.converter.tclObject.TopographyObject;
import models.Project;
import models.converter.ParseException;
import models.networkcomponents.Node;
import models.networkcomponents.WirelessNetwork;
import models.networkcomponents.WirelessNode;
import models.networkcomponents.events.AppEvent;
import models.networkcomponents.protocols.ApplicationProtocol;
import models.networkcomponents.protocols.TransportProtocol;

/**
 * Parser.java
 * @Copyright (C) 2014, Sedic Laboratory, Hanoi University of Science and Technology
 * @Author Duc-Trong Nguyen
 * @Version 2.0
 */
public class Converter {
	private Project project = null;	
	private GlobalObject global;
	private String code;
	
	/**
	 * Constructor
	 * @param project
	 */
	public Converter(Project project) 
	{					
		this.project = project;
	}
	
	public static void main(String[] args)  throws IOException, ParseException {		
		BufferedReader br = new BufferedReader(new FileReader("/home/trongnguyen/scripts/30/ehds/simulate.tcl"));
		StringBuilder sb = null;
		try {
		    sb = new StringBuilder();
		    String line = br.readLine();
		
		    while (line != null) {
		        sb.append(line);
		        sb.append(System.lineSeparator());
		        line = br.readLine();
		    }		    
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
		    br.close();
		}
		
		String everything = sb.toString();
		Converter converter = new Converter(new Project("", new WirelessNetwork("", 400, 400)));
		converter.CTD(everything);
								
		System.out.print(converter.DTC());
	}
	
	// region ------------- functions for CTD ------------- //
	
	/**
	 * CTD - Code to Design
	 * Parser Tcl code to a Project model 
	 * @param text
	 * @return project
	 * @throws ParseException 
	 */
	public Project CTD(String text) throws ParseException
	{
		global = Parser.parse(text);		
		
		String value;
		value = global.insObj.get(global.simObject.insVar.get("-channel")).value;	project.setSelectedChannel(value); if (global.insObj.containsKey(value)) project.getChannels().put(value, global.insObj.get(value).insVar);		
		value = global.simObject.insVar.get("-antType");		project.setSelectedAntenna(value);			if (global.insObj.containsKey(value)) project.getAntennas().put(value, global.insObj.get(value).insVar);										
		value = global.simObject.insVar.get("-ifqType");		project.setSelectedInterfaceQueue(value);	if (global.insObj.containsKey(value)) project.getInterfaceQueues().put(value, global.insObj.get(value).insVar);		
		value = global.simObject.insVar.get("-llType");			project.setSelectedLinkLayer(value);		if (global.insObj.containsKey(value)) project.getLinkLayers().put(value, global.insObj.get(value).insVar);		
		value = global.simObject.insVar.get("-macType");		project.setSelectedMac(value);				if (global.insObj.containsKey(value)) project.getMacs().put(value, global.insObj.get(value).insVar);		
		value = global.simObject.insVar.get("-phyType");		project.setSelectedNetworkInterface(value);	if (global.insObj.containsKey(value)) project.getNetworkInterfaces().put(value, global.insObj.get(value).insVar);		
		value = global.simObject.insVar.get("-propType");		project.setSelectedPropagationModel(value);	if (global.insObj.containsKey(value)) project.getPropagationModels().put(value, global.insObj.get(value).insVar);	
		value = global.simObject.insVar.get("-adhocRouting");	project.setSelectedRoutingProtocol(value);	if (global.insObj.containsKey(value)) project.getRoutingProtocols().put(value, global.insObj.get(value).insVar);								
		value = global.simObject.insVar.get("-ifqLen");			project.setQueueLength(Integer.parseInt(value));
		value = global.simObject.insVar.get("-idlePower");		project.setIddleEnergy(Double.parseDouble(value));
		value = global.simObject.insVar.get("-rxPower");		project.setReceptionEnergy(Double.parseDouble(value));
		value = global.simObject.insVar.get("-txPower");		project.setTransmissionEnergy(Double.parseDouble(value));
		value = global.simObject.insVar.get("-sleepPower");		project.setSleepEnergy(Double.parseDouble(value));
								
		// size
		
		value = global.simObject.insVar.get("-propType");
		int xSize = 0;
		int ySize = 0;
		
		for (TclObject obj : global.insObj.values()) 
		{
			if (obj.value.equals(value)) 
			{
				TclObject topo = global.insObj.get(obj.insVar.get("topography"));
				xSize = Integer.parseInt(topo.insVar.get("width"));
				ySize = Integer.parseInt(topo.insVar.get("width"));
			}
		}
		
		WirelessNetwork network = new WirelessNetwork(project.getNetwork().getName(), xSize, ySize);
		project.setNetwork(network);		
		
		// node list
		
		Double v = global.simObject.At.get("stop");
		if (v != null)	network.setTime((int)(double)v);
		else 			throw new ParseException("Missing End Simulation");
		
		network.getNodeList().clear();
		for (TclObject node : global.simObject.Node) 
		{
			try 
			{
				new WirelessNode(
								network, 
								Integer.parseInt(node.insVar.get("X_")), 
								Integer.parseInt(node.insVar.get("Y_")), 
								project.getNodeRange());
			} 
			catch (Exception e)
			{
				throw new ParseException(ParseException.InvalidArgument);
			}
		}		
		
		return project;
	}
	
	// endregion
	
	// region ------------- functions for DTC ------------- //

	public String DTC() throws ParseException
	{
		return initializeTcl(project);	
	}

	private String initializeTcl(Project project) {
		StringBuilder sb = new StringBuilder();
		
		// set up global variables
		putLine(sb, "set opt(chan)\t\t" + genTclClass(project.getSelectedChannel()));
		putLine(sb, "set opt(prop)\t\t" + genTclClass(project.getSelectedPropagationModel()));
		putLine(sb, "set opt(netif)\t\t" + genTclClass(project.getSelectedNetworkInterface()));
		putLine(sb, "set opt(mac)\t\t" + genTclClass(project.getSelectedMac()));
		putLine(sb, "set opt(ifq)\t\t" + genTclClass(project.getSelectedInterfaceQueue()));
		putLine(sb, "set opt(ll)\t\t" + genTclClass(project.getSelectedLinkLayer()));
		putLine(sb, "set opt(ant)\t\t" + genTclClass(project.getSelectedAntenna()));
		breakLine(sb);
		
		putLine(sb, "# define network size variables");
		putLine(sb, "set opt(x)\t\t" + project.getNetwork().getWidth() + "\t\t; # X dimension of the topography");
		putLine(sb, "set opt(y)\t\t" + project.getNetwork().getLength() + "\t\t; # Y dimension of the topography");
		putLine(sb, "set opt(nn)\t\t" + project.getNetwork().getNodeList().size() + "\t\t; # number of nodes");
		putLine(sb, "set opt(stop)\t\t" + project.getNetwork().getTime() + "\t\t; # simulation time");
		breakLine(sb);
		
		putLine(sb, "set opt(ifqlen)\t\t" + project.getQueueLength() + "\t\t; # max packets in interface queue");
		putLine(sb, "set opt(tr)\t\ttrace.tr" + "\t; # trace file");
		putLine(sb, "set opt(nam)\t\tnam.tr" + "\t\t; # nam trace file");
		putLine(sb, "set opt(wissim)\t\twissim.tr" + "\t; # wissim trace file");
		putLine(sb, "set opt(rp)\t\t" + project.getSelectedRoutingProtocol() + "\t\t; # routing protocol");
		putLine(sb, "set opt(energymodel)\t" + "EnergyModel");
		putLine(sb, "set opt(initialenergy)\t" + "200" + "\t\t; # initial energy");
		breakLine(sb);
		
		putLine(sb, "# ================================================================");
		breakLine(sb);
		
		putLine(sb, generateTclClassConfig(project.getSelectedAntenna(), project.getAntennas()));
		putLine(sb, generateTclClassConfig(project.getSelectedChannel(), project.getChannels()));
		putLine(sb, generateTclClassConfig(project.getSelectedInterfaceQueue(), project.getInterfaceQueues()));
		putLine(sb, generateTclClassConfig(project.getSelectedLinkLayer(), project.getLinkLayers()));
		putLine(sb, generateTclClassConfig(project.getSelectedMac(), project.getMacs()));
		putLine(sb, generateTclClassConfig(project.getSelectedNetworkInterface(), project.getNetworkInterfaces()));
		putLine(sb, generateTclClassConfig(project.getSelectedPropagationModel(), project.getPropagationModels()));
		putLine(sb, generateTclClassConfig(project.getSelectedRoutingProtocol(), project.getRoutingProtocols()));
		putLine(sb, generateTclClassConfig(project.getSelectedTransportProtocol(), project.getTransportProtocols()));
		breakLine(sb);
		
		
		// initialize global variables
		putLine(sb, "#");
		putLine(sb, "# Initialize Global Variables");
		putLine(sb, "#");
		breakLine(sb);
		putLine(sb, "#set up ns simulator and trace");
		putLine(sb, "set ns_ [new Simulator]");
		putLine(sb, "set chan [new $opt(chan)]");
		putLine(sb, "set prop [new $opt(prop)]");
		putLine(sb, "set topo [new Topography]");
		breakLine(sb);
		putLine(sb, "set tracefd\t[open $opt(tr) w]");
		putLine(sb, "set namtrace\t[open $opt(nam) w]");
		putLine(sb, "set wissimtrace\t[open $opt(wissim) w]");
		breakLine(sb);
		putLine(sb, "# run the simulator");
		putLine(sb, "$ns_ trace-all $tracefd");
		putLine(sb, "$ns_ nam-trace-all-wireless $namtrace $opt(x) $opt(y)");
		putLine(sb, "$ns_ wissim-trace-all $wissimtrace");
		breakLine(sb);
		putLine(sb, "$topo load_flatgrid $opt(x) $opt(y) ");
		putLine(sb, "$prop topography $topo");
		breakLine(sb);
		putLine(sb, "set god_ [create-god $opt(nn)]");
		breakLine(sb);
		putLine(sb, "# configure the nodes");
		putLine(sb, "$ns_ node-config 	-adhocRouting $opt(rp) \\");
		putLine(sb, "					-llType $opt(ll) \\");
		putLine(sb, "					-macType $opt(mac) \\");
		putLine(sb, "					-ifqType $opt(ifq) \\");
		putLine(sb, "					-ifqLen $opt(ifqlen) \\");
		putLine(sb, "					-antType $opt(ant) \\");
		putLine(sb, "					-propType $opt(prop) \\");
		putLine(sb, "					-phyType $opt(netif) \\");
		putLine(sb, "					-channel [new $opt(chan)] \\");
		putLine(sb, "					-topoInstance $topo \\");
		putLine(sb, "					-agentTrace ON \\");
		putLine(sb, "					-routerTrace ON \\");
		putLine(sb, "					-macTrace OFF \\");
		putLine(sb, "					-movementTrace OFF \\");
		putLine(sb, "				   	-energyTrace OFF \\");
		putLine(sb, "				 	-energyModel $opt(energymodel) \\");
		putLine(sb, "				 	-idlePower " + project.getIddleEnergy() + " \\");
		putLine(sb, "				 	-rxPower " + project.getReceptionEnergy() + " \\");
		putLine(sb, "				 	-txPower " + project.getTransmissionEnergy() + " \\");
		putLine(sb, "			    		-sleepPower " + project.getSleepEnergy() + " \\");
		putLine(sb, "				 	-initialEnergy $opt(initialenergy)");
		breakLine(sb);
		
		putLine(sb, "# set up nodes");
		putLine(sb, "for {set i 0} {$i < $opt(nn)} {incr i} {");
		putLine(sb, "\tset mnode_($i) [$ns_ node]");
		putLine(sb, "}");
		breakLine(sb);
		
		putLine(sb, "# node location data");
		for (Node n : project.getNetwork().getNodeList()) {
			putLine(sb, "$mnode_(" + n.getId() + ") set X_ " + ((WirelessNode)n).getX());
			putLine(sb, "$mnode_(" + n.getId() + ") set Y_ " + ((WirelessNode)n).getY());
			putLine(sb, "$mnode_(" + n.getId() + ") set Z_ 0");
		}
		breakLine(sb);
		
		putLine(sb, "for {set i 0} {$i < $opt(nn)} { incr i } {");
		putLine(sb, "\t$ns_ initial_node_pos $mnode_($i) 5");
		putLine(sb, "}");
		breakLine(sb);
		
		putLine(sb, "# telling nodes when the simulator ends");
		putLine(sb, "for {set i 0} {$i < $opt(nn)} { incr i } {");
		putLine(sb, "\t$ns_ at $opt(stop).000000001 \"$mnode_($i) reset\"");
		putLine(sb, "}");
		breakLine(sb);
		
		for (Node n : project.getNetwork().getNodeList()) 
		{
			for (TransportProtocol tp : n.getTransportPrototolList()) 
			{
				for (ApplicationProtocol ap : tp.getAppList()) 
				{
					// set up a udp connection
					switch (tp.getType()) 
					{
						case TransportProtocol.TCP:
							putLine(sb, "set " + tp.getName() + " [new Agent/TCP]");
							break;
					
						case TransportProtocol.UDP:
							putLine(sb, "set " + tp.getName() + " [new Agent/UDP]");
							break;
					}			
					
					WirelessNode destNode = (WirelessNode)ap.getDestNode();
					putLine(sb, "$mnode_(" + n.getId() + ") setdest " + destNode.getX() + " " + destNode.getY() + " 0");
					putLine(sb, "$ns_ attach-agent $mnode_(" + n.getId() + ") $" + tp.getName());
					breakLine(sb);
					
					// set up sink
					putLine(sb, "set sink_(" + ap.getDestNode().getId() + ") [new Agent/Null]");
					putLine(sb, "$ns_ attach-agent $mnode_(" + ap.getDestNode().getId() + ") $sink_(" + ap.getDestNode().getId() + ")");
					breakLine(sb);
					
					// connect udp with sink
					putLine(sb, "$ns_ connect $" + tp.getName() + " $sink_(" + ap.getDestNode().getId() + ")");
					putLine(sb, "$" + tp.getName() + " set fid_ 2");
					breakLine(sb);
					
					// setup a cbr over udp connection
					String type = "";
					switch (ap.getType()) {
					case ApplicationProtocol.CBR:
						type = "CBR";
						break;
					case ApplicationProtocol.FTP:
						type = "FTP";
						break;
					case ApplicationProtocol.PARETO:
						type = "PARETO";
						break;
					case ApplicationProtocol.TELNET:
						type = "TELNET";
						break;
					case ApplicationProtocol.VBR:
						type = "VBR";
						break;
					}
					putLine(sb, "set " + ap.getName() + " [new Application/Traffic/" + type + "]");
					putLine(sb, "$" + ap.getName() + " attach-agent $" + tp.getName());
					
					HashMap<String, String> params = ap.getParameters();
					Set<Entry<String,String>> set = params.entrySet();
					Iterator<Entry<String, String>> iterator = set.iterator();
					while (iterator.hasNext()) {
						Entry<String,String> entry = iterator.next();
						putLine(sb, "$" + ap.getName() + " set " + entry.getKey() + " " + entry.getValue());
					}
					
					for (AppEvent e : ap.getEventList()) {
						switch (e.getType()) {
						case AppEvent.START:
							putLine(sb, "$ns_ at " + e.getRaisedTime() + " \"$" + ap.getName() + " start\"");
							break;
						case AppEvent.STOP:
							putLine(sb, "$ns_ at " + e.getRaisedTime() + " \"$" + ap.getName() + " stop \"");
							break;
						}
					}					
				}
			}
		}
		breakLine(sb);
		
		putLine(sb, "# ending nam and the simulation");
		putLine(sb, "$ns_ at $opt(stop) \"$ns_ nam-end-wireless $opt(stop)\"");
		putLine(sb, "$ns_ at $opt(stop) \"stop\"");
		putLine(sb, "$ns_ at [expr $opt(stop) + 0.01] \"puts \\\"end simulation\\\"; $ns_ halt\"");
		breakLine(sb);
		
		putLine(sb, "proc stop {} {");
		putLine(sb, "	global ns_ tracefd namtrace");
		putLine(sb, "	$ns_ flush-trace");
		putLine(sb, "	close $tracefd");
		putLine(sb, "	close $namtrace");
		putLine(sb, "	close $wissimtrace");
		putLine(sb, "	exit 0");
		putLine(sb, "}");
		breakLine(sb);
		
		// run the simulator
		putLine(sb, "$ns_ run");
		breakLine(sb);
		
		putLine(sb, "########### end script #####################");
		
		return sb.toString();
	}
	
	private static void putLine(StringBuilder sb, String str) {
		sb.append(str + System.lineSeparator());
	}
	
	private static void breakLine(StringBuilder sb) {
		sb.append(System.lineSeparator());
	}
	
	private static String genTclClass(String type) {
		if (type == null) return "";
		
		switch (type) {
			case "AODV":
				return "Agent/AODV";
			case "DSR":
				return "Agent/DSR";
			case "DSDV":
				return "Agent/DSDV";
			case "GPSR":
				return "Agent/GPSR";
			case "GEAR":
				return "Agent/GEAR";
			case "ELBAR":
				return "Agent/ELBAR";
			case "ELLIPSE":
				return "Agent/ELLIPSE";
			case "EHDS":
				return "Agent/EHDS";
			case "TCP":
				return "Agent/TCP";
			case "UDP":
				return "Agent/UDP";
			case "Two Ray Ground":
				return "Propagation/TwoRayGround";
			case "Wireless":
				return "Channel/WirelessChannel";
			case "Wireless Phy":
				return "Phy/WirelessPhy";
			case "802.11":
				return "Mac/802_11";
			case "Droptail/Priqueue":
				return "Queue/DropTail/PriQueue";
			case "Omni Antenna":
				return "Antenna/OmniAntenna";
			case "CBR":
				return "Agent/CBR";
		}
		
		return type;
	}
	
	private static String generateTclClassConfig(String name, HashMap<String, HashMap<String, String>> hashMap) {
		HashMap<String, String> h = hashMap.get(name);
		if (h == null) return "";
		
		String snippet = "";
		Set<Entry<String, String>> set = h.entrySet();
		Iterator<Entry<String, String>> i = set.iterator();
		
		while (i.hasNext()) {
			Entry<String, String> entry = i.next();
			String value = "";
			
			if (entry.getKey().equals(""))
				continue;
			
			if (entry.getValue().equals(""))
				value = "0";
			else
				value = entry.getValue();
			
			snippet += genTclClass(name) + " set " + entry.getKey() + "\t\t" + value + "\r\n";
		}
		
		return snippet;
	}
	
	// endregion
}
