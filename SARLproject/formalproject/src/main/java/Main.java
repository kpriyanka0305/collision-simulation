import io.sarl.bootstrap.SRE;
import io.sarl.bootstrap.SREBootstrap;
import io.sarl.demos.basic.helloworld.WarningService;
import it.polito.appeal.traci.SumoTraciConnection;
import io.sarl.demos.basic.helloworld.Simulations.*;
import de.tudresden.sumo.config.Constants;
import de.tudresden.sumo.subscription.ResponseType;
import de.tudresden.sumo.subscription.SubscribtionVariable;
import de.tudresden.sumo.subscription.SubscriptionObject;
import de.tudresden.sumo.subscription.VariableSubscription;
import de.tudresden.sumo.util.Observable;
import de.tudresden.sumo.util.Observer;
import de.tudresden.ws.container.SumoStringList;

// Hello

public class Main implements Observer {
	static String sumo_bin = "sumo-gui";
	static String config_file = "data/cross.sumocfg";
	static double step_length = 0.2;

	SumoTraciConnection conn;

	public static void main(String[] args) throws Exception {
		SumoTraciConnection connection = SumoConnect();

		subscribe(connection);

		SREBootstrap bootstrap = SRE.getBootstrap();
		bootstrap.startAgent(WarningService.class, connection);
//        bootstrap.startAgent(Chaos.class, connection);
		// bootstrap.startAgent(OnlyRSUWithCamera.class, connection);
	}

	public static SumoTraciConnection SumoConnect() throws Exception {
		SumoTraciConnection conn = new SumoTraciConnection(sumo_bin, config_file);
		conn.addOption("step-length", step_length + "");
		conn.addOption("start", "false"); // start sumo immediately
		conn.addOption("log", "data/log.txt");
		conn.runServer();
		conn.setOrder(1);
		return conn;
	}

	public static void subscribe(SumoTraciConnection conn) throws Exception {
		conn.addObserver(new Main());
		VariableSubscription vs = new VariableSubscription(SubscribtionVariable.simulation, 0, 100000 * 60, "");
		vs.addCommand(Constants.VAR_DEPARTED_VEHICLES_IDS);
		vs.addCommand(Constants.VAR_ARRIVED_VEHICLES_IDS);
		conn.do_subscription(vs);
	}

	@Override
	public void update(Observable arg0, SubscriptionObject so) {
		if (so.response == ResponseType.SIM_VARIABLE) {
			if (so.variable == Constants.VAR_DEPARTED_VEHICLES_IDS) {
				SumoStringList ssl = (SumoStringList) so.object;
				if (ssl.size() > 0) {
					for (String vehID : ssl) {
						System.out.println("Subscription Departed vehicles: " + vehID);
					}
				}
			}
			else if (so.variable == Constants.VAR_ARRIVED_VEHICLES_IDS) {
				SumoStringList ssl = (SumoStringList) so.object;
				if (ssl.size() > 0) {
					for (String vehID : ssl) {
						System.out.println("Subscription Arrived vehicles: " + vehID);
					}
				}
			}
		}
	}
}
