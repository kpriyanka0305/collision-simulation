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
import main.SimulationParameters;

public class SimWarningService extends Simulation {
	private SumoTraciConnection conn;
	private List<OBU> allOBUs = new ArrayList<OBU>();
	private List<RSU> allRSUs = new ArrayList<RSU>();
	private List<Camera> allCameras = new ArrayList<Camera>();
	private Map<String, Boolean> RsusStatus = new HashMap<String, Boolean>();
	private double rsuDistance = 13.0;
	private double cyclistRange = 5.0;

	private Kpi kpis;
	private Controller controller;
	private final SimulationParameters simParameters;

	public SimWarningService(SumoTraciConnection conn, Kpi kpis, SimulationParameters simParameters) throws Exception {
		this.conn = conn;
		this.kpis = kpis;
		this.simParameters = simParameters;

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
			if (type.contains("bus")) {
				if (!allOBUs.stream().anyMatch((obu) -> obu.getName().equals(v))) {
					OBU obu = new OBU(v, conn, controller, simParameters);
					allOBUs.add(obu);
				}
			} else if (type.contains("bicycle-distracted")) {
				double distanceToJunction = (Double) (vehData.get("distanceToJunction"));
				double speed = (Double) (vehData.get("speed"));
				boolean eastRsu = (Boolean) (RsusStatus.get("East"));
				String roadId = (String) (vehData.get("roadId"));
				if (eastRsu && roadId.contains("i")) {
					if (distanceToJunction > rsuDistance && distanceToJunction < rsuDistance + cyclistRange) {
						conn.do_job_set(Vehicle.setSpeed(v, 0.0));
					}
				} else {
					if (speed == 0) {
						conn.do_job_set(Vehicle.setSpeed(v, simParameters.bikeMaxSpeed));
					}
				}
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
		controller = new Controller();
		allRSUs.add(new RSU("East", 15.5, -10.5, conn, controller, this));
		allCameras.add(new Camera("CameraOne", conn, -15.0, 0.0, 2.0, 0.7, 4, controller, simParameters.defectiveITS));
	}

	private Map<String, Object> readData(String id) throws Exception {
		SumoPosition2D vehiclePosition = (SumoPosition2D) (conn.do_job_get(Vehicle.getPosition(id)));
		SumoPosition2D junctionPosition = (SumoPosition2D) (conn.do_job_get(Junction.getPosition("0")));
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
