package controllers.parser;

import controllers.parser.ns2.NS2Parser;

/**
 * Manager the parser.
 * @author Trongnguyen
 *
 */
public class ParserManager {
	/**
	 * type for trace file.
	 * @author Trongnguyen
	 * 
	 */
	public static enum Type { NS2 }
	
	/**
	 * type of trace file.
	 * default: NS2
	 */
	private static Type type = Type.NS2;
	
	/**
	 * instance of parser.
	 */
	private static Parser parser;

	/**
	 * @return the type
	 */
	public static Type getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public static void setType(final Type type) {
		ParserManager.type = type;
	}
	
	/**
	 * Initialize parser.
	 */
	public static void initParser() {
		switch (type) {
			case NS2:
				parser = new NS2Parser();
				break;
	
			default:
				break;
		}
	}
	
	/**
	 * @return the parser
	 */
	public static Parser getParser() {
		return parser;
	}
}
