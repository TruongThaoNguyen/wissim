package controllers.converter.shadow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import controllers.converter.Converter;
import controllers.converter.Scanner;
import controllers.converter.Scheduler;
import controllers.converter.TclObject;
import models.converter.Entry;
import models.converter.InsProc;
import models.converter.InsVar;
import models.converter.ParseException;
import models.networkcomponents.Node;
import models.networkcomponents.WirelessNetwork;

/**
 * SimulatorObject.java
 * @Copyright (C) 2014, Sedic Laboratory, Hanoi University of Science and Technology
 * @Author Duc-Trong Nguyen
 * @Version 2.0
 */

public class SNetwork extends WirelessNetwork implements TclObject, Scheduler
{			
	/**
	 * Create new Shadow Network Object.
	 * @param value
	 */
	public SNetwork(String label)
	{		
		this.label = label;	
		nodeConfig = new SCommonObject("node-config");
		addInsProc();
	}
	
	private String label;
	private List<Entry> entryList = new ArrayList<Entry>();
	private HashMap<String, InsProc> insProc = new HashMap<String, InsProc>();
	private HashMap<String, InsVar>  insVar = new HashMap<String, InsVar>();
	private HashMap<String, Double> event = new HashMap<String, Double>();
	
	SCommonObject nodeConfig; 

	// region ------------------- Event ------------------- //
	
	@Override
	public void addEvent(double time, String arg) {
		event.put(arg, time);		
		
		// check if this event is stop event or not
		if (arg.contains("end"))
		{
			setTime((int)time);
		}
	}

	@Override
	public double getEvent(String arg) {
		return event.get(arg);
	}
	
	@Override
	public HashMap<String, Double> getEvent() {
		// TODO Auto-generated method stub
		return null;
	}
	
	// endregion Event
	
	// region ------------------- TCL properties ------------------- //
	
	// region ------------------- Parse Tcl code ------------------- //
	
	@Override
	public String parse(List<String> command, boolean isRecord) throws Exception {
		if (command.isEmpty()) throw new ParseException(ParseException.MissArgument);
		
		InsProc proc = insProc.get(Converter.parseIdentify(command.get(0)));
		if (proc != null)
		{
			command.remove(0);
			return proc.Run(command, isRecord);
		}
		else 		
			return insProc.get(null).Run(command, isRecord);
	}

	// endregion Parse Tcl code
	
	// region ------------------- Register Entry ------------------- //

	@Override
	public void addEntry(Entry e) {
		entryList.add(e);	
	}
	
	@Override
	public void addEntry(int index, Entry e) {
		entryList.add(index, e);	
	}
	
	@Override
	public List<Entry> getEntry() {
		return entryList;
	}

	// endregion Register Entry	

	// region ------------------- Label ------------------- //
	
	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public void setLabel(String label) {
		this.label = label;	
	}
	
	// endregion Label
	
	// region ------------------- InsVar ------------------- //
	
	@Override
	public HashMap<String, InsVar> getInsVar() {
		return insVar;
	}
	
	@Override
	public InsVar getInsVar(String key) {
		return insVar.get(key);
	}

	@Override
	public InsVar setInsVar(String key, String value) {		
		InsVar i = insVar.get(key);
		if (i != null)
		{
			i.setValue(value);
		}
		else
		{
			i = new InsVar(value);
			insVar.put(key, i);
		}
		return i;
	}
	
	@Override
	public InsVar setInsVar(String key, String value, String label) {
		InsVar i = new InsVar(value, label);
		insVar.put(key, i);
		return i;
	}

	// endregion InsVar

	// region ------------------- InsProc ------------------- //

	@Override
	public InsProc getInsProc(String key) {
		return insProc.get(key);
	}
	
	@Override
	public void addInsProc(InsProc p) {
		insProc.put(p.insprocName, p);
	}
	
	protected void addInsProc()	{		
		new InsProc(this, null) {
			@Override
			protected String run(List<String> command) throws Exception {				
				return null;
			}
		};
		
		new InsProc(this, "set") {
			@Override
			protected String run(List<String> command) throws Exception {				
				switch (command.size()) 
				{
					case 0 : throw new ParseException(ParseException.MissArgument);
					case 1 : return getInsVar(Converter.parseIdentify(command.get(0))).getValue();
					case 2 : return setInsVar(Converter.parseIdentify(command.get(0)), Converter.parseIdentify(command.get(1)), command.get(1)).getValue();
					default: throw new ParseException(ParseException.InvalidArgument);
				}
			}

			@Override
			public String print(List<String> command) {
				return command.get(0) + " " + parent.getInsVar(command.get(0));
			}			
		};
		
		new InsProc(this, "trace-all") {
			@Override
			public String run(List<String> command) throws Exception {				
				if (command.size() == 0)	throw new ParseException(ParseException.MissArgument);
				if (command.size() > 1)		throw new ParseException(ParseException.InvalidArgument);
				
				return setInsVar("trace-all", Converter.parseIdentify(command.get(0)), command.get(0)).getValue();
			}

			@Override
			public String print(List<String> command) {
				return getInsVar("trace-all").getLabel();
			}			
		};
	
		new InsProc(this, "node-config") {
			@Override
			public String run(List<String> command) throws Exception {
				nodeConfig.addEntry(entry);
				
				if (command.size() % 2 == 1) throw new ParseException(ParseException.MissArgument);
				for (int i = 0; i < command.size(); i+=2)
				{
					nodeConfig.setInsVar(Converter.parseIdentify(command.get(i)), Converter.parseIdentify(command.get(i + 1)), command.get(i + 1));
				}
				return "";
			}

			@Override
			public String print(List<String> command) {
				String result = "";
				for (String key : nodeConfig.getInsVar().keySet())
				{
					result += " \\\n\t" + key + " " + nodeConfig.getInsVar(key);
				}
				return result;
			}			
		};
		
		new InsProc(this, "node") {
			@Override
			public String run(List<String> command) throws Exception {
				return insprocNode(command);
			}	
		};
	
		new InsProc(this, "initial_node_pos") {
			@Override
			protected String run(List<String> arg) throws Exception {
				if (arg.size() != 2) throw new ParseException(ParseException.InvalidArgument);
				List<String> command = new ArrayList<>();
				command.add("set");
				command.add("initial_node_pos");
				command.add(arg.get(1));
				TclObject node = Converter.global.getObject(Converter.parseIdentify(arg.get(0)));
				node.addEntry(entry);				
				return node.parse(command, false);
			}
			
		};
		
		new InsProc(this, "at") {
			@Override
			public String run(List<String> command) throws Exception 
			{
				if (command.size() != 2) throw new ParseException(ParseException.InvalidArgument);
				
				double time = Double.parseDouble(Converter.parseIdentify(command.get(0)));
				String arg  = Converter.parseIdentify(command.get(1));									
										
				addEvent(time, arg);
				return arg;
			}
			
			@Override
			protected void record(List<String> command) {
				entry = new Entry(this, command);
				Converter.generateEntry.add(entry);
			}
		};

		new InsProc(this, "attach-agent") {
			// first argument is base agent, second once is attack agent
			@Override
			protected String run(List<String> command) throws Exception {
				if (command.size() != 2) throw new ParseException(ParseException.InvalidArgument);
				
				SNode 		  		node  = (SNode) 		  	 Converter.global.getObject(Converter.parseIdentify(command.get(0)));
				STransportProtocol	agent = (STransportProtocol) Converter.global.getObject(Converter.parseIdentify(command.get(1)));				
				
				agent.setNode(node);
				node.addTransportProtocol(agent);
				return "";
			}	
			
			
		};
		
		new InsProc(this, "connect"){
			@Override
			public String run(List<String> command) throws Exception {
				if (command.size() != 2) throw new ParseException(ParseException.InvalidArgument);
				STransportProtocol	base  = (STransportProtocol) Converter.global.getObject(Converter.parseIdentify(command.get(0)));
				STransportProtocol	agent = (STransportProtocol) Converter.global.getObject(Converter.parseIdentify(command.get(1)));
				base.setConnected(agent);
				return "";
			}	
		};
	}
		
	private String insprocNode(List<String> command) throws Exception {
		if (command.size() != 0) throw new ParseException(ParseException.InvalidArgument);
		SNode newNode = new SNode(this);
		newNode.addEntry(Converter.generateEntry.get(Converter.generateEntry.size() - 1));
		nodeList.add(newNode);		
		return Converter.global.addObject(newNode);								
	}
	
	// endregion InsProc
	
	// endregion TCL properties
	
	// region ------------------- Wireless Network properties ------------------- //
	
	@Override
	protected boolean removenode(Node n) {	
		if (!nodeList.contains(n)) return false;							
		
		// remove from global objList
		Converter.global.removeObject(n);
		
		// remove form nodeList
		nodeList.remove(n);
		
		// remove register entry
		for (Entry e : ((SNode)n).getEntry())
		{
			Converter.generateEntry.remove(e);				
		}	
		
		return true;
	}

	@Override
	protected SNode addnode(int x, int y) {
		SNode newNode = new SNode(this, x, y);
		newNode.setLabel("$mnode_(" + newNode.getId() + ")");		
		
		// region ------------------- auto generate tcl code ------------------- //

		// find last index of network's component in the global register
		List<Entry> e;	
		
		e = nodeConfig.getEntry();
		
		// if (nodeList.isEmpty()) e = nodeConfig.getEntry();
		// else 					e = ((SNode)nodeList.get(nodeList.size() - 1)).getEntry();		
		int	index = Converter.generateEntry.lastIndexOf(e.get(e.size() - 1)) + 1;		
		
		// space
		Entry en = new Entry("\n");
		Converter.generateEntry.add(index++, en);
		newNode.addEntry(en);
		
		// create node 		set mnode_($i) [$ns_ node]
		en = new Entry("set mnode_(" + newNode.getId() + ") [" + this.getLabel() + " node]\n");
		Converter.generateEntry.add(index++, en);
		newNode.addEntry(en);
		
		// set position		$mnode_(0) set X_ 30	; $mnode_(0) set Y_ 860	; $mnode_(0) set Z_ 0
		en = new Entry(newNode.getLabel() + " set X_ " + x + "\n");
		Converter.generateEntry.add(index++, en);
		newNode.addEntry(en);
		
		en = new Entry(newNode.getLabel() + " set Y_ " + y + "\n");
		Converter.generateEntry.add(index++, en);
		newNode.addEntry(en);
		
		en = new Entry(newNode.getLabel() + " set Z_ 0\n");
		Converter.generateEntry.add(index++, en);
		newNode.addEntry(en);
		
		// $ns_ initial_node_pos $mnode_($i) 5
		en = new Entry(this.label + " initial_node_pos " + newNode.getLabel() + " 5\n");
		Converter.generateEntry.add(index++, en);
		newNode.addEntry(en);
		
		// $ns_ at $opt(stop) "$mnode($i) reset" 
		en = new Entry(this.getLabel() + " at " + getTime() + ".0001 \"" + newNode.getLabel() + " reset\"\n");
		Converter.generateEntry.add(index++, en);
		newNode.addEntry(en);
		
		// endregion generate auto tcl code
		
		getNodeList().add(newNode);		
		return newNode;
	}

	@Override
	protected void settime(int time) {
		// check if this event is stop event or not
		for (String key : event.keySet()) {
			//if (key.contains(getLabel() + "halt") || key.contains("exit 0"))
			if (key.contains("stop"))
				event.put(key, (double) time);
		}
	}
	
	// endregion Wireless Network properties	
}
