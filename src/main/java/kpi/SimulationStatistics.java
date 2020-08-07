package kpi;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

import main.SimulationParameters;
import util.IntegerHistogram;
import util.Util;

public class SimulationStatistics {
	private IntegerHistogram busWaitingTimes = new IntegerHistogram();
	private List<SingleRunStatistics> runs = new ArrayList<>();
	// TODO: these are also in SimulationParameters, no need to duplicate here
	private double currentBikeMaxSpeed = 0;
	private double currentBusMaxSpeed = 0;
	private SimulationParameters currentSimParameters = null;

	public void setCurrentSimParameters(SimulationParameters currentSimParameters) {
		this.currentSimParameters = currentSimParameters;
	}

	public void setCurrentBikeMaxSpeed(double currentBikeMaxSpeed) {
		this.currentBikeMaxSpeed = currentBikeMaxSpeed;
	}

	public void setCurrentBusMaxSpeed(double currentBusMaxSpeed) {
		this.currentBusMaxSpeed = currentBusMaxSpeed;
	}

	public void busArrived(Kpi kpi, String busID) {
		long busWaitingTime = kpi.getWaitingTime(busID);
		busWaitingTimes.add(busWaitingTime);
		boolean hardBrakingHappened = kpi.anyHardBrakings(busID, SimulationParameters.NEAR_COLLISION_DISTANCE).isPresent();
		runs.add(new SingleRunStatistics(currentBikeMaxSpeed, currentBusMaxSpeed,
				busWaitingTime * SimulationParameters.STEP_LENGTH, hardBrakingHappened, currentSimParameters.defectiveITS));
		return;
	}

	public void writeStatistics(Date timestamp) {
		writeSpeedsHistogramGraph(timestamp);
		writeStatisticsTable(timestamp);
	}

	public void writeSpeedsHistogramGraph(Date timestamp) {
		FileWriter waitingTimeFile;
		try {
			waitingTimeFile = new FileWriter(Util.mkFileName(timestamp, SimulationParameters.WAITING_TIME_BASE), true);
			waitingTimeFile.append(
					busWaitingTimes.prettyPrint(waitTimeSteps -> waitTimeSteps * SimulationParameters.STEP_LENGTH));
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
			writer = new FileWriter(Util.mkFileName(timestamp, SimulationParameters.WAITING_TIME_TABLE_BASE, ".csv"));
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
