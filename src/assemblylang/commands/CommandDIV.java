package assemblylang.commands;

import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;

import assemblylang.Engine;
import assemblylang.ICommand;

public class CommandDIV implements ICommand {

	@Override
	public int runCommand(int[] input, Engine engine, Map<String, ?> customValueMap, int argCount) {
		int result = input[0];
		int[] subinput = ArrayUtils.subarray(input, 1, input.length);
		if(ArrayUtils.contains(subinput, 0)) {
			engine.throwError("Zero division detected.");
			return -1;
		}
		for(int in:subinput) {
			result /= in;
		}
		return result;
	}

	@Override
	public boolean isRunnable(int[] input, Engine engine, Map<String, ?> customValueMap, int argCount) {
		return true;
	}

	@Override
	public int[] getArgCounts() {
		return null;
	}

	@Override
	public String getReturnRegName() {
		return "OP";
	}

	@Override
	public int[] getNoConversionLocations() {
		return null;
	}
	
	@Override
	public int getMinArgCount() {
		return 2;
	}

}
