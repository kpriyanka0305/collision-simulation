# To be called like this:
#    $ gnuplot -c collision-monte-carlo.plt distances2020-05-29-15-18-53.txt
set terminal pdfcairo
set output 'collision.pdf'

#set terminal qt persist

#load 'set1.pal'

DIST_FILE = ARG1
stats DIST_FILE name 'DISTS' nooutput

set yrange [0:]
#set logscale y 2
#set grid

set title "distance between bus and bicycle over time (" . DIST_FILE . ")"
set xlabel "time (s)"
set ylabel "distance (m)"

plot for [i=0:DISTS_blocks-1] DIST_FILE index i with lines title columnheader(1), \
  2 title "near collision distance" linestyle 0
