package controllers.synchronizer.shadow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import controllers.synchronizer.Synchronizer;
import controllers.synchronizer.Scheduler;
import controllers.synchronizer.TclObject;
import models.converter.Entry;
import models.converter.InsProc;
import models.converter.InsVar;
import models.converter.ParseException;
import models.networkcomponents.Node;
import models.networkcomponents.events.Event;
import models.networkcomponents.events.Event.EventType;
import models.networkcomponents.protocols.ApplicationProtocol;
import models.networkcomponents.protocols.TransportProtocol.TransportProtocolType;

/**
 * Shadow Object class of ApplicationProtocol class. 
 * @author Trongnguyen
 *
 */
public class SApplicationProtocol extends ApplicationProtocol implements TclObject, Scheduler 
{	
	/**
	 * Create new Shadow Application Protocol.
	 * @param label label for new object.
	 */
	public SApplicationProtocol(final String label) 
	{
		super(ApplicationProtocolType.valueOf(label));
		this.label = label;
		addInsProc();
	}
	
	/**
	 * Create new Shadow Application Protocol.
	 * @param type type of Application
	 * @param name name of Application
	 * @param tp Transport protocol
	 * @param destNode destination node
	 */
	public SApplicationProtocol(final ApplicationProtocolType type, final String name, STransportProtocol tp, Node destNode) 
	{
		super(type);		
		setDestNode(destNode);
		addInsProc();
	}
	
	// region ------------------- Scheduler ------------------- //
	
	/**
	 * event table.
	 */
	private HashMap<String, Double> event = new HashMap<String, Double>();
	
	@Override
	public final void addEvent(final double time, String arg) 
	{
		event.put(arg, time);		
	}

	@Override
	public final double getEvent(final String arg) 
	{
		return event.get(arg);
	}
	
	@Override
	public final HashMap<String, Double> getEvent() 
	{
		return event;
	}
	
	// endregion Scheduler
	
	// region ------------------- TCL properties ------------------- //	
	
	/**
	 * label to show in tcl code
	 */
	private String label;
	
	/**
	 * list of entry element in tcl code that have concern with this.
	 */
	private List<Entry> entryList = new ArrayList<Entry>();
	
	/**
	 * table of insProc.
	 */
	private HashMap<String, InsProc> insProc = new HashMap<String, InsProc>();
	
	/**
	 * table of insVar.
	 */
	private HashMap<String, InsVar>  insVar = new LinkedHashMap<String, InsVar>();
		
	@Override
	public final String parse(List<String> command, boolean isRecord) throws Exception {
		if (command.isEmpty()) throw new ParseException(ParseException.MissArgument);
		
		InsProc proc = insProc.get(Synchronizer.parseIdentify(command.get(0)));
		if (proc != null)
		{
			command.remove(0);
			return proc.Run(command, isRecord);
		} 
		else 
		{
			return insProc.get(null).Run(command, isRecord);
		}
	}

	@Override
	public final void addEntry(final Entry e) {
		entryList.add(e);	
		if (transportProtocol != null) transportProtocol.addEntry(e);
	}
	
	@Override
	public final void addEntry(final int index, final Entry e) {
		entryList.add(index, e);
		if (transportProtocol != null) transportProtocol.addEntry(e);
	}
	
	@Override
	public final List<Entry> getEntry() {
		return entryList;
	}
	
	@Override
	public final String getLabel() {
		return label;
	}

	@Override
	public final void setLabel(final String label) {
		this.label = label;	
	}
	
	// region ------------------- InsVar ------------------- //
	
	@Override
	public final HashMap<String, InsVar> getInsVar() {
		return insVar;
	}
	
	@Override
	public final InsVar getInsVar(String key) {
		return insVar.get(key);
	}

	@Override
	public final InsVar setInsVar(String key, String value) {		
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
	public final InsVar setInsVar(String key, String value, String label) {
		InsVar i = new InsVar(value, label);
		insVar.put(key, i);
		return i;
	}

	// endregion InsVar

	// region ------------------- InsProc ------------------- //

	@Override
	public final InsProc getInsProc(String key) {
		return insProc.get(key);
	}
	
	@Override
	public final void addInsProc(InsProc p) {
		insProc.put(p.insprocName, p);
	}
	
	/**
	 * create and add default InsProc.
	 */
	protected final void addInsProc()	{
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
		
		new InsProc(this, "attach-agent")
		{
			@Override
			protected String run(List<String> command) throws Exception {
				insProcAttachAgent(command);
				return "";
			}
		};
	}
	
	/**
	 * Implement of "AttachAgent" insProc.
	 * @param command command of task
	 * @throws Exception Parse Tcl command exception
	 */
	private void insProcAttachAgent(List<String> command) throws Exception
	{
		if (command.size() != 1) throw new ParseException(ParseException.InvalidArgument);
		STransportProtocol tp = (STransportProtocol) Synchronizer.global.getObject(Synchronizer.parseIdentify(command.get(0)));;
		tp.addApp(this);
		this.transportProtocol = tp;
	}
	
	// endregion field
	
	// endregion

	// region ------------------- Application properties ------------------- //
	
	/**
	 * transportProtocol.
	 */
	STransportProtocol transportProtocol;
	
	@Override
	public final String getName() {
		return label;
	}

	@Override
	public final HashMap<String, String> getParameters() {
		HashMap<String, String> re = new HashMap<>();
		for (String key : insVar.keySet()) {
			re.put(key, insVar.get(key).getValue());
		}
		return re;
	}

	@Override
	public final void setParameters(final HashMap<String, String> params) {
		insVar.clear();
		for (String key : params.keySet()) {
			addParameter(key, params.get(key));
		}
	}

	@Override
	public final void addParameter(final String param, final String value) {
		insVar.put(param, new InsVar(param, value));
		
		// generate tcl code here
		int	index = Synchronizer.getGenerateEntry().lastIndexOf(this.getEntry().get(this.getEntry().size() - 1)) + 1;
		
		Entry e = new Entry(getInsProc("set"), Arrays.asList(param + "", value + ""));
		Synchronizer.registerEntry(index, e);
		this.addEntry(e);
	}

	@Override
	public final String getValue(final String param) {
		return getInsVar(param).toString();
	}

	@Override
	public final boolean removeEvent(final Event e) {						
		return event.remove(e) != null;		
	}

	@Override
	public final boolean addEvent(final EventType type, final double raisedTime) {		
		event.put(type.toString(), raisedTime);

		// generate tcl code here
		int	index = Synchronizer.getGenerateEntry().lastIndexOf(this.getEntry().get(this.getEntry().size() - 1)) + 1;
		
		//	$ns_ at 100 "$cbr_($i) start"
		Entry e = new Entry(Synchronizer.global.getNetwork().getLabel() + " at " + raisedTime + " \"" + this.getLabel() + " " + type.toString().toLowerCase() + "\"\n");
		Synchronizer.registerEntry(index, e);
		this.addEntry(e);
		
		return true;
	}

	@Override
	public final List<Event> getEventList() {
		List<Event> re = new ArrayList<Event>();
		for (String key : event.keySet()) {
			try {
				re.add(new Event(EventType.valueOf(key), event.get(key)));
			} 
			catch(Exception e) {}
		}
		return re;
	}

	@Override
	public final Node getDestNode() {
		if (transportProtocol == null) return null;
		List<ApplicationProtocol> l = transportProtocol.getAppList();
		
		if (l.isEmpty()) return null;
		return transportProtocol.getConnected().getNode();
	}

	@Override
	protected final void setDestNode(final Node node) {
		SNode destNode = (SNode) node;
		
		// connect base TransportProtocol to destNode's TransportProtocol
		if (transportProtocol != null)
		{
			// Add a Agent/Null to destNode and connect this to transportProtocol
			STransportProtocol newSink = destNode.addTransportProtocol(TransportProtocolType.Null, destNode.getName() + "_sink");						
			newSink.setLabel("$sink(" + destNode.getId() + destNode.getTransportPrototolList().size() + ")");
			transportProtocol.setConnected(newSink);			
			
			// region -------------------  generate Tcl code ------------------- //

			// find last index of entry in the global register			
			int	index = Math.max(Math.max(
					Synchronizer.getGenerateEntry().lastIndexOf(this.getEntry().get(this.getEntry().size() - 1)),
					Synchronizer.getGenerateEntry().lastIndexOf(destNode.getEntry().get(destNode.getEntry().size() - 1))),					
					Synchronizer.getGenerateEntry().lastIndexOf(transportProtocol.getEntry().get(transportProtocol.getEntry().size() - 1)));
							
			// 	$ns_ connect $udp_($i) $sink_($i)
			Entry en = new Entry(Synchronizer.global.getLabel() + " connect " + transportProtocol.getLabel() + " " + newSink.getLabel() + "\n"); 
			Synchronizer.registerEntry(index + 1, en);
			this.addEntry(en);
			transportProtocol.addEntry(en);
			destNode.addEntry(en);
			
			// 	$mnode_($s($i)) setdest [$mnode_($d($i)) set X_] [$mnode_($d($i)) set Y_] 0
			en = new Entry(transportProtocol.getNode().getLabel() + " setdest [" + destNode.getLabel() + "set X_] [" + destNode.getLabel() + " set Y_] 0\n"); 
			Synchronizer.registerEntry(index + 1, en);
			this.addEntry(en);
			transportProtocol.addEntry(en);
			destNode.addEntry(en);
			
			// endregion generate Tcl code
		}
	}
	
	// endregion Application properties
}
