package agent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import de.tudresden.sumo.cmd.Junction;
import de.tudresden.sumo.cmd.Vehicle;
import de.tudresden.ws.container.SumoPosition2D;
import it.polito.appeal.traci.SumoTraciConnection;
import kpi.Kpi;

public class SimWarningService implements Simulation {

	private SumoTraciConnection conn;
	private List<String> OBusList = new ArrayList<String>();
	private Map<String, Boolean> RsusStatus = new HashMap<String, Boolean>();
	private double rsu_distance = 13.0;
	private double cyclist_range = 5.0;

	private Kpi kpis;
	private Controller controller;

	private Consumer<Void> shutdownCallback;

	public SimWarningService(SumoTraciConnection conn, Kpi kpis, Consumer<Void> shutdownCallback) {
		this.conn = conn;
		this.kpis = kpis;
		this.shutdownCallback = shutdownCallback;

		this.RsusStatus.put("East", false);
		this.RsusStatus.put("West", false);

		spawnElements();

//		run
	}

	public void RSUStatus(String name, boolean status) {
		if (name.equals("East")) {
			this.RsusStatus.put("East", status);
		} else {
			this.RsusStatus.put("West", status);
		}
	}

	@Override
	public void step() throws Exception {
		this.conn.do_timestep();
		List<String> vehicles = (List<String>)(this.conn.do_job_get(Vehicle.getIDList()));
		
		if( vehicles.isEmpty() )
		{
//			emit(new ShutdownSimulation)
//			return
		}

		kpis.checkKPIs();

		for (String v : vehicles) {
			Map<String,Object> veh_data = readData(v);
			String type = (String)(veh_data.get("type"));
			if (type.contains("bus")) {
				if (!this.OBusList.contains(v)) {
//					spawn(OBU, v, this.conn)
					this.OBusList.add(v);
					System.out.println(v + " ENTERED");
				}
			} else if (type.contains("bicycle-distracted")) {	
				double distance = (Double)(veh_data.get("distance"));
				double speed = (Double)(veh_data.get("speed"));
				boolean east_rsu = (Boolean)(this.RsusStatus.get("East"));
				String road_id = (String)(veh_data.get("road_id"));
				if (east_rsu && road_id.contains("i")) {
					if (distance > rsu_distance && distance < rsu_distance + cyclist_range) {
						this.conn.do_job_set(Vehicle.setSpeed(v, 0.0));
						//println("Bicycle distracted entered" + distance + "RSU distance is:-" + rsu_distance + "Cyclists range is :- " + cyclist_range + "cyclists speed :-" + speed);
					}
				} else {
					if (speed == 0) {
						this.conn.do_job_set(Vehicle.setSpeed(v, 4.2)); // 4.2 - speed of bicycle set here
						//println(v + "Bicycle distracted entered");
					}
				}
			}
		}

		for (String k : this.OBusList) {
			boolean flag = false;

			for (String v : vehicles) {
				if (k.equals(v)) {
					flag = true;
					break;
				}
			}

			if (!flag) {
				System.out.println(k + " REMOVED");
//				emit(new OBUDisconnect(k))
				controller.OBUDisconnect(k);
				this.OBusList.remove(k);
				break;
			}
		}
	}

	void spawnElements() {
//		spawn(Controller, this.conn)
//		spawn(RSU, "East", 15.5, -10.5, this.conn) // EAST X, Y
//		spawn(Camera, "CameraOne", this.conn, -15.0, 0.0, 2.0, 0.7, 4) 	// CameraName, Connection, X, Y, Size, Height, Angle
	}

	private Map<String, Object> readData(String id) throws Exception {
		SumoPosition2D position = (SumoPosition2D) (this.conn.do_job_get(Vehicle.getPosition(id)));
		SumoPosition2D centre = (SumoPosition2D) (this.conn.do_job_get(Junction.getPosition("0")));
		String type = (String) (this.conn.do_job_get(Vehicle.getTypeID(id)));
		double speed = (Double) (this.conn.do_job_get(Vehicle.getSpeed(id)));
		double length = (Double) (this.conn.do_job_get(Vehicle.getLength(id)));
		double accel = (Double) (this.conn.do_job_get(Vehicle.getAccel(id)));
		String road_id = (String) (this.conn.do_job_get(Vehicle.getRoadID(id)));
		double tempx = Math.abs(centre.x - position.x);
		double tempy = Math.abs(centre.y - position.y);
		double distance = Math.sqrt(tempx * tempx + tempy * tempy);
		distance = distance - length / 2;
		Map<String, Object> myMap = new HashMap<>();
		myMap.put("type", type);
		myMap.put("speed", speed);
		myMap.put("accel", accel);
		myMap.put("distance", distance);
		myMap.put("road_id", road_id);
		myMap.put("length", length);
		return myMap;
	}
}
