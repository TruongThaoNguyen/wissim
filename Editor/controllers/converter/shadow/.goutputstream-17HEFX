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
import models.networkcomponents.protocols.TransportProtocol;

public class STransportProtocol extends TransportProtocol implements TclObject, Scheduler {	
	
	protected STransportProtocol(String label) {				
		super(-1, label, null);
		this.label = label;
		
		if (label.equals("UDP")) setType(TransportProtocol.UDP);
		if (label.equals("TCP")) setType(TransportProtocol.TCP);
		
		addInsProc();
	}	

	public STransportProtocol(int type, String label, SNode node) {
		super(type, label, node);
		this.label = label;
		
		addInsProc();
	}

	
	private TransportProtocol connectedAgent = null;
	
	public void setConnected(STransportProtocol agent) {
		connectedAgent = agent;
	}
	
	public TransportProtocol getConnected() {
		return connectedAgent;
	}
	
	// region ------------------- Scheduler ------------------- //
	
	private HashMap<String, Double> event = new HashMap<String, Double>();
	
	@Override
	public void addEvent(Double time, String arg) {
		event.put(arg, time);		
		// TODO:
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

	public void addApp(SApplicationProtocol sApplicationProtocol) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void addApp(int type, String name, Node destNode) {
		SApplicationProtocol newApp = new SApplicationProtocol(type, name, this, destNode);
		this.appList.add(newApp);
			
		// TODO Auto-generated Tcl code
		
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
