package assemblylang.commands;

import java.util.Arrays;
import java.util.Map;

import assemblylang.Engine;
import assemblylang.ICommand;

public class CommandADD implements ICommand {

	@Override
	public int runCommand(int[]input, Engine engine, Map<String, ?> customValueMap) {
		return Arrays.stream(input).sum();
		
	}

	@Override
	public int[] getArgCounts() {
		return null;
	}

	@Override
	public boolean isRunnable(int[]input, Engine engine, Map<String, ?> customValueMap) {
		return true;
	}

	@Override
	public String getReturnRegName() {
		return "OP";
	}

	@Override
	public int[] getNoConversionLocations() {
		return null;
	}
	
	@Override
	public int getMinArgCount() {
		return 2;
	}

}
