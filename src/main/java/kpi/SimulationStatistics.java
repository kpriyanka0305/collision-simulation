package kpi;

import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;

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
		boolean anyHardBrakings = kpi.anyHardBrakings(busID, SimulationParameters.NEAR_COLLISION_DISTANCE).isPresent();
		runs.add(new SingleRunStatistics(currentBikeMaxSpeed, currentBusMaxSpeed, busWaitingTime * SimulationParameters.STEP_LENGTH, anyHardBrakings, currentSimParameters.defectiveITS));
		return;
	}

	public void writeStatisticsTable(Date timestamp) {
		Writer writer;
		try {
			runs.sort((s1, s2) -> Double.compare(s1.getBusWaitingTime(), s2.getBusWaitingTime()));
			writer = new FileWriter(Util.mkFileName(timestamp, Kpi.WAITING_TIME_TABLE_BASE, ".csv"));
			StatefulBeanToCsv<SingleRunStatistics> beanToCsv = new StatefulBeanToCsvBuilder<SingleRunStatistics>(writer).build();
			beanToCsv.write(runs);
			writer.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
