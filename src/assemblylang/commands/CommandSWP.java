package assemblylang.commands;

import java.util.Map;

import assemblylang.Engine;
import assemblylang.ICommand;

public class CommandSWP implements ICommand {

	@Override
	public int runCommand(int[] input, Engine engine, Map<String, ?> customValueMap, int argCount) {
		int savenum = engine.getReg(input[1]);
		engine.setReg(input[1], engine.getReg(input[0]));
		engine.setReg(input[0], savenum);
		return 0;
	}

	@Override
	public boolean isRunnable(int[] input, Engine engine, Map<String, ?> customValueMap, int argCount) {
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
		return new int[]{0,1};
	}

}
