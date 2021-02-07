package agent;

import de.tudresden.sumo.cmd.Vehicle;
import de.tudresden.ws.container.SumoColor;
import it.polito.appeal.traci.SumoTraciConnection;
import util.Util;

public class OBU {
	// Location of the OBUs
	private String name;
	private String type;
	private SumoTraciConnection conn;

	public OBU(String name, SumoTraciConnection conn, Controller controller) throws Exception {
		this.name = name;
		this.conn = conn;
		this.type = (String) (conn.do_job_get(Vehicle.getTypeID(name)));
		controller.OBUConnect(this);
	}

	public String getName() {
		return name;
	}

	public void UnwarnOBU() throws Exception {
		goDefaultColor();
	}

	public void WarnOBU(String name) throws Exception {
		if (name.equals(this.name)) {
			goRed();
		}
	}

	private void goRed() throws Exception {
		conn.do_job_set(Vehicle.setColor(name, new SumoColor(255, 0, 0, 255)));
		Util.roadUserBehaviourReckless(conn, name, false);
	}

	private void goDefaultColor() throws Exception {
		conn.do_job_set(Vehicle.setColor(name, new SumoColor(255, 255, 0, 255)));
		Util.roadUserBehaviourReckless(conn, name, true);
	}
}
