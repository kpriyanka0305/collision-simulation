package main;

public class SimulationParameters {
	private final static String OUT_DIR = "output";

	public static String getOutDir() {
		return OUT_DIR;
	}

	private static final String BUS_PREFIX = "taxi";
	private static final String BIKE_PREFIX = "bicycle";
	private static final String PEDESTRIAN_PREFIX = "pedestrian";

	public static String getBusPrefix() {
		return BUS_PREFIX;
	}

	public static String getBikePrefix() {
		return BIKE_PREFIX;
	}

	public static String getPedestrianPrefix() {
		return PEDESTRIAN_PREFIX;
	}

	private static final String SUMO_GUI_BIN = "sumo-gui";
	private static final String SUMO_CLI_BIN = "sumo";
	private final String sumoBin;

	private static final String CONFIG_FILE = "data/stationsplein/stationsplein.sumocfg";

	public static String getConfigFile() {
		return CONFIG_FILE;
	}

	// We store the random seed to be able to reproduce runs
	private final long seed;

	public long getSeed() {
		return seed;
	}

	// The center of the junction that the warning system should control.
	// We don't want to use sumo's position of the junction, because that is not
	// always in the center. Better to specify it manually. Get the coordinates by
	// hovering the mouse over the junction center in netedit.
	private static final double REFERENCE_POINT_X = 67.83;
	private static final double REFERENCE_POINT_Y = 24.31;

	public static double getReferencePointX() {
		return REFERENCE_POINT_X;
	}

	public static double getReferencePointY() {
		return REFERENCE_POINT_Y;
	}

	// simulation step length is in seconds
	private static final double STEP_LENGTH = 0.1;

	public static double getStepLength() {
		return STEP_LENGTH;
	}

	private final long GUI_STEP_DELAY = 10;
	private final long HEADLESS_STEP_DELAY = 0;
	private final long stepDelay;

	// how often the monte carlo simulation should be run
	private static final int NUM_MONTE_CARLO_RUNS = 50;

	public static int getNumMonteCarloRuns() {
		return NUM_MONTE_CARLO_RUNS;
	}

	private static final double BUS_MAX_SPEED_SIGMA = 1.5;
	private static final double BUS_MAX_SPEED_MEAN = 8.3;

	public static double getBusMaxSpeedSigma() {
		return BUS_MAX_SPEED_SIGMA;
	}

	public static double getBusMaxSpeedMean() {
		return BUS_MAX_SPEED_MEAN;
	}

	private static final double BIKE_MAX_SPEED_SIGMA = 1.5;
	private static final double BIKE_MAX_SPEED_MEAN = 4.7;

	public static double getBikeMaxSpeedSigma() {
		return BIKE_MAX_SPEED_SIGMA;
	}

	public static double getBikeMaxSpeedMean() {
		return BIKE_MAX_SPEED_MEAN;
	}

	private static final double REACTION_TIME_SIGMA = 0.01;
	private static final double REACTION_TIME_MEAN = 3.5;

	public static double getReactionTimeSigma() {
		return REACTION_TIME_SIGMA;
	}

	public static double getReactionTimeMean() {
		return REACTION_TIME_MEAN;
	}

	private static final double DEFECTIVE_ITS_PROBABILITY = 1.0;

	public static double getDefectiveItsProbability() {
		return DEFECTIVE_ITS_PROBABILITY;
	}

	private static final double NEAR_COLLISION_DISTANCE = 2.0;

	public static double getNearCollisionDistance() {
		return NEAR_COLLISION_DISTANCE;
	}

	private final static String DISTANCES_BASE = "distances";
	private final static String ACCELERATIONS_BASE = "accelerations";
	private final static String SPEEDS_BASE = "speeds";
	private final static String WAITING_TIME_BASE = "waitingTime";
	private final static String WAITING_TIME_TABLE_BASE = "waitingTimeTable";
	private final static String PARAMETERS_BASE = "parameters";

	public static String getDistancesBase() {
		return DISTANCES_BASE;
	}

	public static String getAccelerationsBase() {
		return ACCELERATIONS_BASE;
	}

	public static String getSpeedsBase() {
		return SPEEDS_BASE;
	}

	public static String getWaitingTimeBase() {
		return WAITING_TIME_BASE;
	}

	public static String getWaitingTimeTableBase() {
		return WAITING_TIME_TABLE_BASE;
	}

	public static String getParametersBase() {
		return PARAMETERS_BASE;
	}

	public String getSumoBin() {
		return sumoBin;
	}

	public long getStepDelay() {
		return stepDelay;
	}

	public SimulationParameters(UserInterfaceType uiType, long seed) {
		this.seed = seed;

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
