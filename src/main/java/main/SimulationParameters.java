package main;

public class SimulationParameters {
	public final static String OUT_DIR = "output";
	public static final String BUS_PREFIX = "taxi";
	public static final String BIKE_PREFIX = "bicycle";

	static final String SUMO_GUI_BIN = "sumo-gui";
	static final String SUMO_CLI_BIN = "sumo";
	private final String sumoBin;

	static final String CONFIG_FILE = "data/stationsplein/stationsplein.sumocfg";
	// simulation step length is in seconds
	public static final double STEP_LENGTH = 0.1;

	public final long GUI_STEP_DELAY = 10;
	public final long HEADLESS_STEP_DELAY = 0;
	private final long stepDelay;

	// how often the monte carlo simulation should be run
	public static final int NUM_MONTE_CARLO_RUNS = 10;

	static final double BUS_MAX_SPEED_SIGMA = 2.0;
	static final double BUS_MAX_SPEED_MEAN = 8.3;

	static final double BIKE_MAX_SPEED_SIGMA = 1.5;
	static final double BIKE_MAX_SPEED_MEAN = 4.7;

	public static final double DEFECTIVE_ITS_PROBABILITY = 0.5;

	public static final double NEAR_COLLISION_DISTANCE = 2.0;

	public final double busMaxSpeed;
	public final double bikeMaxSpeed;
	public final boolean defectiveITS;

	public final static String DISTANCES_BASE = "/distances";
	public final static String ACCELERATIONS_BASE = "/accelerations";
	public final static String SPEEDS_BASE = "/speeds";
	public final static String WAITING_TIME_BASE = "/waitingTime";
	public final static String WAITING_TIME_TABLE_BASE = "/waitingTimeTable";

	public String getSumoBin() {
		return sumoBin;
	}

	public long getStepDelay() {
		return stepDelay;
	}

	public SimulationParameters(UserInterfaceType uiType, double busMaxSpeed, double bikeMaxSpeed,
			boolean defectiveITS) {
		this.busMaxSpeed = busMaxSpeed;
		this.bikeMaxSpeed = bikeMaxSpeed;
		this.defectiveITS = defectiveITS;

		switch (uiType) {
		case Headless:
			sumoBin = SUMO_CLI_BIN;
			stepDelay = HEADLESS_STEP_DELAY;
			break;
		case GUI:
			sumoBin = SUMO_GUI_BIN;
			stepDelay = GUI_STEP_DELAY;
			break;
		default:
			throw new IllegalArgumentException("Unknown UserInterfaceType");
		}
	}
}
