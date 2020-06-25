#!/bin/bash

WAITING_TIME_FILE=$(ls waitingTime*.txt | sort -r | head -n 1)
SPEED_FILE=$(ls speeds*.txt | sort -r | head -n 1)

gnuplot -c waiting-time-monte-carlo.plt "$WAITING_TIME_FILE"
gnuplot -c speed-monte-carlo.plt "$SPEED_FILE"
