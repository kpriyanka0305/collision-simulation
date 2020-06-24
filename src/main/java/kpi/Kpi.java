package kpi;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

import de.tudresden.sumo.cmd.Vehicle;
import de.tudresden.sumo.config.Constants;
import de.tudresden.sumo.util.SumoCommand;
import de.tudresden.sumo.cmd.Simulation;
import de.tudresden.ws.container.SumoPosition2D;
import it.polito.appeal.traci.SumoTraciConnection;
import main.SimulationParameters;
import math.geom2d.line.LineSegment2D;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;

import util.*;

public class Kpi {
	SumoTraciConnection conn;
	Map<String, Double> activeBuses = new HashMap<>();
	Set<String> activeBikes = new HashSet<>();
	Map<String, Map<String, List<Double[]>>> distances = new HashMap<>();
	Map<String, List<Double[]>> accelerations = new HashMap<>();
	Map<String, List<Double[]>> speeds = new HashMap<>();
	IntegerHistogram waitingTimeHistogram = new IntegerHistogram();

	private final static String DISTANCES_BASE = "/distances";
	private final static String ACCELERATIONS_BASE = "/accelerations";
	private final static String SPEEDS_BASE = "/speeds";
	private final static String SPEEDS_HISTOGRAM_BASE = "/speeds-histogram";

	private final FileWriter distancesFile;
	private final FileWriter accelerationsFile;
	private final FileWriter speedsFile;
	private final FileWriter speedsHistogramFile;

	public Kpi(SumoTraciConnection connection, Date timestamp) throws Exception {
		this.conn = connection;

		String pattern = "yyyy-MM-dd-HH-mm-ss";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		String dateStr = simpleDateFormat.format(timestamp);

		distancesFile = new FileWriter(SimulationParameters.OUT_DIR + DISTANCES_BASE + dateStr + ".txt", true);
		accelerationsFile = new FileWriter(SimulationParameters.OUT_DIR + ACCELERATIONS_BASE + dateStr + ".txt", true);
		speedsFile = new FileWriter(SimulationParameters.OUT_DIR + SPEEDS_BASE + dateStr + ".txt", true);
		speedsHistogramFile = new FileWriter(SimulationParameters.OUT_DIR + SPEEDS_HISTOGRAM_BASE + dateStr + ".txt",
				true);
	}

	public void addBus(String vehicleID, double busMaxSpeed) {
		activeBuses.put(vehicleID, busMaxSpeed);
		distances.put(vehicleID, new HashMap<>());
		accelerations.put(vehicleID, new ArrayList<>());
		speeds.put(vehicleID, new ArrayList<>());
	}

	public void removeBus(String busID) {
		writeDistanceGraph(busID);
		writeAccelGraph(busID);
		writeSpeedGraph(busID);
		waitingTimeHistogram.add(calculateWaitingTime(busID));
		activeBuses.remove(busID);
	}

	public void addBike(String vehicleID) {
		activeBikes.add(vehicleID);
	}

	public void removeBike(String vehicleID) {
		activeBikes.remove(vehicleID);
	}

	// call this function after the simulation is finished
	public void simulationFinished() {
		try {
			writeSpeedsHistogramGraph();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void writeSpeedsHistogramGraph() throws IOException {
		speedsHistogramFile.append("\n\n");
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
		for (String bus : activeBuses.keySet()) {
			updateAcceleration(bus);
			updateSpeed(bus);
			for (String bike : activeBikes) {
				updateMinimalDistance(bus, bike);
			}
		}
	}

	private void writeDistanceGraph(String busID) {
		try {
			double busMaxSpeed = activeBuses.get(busID);
			for (Map.Entry<String, List<Double[]>> dist : distances.get(busID).entrySet()) {
				distancesFile.append("\n\n");
				distancesFile.append("\"bus max speed " + String.format("%.1f", busMaxSpeed) + "\"\n");
				for (Double[] dataPoint : dist.getValue()) {
					distancesFile.append(dataPoint[0] + " " + dataPoint[1] + "\n");
				}
			}

			distancesFile.flush();

		} catch (Exception e) {
			System.out.println(e);
		}
	}

	private void writeAccelGraph(String busID) {
		try {
			accelerationsFile.append("\n\n");
			accelerationsFile.append("\"acceleration " + busID + "\"\n");
			for (Double[] dataPoint : accelerations.get(busID)) {
				accelerationsFile.append(dataPoint[0] + " " + dataPoint[1] + "\n");
			}

			accelerationsFile.flush();

		} catch (Exception e) {
			System.out.println(e);
		}
	}

	private void writeSpeedGraph(String busID) {
		try {
			speedsFile.append("\n\n");
			speedsFile.append("\"speed " + busID + "\"\n");
			for (Double[] dataPoint : speeds.get(busID)) {
				speedsFile.append(dataPoint[0] + " " + dataPoint[1] + "\n");
			}

			speedsFile.flush();

		} catch (Exception e) {
			System.out.println(e);
		}
	}

	private int calculateWaitingTime(String busID) {
		int totalWaitingTimeTicks = 0;
		for (Double[] dataPoint : speeds.get(busID)) {
			if (dataPoint[1] <= 0.000001) {
				// bus speed at that point in time was 0
				totalWaitingTimeTicks += 1;
			}
//				speedsFile.append(dataPoint[0] + " " + dataPoint[1] + "\n");
		}
		return totalWaitingTimeTicks;
	}
}
