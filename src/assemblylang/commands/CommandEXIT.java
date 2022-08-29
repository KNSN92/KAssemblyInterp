package assemblylang.commands;

import assemblylang.Engine;
import assemblylang.ICommand;

public class CommandEXIT implements ICommand {

	@Override
	public int runCommand(int[] input, Engine engine, int argCount) {
		engine.Exit();
		return 0;
	}

	@Override
	public boolean isRunnable(int[] input, Engine engine, int argCount) {
		return true;
	}

	@Override
	public int[] getArgCounts() {
		return new int[] {0};
	}

	@Override
	public String getReturnRegName() {
		return null;
	}

	@Override
	public int getMinArgCount() {
		return 0;
	}
	
	
}
