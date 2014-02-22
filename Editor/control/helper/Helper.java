package control.helper;

import java.awt.Color;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Helper {
	/**
	 * Convert hex color to Rgb color
	 * @param colorStr
	 * @return
	 */
	public static Color hex2Rgb(String colorStr) {
	    return new Color(
	    		Integer.valueOf( colorStr.substring( 1, 3 ), 16 ),
	            Integer.valueOf( colorStr.substring( 3, 5 ), 16 ),
	            Integer.valueOf( colorStr.substring( 5, 7 ), 16 ));
	}
	
	/**
	 * Convert rgb to hex color
	 * @param r
	 * @param g
	 * @param b
	 * @return
	 */
	public static String rgb2Hex(int r, int g, int b) {
		 return String.format("#%02x%02x%02x", r, g, b);
	}
	
	/**
	 * Check whether a string is a valid for file name
	 * @param text
	 * @return
	 */
	public static boolean isValidName(String text) {
	    Pattern pattern = Pattern.compile(
	            "# Match a valid Windows filename (unspecified file system).          \n" +
	            "^                                # Anchor to start of string.        \n" +
	            "(?!                              # Assert filename is not: CON, PRN, \n" +
	            "  (?:                            # AUX, NUL, COM1, COM2, COM3, COM4, \n" +
	            "    CON|PRN|AUX|NUL|             # COM5, COM6, COM7, COM8, COM9,     \n" +
	            "    COM[1-9]|LPT[1-9]            # LPT1, LPT2, LPT3, LPT4, LPT5,     \n" +
	            "  )                              # LPT6, LPT7, LPT8, and LPT9...     \n" +
	            "  (?:\\.[^.]*)?                  # followed by optional extension    \n" +
	            "  $                              # and end of string                 \n" +
	            ")                                # End negative lookahead assertion. \n" +
	            "[^<>:\"/\\\\|?*\\x00-\\x1F]*     # Zero or more valid filename chars.\n" +
	            "[^<>:\"/\\\\|?*\\x00-\\x1F\\ .]  # Last char is not a space or dot.  \n" +
	            "$                                # Anchor to end of string.            ", 
	            Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.COMMENTS);
	        Matcher matcher = pattern.matcher(text);
	        boolean isMatch = matcher.matches();
	        
	        return isMatch;
	}
	
	/**
	 * Get file name (with extension) of a string
	 * @param text
	 * @param ext
	 * @return
	 */
	public static String getFileNameWithExt(String text, String ext) {
		int l = ext.length();
		
		if (text.length() > l + 1) {
			String splitStr = text.substring(text.length() - l - 1, text.length());
			if (splitStr.equals("." + ext))
				return text;
		}
			
		return text + "." + ext;
	}
	
	public static String getFileNameWithoutExt(String text, String ext) {
		int l = ext.length();
		
		if (text.length() > l + 1) {
			String splitStr = text.substring(text.length() - l - 1, text.length());
			if (splitStr.equals("." + ext))
				return text.substring(0, text.length() - l - 1);
		}
			
		return text;		
	}
}
