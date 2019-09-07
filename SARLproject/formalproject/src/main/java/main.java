import io.sarl.bootstrap.SRE;
import io.sarl.bootstrap.SREBootstrap;
import io.sarl.demos.basic.helloworld.WarningService;
import it.polito.appeal.traci.SumoTraciConnection;
import io.sarl.demos.basic.helloworld.Simulations.*;

// Hello

public class main {		
	static String sumo_bin = "sumo-gui";
	static String config_file = "data/cross.sumocfg";
	static double step_length = 0.2;

	public static void main(String[] args) throws Exception {		
		SumoTraciConnection connection = SumoConnect();
		SREBootstrap bootstrap = SRE.getBootstrap();
		bootstrap.startAgent(WarningService.class, connection);	
		//bootstrap.startAgent(Chaos.class, connection);	
		//bootstrap.startAgent(OnlyRSUWithCamera.class, connection);	
	}
	
	public static SumoTraciConnection SumoConnect() throws Exception{		
		SumoTraciConnection conn = new SumoTraciConnection(sumo_bin, config_file);
		conn.addOption("step-length", step_length+"");
		conn.addOption("start", "false"); //start sumo immediately
		conn.addOption("log", "data/log.txt");		
		return conn;			
	}
}
