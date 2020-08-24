package kpi;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import de.tudresden.sumo.cmd.Simulation;
import de.tudresden.sumo.cmd.Vehicle;
import de.tudresden.sumo.config.Constants;
import de.tudresden.sumo.util.SumoCommand;
import de.tudresden.ws.container.SumoPosition2D;
import it.polito.appeal.traci.SumoTraciConnection;
import main.SimulationParameters;
import math.geom2d.line.LineSegment2D;
import util.Util;

public class Kpi {
	SumoTraciConnection conn;
	Map<String, Double> activeBuses = new HashMap<>();
	Map<String, Double> activeBikes = new HashMap<>();
	Map<String, Map<String, List<Double[]>>> distances = new HashMap<>();
	Map<String, List<Double[]>> accelerations = new HashMap<>();
	Map<String, List<Double[]>> speeds = new HashMap<>();

	private final FileWriter distancesFile;
	private final FileWriter accelerationsFile;
	private final FileWriter speedsFile;

	public Kpi(SumoTraciConnection connection, Date timestamp) throws Exception {
		this.conn = connection;

		distancesFile = new FileWriter(Util.mkFileName(timestamp, SimulationParameters.getDistancesBase()), true);
		accelerationsFile = new FileWriter(Util.mkFileName(timestamp, SimulationParameters.getAccelerationsBase()), true);
		speedsFile = new FileWriter(Util.mkFileName(timestamp, SimulationParameters.getSpeedsBase()), true);
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
		activeBuses.remove(busID);
	}

	public void addBike(String vehicleID, double bikeMaxSpeed) {
		activeBikes.put(vehicleID, bikeMaxSpeed);
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
		for (String bus : activeBuses.keySet()) {
			updateAcceleration(bus);
			updateSpeed(bus);
			for (String bike : activeBikes.keySet()) {
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

	// The number of simulation steps where the bus speed of the given bus was 0
	public long getWaitingTime(String busID) {
		long totalWaitingTimeTicks = speeds.get(busID).stream().skip(1).filter(dataPoint -> dataPoint[1] <= 0.000001)
				.count();
		return totalWaitingTimeTicks;
	}

	// Given a trace of bus accelerations and distances between bus and one
	// bike, determine if there was a hard braking event between them.
	// A hard braking event is a point in time where the bus brakes while a bike is
	// very close.
	private Optional<Double> findHardBraking(List<Double[]> busAccelerations, List<Double[]> distances,
			double nearCollisionThreshold) {
		Iterator<Double[]> itAccel = busAccelerations.iterator();
		Iterator<Double[]> itDist = distances.iterator();

		if (itAccel.hasNext() && itDist.hasNext()) {
			Double[] accel = itAccel.next();
			Double[] dist = itDist.next();
			do {
				if (Math.abs(accel[0] - dist[0]) < 0.0001) {
					// time stamps are identical

					if (accel[1] < 0 && dist[1] <= nearCollisionThreshold)
						return Optional.of(accel[0]);

					accel = itAccel.next();
					dist = itDist.next();
				} else if (accel[0] < dist[0]) {
					// accel needs to be advanced
					accel = itAccel.next();
				} else if (accel[0] > dist[0]) {
					// dist needs to be advanced
					dist = itDist.next();
				}
			} while (itAccel.hasNext() && itDist.hasNext());
		}

		return Optional.empty();
	}

	public Optional<Double> anyHardBrakings(String busID, double nearCollisionThreshold) {
		List<Double[]> accels = accelerations.get(busID);
		for (Entry<String, List<Double[]>> dists : distances.get(busID).entrySet()) {
			Optional<Double> result = findHardBraking(accels, dists.getValue(), nearCollisionThreshold);
			if (result.isPresent()) {
				return result;
			}
		}
		return Optional.empty();
	}

	public Optional<Double> getMinimumDistance(String busID) {
		Optional<Double> result = Optional.empty();
		Map<String, List<Double[]>> busDistances = distances.get(busID);
		if (busDistances != null) {
			for (List<Double[]> bikeDistances : busDistances.values()) {
				for (Double[] bikeDistance : bikeDistances) {
					if (result.isPresent()) {
						result = bikeDistance[1] < result.get() ? Optional.of(bikeDistance[1]) : result;
					} else {
						result = Optional.of(bikeDistance[1]);
					}
				}
			}
		}
		return result;
	}
}
