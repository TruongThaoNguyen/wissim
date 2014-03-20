package controllers.converter;

import java.util.HashMap;
import java.util.List;

import models.converter.InsProc;
import models.converter.ParseException;


/**
 * UndefineObject.java
 * @Copyright (C) 2014, Sedic Laboratory, Hanoi University of Science and Technology
 * @Author Duc-Trong Nguyen
 * @Version 2.0
 */

public class SCommonObject implements TclObject 
{	
	private String label;
	private HashMap<String, InsProc> insProc;
	private HashMap<String, String>  insVar;

	public SCommonObject(String label) {
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
			return insProc.get("").Run(command);
	}

	@Override
	public InsProc getInsProc(String key) {
		return insProc.get(key);
	}

	@Override
	public String getInsVar(String key) {
		return insVar.get(key);
	}

	@Override
	public String setInsVar(String key, String value) {
		return insVar.put(key, value);
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
	public void addInsProc(InsProc p) {
		insProc.put(p.insprocName, p);
	}
	
	protected void addInsProc() {				
		new InsProc(this, "set") {

			@Override
			protected String run(List<String> command) throws Exception {				
				return insprocSet(command);
			}

			@Override
			public String print(List<String> command) {
				return command.get(0) + " " + parent.getInsVar(command.get(0));
			}
			
		};
	}
	
	private String insprocSet(List<String> command) throws Exception {
		switch (command.size()) 
		{
			case 0 : throw new ParseException(ParseException.MissArgument);
			case 1 : return getInsVar(Converter.parseIdentify(command.get(0)));
			case 2 : return setInsVar(Converter.parseIdentify(command.get(0)), Converter.parseIdentify(command.get(1)));
			default: throw new ParseException(ParseException.InvalidArgument);
		}		
	}
}
