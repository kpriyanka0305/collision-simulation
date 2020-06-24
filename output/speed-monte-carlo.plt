# To be called like this:
#    $ gnuplot -c speed-monte-carlo.plt speeds2020-06-03-07-26-58.txt

set terminal pdfcairo
set output 'speed.pdf'

SPEED_FILE = ARG1
stats SPEED_FILE name 'SPEEDS' nooutput

set key off

set title "bus speed (" . ARG1 . ")"
set xlabel "time (s)"
set ylabel "speed (m/s)"

plot for [i=0:SPEEDS_blocks-1] SPEED_FILE index i with lines title columnheader(1) linestyle i+1
