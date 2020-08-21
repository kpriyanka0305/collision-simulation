package kpi;

import java.util.Optional;

import com.opencsv.bean.CsvBindByName;

public class SingleRunStatistics {
	@CsvBindByName(column = "Bicycle Max Speed")
	private final double bikeMaxSpeed;
	@CsvBindByName(column = "Bus Max Speed")
	private final double busMaxSpeed;
	@CsvBindByName(column = "Reaction Time")
	private final double reactionTime;
	@CsvBindByName(column = "Wating Time")
	private final double busWaitingTime;
	@CsvBindByName(column = "Hard Braking")
	private final boolean hardBrakingHappened;
	@CsvBindByName(column = "ITS Defective")
	private final boolean defectiveITS;
	@CsvBindByName(column = "Minimum Distance")
	private final Optional<Double> minimumDistance;

	public SingleRunStatistics() {
		this.bikeMaxSpeed = 0;
		this.busMaxSpeed = 0;
		this.reactionTime = 0;
		this.busWaitingTime = 0;
		this.hardBrakingHappened = false;
		this.defectiveITS = false;
		this.minimumDistance = Optional.empty();
	}

	public SingleRunStatistics(double currentBikeMaxSpeed, double currentBusMaxSpeed, double reactionTime,
			double busWaitingTime, boolean hardBrakingHappened, boolean itsDefective, Optional<Double> minimumDistance) {
		this.bikeMaxSpeed = currentBikeMaxSpeed;
		this.busMaxSpeed = currentBusMaxSpeed;
		this.reactionTime = reactionTime;
		this.busWaitingTime = busWaitingTime;
		this.hardBrakingHappened = hardBrakingHappened;
		this.defectiveITS = itsDefective;
		this.minimumDistance = minimumDistance;
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

	public boolean isDefectiveITS() {
		return defectiveITS;
	}

	public Optional<Double> getMinimumDistance() {
		return minimumDistance;
	}
}
