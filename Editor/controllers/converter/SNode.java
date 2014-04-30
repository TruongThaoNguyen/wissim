package controllers.converter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import models.converter.Entry;
import models.converter.InsProc;
import models.converter.InsVar;
import models.converter.ParseException;
import models.networkcomponents.WirelessNode;
import models.networkcomponents.protocols.TransportProtocol;

public class SNode extends WirelessNode implements TclObject 
{		
	public SNode(SNetwork network) {
		super(network);
		setX(0);
		setY(0);		
		addInsProc();
	}
	
	public SNode(SNetwork network, int x, int y, int range)
	{
		super(network);
		setX(x);
		setY(y);
		this.range = range;
		addInsProc();
	}

	// region ------------------- TCL properties ------------------- //
	
	private String label;
	private List<Entry> entryList = new ArrayList<Entry>();
	private HashMap<String, InsProc> insProc = new HashMap<String, InsProc>();
	private HashMap<String, InsVar>  insVar = new HashMap<String, InsVar>();
	private HashMap<String, Double> event = new HashMap<String, Double>();
	
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
	public void addEvent(Double time, String arg) {
		event.put(arg, time);		
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
	public TransportProtocol addTransportProtocol(int type, String name) {
		
		STransportProtocol tp = new STransportProtocol(type, name, this);
		String id = Converter.global.getNetwork().getNodeList().indexOf(this) + "";
		id += getTransportPrototolList().indexOf(tp);		
		tp.setLabel("trans_(" + id + ")");
		
		addTransportProtocol(tp);
		
		// generate Tcl code		
		int index = Converter.generateEntry.lastIndexOf(getEntry().get(getEntry().size() - 1));
		
		// space
		Converter.generateEntry.add(index + 1, new Entry("\n"));
		
		// set udp_($i) [new Agent/UDP]
		Entry e = new Entry("set " + tp.getLabel() + " [new Agent/" + name + "]\n");
		Converter.generateEntry.add(index + 2, e);
		tp.addEntry(e);
		
		// $ns_ attach-agent $mnode_($s($i)) $udp_($i)
		e = new Entry("$" + ((SNetwork)Converter.global.getNetwork()).getLabel() + " attach-agent $" + getLabel() + " $" + tp.getLabel() + "\n");
		Converter.generateEntry.add(index + 3, e);
		addEntry(e);
		tp.addEntry(e);
		
		// $udp_($i) set fid_ 2
		List<String> cmd = new ArrayList<>();
		cmd.add("set");
		cmd.add("fid_");
		cmd.add("2");
		try {
			tp.parse(cmd, false);
		} catch (Exception e1) {			
			e1.printStackTrace();
		}
						
		// endregion generate auto tcl code
		
		return tp;
	}

	@Override
	public boolean removeTransportProtocol(TransportProtocol transproc) {
		if (!transportProtocolList.contains(transproc)) return false;
		transportProtocolList.remove(transproc);
		
		for (Entry e : ((STransportProtocol)transproc).getEntry()) {
			Converter.generateEntry.remove(e);			
		}
		
		return true;
	}

	// endregion Wireless Node properties	
}
