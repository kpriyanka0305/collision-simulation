package agent;

import de.tudresden.sumo.cmd.Vehicle;
import de.tudresden.ws.container.SumoColor;
import it.polito.appeal.traci.SumoTraciConnection;
import main.SimulationParameters;

public class OBU {
	// Location of the OBUs
	private String name;
	private String type;
	private SumoTraciConnection conn;
	private SimulationParameters simParameters;

	public OBU(String name, SumoTraciConnection conn, Controller controller, SimulationParameters simParameters)
			throws Exception {
		this.name = name;
		this.conn = conn;
		this.simParameters = simParameters;
		this.type = (String) (conn.do_job_get(Vehicle.getTypeID(name)));
		controller.OBUConnect(this);
		System.out.println("OBU: created for " + name);
	}

	public String getName() {
		return name;
	}

	public void tempClean() throws Exception {
		goDefaultColor();
	}

	public void WarnOBU(String name) throws Exception {
		if (name.equals(this.name)) {
			goRed();
		}
	}

	private void goRed() throws Exception {
		conn.do_job_set(Vehicle.setColor(name, new SumoColor(255, 0, 0, 255)));
		conn.do_job_set(Vehicle.setSpeed(name, 0.0));
	}

	private void goDefaultColor() throws Exception {
		if (type.contains("reckless")) {
			conn.do_job_set(Vehicle.setColor(name, new SumoColor(255, 140, 0, 255)));
		} else {
			conn.do_job_set(Vehicle.setColor(name, new SumoColor(255, 255, 0, 255)));
		}
		conn.do_job_set(Vehicle.setSpeed(name, simParameters.busMaxSpeed));
	}
}
