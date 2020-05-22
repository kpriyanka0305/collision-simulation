package agent;

public interface Simulation {
	void step() throws Exception;
	void RSUStatus(String name, boolean status);
}