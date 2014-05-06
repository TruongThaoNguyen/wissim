#Setup a UDP connection

set opt(tn) 30	;# s-d number
set opt(interval) 20.0

set s(1)	542	;	set d(1)	90
set s(2)	549	;	set d(2)	76
set s(3)	229	;	set d(3)	41
set s(4)	111	;	set d(4)	792
set s(5)	490	;	set d(5)	85
set s(6)	463	;	set d(6)	145
set s(7)	641	;	set d(7)	231
set s(8)	527	;	set d(8)	244
set s(9)	341	;	set d(9)	255
set s(10)	144	;	set d(10)	36
set s(11)	631	;	set d(11)	223
set s(12)	239	;	set d(12)	203
set s(13)	22	;	set d(13)	35
set s(14)	300	;	set d(14)	178
set s(15)	605	;	set d(15)	257
set s(16)	28	;	set d(16)	159
set s(17)	596	;	set d(17)	57
set s(18)	421	;	set d(18)	124
set s(19)	599	;	set d(19)	146
set s(20)	60	;	set d(20)	213
set s(21)	207	;	set d(21)	132
set s(22)	345	;	set d(22)	119
set s(23)	604	;	set d(23)	6
set s(24)	245	;	set d(24)	28
set s(25)	551	;	set d(25)	112
set s(26)	63	;	set d(26)	53
set s(27)	250	;	set d(27)	327
set s(28)	416	;	set d(28)	8
set s(29)	59	;	set d(29)	19
set s(30)	186	;	set d(30)	50


for {set i 1} {$i <= $opt(tn)} {incr i} {
	$mnode_($s($i)) setdest [$mnode_($d($i)) set X_] [$mnode_($d($i)) set Y_] 0

#	set sink_($i) [new Agent/Null]
#	set udp_($i) [new Agent/UDP]	
#	$ns_ attach-agent $mnode_($d($i)) $sink_($i)
#	$ns_ attach-agent $mnode_($s($i)) $udp_($i)
#	$ns_ connect $udp_($i) $sink_($i)
#	$udp_($i) set fid_ 2
	
	#Setup a CBR over UDP connection
#	set cbr_($i) [new Application/Traffic/CBR]
#	$cbr_($i) attach-agent $udp_($i)
#	$cbr_($i) set type_ CBR
#	$cbr_($i) set packet_size_ 50
#	$cbr_($i) set rate_ 0.1Mb
#	$cbr_($i) set interval_ $opt(interval)
	#$cbr set random_ false
	
#	$ns_ at [expr 100 + [expr $i - 1] * $opt(interval) / $opt(tn)] "$cbr_($i) start"
#	$ns_ at [expr $opt(stop) - 5] "$cbr_($i) stop"
}
