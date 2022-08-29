package assemblylang.commands;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import assemblylang.Engine;
import assemblylang.ICommand;

public class CommandDSP implements ICommand {

	@Override
	public int runCommand(int[]input, Engine engine, int argCount) {
		String printStr = StringUtils.join(ArrayUtils.toObject(input), " ");
		System.out.println(printStr);
		return 0;
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
		return null;
	}

}
