package assemblylang.commands;

import org.apache.commons.lang3.ArrayUtils;

import assemblylang.Engine;
import assemblylang.ICommand;

public class CommandEXPORT implements ICommand {
	
	public int ExportInfos[] = new int[0];

	@Override
	public int runCommand(int[] input, Engine engine, int argCount) {
		ExportInfos = ArrayUtils.addAll(ExportInfos, input);
		return 0;
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
		return null;
	}
	
	@Override
	public void init() {
		ExportInfos = new int[0];
	}
}
