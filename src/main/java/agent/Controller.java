package agent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import main.RandomVariables;
import main.SimulationProperties;

public class Controller {
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
			for (OBU obu : allOBU) {
				obu.UnwarnOBU();
			}
		} else {
			warningOBU(vehicleData);
		}
	}

	private void warningOBU(Collection<VehicleData> vehicleData) throws Exception {
		boolean majorVehicleFlag = false;
		boolean minorVehicleFlag = false;

		String vehicleType;
		double vehicleSecond;
		double vehicleJunctionDistance;

		List<String> busIDList = new ArrayList<String>();

		for (VehicleData vehicle : vehicleData) {
			vehicleType = vehicle.getType();
			vehicleSecond = vehicle.getSeconds();
			vehicleJunctionDistance = vehicle.getDistance();

			if (vehicleType.contains(simParams.getBikePrefix())
					|| vehicleType.contains(simParams.getPedestrianPrefix())) {
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

	public void CameraConnect(Camera CameraAgent) {
		addCamera(CameraAgent); // Adding the *Camera* that is spawned in the default context
	}

	private void removeOBU(String name) {
		allOBU.removeIf((obu) -> obu.getName().equals(name));
	}

	private void addOBU(OBU element) {
		allOBU.add(element);
	}

	private void addCamera(Camera element) {
		allCamera.add(element);
	}
}