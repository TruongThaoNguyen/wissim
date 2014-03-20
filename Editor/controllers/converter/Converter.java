package controllers.converter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import models.Project;
import models.converter.Entry;
import models.converter.ParseException;
import models.converter.Token;
import models.converter.TokenType;

/**
 * Parser.java file.
 * @copyright (C) 2014, Sedic Laboratory, Hanoi University of Science and Technology
 * @author Duc-Trong Nguyen
 * @version 2.0
 */
public class Converter 
{		
	static SProject global;	
	public static List<Entry> generateEntry = new ArrayList<Entry>();
	private static int newObjectID = 1;
	
	/**
	 * CTD - Code to Design
	 * Parser Tcl code to a Project model 
	 * @param text
	 * @return project
	 * @throws ParseException 
	 */
	public static Project CTD(String text) throws ParseException
	{
		generateEntry.clear();
		global = new SProject();
		global.parse(text);
		return global;
	}

	/**
	 * DTC - Design to Code.
	 * Generate TCL code from Project model.
	 * @return List of String, each element of List is a line of TCL scripts
	 */
	public static List<String> DTC()
	{
		int line = 1;
		
		List<String> sb = new ArrayList<String>();		
		for (Entry e : generateEntry) {
			String t = e.print(); 
			sb.add(t);			
			System.out.print(line + "\t" + t);
			if (t.endsWith("\n")) line++;
		}			
		return sb;	
	}
	
	/**
	 * Parse Identify
	 * @param word Tcl word
	 * @return a String is a Identify
	 * @throws Exception
	 */
	static String parseIdentify(String word) throws Exception 
	{	
		StringBuilder result = new StringBuilder();
		Scanner scanner = new Scanner(word);
		List<Token> tokenList = scanner.scanWord();
		
		if (tokenList.size() == 0) return null;		
		
		for (Token token : tokenList) 
		{					
			switch (token.Type) 
			{
				case Identify:
				case Brace:		
					result.append(token.Value);
					break;

				case Parenthesis:	
					result.append("(");
					result.append(parseIdentify(token.Value));
					result.append(")");
					break;

				case Referent:
					result.append(global.insVar.get(parseIdentify(token.Value)));
					break;
					
				case Bracket:
					Scanner subScanner = new Scanner(token.Value);
					List<String> command = subScanner.scanCommand();
					if (subScanner.haveNext()) throw new ParseException(ParseException.InvalidArgument);
					int lastcount = generateEntry.size();
					
					String r = global.parse(command); 
					if (r != null)	result.append(r);
					else
					{
						result.append("[");
						result.append(token.Value);
						result.append("]");
					}
					
					while(generateEntry.size() > lastcount) generateEntry.remove(lastcount);
					break;		
					
				case Quote:
					result.append(parseQuote(token.Value));
					break;							
			}
		}
		
		return result.toString();
	}
	
	/**
	 * get new Identify name for new Object.
	 * @return new ID
	 */
	static String newIndentify() 
	{
		while (global.insObj.containsKey("_o" + newObjectID)) newObjectID++;
		return "_o" + newObjectID;
	}

	/**
	 * Parse Quote string.
	 * Replace Referent token with this value 
	 * @return string with replaced referent token
	 * @throws Exception 
	 */
	private static String parseQuote(String word) throws Exception 
	{
		StringBuilder result = new StringBuilder();
		Scanner scanner = new Scanner(word);
		List<String> command = scanner.scanCommand();
		
		for (String subWord : command) 
		{
			Scanner subScanner = new Scanner(subWord);
			List<Token> tokenList = subScanner.scanWord();
		
			for (Token token : tokenList) 
			{
				if (token.Type == TokenType.Referent)				
					result.append(global.insVar.get(parseIdentify(token.Value)));
				else				
					result.append(token.print());				
			}
			result.append(" ");
		}
		
		return result.substring(0, result.length() - 1);
	}


	
	
	
	public static void main(String[] args)  throws Exception {		
		BufferedReader br = new BufferedReader(new FileReader("/home/trongnguyen/scripts/30/ehds/simulate.tcl"));
		StringBuilder sb = null;
		try {
		    sb = new StringBuilder();
		    String line = br.readLine();
		
		    while (line != null) {
		        sb.append(line);
		        sb.append(System.lineSeparator());
		        line = br.readLine();
		    }		    
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
		    br.close();
		}
		
		String everything = sb.toString();
		CTD(everything);
								
		System.out.println("\n------------------------------\n");		
		
		DTC();				
	}

}
