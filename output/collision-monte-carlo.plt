set terminal pdfcairo
set output 'collision.pdf'

load 'set1.pal'

DIST_FILE = 'distances2020-05-29-14-06-50.txt'
stats DIST_FILE name 'DISTS' nooutput

set logscale y 2

set xlabel "time (s)"
set ylabel "distance (m)"

plot for [i=0:DISTS_blocks-1] DIST_FILE index i with lines title columnheader(1) ls 5
