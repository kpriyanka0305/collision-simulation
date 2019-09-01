FROM ubuntu:18.04

RUN apt-get update && apt-get install -y \
    sumo \
    sumo-tools \
    && rm -rf /var/lib/apt/lists/*

RUN adduser sumo_user --disabled-password

COPY ./intersection /intersection
COPY ./source /source

CMD sumo -c intersection/cross.sumocfg
#CMD python source/runner.py
