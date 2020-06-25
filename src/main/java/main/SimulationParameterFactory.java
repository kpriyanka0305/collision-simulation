package main;

public class SimulationParameterFactory {
	public static SimulationParameters getSimulationParameters(UserInterfaceType uiType, double busMaxSpeed,
			double bikeMaxSpeed) {
		SimulationParameters params = new SimulationParameters(busMaxSpeed, bikeMaxSpeed);
		switch (uiType) {
		case Headless:
			params.setSumoBin(SimulationParameters.SUMO_CLI_BIN);
			params.STEP_DELAY = 0.0;
		}
		return params;
	}
}
