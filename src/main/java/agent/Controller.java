package agent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Controller {
	private List<RSU> allRSU = new ArrayList<RSU>(); // all *RSU* that are connected to the *Controller*
	private List<Camera> allCamera = new ArrayList<Camera>(); // all *Camera* that are connected to the *Controller*
	private List<OBU> allOBU = new ArrayList<OBU>(); // all *OBU* that are connected to the *Controller*

	private Map<String, Double> bicycleSeconds = new HashMap<String, Double>();

	public void SendAllDataCamera(Map<String, VehicleData> vehicleData) throws Exception {
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

	private void warningRSU(Map<String, VehicleData> vehicleData) throws Exception {
		boolean bus_flag = false;
		double def_distance = 32;

		for (String vehID : vehicleData.keySet()) {
			Double distance = vehicleData.get(vehID).getDistance();
			String veh_type = vehicleData.get(vehID).getType();
			if (veh_type.contains("bus")) {
				bus_flag = true;
				if (distance < def_distance) {
					for (RSU rsu : allRSU) {
						rsu.WarnRSU("East");
					}
				}
			}
		}
		if (bus_flag == false) {
			for (RSU rsu : allRSU) {
				rsu.ClearRSU("East");
			}
		}
	}

	private void warningOBU(Map<String, VehicleData> vehicleData) throws Exception {
		boolean bus_flag = false;
		boolean bicycle_flag = false;

		String vehicleType;
		double vehicleSecond;
		double vehicleDistance;
		double vehicleSpeed;
		double vehicleLength;

		double extraSecond;

		List<String> busIDList = new ArrayList<String>();

		for (String vehicleID : vehicleData.keySet()) {
			vehicleType = vehicleData.get(vehicleID).getType();
			vehicleSecond = vehicleData.get(vehicleID).getSeconds();
			vehicleDistance = vehicleData.get(vehicleID).getDistance();

			vehicleSpeed = vehicleData.get(vehicleID).getSpeed();
			vehicleLength = vehicleData.get(vehicleID).getLength();

			extraSecond = vehicleSecond + (vehicleLength / vehicleSpeed);

			// BICYCLE
			if (vehicleType.contains("bicycle") && vehicleSecond <= 3.5 && vehicleDistance >= 2.0) {
				bicycleSeconds.put(vehicleID, vehicleSecond);
				bicycle_flag = true;
			} else if (vehicleType.contains("bicycle") && vehicleDistance < 2.0) {
				bicycleSeconds.remove(vehicleID);
			}

			// BUS
			if (vehicleType.contains("bus") && vehicleSecond <= 3.5 && vehicleDistance >= 4.0) {
				for (Double vs : bicycleSeconds.values()) {
					if ((almostEqual(vehicleSecond, vs, 0.75) || almostEqual(extraSecond, vs, 0.75))
							|| (vs > vehicleSecond && vs < extraSecond)) {
						busIDList.add(vehicleID);
						bus_flag = true;
					}
				}
			}
		}

		if (bus_flag && bicycle_flag) {
			for (String busID : busIDList) {
				for (OBU obu : allOBU) {
					obu.WarnOBU(busID);
				}
			}
		} else if (!bicycle_flag) {
			for (OBU obu : allOBU) {
				obu.tempClean();
			}
		}
	}

	boolean almostEqual(double a, double b, double eps) {
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

	void addRSU(RSU element) {
		allRSU.add(element);
		System.out.println("RSU Connected to the Controller!");
	}

	void addCamera(Camera element) {
		allCamera.add(element);
		System.out.println("Camera Connected to the Controller!");
	}
}