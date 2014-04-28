package controllers.converter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import controllers.WorkSpace;
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
					result.append(global.getInsVar(parseIdentify(token.Value)).getValue());
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

	/**
	 * Create default Script.
	 * @return scripts for new project.
	 */
	public static List<String> DefaultScript()
	{
		List<String> s = new ArrayList<>();
		s.add("set opt(chan)		Channel/WirelessChannel");
		s.add("set opt(prop)		Propagation/TwoRayGround");
		set opt(netif)		Phy/WirelessPhy
		set opt(mac)		Mac/802_11
		set opt(ifq)		Queue/DropTail/PriQueue	;# for dsdv
		set opt(ll)		LL
		set opt(ant)            Antenna/OmniAntenna

		set opt(x)		1000		;# X dimension of the topography
		set opt(y)		1000		;# Y dimension of the topography

		set opt(ifqlen)		50		;# max packet in ifq
		set opt(nn)		10		;# number of nodes
		set opt(seed)		0.0
		set opt(stop)		500		;# simulation time
		set opt(tr)		Trace.tr	;# trace file
		set opt(nam)            nam.out.tr
		set opt(rp)             EHDS		;# routing protocol script (dsr or dsdv)
		set opt(lm)             "off"		;# log movement

		set opt(energymodel)    EnergyModel     ;
		set opt(radiomodel)    	RadioModel      ;
		set opt(initialenergy)  1000		;# Initial energy in Joules
		set opt(checkpoint)	995
		set opt(idlePower) 	0.0096
		set opt(rxPower) 	0.021
		set opt(txPower) 	0.0255
		set opt(sleepPower) 	0.000648
		set opt(transitionPower) 0.024
		set opt(transitionTime)  0.0129

		# ======================================================================

		LL set mindelay_		50us
		LL set delay_			25us
		LL set bandwidth_		0	;# not used

		Agent/Null set sport_		0
		Agent/Null set dport_		0

		Agent/CBR set sport_		0
		Agent/CBR set dport_		0


		# leecom: ?
		Queue/DropTail/PriQueue set Prefer_Routing_Protocols    1

		# unity gain, omni-directional antennas
		# set up the antennas to be centered in the node and 1.5 meters above it
		#leecom: ?
		Antenna/OmniAntenna set X_ 0
		Antenna/OmniAntenna set Y_ 0
		Antenna/OmniAntenna set Z_ 1.5
		Antenna/OmniAntenna set Gt_ 1.0
		Antenna/OmniAntenna set Gr_ 1.0

		# Initialize the SharedMedia interface with parameters to make
		# it work like the 914MHz Lucent WaveLAN DSSS radio interface
		Phy/WirelessPhy set CPThresh_ 10.0
		Phy/WirelessPhy set CSThresh_ 1.559e-11
		Phy/WirelessPhy set RXThresh_ 3.652e-10
		Phy/WirelessPhy set Rb_ 2*1e6
		Phy/WirelessPhy set freq_ 914e+6 
		Phy/WirelessPhy set L_ 1.0
		Phy/WirelessPhy set Pt_ 8.5872e-4    ;# 40m

		Agent/EHDS set energy_checkpoint_ $opt(checkpoint)

		# ======================================================================

		#
		# Initialize Global Variables
		#

		# set start time
		set startTime [clock seconds]

		# set up ns simulator and nam trace
		set ns_		[new Simulator]
		#set chan	[new $opt(chan)]
		set tracefd	[open $opt(tr) w]
		#set namtrace	[open $opt(nam) w]

		# run the simulator
		$ns_ trace-all $tracefd 
		$ns_ namtrace-all-wireless $namtrace $opt(x) $opt(y) 

		set topo [new Topography]
		$topo load_flatgrid $opt(x) $opt(y) 

		set prop [new $opt(prop)]
		$prop topography $topo

		set god_ [create-god $opt(nn)]

		# configure the nodes
		$ns_ node-config \
				 -adhocRouting $opt(rp) \
				 -llType $opt(ll) \
				 -macType $opt(mac) \
				 -ifqType $opt(ifq) \
				 -ifqLen $opt(ifqlen) \
				 -antType $opt(ant) \
				 -propType $opt(prop) \
				 -phyType $opt(netif) \
				 -channel [new $opt(chan)] \
				 -topoInstance $topo \
				 -agentTrace ON \
				 -routerTrace ON \
				 -macTrace OFF \
				 -movementTrace OFF \
				 -energyModel $opt(energymodel) \
				 -idlePower $opt(idlePower) \
				 -rxPower $opt(rxPower) \
				 -txPower $opt(txPower) \
				 -sleepPower $opt(sleepPower) \
				 -transitionPower $opt(transitionPower) \
				 -transitionTime $opt(transitionTime) \
				 -initialEnergy $opt(initialenergy)

		# set up nodes
		for {set i 0} {$i < $opt(nn)} {incr i} {
			set mnode_($i) [$ns_ node]
		}

		# set up node position
		#source ./topo_data.tcl
		# node location data
		$mnode_(0) set X_ 30	; $mnode_(0) set Y_ 860	; $mnode_(0) set Z_ 0
		$mnode_(1) set X_ 221	; $mnode_(1) set Y_ 896	; $mnode_(1) set Z_ 0
		$mnode_(2) set X_ 21	; $mnode_(2) set Y_ 965	; $mnode_(2) set Z_ 0
		$mnode_(3) set X_ 147	; $mnode_(3) set Y_ 951	; $mnode_(3) set Z_ 0
		$mnode_(4) set X_ 766	; $mnode_(4) set Y_ 139	; $mnode_(4) set Z_ 0
		$mnode_(5) set X_ 916	; $mnode_(5) set Y_ 9	; $mnode_(5) set Z_ 0
		$mnode_(6) set X_ 831	; $mnode_(6) set Y_ 924	; $mnode_(6) set Z_ 0
		$mnode_(7) set X_ 854	; $mnode_(7) set Y_ 431	; $mnode_(7) set Z_ 0
		$mnode_(8) set X_ 983	; $mnode_(8) set Y_ 906	; $mnode_(8) set Z_ 0
		$mnode_(9) set X_ 501	; $mnode_(9) set Y_ 22	; $mnode_(9) set Z_ 0


		for {set i 0} {$i < $opt(nn)} { incr i } {
			$ns_ initial_node_pos $mnode_($i) 5
		}

		# telling nodes when the simulator ends
		for {set i 0} {$i < $opt(nn)} {incr i} {
			$ns_ at $opt(stop).000000001 "$mnode_($i) reset"
		}

		#source ./cbr.tcl

		set opt(tn) 3	;# s-d number
		set opt(interval) 20.0

		set s(0)	6	;	set d(0)	0
		set s(1)	2	;	set d(1)	9
		set s(2)	9	;	set d(2)	7

		for {set i 0} {$i < $opt(tn)} {incr i} {
			set sink_($i) [new Agent/Null]
			$ns_ attach-agent $mnode_($d($i)) $sink_($i)

			set udp_($i) [new Agent/UDP]
			$mnode_($s($i)) setdest [$mnode_($d($i)) set X_] [$mnode_($d($i)) set Y_] 0
			$ns_ attach-agent $mnode_($s($i)) $udp_($i)
			$ns_ connect $udp_($i) $sink_($i)
			$udp_($i) set fid_ 2
			
			#Setup a CBR over UDP connection
			set cbr_($i) [new Application/Traffic/CBR]
			$cbr_($i) attach-agent $udp_($i)
			$cbr_($i) set type_ CBR
			$cbr_($i) set packet_size_ 50
			$cbr_($i) set rate_ 0.1Mb
			$cbr_($i) set interval_ $opt(interval)
			#$cbr set random_ false
			
			$ns_ at [expr 100 + [expr $i - 1] * $opt(interval) / $opt(tn)] "$cbr_($i) start"
			$ns_ at [expr $opt(stop) - 5] "$cbr_($i) stop"
		}

		# ending nam and the simulation
		$ns_ at $opt(stop) "$ns_ nam-end-wireless $opt(stop)" 
		$ns_ at $opt(stop) "stop" 

		proc stop {} {
			global ns_ tracefd startTime	;# namtrace
			$ns_ flush-trace
			close $tracefd
			#close $namtrace
			
			puts "end simulation"

			set runTime [clock second]
			set runTime [expr $runTime - $startTime]

			set s [expr $runTime % 60];	set runTime [expr $runTime / 60];
			set m [expr $runTime % 60];	set runTime [expr $runTime / 60];

			puts "Runtime: $runTime hours, $m minutes, $s seconds"

			$ns_ halt
			exit 0
		}

		$ns_ run

		########### end script #####################
	}
	
	
	
	public static void main(String[] args)  throws Exception {	
		boolean isWin =  WorkSpace.isWindow();
		WorkSpace.setDirectory(isWin? "D:\\Work\\scripts\\30\\gpsr\\" : "/home/trongnguyen/scripts/30/ehds/");
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
		
		Converter.DTC();
		
//		
//		for (String string : Converter.DTC()) {
//			System.out.print(string);
//		}
		
	}

}
