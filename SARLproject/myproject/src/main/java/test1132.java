import java.util.ArrayList;
import java.util.List;

import io.sarl.bootstrap.SRE;
import io.sarl.bootstrap.SREBootstrap;
import trasmapi.genAPI.Simulator;
import trasmapi.genAPI.TraSMAPI;
import trasmapi.sumo.Sumo;
import trasmapi.sumo.SumoCom;

public class test1132 {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		SREBootstrap bootstrap = SRE.getBootstrap();
		bootstrap.startAgent(MyAgent.class, "test");
		
		TraSMAPI api = new TraSMAPI("guisim"); 
		List<String> params = new ArrayList<String>();
		params.add("-c=data/cross.sumocfg");

		Sumo sumo = new Sumo("guisim");
		sumo.addParameters(params);
//
//		try
//		{
//		   SumoConfig conf = SumoConfig.load("data/cross.sumocfg");
//		   sumo.addParameters(params);
//		   sumo.addConnections("localhost", conf.getLocalPort());
//		} catch (Exception e) 
//		{
//		   e.printStackTrace();
//		}

		// TraSMAPI initialization.
		api.addSimulator(sumo);
		api.launch();
		api.connect();
		api.start();
		
		while(true)
		{
		   int currentStep = SumoCom.getCurrentSimStep();
		   if(!api.simulationStep(0))
		      break;
		}
		api.close();
	}

}
