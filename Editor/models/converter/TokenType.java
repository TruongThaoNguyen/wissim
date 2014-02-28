package models.converter;

public enum TokenType {	
	Identify,
	/**
	 * #
	 */
	Comment,
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
	/**
	 * -
	 */
	Argument;	
}