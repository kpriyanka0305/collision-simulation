package util;

import math.geom2d.line.LineSegment2D;

import java.text.SimpleDateFormat;
import java.util.Date;

import main.SimulationParameters;
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

	public static String mkFileName(Date timestamp, String baseFileName) {
		return mkFileName(timestamp, baseFileName, ".txt");
	}

	public static String mkFileName(Date timestamp, String baseFileName, String extension) {
		String pattern = "yyyy-MM-dd-HH-mm-ss";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		String dateStr = simpleDateFormat.format(timestamp);

		return SimulationParameters.OUT_DIR + baseFileName + dateStr + extension;
	}
}
