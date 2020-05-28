package agent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Controller {
	private List<RSU> allRSU = new ArrayList<RSU>(); // all *RSU* that are connected to the *Controller*
	private List<Camera> allCamera = new ArrayList<Camera>(); // all *Camera* that are connected to the *Controller*
	private List<OBU> allOBU = new ArrayList<OBU>(); // all *OBU* that are connected to the *Controller*

	private Map<String, Double> bicycleSeconds = new HashMap<String, Double>();

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
		double vehicleSpeed;
		double vehicleLength;

		double extraSecond;

		List<String> busIDList = new ArrayList<String>();

		for (VehicleData vehicle : vehicleData) {
			vehicleType = vehicle.getType();
			vehicleSecond = vehicle.getSeconds();
			vehicleDistance = vehicle.getDistance();

			vehicleSpeed = vehicle.getSpeed();
			vehicleLength = vehicle.getLength();

			extraSecond = vehicleSecond + (vehicleLength / vehicleSpeed);

			// BICYCLE
			if (vehicleType.contains("bicycle") && vehicleSecond <= 3.5 && vehicleDistance >= 2.0) {
				bicycleSeconds.put(vehicle.getId(), vehicleSecond);
				bicycleFlag = true;
			} else if (vehicleType.contains("bicycle") && vehicleDistance < 2.0) {
				bicycleSeconds.remove(vehicle.getId());
			}

			// BUS
			if (vehicleType.contains("bus") && vehicleSecond <= 3.5 && vehicleDistance >= 4.0) {
				for (Double vs : bicycleSeconds.values()) {
					if ((almostEqual(vehicleSecond, vs, 0.75) || almostEqual(extraSecond, vs, 0.75))
							|| (vs > vehicleSecond && vs < extraSecond)) {
						busIDList.add(vehicle.getId());
						busFlag = true;
					}
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

	private boolean almostEqual(double a, double b, double eps) {
		return Math.abs(a - b) < eps;
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
		System.out.println("RSU Connected to the Controller!");
	}

	private void addCamera(Camera element) {
		allCamera.add(element);
		System.out.println("Camera Connected to the Controller!");
	}
}