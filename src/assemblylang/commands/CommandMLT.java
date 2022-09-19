package assemblylang.commands;

import assemblylang.Engine;
import assemblylang.ICommand;

public class CommandMLT implements ICommand {

	@Override
	public int runCommand(int[] input, Engine engine, int argCount) {
		int result = 1;
		for(int in:input) {
			result *= in;
		}
		return result;
	}

	@Override
	public boolean isRunnable(int[] input, Engine engine, int argCount) {
		return true;
	}

	@Override
	public int[] getArgCounts() {
		return null;
	}

	@Override
	public String getReturnRegName() {
		return Engine.defaultReturnRegName;
	}
	
	@Override
	public int getMinArgCount() {
		return 2;
	}

}
