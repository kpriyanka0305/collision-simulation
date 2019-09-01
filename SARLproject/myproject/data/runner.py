import traci
import traci.constants as tc

traci.start(["sumo-gui", "-c", "cross.sumocfg"])

step = 0
temp = 0
while step < 10000:
	traci.simulationStep()

	ids = traci.vehicle.getIDCount()
	print(ids)
	#if temp < ids:
	#	print(ids)
	#	temp = ids

	step += 1

traci.close()

