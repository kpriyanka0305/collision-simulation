#!/bin/bash

die() {
  echo "$@" >&2
  exit 1
}

if [ $# -lt 1 ]; then
  die "usage: $0 <input-dir>"
else
  INPUT_DIR=$1
fi

WAITING_TIME_FILE=$INPUT_DIR/waitingTime.txt
SPEED_FILE=$INPUT_DIR/speeds.txt
DISTANCES_FILE=$INPUT_DIR/distances.txt
ACCELERATIONS_FILE=$INPUT_DIR/accelerations.txt

gnuplot -c waiting-time-monte-carlo.plt "$WAITING_TIME_FILE"
gnuplot -c speed-monte-carlo.plt "$SPEED_FILE"
gnuplot -c collision-monte-carlo.plt "$DISTANCES_FILE"
gnuplot -c acceleration-monte-carlo.plt "$DISTANCES_FILE" "$ACCELERATIONS_FILE"
