package agent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import main.SimulationParameters;

public class Controller {
	private List<RSU> allRSU = new ArrayList<RSU>(); // all *RSU* that are connected to the *Controller*
	private List<Camera> allCamera = new ArrayList<Camera>(); // all *Camera* that are connected to the *Controller*
	private List<OBU> allOBU = new ArrayList<OBU>(); // all *OBU* that are connected to the *Controller*

	public void SendAllDataCamera(Collection<VehicleData> vehicleData) throws Exception {
		// Warning RSU
		if (vehicleData.isEmpty()) {
			for (RSU rsu : allRSU) {
				rsu.ClearRSU("East");
			}
		} else {
			warningRSU(vehicleData);
			warningOBU(vehicleData);
		}
	}

	private void warningRSU(Collection<VehicleData> vehicleData) throws Exception {
		boolean busFlag = false;
		double defDistance = 32;

		for (VehicleData vehicle : vehicleData) {
			Double distance = vehicle.getDistance();
			String vehType = vehicle.getType();
			if (vehType.contains("bus")) {
				busFlag = true;
				if (distance < defDistance) {
					for (RSU rsu : allRSU) {
						rsu.WarnRSU("East");
					}
				}
			}
		}
		if (busFlag == false) {
			for (RSU rsu : allRSU) {
				rsu.ClearRSU("East");
			}
		}
	}

	private void warningOBU(Collection<VehicleData> vehicleData) throws Exception {
		boolean busFlag = false;
		boolean bicycleFlag = false;

		String vehicleType;
		double vehicleSecond;
		double vehicleDistance;

		List<String> busIDList = new ArrayList<String>();

		for (VehicleData vehicle : vehicleData) {
			vehicleType = vehicle.getType();
			vehicleSecond = vehicle.getSeconds();
			vehicleDistance = vehicle.getDistance();

			// BICYCLE
			if (vehicleType.contains(SimulationParameters.BIKE_PREFIX)) {
				if (vehicleSecond <= 3.5 && vehicleDistance >= 2.0) {
					bicycleFlag = true;
				}
			}

			// PEDESTRIAN
			if (vehicleType.contains(SimulationParameters.PEDESTRIAN_PREFIX)) {
				if (vehicleSecond <= 10.0) {
					bicycleFlag = true;
				}
			}

			// BUS
			if (vehicleType.contains(SimulationParameters.BUS_PREFIX)) {
				if (vehicleSecond <= 3.5) {
					busIDList.add(vehicle.getId());
					busFlag = true;
				}
			}
		}

		if (busFlag && bicycleFlag) {
			for (String busID : busIDList) {
				for (OBU obu : allOBU) {
					obu.WarnOBU(busID);
				}
			}
		} else if (!bicycleFlag) {
			for (OBU obu : allOBU) {
				obu.tempClean();
			}
		}
	}

	public void OBUDisconnect(String name) {
		removeOBU(name);
	}

	public void OBUConnect(OBU obu) {
		addOBU(obu);
	}

	public void RSUConnect(RSU RSUAgent) {
		addRSU(RSUAgent); // Adding the *RSU* that is spawned in the default context
	}

	public void CameraConnect(Camera CameraAgent) {
		addCamera(CameraAgent); // Adding the *Camera* that is spawned in the default context
	}

	private void removeOBU(String name) {
		allOBU.removeIf((obu) -> obu.getName().equals(name));
	}

	private void addOBU(OBU element) {
		allOBU.add(element);
	}

	private void addRSU(RSU element) {
		allRSU.add(element);
	}

	private void addCamera(Camera element) {
		allCamera.add(element);
	}
}