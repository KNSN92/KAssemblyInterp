package assemblylang.commands;

import java.util.Map;

import assemblylang.Engine;
import assemblylang.ICommand;

public class CommandMOV implements ICommand {
	
	@Override
	public int runCommand(int[] input, Engine engine, Map<String, ?> customValueMap) {
		engine.setReg(input[1], engine.getReg(input[0]));
		engine.setReg(input[0], 0);
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
		return "OP";
	}

	@Override
	public int[] getNoConversionLocations() {
		return new int[]{0,1};
	}

}
