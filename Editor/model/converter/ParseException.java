package model.converter;

public class ParseException extends Exception {
	public static String InvalidSymbol		= "Invalid symbol";
	public static String InvalidArgument 	= "Invalid argument";
	public static String MissArgument 		= "Miss arugement";
	public static String MissQuote			= "Miss quotation mark";
	public static String BracketNotMatch	= "Braces is not match";	
	public static String Comment			= "Comment is only alow in new command\nDo you miss ';'";
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int line;
	
	public ParseException(int line, String message)
	{
		super(message);
		this.line = line;
	}
	
	public ParseException(String message)
	{
		super(message);
		this.line = -1;
	}
	
	public int getLine() {
		return line;
	}
	public void setLine(int line) {
		this.line = line;
	}
}
