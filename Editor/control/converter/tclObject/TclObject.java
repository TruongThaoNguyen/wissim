package control.converter.tclObject;

import java.util.Hashtable;
import java.util.List;

import control.converter.Parser;
import model.converter.ParseException;
import model.converter.Token;
import model.converter.TokenType;

/*
 * TclObject.java
 * @Copyright (C) 2014, Sedic Laboratory, Hanoi University of Science and Technology
 * @Author Duc-Trong Nguyen
 * @Version 2.0
 */

public class TclObject {
	public String Value;
	protected Hashtable<String, TclObject> insVar;
	protected Hashtable<String, InsProc> insProc;
	
	public TclObject()
	{
		Value = "";
		insVar = new Hashtable<String, TclObject>();
		insProc = new Hashtable<String, InsProc>();
		
		insProc.put("set", new InsProc() {			
			@Override
			public TclObject run(List<Token> token) throws ParseException {
				return insprocSet(token);
			}
		});
	}
	
	// region ------------- Properties ------------- //

	public String getValue() { return Value; }
	
	public TclObject setValue(String value)
	{
		this.Value = value;
		return this;
	}
	
	public TclObject getInsVar(String label) {
		TclObject component = insVar.get(label);		
		if (component == null) 
		{
			component = new TclObject();
			insVar.put(label, component);
		}		
		return component;
	}

	public InsProc getInsProc(String label) {
		return insProc.get(label);				
	}	
	
	// endregion
	
	// region ------------- InsProc ------------- //
	
	private TclObject insprocSet(List<Token> token) throws ParseException 
	{
		switch (token.size()) 
		{
			case 0:		throw new ParseException(ParseException.MissArgument);
			case 1: 	return getInsVar(token.get(0).Value);
			case 2: 	return getInsVar(token.get(0).Value).setValue(token.get(1).Value);
			default:	throw new ParseException(ParseException.InvalidArgument);
		}
	}
	
	// endregion
	
	// region ------------- Parse ------------- //

	protected TclObject parseInsProc(List<Token> token) throws ParseException {
		if (token.isEmpty()) throw new ParseException(ParseException.MissArgument);
		
		InsProc insProc = getInsProc(token.remove(0).Value);
		if (insProc == null) throw new ParseException(ParseException.InvalidArgument); 		
		
		return insProc.run(token);
	}
	
	protected TclObject parseInsVar(List<Token> token) throws ParseException {
		TclObject object = getInsVar(token.remove(0).Value);
		return object.parseInsProc(token);
	}
	
	
	public TclObject parse(List<Token> token) throws ParseException {
		if (token.size() == 0) return this;
		
		for (Token t : token) 
		{
			switch (t.Type) 
			{
				case Brace:					
					List<Token> subToken = Parser.scan(t.Value);
					t.Value = "";
					
					for (Token sub : subToken) 
					{
						if (sub.Type == TokenType.Referent)
							sub.Value = getInsVar(sub.Value).Value;
						t.Value += sub.Value + " ";
					}
					break;
				case Bracket:
					break;
				case Quote:
					t.Type = TokenType.Identify;
					break;
				
				default:
					// do nothing
					break;
			}
		}
		
		switch (token.get(0).Type) 
		{
			case Argument:	return parseArgument(token);
			case Brace:		return parseBrace(token);
			case Bracket:	return parseBracket(token);
			case Comment:	return parseComment(token);
			case Identify:	return parseIdentify(token);
			case Quote:		return parseQuote(token);
			case Referent:	return parseReferent(token);
			default : 		throw new ParseException(ParseException.InvalidArgument);
		}
	}
	
	// ------------- Default parse function ------------- //

	protected TclObject parseIdentify(List<Token> token) throws ParseException {
		if (token.isEmpty()) throw new ParseException("Miss argument");
		
		if (insProc.containsKey(token.get(0).Value))
			return parseInsProc(token);
		else
			return parseInsVar(token);
	}

	protected TclObject parseComment(List<Token> token) throws ParseException {
		throw new ParseException(ParseException.InvalidArgument);
	}
	protected TclObject parseArgument(List<Token> token) throws ParseException {
		throw new ParseException(ParseException.InvalidArgument);
	}
	protected TclObject parseBrace(List<Token> token) throws ParseException {
		throw new ParseException(ParseException.InvalidArgument);
	}
	protected TclObject parseBracket(List<Token> token) throws ParseException {
		throw new ParseException(ParseException.InvalidArgument);
	}
	protected TclObject parseQuote(List<Token> token) throws ParseException {
		throw new ParseException(ParseException.InvalidArgument);
	}
	protected TclObject parseReferent(List<Token> token) throws ParseException {
		throw new ParseException(ParseException.InvalidArgument);
	} 

	// endregion
}