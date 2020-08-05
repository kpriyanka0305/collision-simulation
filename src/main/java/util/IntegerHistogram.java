package util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

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

	public String prettyPrint(Function<Long, ? extends Object> preprocessKey) {
		StringBuilder result = new StringBuilder();
		// This padding with zero values is needed for gnuplot to draw nice box
		// diagrams.
		long minimum = Collections.min(histogram.keySet());
		long maximum = Collections.max(histogram.keySet());
		for (long i = minimum; i <= maximum; ++i) {
			int value = histogram.getOrDefault(i, 0);
			result.append(preprocessKey.apply(i)).append(" ").append(value).append("\n");
		}
		return result.toString();
	}
}
