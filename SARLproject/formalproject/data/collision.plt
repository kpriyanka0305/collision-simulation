set terminal pdfcairo
set output 'collision.pdf'

DIST_FILE = 'distances.txt'
stats DIST_FILE name 'DISTS' nooutput

plot for [i=0:DISTS_blocks-1] DIST_FILE index i with lines title columnheader(1) \
   , 2.0 with lines title "near collision" \
   , 0.5 with lines title "collision"
