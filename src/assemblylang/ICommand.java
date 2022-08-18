package assemblylang;

import java.util.Map;

public interface ICommand {
	
	public int runCommand(int[]input, Engine engine, Map<String,?> customValueMap, int argCount);
	
	public boolean isRunnable(int[]input, Engine engine, Map<String,?> customValueMap, int argCount);
	
	public int[] getArgCounts();
	
	public String getReturnRegName();
	
	default public int[] getNoConversionLocations() {
		return null;
	}
	
	default public int getMinArgCount() {
		return 0;
	}
	
	default public String[] getInitResult(String[] args, Engine engine, Map<String,?> customValueMap, int argCount) {
		return args;
	}
}
