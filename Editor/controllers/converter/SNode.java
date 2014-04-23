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
	private String label;
	private List<Entry> entryList = new ArrayList<Entry>();
	private HashMap<String, InsProc> insProc = new HashMap<String, InsProc>();
	private HashMap<String, InsVar>  insVar = new HashMap<String, InsVar>();
	private HashMap<String, Double> event = new HashMap<String, Double>();
	
	public SNode(SNetwork network) {
		super(network);
		setX(0);
		setY(0);
		addInsProc();
	}
	
	public SNode(SNetwork network, int x, int y, int range)
	{
		super(network, x, y, range);
		setX(x);
		setY(y);
		addInsProc();
	}

	// region ------------------- TCL properties ------------------- //
	
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

	// endregion Wireless Node properties
	
	// region ------------------- Wireless Node properties ------------------- //

	void addTransportProtocol(TransportProtocol tp)
	{
		getTransportProtocolList().add(tp);
	}
	
	@Override
	public void addTransportProtocol(int type, String name) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean removeTransportProtocol(TransportProtocol transproc) {
		// TODO Auto-generated method stub
		return false;
	}

	// endregion Wireless Node properties	
}
