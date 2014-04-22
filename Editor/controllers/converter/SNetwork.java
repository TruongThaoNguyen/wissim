package controllers.converter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import models.converter.Entry;
import models.converter.InsProc;
import models.converter.InsVar;
import models.converter.ParseException;
import models.networkcomponents.Node;
import models.networkcomponents.WirelessNetwork;
import models.networkcomponents.WirelessNode;

/**
 * SimulatorObject.java
 * @Copyright (C) 2014, Sedic Laboratory, Hanoi University of Science and Technology
 * @Author Duc-Trong Nguyen
 * @Version 2.0
 */

public class SNetwork extends WirelessNetwork implements TclObject
{		
	// region ------------------- TCL properties ------------------- //

	private String label;
	private List<Entry> entryList = new ArrayList<Entry>();
	private HashMap<String, InsProc> insProc = new HashMap<String, InsProc>();
	private HashMap<String, InsVar>  insVar = new HashMap<String, InsVar>();
	private HashMap<String, Double> event = new HashMap<String, Double>();
	
	private SCommonObject nodeConfig; 
	
	/**
	 * Create new Shadow Network Object.
	 * @param value
	 */
	public SNetwork(String label)
	{
		super(label);
		this.label = label;	
		nodeConfig = new SCommonObject("node-config");
		addInsProc();
	}

	@Override
	public String parse(List<String> command) throws Exception {
		if (command.isEmpty()) throw new ParseException(ParseException.MissArgument);
		
		InsProc proc = insProc.get(Converter.parseIdentify(command.get(0)));
		if (proc != null)
		{
			command.remove(0);
			return proc.Run(command);
		}
		else 		
			return insProc.get(null).Run(command);
	}

	@Override
	public void addEvent(Double time, String arg) {
		event.put(arg, time);		
	}
	
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
	
	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public void setLabel(String label) {
		this.label = label;	
	}

	
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
						
						obj.addEvent(time, arg);
						return arg;
					}
				}									
				
				addEvent(time, arg);
				return arg;
			}		
		};

		new InsProc(this, "attach-agent") {
			// first arg is base agent, second once is attack agent
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
	protected void removenode(Node n) 
	{	
		SNode s = (SNode) n;
		
		// remove from global objList
		Converter.global.removeObject(s);
		
		// remove register entry
		for (Entry e : s.getEntry())
		{
			Converter.generateEntry.remove(e);				
		}
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
		Converter.generateEntry.add(index + 2, new Entry("set mnode_(" + newNode.getId() + ") [$" + this.label + " node]\n"));
		
		// set position		$mnode_(0) set X_ 30	; $mnode_(0) set Y_ 860	; $mnode_(0) set Z_ 0
		Converter.generateEntry.add(index + 3, new Entry("$mnode_(" + newNode.getId() + ") set X_ " + x + 
												  " ; $mnode_(" + newNode.getId() + ") set Y_ " + y +
												  " ; $mnode_(" + newNode.getId() + ") set Z_ 0\n"));
		
		// 	$ns_ initial_node_pos $mnode_($i) 5
		Converter.generateEntry.add(index + 4, new Entry("$" + label + "initial_node_pos $mnode_(" + newNode.getId() + ") 5\n"));
						
		// endregion generate auto tcl code
		
		return newNode;
	}

	// endregion Wireless Network properties	
}
