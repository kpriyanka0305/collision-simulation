package kpi;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import de.tudresden.sumo.cmd.Vehicle;
import de.tudresden.sumo.cmd.Vehicletype;
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

	public enum CollisionType {
		COLLISION, NEAR_COLLISION
	}

	public Kpi(SumoTraciConnection connection) throws Exception {
		this.conn = connection;
	}

	public void addBus(String vehicleID) {
		activeBuses.add(vehicleID);
		distances.put(vehicleID, new HashMap<>());
	}

	public void removeBus(String busID) {
		activeBuses.remove(busID);
		for (Map.Entry<String, List<Double[]>> dists : distances.get(busID).entrySet()) {
			System.out.println(busID + " -- " + dists.getKey() + " = "
					+ dists.getValue().stream().mapToDouble(v -> v[1]).min());
			writeDistanceGraph(dists.getValue(), busID, dists.getKey());
		}
	}

	public void addBike(String vehicleID) {
		activeBikes.add(vehicleID);
	}

	public void removeBike(String vehicleID) {
		activeBikes.remove(vehicleID);
	}

	public void updateMinimalDistance(String busID, String bikeID) throws Exception {
		SumoPosition2D busPos = (SumoPosition2D) conn.do_job_get(Vehicle.getPosition(busID));
		Double busAngleDeg = (Double) conn.do_job_get(Vehicle.getAngle(busID));
		Double busLength = (Double) conn.do_job_get(Vehicle.getLength(busID));
		SumoPosition2D bikePos = (SumoPosition2D) conn.do_job_get(Vehicle.getPosition(bikeID));

		LineSegment2D busGeom = Util.createBusLineSegment(busPos.x, busPos.y, busAngleDeg, busLength);
		double distance = busGeom.distance(bikePos.x, bikePos.y);

		Double timestamp = (Double) this.conn.do_job_get(Simulation.getTime());
		Map<String, List<Double[]>> bikeDistances = distances.get(busID);
		bikeDistances.putIfAbsent(bikeID, new ArrayList<>());
		bikeDistances.get(bikeID).add(new Double[] { timestamp, distance });
	}

	public void checkKPIs() throws Exception {
		for (String bus : activeBuses) {
			for (String bike : activeBikes) {
				updateMinimalDistance(bus, bike);
			}
		}
	}

	private void writeDistanceGraph(List<Double[]> distances, String busID, String bikeID) {
		try {
			FileWriter file = new FileWriter("data/distances.txt", true);

			file.append("\n\n");
			file.append("\"" + busID + " " + bikeID + "\"");
			for (Double[] dataPoint : distances) {
				file.append(dataPoint[0] + " " + dataPoint[1] + "\n");
			}

			file.close();
		} catch (Exception e) {
			System.out.println(e);
		}
	}
}
