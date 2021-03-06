package agent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.tudresden.sumo.cmd.Poi;
import de.tudresden.sumo.cmd.Polygon;
import de.tudresden.sumo.cmd.Vehicle;
import de.tudresden.ws.container.SumoColor;
import de.tudresden.ws.container.SumoGeometry;
import de.tudresden.ws.container.SumoPosition2D;
import it.polito.appeal.traci.SumoTraciConnection;
import main.SimulationProperties;
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

	private final SimulationProperties simParams;

	// FOR SUMO DRAWING
	private SumoGeometry cameraObject = new SumoGeometry();
	private SumoGeometry fovObject = new SumoGeometry();
	private SumoGeometry referencePointObject = new SumoGeometry();

	// CONNECTION OF COURSE
	private SumoTraciConnection conn;

	private Controller controller;

	// A defective camera does not detect any vehicles
	private boolean defective;

	public Camera(SimulationProperties simParams, String name, SumoTraciConnection conn, double x, double y,
			double size, double height, int angle, Controller controller, boolean defective) throws Exception {
		this.simParams = simParams;

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

		this.defective = defective;

		drawCamera();
		drawFOV();
		// useful for debugging, not so much for simulation
		drawReferencePoint();

		// CONNECT TO THE CONTROLLER
		controller.CameraConnect(this);
	}

	public void observeSituation() throws Exception {

		if (defective) {
			return;
		}

		@SuppressWarnings("unchecked")
		List<String> vehicleIDs = (List<String>) (conn.do_job_get(Vehicle.getIDList()));

		Collection<VehicleData> vehicleData = new ArrayList<>();
		for (String v : vehicleIDs) {
			SumoPosition2D sumoPosition = (SumoPosition2D) (conn.do_job_get(Vehicle.getPosition(v)));
			Point2D vehiclePosition = new Point2D(sumoPosition.x, sumoPosition.y);

			if (fieldOfView.contains(vehiclePosition)) {
				vehicleData.add(readData(v, vehiclePosition));
			}
		}
		controller.SendAllDataCamera(vehicleData);
	}

	private VehicleData readData(String vehicleID, Point2D vehiclePosition) throws Exception {
		String type = (String) (conn.do_job_get(Vehicle.getTypeID(vehicleID)));
		double speed = (Double) (conn.do_job_get(Vehicle.getSpeed(vehicleID)));
		double length = (Double) (conn.do_job_get(Vehicle.getLength(vehicleID)));
		double accel = (Double) (conn.do_job_get(Vehicle.getAccel(vehicleID)));
		Point2D junctionPosition = new Point2D(simParams.getReferencePointX(), simParams.getReferencePointY());
		// TODO: is this correct?
		double distanceToJunction = junctionPosition.distance(vehiclePosition) - length / 2;
		double seconds;

		if (speed > 0) {
			seconds = distanceToJunction / speed;
		} else {
			seconds = 100.0;
		}

		VehicleData vehicleData = new VehicleData(vehicleID, type, speed, accel, length, distanceToJunction, seconds);
		return vehicleData;
	}

	// FOR SIMULATION/COLLISION PURPOSES ONLY
	private void drawCamera() throws Exception {
		cameraObject.add(new SumoPosition2D(x + size / 2, y + size / 2));
		cameraObject.add(new SumoPosition2D(x - size / 2, y + size / 2));
		cameraObject.add(new SumoPosition2D(x - size / 2, y - size / 2));
		cameraObject.add(new SumoPosition2D(x + size / 2, y - size / 2));

		conn.do_job_set(Polygon.add(name, cameraObject, new SumoColor(r, g, b, 255), true, "Square", -2));
	}

	private void drawFOV() throws Exception {
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

		SumoColor fovColor = defective ? new SumoColor(255, g, b, 64) : new SumoColor(r, g, b, 64);
		conn.do_job_set(Polygon.add(name + "FOV", fovObject, fovColor, true, "FOV", -2));
	}

	private void drawReferencePoint() throws Exception {
		double x = simParams.getReferencePointX();
		double y = simParams.getReferencePointY();
		double r = simParams.getReactionDistance();
		int numPoints = 8; // octagon

		for (int i = 0; i < numPoints; i++) {
			referencePointObject.add(new SumoPosition2D(x + r * Math.cos(2 * Math.PI * i / numPoints),
					y + r * Math.sin(2 * Math.PI * i / numPoints)));
		}

		conn.do_job_set(Polygon.add("reference point", referencePointObject, new SumoColor(255, 0, 0, 128), true,
				"reference point", 20));
	}
}
