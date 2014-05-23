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
