package main;

import java.io.Serializable;

public class SimulationParameters implements Serializable {
	private static final long serialVersionUID = 4676500355283812043L;
	public final static String OUT_DIR = "output";
	public static final String BUS_PREFIX = "taxi";
	public static final String BIKE_PREFIX = "bicycle";
	public static final String PEDESTRIAN_PREFIX = "pedestrian";

	static final String SUMO_GUI_BIN = "sumo-gui";
	static final String SUMO_CLI_BIN = "sumo";
	private final String sumoBin;

	static final String CONFIG_FILE = "data/stationsplein/stationsplein.sumocfg";

	// We store the random seed to be able to reproduce runs
	long randomSeed;

	// The center of the junction that the warning system should control.
	// We don't want to use sumo's position of the junction, because that is not
	// always in the center. Better to specify it manually. Get the coordinates by
	// hovering the mouse over the junction center in netedit.
	public static final double REFERENCE_POINT_X = 67.83;
	public static final double REFERENCE_POINT_Y = 24.31;

	// simulation step length is in seconds
	public static final double STEP_LENGTH = 0.1;

	public final long GUI_STEP_DELAY = 10;
	public final long HEADLESS_STEP_DELAY = 0;
	private final long stepDelay;

	// how often the monte carlo simulation should be run
	public static final int NUM_MONTE_CARLO_RUNS = 50;

	static final double BUS_MAX_SPEED_SIGMA = 0.01;
	static final double BUS_MAX_SPEED_MEAN = 8.3;

	static final double BIKE_MAX_SPEED_SIGMA = 0.01;
	static final double BIKE_MAX_SPEED_MEAN = 4.7;
	
	static final double REACTION_TIME_SIGMA = 1.5;
	static final double REACTION_TIME_MEAN = 3.5;

	public static final double DEFECTIVE_ITS_PROBABILITY = 0.0;

	public static final double NEAR_COLLISION_DISTANCE = 2.0;

	public final double busMaxSpeed;
	public final double bikeMaxSpeed;
	public final boolean defectiveITS;
	public final double reactionTime;

	public final static String DISTANCES_BASE = "distances";
	public final static String ACCELERATIONS_BASE = "accelerations";
	public final static String SPEEDS_BASE = "speeds";
	public final static String WAITING_TIME_BASE = "waitingTime";
	public final static String WAITING_TIME_TABLE_BASE = "waitingTimeTable";
	public static final String PARAMETERS_BASE = "parameters";

	public String getSumoBin() {
		return sumoBin;
	}

	public long getStepDelay() {
		return stepDelay;
	}

	public SimulationParameters(UserInterfaceType uiType, double busMaxSpeed, double bikeMaxSpeed, boolean defectiveITS,
			double reactionTime, long seed) {
		this.busMaxSpeed = busMaxSpeed;
		this.bikeMaxSpeed = bikeMaxSpeed;
		this.defectiveITS = defectiveITS;
		this.reactionTime = reactionTime;
		this.randomSeed = seed;

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
