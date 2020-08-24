package main;

import java.util.Random;

// These values change for every run
public class RandomVariables {
	public final double busMaxSpeed;
	public final double bikeMaxSpeed;
	public final boolean defectiveITS;
	public final double reactionTime;

	public RandomVariables(SimulationProperties params) {
		Random r = params.getRandom();
		this.busMaxSpeed = makePositiveRandomDouble(r, params.getBusMaxSpeedMean(), params.getBusMaxSpeedSigma());
		this.bikeMaxSpeed = makePositiveRandomDouble(r, params.getBikeMaxSpeedMean(), params.getBikeMaxSpeedSigma());
		this.reactionTime = makePositiveRandomDouble(r, params.getReactionTimeMean(), params.getReactionTimeSigma());
		this.defectiveITS = makeRandomBoolean(r, params.getDefectiveItsProbability());
	}

	// returns a non-null positive number of the normal distribution
	private static double makePositiveRandomDouble(Random r, double mean, double sigma) {
		double result = -1;
		do {
			result = (r.nextGaussian() * sigma) + mean;
		} while (result < 0.0000001);
		return result;
	}

	// returns a boolean that is true with probability p
	private static boolean makeRandomBoolean(Random r, double p) throws IllegalArgumentException {
		if (p < 0 || p > 1) {
			throw new IllegalArgumentException("p must be between 0 and 1");
		}
		return r.nextDouble() < p;
	}
}
