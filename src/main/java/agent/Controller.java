package agent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import main.RandomVariables;
import main.SimulationProperties;

public class Controller {
	private List<RSU> allRSU = new ArrayList<RSU>(); // all *RSU* that are connected to the *Controller*
	private List<Camera> allCamera = new ArrayList<Camera>(); // all *Camera* that are connected to the *Controller*
	private List<OBU> allOBU = new ArrayList<OBU>(); // all *OBU* that are connected to the *Controller*
	
	private final SimulationProperties simParams;
	private final RandomVariables randomVars;
	
	public Controller(SimulationProperties simParams, RandomVariables randomVars) {
		this.simParams = simParams;
		this.randomVars = randomVars;
	}

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
		boolean majorVehicleFlag = false;
		double defDistance = 32;

		for (VehicleData vehicle : vehicleData) {
			Double distance = vehicle.getDistance();
			String vehType = vehicle.getType();
			if (vehType.contains("bus")) {
				majorVehicleFlag = true;
				if (distance < defDistance) {
					for (RSU rsu : allRSU) {
						rsu.WarnRSU("East");
					}
				}
			}
		}
		if (majorVehicleFlag == false) {
			for (RSU rsu : allRSU) {
				rsu.ClearRSU("East");
			}
		}
	}

	private void warningOBU(Collection<VehicleData> vehicleData) throws Exception {
		boolean majorVehicleFlag = false;
		boolean minorVehicleFlag = false;

		String vehicleType;
		double vehicleSecond;

		List<String> busIDList = new ArrayList<String>();

		for (VehicleData vehicle : vehicleData) {
			vehicleType = vehicle.getType();
			vehicleSecond = vehicle.getSeconds();

			if (vehicleType.contains(simParams.getBikePrefix())) {
				if (vehicleSecond <= randomVars.reactionTime) {
					minorVehicleFlag = true;
				}
			} else if (vehicleType.contains(simParams.getPedestrianPrefix())) {
				if (vehicleSecond <= randomVars.reactionTime) {
					minorVehicleFlag = true;
				}
			} else if (vehicleType.contains(simParams.getBusPrefix())) {
				if (vehicleSecond <= randomVars.reactionTime) {
					busIDList.add(vehicle.getId());
					majorVehicleFlag = true;
				}
			}
		}

		if (majorVehicleFlag && minorVehicleFlag) {
			for (String busID : busIDList) {
				for (OBU obu : allOBU) {
					obu.WarnOBU(busID);
				}
			}
		} else if (!minorVehicleFlag) {
			for (OBU obu : allOBU) {
				obu.UnwarnOBU();
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