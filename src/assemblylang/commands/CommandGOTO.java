package assemblylang.commands;

import java.util.Map;

import assemblylang.Engine;
import assemblylang.ICommand;

public class CommandGOTO implements ICommand{

	@Override
	public int runCommand(int[] input, Engine engine, Map<String, ?> customValueMap) {
		engine.Goto(input[0]);
		return 0;
	}

	@Override
	public boolean isRunnable(int[] input, Engine engine, Map<String, ?> customValueMap) {
		return true;
	}

	@Override
	public int[] getArgCounts() {
		return new int[] {1};
	}

	@Override
	public String getReturnRegName() {
		return null;
	}

	@Override
	public int[] getNoConversionLocations() {
		return null;
	}

}
