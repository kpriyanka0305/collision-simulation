set terminal pdfcairo
set output 'collision.pdf'

load 'set1.pal'

DIST_FILE = 'distances.txt'
stats DIST_FILE name 'DISTS' nooutput

set logscale y 2

plot for [i=0:DISTS_blocks-1] DIST_FILE index i with lines title columnheader(1) ls i+3\
   , 2.0 with lines title "near collision" ls 1\
   , 0.5 with lines title "collision" ls 2
