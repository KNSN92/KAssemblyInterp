package assemblylang.commands;

import java.util.Map;

import assemblylang.Engine;
import assemblylang.ICommand;

public class CommandSET implements ICommand {

	@Override
	public int runCommand(int[] input, Engine engine, Map<String, ?> customValueMap) {
		engine.setReg(input[0], input[1]);
		return 0;
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
		return null;
	}

	@Override
	public int[] getNoConversionLocations() {
		return new int[]{0};
	}

}
