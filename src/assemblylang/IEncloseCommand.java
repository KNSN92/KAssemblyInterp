package assemblylang;

import java.util.Map;

public interface IEncloseCommand extends ICommand {
	public String getEndEncloseCommand();
	public int runEndEncloseCommand(int[]input, Engine engine, Map<String,?> customValueMap);
	
	public boolean isRunnableForEndCommand(int[]input, Engine engine, Map<String,?> customValueMap);
	
	public int[] getArgCountsForEndCommand();
	
	public String getReturnRegNameForEndCommand();
	
	public int[] getNoConversionLocationsForEndCommand();
	
	default public int getMinArgCountForEndCommand() {
		return 1;
	}
	
	default public String[] getInitResultForEndCommand(String[] args, Engine engine) {
		return args;
	}
}
