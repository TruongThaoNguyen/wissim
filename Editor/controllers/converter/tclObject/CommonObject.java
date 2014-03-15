package controllers.converter.tclObject;

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
				
		InsProc proc = insProc.get(Parser.parseIdentify(command.get(0)));
		if (proc != null)
		{
			command.remove(0);
			return proc.run(command);
		}
		else 		
			return insProc.get("undefined").run(command);
	}

	@Override
	protected void addInsProc() {
		super.addInsProc();
		
		new InsProc(this, "setdest") {			
			@Override
			public String run(List<String> command) throws Exception {
				record(this, command);
				return insprocSetDest(command);
			}

			@Override
			public String print(List<String> command) throws Exception {
				return parent.value + " " + insprocName + " " + parent.insVar.get("X_") + " " + parent.insVar.get("Y_") + " " + parent.insVar.get("Z_");   
			}
		};
				
		new InsProc(this, "attach-agent"){
			@Override
			public String run(List<String> command) throws Exception {
				record(this, command);
				return insprocAttachAgent(command);
			}

			@Override
			public String print(List<String> command) throws Exception {
				return parent.value + " " + insprocName + " " + parent.attachAgent.value;
			}			
		};
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
