# To be called like this:
#    $ gnuplot -c collision-monte-carlo.plt distances2020-05-29-15-18-53.txt
set terminal pdfcairo
set output 'collision.pdf'

#load 'set1.pal'

DIST_FILE = ARG1
stats DIST_FILE name 'DISTS' nooutput

#set logscale y 2

set title "distance between bus and bicycle over time, with varying bus speeds"
set xlabel "time (s)"
set ylabel "distance (m)"

plot for [i=0:DISTS_blocks-1] DIST_FILE index i with lines title columnheader(1)
