package assemblylang;

public interface ICommand {
	
	public int runCommand(int[]input, Engine engine, int argCount);
	
	public boolean isRunnable(int[]input, Engine engine, int argCount);
	
	public int[] getArgCounts();
	
	public String getReturnRegName();
	
	default public int[] getNoConversionLocations() {
		return null;
	}
	
	default public int getMinArgCount() {
		return 0;
	}
	
	default public String[] getInitResult(String[] args, Engine engine, int argCount, boolean isInit) {
		return args;
	}
	
	default public void init(Engine engine) {}
	
	default public void initRun(int[]input, Engine engine, int argCount) {}
	
	default public void registered(Engine engine) {};
}
