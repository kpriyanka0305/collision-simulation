# To be called like this:
#    $ gnuplot -c collision-monte-carlo.plt 2020-08-24-16-22-33/distances.txt
# Or this
#    $ ls 2020-*/distances.txt | sort -r | head -n 1 | map gnuplot -c collision-monte-carlo.plt
set terminal pdfcairo
set output 'collision.pdf'

#set terminal qt persist

#load 'set1.pal'

DIST_FILE = ARG1
stats DIST_FILE name 'DISTS' nooutput

set yrange [0:]
set logscale y 2
#set grid

set key off
set title "distance between bus and bicycle over time (" . DIST_FILE . ")"
set xlabel "time (s)"
set ylabel "distance (m)"

plot for [i=0:DISTS_blocks-1] DIST_FILE index i with lines title columnheader(1), \
  2 title "near collision distance" linestyle 0
