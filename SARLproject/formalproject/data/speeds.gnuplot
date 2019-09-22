set terminal pdfcairo
set output 'plot.pdf'
plot 'accelerations.txt' index 0 with lines title columnheader(1) \
   , 'speeds.txt' index 0 with lines title columnheader(1)
