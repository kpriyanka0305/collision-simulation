#!/bin/bash

WAITING_TIME_FILE=$(ls waitingTime*.txt | sort -r | head -n 1)
SPEED_FILE=$(ls speeds*.txt | sort -r | head -n 1)
DISTANCES_FILE=$(ls distances*.txt | sort -r | head -n 1)
ACCELERATIONS_FILE=$(ls accelerations*.txt | sort -r | head -n 1)

gnuplot -c waiting-time-monte-carlo.plt "$WAITING_TIME_FILE"
gnuplot -c speed-monte-carlo.plt "$SPEED_FILE"
gnuplot -c collision-monte-carlo.plt "$DISTANCES_FILE"
gnuplot -c acceleration-monte-carlo.plt "$DISTANCES_FILE" "$ACCELERATIONS_FILE"
