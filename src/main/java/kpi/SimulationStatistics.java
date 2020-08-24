package kpi;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

import main.RandomVariables;
import main.SimulationProperties;
import util.IntegerHistogram;
import util.Util;

public class SimulationStatistics {
	private IntegerHistogram busWaitingTimes = new IntegerHistogram();
	private List<SingleRunStatistics> runs = new ArrayList<>();
	// TODO: these are also in RandomVariables, no need to duplicate here
	private double currentBikeMaxSpeed = 0;
	private double currentBusMaxSpeed = 0;
	private double currentReactionTime = 0;
	private RandomVariables randomVars = null;
	private final SimulationProperties simParams;

	public SimulationStatistics(SimulationProperties simParams) {
		this.simParams = simParams;
	}

	public void setCurrentRandomVars(RandomVariables randomVars) {
		this.randomVars = randomVars;
	}

	public void setCurrentBikeMaxSpeed(double currentBikeMaxSpeed) {
		this.currentBikeMaxSpeed = currentBikeMaxSpeed;
	}

	public void setCurrentBusMaxSpeed(double currentBusMaxSpeed) {
		this.currentBusMaxSpeed = currentBusMaxSpeed;
	}

	public void setCurrentReactionTime(double currentReactionTime) {
		this.currentReactionTime = currentReactionTime;
	}

	public void busArrived(Kpi kpi, String busID) {
		long busWaitingTime = kpi.getWaitingTime(busID);
		busWaitingTimes.add(busWaitingTime);
		boolean hardBrakingHappened = kpi.anyHardBrakings(busID, simParams.getNearCollisionDistance()).isPresent();
		Optional<Double> minimumDistance = kpi.getMinimumDistance(busID);
		runs.add(new SingleRunStatistics(currentBikeMaxSpeed, currentBusMaxSpeed, currentReactionTime,
				busWaitingTime * simParams.getStepLength(), hardBrakingHappened, randomVars.defectiveITS,
				minimumDistance));
		return;
	}

	public void writeStatistics(Date timestamp) throws IOException {
		writeSpeedsHistogramGraph(timestamp);
		writeStatisticsTable(timestamp);
	}

	public void writeSpeedsHistogramGraph(Date timestamp) throws IOException {
		FileWriter waitingTimeFile;
		waitingTimeFile = new FileWriter(Util.mkFileName(simParams, timestamp, simParams.getWaitingTimeBase()), true);
		waitingTimeFile.append(busWaitingTimes.prettyPrint(waitTimeSteps -> waitTimeSteps * simParams.getStepLength()));
		waitingTimeFile.close();
	}

	public void writeStatisticsTable(Date timestamp) throws IOException {
		try {
			Writer writer;
			runs.sort((s1, s2) -> Double.compare(s1.getBusWaitingTime(), s2.getBusWaitingTime()));
			writer = new FileWriter(Util.mkFileName(simParams, timestamp, simParams.getWaitingTimeTableBase(), ".csv"));
			StatefulBeanToCsv<SingleRunStatistics> beanToCsv = new StatefulBeanToCsvBuilder<SingleRunStatistics>(writer)
					.build();
			beanToCsv.write(runs);
			writer.close();
		} catch (CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
			throw new UnsupportedOperationException(e.getMessage());
		}
	}

}
