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
	private SimulationParameters simParams;

	public OBU(String name, SumoTraciConnection conn, Controller controller, SimulationParameters simParams)
			throws Exception {
		this.name = name;
		this.conn = conn;
		this.simParams = simParams;
		this.type = (String) (conn.do_job_get(Vehicle.getTypeID(name)));
//		emit(new OBUConnect(this.name))
		controller.OBUConnect(this);
	}

	public String getName() {
		return name;
	}

	void OBUDisconnect(String name) {
		if (name.equals(this.name)) {
//			killMe
		}
	}

	void tempClean() throws Exception {
		goDefaultColor();
	}

	void WarnOBU(String name) throws Exception {
		if (name.equals(this.name)) {
			goRed();
		}
	}

	void goRed() throws Exception {
		conn.do_job_set(Vehicle.setColor(name, new SumoColor(255, 0, 0, 255)));
		conn.do_job_set(Vehicle.setSpeed(name, 0.0));
	}

	void goDefaultColor() throws Exception {
		if (type.contains("reckless")) {
			conn.do_job_set(Vehicle.setColor(name, new SumoColor(255, 140, 0, 255)));
		} else {
			conn.do_job_set(Vehicle.setColor(name, new SumoColor(255, 255, 0, 255)));
		}
		conn.do_job_set(Vehicle.setSpeed(name, simParams.busMaxSpeed));
	}

//	on Destroy
//	{
//		println(name + " OBU destroyed")	
//	}
}
