package controllers.converter.tclObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import models.converter.ParseException;
import controllers.converter.Parser;

/**
 * UndefineObject.java
 * @Copyright (C) 2014, Sedic Laboratory, Hanoi University of Science and Technology
 * @Author Duc-Trong Nguyen
 * @Version 2.0
 */

public class CommonObject extends TclObject 
{	
	public CommonObject(String value) {
		super(value);
	}

	/**
	 * Parse by call InstProc
	 * @param command Token list, first token is identify of insProc that will used
	 * @return TclObject, result of InsProc
	 * @throws Exception
	 */
	@Override
	public String parse(List<String> command) throws Exception
	{
		if (command.isEmpty()) throw new ParseException(ParseException.MissArgument);
		
		String arg = Parser.parseIdentify(command.remove(0));
		
		if (insProc.containsKey(arg)) 
			return insProc.get(arg).run(command);		

		// Undefined insProc 		
		String newInsVar = "";		
		for (String word : command) 
		{
			newInsVar += Parser.parseIdentify(word) + " ";
		}				
		if (newInsVar.length() > 1) newInsVar = newInsVar.substring(0, newInsVar.length() - 1);
		
		System.out.print("\nUndefine InsProc: " + arg + " " + newInsVar + "\n");
		
		return insVar.put(arg, newInsVar);
	}

	@Override
	protected void addInsProc() {
		super.addInsProc();
		
		insProc.put("setdest", new InsProc() {			
			@Override
			public String run(List<String> command) throws Exception {
				return insprocSetDest(command);
			}
		});
				
		insProc.put("attach-agent", new InsProc(){
			@Override
			public String run(List<String> command) throws Exception {
				return insprocAttachAgent(command);
			}			
		});
	}
	
	protected String insprocSetDest(List<String> command) throws Exception {
		if (command.size() != 3) throw new ParseException(ParseException.InvalidArgument);
		insVar.put("destX", Parser.parseIdentify(command.get(0)));
		insVar.put("destY", Parser.parseIdentify(command.get(1)));
		return null;
	}

	protected String insprocAttachAgent(List<String> command) throws Exception {
		if (command.size() != 1) throw new ParseException(ParseException.InvalidArgument);
		
		TclObject agent = Parser.global.insObj.get(Parser.parseIdentify(command.get(0)));		
		
		agent.attachList.add(this);
		this.attachAgent = agent;
		
		return "";	
	}

	
	
	
}
