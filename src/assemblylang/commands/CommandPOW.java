package assemblylang.commands;

import assemblylang.Engine;
import assemblylang.ICommand;

public class CommandPOW implements ICommand {

	@Override
	public int runCommand(int[] input, Engine engine, int argCount) {
		return (int) Math.pow(input[0], input[1]);
	}

	@Override
	public boolean isRunnable(int[] input, Engine engine, int argCount) {
		return true;
	}

	@Override
	public int[] getArgCounts() {
		return new int[] {2};
	}

	@Override
	public String getReturnRegName() {
		return Engine.defaultReturnRegName;
	}

}
