import traci
import sys

from models.WarningLogic import WarningLogic
from models.Vehicle import Vehicle
from models.RoadsideUnit import RoadsideUnit

try:
    sumocfg_file = str(sys.argv[1])
    traci.start(["sumo-gui", "-c", sumocfg_file])
    
except:
    traci.start(["sumo-gui", "-c", "../intersection/scenario1/cross.sumocfg"])


warningLogic = WarningLogic()
roadsideUnit_1 = RoadsideUnit(traci, x=215,y=193,width=100,height=14)
roadsideUnit_2 = RoadsideUnit(traci, x=85,y=193,width=100,height=14)
while (traci.simulation.getMinExpectedNumber() > 0):
    traci.simulationStep()
    vehicles = [Vehicle(traci, vehicle_id) for vehicle_id in traci.vehicle.getIDList()]
    if(warningLogic.isDangerous(vehicles)):
        roadsideUnit_1.warn()
        roadsideUnit_2.warn()
    else:
        roadsideUnit_1.clearWarning()
        roadsideUnit_2.clearWarning()

traci.close()