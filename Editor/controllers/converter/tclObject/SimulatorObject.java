package controllers.converter.tclObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import controllers.converter.Parser;
import models.converter.ParseException;

/**
 * SimulatorObject.java
 * @Copyright (C) 2014, Sedic Laboratory, Hanoi University of Science and Technology
 * @Author Duc-Trong Nguyen
 * @Version 2.0
 */

public class SimulatorObject extends CommonObject
{
	public List<TclObject> Node;
	public HashMap<String, Double> At;
	
	public SimulatorObject(String value) {
		super(value);
		Node = new ArrayList<TclObject>();
		At = new HashMap<String, Double>();
	}

	@Override
	protected void addInsProc()
	{
		super.addInsProc();
		
		insProc.put("trace-all", new InsProc() {
			@Override
			public String run(List<String> command) throws Exception {
				return insprocTraceAll(command);
			}			
		});
	
		insProc.put("node-config", new InsProc() {
			@Override
			public String run(List<String> command) throws Exception {
				insprocNodeConfig(command);
				return null;
			}			
		});
		
		insProc.put("node", new InsProc() {
			@Override
			public String run(List<String> command) throws Exception {
				return insprocNode(command);
			}			
		});
	
		insProc.put("at", new InsProc() {
			@Override
			public String run(List<String> command) throws Exception {
				return insporcAt(command);
			}			
		});
		
		insProc.put("connect", new InsProc(){
			@Override
			public String run(List<String> command) throws Exception {
				return insprocConnect(command);
			}			
		});
	}

	private String insprocConnect(List<String> command) throws Exception {
		if (command.size() != 2) throw new ParseException(ParseException.InvalidArgument);
		
		Parser.global.insObj.get(Parser.parseIdentify(command.get(0))).connectAgent = Parser.global.insObj.get(Parser.parseIdentify(command.get(1)));
		
		return "";
	}
	
	@Override
	protected String insprocAttachAgent(List<String> command) throws Exception {
		if (command.size() != 2) throw new ParseException(ParseException.InvalidArgument);
		
		String arg = Parser.parseIdentify(command.get(0));
		TclObject obj1 = Parser.global.insObj.get(arg);
		TclObject obj2 = Parser.global.insObj.get(Parser.parseIdentify(command.get(1)));
		
		obj1.attachList.add(obj2);
		obj2.attachAgent = obj1;
		
		return "";	
	}

	private String insporcAt(List<String> command) throws Exception {
		if (command.size() != 2) throw new ParseException(ParseException.InvalidArgument);
		String key = Parser.parseIdentify(command.get(1));		
		Double val;
		
		String t = Parser.parseIdentify(command.get(0));
		val = Double.parseDouble(t);
		
		At.put(key, val);
		return key;
	}

	private String insprocNode(List<String> command) throws Exception {
		if (command.size() != 0) throw new ParseException(ParseException.InvalidArgument);
		CommonObject newNode = new CommonObject("node");
		Node.add(newNode);
		String id = Parser.newIndentify();
		Parser.global.insObj.put(id, newNode);		
		return id;
	}

	private void insprocNodeConfig(List<String> command) throws Exception {
		if (command.size() % 2 == 1) throw new ParseException(ParseException.MissArgument);
		for (int i = 0; i < command.size(); i+=2)
		{
			insVar.put(Parser.parseIdentify(command.get(i)), Parser.parseIdentify(command.get(i + 1)));
		}		
	}

	private String insprocTraceAll(List<String> command) throws Exception {
		if (command.size() == 0)	throw new ParseException(ParseException.MissArgument);
		if (command.size() > 1)		throw new ParseException(ParseException.InvalidArgument);
		
		return insVar.put("TraceFile", Parser.parseIdentify(command.get(0)));
	}
}
