class Vehicle:
    def __init__(self, traci, id):
        self._traci=traci
        self.id=id

    @property
    def road_id(self):
        return self._traci.vehicle.getRoadID(self.id)
    @property
    def type_id(self):
        return self._traci.vehicle.getTypeID(self.id)