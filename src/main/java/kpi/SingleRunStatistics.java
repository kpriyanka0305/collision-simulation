package kpi;

import com.opencsv.bean.CsvBindByName;

public class SingleRunStatistics {
	@CsvBindByName
	private final double bikeMaxSpeed;
	@CsvBindByName
	private final double busMaxSpeed;
	@CsvBindByName
	private final double busWaitingTime;

	public SingleRunStatistics() {
		this.bikeMaxSpeed = 0;
		this.busMaxSpeed = 0;
		this.busWaitingTime = 0;
	}

	public SingleRunStatistics(double currentBikeMaxSpeed, double currentBusMaxSpeed, double busWaitingTime) {
		this.bikeMaxSpeed = currentBikeMaxSpeed;
		this.busMaxSpeed = currentBusMaxSpeed;
		this.busWaitingTime = busWaitingTime;
	}

	public double getBikeMaxSpeed() {
		return bikeMaxSpeed;
	}

	public double getBusMaxSpeed() {
		return busMaxSpeed;
	}

	public double getBusWaitingTime() {
		return busWaitingTime;
	}

}
