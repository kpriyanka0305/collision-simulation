package main;

public class SimulationParameters {
	public final static String OUT_DIR = "output";
	static final String BUS_PREFIX = "bus";
	static final String BIKE_PREFIX = "bicycle";

	static final String SUMO_GUI_BIN = "sumo-gui";
	static final String SUMO_CLI_BIN = "sumo";
	private final String sumoBin;

	static final String CONFIG_FILE = "data/hard-braking-connected.sumocfg";
	// simulation step length is in seconds
	public static final double STEP_LENGTH = 0.3;
	private final long stepDelay;

	// how often the monte carlo simulation should be run
	public static final int NUM_MONTE_CARLO_RUNS = 10;

	static final double busMaxSpeedSigma = 2.0;
	static final double busMaxSpeedMean = 8.3;

	static final double bicycleMaxSpeedMean = 4.7;
	static final double bicycleMaxSpeedSigma = 1.5;

	public final double busMaxSpeed;
	public final double bikeMaxSpeed;

	public String getSumoBin() {
		return sumoBin;
	}

	public long getStepDelay() {
		return stepDelay;
	}

	public SimulationParameters(UserInterfaceType uiType, double busMaxSpeed, double bikeMaxSpeed) {
		this.busMaxSpeed = busMaxSpeed;
		this.bikeMaxSpeed = bikeMaxSpeed;

		switch (uiType) {
		case Headless:
			sumoBin = SUMO_CLI_BIN;
			stepDelay = 0;
			break;
		case GUI:
			sumoBin = SUMO_GUI_BIN;
			stepDelay = 30;
			break;
		default:
			throw new IllegalArgumentException("Unknown UserInterfaceType");
		}
	}
}
