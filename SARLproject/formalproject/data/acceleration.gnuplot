set terminal pdfcairo
set output 'plot.pdf'
plot 'distances.txt' index 0 with lines title columnheader(1) \
   , 'accelerations.txt' index 0 with lines title columnheader(1) \
   , 'speeds.txt' index 0 with lines title columnheader(1)
