#!/bin/sh

##
# party.sh
# Written by Joakim Sandman, October 2015.
# Last update: 8/10-15.
# Lab 1: Chattserver, Datakommunikation och datornät HT15.
# This script starts up 10 chat servers to populate the name server.
# It then kills them all after some time has passed.
##

# Run: ./party.sh
# Run without output: ./party.sh >&/dev/null
# Cancel (manually): killall server

# Enable job control
set -m

# Start up 10 instances of server and store their pids
./server "Anti-SkyNet" 51515 </dev/null &
pid1=$!
./server "Joshua - \"The only winning move is not to play\"" 51516 </dev/null &
pid2=$!
./server "Transhumanism (H+)" 51517 </dev/null &
pid3=$!
./server "Epistemological Cyberneticist" 51518 </dev/null &
pid4=$!
./server "Þursa-smiðja" 51519 </dev/null &
pid5=$!
./server "¬(µæłø ∨ (Ω»¦«¥⅝²ß)) ∧ ©" 51520 </dev/null &
pid6=$!
./server "All your base..." 51521 </dev/null &
pid7=$!
./server "...are belong to us!" 51522 </dev/null &
pid8=$!
./server " 
	" 51523 </dev/null & # [space][newline][tab]
pid9=$!
./server "GLaDOS (Friendly Edition)" 51524 </dev/null &
pid0=$!
# Add   </dev/null   before the &'s above to prevent any input
# Add   >&/dev/null   before the &'s above to suppress any output

# Let them run for some time, unless manually killed
sleep 1d

# KIll all instances of server
kill $pid1
kill $pid2
kill $pid3
kill $pid4
kill $pid5
kill $pid6
kill $pid7
kill $pid8
kill $pid9
kill $pid0
#killall server

>&2 echo
>&2 echo
>&2 echo "***** All servers terminated! *****"
>&2 echo

exit 0;

