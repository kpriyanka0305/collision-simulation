package agent;

public class VehicleData {

	private String id;
	private String type;
	private double speed;
	private double accel;
	private double length;
	private double distance;
	private double seconds;

	public VehicleData(String id, String type, double speed, double accel, double length, double distance,
			double seconds) {
		this.id = id;
		this.type = type;
		this.speed = speed;
		this.accel = accel;
		this.length = length;
		this.distance = distance;
		this.seconds = seconds;
	}

	public String getId() {
		return id;
	}

	public double getSpeed() {
		return speed;
	}

	public String getType() {
		return type;
	}

	public double getAccel() {
		return accel;
	}

	public double getDistance() {
		return distance;
	}

	public double getLength() {
		return length;
	}

	public double getSeconds() {
		return seconds;
	}

}
