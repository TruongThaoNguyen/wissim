LL set mindelay_		50us
LL set delay_			25us
LL set bandwidth_		0

Agent/Null set sport_		0
Agent/Null set dport_		0

Agent/CBR set sport_		0
Agent/CBR set dport_		0

Queue/DropTail/PriQueue set Prefer_Routing_Protocols    1

# unity gain, omni-directional antennas
# set up the antennas to be centered in the node and 1.5 meters above it
Antenna/OmniAntenna set X_ 0
Antenna/OmniAntenna set Y_ 0
Antenna/OmniAntenna set Z_ 1.5
Antenna/OmniAntenna set Gt_ 1.0
Antenna/OmniAntenna set Gr_ 1.0

# Initialize the SharedMedia interface with parameters to make
# it work like the 914MHz Lucent WaveLAN DSSS radio interface
Phy/WirelessPhy set CPThresh_ 10.0
Phy/WirelessPhy set CSThresh_ 1.559e-11
Phy/WirelessPhy set RXThresh_ 1.20174e-07 ; # 40 m
Phy/WirelessPhy set Rb_ 2*1e6
Phy/WirelessPhy set freq_ 914e+08 
Phy/WirelessPhy set L_ 1.0
Phy/WirelessPhy set Pt_ 0.281838

#	Pt_ = 8.5872e-4; // For 40m transmission range.
#	Pt_ = 7.214e-3;  // For 100m transmission range.
#	Pt_ = 0.2818; // For 250m transmission range.


#	RxThresh 
#	1m : 0.000192278     
#	5m : 7.69113e-06
#	9m : 2.37381e-06
#	10m : 1.92278e-06
#	25m : 3.07645e-07
#	40m : 1.20174e-07
#	50m : 7.69113e-08
#	75m : 3.41828e-08
#	100m : 1.42681e-08
#	125m : 5.8442e-09
#	150m : 2.81838e-09
#	175m : 1.52129e-09
#	200m : 8.91754e-10
#	225m : 5.56717e-10
#	250m : 3.65262e-10
#	500m : 2.28289e-11
#	1000m : 1.42681e-12

# ======================================================================

#
# Initialize Global Variables
#

# set start time
set startTime [clock seconds]

# set up ns simulator and nam trace
set ns_		[new Simulator]
set chan	[new Channel/WirelessChannel]
set prop	[new Propagation/TwoRayGround]
set topo	[new Topography]

# run the simulator
set tracefd	[open Trace.tr w]
$ns_ trace-all $tracefd 

$topo load_flatgrid 1000 1000
$prop topography $topo

set god_ [create-god 0]

# configure the nodes
$ns_ node-config -adhocRouting GPSR \
		 -llType LL \
		 -macType Mac/802_11 \
		 -ifqType Queue/DropTail/PriQueue \
		 -ifqLen 50 \
		 -antType Antenna/OmniAntenna \
		 -propType Propagation/TwoRayGround \
		 -phyType Phy/WirelessPhy \
		 -channel [new Channel/WirelessChannel] \
		 -topoInstance $topo \
		 -agentTrace ON \
		 -routerTrace ON \
		 -macTrace OFF \
		 -movementTrace OFF \
		 -energyModel EnergyModel \
		 -idlePower 0.0096 \
		 -rxPower 0.021 \
		 -txPower 0.0255 \
		 -sleepPower 0.000648 \
		 -transitionPower 0.024 \
		 -transitionTime 0.0129 \
		 -initialEnergy 1000

# ending the simulation 
$ns_ at 200 "stop" 

proc stop {} {
	global ns_ tracefd startTime
	$ns_ flush-trace
	close $tracefd
	
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
