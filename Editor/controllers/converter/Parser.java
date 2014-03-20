package controllers.converter;

import java.util.List;

import models.converter.ParseException;
import models.converter.Token;
import models.converter.TokenType;
import controllers.converter.tclObject.GlobalObject;

/**
 * Scanner.java
 * @Copyright (C) 2014, SEDIC Laboratory, Hanoi University of Science and Technology
 * @Author Duc-Trong Nguyen
 * @Version 2.0
 */

public class Parser 
{		
	public static GlobalObject global;
	private static int newObjectID = 1;	
	
	/**
	 * Parse a Tcl script
	 * @param code Tcl script
	 * @return Global object corresponding with script
	 * @throws ParseExceptio
	 */
	public static GlobalObject parse(String text) throws ParseException	
	{	
		global = new GlobalObject("");
		global.parse(text);
		return global;
	}	
	
	/**
	 * Parse Identify
	 * @param word Tcl word
	 * @return a String is a Identify
	 * @throws Exception
	 */
	public static String parseIdentify(String word) throws Exception 
	{	
		String result = "";
		Scanner scanner = new Scanner(word);
		List<Token> tokenList = scanner.scanWord();
		
		if (tokenList.size() == 0) return null;		
		
		for (Token token : tokenList) 
		{					
			switch (token.Type) 
			{
				case Identify:
				case Brace:		
					result += token.Value;
					break;

				case Parenthesis:	
					result += "(" + parseIdentify(token.Value) + ")";
					break;

				case Referent:
					result += global.insVar.get(parseIdentify(token.Value));
					break;
					
				case Bracket:
					Scanner subScanner = new Scanner(token.Value);
					List<String> command = subScanner.scanCommand();
					if (subScanner.haveNext()) throw new ParseException(ParseException.InvalidArgument);
					result += global.parse(command);					
					break;		
					
				case Quote:
					result += parseQuote(token.Value);
					break;							
			}
		}
		
		return result;
	}
	
	/**
	 * Parse Quote string.
	 * Replace Referent token with this value 
	 * @return string with replaced referent token
	 * @throws Exception 
	 */
	private static String parseQuote(String word) throws Exception 
	{
		String result = "";
		Scanner scanner = new Scanner(word);
		List<String> command = scanner.scanCommand();
		
		for (String subWord : command) 
		{
			Scanner subScanner = new Scanner(subWord);
			List<Token> tokenList = subScanner.scanWord();
		
			for (Token token : tokenList) 
			{
				if (token.Type == TokenType.Referent)				
					result += global.insVar.get(parseIdentify(token.Value));
				else				
					result += token.print();				
			}
			result += " ";
		}
		
		return result.substring(0, result.length() - 1);
	}

	public static String newIndentify() 
	{
		while (global.insObj.containsKey("_o" + newObjectID)) newObjectID++;
		return "_o" + newObjectID;
	}



}
