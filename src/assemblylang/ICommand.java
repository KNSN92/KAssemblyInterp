package assemblylang;

import java.util.Map;

public interface ICommand {
	
	public int runCommand(int[]input, Engine engine, Map<String,?> customValueMap);
	
	public boolean isRunnable(int[]input, Engine engine, Map<String,?> customValueMap);
	
	public int[] getArgCounts();
	
	public String getReturnRegName();
	
	public int[] getNoConversionLocations();
	
	default public int getMinArgCount() {
		return 1;
	}
	
	default public String[] getInitResult(String[] args, Engine engine) {
		return args;
	}
}
