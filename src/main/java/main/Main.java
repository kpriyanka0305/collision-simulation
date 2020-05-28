package main;

import it.polito.appeal.traci.SumoTraciConnection;
import kpi.Kpi;
import agent.*;
import de.tudresden.sumo.cmd.Vehicle;
import de.tudresden.sumo.config.Constants;
import de.tudresden.sumo.subscription.ResponseType;
import de.tudresden.sumo.subscription.SubscribtionVariable;
import de.tudresden.sumo.subscription.SubscriptionObject;
import de.tudresden.sumo.subscription.VariableSubscription;
import de.tudresden.sumo.util.Observable;
import de.tudresden.sumo.util.Observer;
import de.tudresden.ws.container.SumoStringList;

public class Main implements Observer {
	static final String SUMO_BIN = "sumo-gui";
	static final String CONFIG_FILE = "data/hard-braking-connected.sumocfg";
	static final double STEP_LENGTH = 0.1;
	static final String BUS_PREFIX = "bus";
	static final String BIKE_PREFIX = "bicycle";

	private SumoTraciConnection conn;
	private Kpi kpi;
	private SimulationParameters simParameters;

	public Main(String sumocfg, double busMaxSpeed, double bikeMaxSpeed) throws Exception {
		this.conn = SumoConnect(sumocfg);
		this.kpi = new Kpi(conn);
		this.simParameters = new SimulationParameters(busMaxSpeed, bikeMaxSpeed);
		subscribe();
	}

	public static void main(String[] args) throws Exception {
		String sumocfg = CONFIG_FILE;
		if (args.length > 0) {
			sumocfg = args[0];
		}

		long startTime = System.nanoTime();

		for (int i = 0; i < 1; i++) {
			Main m = new Main(sumocfg, 8.3, 4.7);
			m.runSimulation();
		}

		long endTime = System.nanoTime();
		long duration = (endTime - startTime);
		System.out.println("elapsed time: " + duration / 1000000 + " ms");
	}

	private void runSimulation() throws Exception {
		Simulation sim = new SimWarningService(conn, kpi, simParameters);
//		Simulation sim = new SimChaos(conn, kpi);
		while (sim.step()) {
			Thread.sleep(10);
		}
		conn.close();
	}

	public static SumoTraciConnection SumoConnect(String sumocfg) throws Exception {
		SumoTraciConnection conn = new SumoTraciConnection(SUMO_BIN, sumocfg);
		conn.addOption("step-length", STEP_LENGTH + "");
		conn.addOption("start", "true"); // start simulation at startup
		conn.addOption("log", "out/log.txt");
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
//							Double curMaxSpeed = (Double) conn.do_job_get(Vehicle.getMaxSpeed(vehicleID));
//							System.out.println("Departed vehicle: " + vehicleID + " max speed " + curMaxSpeed);
							if (vehicleID.startsWith(BUS_PREFIX)) {
								conn.do_job_set(Vehicle.setMaxSpeed(vehicleID, simParameters.busMaxSpeed));
								kpi.addBus(vehicleID);
							} else if (vehicleID.startsWith(BIKE_PREFIX)) {
								// TODO: same problem as with bus, this changes the ID name of the bike. Need to adjust in agent (I think it was camera)
								conn.do_job_set(Vehicle.setMaxSpeed(vehicleID, simParameters.bikeMaxSpeed));
								kpi.addBike(vehicleID);
							}
						}
					}
				} else if (so.variable == Constants.VAR_ARRIVED_VEHICLES_IDS) {
					SumoStringList ssl = (SumoStringList) so.object;
					if (ssl.size() > 0) {
						for (String vehicleID : ssl) {
							System.out.println("Arrived vehicle: " + vehicleID);
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