package agent;

import de.tudresden.sumo.cmd.Vehicle;
import de.tudresden.ws.container.SumoColor;
import it.polito.appeal.traci.SumoTraciConnection;

public class OBU {
	// Location of the OBUs
	private String name;
	private String type;
	private SumoTraciConnection conn;

	public OBU(String name, SumoTraciConnection conn, Controller controller) throws Exception {
		this.name = name;
		this.conn = conn;
		this.type = (String)(this.conn.do_job_get(Vehicle.getTypeID(this.name)));
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
		this.conn.do_job_set(Vehicle.setColor(this.name, new SumoColor(255, 0, 0, 255)));
		this.conn.do_job_set(Vehicle.setSpeed(this.name, 0.0));
	}

	void goDefaultColor() throws Exception {
		if (type.contains("reckless")) {
			this.conn.do_job_set(Vehicle.setColor(this.name, new SumoColor(255, 140, 0, 255)));
		} else {
			this.conn.do_job_set(Vehicle.setColor(this.name, new SumoColor(255, 255, 0, 255)));
		}
		this.conn.do_job_set(Vehicle.setSpeed(this.name, 8.3));
	}

//	on Destroy
//	{
//		println(this.name + " OBU destroyed")	
//	}
}
