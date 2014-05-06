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
import models.networkcomponents.Node;
import models.networkcomponents.WirelessNetwork;
import models.networkcomponents.WirelessNetwork;
import models.networkcomponents.WirelessNode;
import models.networkcomponents.WirelessNode;

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
	
	private SCommonObject nodeConfig; 

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
	
	// region ------------------- Event ------------------- //
	
	@Override
	public void addEvent(Double time, String arg) {
		event.put(arg, time);		
	}

	// endregion Event
	
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
	
		new InsProc(this, "at") {
			@Override
			public String run(List<String> command) throws Exception 
			{
				if (command.size() != 2) throw new ParseException(ParseException.InvalidArgument);
				
				double time = Double.parseDouble(Converter.parseIdentify(command.get(0)));
				String arg  = Converter.parseIdentify(command.get(1));									
								
				List<String> sc = (new Scanner(arg)).scanCommand();
				
				if (sc.size() > 1)
				{
					TclObject obj = Converter.global.getObject(sc.remove(0));
					if (obj != null)
					{							
						arg = "";				
						for (String s : sc)	arg += s;				
						
						((Scheduler)obj).addEvent(time, arg);
						obj.addEntry(entry);
						return arg;
					}
				}									
				
				addEvent(time, arg);
				return arg;
			}
			
			@Override
			protected void record(List<String> command) {
				Converter.generateEntry.add(new Entry(this, command));
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
				if (agent.getLabel() != "Null") node.addTransportProtocol(agent);
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
	protected WirelessNode addnode(int x, int y, int rage) {
		SNode newNode = new SNode(this, x, y, rage);
		Converter.global.addObject(newNode);
		
		// region ------------------- auto generate tcl code ------------------- //

		List<Entry> e = ((SNode)nodeList.get(nodeList.size() - 1)).getEntry();		
		int index = Converter.generateEntry.lastIndexOf(e.get(e.size() - 1));
		
		// space
		Converter.generateEntry.add(index + 1, new Entry("\n"));
		
		// create node 		set mnode_($i) [$ns_ node]
		Entry en = new Entry("set mnode_(" + newNode.getId() + ") [$" + this.label + " node]\n");
		Converter.generateEntry.add(index + 2, en);
		newNode.addEntry(en);
		
		// set position		$mnode_(0) set X_ 30	; $mnode_(0) set Y_ 860	; $mnode_(0) set Z_ 0
		en = new Entry("$mnode_(" + newNode.getId() + ") set X_ " + x + " ; " + 
						"$mnode_(" + newNode.getId() + ") set Y_ " + y + " ; " +
						"$mnode_(" + newNode.getId() + ") set Z_ 0\n");
		Converter.generateEntry.add(index + 3, en);
		newNode.addEntry(en);
		
		// 	$ns_ initial_node_pos $mnode_($i) 5
		en = new Entry("$" + label + "initial_node_pos $mnode_(" + newNode.getId() + ") 5\n");
		Converter.generateEntry.add(index + 4, en);
		newNode.addEntry(en);
		
		// endregion generate auto tcl code
		
		return newNode;
	}

	@Override
	public List<Node> getNodeList() {
		return nodeList;
	}

	@Override
	public int getTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected void settime(int time) {
		// TODO Auto-generated method stub
		
	}

	// endregion Wireless Network properties	
}
