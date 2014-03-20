package controllers.converter;

import java.util.HashMap;
import java.util.List;

import models.converter.InsProc;
import models.converter.ParseException;
import models.networkcomponents.WirelessNetwork;

/**
 * SimulatorObject.java
 * @Copyright (C) 2014, Sedic Laboratory, Hanoi University of Science and Technology
 * @Author Duc-Trong Nguyen
 * @Version 2.0
 */

public class SNetwork extends WirelessNetwork implements TclObject
{	
	private String label;
	private HashMap<String, InsProc> insProc;
	private HashMap<String, String>  insVar;

	public SNetwork(String label) {
		super(label);
		this.label = label;
		this.insProc = new HashMap<String, InsProc>();
		this.insVar = new HashMap<String, String>();
		addInsProc();
	}

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
	public String getInsVar(String key) {
		return insVar.get(key);
	}

	@Override
	public String setInsVar(String key, String value) 
	{
		switch (key) 
		{
			
	
			default:	return insVar.put(key, value);
		}
		
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
	public InsProc getInsProc(String key) {
		return insProc.get(key);
	}
	
	@Override
	public void addInsProc(InsProc p) {
		insProc.put(p.insprocName, p);
	}
	
	protected void addInsProc()
	{		
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
					case 1 : return getInsVar(Converter.parseIdentify(command.get(0)));
					case 2 : return setInsVar(Converter.parseIdentify(command.get(0)), Converter.parseIdentify(command.get(1)));
					default: throw new ParseException(ParseException.InvalidArgument);
				}
			}

			@Override
			public String print(List<String> command) {
				String arg = parent.getInsVar(command.get(0));
				TclObject obj = Converter.global.insObj.get(arg);
				if (obj != null)
					return command.get(0) + " " + obj.getLabel(); 
				else
					return command.get(0) + " " + arg;
			}			
		};
		
		new InsProc(this, "trace-all") {
			@Override
			public String run(List<String> command) throws Exception {				
				if (command.size() == 0)	throw new ParseException(ParseException.MissArgument);
				if (command.size() > 1)		throw new ParseException(ParseException.InvalidArgument);
				
				return setInsVar("trace-all", Converter.parseIdentify(command.get(0)));
			}

			@Override
			public String print(List<String> command) {
				return getInsVar("trace-all");
			}			
		};
	
		new InsProc(this, "node-config") {
			@Override
			public String run(List<String> command) throws Exception {
				if (command.size() % 2 == 1) throw new ParseException(ParseException.MissArgument);
				for (int i = 0; i < command.size(); i+=2)
				{
					setInsVar(Converter.parseIdentify(command.get(i)), Converter.parseIdentify(command.get(i + 1)));
				}
				return "";
			}

			@Override
			public String print(List<String> command) {
				return printInsprocNodeconfig();
			}			
		};
		
		new InsProc(this, "node") {
			@Override
			public String run(List<String> command) throws Exception {
				return insprocNode(command);
			}

			@Override
			public String print(List<String> command) {
				return "";
			}			
		};
	
		new InsProc(this, "at") {
			@Override
			public String run(List<String> command) throws Exception {
				return insprocAt(command);
			}

			@Override
			public String print(List<String> command) {
				return printInsprocAt(command);
			}			
		};
		
		new InsProc(this, "connect"){
			@Override
			public String run(List<String> command) throws Exception {
				return insprocConnect(command);
			}

			@Override
			public String print(List<String> command) {
				// TODO Auto-generated method stub
				return null;
			}			
		};
	}

	protected String printInsprocAt(List<String> command) {
		// TODO Auto-generated method stub
		return null;
	}

	protected String printInsprocNodeconfig() {
		// TODO Auto-generated method stub
		return null;
	}
	
	private String insprocNode(List<String> command) throws Exception {
//		if (command.size() != 0) throw new ParseException(ParseException.InvalidArgument);
//		SCommonObject newNode = new SCommonObject("node");
//		Node.add(newNode);
//		String id = Converter.newIndentify();
//		Converter.global.insObj.put(id, newNode);		
//		return id;
		return null;
	}
	
	private String insprocAt(List<String> command) throws Exception {
//		if (command.size() != 2) throw new ParseException(ParseException.InvalidArgument);
//		
//		String arg = Converter.parseIdentify(command.get(1));		
//		Double time = Double.parseDouble(Converter.parseIdentify(command.get(0)));
//				
//		Scanner scanner = new Scanner(arg);
//		List<String> sc = scanner.scanCommand();
//		
//		if (sc.size() > 1)
//		{
//			TclObject obj = Converter.global.insObj.get(sc.remove(0));
//			if (obj != null)
//			{
//				arg = "";
//				for (String s : sc) 
//				{
//					arg += s;
//				}
//				
//				obj.At.put(arg, time);
//				return arg;
//			}
//		}									
//		
//		At.put(arg, time);
//		return arg;
		return null;
	}
	
	private String insprocConnect(List<String> command) throws Exception {
		if (command.size() != 2) throw new ParseException(ParseException.InvalidArgument);
//		
//		Converter.global.insObj.get(Converter.parseIdentify(command.get(0))).connectAgent = Converter.global.insObj.get(Converter.parseIdentify(command.get(1)));
//		
		return "";
	}
}
