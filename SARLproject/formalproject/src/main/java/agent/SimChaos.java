package agent;

import java.util.List;

import de.tudresden.sumo.cmd.Vehicle;
import it.polito.appeal.traci.SumoTraciConnection;
import kpi.Kpi;

public class SimChaos implements Simulation {
	private SumoTraciConnection conn;
	private Kpi kpis;

	public SimChaos(SumoTraciConnection conn, Kpi kpis) {
		this.conn = conn;
		this.kpis = kpis;
	}

	@Override
	public boolean step() throws Exception {
		@SuppressWarnings("unchecked")
		List<String> vehicles = (List<String>) (this.conn.do_job_get(Vehicle.getIDList()));

		if (vehicles.isEmpty()) {
			// simulation wants to shut down
			return false;
		}

		this.conn.do_timestep();
		kpis.checkKPIs();

		// simulation wants to continue
		return true;
	}

	@Override
	public void RSUStatus(String name, boolean status) {
		// nop
	}
}
