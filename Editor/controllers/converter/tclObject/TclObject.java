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
		insVar 	= new HashMap<String, String>();
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
	
	protected void addInsProc() 
	{
		insProc.put("set", new InsProc() 
		{			
			@Override
			public String run(List<String> command) throws Exception 
			{
				return insprocSet(command);
			}
		});	

		insProc.put("<", new InsProc() {
			@Override
			public String run(List<String> command) throws Exception {
				return inspocLessThan(command);
			}			
		});
	}	

	private String insprocSet(List<String> command) throws Exception 
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