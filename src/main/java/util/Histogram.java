package util;

import java.util.HashMap;
import java.util.Map;

public class Histogram {
	private final double binSize;
	private Map<Integer, Integer> histogram;

	private static Integer increment(Integer key, Integer oldValue) {
		if (oldValue != null) {
			return oldValue + 1;
		} else {
			return 1;
		}
	}
	
	public Histogram() {
		this(1.0);
	}

	public Histogram(double binSize) {
		this.binSize = binSize;
		this.histogram = new HashMap<>();
	}

	public void add(double value) {
		Integer bin = (int) (Math.round(value / binSize));
		histogram.compute(bin, Histogram::increment);
	}

	public String prettyPrint() {
		long numEntries = histogram.values().stream().reduce(0, (x,y) -> x + y);
		double factor = numEntries * binSize;
		StringBuilder result = new StringBuilder();
		histogram.forEach((key, value) -> result.append(key * binSize).append(" ").append((double)(value) / factor).append("\n"));
		return result.toString();
	}
}
