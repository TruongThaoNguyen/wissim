package controllers.converter.tclObject;

import java.util.ArrayList;
import java.util.List;

import controllers.converter.Parser;
import controllers.converter.Scanner;
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
	
	public SimulatorObject(String value) {
		super(value);
		Node = new ArrayList<TclObject>();
	}

	@Override
	protected void addInsProc()
	{
		super.addInsProc();
		
		new InsProc(this, "trace-all") {
			@Override
			public String run(List<String> command) throws Exception {
				record(this, command);
				return insprocTraceAll(command);
			}

			@Override
			public String print(List<String> command) throws Exception {
				// TODO Auto-generated method stub
				return null;
			}			
		};
	
		new InsProc(this, "node-config") {
			@Override
			public String run(List<String> command) throws Exception {
				record(this, command);
				insprocNodeConfig(command);
				return null;
			}

			@Override
			public String print(List<String> command) throws Exception {
				// TODO Auto-generated method stub
				return null;
			}			
		};
		
		new InsProc(this, "node") {
			@Override
			public String run(List<String> command) throws Exception {
				record(this, command);
				return insprocNode(command);
			}

			@Override
			public String print(List<String> command) throws Exception {
				// TODO Auto-generated method stub
				return null;
			}			
		};
	
		new InsProc(this, "at") {
			@Override
			public String run(List<String> command) throws Exception {
				record(this, command);
				return insporcAt(command);
			}

			@Override
			public String print(List<String> command) throws Exception {
				// TODO Auto-generated method stub
				return null;
			}			
		};
		
		new InsProc(this, "connect"){
			@Override
			public String run(List<String> command) throws Exception {
				record(this, command);
				return insprocConnect(command);
			}

			@Override
			public String print(List<String> command) throws Exception {
				// TODO Auto-generated method stub
				return null;
			}			
		};
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
		
		String arg = Parser.parseIdentify(command.get(1));		
		Double time = Double.parseDouble(Parser.parseIdentify(command.get(0)));
				
		Scanner scanner = new Scanner(arg);
		List<String> sc = scanner.scanCommand();
		
		if (sc.size() > 1)
		{
			TclObject obj = Parser.global.insObj.get(sc.remove(0));
			if (obj != null)
			{
				arg = "";
				for (String s : sc) 
				{
					arg += s;
				}
				
				obj.At.put(arg, time);
				return arg;
			}
		}									
		
		At.put(arg, time);
		return arg;
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
