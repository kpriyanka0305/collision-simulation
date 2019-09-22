set terminal qt persist
plot 'distances.txt' with lines \
   , 2.0 with lines title "near collision" \
   , 0.5 with lines title "collision"
pause -1
