#!/usr/bin/env bash

./plot.sh $(ls -d 2021-* | sort -r | head -n 1)
