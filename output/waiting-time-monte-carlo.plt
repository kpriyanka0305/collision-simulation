# To be called like this:
#    $ gnuplot -c waiting-time-monte-carlo.plt waitingTime2020-06-25-08-57-04.txt

set terminal pdfcairo
set output 'waiting-time.pdf'

WAITTIME_FILE = ARG1
#stats WAITTIME_FILE name 'WAITTIMES' nooutput

set boxwidth 0.5 relative
set style fill solid

set logscale y 2
set yrange [0:]

set title "bus wait times (" . WAITTIME_FILE . ")"

#set title "distance bus bicycle / bus acceleration (" . ARG1 . ")"
set xlabel "wait time (s)"
set ylabel "number of buses"
#set y2label "acceleration (m/s^2)"

plot WAITTIME_FILE with boxes title "bus wait times"
