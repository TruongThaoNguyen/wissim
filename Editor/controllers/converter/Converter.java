package controllers.converter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import controllers.Configure;
import controllers.converter.shadow.SProject;
import models.Project;
import models.converter.Entry;
import models.converter.ParseException;
import models.converter.Token;
import models.converter.Token.TokenType;

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
	public static Project CTD(String text) throws ParseException {
		generateEntry.clear();
		global = new SProject();
		global.parse(text);
		return global;
	}

	/**
	 * DTC - Design to Code.
	 * Generate TCL code from Project model.
	 * @return String
	 * @throws ParseException 
	 */
	public static String DTC() throws ParseException {
		StringBuilder sb = new StringBuilder();
		
		// configure
		for (String k : Project.configure.keySet()) {
			HashMap<String, String> h = Project.getConfig(k).get(global.getSelectedConfig(k));
			if (h != null)
			{
				for (String key : h.keySet()) 
				{										
					sb.append(global.getSelectedConfig(k));
					sb.append(" set ");
					sb.append(key);
					sb.append(" ");
					sb.append(h.get(key));
					sb.append("\n");
				}				
				
				sb.append("\n");				
			}
		}
		
		// remove raw space line
		while (generateEntry.get(0).toString().trim().isEmpty()) generateEntry.remove(0);
		
		// generate code		
		for (Entry e : generateEntry) 
		{
			String value = e.toString();	//		System.out.print(value);
			sb.append(value);
		}
		
		return sb.toString();
	}
	
	/**
	 * DTC - Design to Code
	 * Generate TCL code form Project model.
	 * Split Tcl code to token to display with style
	 * @return list of token
	 * @throws ParseException
	 */
	public static List<Token> DTC_token() throws ParseException {
		List<Token> token = new ArrayList<Token>();

		// configure
		for (String k : Project.configure.keySet()) {			
			HashMap<String, String> h = Project.getConfig(k).get(global.getSelectedConfig(k));
			if (h != null)
			{
				for (String key : h.keySet()) 
				{					
					token.add(new Token(TokenType.Identify, global.getSelectedConfig(k)));
					token.add(new Token(TokenType.Keyword, " set "));
					token.add(new Token(TokenType.Identify, key + " " + h.get(key) + "\n"));				
				}				
				token.add(new Token(TokenType.Separator, "\n"));
			}
		}
		
		// remove raw space line
		while (generateEntry.get(0).toString().trim().isEmpty()) generateEntry.remove(0);
		
		// generate code		
		for (Entry e : generateEntry) 
		{
			String value = e.toString();			System.out.print(value);
			Scanner scanner = new Scanner(value);
			token.addAll(scanner.scan());
		}
		
		return token;
	}
	
	/**
	 * Parse Identify
	 * @param word Tcl word
	 * @return a String is a Identify
	 * @throws Exception
	 */
	public static String parseIdentify(String word) throws Exception {	
		StringBuilder result = new StringBuilder();
		Scanner scanner = new Scanner(word);
		List<Token> tokenList = scanner.scanWord();
		
		if (tokenList.size() == 0) return null;		
		
		for (Token token : tokenList) 
		{					
			switch (token.Type()) 
			{
				case Parenthesis:	
					result.append("(");
					result.append(parseIdentify(token.Value()));
					result.append(")");
					break;

				case Referent:
					result.append(global.getInsVar(parseIdentify(token.Value())).getValue());
					break;
					
				case Bracket:
					Scanner subScanner = new Scanner(token.Value());
					List<String> command = subScanner.scanCommand();
					if (subScanner.haveNext()) throw new ParseException(ParseException.InvalidArgument);					
					
					String r = global.parse(command, false); 
					if (r != null)	result.append(r);
					else			result.append(token.toString());																	
					break;		
					
				case Quote:
					result.append(parseQuote(token.Value()));
					break;
									
				case Keyword:
				case Separator:
				case Identify:
				case Brace:		
					result.append(token.Value());
					break;
					
				case Space:
				case Comment: throw new ParseException(ParseException.InvalidSymbol);
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
	private static String parseQuote(String word) throws Exception {
		StringBuilder result = new StringBuilder();
		Scanner scanner = new Scanner(word);
		List<String> command = scanner.scanCommand();
		
		for (String subWord : command) 
		{
			Scanner subScanner = new Scanner(subWord);
			List<Token> tokenList = subScanner.scanWord();
		
			for (Token token : tokenList) 
			{
				if (token.Type() == TokenType.Referent)				
					result.append(global.getInsVar(parseIdentify(token.Value())).getValue());
				else				
					result.append(token.toString());				
			}
			result.append(" ");
		}
		
		return result.substring(0, result.length() - 1);
	}
	
	public static String DefaultScript() {		
		StringBuilder sb = new StringBuilder();		
		sb.append("#\n");
		sb.append("# Initialize Global Variable\n");
		sb.append("#\n");
		sb.append("\n");
		sb.append("# set start time\n");
		sb.append("set startTime [clock seconds]\n");
		sb.append("\n");
		sb.append("# set up ns simulator and nam trace\n");
		sb.append("set ns_		[new Simulator]\n");
		sb.append("set chan	[new Channel/WirelessChannel]\n");
		sb.append("set prop	[new Propagation/TwoRayGround]\n");
		sb.append("set topo	[new Topography]\n");
		sb.append("\n");
		sb.append("# run the simulator\n");
		sb.append("set tracefd	[open Trace.tr w]\n");
		sb.append("$ns_ trace-all $tracefd\n");
		sb.append("\n");
		sb.append("$topo load_flatgrid 1000 1000\n");
		sb.append("$prop topography $topo\n");
		sb.append("\n");
		sb.append("set god_ [create-god 0]\n");
		sb.append("\n");
		sb.append("# configure the nodes\n");
		sb.append("$ns_ node-config -adhocRouting GPSR \\\n");
		sb.append("		 -llType LL \\\n");
		sb.append("		 -macType Mac/802_11 \\\n");
		sb.append("		 -ifqType Queue/DropTail/PriQueue \\\n");
		sb.append("		 -ifqLen 50 \\\n");
		sb.append("		 -antType Antenna/OmniAntenna \\\n");
		sb.append("		 -propType Propagation/TwoRayGround \\\n");
		sb.append("		 -phyType Phy/WirelessPhy \\\n");
		sb.append("		 -channel [new Channel/WirelessChannel] \\\n");
		sb.append("		 -topoInstance $topo \\\n");
		sb.append("		 -agentTrace ON \\\n");
		sb.append("		 -routerTrace ON \\\n");
		sb.append("		 -macTrace OFF \\\n");
		sb.append("		 -movementTrace OFF \\\n");
		sb.append("		 -energyModel EnergyModel \\\n");
		sb.append("		 -idlePower 0.0096 \\\n");
		sb.append("		 -rxPower 0.021 \\\n");
		sb.append("		 -txPower 0.0255 \\\n");
		sb.append("		 -sleepPower 0.000648 \\\n");
		sb.append("		 -transitionPower 0.024 \\\n");
		sb.append("		 -transitionTime 0.0129 \\\n");
		sb.append("		 -initialEnergy 1000\n");
		sb.append("\n");
		sb.append("# ending the simulation\n"); 
		sb.append("$ns_ at 200 \"stop\"\n");
		sb.append("\n");
		sb.append("proc stop {} {\n");
		sb.append("	global ns_ tracefd startTime\n");
		sb.append("	$ns_ flush-trace\n");
		sb.append("	close $tracefd\n");
		sb.append("\n");
		sb.append("	puts \"end simulation\"\n");
		sb.append("\n");
		sb.append("	set runTime [clock second]\n");
		sb.append("	set runTime [expr $runTime - $startTime]\n");
		sb.append("\n");
		sb.append("	set s [expr $runTime % 60];	set runTime [expr $runTime / 60];\n");
		sb.append("	set m [expr $runTime % 60];	set runTime [expr $runTime / 60];\n");
		sb.append("\n");
		sb.append("	puts \"Runtime: $runTime hours, $m minutes, $s seconds\"\n");
		sb.append("\n");
		sb.append("	$ns_ halt\n");
		sb.append("	exit 0\n");
		sb.append("}\n");
		sb.append("\n");
		sb.append("$ns_ run\n");
		sb.append("\n");
		sb.append("##################### end script #####################\n");
		
		return sb.toString();
	}
	
	public static void main(String[] args)  throws Exception {		
		if (System.getProperty("os.name").toLowerCase().indexOf("win") > 0)
			Configure.setTclFile("D:\\Work\\scripts\\30\\gpsr\\");
		else
			Configure.setTclFile("/home/trongnguyen/scripts/30/gpsr/");
		String fileName = Configure.getDirectory() + "simulate.tcl";
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		StringBuilder sb = new StringBuilder();
	    
		String line = br.readLine();		
	    while (line != null) {
	        sb.append(line);
	        sb.append(System.lineSeparator());
	        line = br.readLine();
	    }		    		
		br.close();
		
		String code = sb.toString();
		
		// ------------ using converter
		
		Converter.CTD(code);
								
		System.out.println("\n------------------------------\n");		
		
		// Do something with project object
		
		Converter.DTC();
	}

}
