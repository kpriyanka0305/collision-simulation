package main;

import it.polito.appeal.traci.SumoTraciConnection;
import kpi.Kpi;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import agent.*;
import de.tudresden.sumo.cmd.Vehicle;
import de.tudresden.sumo.cmd.Simulation;
import de.tudresden.sumo.config.Constants;
import de.tudresden.sumo.subscription.ResponseType;
import de.tudresden.sumo.subscription.SubscribtionVariable;
import de.tudresden.sumo.subscription.SubscriptionObject;
import de.tudresden.sumo.subscription.VariableSubscription;
import de.tudresden.sumo.util.Observable;
import de.tudresden.sumo.util.Observer;
import de.tudresden.ws.container.SumoStringList;

public class Main implements Observer {
	static final String SUMO_BIN = "sumo";
	static final String CONFIG_FILE = "data/hard-braking-connected.sumocfg";
	static final double STEP_LENGTH = 0.1;
	static final String BUS_PREFIX = "bus";
	static final String BIKE_PREFIX = "bicycle";

	static final double busMaxSpeedSigma = 1.0;
	static final double busMaxSpeed = 8.3;

	static final double bicycleMaxSpeed = 4.7;

	private SumoTraciConnection conn;
	private Kpi kpi;
	private SimulationParameters simParameters;

	public Main(Date timestamp, String sumocfg, double busMaxSpeed, double bikeMaxSpeed) throws Exception {
		this.conn = SumoConnect(sumocfg);
		this.kpi = new Kpi(conn, timestamp);
		this.simParameters = new SimulationParameters(busMaxSpeed, bikeMaxSpeed);
		subscribe();
	}

	public static void main(String[] args) throws Exception {
		String sumocfg = CONFIG_FILE;
		if (args.length > 0) {
			sumocfg = args[0];
		}

		// timestamp must be the same across all simulation runs. It is used for
		// the file name of the data logs.
		Date timestamp = new Date();

		long startTime = System.nanoTime();

		monteCarloSimulation(sumocfg, timestamp);

		long endTime = System.nanoTime();
		long duration = (endTime - startTime);
		System.out.println("elapsed time: " + duration / 1000000 + " ms");
	}

	private static void crispSimulation(String sumocfg, Date timestamp) throws Exception {
		Main m = new Main(timestamp, sumocfg, busMaxSpeed, bicycleMaxSpeed);
		m.runSimulation();
	}

	private static void monteCarloSimulation(String sumocfg, Date timestamp) throws Exception {
		Random r = new Random();
		for (int i = 0; i < 10; i++) {
			double busSpeed = r.nextGaussian() * busMaxSpeedSigma + busMaxSpeed;
			Main m = new Main(timestamp, sumocfg, busSpeed, bicycleMaxSpeed);
			m.runSimulation();
		}
	}

	private void runSimulation() throws Exception {
		agent.Simulation sim = new SimWarningService(conn, kpi, simParameters);
//		agent.Simulation sim = new SimChaos(conn, kpi);
		// getMinExpectedNumber returns present and future vehicles. If that
		// number is 0 we are done.
		while ((int) (conn.do_job_get(Simulation.getMinExpectedNumber())) > 0) {
			sim.step();
		}
		conn.close();
	}

	public static SumoTraciConnection SumoConnect(String sumocfg) throws Exception {
		SumoTraciConnection conn = new SumoTraciConnection(SUMO_BIN, sumocfg);
		conn.addOption("step-length", STEP_LENGTH + "");
		conn.addOption("start", "true"); // start simulation at startup
		conn.addOption("log", SimulationParameters.OUT_DIR + "/log.txt");
		conn.runServer();
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
							if (vehicleID.startsWith(BUS_PREFIX)) {
								conn.do_job_set(Vehicle.setMaxSpeed(vehicleID, simParameters.busMaxSpeed));
								kpi.addBus(vehicleID, simParameters.busMaxSpeed);
							} else if (vehicleID.startsWith(BIKE_PREFIX)) {
								conn.do_job_set(Vehicle.setMaxSpeed(vehicleID, simParameters.bikeMaxSpeed));
								kpi.addBike(vehicleID);
							}
						}
					}
				} else if (so.variable == Constants.VAR_ARRIVED_VEHICLES_IDS) {
					SumoStringList ssl = (SumoStringList) so.object;
					if (ssl.size() > 0) {
						for (String vehicleID : ssl) {
							if (vehicleID.startsWith(BUS_PREFIX)) {
								kpi.removeBus(vehicleID);
							} else if (vehicleID.startsWith(BIKE_PREFIX)) {
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