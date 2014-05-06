package controllers.converter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import controllers.WorkSpace;
import controllers.converter.shadow.SProject;
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
	public static SProject global;	
	public static List<Entry> generateEntry = new ArrayList<Entry>();
	
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
	public static String parseIdentify(String word) throws Exception 
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
					result.append(global.getInsVar(parseIdentify(token.Value)).getValue());
					break;
					
				case Bracket:
					Scanner subScanner = new Scanner(token.Value);
					List<String> command = subScanner.scanCommand();
					if (subScanner.haveNext()) throw new ParseException(ParseException.InvalidArgument);					
					
					String r = global.parse(command, false); 
					if (r != null)	result.append(r);
					else
					{
						result.append("[");
						result.append(token.Value);
						result.append("]");
					}									
					break;		
					
				case Quote:
					result.append(parseQuote(token.Value));
					break;							
			}
		}
		
		return result.toString();
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
					result.append(global.getInsVar(parseIdentify(token.Value)).getValue());
				else				
					result.append(token.print());				
			}
			result.append(" ");
		}
		
		return result.substring(0, result.length() - 1);
	}


	
	
	
	public static void main(String[] args)  throws Exception {	
		boolean isWin =  WorkSpace.isWindow();
		WorkSpace.setDirectory(isWin? "D:\\Work\\scripts\\30\\gpsr\\" : "test_store/");
		String fileName =  WorkSpace.getDirectory() + "simulate.tcl";
		BufferedReader br = new BufferedReader(new FileReader(fileName));
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
		
		String code = sb.toString();
		
		
		// ------------ using converter
		
		Converter.CTD(code);
								
		System.out.println("\n------------------------------\n");		
		
		// Do something with project object
		
		//Converter.DTC();
		
		fileName = WorkSpace.getDirectory() + "test.tcl";
		BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));
		
		for (String string : Converter.DTC()) {
			bw.write(string);
		}
		bw.close();
	}

}
