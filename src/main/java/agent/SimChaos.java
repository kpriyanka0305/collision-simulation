package agent;

import java.util.List;

import de.tudresden.sumo.cmd.Vehicle;
import it.polito.appeal.traci.SumoTraciConnection;
import kpi.Kpi;

public class SimChaos extends Simulation {
	private SumoTraciConnection conn;
	private Kpi kpis;

	public SimChaos(SumoTraciConnection conn, Kpi kpis) {
		this.conn = conn;
		this.kpis = kpis;
	}

	@Override
	public void step() throws Exception {
		numSteps++;
		conn.do_timestep();

		@SuppressWarnings("unchecked")
		List<String> vehicles = (List<String>) (conn.do_job_get(Vehicle.getIDList()));

		kpis.checkKPIs();
	}

	@Override
	public void RSUStatus(String name, boolean status) {
		// nop
	}
}
