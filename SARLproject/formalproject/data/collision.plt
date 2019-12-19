set terminal pdfcairo
set output 'collision.pdf'

load 'set1.pal'

DIST_FILE_A = 'distances-3.7.txt'
stats DIST_FILE_A name 'DISTS_A' nooutput

DIST_FILE_B = 'distances-4.15.txt'
stats DIST_FILE_B name 'DISTS_B' nooutput

DIST_FILE_C = 'distances-4.7.txt'
stats DIST_FILE_C name 'DISTS_C' nooutput

set logscale y 2

plot for [i=0:DISTS_A_blocks-1] DIST_FILE_A index i with lines title columnheader(1) ls 5\
   , for [i=0:DISTS_B_blocks-1] DIST_FILE_B index i with lines title columnheader(1) ls 4\
   , for [i=0:DISTS_C_blocks-1] DIST_FILE_C index i with lines title columnheader(1) ls 3\
   , 2.0 with lines title "near collision (2m)" ls 2\
   , 0.5 with lines title "collision (0.5m)" ls 1
