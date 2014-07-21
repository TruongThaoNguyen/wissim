package controllers.synchronizer.shadow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import controllers.Configure;
import controllers.synchronizer.Synchronizer;
import controllers.synchronizer.Scanner;
import controllers.synchronizer.Scheduler;
import controllers.synchronizer.TclObject;
import models.converter.Entry;
import models.converter.InsProc;
import models.converter.InsVar;
import models.converter.ParseException;
import models.converter.Token;
import models.converter.Token.TokenType;
import models.networkcomponents.Node;
import models.networkcomponents.WirelessNetwork;

/**
 * SimulatorObject.java
 * @author Duc-Trong Nguyen
 */
public class SNetwork extends WirelessNetwork implements TclObject, Scheduler
{	
	/**
	 * label of this object.
	 */
	private String label;
	
	/**
	 * entry list.
	 */
	private List<Entry> entryList = new ArrayList<Entry>();
	
	/**
	 * insProc list.
	 */
	private HashMap<String, InsProc> insProc = new HashMap<String, InsProc>();
	
	/**
	 * insVar list.
	 */
	private HashMap<String, InsVar>  insVar = new HashMap<String, InsVar>();
	
	/**
	 * event list.
	 */
	private HashMap<String, Double> event = new HashMap<String, Double>();
	
	/**
	 * nodeConfig object.
	 * that object include all configure for nodes.
	 * corresponding with "node-config" Tcl command.
	 */
	SCommonObject nodeConfig; 
	
	/**
	 * Create new Shadow Network Object.
	 * @param label label of this object
	 */
	public SNetwork(final String label)
	{		
		this.label = label;	
		nodeConfig = new SCommonObject("node-config");
		nodeConfig.setParent(this);
		addInsProc();
	}

	// region ------------------- Event ------------------- //
	
	@Override
	public void addEvent(final double time, String arg) {
		event.put(arg, time);
		
		// check if this event is stop event or not
		if (arg.contains("end")) setTime((int)time);				
	}

	@Override
	public double getEvent(String arg) {
		return event.get(arg);
	} 
	
	@Override
	public HashMap<String, Double> getEvent() {
		return event;
	}
	
	// endregion Event
	
	// region ------------------- TCL properties ------------------- //
	
	// region ------------------- Parse Tcl code ------------------- //
	
	@Override
	public String parse(List<String> command, boolean isRecord) throws Exception {
		if (command.isEmpty()) throw new ParseException(ParseException.MissArgument);
		
		InsProc proc = insProc.get(Synchronizer.parseIdentify(command.get(0)));
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
	
	/**
	 * get nodeConfig object.
	 * nodeConfig object is object that storage configation for each node.
	 * @return
	 */
	public SCommonObject getNodeConfig() {
		return nodeConfig;
	}
	
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
					case 1 : InsVar i = getInsVar(Synchronizer.parseIdentify(command.get(0)));
					 	if (i != null)	return i.getValue();
					 	return null;
					case 2 : return setInsVar(Synchronizer.parseIdentify(command.get(0)), Synchronizer.parseIdentify(command.get(1)), command.get(1)).getValue();
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
				
				return Configure.setTraceFile(Synchronizer.parseIdentify(command.get(0)));
				//return setInsVar("trace-all", Converter.parseIdentify(command.get(0)), command.get(0)).getValue();
			}

//			@Override
//			public String print(List<String> command) {
//				//return getInsVar("trace-all").getLabel();
//				return "[open " + Configure.getTraceFile() + " w]";
//			}			
		};
	
		new InsProc(this, "node-config") {
			@Override
			public String run(List<String> command) throws Exception {
				nodeConfig.addEntry(entry);
				
				if (command.size() % 2 == 1) throw new ParseException(ParseException.MissArgument);
				for (int i = 0; i < command.size(); i+=2)
				{
					nodeConfig.setInsVar(Synchronizer.parseIdentify(command.get(i)), Synchronizer.parseIdentify(command.get(i + 1)), command.get(i + 1));
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
				TclObject node = Synchronizer.global.getObject(Synchronizer.parseIdentify(arg.get(0)));
				node.addEntry(entry);				
				return node.parse(command, false);
			}
			
		};
		
		new InsProc(this, "at") {
			@Override
			public String run(List<String> command) throws Exception 
			{
				if (command.size() != 2) throw new ParseException(ParseException.InvalidArgument);		
				addEvent(Double.parseDouble(Synchronizer.parseIdentify(command.get(0))), command.get(1));
				
				// check if it is about node				
				List<Token> token = new Scanner(command.get(1)).scanWord();
				if (token.size() == 1 && token.get(0).Type() == TokenType.Quote)
				{
					List<String> cmd = new Scanner(token.get(0).Value()).scanCommand();
					if (cmd.size() > 1)
					{
						TclObject o = Synchronizer.global.getObject(Synchronizer.parseIdentify(cmd.get(0)));
						if (o != null) o.addEntry(entry);						
					}
				}
				
				return command.get(1);
			}
			
			@Override
			protected String print(List<String> arg) {
				return getEvent(arg.get(1)) + " " + arg.get(1);
			}
		};

		new InsProc(this, "attach-agent") {
			// first argument is base agent, second once is attack agent
			@Override
			protected String run(List<String> command) throws Exception {
				if (command.size() != 2) throw new ParseException(ParseException.InvalidArgument);
				
				SNode 		  		node  = (SNode) 		  	 Synchronizer.global.getObject(Synchronizer.parseIdentify(command.get(0)));
				STransportProtocol	agent = (STransportProtocol) Synchronizer.global.getObject(Synchronizer.parseIdentify(command.get(1)));				
				
				agent.setNode(node);
				node.addTransportProtocol(agent);
				return "";
			}	
			
			
		};
		
		new InsProc(this, "connect"){
			@Override
			public String run(List<String> command) throws Exception {
				if (command.size() != 2) throw new ParseException(ParseException.InvalidArgument);
				STransportProtocol	base  = (STransportProtocol) Synchronizer.global.getObject(Synchronizer.parseIdentify(command.get(0)));
				STransportProtocol	agent = (STransportProtocol) Synchronizer.global.getObject(Synchronizer.parseIdentify(command.get(1)));
				base.setConnected(agent);
				return "";
			}	
		};
	}
		
	private String insprocNode(List<String> command) throws Exception {
		if (command.size() != 0) throw new ParseException(ParseException.InvalidArgument);
		SNode newNode = new SNode(this);
		newNode.addEntry(Synchronizer.getGenerateEntry().get(Synchronizer.getGenerateEntry().size() - 1));			
		return Synchronizer.global.addObject(newNode);								
	}
	
	// endregion InsProc
	
	// endregion TCL properties
	
	// region ------------------- Wireless Network properties ------------------- //
	
	@Override
	protected boolean removenode(Node n) {	
		if (!nodeList.contains(n)) return false;							
		
		// remove from global objList
		Synchronizer.global.removeObject(n);
		
		// remove form nodeList
		nodeList.remove(n);
		
		// remove register entry
		for (Entry e : ((SNode)n).getEntry())
		{
			int i = Synchronizer.getGenerateEntry().indexOf(e);
			if (Synchronizer.getGenerateEntry().get(i - 1).toString().trim().isEmpty()) Synchronizer.getGenerateEntry().remove(i - 1);
			Synchronizer.getGenerateEntry().remove(e);			
		}	
		
		return true;
	}

	@Override
	protected SNode addnode(int x, int y) {
		SNode newNode = new SNode(this, x, y);
		newNode.setLabel("$mnode_(" + newNode.getId() + ")");		
		
		// region ------------------- auto generate tcl code ------------------- //

		// find last index of network's component in the global register					
//		int	index = 0;
//		for (Entry entry : nodeConfig.getEntry()) {
//			index = Math.max(index, Converter.getGenerateEntry().lastIndexOf(entry));
//		}
//		index++;
		
		int index = Synchronizer.getNewNodeIndex();
		
		// space
		Entry en = new Entry("\n");
		Synchronizer.registerEntry(index++, en);
		newNode.addEntry(en);
		
		// create node 		set mnode_($i) [$ns_ node]
		en = new Entry("set mnode_(" + newNode.getId() + ") [" + this.getLabel() + " node]\n");
		Synchronizer.registerEntry(index++, en);
		newNode.addEntry(en);
		
		// set position		$mnode_(0) set X_ 30	; $mnode_(0) set Y_ 860	; $mnode_(0) set Z_ 0
		en = new Entry(newNode.getInsProc("set"), Arrays.asList("X_", x + ""));
		Synchronizer.registerEntry(index++, en);
		newNode.addEntry(en);
		
		en = new Entry(newNode.getInsProc("set"), Arrays.asList("Y_", y + ""));
		Synchronizer.registerEntry(index++, en);
		newNode.addEntry(en);
		
		en = new Entry(newNode.getLabel() + " set Z_ 0\n");
		Synchronizer.registerEntry(index++, en);
		newNode.addEntry(en);		
		
		// $ns_ initial_node_pos $mnode_($i) 5
		en = new Entry(this.label + " initial_node_pos " + newNode.getLabel() + " 5\n");
		Synchronizer.registerEntry(index++, en);
		newNode.addEntry(en);
		
		// $ns_ at [expr $opt(stop) - 5] "[$mnode_($i) set ragent_] dump"
		en = new Entry(this.label + " at [expr " + getTime() + " - 5] \"[" + newNode.getLabel() + " set ragent_] dump\"\n");
		Synchronizer.registerEntry(index++, en);
		newNode.addEntry(en);
		
		// $ns_ at $opt(stop) "$mnode($i) reset" 
		en = new Entry(this.label + " at " + getTime() + ".0001 \"" + newNode.getLabel() + " reset\"\n");
		Synchronizer.registerEntry(index++, en);
		newNode.addEntry(en);
		
		Synchronizer.setNewNodeIndex(index);
		
		// endregion generate auto tcl code
		
		//getNodeList().add(newNode);		
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
