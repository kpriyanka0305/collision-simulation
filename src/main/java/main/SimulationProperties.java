package main;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;
import java.util.Random;

/* There are two cases we want to handle:
 * 1. Fresh simulation run, where the seed is randomly chosen and parameters come from the default .properties file.
 * 2. Reproduced simulation run, where seed and parameters come from the recorded .properties file.
 */
public class SimulationProperties {
	public String getOutDir() {
		return getProperty("outDir");
	}

	public String getBusPrefix() {
		return getProperty("busPrefix");
	}

	public String getBikePrefix() {
		return getProperty("bikePrefix");
	}

	public String getPedestrianPrefix() {
		return getProperty("pedestrianPrefix");
	}

	public String getSumoConfigFileName() {
		return getProperty("configFile");
	}

	// The center of the junction that the warning system should control.
	// We don't want to use sumo's position of the junction, because that is not
	// always in the center. Better to specify it manually. Get the coordinates by
	// hovering the mouse over the junction center in netedit.
	public double getReferencePointX() {
		return Double.parseDouble(getProperty("referencePointX"));
	}

	public double getReferencePointY() {
		return Double.parseDouble(getProperty("referencePointY"));
	}

	// simulation step length is in seconds
	public double getStepLength() {
		return Double.parseDouble(getProperty("stepLength"));
	}

	// how often the Monte Carlo simulation should be run
	public int getNumMonteCarloRuns() {
		return Integer.parseInt(getProperty("numMonteCarloRuns"));
	}

	public double getBusMaxSpeedSigma() {
		return Double.parseDouble(getProperty("busSpeedSigma"));
	}

	public double getBusMaxSpeedMean() {
		return Double.parseDouble(getProperty("busSpeedMean"));
	}

	public double getBikeMaxSpeedSigma() {
		return Double.parseDouble(getProperty("bikeSpeedSigma"));
	}

	public double getBikeMaxSpeedMean() {
		return Double.parseDouble(getProperty("bikeSpeedMean"));
	}

	public double getReactionTimeSigma() {
		return Double.parseDouble(getProperty("reactionTimeSigma"));
	}

	public double getReactionTimeMean() {
		return Double.parseDouble(getProperty("reactionTimeMean"));
	}

	public double getDefectiveItsProbability() {
		return Double.parseDouble(getProperty("defectiveItsProbability"));
	}

	public double getNearCollisionDistance() {
		return Double.parseDouble(getProperty("nearCollisionDistance"));
	}

	// These don't need to change, don't need to be in the properties file, can be
	// hardcoded
	private final static String DISTANCES_BASE = "distances";
	private final static String ACCELERATIONS_BASE = "accelerations";
	private final static String SPEEDS_BASE = "speeds";
	private final static String WAITING_TIME_BASE = "waitingTime";
	private final static String WAITING_TIME_TABLE_BASE = "waitingTimeTable";
	private final static String PARAMETERS_BASE = "parameters";

	public String getDistancesBase() {
		return DISTANCES_BASE;
	}

	public String getAccelerationsBase() {
		return ACCELERATIONS_BASE;
	}

	public String getSpeedsBase() {
		return SPEEDS_BASE;
	}

	public String getWaitingTimeBase() {
		return WAITING_TIME_BASE;
	}

	public String getWaitingTimeTableBase() {
		return WAITING_TIME_TABLE_BASE;
	}

	public String getParametersBase() {
		return PARAMETERS_BASE;
	}

	// We store the random seed to be able to reproduce runs
	private final long seed;
	private final Random random;

	public Random getRandom() {
		return random;
	}

	// TODO: how to handle this?
	private static final String SUMO_GUI_BIN = "sumo-gui";
	private static final String SUMO_CLI_BIN = "sumo";
	private final String sumoBin;

	public String getSumoBin() {
		return sumoBin;
	}

	private final long GUI_STEP_DELAY = 10;
	private final long HEADLESS_STEP_DELAY = 0;
	private final long stepDelay;

	public long getStepDelay() {
		return stepDelay;
	}

	private final Properties prop;

	private String getProperty(String propName) {
		String result = prop.getProperty(propName);
		if (result == null) {
			throw new IllegalArgumentException("no such property: " + propName);
		}
		return result;
	}

	/**
	 * This constructor is for reproduced simulation runs. All properties come from
	 * the recorded .properties file, and ideally we should see identical behaviour.
	 * 
	 * @param propertyFilename The property file to load.
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public SimulationProperties(String propertyFilename) throws FileNotFoundException, IOException {
		prop = new Properties();
		prop.load(new FileInputStream(propertyFilename));

		this.seed = Long.parseLong(prop.getProperty("seed"));
		this.random = new Random(seed);

		this.stepDelay = Integer.parseInt(prop.getProperty("stepDelay"));
		this.sumoBin = prop.getProperty("sumoBin");
	}

	/**
	 * This constructor is for fresh simulation runs. The seed should be randomly
	 * chosen by the main function.
	 * 
	 * @param uiType
	 * @param seed
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public SimulationProperties(UserInterfaceType uiType, long seed) throws FileNotFoundException, IOException {
		prop = new Properties();
		prop.load(new FileInputStream("simulation.properties"));

		// The file git.properties is created by the git maven plugin
		// https://github.com/git-commit-id/git-commit-id-maven-plugin
		Properties gitProp = new Properties();
		gitProp.load(new FileInputStream("git.properties"));
		prop.setProperty("git.commit.id.full", gitProp.getProperty("git.commit.id.full"));

		this.seed = seed;
		this.random = new Random(seed);
		prop.setProperty("seed", "" + seed);

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

	public void store(String fileName) throws IOException {
		prop.store(new FileWriter(fileName), "SimulationProperties");
	}
}
