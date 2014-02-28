package controllers.converter.tclObject;

import java.util.Hashtable;
import java.util.List;

import controllers.converter.Parser;
import models.converter.ParseException;
import models.converter.Token;

/*
 * TclObject.java
 * @Copyright (C) 2014, Sedic Laboratory, Hanoi University of Science and Technology
 * @Author Duc-Trong Nguyen
 * @Version 2.0
 */

public class TclObject {
	
	/**
	 * Value of TclObject
	 */
	public String Value;
	
	protected Hashtable<String, TclObject> insVar;
	protected Hashtable<String, InsProc> insProc;
	
	/**
	 * Create TclObject
	 * @param string 
	 */
	public TclObject(String value)
	{
		Value = value;
		insVar = new Hashtable<String, TclObject>();
		insProc = new Hashtable<String, InsProc>();
		
		addInsProc();
	}
	
	/**
	 * Create TclObject
	 */
	public TclObject()
	{
		Value = "";
		insVar = new Hashtable<String, TclObject>();
		insProc = new Hashtable<String, InsProc>();
		
		addInsProc();
	}
	
	private void addInsProc() {
		insProc.put("set", new InsProc() {			
			@Override
			public TclObject run(List<String> words) throws ParseException {
				return insprocSet(words);
			}
		});
	}
	
	// region ------------- Value ------------- //

	/**
	 * Get Value of TclObject
	 * @return String value
	 */
	public String getValue() { return Value; }
	
	/**
	 * Set Value of TclObject
	 * @param value
	 * @return this TclObject
	 */
	public TclObject setValue(String value)
	{
		this.Value = value;
		return this;
	}

	// endregion
	
	// region ------------- InstVar ------------- //
	
	/**
	 * Get InsVar that have matched label
	 * if no InsVar matched, create new Insvar with label
	 * @param label
	 * @return TclObject 
	 */
	public TclObject getInsVar(String label) {		
		TclObject component = insVar.get(label);		
		if (component == null) 
		{
			component = new TclObject();
			insVar.put(label, component);
		}		
		return component;
	}

	/**
	 * Set InsVar
	 * If InsVar with matched label is availabe, replate it
	 * @param label
	 * @param insvar new TclObject
	 * @return Insvar
	 */
	public TclObject setInsVar(String label, TclObject insvar)
	{
		this.insVar.put(label, insvar);
		return insvar;
	}

	// endregion
	
	// region ------------- InsProc ------------- //
	
	/**
	 * get InsProc match label
	 * return null if no insporc matched
	 * @param label
	 * @return InsProc
	 */
	public InsProc getInsProc(String label) {
		return insProc.get(label);				
	}	
	
	private TclObject insprocSet(List<String> words) throws ParseException 
	{
//		switch (token.size()) 
//		{
//			case 0:		throw new ParseException(ParseException.MissArgument);
//			case 1:		return this.getInsVar(Parser.parseIdentify(token.get(0)).Value);
//			case 2:		return this.setInsVar(Parser.parseIdentify(token.get(0)).Value, Parser.parseIdentify(token.get(1)));
//			default:	throw new ParseException(ParseException.InvalidArgument);
//		}
		return null;
	}
	
	// endregion
	
	// region ------------- Parse ------------- //

	/**
	 * Parse by call InstProc
	 * @param token Token list, first token is identify of insProc that will used
	 * @return TclObject, result of InsProc
	 * @throws ParseException
	 */
	protected TclObject parseInsProc(List<String> words) throws ParseException 
	{
		if (words.isEmpty()) throw new ParseException(ParseException.MissArgument);
		
		InsProc insProc = getInsProc(Parser.parseIdentify(words.remove(0)).Value);
		
		if (insProc == null) throw new ParseException(ParseException.InvalidArgument); 		
		
		return insProc.run(words);	
	}
	
	protected TclObject parseInsVar(List<String> words) throws ParseException 
	{
		TclObject object = getInsVar(Parser.parseIdentify(words.remove(0)).Value);
		return object.parseInsProc(words);
	}
	
	public TclObject parse(List<Token> command) throws ParseException 
	{		
//		if (command.size() == 0) return new TclObject();				
//		
//		TclObject object = Parser.parseIdentify(command.get(0));				
//		if (object == null) return null;
//		
//		if (insProc.containsKey(object.Value)) {
//			return parseInsProc(command);
//		} 
//		else {
//			return parseInsVar(command);
//		}
		return null;
	}
	
	// endregion
}