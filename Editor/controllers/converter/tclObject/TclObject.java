package controllers.converter.tclObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import controllers.converter.Parser;
import models.converter.ParseException;

/**
 * TclObject.java
 * @Copyright (C) 2014, Sedic Laboratory, Hanoi University of Science and Technology
 * @Author Duc-Trong Nguyen
 * @Version 2.0
 */
public abstract class TclObject {
	public String value;
	public HashMap<String, String> 	insVar;
	public HashMap<String, InsProc> insProc;
	public HashMap<String, Double> 	At;
	
	public List<TclObject>	attachList;		// List of attaching agent
	public TclObject		attachAgent;	// Agent attached
	public TclObject 		connectAgent;	// Agent connecting
	
	/**
	 * Create TclObject
	 */
	public TclObject(String value)
	{
		this.value = value;
		insVar 	= new LinkedHashMap<String, String>();
		insProc = new HashMap<String, InsProc>();
		At 		= new HashMap<String, Double>();
		attachList 	 = new ArrayList<TclObject>();
		attachAgent  = null;
		connectAgent = null;
		
		addInsProc();
	}	
	
	/**
	 * Parse by call InstProc
	 * @param command Token list, first token is identify of insProc that will used
	 * @return TclObject, result of InsProc
	 * @throws Exception
	 */
	public abstract String parse(List<String> command) throws Exception;
	

	public String printf() 
	{
		for (String i : insVar.keySet()) 
		{
			System.out.println(value + " set " + i + " " + insVar.get(i));	
		}	
		
		return "";
	}
	
	protected void addInsProc() 
	{		
		new InsProc(this, "undefined") {
			@Override
			protected String run(List<String> command) throws Exception {
				record(this, command);
				return insprocUndefined(command);
			}

			@Override
			public String print(List<String> command) throws Exception {
				String st = parent.value + " " + insprocName;
				for (String string : command) {
					st += string;
				}
				return st;
			}			
		};
		
		new InsProc(this, "set") 
		{			
			@Override
			protected String run(List<String> command) throws Exception 
			{
				record(this, command);
				return insprocSet(command);
			}

			@Override
			public String print(List<String> command) throws Exception {
				String arg = command.get(0);
				String value = parent.insVar.get(arg);
				if (value != null)	
					return parent.value + " " + insprocName + " " + arg + " " + value;
				else 
					return null;
			}
		};	

		new InsProc(this, "<") {
			@Override
			protected String run(List<String> command) throws Exception {
				record(this, command);
				return inspocLessThan(command);
			}

			@Override
			public String print(List<String> command) throws Exception {
				// TODO
				return parent.value + " < " + command.get(0);
			}			
		};
	}	
	
	protected String insprocUndefined(List<String> command) throws Exception {
		String arg = command.remove(0);
		String newInsVar = "";		
		
		for (String word : command) 
		{
			newInsVar += Parser.parseIdentify(word) + " ";
		}		
		if (newInsVar.length() > 1) newInsVar = newInsVar.substring(0, newInsVar.length() - 1);
		
		System.out.print("\nUndefine InsProc: " + arg + " " + newInsVar + "\n");
		
		return insVar.put(arg, newInsVar);	
	}

	protected String insprocSet(List<String> command) throws Exception 
	{
		switch (command.size()) 
		{
			case 0 : throw new ParseException(ParseException.MissArgument);
			case 1 : return insVar.get(Parser.parseIdentify(command.get(0)));
			case 2 : return insVar.put(Parser.parseIdentify(command.get(0)), Parser.parseIdentify(command.get(1)));
			default: throw new ParseException(ParseException.InvalidArgument);
		}
	}	

	protected String inspocLessThan(List<String> command) throws Exception {
		if (command.size() != 1) throw new ParseException(ParseException.InvalidArgument);		
		String arg = Parser.parseIdentify(command.get(0));
		return Double.parseDouble(value) < Double.parseDouble(arg) ? "true" : "false";		
	}
}