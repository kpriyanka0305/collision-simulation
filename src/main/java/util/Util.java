package util;

import math.geom2d.line.LineSegment2D;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.tudresden.sumo.cmd.Vehicle;
import it.polito.appeal.traci.SumoTraciConnection;
import main.SimulationProperties;
import math.geom2d.*;

public class Util {

	public static LineSegment2D createBusLineSegment(double busX, double busY, double busAngleDeg, double busLength) {
		Point2D busStart = new Point2D(busX, busY);

		// Sumo and geom2d have different coordinate systems. Sumo angles are in
		// degrees, clockwise from the y-axis, while geom2d angles are in
		// radians, counterclockwise from the x-axis. We need to invert rotation
		// direction, and rotate by 180 degrees to get a vector that points to
		// the last point of the bus. Remember, the direction points in the
		// forward direction, but we want backwards direction.
		double busAngleRad = Math.toRadians(-busAngleDeg - 90);
		Point2D busEnd = Point2D.createPolar(busStart, busLength, busAngleRad);
		return new LineSegment2D(busStart, busEnd);
	}

	public static String mkFileName(SimulationProperties simParams, Date timestamp, String baseFileName) {
		return mkFileName(simParams, timestamp, baseFileName, ".txt");
	}

	public static String mkFileName(SimulationProperties simParams, Date timestamp, String baseFileName,
			String extension) {
		String pattern = "yyyy-MM-dd-HH-mm-ss";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		String dateStr = simpleDateFormat.format(timestamp);

		// Create directories if necessary
		String directory = simParams.getOutDir() + "/" + dateStr + "/";
		new File(directory).mkdirs();

		return directory + baseFileName + extension;
	}

	public static void roadUserBehaviourReckless(SumoTraciConnection conn, String name) throws Exception {
		// all checks off
		conn.do_job_set(Vehicle.setSpeedMode(name, 0));
		// unsafe distance
		conn.do_job_set(Vehicle.setMinGap(name, 0));
	}

	public static void roadUserBehaviourDisciplined(SumoTraciConnection conn, String name) throws Exception {
		// all checks on
		conn.do_job_set(Vehicle.setSpeedMode(name, 31));
		// some safe distance
		conn.do_job_set(Vehicle.setMinGap(name, 2.5));
	}
}
