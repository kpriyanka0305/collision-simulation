package util;

public class Stopwatch {
	long startTime;
	long endTime;

	public Stopwatch() {
		startTime = System.nanoTime();
		endTime = 0;
	}

	public void start() {
		startTime = System.nanoTime();
	}

	public void stop() {
		endTime = System.nanoTime();
	}

	public void printTime(String message) {
		long duration = (endTime - startTime);
		System.out.println(message + " " + duration / 1000000 + " ms");
	}
}
