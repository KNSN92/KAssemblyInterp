package assemblylang.commands;

import assemblylang.Engine;
import assemblylang.ICommand;

public class CommandABS implements ICommand {

	@Override
	public int runCommand(int[] input, Engine engine, int argCount) {
		return Math.abs(input[0]);
	}

	@Override
	public boolean isRunnable(int[] input, Engine engine, int argCount) {
		return true;
	}

	@Override
	public int[] getArgCounts() {
		return new int[] {1};
	}

	@Override
	public String getReturnRegName() {
		return Engine.DEFAULT_RETURN_REG_NAME;
	}

}
