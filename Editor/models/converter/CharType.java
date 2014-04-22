package models.converter;

/**
 * CharType.java
 * @Copyright (C) 2014, Sedic Laboratory, Hanoi University of Science and Technology
 * @Author Duc-Trong Nguyen
 * @Version 2.0
 */

public enum CharType {
	Unknow,
	/**
	 * space and tab
	 */
	Space,
	/**
	 * \n
	 */
	Separator,
	/**
	 * ;
	 */
	Semicolon,
	/**
	 * \
	 */
	BackSlash,
	/**
	 * #
	 */
	Comment,
	/**
	 * ABC...Z abc...z 012...9 *()-_+,./
	 * Insproc and insvar's name
	 */
	Letter,
	/**
	 * $
	 */
	Referent,
	/**
	 * "
	 */
	Quote,
	/**
	 * [
	 */
	BracketOpen,
	/**
	 * ]
	 */
	BracketClose,
	/**
	 * {
	 */
	BraceOpen,
	/**
	 * }
	 */
	BraceClose,
	/**
	 * (
	 */
	ParenthesisOpen,
	/**
	 * )
	 */
	ParenthesisClose;
	
	public static CharType TypeOf(char ch) 
	{
		switch (ch) 
		{
			case '\t':	
			case ' ' : return Space;
			case '\n': return Separator;
			case ';' : return Semicolon;
			case '\\': return BackSlash; 
			case '#' : return Comment;								
			case '"' : return Quote;				
			case '[' : return BracketOpen;
			case ']' : return BracketClose;
			case '{' : return BraceOpen;
			case '}' : return BraceClose;
			case '$' : return Referent;				
			case '(' : return ParenthesisOpen;
			case ')' : return ParenthesisClose;
//			case '-' : return Argument;
			case '1' : case '2' : case '3' : case '4' : case '5' : case '6' : case '7' : case '8' :	case '9' : case '0' :
			case 'Q' : case 'W' : case 'E' : case 'R' : case 'T' : case 'Y' : case 'U' : case 'I' : case 'O' : case 'P' : 
			case 'A' : case 'S' : case 'D' : case 'F' : case 'G' : case 'H' : case 'J' : case 'K' : case 'L' : 
			case 'Z' : case 'X' : case 'C' : case 'V' : case 'B' : case 'N' : case 'M' : 
			case 'q' : case 'w' : case 'e' : case 'r' : case 't' : case 'y' : case 'u' : case 'i' : case 'o' : case 'p' : 
			case 'a' : case 's' : case 'd' : case 'f' : case 'g' : case 'h' : case 'j' : case 'k' : case 'l' : 
			case 'z' : case 'x' : case 'c' : case 'v' : case 'b' : case 'n' : case 'm' :
			case '@' : case '_' : case '.' : case ',' : case '`' : case '\'' :
			case '~' : case '!' : case '%' : case '^' : case '&' : case '*' : case '+' : case '=' : case '|' : case '/' : 
			case ':' : case '?' : case '<' : case '>' :	case '-' :			
						return Letter;			
			default:	return Unknow;
		}
	}
}
