package main;

public class SimulationParameters {
	public final static String OUT_DIR = "output";

	public final double busMaxSpeed;
	public final double bikeMaxSpeed;

	public SimulationParameters(double busMaxSpeed, double bikeMaxSpeed) {
		this.busMaxSpeed = busMaxSpeed;
		this.bikeMaxSpeed = bikeMaxSpeed;
	}
}
