package util;

import math.geom2d.line.LineSegment2D;

import java.text.SimpleDateFormat;
import java.util.Date;

import main.SimulationParameters;
import math.geom2d.*;

public class Util {

	public static LineSegment2D createBusLineSegment(double busX, double busY, double busAngleDeg, double busLength) {
		Point2D busStart = new Point2D(busX, busY);
		// This creates a point in the direction of the bus. We need to rotate
		// by 180 degrees to get the actual end point.
		Point2D busEnd = Point2D.createPolar(busStart, busLength, Math.toRadians(busAngleDeg))
				// Sumo and geom2d have different coordinate systems.
				// Sumo angles are in degrees, clockwise from the x-axis while geom2d
				// angles are in radians, clockwise from the y-axis. Therefore, the
				// angle can be considered as already rotatet by 90 degrees. We need to
				// rotate by another 90 degrees.
				.rotate(busStart, Angle2D.M_PI_2);
		return new LineSegment2D(busStart, busEnd);
	}

	public static String mkFileName(Date timestamp, String baseFileName) {
		return mkFileName(timestamp, baseFileName, ".txt");
	}

	public static String mkFileName(Date timestamp, String baseFileName, String extension)
	{
		String pattern = "yyyy-MM-dd-HH-mm-ss";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		String dateStr = simpleDateFormat.format(timestamp);

		return SimulationParameters.OUT_DIR + baseFileName + dateStr + extension;
	}
}
