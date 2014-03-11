package models.converter;

/**
 * TokenType.java
 * @Copyright (C) 2014, Sedic Laboratory, Hanoi University of Science and Technology
 * @Author Duc-Trong Nguyen
 * @Version 2.0
 */

public enum TokenType {	
//	Space,	
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
	Parenthesis;
//	/*
//	 * ; or /n
//	 */
//	Separator;	
}