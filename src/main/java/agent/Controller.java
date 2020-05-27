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

//	on Initialize  {
//		println("Controller spawned!")			
//	}
//
//	on Destroy {
//		println("Controller destroyed")
//	}
//	
//	on ShutdownSimulation {
//		killMe
//	}

	public void SendAllDataCamera(Map<String, Map<String, Object>> vehicle_data) throws Exception {
		// Warning RSU
		if (vehicle_data.isEmpty()) {
			for (RSU rsu : allRSU) {
//				emit(new ClearRSU("East"))
				rsu.ClearRSU("East");
			}
		} else {
			warningRSU(vehicle_data);
			warningOBU(vehicle_data);
		}
	}

	private void warningRSU(Map<String, Map<String, Object>> vehicle_data) throws Exception {
		boolean bus_flag = false;
		double def_distance = 32;

		for (String vehID : vehicle_data.keySet()) {
			Double distance = (Double) (vehicle_data.get(vehID).get("distance"));
			String veh_type = (String) (vehicle_data.get(vehID).get("type"));
			if (veh_type == "bus") {
				bus_flag = true;
				if (distance < def_distance) {
//					emit(new WarnRSU("East"))
					for (RSU rsu : allRSU) {
						rsu.WarnRSU("East");
					}
				}
			}
		}
		if (bus_flag == false) {
			for (RSU rsu : allRSU) {
//				emit(new ClearRSU("East"))
				rsu.ClearRSU("East");
			}
		}
	}

	private void warningOBU(Map<String, Map<String, Object>> vehicle_data) throws Exception {
		boolean bus_flag = false;
		boolean bicycle_flag = false;

		String vehicleType;
		double vehicleSecond;
		double vehicleDistance;
		double vehicleSpeed;
		double vehicleLength;

		double extraSecond;

		List<String> busIDList = new ArrayList<String>();

		for (String vehicleID : vehicle_data.keySet()) {

			vehicleType = (String) (vehicle_data.get(vehicleID).get("type"));
			vehicleSecond = (Double) (vehicle_data.get(vehicleID).get("seconds"));
			vehicleDistance = (Double) (vehicle_data.get(vehicleID).get("distance"));

			vehicleSpeed = (Double) (vehicle_data.get(vehicleID).get("speed"));
			vehicleLength = (Double) (vehicle_data.get(vehicleID).get("length"));

			extraSecond = vehicleSecond + (vehicleLength / vehicleSpeed);

			// BICYCLE
			if (vehicleType.contains("bicycle") && vehicleSecond <= 3.5 && vehicleDistance >= 2.0) {

				bicycleSeconds.put(vehicleID, vehicleSecond);
				// println(vehicleID + " : " + vehicleSecond)
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
//				emit(new WarnOBU(busID))
				for (OBU obu : allOBU) {
					obu.WarnOBU(busID);
				}
			}
		} else if (!bicycle_flag) {
			for (OBU obu : allOBU) {
//				emit(new tempClean)
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