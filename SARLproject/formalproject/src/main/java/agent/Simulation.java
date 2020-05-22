package agent;

public interface Simulation {
	boolean step() throws Exception;
	void RSUStatus(String name, boolean status);
}