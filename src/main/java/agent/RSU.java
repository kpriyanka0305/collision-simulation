package agent;

import de.tudresden.sumo.cmd.Polygon;
import de.tudresden.ws.container.SumoColor;
import de.tudresden.ws.container.SumoGeometry;
import de.tudresden.ws.container.SumoPosition2D;
import it.polito.appeal.traci.SumoTraciConnection;

public class RSU {
	private String name; // EAST or WEST
	private double x; // Location on SUMO (x)
	private double y; // Location on SUMO (y)
	private SumoGeometry RSUObject = new SumoGeometry();
	private double size = 5;
	private boolean status = false;
	private SumoTraciConnection conn;
	private Simulation simulation;

	public RSU(String name, Double x, Double y, SumoTraciConnection conn, Controller controller, Simulation simulation)
			throws Exception {
		this.name = name;
		this.x = x;
		this.y = y;
		this.conn = conn;
		this.simulation = simulation;

		drawRSU();

//		emit(new RSUConnect(this))
		controller.RSUConnect(this);
		System.out.println("RSU spawned");
	}

	void WarnRSU(String name) throws Exception {
		if (name.equals(this.name)) {
			this.status = true;
			goRed();
//			emit(new RSUStatus(this.name, this.status))
			simulation.RSUStatus(this.name, this.status);
		}
	}

	void ClearRSU(String name) throws Exception {
		if (name.equals(this.name)) {
			this.status = false;
			goGreen();
//			emit(new RSUStatus(this.name, this.status))
			simulation.RSUStatus(this.name, this.status);
		}
	}

//	on Destroy {
//		println("RSU destroyed")
//	}	

//	on ShutdownSimulation {
//		killMe
//	}

	void goRed() throws Exception {
		this.conn.do_job_set(Polygon.setColor(this.name, new SumoColor(255, 0, 0, 255)));
	}

	void goGreen() throws Exception {
		this.conn.do_job_set(Polygon.setColor(this.name, new SumoColor(0, 255, 0, 255)));
	}

	void drawRSU() throws Exception {
		this.RSUObject.add(new SumoPosition2D(x + size / 2, y + size / 2));
		this.RSUObject.add(new SumoPosition2D(x - size / 2, y + size / 2));
		this.RSUObject.add(new SumoPosition2D(x - size / 2, y - size / 2));
		this.RSUObject.add(new SumoPosition2D(x + size / 2, y - size / 2));

		this.conn.do_job_set(Polygon.add(this.name, RSUObject, new SumoColor(0, 255, 0, 255), true, "Square", -2));
	}
}