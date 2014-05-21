package controllers.converter.shadow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import controllers.converter.Converter;
import controllers.converter.Scheduler;
import controllers.converter.TclObject;
import models.converter.Entry;
import models.converter.InsProc;
import models.converter.InsVar;
import models.converter.ParseException;
import models.networkcomponents.Node;
import models.networkcomponents.protocols.ApplicationProtocol;
import models.networkcomponents.protocols.ApplicationProtocol.ApplicationProtocolType;
import models.networkcomponents.protocols.TransportProtocol;

public class STransportProtocol extends TransportProtocol implements TclObject, Scheduler {	
	
	private SNode node;
	
	protected STransportProtocol(String label) {				
		super(TransportProtocolType.valueOf(label), label);
		this.label = label;		
		
		addInsProc();
	}	

	public STransportProtocol(TransportProtocolType type, String label, SNode node) {
		super(type, label);
		this.label = label;
		this.node = node;
		
		addInsProc();
	}
	
	STransportProtocol connectedAgent;
	
	public void setConnected(STransportProtocol agent) {
		connectedAgent = agent;
	}
	
	// region ------------------- Scheduler ------------------- //
	
	private HashMap<String, Double> event = new HashMap<String, Double>();
	
	@Override
	public void addEvent(double time, String arg) {
		event.put(arg, time);		
		// TODO:
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
	
	// endregion Scheduler
	
	// region ------------------- TCL properties ------------------- //

	private String label;
	private List<Entry> entryList = new ArrayList<Entry>();
	private HashMap<String, InsProc> insProc = new HashMap<String, InsProc>();
	private HashMap<String, InsVar>  insVar = new HashMap<String, InsVar>();
	
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
	
	@Override
	public String getLabel() {
		return label;				
	}

	@Override
	public void setLabel(String label) {
		this.label = label;		
	}

	@Override
	public void addEntry(Entry e) {
		this.entryList.add(e);		
	}

	@Override
	public void addEntry(int index, Entry e) {
		this.entryList.add(index, e);	
	}
	
	@Override
	public List<Entry> getEntry() {
		return entryList;
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
	public void addInsProc(InsProc p) {
		insProc.put(p.insprocName, p);	
	}

	@Override
	public InsProc getInsProc(String key) {
		return insProc.get(key);
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
	}
	
	// endregion InsProc
	// endregion TCL properties
	
	// region ------------------- Transport properties ------------------- //
	
	public void setNode(SNode node) {
		this.node = node;
	}

	@Override
	protected void setName(String name) {
		this.label = name;
	}
	
	@Override
	public String getName() {
		return label;
	}

	@Override
	public HashMap<String, String> getParameters() {
		HashMap<String, String> re = new HashMap<>();
		for (String key : insVar.keySet()) {
			re.put(key, insVar.get(key).getValue());
		}
		return re;
	}

	@Override
	public void setParameters(HashMap<String, String> params) {
		insVar.clear();
		for (String key : params.keySet()) {
			addParameter(key, params.get(key));
		}
	}

	@Override
	public void addParameter(String param, String value) {
		insVar.put(param, new InsVar(param, value));
	}

	@Override
	public String getValue(String param) {
		return getInsVar(param).toString();
	}

	public void addApp(SApplicationProtocol app) {
		this.appList.add(app);
	}
	
	@Override
	public SApplicationProtocol addApp(ApplicationProtocolType type, String name, Node destNode) {
		SNode dest = (SNode)destNode;
		
		SApplicationProtocol app = new SApplicationProtocol(type, name, this, destNode);
		String label =  type + "_(" + this.node.getId() + "_" + this.node.getTransportPrototolList().size() + ")";
		app.setLabel("$" + label);

		addApp(app);		
			
		// region ------------------- Generate Tcl code ------------------- //

		int index = Converter.generateEntry.lastIndexOf(getEntry().get(getEntry().size() - 1));

		// space
		Entry e = new Entry("\n");
		Converter.generateEntry.add(index + 1, e);
		app.addEntry(e);
		dest.addEntry(e);
		this.addEntry(e);		
		this.entryList.add(e);
		this.node.addEntry(e);

		// set sink_($i) [new Agent/Null]
		e = new Entry("set sink_(" + dest.getId() + ") [new Agent/Null]\n");
		Converter.generateEntry.add(index + 2, e);
		app.addEntry(e);
		dest.addEntry(e);
		this.addEntry(e);		
		this.entryList.add(e);
		this.node.addEntry(e);
		
		
		// $ns_ attach-agent $mnode_($d($i)) $sink_($i)
		e = new Entry(Converter.global.getNetwork().getLabel() + " attach-agent " + dest.getLabel() + " $sink_(" + dest.getId() + ")\n");
		Converter.generateEntry.add(index + 3, e);
		app.addEntry(e);
		dest.addEntry(e);
		this.addEntry(e);		
		this.entryList.add(e);
		this.node.addEntry(e);
		
		// $mnode_($s($i)) setdest [$mnode_($d($i)) set X_] [$mnode_($d($i)) set Y_] 0
		e = new Entry(this.node.getLabel() + " setdest [" + dest.getLabel() + " set X_] [" + dest.getLabel() + " set Y_] 0\n");
		Converter.generateEntry.add(index + 4, e);
		app.addEntry(e);
		dest.addEntry(e);
		this.addEntry(e);		
		this.entryList.add(e);
		this.node.addEntry(e);
		
		// $ns_ connect $udp_($i) $sink_($i)
		e = new Entry(Converter.global.getNetwork().getLabel() + " connect " + this.getLabel() + " " + "$sink_(" + dest.getId() + ")\n");
		Converter.generateEntry.add(index + 5, e);
		app.addEntry(e);
		dest.addEntry(e);
		this.addEntry(e);		
		this.entryList.add(e);
		this.node.addEntry(e);
		
		// space
		e = new Entry("\n");
		Converter.generateEntry.add(index + 6, e);
		app.addEntry(e);
		dest.addEntry(e);
		this.addEntry(e);		
		this.entryList.add(e);
		this.node.addEntry(e);
		
		// set cbr_($i) [new Application/Traffic/CBR]
		e = new Entry("set " + label + " [new Application/Traffic/" + type + "]\n");
		Converter.generateEntry.add(index + 7, e);
		app.addEntry(e);
		dest.addEntry(e);
		this.addEntry(e);		
		this.entryList.add(e);
		this.node.addEntry(e);
		
		// $cbr_($i) attach-agent $udp_($i)
		e = new Entry(app.getLabel() + " attach-agent " + this.getLabel() + "\n");
		Converter.generateEntry.add(index + 8, e);
		app.addEntry(e);
		dest.addEntry(e);
		this.addEntry(e);		
		this.entryList.add(e);
		this.node.addEntry(e);
		
		// endregion Generate Tcl code
		
		return app;
	}

	@Override
	public boolean removeApp(ApplicationProtocol app) {
		if (!getAppList().contains(app)) return false;
		
		getAppList().remove(app);
		for (Entry e : ((SApplicationProtocol)app).getEntry()) {
			Converter.generateEntry.remove(e);
		}
		
		return true;
	}

	// endregion Transport properties	
}
