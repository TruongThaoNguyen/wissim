package control.converter;

import java.util.ArrayList;
import java.util.List;

import model.converter.CharType;
import model.converter.ParseException;
import model.converter.Token;
import model.converter.TokenType;
import control.converter.tclObject.TclObject;

/*
 * Scanner.java
 * @Copyright (C) 2014, Sedic Laboratory, Hanoi University of Science and Technology
 * @Author Duc-Trong Nguyen
 * @Version 2.0
 */

public class Parser {	
//	public TclObject Parse(String text) throws ParseException 
//	{
//		TclObject object = new GlobalObject();
//		Parse(object, text);
//		return object;
//	}
	
	public void Parse(TclObject object, String text) throws ParseException 
	{			
		// Split text to lines
		String[] lines = text.split(System.lineSeparator());
		
		// remove BackSlat tokens "\"
		for (int lineIndex = lines.length - 1; lineIndex >= 0; lineIndex--)
		{
			if (lines[lineIndex].charAt(lines[lineIndex].length() - 1) == '\\')
			{
				int i = lineIndex + 1;
				while (i < lines.length)
				{
					if (lines[i].replace(" ", "").replace("\t", "").charAt(0) != '#')
						break;
					i++;
				}
				if (i == lines.length) throw new ParseException()
			}		
		}				
		
		for (int lineIndex = 0; lineIndex < lines.length; lineIndex++) 
		{		
			// remove commands
			if (lines[lineIndex].contains("#"))	lines[lineIndex] = lines[lineIndex].substring(0, lines[lineIndex].indexOf('#') + 1);
			
			// split line to commands
			String[] commands = lines[lineIndex].split(";");
			
			// parse command
			for (String c : commands) 
			{
				//try
				{	
					List<Token> token = scan(c);
					if (token.size() != 0)
					{
						// ** print token to console
						System.out.print("line " + (lineIndex + 1) + ": ");
						for (Token t : token) t.print();
						System.out.println();	
					
						object.parse(token);											
					}
				}
//				catch (ParseException e)
//				{
//					e.setLine(lineIndex);
//					throw e;
//				}
			}			
		}	
	}
	
	public static List<Token> scan(String line) throws ParseException
	{
		List<Token> token = new ArrayList<Token>();
		
		int col = 0;
		int count, i;
		
		while (col < line.length())
		{
			switch (CharType.TypeOf(line.charAt(col))) {
			case Space:
				// skip space // do nothing
				break;
				
			case Comment:		// #		
				if (token.isEmpty())
				{
					token.add(new Token(TokenType.Comment, line.substring(col + 1)));
					col = line.length();
				}
				else throw new ParseException(ParseException.Comment);
				break;			
				
			case BraceOpen:		// {		
				count = 1;				
				for (i = col + 1; i < line.length(); i++)
				{
					switch (CharType.TypeOf(line.charAt(col))) {
					case BraceOpen: 
						count++;
						break;
					case BraceClose:
						count--;
						if (count == 0) break;							
					default:
						break;
					}					
				}
				if (count == 0)
				{
					token.add(new Token(TokenType.Brace, line.substring(col + 1, i - 1)));
					col = i;
				}
				else
				{
					throw new ParseException(ParseException.BracketNotMatch);
				}
				break;				
			case BraceClose:	throw new ParseException(ParseException.BracketNotMatch);
								
			case BracketOpen:
				count = 1;
				for (i = col + 1; i < line.length(); i++)
				{
					switch (CharType.TypeOf(line.charAt(col))) {
					case BracketOpen: 
						count++;
						break;
					case BracketClose:
						count--;
						if (count == 0) break;							
					default:
						break;
					}					
				}
				if (count == 0)
				{
					token.add(new Token(TokenType.Bracket, line.substring(col + 1, i - 1)));
					col = i;
				}
				else	throw new ParseException(ParseException.BracketNotMatch);
				break;
			case BracketClose:	throw new ParseException(ParseException.BracketNotMatch);
			
			case Quote:
				i = col + 1;
				while (i < line.length() && CharType.TypeOf(line.charAt(i)) != CharType.Quote)
					i++;
				if (CharType.TypeOf(line.charAt(i)) == CharType.Quote)
				{
					token.add(new Token(TokenType.Quote, line.substring(col + 1, i)));
					col = i;
				}
				else throw new ParseException(ParseException.MissQuote);
				break;
			
			case Referent:				
				i = col + 1;
				while (i < line.length() && CharType.TypeOf(line.charAt(i)) == CharType.Letter)
					i++;
				token.add(new Token(TokenType.Referent, line.substring(col + 1, i)));
				col = i;
				break;
				
			case Argument:
				i = col + 1;
				while (i < line.length())
				{
					CharType next = CharType.TypeOf(line.charAt(i));
					if (next == CharType.Letter || next == CharType.Argument)
						i++;							
					else break;
				}
				token.add(new Token(TokenType.Argument, line.substring(col, i)));
				col = i - 1;
				break;
				
			case Letter:
				i = col + 1;
				while (i < line.length())
				{
					CharType next = CharType.TypeOf(line.charAt(i));
					if (next == CharType.Letter || next == CharType.Argument)
						i++;							
					else break;
				} 
				token.add(new Token(TokenType.Identify, line.substring(col, i)));
				col = i - 1;
				break;
				
			case Unknow:
				throw new ParseException(ParseException.InvalidSymbol);
			}
			col++;
		}
		
		return token;
	}
}
