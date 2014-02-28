package controllers.converter;

import java.util.ArrayList;
import java.util.List;

import models.converter.CharType;
import models.converter.ParseException;
import models.converter.Token;
import models.converter.TokenType;

class Scanner {
	private static String code;
	private static int line;
	private static int index;
	
	public static List<List<Token>> scan(String text) throws ParseException
	{
		code = text;					
		line = 0;
		index = 0;
		List<List<Token>> script = new ArrayList<List<Token>>();			
		List<Token> command = new ArrayList<Token>();
		
		while (index < code.length())
		{
			switch (CharType.TypeOf(code.charAt(index))) 
			{
				case Space:	// skip space // do nothing
					index++;
					break;

				case Separator:
					line++;
					script.add(command);
					command = new ArrayList<Token>();
					index++;
					break;
					
				case Comment:		// #		
					if (command.isEmpty())				
						command.add(new Token(TokenType.Comment, scanComment()));					
					else 
						throw new ParseException(ParseException.Comment);
					break;			
					
				case BraceOpen:		// {
					command.add(new Token(TokenType.Brace, scanBrace()));
					index++;
					break;
				
				case BraceClose:	
					throw new ParseException(ParseException.BracketNotMatch);					
									
				case BracketOpen:
					command.add(new Token(TokenType.Bracket, scanBracket()));
					index++;
					break;
					
				case BracketClose:	
					throw new ParseException(ParseException.BracketNotMatch);
				
				case ParenthesisOpen:
					command.add(new Token(TokenType.Parenthesis, scanParenthesis()));
					index++;					
					break;
					
				case ParenthesisClose: 
					throw new ParseException(ParseException.BracketNotMatch);
					
				case Quote:
					command.add(new Token(TokenType.Quote, scanQuote()));
					index++;
					break;
				
				case Referent:			
					index++;
					command.add(new Token(TokenType.Referent, scanLetter()));
					break;
			
				case Letter:
					command.add(new Token(TokenType.Identify, scanLetter()));					
					break;			
				
				case BackSlash:
					if (CharType.TypeOf(code.charAt(index + 1)) == CharType.Separator)
						index = index + 2;
					else
						throw new ParseException(ParseException.InvalidSymbol);
					break;
					
				case Unknow:
					throw new ParseException(ParseException.InvalidSymbol);
			
			}	
		}
		
		return script;
	}

	private static String scanQuote() throws ParseException 
	{
		if (CharType.TypeOf(code.charAt(index)) != CharType.Quote) return "";	
		
		int i = ++index;
		for (;index < code.length(); index++)
		{
			switch(CharType.TypeOf(code.charAt(index)))
			{
				case BackSlash:
					index++;
					break;
				
				case Separator:
					line++;
					break;
				
				case Unknow: throw new ParseException(ParseException.InvalidSymbol);
					
				case Quote:	return code.substring(i, index);							
					
				case BraceClose:
				case BraceOpen:
				case BracketClose:
				case BracketOpen:
				case Comment:
				case Letter:
				case ParenthesisClose:
				case ParenthesisOpen:
				case Referent:				
				case Space:
					// do nothing
					break;											
			}
		}
				
		throw new ParseException(ParseException.MissQuote);
	}

	private static String scanLetter() 
	{
		if (CharType.TypeOf(code.charAt(index)) != CharType.Letter) return "";
		
		int i = index;
		for (;index < code.length(); index++)
		{
			if (CharType.TypeOf(code.charAt(index)) != CharType.Letter) break;
		} 
		return code.substring(i, index);
	}

	private static String scanParenthesis() throws ParseException 
	{
		if (CharType.TypeOf(code.charAt(index)) != CharType.ParenthesisOpen) return "";
		
		int count = 1;
		int i = ++index;
		for (; index < code.length(); index++)
		{
			switch (CharType.TypeOf(code.charAt(index)))
			{
				case BackSlash:
					index++;
					break;
				
				case Separator:
					line++;
					break;
				
				case Unknow: throw new ParseException(ParseException.InvalidSymbol);
					
				case ParenthesisOpen:
					count++;
					break;
				
				case ParenthesisClose:
					count--;
					if (count == 0) return code.substring(i, index);						
					break;						
					
				case BraceClose:
				case BraceOpen:
				case BracketClose:
				case BracketOpen:
				case Comment:
				case Letter:
				case Quote:
				case Referent:
				case Space:
					// do nothing
					break;					
			}
		}
		throw new ParseException(ParseException.BracketNotMatch);
	}

	private static String scanBracket() throws ParseException 
	{
		if (CharType.TypeOf(code.charAt(index)) != CharType.BraceOpen) return "";
		
		int count = 1;
		int i = ++index;
		for (; index < code.length(); index++)
		{
			switch (CharType.TypeOf(code.charAt(index)))
			{
				case BackSlash:
					index++;
					break;
				
				case Separator:
					line++;
					break;
				
				case Unknow: throw new ParseException(ParseException.InvalidSymbol);
			
				case BracketOpen:
					count++;
					break;
				case BracketClose:
					count--;
					if (count == 0) return code.substring(i, index);						
					break;				

				case BraceClose:
				case BraceOpen:								
				case Comment:
				case Letter:
				case ParenthesisClose:
				case ParenthesisOpen:
				case Quote:
				case Referent:
				case Space:
					// do nothing
					break;			
			}			
		}
		throw new ParseException(ParseException.BracketNotMatch);
	}

	private static String scanBrace() throws ParseException 
	{
		if (CharType.TypeOf(code.charAt(index)) != CharType.BraceOpen) return ""; 
		
		int count = 1;
		int i = ++index;
		for (; index < code.length(); index++)
		{
			switch (CharType.TypeOf(code.charAt(index)))
			{
				case BackSlash:
					index++;
					break;
				
				case Separator:
					line++;
					break;
				
				case Unknow: throw new ParseException(ParseException.InvalidSymbol);
			
				case BraceOpen:	
					count++;
					break;
					
				case BraceClose:
					count--;
					if (count == 0) return code.substring(i, index);
					break;
				
				default: 
					break;
			}
		} 
		throw new ParseException(ParseException.BracketNotMatch);
	}

	private static String scanComment() 
	{		
		if (CharType.TypeOf(code.charAt(index)) != CharType.Comment) return "";
		
		int i = ++index;
		while (index < code.length() && CharType.TypeOf(code.charAt(index)) != CharType.Separator) index++;
		line++;
		return code.substring(i, index);
	}
}
