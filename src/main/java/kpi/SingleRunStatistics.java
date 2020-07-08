package kpi;

public class SingleRunStatistics {
	private final double bikeMaxSpeed;
	private final double busMaxSpeed;
	private final int busWaitingTime;

	public SingleRunStatistics(double currentBikeMaxSpeed, double currentBusMaxSpeed, int busWaitingTime) {
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

	public int getBusWaitingTime() {
		return busWaitingTime;
	}

}
