package util;

import java.util.HashMap;
import java.util.Map;

public class IntegerHistogram {
	private Map<Long, Integer> histogram;

	private static Integer increment(Long key, Integer oldValue) {
		if (oldValue != null) {
			return oldValue + 1;
		} else {
			return 1;
		}
	}
	
	public IntegerHistogram() {
		this.histogram = new HashMap<>();
	}

	public void add(Long value) {
		histogram.compute(value, IntegerHistogram::increment);
	}

	public String prettyPrint() {
		StringBuilder result = new StringBuilder();
		histogram.forEach((key, value) -> result.append(key).append(" ").append(value).append("\n"));
		return result.toString();
	}
}
