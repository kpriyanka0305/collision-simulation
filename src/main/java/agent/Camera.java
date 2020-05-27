package agent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.tudresden.sumo.cmd.Polygon;
import de.tudresden.sumo.cmd.Vehicle;
import de.tudresden.ws.container.SumoColor;
import de.tudresden.ws.container.SumoGeometry;
import de.tudresden.ws.container.SumoPosition2D;
import it.polito.appeal.traci.SumoTraciConnection;
import math.geom2d.Point2D;
import math.geom2d.polygon.Polygon2D;
import math.geom2d.polygon.SimplePolygon2D;

public class Camera {

	private String name;
	private int angle; // 90 - NORTH, 180 - WEST, 270 - SOUTH, 0 - EAST
	private double height; // Meters
	private double x; // LOCATION ON THE MAP X
	private double y; // LOCATION ON THE MAP Y
	private double size; // Rectangle size
	private int r = 14;
	private int g = 214;
	private int b = 197;

	private Polygon2D fieldOfView;

	// FOR SUMO DRAWING
	private SumoGeometry cameraObject = new SumoGeometry();
	private SumoGeometry fovObject = new SumoGeometry();

	// FOR COLLISION
	private SumoPosition2D center;

	// CONNECTION OF COURSE
	private SumoTraciConnection conn;

	private Controller controller;

	public Camera(String name, SumoTraciConnection conn, double x, double y, double size, double height, int angle,
			Controller controller) throws Exception {
		// BASE
		this.name = name;
		this.conn = conn;
		this.controller = controller;

		// THESE ARE FOR CAMERA SQUARE
		this.x = x;
		this.y = y;
		this.size = size;

		// THESE ARE FOR FIELD OF VIEW
		this.height = height;
		this.angle = angle;

		// THESE ARE FOR COLLISION POINT
		this.center = new SumoPosition2D(2.28, 0.88);

		drawCamera();
		drawFOV();

		// CONNECT TO THE CONTROLLER
//		emit(new CameraConnect(this))
		controller.CameraConnect(this);
	}

	public void observeSituation() throws Exception {
		@SuppressWarnings("unchecked")
		List<String> vehicles = (List<String>) (conn.do_job_get(Vehicle.getIDList()));

		Map<String, Map<String, Object>> vehicle_data = new HashMap<>();
		for (String v : vehicles) {
			SumoPosition2D sumoPosition = (SumoPosition2D) (conn.do_job_get(Vehicle.getPosition(v)));
			Point2D position = new Point2D(sumoPosition.x, sumoPosition.y);

			if (fieldOfView.contains(position)) {
				vehicle_data.put(v, readData(v, position));
			}
		}
//		emit(new SendAllDataCamera(vehicle_data))
		controller.SendAllDataCamera(vehicle_data);
	}

	Map<String, Object> readData(String id, Point2D position) throws Exception {
		String type = (String) (conn.do_job_get(Vehicle.getTypeID(id)));
		double speed = (Double) (conn.do_job_get(Vehicle.getSpeed(id)));
		double length = (Double) (conn.do_job_get(Vehicle.getLength(id)));
		double accel = (Double) (conn.do_job_get(Vehicle.getAccel(id)));
		Point2D cameraLocation = new Point2D(center.x, center.y);
		double distance = cameraLocation.distance(position);
		double seconds;

		distance = distance - length / 2;

		if (speed > 0) {
			seconds = distance / speed;
		} else {
			seconds = 100.0;
		}

		Map<String, Object> myMap = new HashMap<>();
		myMap.put("type", type);
		myMap.put("speed", speed);
		myMap.put("accel", accel);
		myMap.put("length", length);
		myMap.put("distance", distance);
		myMap.put("seconds", seconds);
		return myMap;
	}

//	on ShutdownSimulation
//	{
//		val task = task("camera observing")
//		task.cancel()
//		killMe
//	}

//	on Destroy
//	{
//		println("Camera destroyed")
//	}

	// FOR SIMULATION/COLLISION PURPOSES ONLY
	void drawCamera() throws Exception {
		cameraObject.add(new SumoPosition2D(x + size / 2, y + size / 2));
		cameraObject.add(new SumoPosition2D(x - size / 2, y + size / 2));
		cameraObject.add(new SumoPosition2D(x - size / 2, y - size / 2));
		cameraObject.add(new SumoPosition2D(x + size / 2, y - size / 2));

		conn.do_job_set(
				Polygon.add(name, cameraObject, new SumoColor(r, g, b, 255), true, "Square", -2));
	}

	void drawFOV() throws Exception {
		double smallRadius = size / 2 + 15;
		double bigRadius = size / 2 + 15 + (90 * Math.sqrt(height));

		double fovAngle = 25;

		// NECESSARY CALCULATIONS
		double xSmallOne = x + Math.cos(Math.toRadians(angle + fovAngle)) * smallRadius;
		double ySmallOne = y + Math.sin(Math.toRadians(angle + fovAngle)) * smallRadius;

		double xSmallTwo = x + Math.cos(Math.toRadians(angle - fovAngle)) * smallRadius;
		double ySmallTwo = y + Math.sin(Math.toRadians(angle - fovAngle)) * smallRadius;

		double xBigOne = x + Math.cos(Math.toRadians(angle - fovAngle)) * bigRadius;
		double yBigOne = y + Math.sin(Math.toRadians(angle - fovAngle)) * bigRadius;

		double xBigTwo = x + Math.cos(Math.toRadians(angle + fovAngle)) * bigRadius;
		double yBigTwo = y + Math.sin(Math.toRadians(angle + fovAngle)) * bigRadius;

		// DRAWING OBJECT
		fovObject.add(new SumoPosition2D(xSmallOne, ySmallOne));
		fovObject.add(new SumoPosition2D(xSmallTwo, ySmallTwo));
		fovObject.add(new SumoPosition2D(xBigOne, yBigOne));
		fovObject.add(new SumoPosition2D(xBigTwo, yBigTwo));

		// FOR COLLISION
		fieldOfView = new SimplePolygon2D(new Point2D(xSmallOne, ySmallOne), new Point2D(xSmallTwo, ySmallTwo),
				new Point2D(xBigOne, yBigOne), new Point2D(xBigTwo, yBigTwo));

		conn.do_job_set(
				Polygon.add(name + "FOV", fovObject, new SumoColor(r, g, b, 64), true, "FOV", -2));
	}
}
