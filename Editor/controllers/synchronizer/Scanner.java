package controllers.synchronizer;

import java.util.ArrayList;
import java.util.List;

import models.converter.CharType;
import models.converter.ParseException;
import models.converter.Token;
import models.converter.Token.TokenType;

/**
 * Scanner used to scan TCL command.
 * @Author Duc-Trong Nguyen
 */
public class Scanner 
{
	/**
	 * TCL source code.
	 */
	private String code;
	
	/**
	 * Current line.
	 */
	private int line;
	
	/**
	 * Current index.
	 */
	private int index;
	
	/**
	 * get current line.
	 * @return line that parse reached.
	 */
	public final int getLine() { return line; }
	
	/**
	 * Create new Scanner.
	 * @param text Tcl code.
	 */
	public Scanner(final String text) {
		this.code = text.replace("\r\n", "\n").replace("\r", "\n");
					
		line = 0;
		index = 0;			
	}

	/**
	 * check whether have next line in code or reached end of source code.
	 * @return true if have next line. false if it is end of code
	 */
	public final boolean haveNext() {
		return index < code.length();
	}

	/**
	 * Split tcl code to words.
	 * Command is list of words
	 * @return List of words
	 * @throws ParseException 
	 */
	public final List<String> scanCommand() throws ParseException	{	
		List<String> command = new ArrayList<String>();		
		
		// parse throw separator and space after last command.
		while (index < code.length()) {	
			CharType type = CharType.TypeOf(code.charAt(index));
			if (type == CharType.Separator)	{
				index++;
				line++;
				break;
			} else if (type == CharType.Space) {
				index++;
			} else {
				break;
			}
		}		
		
		// parse throw space before this command
		while (index < code.length()) {
			if (CharType.TypeOf(code.charAt(index)) == CharType.Space) {
				index++;
			} else {
				break;
			}
		}
				
		// scan this command
		int i = index;	
		while (index < code.length()) {
			switch (CharType.TypeOf(code.charAt(index))) {
				case Space:
					if (index > i) {
						command.add(code.substring(i, index));
					}
					while (	index < code.length() && CharType.TypeOf(code.charAt(index)) == CharType.Space) {
						index++;
					}
					i = index;
					break;
				
				case Semicolon:
					String word  = code.substring(i, index);
					if (!word.isEmpty()) {
						command.add(word);
					}
					index++;		
					while (index < code.length() 
						&& CharType.TypeOf(code.charAt(index)) == CharType.Space) index++;
					if (CharType.TypeOf(code.charAt(index)) == CharType.Separator) {
						command.add(";\n");
					} else {
						command.add(";"); 
					}
					return command;
					
				case Separator:				
					if (index > i) {
						command.add(code.substring(i, index));
					}	
					command.add("\n");
					return command;
					
				case Comment:
					if (command.size() != 0) {
						throw new ParseException(ParseException.Comment);				
					}
					while (index < code.length() && CharType.TypeOf(code.charAt(index)) != CharType.Separator) {
						index++;					
					}
					command.add(code.substring(i, index));
					command.add("\n");
					return command;

				case BackSlash:
					index++;
					if (index >= code.length() 
					|| CharType.TypeOf(code.charAt(index)) != CharType.Separator) 
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

				case Unknow: 
					throw new ParseException(ParseException.InvalidSymbol);
			}
		}
		
		if (index > i) {
			command.add(code.substring(i, index));
			//command.add("\n");
		}
		return command;
	}

	/**
	 * Split word to tokens.
	 * @return List of tokens 
	 * @throws ParseException parse exception.
	 */
	public final List<Token> scanWord() throws ParseException {
		List<Token> tokenList = new ArrayList<Token>();
		
		while (index < code.length()) {
			switch (CharType.TypeOf(code.charAt(index))) {							
				case Separator: line++;
				case Semicolon:					
					index++;
					return tokenList;
							
				case BackSlash:
					index++;
					if (index >= code.length() 
					|| CharType.TypeOf(code.charAt(index)) != CharType.Separator) 
						throw new ParseException(ParseException.InvalidSymbol);
					index++;
					line++;
					break;
					
				case Comment:
					while (index < code.length() && code.charAt(index) != '\n') {
						index++;
					}
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
					tokenList.add(new Token(TokenType.Referent, scanReferent()));
					break;
					
				case Letter:
					tokenList.add(new Token(TokenType.Identify, scanIdentify()));
					break;				
					
				case Space:										
				case BraceClose:
				case BracketClose:
				case ParenthesisClose:
				case Unknow:
					throw new ParseException(ParseException.InvalidSymbol);
			}
		}
		
		return tokenList;
	}	

	/**
	 * Split Tcl code to words. using for display only.
	 * @return List of tokens
	 * @throws ParseException parse exception
	 */
	public final List<Token> scan() throws ParseException
	{
		List<Token> tokenList = new ArrayList<Token>();
				
		while (index < code.length())
		{
			switch (CharType.TypeOf(code.charAt(index)))
			{	
				case Comment:
					int i = ++index;
					while (index < code.length() && code.charAt(index) != '\n') index++;
					tokenList.add(new Token(TokenType.Comment, code.substring(i, index)));
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
					tokenList.add(new Token(TokenType.Referent, scanReferent()));
					break;
					
				case BackSlash:
					tokenList.add(new Token(TokenType.Identify, "\\"));
					index++;
					break;
					
				case Letter:
					tokenList.add(new Token(TokenType.Identify, scanIdentify()).ChecKeyword());
					break;				
					
				case Separator:
				case Semicolon:							
					tokenList.add(new Token(TokenType.Separator, code.charAt(index) + ""));
					index++;		
					break;
					
				case Space:						
					tokenList.add(new Token(TokenType.Space, code.charAt(index) + ""));
					index++;
					break;
					
				case BraceClose:
				case BracketClose:
				case ParenthesisClose:
				case Unknow:
					throw new ParseException(ParseException.InvalidSymbol);
			}
		}
		
		return tokenList;
	}	
	
	/**
	 * Split command to term and operator.
	 * @return List of String.
	 * @throws Exception Parser exception
	 */
	public final List<String> scanOperator() throws Exception
	{
		List<String> command = new ArrayList<String>();		
				
		// scan this command
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

				case Referent:
					index++;
					break;
					
				case Letter:
					switch (code.charAt(index)) 
					{
						case '*' : case '/' : case '%' :
						case '+' : case '-' :
							if (index > i) command.add(Synchronizer.parseIdentify(code.substring(i, index)));													
							command.add(code.charAt(index) + "");							
							index++;
							i = index;
							break;
							
						case '<' : case '>' : case '!' :  case '=' :
							if (index > i) command.add(Synchronizer.parseIdentify(code.substring(i, index)));
							if (index + 1 < code.length() && code.charAt(index + 1) == '=')					
								command.add(code.substring(index, ++index));
							else
								command.add(code.charAt(index) + "");							
							index++;
							i = index;
							break;
							
						case '&' : case '|':
							if (index > i) command.add(Synchronizer.parseIdentify(code.substring(i, index)));
							if (index + 1 < code.length() && code.charAt(index) == code.charAt(index + 1))
								command.add(code.substring(index, ++index));
							else
								command.add(code.charAt(index) + "");
							index++;
							i = index;							
							break;
						
						default : index++;
					}		
					break;
					
				case Separator:				
					throw new ParseException(ParseException.MissArgument);
					
				case BraceClose:
				case BracketClose:
				case ParenthesisClose:
					throw new ParseException(ParseException.BracketNotMatch);
					
				case Semicolon:												
				case Comment:
				case Unknow: throw new ParseException(ParseException.InvalidSymbol);
			}
		}
		
		if (index > i) command.add(Synchronizer.parseIdentify(code.substring(i, index)));
		
		return command;
	}
	
	/**
	 * Scan Identify.
	 * Identify = Letter
	 * Identify = Letter + Parenthesis
	 * Put index to next of letter list.
	 * @return string is identification
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
	 * @return string is referent
	 * @throws ParseException 
	 */
	private String scanReferent() throws ParseException 
	{
		int i = ++index;
		while (index < code.length() && CharType.TypeOf(code.charAt(index)) == CharType.Letter)	index++;
		
		if (index < code.length() && CharType.TypeOf(code.charAt(index)) == CharType.ParenthesisOpen) scanParenthesis();
		return code.substring(i, index);
	}

	/**
	 * Scan quote.
	 * Put index to next of '"'
	 * @return string is value of Quote
	 * @throws ParseExeption parse exception
	 */
	private String scanQuote() throws ParseException 
	{					
		int i = ++index;
		for (; index < code.length(); index++)
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
	 * @return string is value of parenthesis
	 * @throws ParseException parse exception 
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
	 * @return string is value of bracket
	 * @throws ParseException parse exception
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
	 * @throws ParseException parse exception
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
