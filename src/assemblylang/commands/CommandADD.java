package assemblylang.commands;

import java.util.Arrays;

import assemblylang.Engine;
import assemblylang.ICommand;

public class CommandADD implements ICommand {

	@Override
	public int runCommand(int[]input, Engine engine, int argCount) {
		return Arrays.stream(input).sum();
		
	}

	@Override
	public int[] getArgCounts() {
		return null;
	}

	@Override
	public boolean isRunnable(int[]input, Engine engine, int argCount) {
		return true;
	}

	@Override
	public String getReturnRegName() {
		return "OP";
	}
	
	@Override
	public int getMinArgCount() {
		return 2;
	}

}
