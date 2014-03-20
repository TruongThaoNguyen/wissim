package controllers.converter;

import java.util.ArrayList;
import java.util.List;

import models.converter.CharType;
import models.converter.ParseException;
import models.converter.Token;
import models.converter.TokenType;

/**
 * Scanner.java
 * @Copyright (C) 2014, Sedic Laboratory, Hanoi University of Science and Technology
 * @Author Duc-Trong Nguyen
 * @Version 2.0
 */

public class Scanner {
	private String code;
	private int line;
	private int index;
	
	public int getLine() { return line; }
	
	public Scanner(String text)	
	{
		this.code = text;
		line = 0;
		index = 0;			
	}

	public boolean haveNext() {
		return index < code.length();
	}


	/**
	 * Split tcl code to words.
	 * Command is list of words
	 * @return List of words
	 * @throws ParseException 
	 */
	public List<String> scanCommand() throws ParseException
	{	
		List<String> command = new ArrayList<String>();		
		
		while (index < code.length())
		{
			CharType type = CharType.TypeOf(code.charAt(index));
			if (type == CharType.Separator)
			{
				index++;
				line++;
				break;
			}
			else if (type == CharType.Space) index++;
			else break;
		}		
		while (index < code.length())
		{
			if (CharType.TypeOf(code.charAt(index)) == CharType.Space) index++;
			else break;
		}
		

		int i = index;
		
		while (index < code.length())
		{
			switch (CharType.TypeOf(code.charAt(index)))
			{
				case Space:
					if (index > i) command.add(code.substring(i, index));					
					while (index < code.length() && CharType.TypeOf(code.charAt(index)) == CharType.Space) index++;
					i = index;
					break;
				
				case Semicolon:
					String word  = code.substring(i, index);
					if (!word.isEmpty()) command.add(word);					
					index++;		
					while (index < code.length() && CharType.TypeOf(code.charAt(index)) == CharType.Space) index++;
					if (CharType.TypeOf(code.charAt(index)) == CharType.Separator) command.add(";\n");
					else command.add(";"); 
					return command;
					
				case Separator:				
					if (index > i) command.add(code.substring(i, index));	
					command.add("\n");
					return command;
					
				case Comment:
					if (command.size() != 0) throw new ParseException(ParseException.Comment);				
					while (index < code.length() && CharType.TypeOf(code.charAt(index)) != CharType.Separator) index++;					
					command.add(code.substring(i, index));
					command.add("\n");
					return command;

				case BackSlash:
					index++;
					if (index >= code.length() || CharType.TypeOf(code.charAt(index)) != CharType.Separator) 
						throw new ParseException(ParseException.InvalidSymbol);
					index++;
					line++;
					i = index;
					break;

				case BraceOpen:
					scanBrace();
					break;
					
				case BracketOpen:
					scanBracket();
					break;
					
				case ParenthesisOpen:
					scanParenthesis();
					break;
					
				case Quote:
					scanQuote();
					break;

				case Letter:
				case Referent:
				case BraceClose:
				case BracketClose:
				case ParenthesisClose:
					index++;				
					break;

				case Unknow: throw new ParseException(ParseException.InvalidSymbol);
			}
		}
		
		if (index > i)
		{
			command.add(code.substring(i, index));
			command.add("\n");
		}
		return command;
	}

	/**
	 * Split word to tokens
	 * @return List of tokens
	 * @throws ParseException
	 */
	public List<Token> scanWord() throws ParseException
	{
		List<Token> tokenList = new ArrayList<Token>();
		
		while (index < code.length())
		{
			switch (CharType.TypeOf(code.charAt(index)))
			{
				case Separator: line++;
				case Semicolon:					
					index++;
					return tokenList;
							
				case BackSlash:
					index++;
					if (index >= code.length() || CharType.TypeOf(code.charAt(index)) != CharType.Separator) 
						throw new ParseException(ParseException.InvalidSymbol);
					index++;
					line++;
					break;
					
				case Comment:
					while (index < code.length() && code.charAt(index) != '\n') index++;
					index++;
					line++;
					break;
			
				case Quote:
					tokenList.add(new Token(TokenType.Quote, scanQuote()));
					break;
					
				case ParenthesisOpen:
					tokenList.add(new Token(TokenType.Parenthesis, scanParenthesis()));
					break;
		
				case BracketOpen:
					tokenList.add(new Token(TokenType.Bracket, scanBracket()));
					break;
					
				case BraceOpen:
					tokenList.add(new Token(TokenType.Brace, scanBrace()));
					break;				

				case Referent:
					index++;
					tokenList.add(new Token(TokenType.Referent, scanReferent()));
					break;
					
				case Letter:
					tokenList.add(new Token(TokenType.Identify, scanIdentify()));					
					break;				
					
				case BraceClose:
				case BracketClose:
				case ParenthesisClose:														
				case Space:
				case Unknow:
					throw new ParseException(ParseException.InvalidSymbol);
			}
		}
		
		return tokenList;
	}	
	
	/**
	 * Scan Identify.
	 * Identify = Letter
	 * Identify = Letter + Parenthesis
	 * Put index to next of letter list.
	 * @return
	 * @throws ParseException 
	 */
	private String scanIdentify() throws ParseException 
	{
		int i =  index;
		while (index < code.length() && CharType.TypeOf(code.charAt(index)) == CharType.Letter)	index++;		
		return code.substring(i, index);
	}
	
	/**
	 * Scan Referent.
	 * Identify = Letter
	 * Identify = Letter + Parenthesis
	 * Put index to next of letter list.
	 * @return
	 * @throws ParseException 
	 */
	private String scanReferent() throws ParseException 
	{
		int i =  index;
		while (index < code.length() && CharType.TypeOf(code.charAt(index)) == CharType.Letter)	index++;
		
		if (index < code.length() && CharType.TypeOf(code.charAt(index)) == CharType.ParenthesisOpen) scanParenthesis();
		return code.substring(i, index);
	}

	/**
	 * Scan quote.
	 * Put index to next of '"'
	 */
	private String scanQuote() throws ParseException 
	{					
		int i = ++index;
		for (;index < code.length(); index++)
		{
			switch(CharType.TypeOf(code.charAt(index)))
			{					
				case Quote:	return code.substring(i, index++);
				
				case Separator:
					line++;
					break;
					
				case Unknow: throw new ParseException(ParseException.InvalidSymbol);
				
				default: break;
			}
		}
				
		throw new ParseException(ParseException.MissQuote);
	}
	
	/**
	 * Scan parenthesis.
	 * Put index to next of ')'
	 */
	private String scanParenthesis() throws ParseException 
	{				
		int count = 1;
		int i = ++index;
		for (; index < code.length(); index++)
		{
			switch (CharType.TypeOf(code.charAt(index)))
			{								
				case ParenthesisOpen:
					count++;
					break;
				
				case ParenthesisClose:
					count--;
					if (count == 0) return code.substring(i, index++);						
					break;						
					
				case Separator:
					line++;
					break;
					
				case Unknow: throw new ParseException(ParseException.InvalidSymbol);
				
				default: break;				
			}
		}
		throw new ParseException(ParseException.BracketNotMatch);
	}

	/**
	 * Scan bracket.
	 * Put index to next of ']'
	 */
	private String scanBracket() throws ParseException 
	{	
		int count = 1;
		int i = ++index;
		for (; index < code.length(); index++)
		{
			switch (CharType.TypeOf(code.charAt(index)))
			{							
				case BracketOpen:
					count++;
					break;
				case BracketClose:
					count--;
					if (count == 0) return code.substring(i, index++);						
					break;				

				case Separator:
					line++;
					break;
					
				case Unknow: throw new ParseException(ParseException.InvalidSymbol);
				
				default: break;			
			}			
		}
		throw new ParseException(ParseException.BracketNotMatch);
	}

	/**
	 * Scan brace.
	 * Put index to next of ')'
	 * @throws ParseException 
	 */
	private String scanBrace() throws ParseException 
	{
		int count = 1;
		int i = ++index;
		for (; index < code.length(); index++)
		{
			switch (CharType.TypeOf(code.charAt(index)))
			{			
				case BraceOpen:	
					count++;
					break;
					
				case BraceClose:
					count--;
					if (count == 0) return code.substring(i, index++);
					break;
				
				case Separator:
					line++;
					break;
					
				case Unknow: throw new ParseException(ParseException.InvalidSymbol);
				
				default: break;
			}
		} 
		throw new ParseException(ParseException.BracketNotMatch);
	}
}
