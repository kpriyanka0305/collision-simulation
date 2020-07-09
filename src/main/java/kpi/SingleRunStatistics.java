package kpi;

import com.opencsv.bean.CsvBindByName;

public class SingleRunStatistics {
	@CsvBindByName(column = "Bicycle Max Speed")
	private final double bikeMaxSpeed;
	@CsvBindByName(column = "Bus Max Speed")
	private final double busMaxSpeed;
	@CsvBindByName(column = "Wating Time")
	private final double busWaitingTime;
	@CsvBindByName(column = "Hard Braking")
	private final boolean hardBrakingHappened;

	public SingleRunStatistics() {
		this.bikeMaxSpeed = 0;
		this.busMaxSpeed = 0;
		this.busWaitingTime = 0;
		this.hardBrakingHappened = false;
	}

	public SingleRunStatistics(double currentBikeMaxSpeed, double currentBusMaxSpeed, double busWaitingTime, boolean hardBrakingHappened) {
		this.bikeMaxSpeed = currentBikeMaxSpeed;
		this.busMaxSpeed = currentBusMaxSpeed;
		this.busWaitingTime = busWaitingTime;
		this.hardBrakingHappened = hardBrakingHappened;
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

	public boolean isHardBrakingHappened() {
		return hardBrakingHappened;
	}

}
