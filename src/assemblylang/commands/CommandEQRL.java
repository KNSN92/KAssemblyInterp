package assemblylang.commands;

import java.util.Map;

import assemblylang.Engine;
import assemblylang.ICommand;
import assemblylang.util.CmdStrUtil;

public class CommandEQRL implements ICommand {

	private String label = "";
	
	private boolean not = false;
	
	public CommandEQRL(boolean not) {
		this.not = not;
	}

	@Override
	public int runCommand(int[] input, Engine engine, int argCount) {
		if(not) {
			if(input[1]!=input[2]) {
				Map<String, Integer> labelPos = ((CommandLABEL) engine.getCommand("LABEL")).labelPos;
				if (labelPos.containsKey(label)) {
					engine.Goto(labelPos.get(label));
				}else {
					engine.throwError("Label not found.");
				}
			}
		}else {
			if(input[1]==input[2]) {
				Map<String, Integer> labelPos = ((CommandLABEL) engine.getCommand("LABEL")).labelPos;
				if (labelPos.containsKey(label)) {
					engine.Goto(labelPos.get(label));
				}else {
					engine.throwError("Label not found.");
				}
			}
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
		return Engine.defaultReturnRegName;
	}
	
	@Override
	public String[] getInitResult(String[] args, Engine engine, int argCount, boolean isInit) {

		label  = args[0];
		args = CmdStrUtil.replaceZero(args,0);
		return args;
	}

}
