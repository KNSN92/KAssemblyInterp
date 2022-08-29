package assemblylang.commands;

import assemblylang.Engine;
import assemblylang.ICommand;

public class CommandEQRL implements ICommand {

	@Override
	public int runCommand(int[] input, Engine engine, int argCount) {
		if(input[1]==input[2]) {
			engine.Goto(input[0]);
		}
		return 0;
	}

	@Override
	public boolean isRunnable(int[] input, Engine engine, int argCount) {
		return true;
	}

	@Override
	public int[] getArgCounts() {
		return new int[] {3};
	}

	@Override
	public String getReturnRegName() {
		return "OP";
	}

}
