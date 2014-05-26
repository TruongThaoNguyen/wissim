package models.converter;

/**
 * Token.java
 * @Copyright (C) 2014, Sedic Laboratory, Hanoi University of Science and Technology
 * @Author Duc-Trong Nguyen
 * @Version 2.0
 */

public class Token {
	public enum TokenType {	
		Comment,
		Keyword,
		Space,
		Identify,
		/**
		 * $
		 */
		Referent,
		/**
		 * "
		 */
		Quote,
		/** 
		 * []
		 */
		Bracket,
		/**
		 * {}
		 */
		Brace,
		/**
		 * ()
		 */
		Parenthesis,
		/*
		 * ; or /n
		 */
		Separator;	
	}
	
	/**
	 * type of Token
	 */
	private TokenType type;
	
	/**
	 * Value of token
	 */
	private String value;	
	
	public Token(TokenType type, String value) 
	{
		this.type  = type;
		this.value = value;
	}	

	public TokenType Type() {
		return type;
	}
	
	public String Value() {
		return value;
	}
	
	public Token ChecKeyword()
	{
		if (type == TokenType.Identify)
		{
			switch (value) {
			case "string" :		case "subst" :		case "regexp" :		case "regsub" :		case "scan" :		
			case "format" :		case "binary" :		case "list" :		case "split" :		case "exit":	
			case "join" :		case "concat" :		case "llength" :	case "lrange" :		case "lsearch" :	
			case "lreplace" :	case "lindex" :		case "lsort" :		case "linsert" :	case "lrepeat" :	
			case "dict" :		case "if" :			case "else" :		case "elseif" :		case "then" :	
			case "for" :		case "foreach" :	case "switch" :		case "case" :		case "while" :	
			case "continue" :	case "return" :		case "break" :		case "catch" :		case "error" :	
			case "eval" :		case "uplevel" :	case "after" :		case "update" :		case "vwait" :	
			case "proc" :		case "rename" :		case "set" :		case "lset" :		case "lassign" :	
			case "unset" :		case "namespace" :	case "variable" :	case "upvar" :		case "global" :	
			case "trace" :		case "array" :		case "incr" :		case "append" :		case "lappend" :	
			case "expr" :		case "file" :		case "open" :		case "close" :		case "socket" :	
			case "fconfigure" :	case "puts" :		case "gets" :		case "read" :		case "seek" :	
			case "tell" :		case "eof" :		case "flush" :		case "fblocked" :	case "fcopy" :	
			case "fileevent" :	case "source" :		case "load" :		case "unload" :		case "package" :	
			case "info" :		case "interp" :		case "history" :	case "bgerror" :	case "unknown" :	
			case "memory" :		case "cd" :			case "pwd" :		case "clock" :		case "time" :	
			case "exec" :		case "glob" :		case "pid" :		
				type = TokenType.Keyword;
				break;
			}
		}
		return this;
	}
	
	/**
	 * print without space
	 * @return
	 */
	public String toString() 
	{
		switch (type) 
		{			
			case Space: 
			case Separator:
			case Keyword:
			case Identify:		return value;
			case Comment:		return '#' + value;
			case Referent:		return '$' + value;
			case Brace:			return '{' + value + '}';		
			case Bracket:		return '[' + value + ']';
			case Parenthesis:	return '(' + value + ')';
			case Quote:			return '"' + value + '"';					
		}
		return value;		
	}
}
