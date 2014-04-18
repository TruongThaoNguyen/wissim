package controllers.converter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import models.Project;
import models.converter.CharType;
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

	public void parse(String code) throws ParseException
	{
		Scanner scanner = new Scanner(code);

		while (scanner.haveNext())
		{			
			List<String> command = scanner.scanCommand();
			
			System.out.print((scanner.getLine() + 1) + ":\t");
			for (String word : command) System.out.print(" " + word);			
			
			try 
			{
				parse(command);
			} 
			catch (ParseException e)
			{
				e.setLine(e.getLine() + scanner.getLine());
				throw e;
			} 
			catch (Exception e) 
			{
				throw new ParseException(scanner.getLine(), e.getMessage());
			}
		}
	}
	
	@Override
	public String parse(List<String> command) throws Exception
	{		
		if (command.isEmpty()) 	return null;		
		if (command.size() < 2)	return getInsProc(null).Run(command);					
		
		String arg = Converter.parseIdentify(command.get(0));
		
		if (arg == null) return getInsProc(null).Run(command);  
		
		InsProc p = getInsProc(arg);
		if (p != null)
		{
			command.remove(0);
			return p.Run(command);
		}
	
		TclObject obj = insObj.get(arg);
		if (obj != null)	
		{
			command.remove(0);
			return obj.parse(command);
		}
		
		obj = new SCommonObject(arg);
		p = obj.getInsProc(Converter.parseIdentify(command.get(1)));
		if (p != null)
		{			
			insObj.put(arg, obj);
			command.remove(0);
			command.remove(0);
			return p.Run(command);
		}
		
		// Undefined insProc 
		return getInsProc(null).Run(command);
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
	
	public TclObject removeObject(TclObject obj)
	{
		for (String s : insObj.keySet())
			if (insObj.get(s) == obj)
				return insObj.remove(s);
		return null;
	}
	
	// endregion Object list
	
	// region ------------------- Generate Entry ------------------- //

	@Override
	public void setEntry(Entry e) {
		entryList.add(e);		
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
	
	// region ------------- InsProc ------------- //	
		
	@Override
	public InsProc getInsProc(String key) 
	{
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
				if (arg.equals("Application/Traffic/CBR"))	return addObject(new SApplicationProtocol()); 
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
			public String Run(List<String> command) throws Exception {
				// do not store this command to register
				
				// remove sperator
				String l = command.get(command.size() - 1);
				CharType type = CharType.TypeOf(l.charAt(0));
				if (type == CharType.Semicolon || type == CharType.Separator) command.remove(l);
				
				return run(command);
			}

			@Override
			protected String run(List<String> command) throws Exception {
				return insprocFor(command);
			}			
		};

		new InsProc(this, "incr") {
			@Override
			protected String run(List<String> command) throws Exception {				
				if (command.size() != 1) throw new ParseException(ParseException.InvalidArgument);				
				InsVar i = insVar.get(Converter.parseIdentify(command.get(0)));						
				return i.setValue(Integer.parseInt(i.getValue()) + 1);
			}	
		};
		
		new InsProc(this, "expr") {
			@Override
			protected String run(List<String> command) throws Exception {
				return insprocExpr(command);
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
		parse(arg[0]);
		while (Boolean.parseBoolean(insprocExpr(new ArrayList<String>(arg1))) && limit > 0)			
		{
			parse(arg[3]);			
			parse(arg[2]);
			limit--;
		}
		
		return "";
	}

	// endregion
	
	// endregion TCL feature
	
	
	// region ------------------- Project feature ------------------- //
	
	@Override
	public WirelessNetwork getNetwork() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setNodeRange(int nodeRange) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setQueueLength(int queueLength) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setIddleEnergy(double iddleEnergy) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setReceptionEnergy(double receptionEnergy) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setSleepEnergy(double sleepEnergy) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setTransmissionEnergy(double transmissionEnergy) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public double getSleepEnergy() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getTransmissionEnergy() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getQueueLength() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getNodeRange() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getIddleEnergy() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getReceptionEnergy() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setRoutingProtocols(HashMap<String, HashMap<String, String>> ps) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setTransportProtocols(
			HashMap<String, HashMap<String, String>> ps) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setLinkLayers(HashMap<String, HashMap<String, String>> ps) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setApplicationProtocols(
			HashMap<String, HashMap<String, String>> ps) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setMacs(HashMap<String, HashMap<String, String>> ps) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setChannels(HashMap<String, HashMap<String, String>> ps) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setPropagationModels(HashMap<String, HashMap<String, String>> ps) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setNetworkInterfaces(HashMap<String, HashMap<String, String>> ps) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setAntennas(HashMap<String, HashMap<String, String>> ps) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setInterfaceQueues(HashMap<String, HashMap<String, String>> ps) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public HashMap<String, HashMap<String, String>> getApplicationProtocols() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HashMap<String, HashMap<String, String>> getMacs() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HashMap<String, HashMap<String, String>> getChannels() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HashMap<String, HashMap<String, String>> getPropagationModels() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HashMap<String, HashMap<String, String>> getNetworkInterfaces() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HashMap<String, HashMap<String, String>> getAntennas() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HashMap<String, HashMap<String, String>> getInterfaceQueues() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HashMap<String, HashMap<String, String>> getRoutingProtocols() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HashMap<String, HashMap<String, String>> getTransportProtocols() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HashMap<String, HashMap<String, String>> getLinkLayers() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setSelectedRoutingProtocol(String selected) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setSelectedTransportProtocol(String selected) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setSelectedApplicationProtocol(String selected) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setSelectedLinkLayer(String selected) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setSelectedMac(String selected) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setSelectedAntenna(String selected) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setSelectedChannel(String selected) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setSelectedPropagationModel(String selected) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setSelectedInterfaceQueue(String selected) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setSelectedNetworkInterface(String selected) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getSelectedRoutingProtocol() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSelectedTransportProtocol() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSelectedApplicationProtocol() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSelectedLinkLayer() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSelectedMac() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSelectedChannel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSelectedPropagationModel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSelectedAntenna() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSelectedInterfaceQueue() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSelectedNetworkInterface() {
		// TODO Auto-generated method stub
		return null;
	}

	// endregion Project feature
}
