set terminal pdfcairo
set output 'acceleration.pdf'

DIST_FILE = 'distances.txt'
stats DIST_FILE name 'DISTS' nooutput

ACCEL_FILE = 'accelerations.txt'
stats ACCEL_FILE name 'ACCELS' nooutput

plot for [i=0:DISTS_blocks-1] DIST_FILE index i with lines title columnheader(1) linewidth 3 \
   , for [i=0:ACCELS_blocks-1] ACCEL_FILE index i with lines title columnheader(1) linewidth 3
