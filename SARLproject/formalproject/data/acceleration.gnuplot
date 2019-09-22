set terminal pdfcairo
set output 'acceleration.pdf'
plot 'distances.txt' index 0 with lines title columnheader(1) \
   , 'accelerations.txt' index 0 with lines title columnheader(1)
