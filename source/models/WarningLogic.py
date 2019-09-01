

class WarningLogic:

    def __init__(self):
        pass

    def isDangerous(self, vehicles):
        buses = filter(lambda vehicle: vehicle.type_id == "bus", vehicles)
        bicycles = filter(lambda vehicle: vehicle.type_id == "bicycle", vehicles)

        isIncoming = lambda vehicle: vehicle.road_id[-1] == 'i'
        incomingBuses = list(filter(isIncoming, buses))
        incomingBicycles = list(filter(isIncoming, bicycles))

        incomingBusExists = len(incomingBuses)>0
        incomingBicycleExists = len(incomingBicycles)>0

        return incomingBicycleExists and incomingBusExists