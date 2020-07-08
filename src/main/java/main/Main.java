package main;

import java.util.Date;
import java.util.Optional;
import java.util.Random;

import agent.SimWarningService;
import de.tudresden.sumo.cmd.Simulation;
import de.tudresden.sumo.cmd.Vehicle;
import de.tudresden.sumo.config.Constants;
import de.tudresden.sumo.subscription.ResponseType;
import de.tudresden.sumo.subscription.SubscribtionVariable;
import de.tudresden.sumo.subscription.SubscriptionObject;
import de.tudresden.sumo.subscription.VariableSubscription;
import de.tudresden.sumo.util.Observable;
import de.tudresden.sumo.util.Observer;
import de.tudresden.ws.container.SumoStringList;
import it.polito.appeal.traci.SumoTraciConnection;
import kpi.Kpi;
import kpi.SimulationStatistics;
import util.Stopwatch;

public class Main implements Observer {

	private SumoTraciConnection conn;
	private Kpi kpi;
	private SimulationParameters simParameters;
	private Optional<SimulationStatistics> statistics = Optional.empty();

	public Main(Date timestamp, String sumocfg, Optional<SimulationStatistics> statistics, double busMaxSpeed,
			double bikeMaxSpeed, boolean defectiveITS) throws Exception {
		this.simParameters = new SimulationParameters(UserInterfaceType.GUI, busMaxSpeed, bikeMaxSpeed, defectiveITS);
		this.conn = SumoConnect(sumocfg, simParameters);
		this.kpi = new Kpi(conn, timestamp);
		this.statistics = statistics;
		subscribe();
	}

	public static void main(String[] args) throws Exception {
		String sumocfg = SimulationParameters.CONFIG_FILE;
		if (args.length > 0) {
			sumocfg = args[0];
		}

		// timestamp must be the same across all simulation runs. It is used for
		// the file name of the data logs.
		Date timestamp = new Date();

		Stopwatch totalTime = new Stopwatch();

		monteCarloSimulation(sumocfg, timestamp);
//		crispSimulation(sumocfg, timestamp);

		totalTime.stop();
		totalTime.printTime("total time");
	}

	private static void crispSimulation(String sumocfg, Date timestamp) throws Exception {
		Main m = new Main(timestamp, sumocfg, Optional.empty(), SimulationParameters.busMaxSpeedMean,
				SimulationParameters.bicycleMaxSpeedMean, true);
		m.runSimulation();
	}

	private static void monteCarloSimulation(String sumocfg, Date timestamp) throws Exception {
		Random r = new Random();
		SimulationStatistics statistics = new SimulationStatistics();
		for (int i = 0; i < SimulationParameters.NUM_MONTE_CARLO_RUNS; i++) {
//		for (double busSpeed = 5.0; busSpeed < 8.3; busSpeed += 0.1) {
			Stopwatch singleRun = new Stopwatch();

			double busMaxSpeed = (r.nextGaussian() * SimulationParameters.busMaxSpeedSigma)
					+ SimulationParameters.busMaxSpeedMean;
			double bikeMaxSpeed = (r.nextGaussian() * SimulationParameters.bicycleMaxSpeedSigma)
					+ SimulationParameters.bicycleMaxSpeedMean;
			Main m = new Main(timestamp, sumocfg, Optional.of(statistics), busMaxSpeed, bikeMaxSpeed, false);
			m.runSimulation();

			singleRun.stop();
			singleRun.printTime("lap time " + i);
		}
//		Kpi.writeSpeedsHistogramGraph(timestamp, busWaitingTimes);
		statistics.writeStatisticsTable(timestamp);
	}

	private void runSimulation() throws Exception {
		agent.Simulation sim = new SimWarningService(conn, kpi, simParameters);
//		agent.Simulation sim = new SimChaos(conn, kpi);
		// getMinExpectedNumber returns present and future vehicles. If that
		// number is 0 we are done.
		while ((int) (conn.do_job_get(Simulation.getMinExpectedNumber())) > 0) {
			sim.step();
			Thread.sleep(simParameters.getStepDelay());
		}
		conn.close();
	}

	public static SumoTraciConnection SumoConnect(String sumocfg, SimulationParameters params) throws Exception {
		SumoTraciConnection conn = new SumoTraciConnection(params.getSumoBin(), sumocfg);
		conn.addOption("step-length", SimulationParameters.STEP_LENGTH + "");
		conn.addOption("start", "true"); // start simulation at startup
		conn.addOption("log", SimulationParameters.OUT_DIR + "/log.txt");
		conn.runServer();
		// Mandatory when using multiple clients. I'm not sure what this is doing here.
		conn.setOrder(1);
		return conn;
	}

	public void subscribe() throws Exception {
		conn.addObserver(this);
		VariableSubscription vs = new VariableSubscription(SubscribtionVariable.simulation, 0, 100000 * 60, "");
		vs.addCommand(Constants.VAR_DEPARTED_VEHICLES_IDS);
		vs.addCommand(Constants.VAR_ARRIVED_VEHICLES_IDS);
		conn.do_subscription(vs);
	}

	@Override
	public void update(Observable arg0, SubscriptionObject so) {
		try {
			if (so.response == ResponseType.SIM_VARIABLE) {
				if (so.variable == Constants.VAR_DEPARTED_VEHICLES_IDS) {
					SumoStringList ssl = (SumoStringList) so.object;
					if (ssl.size() > 0) {
						for (String vehicleID : ssl) {
							if (vehicleID.startsWith(SimulationParameters.BUS_PREFIX)) {
								conn.do_job_set(Vehicle.setMaxSpeed(vehicleID, simParameters.busMaxSpeed));
								conn.do_job_set(Vehicle.setSpeedMode(vehicleID, 7));
								conn.do_job_set(Vehicle.setMinGap(vehicleID, 0));
								kpi.addBus(vehicleID, simParameters.busMaxSpeed);
								statistics.ifPresent(s -> s.setCurrentBusMaxSpeed(simParameters.busMaxSpeed));
							} else if (vehicleID.startsWith(SimulationParameters.BIKE_PREFIX)) {
								conn.do_job_set(Vehicle.setMaxSpeed(vehicleID, simParameters.bikeMaxSpeed));
								kpi.addBike(vehicleID, simParameters.bikeMaxSpeed);
								statistics.ifPresent(s -> s.setCurrentBikeMaxSpeed(simParameters.bikeMaxSpeed));
							}
						}
					}
				} else if (so.variable == Constants.VAR_ARRIVED_VEHICLES_IDS) {
					SumoStringList ssl = (SumoStringList) so.object;
					if (ssl.size() > 0) {
						for (String vehicleID : ssl) {
							if (vehicleID.startsWith(SimulationParameters.BUS_PREFIX)) {
								// must be called before kpi.removeBus
								statistics.ifPresent(s -> s.busArrived(kpi, vehicleID));
								kpi.removeBus(vehicleID);
							} else if (vehicleID.startsWith(SimulationParameters.BIKE_PREFIX)) {
								kpi.removeBike(vehicleID);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}