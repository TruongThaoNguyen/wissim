package controllers.converter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import models.converter.*;
import controllers.converter.tclObject.*;

/*
 * Scanner.java
 * @Copyright (C) 2014, Sedic Laboratory, Hanoi University of Science and Technology
 * @Author Duc-Trong Nguyen
 * @Version 2.0
 */

public class Parser {		
	/**
	 * global TclObject
	 */
	public static GlobalObject global;
	
	/**
	 * Parse text code to Tcl global object
	 * @param object GlobalObject
	 * @param text Tcl code
	 * @throws ParseException Parse Exception
	 */
	public static GlobalObject parse(String code) throws ParseException 
	{							
		global = new GlobalObject();

		List<List<Token>> script = Scanner.scan(code);
		
		for (List<Token> command : script) 
		{
			global.parse(command);
		}
		
		return global;
	}	
	
	public static TclObject parseIdentify(String word) throws ParseException
	{			
		List<List<Token>> command = Scanner.scan(word);
				
		if (command.size() == 0) return null;
		if (command.size() > 1) throw new ParseException(ParseException.InvalidArgument);
		
		List<Token> token = command.get(0);
		
		if (token.size() == 0) return null;
		if (token.size() > 1) throw new ParseException(ParseException.InvalidArgument);
		
		switch (token.get(0).Type)
		{			
			case Argument:		throw new ParseException(ParseException.InvalidArgument);
			case Comment:		return null;
			
			case Quote:
			case Identify:		return new TclObject(token.get(0).Value);
			
			case Parenthesis:	return new TclObject("(" + parseIdentify(token.get(0).Value).Value + ")");
			
			case Referent:		return global.getInsVar(parseIdentify(token.get(0).Value).Value);							
				
			case Brace:
				List<Token> subToken = scan(token.get(0).Value);
				String result = "";
				for (Token s : subToken) 
				{
					if (s.Type == TokenType.Referent)
						result = result + global.getInsVar(s.Value).Value + " ";
					else
						result = result + s.Value + " ";
				}
				// remove last space character
				return new TclObject(result.substring(0, result.length()));
				
			case Bracket:
				break;															
		}
		
		return new TclObject(token.get(0).Value);
	}
}
