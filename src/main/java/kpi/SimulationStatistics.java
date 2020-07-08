package kpi;

import java.util.ArrayList;
import java.util.List;

import util.*;

public class SimulationStatistics {
	private IntegerHistogram busWaitingTimes = new IntegerHistogram();
	private List<SingleRunStatistics> runs = new ArrayList<>();
	private double currentBikeMaxSpeed = 0;
	private double currentBusMaxSpeed = 0;

	public void setCurrentBikeMaxSpeed(double currentBikeMaxSpeed) {
		this.currentBikeMaxSpeed = currentBikeMaxSpeed;
	}

	public void setCurrentBusMaxSpeed(double currentBusMaxSpeed) {
		this.currentBusMaxSpeed = currentBusMaxSpeed;
	}

	public void busArrived(Kpi kpi, String vehicleID) {
		int busWaitingTime = kpi.getWaitingTime(vehicleID);
		busWaitingTimes.add(busWaitingTime);
		runs.add(new SingleRunStatistics(currentBikeMaxSpeed, currentBusMaxSpeed, busWaitingTime));
		return;
	}
	
	public void writeStatisticsTable() {
		
	}

}
