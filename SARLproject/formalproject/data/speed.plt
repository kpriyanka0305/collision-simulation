set terminal pdfcairo
set output 'speed.pdf'

ACCEL_FILE = 'accelerations.txt'
stats ACCEL_FILE name 'ACCELS' nooutput

SPEED_FILE = 'speeds.txt'
stats SPEED_FILE name 'SPEEDS' nooutput

set xlabel "time (s)"
set ylabel "speed (m/s)"
set y2label "acceleration (m/s^2)"

plot for [i=0:ACCELS_blocks-1] ACCEL_FILE index i with lines title columnheader(1) linewidth 3 \
   , for [i=0:SPEEDS_blocks-1] SPEED_FILE index i with lines title columnheader(1) linewidth 3
