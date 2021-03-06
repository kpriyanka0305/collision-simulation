package agent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.tudresden.sumo.cmd.Junction;
import de.tudresden.sumo.cmd.Vehicle;
import de.tudresden.ws.container.SumoPosition2D;
import it.polito.appeal.traci.SumoTraciConnection;
import kpi.Kpi;
import main.RandomVariables;
import main.SimulationProperties;

public class SimWarningService extends Simulation {
	private SumoTraciConnection conn;
	private List<OBU> allOBUs = new ArrayList<OBU>();
	private List<Camera> allCameras = new ArrayList<Camera>();
	private Map<String, Boolean> RsusStatus = new HashMap<String, Boolean>();

	private Kpi kpis;
	private Controller controller;
	private final SimulationProperties simParams;
	private final RandomVariables randomVars;

	public SimWarningService(SumoTraciConnection conn, Kpi kpis, SimulationProperties simParams,
			RandomVariables randomVars) throws Exception {
		this.conn = conn;
		this.kpis = kpis;
		this.simParams = simParams;
		this.randomVars = randomVars;

		RsusStatus.put("East", false);
		RsusStatus.put("West", false);

		spawnElements();
	}

	public void RSUStatus(String name, boolean status) {
		if (name.equals("East")) {
			RsusStatus.put("East", status);
		} else {
			RsusStatus.put("West", status);
		}
	}

	@Override
	public void step() throws Exception {
		numSteps++;
		conn.do_timestep();

		@SuppressWarnings("unchecked")
		List<String> vehicles = (List<String>) (conn.do_job_get(Vehicle.getIDList()));

		for (String v : vehicles) {
			Map<String, Object> vehData = readData(v);
			String type = (String) (vehData.get("type"));
			if (type.contains(simParams.getBusPrefix())) {
				if (!allOBUs.stream().anyMatch((obu) -> obu.getName().equals(v))) {
					OBU obu = new OBU(v, conn, controller);
					allOBUs.add(obu);
				}
				// TODO: this clause is still specific to the Neckerspoel scenario. Need to
				// generalize it. Also, the calculation of distance to junction is different
				// from how Camera does it. This may lead to bugs???
			} else if (type.contains("bicycle-distracted")) {
				// We don't want this to happen for now
				throw new UnsupportedOperationException("support for distracted bicycles not implemented");
//				double distanceToJunction = (Double) (vehData.get("distanceToJunction"));
//				double speed = (Double) (vehData.get("speed"));
//				boolean eastRsu = (Boolean) (RsusStatus.get("East"));
//				String roadId = (String) (vehData.get("roadId"));
//				if (eastRsu && roadId.contains("i")) {
//					if (distanceToJunction > rsuDistance && distanceToJunction < rsuDistance + cyclistRange) {
//						conn.do_job_set(Vehicle.setSpeed(v, 0.0));
//					}
//				} else {
//					if (speed == 0) {
//						conn.do_job_set(Vehicle.setSpeed(v, simParameters.bikeMaxSpeed));
//					}
//				}
			}
		}

		for (OBU obu : allOBUs) {
			boolean flag = false;

			for (String v : vehicles) {
				if (obu.getName().equals(v)) {
					flag = true;
					break;
				}
			}

			if (!flag) {
				controller.OBUDisconnect(obu.getName());
				allOBUs.removeIf((o) -> o.getName().equals(obu.getName()));
				break;
			}
		}

		for (Camera cam : allCameras) {
			cam.observeSituation();
		}

		kpis.checkKPIs();
	}

	private void spawnElements() throws Exception {
		controller = new Controller(simParams, randomVars);
//		allRSUs.add(new RSU("East", 15.5, -10.5, conn, controller, this));
		allCameras.add(new Camera(simParams, "CameraOne", conn, /* x */ 99.0, /* y */ -13.0, /* size */ 2.0, /* height */ 0.7,
				/* angle */ 110, controller, randomVars.defectiveITS));
	}

	private Map<String, Object> readData(String id) throws Exception {
		SumoPosition2D vehiclePosition = (SumoPosition2D) (conn.do_job_get(Vehicle.getPosition(id)));
		// TODO: This code is specific to a scenario. Need to generalize.
		SumoPosition2D junctionPosition = (SumoPosition2D) (conn.do_job_get(Junction.getPosition("StationspleinSW")));
		String type = (String) (conn.do_job_get(Vehicle.getTypeID(id)));
		double speed = (Double) (conn.do_job_get(Vehicle.getSpeed(id)));
		double length = (Double) (conn.do_job_get(Vehicle.getLength(id)));
		double accel = (Double) (conn.do_job_get(Vehicle.getAccel(id)));
		String roadId = (String) (conn.do_job_get(Vehicle.getRoadID(id)));
		double dX = Math.abs(junctionPosition.x - vehiclePosition.x);
		double dY = Math.abs(junctionPosition.y - vehiclePosition.y);
		double distanceToJunction = Math.sqrt(dX * dX + dY * dY) - length / 2;
		Map<String, Object> myMap = new HashMap<>();
		myMap.put("type", type);
		myMap.put("speed", speed);
		myMap.put("accel", accel);
		myMap.put("distanceToJunction", distanceToJunction);
		myMap.put("roadId", roadId);
		myMap.put("length", length);
		return myMap;
	}
}
