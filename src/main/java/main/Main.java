package main;

import java.util.Date;
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
	private SimulationProperties simParams;
	private RandomVariables randomVars;
	private SimulationStatistics statistics;

	public Main(Date timestamp, SimulationProperties simParams, RandomVariables randomVars,
			SimulationStatistics statistics) throws Exception {
		this.simParams = simParams;
		this.randomVars = randomVars;
		this.conn = SumoConnect(simParams.getSumoConfigFileName(), simParams);
		this.kpi = new Kpi(simParams, conn, timestamp);
		this.statistics = statistics;
		subscribe();
	}

	public static void main(String[] args) throws Exception {
		// timestamp must be the same across all simulation runs. It is used for
		// the file name of the data logs.
		Date timestamp = new Date();

		Random r = new Random();
		long seed = r.nextLong();

		SimulationProperties simParams = new SimulationProperties(UserInterfaceType.GUI, UncertaintyType.Crisp, seed);
//		SimulationProperties simParams = new SimulationProperties(UserInterfaceType.Headless, UncertaintyType.MonteCarlo, seed);
//		SimulationProperties simParams = new SimulationProperties("output/" + "2020-08-24-16-22-33" + "/parameters.properties");

		SimulationStatistics statistics = new SimulationStatistics(simParams);

		Stopwatch totalTime = new Stopwatch();

		switch (simParams.getUncertaintyType()) {
		case Crisp: {
			RandomVariables randomVars = new RandomVariables(simParams, simParams.getUncertaintyType());
			statistics.setCurrentRandomVars(randomVars);
			Main m = new Main(timestamp, simParams, randomVars, statistics);
			m.runSimulation();
			break;
		}
		case MonteCarlo: {
			for (int i = 0; i < simParams.getNumMonteCarloRuns(); i++) {
				Stopwatch singleRun = new Stopwatch();

				RandomVariables randomVars = new RandomVariables(simParams, simParams.getUncertaintyType());
				statistics.setCurrentRandomVars(randomVars);
				Main m = new Main(timestamp, simParams, randomVars, statistics);
				m.runSimulation();

				singleRun.stop();
				singleRun.printTime("lap time " + i);
			}
			break;
		}
		}

		totalTime.stop();
		totalTime.printTime("total time");

		statistics.writeStatistics(timestamp);
	}

	private void runSimulation() throws Exception {
		statistics.setCurrentReactionTime(randomVars.reactionTime);
		agent.Simulation sim = new SimWarningService(conn, kpi, simParams, randomVars);
//		agent.Simulation sim = new SimChaos(conn, kpi);
		// getMinExpectedNumber returns present and future vehicles. If that
		// number is 0 we are done.
		while ((int) (conn.do_job_get(Simulation.getMinExpectedNumber())) > 0) {
			sim.step();
			Thread.sleep(simParams.getStepDelay());
		}
		conn.close();
	}

	public static SumoTraciConnection SumoConnect(String sumocfg, SimulationProperties simParams) throws Exception {
		SumoTraciConnection conn = new SumoTraciConnection(simParams.getSumoBin(), sumocfg);
		conn.addOption("quit-on-end", "true");
		conn.addOption("step-length", simParams.getStepLength() + "");
		conn.addOption("start", "true"); // start simulation at startup
		conn.addOption("log", simParams.getOutDir() + "/log.txt");
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
							if (vehicleID.startsWith(simParams.getBusPrefix())) {
								conn.do_job_set(Vehicle.setMaxSpeed(vehicleID, randomVars.busMaxSpeed));
								// toggling these two parameters turns a distracted taxi into a observant one
								conn.do_job_set(Vehicle.setSpeedMode(vehicleID, 0));
								conn.do_job_set(Vehicle.setMinGap(vehicleID, 0));
								kpi.addBus(vehicleID, randomVars.busMaxSpeed);
								statistics.setCurrentBusMaxSpeed(randomVars.busMaxSpeed);
							} else if (vehicleID.startsWith(simParams.getBikePrefix())
									|| vehicleID.startsWith(simParams.getPedestrianPrefix())) {
								conn.do_job_set(Vehicle.setMaxSpeed(vehicleID, randomVars.bikeMaxSpeed));
								conn.do_job_set(Vehicle.setSpeedMode(vehicleID, 0));
								conn.do_job_set(Vehicle.setMinGap(vehicleID, 0));
								kpi.addBike(vehicleID, randomVars.bikeMaxSpeed);
								statistics.setCurrentBikeMaxSpeed(randomVars.bikeMaxSpeed);
							}
						}
					}
				} else if (so.variable == Constants.VAR_ARRIVED_VEHICLES_IDS) {
					SumoStringList ssl = (SumoStringList) so.object;
					if (ssl.size() > 0) {
						for (String vehicleID : ssl) {
							if (vehicleID.startsWith(simParams.getBusPrefix())) {
								// must be called before kpi.removeBus
								statistics.busArrived(kpi, vehicleID);
								kpi.removeBus(vehicleID);
							} else if (vehicleID.startsWith(simParams.getBikePrefix())) {
								kpi.removeBike(vehicleID);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace(System.out);
			// Ideally I would want to propagate the exception to terminate the program. But
			// since update is called from a different thread, and update does not
			// allow exceptions, I think this is the way to go.
			System.exit(1);
		}
	}
}