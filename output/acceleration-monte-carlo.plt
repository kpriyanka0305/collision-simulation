# To be called like this:
#    $ gnuplot -c collision-monte-carlo.plt distances2020-05-29-15-18-53.txt accelerations2020-05-29-15-18-53.txt

set terminal pdfcairo
set output 'acceleration.pdf'

DIST_FILE = ARG1
stats DIST_FILE name 'DISTS' nooutput

ACCEL_FILE = ARG2
stats ACCEL_FILE name 'ACCELS' nooutput

set title "distance between bus and bicycle over time / bus acceleration"
set xlabel "time (s)"
set ylabel "distance (m)"
set y2label "acceleration (m/s^2)"

plot for [i=0:DISTS_blocks-1] DIST_FILE index i with lines title columnheader(1) linestyle i+1 \
   , for [i=0:ACCELS_blocks-1] ACCEL_FILE index i with lines title columnheader(1) linestyle i+1
