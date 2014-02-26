package model.converter;

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