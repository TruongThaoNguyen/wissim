package models.converter;

/**
 * Token.java
 * @Copyright (C) 2014, Sedic Laboratory, Hanoi University of Science and Technology
 * @Author Duc-Trong Nguyen
 * @Version 2.0
 */

public class Token {	
	/**
	 * type of Token
	 */
	public TokenType Type;
	
	/**
	 * Value of token
	 */
	public String Value;	
	
	public Token(TokenType type, String value) 
	{
		this.Type  = type;
		this.Value = value;
	}	

//	public void ChecKeyword()
//	{
//		if (Type == TokenType.Identify)
//		{
//			switch (Value) {
//			case "string" :		case "subst" :		case "regexp" :		case "regsub" :		case "scan" :		
//			case "format" :		case "binary" :		case "list" :		case "split" :		case "exit":	
//			case "join" :		case "concat" :		case "llength" :	case "lrange" :		case "lsearch" :	
//			case "lreplace" :	case "lindex" :		case "lsort" :		case "linsert" :	case "lrepeat" :	
//			case "dict" :		case "if" :			case "else" :		case "elseif" :		case "then" :	
//			case "for" :		case "foreach" :	case "switch" :		case "case" :		case "while" :	
//			case "continue" :	case "return" :		case "break" :		case "catch" :		case "error" :	
//			case "eval" :		case "uplevel" :	case "after" :		case "update" :		case "vwait" :	
//			case "proc" :		case "rename" :		case "set" :		case "lset" :		case "lassign" :	
//			case "unset" :		case "namespace" :	case "variable" :	case "upvar" :		case "global" :	
//			case "trace" :		case "array" :		case "incr" :		case "append" :		case "lappend" :	
//			case "expr" :		case "file" :		case "open" :		case "close" :		case "socket" :	
//			case "fconfigure" :	case "puts" :		case "gets" :		case "read" :		case "seek" :	
//			case "tell" :		case "eof" :		case "flush" :		case "fblocked" :	case "fcopy" :	
//			case "fileevent" :	case "source" :		case "load" :		case "unload" :		case "package" :	
//			case "info" :		case "interp" :		case "history" :	case "bgerror" :	case "unknown" :	
//			case "memory" :		case "cd" :			case "pwd" :		case "clock" :		case "time" :	
//			case "exec" :		case "glob" :		case "pid" :		
//				Type = TokenType.Keyword;
//				break;
//			}
//		}
//	}
	
	/**
	 * print without space
	 * @return
	 */
	public String print() 
	{
		switch (Type) 
		{
			case Identify:		return Value;
			case Referent:		return '$' + Value;
			case Brace:			return '{' + Value + '}';		
			case Bracket:		return '[' + Value + ']';
			case Parenthesis:	return '(' + Value + ')';
			case Quote:			return '"' + Value + '"'; 
		}
		
		return Value;
	}
}
