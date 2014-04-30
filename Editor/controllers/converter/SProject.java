package controllers.converter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import controllers.WorkSpace;
import models.Project;
import models.converter.Entry;
import models.converter.InsProc;
import models.converter.InsVar;
import models.converter.ParseException;
import models.converter.Token;
import models.converter.TokenType;
import models.networkcomponents.WirelessNetwork;

/**
 * GlobalObject.java
 * @Copyright (C) 2014, Sedic Laboratory, Hanoi University of Science and Technology
 * @Author Duc-Trong Nguyen
 * @Version 2.0
 */
public class SProject  extends Project implements TclObject
{
	private HashMap<String, TclObject>	insObj = new HashMap<String, TclObject>();
	private List<Entry> entryList = new ArrayList<Entry>();
	private HashMap<String, InsProc> insProc = new HashMap<String, InsProc>();
	private HashMap<String, InsVar>  insVar = new HashMap<String, InsVar>();
	private HashMap<String, Double> event = new HashMap<String, Double>(); 
	
	private SNetwork network;
	
	public SProject()
	{
		super();
		network = new SNetwork("_o0");
		insObj.put("_o0", (TclObject) network);		
		
		addInsProc();

		//		entryList = new ArrayList<Entry>();		
	}	
	
	// region ------------------- TCL feature ------------------- //	
	
	// region ------------------- Parse ------------------- //

	/**
	 * parse Tcl codes. Recored code to register.
	 * @param code Tcl code
	 * @throws ParseException
	 */
	public void parse(String code) throws ParseException {
		parse(code, true);
	}
	
	/**
	 * parse Tcl code.
	 * @param code Tcl code.
	 * @param isRecord Record to Register or not.
	 * @throws ParseException
	 */
	public void parse(String code, boolean isRecord) throws ParseException {
		Scanner scanner = new Scanner(code);

		while (scanner.haveNext())
		{			
			List<String> command = scanner.scanCommand();
			
			System.out.print((scanner.getLine() + 1) + ":\t");
			for (String word : command) System.out.print(" " + word);			
			
			try 
			{
				parse(command, isRecord);
			}
			catch (ParseException e)
			{				
				e.setLine(e.getLine() + scanner.getLine());				
				System.out.println("Line " + e.getLine() + " " + e.getMessage());
				throw e;
			}
			catch (Exception e) 
			{					
				throw new ParseException(scanner.getLine(), e.getMessage());
			}			 			
		}
	}

	@Override
	public String parse(List<String> command, boolean isRecord) throws Exception {
		
		if (command.isEmpty()) 	return null;		
		if (command.size() < 2)	return getInsProc(null).Run(command, isRecord);					
		
		String arg = Converter.parseIdentify(command.get(0));
		
		if (arg == null) return getInsProc(null).Run(command, isRecord);  
		
		InsProc p = getInsProc(arg);
		if (p != null)
		{
			command.remove(0);
			return p.Run(command, isRecord);
		}
	
		TclObject obj = insObj.get(arg);
		if (obj != null)	
		{
			command.remove(0);
			return obj.parse(command, isRecord);
		}
		
		obj = new SCommonObject(arg);
		p = obj.getInsProc(Converter.parseIdentify(command.get(1)));
		if (p != null)
		{			
			insObj.put(arg, obj);
			command.remove(0);
			command.remove(0);
			return p.Run(command,isRecord);
		}
		
		// Undefined insProc 
		return getInsProc(null).Run(command, isRecord);
	}
	
	// endregion Parse

	@Override
	public void addEvent(Double time, String arg) {
		event.put(arg, time);		
	}
	
	// region ------------------- Label ------------------- //

	@Override
	public String getLabel() {
		return null;
	}
	
	@Override
	public void setLabel(String label) {
		// Do nothing
	}
	
	// endregion Label
	
	// region ------------------- Object list ------------------- //

	private static int newObjectID = 1;
	
	/**
	 * get new Identify name for new Object.
	 * @return new ID
	 */
	private String newIndentify() 
	{
		while (insObj.containsKey("_o" + newObjectID)) newObjectID++;
		return "_o" + newObjectID;
	}

	public String addObject(TclObject obj)
	{
		String id = newIndentify();
		insObj.put(id, obj);
		obj.setLabel(id);
		return id;
	}

	public TclObject getObject(String key)
	{
		return insObj.get(key);
	}
	
	public TclObject removeObject(String key)
	{
		return insObj.remove(key);
	}
	
	public TclObject removeObject(Object obj)
	{
		for (String s : insObj.keySet())
			if (insObj.get(s) == obj)
				return insObj.remove(s);
		return null;
	}
	
	// endregion Object list
	
	// region ------------------- Generate Entry ------------------- //

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

	// endregion Generate Entry
	
	// region ------------------- InsVar ------------------- //
	
	@Override
	public HashMap<String, InsVar> getInsVar() {
		return insVar;
	}
	
	@Override
	public InsVar getInsVar(String key) 
	{
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
		if (insObj.containsKey(value)) getObject(value).setLabel("$" + key);
		return i;
	}
	
	@Override
	public InsVar setInsVar(String key, String value, String label) {
		InsVar i = new InsVar(value, label);		
		insVar.put(key, i);
		if (insObj.containsKey(value)) getObject(value).setLabel("$" + key);
		return i;
	}

	// endregion InsVar	
	
	// region ------------------- InsProc ----------------- //	
		
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
			public String print(List<String> command)
			{
				String id;
				try
				{
					id = Converter.parseIdentify(command.get(0));
				}
				catch (Exception e)
				{
					id = command.get(0);
				}
				
				InsVar arg = parent.getInsVar(id);
				return command.get(0) + " " + arg;
			}			
		};
			
		new InsProc(this, "new") {
			@Override
			protected String run(List<String> command) throws Exception {
				if (command.size() == 0)	throw new ParseException(ParseException.MissArgument);
				if (command.size() >  1)	throw new ParseException(ParseException.InvalidArgument);
				
				String arg = Converter.parseIdentify(command.get(0));
				
				if (arg.equals("Simulator")) 				return "_o0";
				if (arg.equals("Topography"))				return addObject(new STopographyObject("[new " + arg + "]"));		
				if (arg.startsWith("Application/Traffic/"))	return addObject(new SApplicationProtocol(arg.replace("Application/Tracffic/", ""))); 
				if (arg.startsWith("Agent/"))				return addObject(new STransportProtocol(arg.replace("Agent/",  "")));
				
				return addObject(new SCommonObject("[new " + arg + "]"));
			}		
		};
	
		new InsProc(this, "open") {
			@Override
			protected String run(List<String> command) throws Exception {
				if (command.size() == 0)	throw new ParseException(ParseException.MissArgument);
				if (command.size() > 2) 	throw new ParseException(ParseException.InvalidArgument);
				
				String arg = Converter.parseIdentify(command.get(1));
				if (!arg.equals("w") && !arg.equals("r")) throw new ParseException(ParseException.InvalidArgument);
				
				return "[open " + Converter.parseIdentify(command.get(0)) + " " + command.get(1) + "]";
			}	
		};
	
		new InsProc(this, "for") {
			@Override
			protected String run(List<String> command) throws Exception {
				return insprocFor(command);
			}
			
			@Override
			public String Print(List<String> command) {
				StringBuilder sb = new StringBuilder();
				sb.append("# for {" + command.get(0) + "} {" + 
									  command.get(1) + "} {" + 
									  command.get(2) + "} {" + 
									  command.get(3).replace("\n", "\n# ") + "}");
				return sb.toString();
			}
		};

		new InsProc(this, "incr") {
			@Override
			protected String run(List<String> command) throws Exception {				
				if (command.size() != 1) throw new ParseException(ParseException.InvalidArgument);				
				InsVar i = insVar.get(Converter.parseIdentify(command.get(0)));				
				return i.setValue(Integer.parseInt(i.getValue()) + 1, false);				
			}	
		};
		
		new InsProc(this, "expr") {
			@Override
			protected String run(List<String> command) throws Exception {
				return insprocExpr(command);
			}
		};

		new InsProc(this, "source") {
			private BufferedReader br;
			@Override
			protected String run(List<String> command) throws Exception {
				if (command.size() != 1) throw new ParseException(ParseException.InvalidArgument);				
							
				String fileName =  WorkSpace.getDirectory() + Converter.parseIdentify(command.get(0));				
				br = new BufferedReader(new FileReader(fileName));
				StringBuilder sb = new StringBuilder();
			    String line = br.readLine();
			    while (line != null) {
			        sb.append(line);
			        sb.append(System.lineSeparator());
			        line = br.readLine();
			    }		    
				
			    parse(sb.toString());
				return null;
			}
			
			@Override
			public String Print(List<String> command) {
				return "# source " + command.get(0);
			}
		};
	}

	private int checkOperator(String s)	{
		if (s.equals("*") || s.equals("/") || s.equals("%")) return 6;
		if (s.equals("+") || s.equals("-")) return 5;
		if (s.equals("<") || s.equals(">") || s.equals("<=") || s.equals(">=")) return 4;
		if (s.equals("==") || s.equals("!=")) return 3;
		if (s.equals("&&")) return 2;
		if (s.equals("||")) return 1;
		return 0;
	}
		
	private String insprocExpr(List<String> command) throws Exception {
		Stack<String> stack = new Stack<String>();
		List<String> prefix = new ArrayList<String>();		
		
		// convert to prefix
		for (String c : command)
		{
			Scanner scanner = new Scanner(c);
			for (String s : scanner.scanOperator())
			{				
				int i = checkOperator(s);
				if (i > 0)
				{									
					while (!stack.isEmpty() && checkOperator(stack.peek()) >= i)
					{
						prefix.add(stack.pop());
					}
					stack.push(s);
				}
				else
				{
					prefix.add(Converter.parseIdentify(s));
				}
			}
		}		
		while (!stack.isEmpty()) 
		{
			prefix.add(stack.pop());
		}
		
		// calculate value
		while (!prefix.isEmpty())
		{
			String i = prefix.remove(0);
			if (checkOperator(i) > 0)
			{
				String b = stack.pop();
				String a = stack.pop();				
				
				switch (i) 
				{
					case "*" : stack.push((Double.parseDouble(a) *  Double.parseDouble(b)) + ""); break;
					case "/" : stack.push((Double.parseDouble(a) /  Double.parseDouble(b)) + ""); break;
					case "%" : stack.push((Double.parseDouble(a) %  Double.parseDouble(b)) + ""); break;
					case "+" : stack.push((Double.parseDouble(a) +  Double.parseDouble(b)) + ""); break;
					case "-" : stack.push((Double.parseDouble(a) -  Double.parseDouble(b)) + ""); break;
					case "<" : stack.push((Double.parseDouble(a) <  Double.parseDouble(b)) + ""); break;
					case ">" : stack.push((Double.parseDouble(a) >  Double.parseDouble(b)) + ""); break;
					case "<=": stack.push((Double.parseDouble(a) <= Double.parseDouble(b)) + ""); break;
					case ">=": stack.push((Double.parseDouble(a) >= Double.parseDouble(b)) + ""); break;
					case "==": stack.push((Double.parseDouble(a) == Double.parseDouble(b)) + ""); break;
					case "!=": stack.push((Double.parseDouble(a) != Double.parseDouble(b)) + ""); break;
					case "&&": stack.push(((Double.parseDouble(a) == 0 || Boolean.parseBoolean(a)) && 
										   (Double.parseDouble(b) == 0 || Boolean.parseBoolean(b)))	+ ""); break;
					case "||": stack.push(((Double.parseDouble(a) == 0 || Boolean.parseBoolean(a)) || 
							   			   (Double.parseDouble(b) == 0 || Boolean.parseBoolean(b)))	+ ""); break;							
				}
			}
			else
			{
				stack.push(i);
			}
		}
		
		return stack.pop();
	}

	private String insprocFor(List<String> command) throws Exception {
		if (command.size() != 4) throw new ParseException(ParseException.InvalidArgument);
		
		String[] arg = new String[4];
		
		for (int i = 0; i < 4; i++)
		{
			Scanner scanner = new Scanner(command.get(i));
			List<Token> token = scanner.scanWord();
			if (scanner.haveNext() || token.size() != 1 || token.get(0).Type != TokenType.Brace) 
				throw new ParseException(ParseException.InvalidArgument);
			arg[i] = token.get(0).Value;
		}
		
		Scanner scanner = new Scanner(arg[1]);
		List<String> arg1 = scanner.scanCommand();
		if (scanner.haveNext()) throw new ParseException(ParseException.InvalidArgument);
		
		int limit = 10000;
		parse(arg[0], true);
		while (Boolean.parseBoolean(insprocExpr(new ArrayList<String>(arg1))) && limit > 0)			
		{			
			parse(arg[3], true);			
			parse(arg[2], true);
			limit--;
		}
		
		
		Converter.generateEntry.add(new Entry("\n"));
		return "";
	}

	// endregion
	
	// endregion TCL feature
	
	// region ------------------- Project feature ------------------- //
	
	@Override
	public WirelessNetwork getNetwork() {
		return network;
	}
	
	// region ------------------- Configure ------------------- //
	
	// region ------------------- Set ------------------- //

	@Override public void setSelectedChannel(String selected) {	network.setInsVar("-channel", "[new " + selected + "]\n"); }
		
	@Override public void setQueueLength		(int 	value) { network.setInsVar("-ifqLen", value + ""); }
	@Override public void setIddleEnergy		(double value) { network.setInsVar("-energyModel", "EnergyModel"); network.setInsVar("-idlePower", 		value + ""); }
	@Override public void setReceptionEnergy	(double value) { network.setInsVar("-energyModel", "EnergyModel"); network.setInsVar("-rxPower", 		value + ""); }
	@Override public void setSleepEnergy		(double value) { network.setInsVar("-energyModel", "EnergyModel"); network.setInsVar("-sleepPower", 	value + ""); }
	@Override public void setTransmissionEnergy	(double value) { network.setInsVar("-energyModel", "EnergyModel"); network.setInsVar("-transitionPower",value + ""); }
	@Override public void setInitialEnergy		(double value) { network.setInsVar("-energyModel", "EnergyModel"); network.setInsVar("-initialEnergy", 	value + ""); }
	
	@Override public void setSelectedRoutingProtocol	(String selected) {	setConfig("-adhocRouting",	selected); }
	@Override public void setSelectedLinkLayer			(String selected) { setConfig("-llType", 		selected); }
	@Override public void setSelectedMac				(String selected) {	setConfig("-macType", 		selected); }
	@Override public void setSelectedInterfaceQueue		(String selected) {	setConfig("-ifqType", 		selected); }
	@Override public void setSelectedAntenna			(String selected) {	setConfig("-antType", 		selected); }
	@Override public void setSelectedPropagationModel	(String selected) {	setConfig("-propType", 		selected); }	
	@Override public void setSelectedNetworkInterface	(String selected) { setConfig("-phyType", 		selected); }	
	
	private void setConfig(String label, String selected) {
		// check current selected routing protocol
		InsVar insVar = network.getInsVar(label);
		if (insVar == null || !insVar.getValue().equals(selected))
		{
			network.setInsVar(label, selected);

			// add new tcl code for new routing protocol
			SCommonObject newObj = new SCommonObject(selected, getRoutingProtocols().get(selected));
			insObj.put(selected, newObj);			
			
			int index = 0;
			for (String key : newObj.getInsVar().keySet()) 
			{
				Entry newEntry = new Entry(key + " " + newObj.getInsVar(key) + "\n"); 
				addEntry(index++, newEntry);
				newObj.addEntry(newEntry);
			}
		}
	}

	// endregion Set	
	
	// region ------------------- get ------------------- //

	@Override public String getSelectedChannel() 	{
		String v = network.getInsVar("-channel").getValue();
		if (v.startsWith("[new")) return v.substring(5, v.length() - 7);
		return v;
	}
	
	@Override public int 	getQueueLength() 		{ return Integer.parseInt  (network.getInsVar("-ifqLen"			).getValue()); }
	@Override public double getSleepEnergy() 		{ return Double.parseDouble(network.getInsVar("-sleepPower"		).getValue()); }
	@Override public double getTransmissionEnergy() { return Double.parseDouble(network.getInsVar("-transitionPower").getValue()); }
	@Override public double getIddleEnergy() 		{ return Double.parseDouble(network.getInsVar("-idlePower"		).getValue()); }
	@Override public double getReceptionEnergy() 	{ return Double.parseDouble(network.getInsVar("-rxPower"		).getValue()); }
	@Override public double getInitialEnergy()		{ return Double.parseDouble(network.getInsVar("-initialEnergy"	).getValue()); }
	
	@Override public String getSelectedRoutingProtocol() 	{ return network.getInsVar("-adhocRouting"	).getValue(); }
	@Override public String getSelectedLinkLayer()			{ return network.getInsVar("-llType"		).getValue(); }
	@Override public String getSelectedMac() 				{ return network.getInsVar("-macType"		).getValue(); }
	@Override public String getSelectedPropagationModel()	{ return network.getInsVar("-propType"		).getValue(); }
	@Override public String getSelectedAntenna() 			{ return network.getInsVar("-antType"		).getValue(); }
	@Override public String getSelectedInterfaceQueue() 	{ return network.getInsVar("-ifqType"		).getValue(); }
	@Override public String getSelectedNetworkInterface() 	{ return network.getInsVar("-phyType"		).getValue(); }
	
	// endregion get
	// endregion Configre

	// endregion Project feature
}
