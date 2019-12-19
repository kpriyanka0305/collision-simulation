set terminal pdfcairo
set output 'collision.pdf'

load 'set1.pal'

DIST_FILE_37 = 'distances-3.7.txt'
stats DIST_FILE_37 name 'DISTS37' nooutput

DIST_FILE_42 = 'distances-4.2.txt'
stats DIST_FILE_42 name 'DISTS42' nooutput

DIST_FILE_47 = 'distances-4.7.txt'
stats DIST_FILE_47 name 'DISTS47' nooutput

set logscale y 2

plot for [i=0:DISTS37_blocks-1] DIST_FILE_37 index i with lines title columnheader(1) ls 5\
   , for [i=0:DISTS42_blocks-1] DIST_FILE_42 index i with lines title columnheader(1) ls 4\
   , for [i=0:DISTS47_blocks-1] DIST_FILE_47 index i with lines title columnheader(1) ls 3\
   , 2.0 with lines title "near collision" ls 2\
   , 0.5 with lines title "collision" ls 1
