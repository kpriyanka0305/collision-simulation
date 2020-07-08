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
		long busWaitingTime = kpi.getWaitingTime(vehicleID);
		busWaitingTimes.add(busWaitingTime);
		runs.add(new SingleRunStatistics(currentBikeMaxSpeed, currentBusMaxSpeed, busWaitingTime * SimulationParameters.STEP_LENGTH));
		return;
	}

	public void writeStatisticsTable(Date timestamp) {
		Writer writer;
		try {
			writer = new FileWriter("yourfile.csv");
			StatefulBeanToCsv<SingleRunStatistics> beanToCsv = new StatefulBeanToCsvBuilder<SingleRunStatistics>(writer).build();
			beanToCsv.write(runs);
			writer.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
