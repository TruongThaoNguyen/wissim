package controllers.synchronizer.shadow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import controllers.synchronizer.Synchronizer;
import controllers.synchronizer.Scheduler;
import controllers.synchronizer.TclObject;
import models.converter.Entry;
import models.converter.InsProc;
import models.converter.InsVar;
import models.converter.ParseException;
import models.networkcomponents.Node;
import models.networkcomponents.WirelessNode;
import models.networkcomponents.events.Event;
import models.networkcomponents.events.Event.EventType;
import models.networkcomponents.protocols.TransportProtocol;
import models.networkcomponents.protocols.TransportProtocol.TransportProtocolType;

public class SNode extends WirelessNode implements TclObject, Scheduler {
	
	public SNode(SNetwork network) {
		super(network);
		setInsVar("X_", "0");
		setInsVar("Y_", "0");		
		addInsProc();		
	}
	
	/**
	 * Create new SNode Object.
	 * @param network network that new node belong to
	 * @param x x position
	 * @param y y position
	 */
	public SNode(SNetwork network, int x, int y) {
		super(network);
		setInsVar("X_", x + "");
		setInsVar("Y_", y + "");
		addInsProc();
	}

	// region ------------------- Scheduler ------------------- //
	
	private HashMap<String, Double> event = new HashMap<String, Double>();
	
	@Override
	public void addEvent(double time, String arg) {
		this.event.put(arg, time);
	}

	@Override
	public double getEvent(String arg) {
		return event.get(arg);
	}
	
	@Override
	public HashMap<String, Double> getEvent() {
		return event;
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
		
		InsProc proc = insProc.get(Synchronizer.parseIdentify(command.get(0)));
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
		((SNetwork)network).addEntry(e);
	}
	
	@Override
	public void addEntry(int index, Entry e) {
		this.entryList.add(index, e);
		((SNetwork)network).addEntry(e);
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
	}
	
	// endregion InsProc
	
	// endregion TCL properties
	
	// region ------------------- Wireless Node properties ------------------- //
	
	@Override
	public int getX() {
		return Integer.parseInt(getInsVar("X_").getValue());
	}

	@Override
	public void setX(int x) {
		setInsVar("X_", x + "");
	}

	@Override
	public int getY() {
		return Integer.parseInt(getInsVar("Y_").getValue());
	}

	@Override
	public void setY(int y) {
		setInsVar("Y_", y + "");
	}
	
	public void addTransportProtocol(STransportProtocol agent) {
		transportProtocolList.add(agent);
	}
	
	@Override
	public STransportProtocol addTransportProtocol(TransportProtocolType type, String name) {
		STransportProtocol tp = new STransportProtocol(type, type + "_" + name, this);
		String label = (type + "_(" + name + ")").toLowerCase();
		tp.setLabel("$" +  label);
		
		addTransportProtocol(tp);
		
		// region ------------------- Generate Tcl Code ------------------- //
		
		int index = 0;
		for (Node node : Synchronizer.global.getNetwork().getNodeList()) {
			SNode sn = (SNode) node;
			index = Math.max(index, Synchronizer.getGenerateEntry().lastIndexOf(sn.getEntry().get(sn.getEntry().size() - 1)));
		}	
		index += 1;
		
		// space
		Entry e = new Entry(" \n");
		Synchronizer.registerEntry(index++, e);
		addEntry(e);
		tp.addEntry(e);
		
		// set udp_($i) [new Agent/UDP]
		e = new Entry("set " + label + " [new Agent/" + type + "]\n");
		Synchronizer.registerEntry(index++, e);
		addEntry(e);
		tp.addEntry(e);
		
		// $ns_ attach-agent $mnode_($s($i)) $udp_($i)
		e = new Entry(((SNetwork)Synchronizer.global.getNetwork()).getLabel() + " attach-agent " + getLabel() + " " + tp.getLabel() + "\n");
		Synchronizer.registerEntry(index++, e);
		addEntry(e);
		tp.addEntry(e);		
		
		// endregion Generate Tcl Code
		
		return tp;
	}

	@Override
	public boolean removeTransportProtocol(TransportProtocol transproc) {
		if (!transportProtocolList.contains(transproc)) return false;
		transportProtocolList.remove(transproc);
		
		for (Entry e : ((STransportProtocol)transproc).getEntry()) {
			Synchronizer.getGenerateEntry().remove(e);			
		}
		
		return true;
	}

	@Override
	public int getRange() {
		return Synchronizer.global.getNodeRange();
	}

	@Override
	public List<Event> getEventList() {
		// TODO
		return null;
	}
	
	@Override
	public void addEvent(EventType type, int raiseTime) {
		event.put(type + "", (double) raiseTime);
	}

	@Override
	public void removeEvent(Event event) {
		// TODO Auto-generated method stub
		
	}
	
	// endregion Wireless Node properties	
}
