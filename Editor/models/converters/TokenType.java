package models.converters;

public enum TokenType {
	Comment,
	Identify,
	Referent,
	/**
	 * "
	 */
	Quote,
	/** 
	 * [
	 */
	Bracket,
	/**
	 * {
	 */
	Brace,
	Argument;	
}