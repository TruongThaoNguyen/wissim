package controllers.synchronizer.shadow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import controllers.synchronizer.Synchronizer;
import controllers.synchronizer.TclObject;
import models.converter.Entry;
import models.converter.InsProc;
import models.converter.InsVar;
import models.converter.ParseException;

/**
 * UndefineObject - Tcl Shadow object with undefined type. 
 */
public class SCommonObject implements TclObject
{	
	/**
	 * label of this object.
	 */
	private String label;
	
	/**
	 * entry list of this object.
	 */
	private List<Entry> entryList = new ArrayList<Entry>();
	
	/**
	 * insProc list.
	 */
	private HashMap<String, InsProc> insProc  = new HashMap<String, InsProc>();	
	
	/**
	 * insVar list.
	 */
	private HashMap<String, InsVar>  insVar  = new LinkedHashMap<String, InsVar>();
	
	/**
	 * parent object of this object.
	 */
	private TclObject parent;
	
	/**
	 * Create new Shadow Common Object.
	 * @param label Value and label
	 */
	public SCommonObject(final String label)
	{
		this.label = label;		
		addInsProc();
	}

	/**
	 * create new CommonObject.
	 * @param value
	 * @param insVar
	 */
	public SCommonObject(final String label, HashMap<String, String> var)
	{
		this.label = label;
		
		if (var != null)
			for (String key	: var.keySet())
			{
				this.insVar.put(key, new InsVar(var.get(key)));	
			}	
		
		addInsProc();
	}
	
	/**
	 * set parent for this object.
	 * @param p parent object
	 */
	public void setParent(TclObject p)
	{
		parent = p;
	}
	
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
		entryList.add(e);	
		if (parent != null) parent.addEntry(e);
	}
	
	@Override
	public void addEntry(int index, Entry e) {
		entryList.add(index, e);
		if (parent != null) parent.addEntry(e);
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
	public InsProc getInsProc(String key) {
		return insProc.get(key);
	}
	
	@Override
	public void addInsProc(InsProc p) {
		insProc.put(p.insprocName, p);
	}
	
	/**
	 * add default insProcs.
	 */
	protected void addInsProc() {				
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
}
