package kpi;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import de.tudresden.sumo.cmd.Vehicle;
import de.tudresden.sumo.cmd.Vehicletype;
import de.tudresden.sumo.config.Constants;
import de.tudresden.sumo.util.SumoCommand;
import de.tudresden.sumo.cmd.Simulation;
import de.tudresden.ws.container.SumoPosition2D;
import it.polito.appeal.traci.SumoTraciConnection;
import math.geom2d.line.LineSegment2D;

import java.io.FileWriter;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import util.*;

public class Kpi {
	SumoTraciConnection conn;
	Set<String> activeBuses = new HashSet<>();
	Set<String> activeBikes = new HashSet<>();
	Map<String, Map<String, List<Double[]>>> distances = new HashMap<>();
	Map<String, List<Double[]>> accelerations = new HashMap<>();
	Map<String, List<Double[]>> speeds = new HashMap<>();

	public Kpi(SumoTraciConnection connection) throws Exception {
		this.conn = connection;
	}

	public void addBus(String vehicleID) {
		activeBuses.add(vehicleID);
		distances.put(vehicleID, new HashMap<>());
		accelerations.put(vehicleID, new ArrayList<>());
		speeds.put(vehicleID, new ArrayList<>());
	}

	public void removeBus(String busID) {
		activeBuses.remove(busID);
		for (Map.Entry<String, List<Double[]>> dists : distances.get(busID).entrySet()) {
			writeDistanceGraph(dists.getValue(), busID, dists.getKey());
		}
		writeAccelGraph(busID);
		writeSpeedGraph(busID);
	}

	public void addBike(String vehicleID) {
		activeBikes.add(vehicleID);
	}

	public void removeBike(String vehicleID) {
		activeBikes.remove(vehicleID);
	}

	private void updateMinimalDistance(String busID, String bikeID) throws Exception {
		SumoPosition2D busPos = (SumoPosition2D) conn.do_job_get(Vehicle.getPosition(busID));
		Double busAngleDeg = (Double) conn.do_job_get(Vehicle.getAngle(busID));
		Double busLength = (Double) conn.do_job_get(Vehicle.getLength(busID));
		SumoPosition2D bikePos = (SumoPosition2D) conn.do_job_get(Vehicle.getPosition(bikeID));

		LineSegment2D busGeometry = Util.createBusLineSegment(busPos.x, busPos.y, busAngleDeg, busLength);
		double distance = busGeometry.distance(bikePos.x, bikePos.y);

		Double timestamp = (Double) this.conn.do_job_get(Simulation.getTime());
		Map<String, List<Double[]>> bikeDistances = distances.get(busID);
		bikeDistances.putIfAbsent(bikeID, new ArrayList<>());
		bikeDistances.get(bikeID).add(new Double[] { timestamp, distance });
	}

	private void updateAcceleration(String busID) throws Exception {
		// traas API is wrong. getAccel does *not* return current acceleration, but max
		// possible acceleration
		SumoCommand getAcceleration = new SumoCommand(Constants.CMD_GET_VEHICLE_VARIABLE, Constants.VAR_ACCELERATION,
				busID, Constants.RESPONSE_GET_VEHICLE_VARIABLE, Constants.TYPE_DOUBLE);
		Double busAccel = (Double) conn.do_job_get(getAcceleration);
		Double timestamp = (Double) this.conn.do_job_get(Simulation.getTime());
		accelerations.get(busID).add(new Double[] { timestamp, busAccel });
	}

	private void updateSpeed(String busID) throws Exception {
		Double busSpeed = (Double) conn.do_job_get(Vehicle.getSpeed(busID));
		Double timestamp = (Double) this.conn.do_job_get(Simulation.getTime());
		speeds.get(busID).add(new Double[] { timestamp, busSpeed });
	}

	public void checkKPIs() throws Exception {
		for (String bus : activeBuses) {
			updateAcceleration(bus);
			updateSpeed(bus);
			for (String bike : activeBikes) {
				updateMinimalDistance(bus, bike);
			}
		}
	}

	private void writeDistanceGraph(List<Double[]> distances, String busID, String bikeID) {
		try {
			FileWriter file = new FileWriter("data/distances.txt", false);

			file.append("\n\n");
			file.append("\"distance " + busID + " " + bikeID + "\"\n");
			for (Double[] dataPoint : distances) {
				file.append(dataPoint[0] + " " + dataPoint[1] + "\n");
			}

			file.close();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	private void writeAccelGraph(String busID) {
		try {
			FileWriter file = new FileWriter("data/accelerations.txt", false);

			file.append("\n\n");
			file.append("\"acceleration " + busID + "\"\n");
			for (Double[] dataPoint : accelerations.get(busID)) {
				file.append(dataPoint[0] + " " + dataPoint[1] + "\n");
			}

			file.close();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	private void writeSpeedGraph(String busID) {
		try {
			FileWriter file = new FileWriter("data/speeds.txt", false);

			file.append("\n\n");
			file.append("\"speed " + busID + "\"\n");
			for (Double[] dataPoint : speeds.get(busID)) {
				file.append(dataPoint[0] + " " + dataPoint[1] + "\n");
			}

			file.close();
		} catch (Exception e) {
			System.out.println(e);
		}
	}
}
