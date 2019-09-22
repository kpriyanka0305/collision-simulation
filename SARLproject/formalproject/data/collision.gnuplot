set terminal qt persist
plot 'distances.txt' index 0 with lines title columnheader(1) \
   , 2.0 with lines title "near collision" \
   , 0.5 with lines title "collision"
pause -1
