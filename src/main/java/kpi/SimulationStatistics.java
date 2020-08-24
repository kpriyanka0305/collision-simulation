package kpi;

import java.io.FileOutputStream;
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
	// TODO: these are also in SimulationParameters, no need to duplicate here
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
		boolean hardBrakingHappened = kpi.anyHardBrakings(busID, simParams.getNearCollisionDistance())
				.isPresent();
		Optional<Double> minimumDistance = kpi.getMinimumDistance(busID);
		runs.add(new SingleRunStatistics(currentBikeMaxSpeed, currentBusMaxSpeed, currentReactionTime,
				busWaitingTime * simParams.getStepLength(), hardBrakingHappened, randomVars.defectiveITS,
				minimumDistance));
		return;
	}

	public void writeStatistics(Date timestamp) {
		writeSimulationParameters(timestamp);
		writeSpeedsHistogramGraph(timestamp);
		writeStatisticsTable(timestamp);
	}

	private void writeSimulationParameters(Date timestamp) {
		try {
			String fileName = Util.mkFileName(simParams, timestamp, simParams.getParametersBase());
			simParams.store(fileName);
		} catch (IOException e) {
			System.err.println("could not write parameters file");
			e.printStackTrace();
		}
	}

	public void writeSpeedsHistogramGraph(Date timestamp) {
		FileWriter waitingTimeFile;
		try {
			waitingTimeFile = new FileWriter(Util.mkFileName(simParams, timestamp, simParams.getWaitingTimeBase()),
					true);
			waitingTimeFile.append(
					busWaitingTimes.prettyPrint(waitTimeSteps -> waitTimeSteps * simParams.getStepLength()));
			waitingTimeFile.close();
		} catch (IOException e) {
			System.err.println("could not write speeds histogram file");
			e.printStackTrace();
		}
	}

	public void writeStatisticsTable(Date timestamp) {
		try {
			Writer writer;
			runs.sort((s1, s2) -> Double.compare(s1.getBusWaitingTime(), s2.getBusWaitingTime()));
			writer = new FileWriter(Util.mkFileName(simParams, timestamp, simParams.getWaitingTimeTableBase(), ".csv"));
			StatefulBeanToCsv<SingleRunStatistics> beanToCsv = new StatefulBeanToCsvBuilder<SingleRunStatistics>(writer)
					.build();
			beanToCsv.write(runs);
			writer.close();
		} catch (IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
			System.err.println("could not write statistics table");
			e.printStackTrace();
		}
	}

}
