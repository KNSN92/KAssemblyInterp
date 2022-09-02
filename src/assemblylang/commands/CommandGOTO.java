package assemblylang.commands;

import java.util.Map;

import assemblylang.Engine;
import assemblylang.ICommand;
import assemblylang.util.CmdStrUtil;

public class CommandGOTO implements ICommand {

	String label = "";

	@Override
	public int runCommand(int[] input, Engine engine, int argCount) {
		Map<String, Integer> labelPos = ((CommandLABEL) engine.getCommand("LABEL")).labelPos;
		if (labelPos.containsKey(label)) {
			engine.Goto(labelPos.get(label));
		}else {
			engine.throwError("Label not fount.");
		}
		return 0;
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
		return null;
	}

	@Override
	public String[] getInitResult(String[] args, Engine engine, int argCount, boolean isInit) {

		label = args[0];
		args = CmdStrUtil.replaceZero(args,0);
		return args;
	}

}
