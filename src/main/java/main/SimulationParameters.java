package main;

public class SimulationParameters {
	public final static String OUT_DIR = "output";
	static final String BUS_PREFIX = "bus";
	static final String BIKE_PREFIX = "bicycle";

	static final String SUMO_GUI_BIN = "sumo-gui";
	static final String SUMO_CLI_BIN = "sumo-gui";
	private String sumoBin = SUMO_CLI_BIN;

	public void setSumoBin(String sumoBin) {
		this.sumoBin = sumoBin;
	}

	public String getSumoBin() {
		return sumoBin;
	}

	static final String CONFIG_FILE = "data/hard-braking-connected.sumocfg";
	// simulation step length is in seconds
	static final double STEP_LENGTH = 0.1;
	static double STEP_DELAY = 0.0;

	// how often the monte carlo simulation should be run
	static final int NUM_MONTE_CARLO_RUNS = 50;

	static final double busMaxSpeedSigma = 2.0;
	static final double busMaxSpeedMean = 8.3;

	static final double bicycleMaxSpeed = 4.7;

	public final double busMaxSpeed;
	public final double bikeMaxSpeed;

	public SimulationParameters(double busMaxSpeed, double bikeMaxSpeed) {
		this.busMaxSpeed = busMaxSpeed;
		this.bikeMaxSpeed = bikeMaxSpeed;
	}
}
