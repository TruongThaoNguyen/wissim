package controllers.converter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import models.converter.Entry;
import models.converter.InsProc;
import models.converter.InsVar;
import models.converter.ParseException;
import models.networkcomponents.Node;
import models.networkcomponents.protocols.ApplicationProtocol;

public class SApplicationProtocol extends ApplicationProtocol implements TclObject {
	

	private String label;
	private List<Entry> entryList = new ArrayList<Entry>();
	private HashMap<String, InsProc> insProc = new HashMap<String, InsProc>();
	private HashMap<String, InsVar>  insVar = new HashMap<String, InsVar>();
	private HashMap<String, Double> event = new HashMap<String, Double>();

	public SApplicationProtocol() {
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
	public void setEntry(Entry e) {
		entryList.add(e);	
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
			i.Value = value;
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

			@Override
			public String print(List<String> command) {
				StringBuilder sb = new StringBuilder();
				for (String s : command) {
					sb.append(s);
				}
				return sb.toString();
			}			
		};

		new InsProc(this, "set") {
			@Override
			protected String run(List<String> command) throws Exception {				
				switch (command.size()) 
				{
					case 0 : throw new ParseException(ParseException.MissArgument);
					case 1 : return getInsVar(Converter.parseIdentify(command.get(0))).Value;
					case 2 : return setInsVar(Converter.parseIdentify(command.get(0)), Converter.parseIdentify(command.get(1)), command.get(1)).Value;
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
	
	private void insProcAttachAgent(List<String> command) throws Exception
	{
		if (command.size() != 1) throw new ParseException(ParseException.InvalidArgument);
		STransportProtocol tp = (STransportProtocol) Converter.global.getObject(Converter.parseIdentify(command.get(0)));;
		tp.addApp(this);	
	}
	
	// endregion field
	
	// endregion

	// region ------------------- Application properties ------------------- //
	
	@Override
	public Node getDestNode() {				
		try
		{
			return ((STransportProtocol)getTransportProtocol()).getConnected().getNode(); 
		}
		catch (Exception e)
		{
			return null;
		}
	}
	
	// endregion Application properties
}
