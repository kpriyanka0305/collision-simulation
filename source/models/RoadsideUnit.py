
class RoadsideUnit:
    roadside_unit_count = 0
    def __init__(self, traci, x, y, width, height):
        self.traci = traci
        RoadsideUnit.roadside_unit_count += 1
        self.id="RoadsideUnit_{}".format(RoadsideUnit.roadside_unit_count)
        rectangle = Rectangle(x,y,width,height)
        self.traci.polygon.add(
            self.id,
            shape=rectangle.shape,
            color=(127, 127, 127, 255),
            fill=True)

    def warn(self):
        self.traci.polygon.setColor(self.id, color=(255,0,0,255))

    def clearWarning(self):
        self.traci.polygon.setColor(self.id, color=(0,255,0,255))

class Rectangle:
    def __init__(self, x, y, width, height):
        self.shape = [(x,y), (x+width,y), (x+width,y+height), (x,y+height)]
