package controllers.synchronizer.shadow;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import controllers.Configure;
import controllers.synchronizer.Synchronizer;
import controllers.synchronizer.Scanner;
import controllers.synchronizer.TclObject;
import models.Project;
import models.converter.Entry;
import models.converter.InsProc;
import models.converter.InsVar;
import models.converter.ParseException;
import models.converter.Token;
import models.converter.Token.TokenType;

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
			
		//	System.out.print((scanner.getLine() + 1) + ":\t");
		//	for (String word : command) System.out.print(" " + word);			
			
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
		
		String arg = Synchronizer.parseIdentify(command.get(0));
		
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
		
//		obj = new SCommonObject(arg);
//		p = obj.getInsProc(Converter.parseIdentify(command.get(1)));
//		if (p != null)
//		{			
//			insObj.put(arg, obj);
//			command.remove(0);
//			command.remove(0);
//			return p.Run(command,isRecord);
//		}
		
		if (parseConfig(command)) return "";
		
		// Undefined insProc 
		return getInsProc(null).Run(command, isRecord);
	}
	
	private boolean parseConfig(List<String> command)
	{
		for (String key : configure.keySet()) {
			if (configure.get(key).keySet().contains(command.get(0)))
			{
				configure.get(key).get(command.get(0)).put(command.get(2), command.get(3));
				return true;				
			}
		}
		
		return false;
	}
	
	// endregion Parse

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

	@Deprecated
	public TclObject getObjectbyLabel(String label)
	{
		for (String s : insObj.keySet())
		{			
			if (insObj.get(s).getLabel().equals(label)) return insObj.get(s);
		}
		return null;
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
		InsVar i;
		
		if (value.contains("[create-god"))	// check whether this insvar is god?
		{
			i = new InsVar(value, label) {				
				public String toString()
				{
					return "[create-god " + getNetwork().getNodeList().size() + "]";
				}
			};
		}
		else 
		{
			i = new InsVar(value, label);					
		}
				
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
					case 1 : 
						InsVar i = getInsVar(Synchronizer.parseIdentify(command.get(0)));
						if (i != null)	return i.getValue();
						return null;
					case 2 : //return setInsVar(Converter.parseIdentify(command.get(0)), Converter.parseIdentify(command.get(1)), command.get(1)).getValue();
						String arg0 = Synchronizer.parseIdentify(command.get(0));
						String arg1 = Synchronizer.parseIdentify(command.get(1));
						String arg2 = command.get(1);
						return setInsVar(arg0, arg1, arg2).getValue();
					default: throw new ParseException(ParseException.InvalidArgument);
				}
			}

			@Override
			public String print(List<String> command)
			{
				String id;
				try
				{
					id = Synchronizer.parseIdentify(command.get(0));
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
				
				String arg = Synchronizer.parseIdentify(command.get(0));
				
				if (arg.equals("Simulator")) 				return "_o0";
				if (arg.equals("Topography"))				return addObject(new STopographyObject("[new " + arg + "]"));		
				if (arg.startsWith("Application/Traffic/"))	return addObject(new SApplicationProtocol(arg.replace("Application/Traffic/", ""))); 
				if (arg.startsWith("Agent/"))				return addObject(new STransportProtocol(arg.replace("Agent/",  "")));
				
				return addObject(new SCommonObject("[new " + arg + "]"));
			}		
		};
	
		new InsProc(this, "open") {
			@Override
			protected String run(List<String> command) throws Exception {
				if (command.size() == 0)	throw new ParseException(ParseException.MissArgument);
				if (command.size() > 2) 	throw new ParseException(ParseException.InvalidArgument);
				
				String arg1 = Synchronizer.parseIdentify(command.get(0));
				String arg2 = Synchronizer.parseIdentify(command.get(1));				
				
				SCommonObject file = new SCommonObject("");				
				String id = addObject(file);
				file.setLabel("[open " + arg1 + " " + arg2 + "]");
				file.setInsVar("name",	arg1);
				file.setInsVar("arg", 	arg2);
							
				return id;
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
				InsVar i = insVar.get(Synchronizer.parseIdentify(command.get(0)));				
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
							
				String fileName =  Configure.getTclFile().substring(0, Configure.getTclFile().lastIndexOf("/") + 1)
								+  Synchronizer.parseIdentify(command.get(0));				
				br = new BufferedReader(new FileReader(fileName));				
				
				StringBuilder sb = new StringBuilder();
			    String line = br.readLine();
			    while (line != null) {
			        sb.append(line);
			        sb.append(System.lineSeparator());
			        line = br.readLine();
			    }		    
				
			    br.close();
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
					prefix.add(Synchronizer.parseIdentify(s));
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
			if (scanner.haveNext() || token.size() != 1 || token.get(0).Type() != TokenType.Brace) 
				throw new ParseException(ParseException.InvalidArgument);
			arg[i] = token.get(0).Value();
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
		
		
		Synchronizer.registerEntry(new Entry("\n"));
		return "";
	}

	// endregion
	
	// endregion TCL feature
	
	// region ------------------- Project feature ------------------- //
	
	@Override
	public SNetwork getNetwork() {
		return network;
	}
	
	// region ------------------- Configure ------------------- //
	
	/**
	 * Run Threshold to configure propagationModel and Antenna to fit with nodeRange.
	 * Call after setting each ones of nodeRange, propagationModel and Antenna
	 */
	private void runThreshold() {
		try {
			HashMap<String, String> netinte = getNetworkInterfaces().get(getSelectedNetworkInterface());
			HashMap<String, String> antenna = getAntennas().get(getSelectedAntenna());			
			
			//System.out.print("Set node range\nRun threshold\n");
			
			Process p = Runtime.getRuntime().exec(
					Configure.getNS2Path() + "ns-2.35/indep-utils/propagation/threshold -m " +
					getSelectedPropagationModel().substring(12) + " " +					
					nodeRange);		
			
			BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));			
			String line;
			line = br.readLine();  												// System.out.println(line);	// distance =		
			line = br.readLine();  												// System.out.println(line);	// propagation model:
			line = br.readLine();  												// System.out.println(line);	// 
			line = br.readLine(); 												// System.out.println(line);	// Selected parameters:
			line = br.readLine(); netinte.put("Pt_", 	line.substring(16));	// System.out.println(line);	// transmit power:		    	
			line = br.readLine(); netinte.put("freq_", 	line.substring(11));	// System.out.println(line);	// frequency:
			line = br.readLine(); antenna.put("Gt_",	line.substring(23));	// System.out.println(line);	// transmit antenna gain:
			line = br.readLine(); antenna.put("Gr_",	line.substring(22));	// System.out.println(line);	// receive antenna gain:
			line = br.readLine(); netinte.put("L_",		line.substring(13));	// System.out.println(line);	// system loss:
			line = br.readLine(); antenna.put("Z_",		line.substring(25)); 	// System.out.println(line);	// transmit antenna height:
			line = br.readLine();  												// System.out.println(line);	// receive antenna height:
			line = br.readLine();  												// System.out.println(line);	// 
			line = br.readLine(); netinte.put("RXThresh_",line.substring(34));	// System.out.println(line);	// Receiving threshold RXThresh_ is:
		    
			br.close();			
		} catch (Exception e) {
			System.err.print(e.getMessage());
		}
	}
	
	// region ------------------- Set ------------------- //
	
	@Override public void setSelectedChannel			(String selected) { network.nodeConfig.setInsVar("-channel", "[new " + 	selected + "]"); }		
	@Override public void setSelectedRoutingProtocol	(String selected) {	network.nodeConfig.setInsVar("-adhocRouting",		selected); }
	@Override public void setSelectedLinkLayer			(String selected) { network.nodeConfig.setInsVar("-llType", 			selected); }
	@Override public void setSelectedMac				(String selected) {	network.nodeConfig.setInsVar("-macType", 			selected); }
	@Override public void setSelectedInterfaceQueue		(String selected) {	network.nodeConfig.setInsVar("-ifqType", 			selected); }		
	@Override public void setSelectedNetworkInterface	(String selected) { network.nodeConfig.setInsVar("-phyType", 			selected); }
	@Override public void setSelectedAntenna			(String selected) {	network.nodeConfig.setInsVar("-antType", 			selected); runThreshold(); }
	@Override public void setSelectedPropagationModel	(String selected) {	network.nodeConfig.setInsVar("-propType", 			selected); runThreshold(); }

	@Override public void setNodeRange			(int 	value) { nodeRange = value;	runThreshold();	}		
	@Override public void setQueueLength		(int 	value) { network.nodeConfig.setInsVar("-ifqLen", value + ""); }
	@Override public void setIddleEnergy		(double value) { network.nodeConfig.setInsVar("-energyModel", "EnergyModel"); network.setInsVar("-idlePower", 		value + ""); }
	@Override public void setReceptionEnergy	(double value) { network.nodeConfig.setInsVar("-energyModel", "EnergyModel"); network.setInsVar("-rxPower", 		value + ""); }
	@Override public void setSleepEnergy		(double value) { network.nodeConfig.setInsVar("-energyModel", "EnergyModel"); network.setInsVar("-sleepPower", 		value + ""); }
	@Override public void setTransmissionEnergy	(double value) { network.nodeConfig.setInsVar("-energyModel", "EnergyModel"); network.setInsVar("-transitionPower",	value + ""); }
	@Override public void setInitialEnergy		(double value) { network.nodeConfig.setInsVar("-energyModel", "EnergyModel"); network.setInsVar("-initialEnergy", 	value + ""); }
	
	// endregion Set	
	
	// region ------------------- get ------------------- //

	@Override public String getSelectedChannel() 	
	{
		String v = network.getInsVar("-channel").getValue();
		if (v.startsWith("[new")) return v.substring(5, v.length() - 7);
		return v;
	}
		
	public String getSelectedConfig(String key)  	{ return network.nodeConfig.getInsVar(key) == null ? null : network.nodeConfig.getInsVar(key).getValue(); }
	
	@Override public String getSelectedRoutingProtocol() 	{ return network.nodeConfig.getInsVar("-adhocRouting"	).getValue(); }
	@Override public String getSelectedLinkLayer()			{ return network.nodeConfig.getInsVar("-llType"			).getValue(); }
	@Override public String getSelectedMac() 				{ return network.nodeConfig.getInsVar("-macType"		).getValue(); }
	@Override public String getSelectedPropagationModel()	{ return network.nodeConfig.getInsVar("-propType"		).getValue(); }
	@Override public String getSelectedAntenna() 			{ return network.nodeConfig.getInsVar("-antType"		).getValue(); }
	@Override public String getSelectedInterfaceQueue() 	{ return network.nodeConfig.getInsVar("-ifqType"		).getValue(); }
	@Override public String getSelectedNetworkInterface() 	{ return network.nodeConfig.getInsVar("-phyType"		).getValue(); }
	
	@Override public int 	getQueueLength() 		{ return Integer.parseInt  (network.nodeConfig.getInsVar("-ifqLen"			).getValue()); }
	@Override public double getSleepEnergy() 		{ return Double.parseDouble(network.nodeConfig.getInsVar("-sleepPower"		).getValue()); }
	@Override public double getTransmissionEnergy() { return Double.parseDouble(network.nodeConfig.getInsVar("-transitionPower"	).getValue()); }
	@Override public double getIddleEnergy() 		{ return Double.parseDouble(network.nodeConfig.getInsVar("-idlePower"		).getValue()); }
	@Override public double getReceptionEnergy() 	{ return Double.parseDouble(network.nodeConfig.getInsVar("-rxPower"			).getValue()); }
	@Override public double getInitialEnergy()		{ return Double.parseDouble(network.nodeConfig.getInsVar("-initialEnergy"	).getValue()); }
	
	// endregion get
	
	// endregion Configre

	// endregion Project feature
}
