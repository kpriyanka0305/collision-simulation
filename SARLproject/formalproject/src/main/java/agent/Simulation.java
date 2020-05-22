package agent;

public abstract class Simulation {
	protected long numSteps = 0;
	abstract public boolean step() throws Exception;
	abstract public void RSUStatus(String name, boolean status);
}