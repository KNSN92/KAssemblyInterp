package assemblylang.commands;

import java.util.Map;

import assemblylang.Engine;
import assemblylang.ICommand;

public class CommandMOD implements ICommand {

	@Override
	public int runCommand(int[] input, Engine engine, Map<String, ?> customValueMap) {
		return input[0] % input[1];
	}

	@Override
	public boolean isRunnable(int[] input, Engine engine, Map<String, ?> customValueMap) {
		return true;
	}

	@Override
	public int[] getArgCounts() {
		return new int[] {2};
	}

	@Override
	public String getReturnRegName() {
		return "OP";
	}


	@Override
	public int[] getNoConversionLocations() {
		return null;
	}

}
