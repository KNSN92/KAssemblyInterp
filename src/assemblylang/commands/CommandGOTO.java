 package assemblylang.commands;

import java.util.Map;

import assemblylang.Engine;
import assemblylang.EnumVarType;
import assemblylang.ICommand;
import assemblylang.IVarType;
import assemblylang.util.CmdStrUtil;

public class CommandGOTO implements ICommand {

	private String label = "";

	@Override
	public Object runCommand(Object[] input, Engine engine, IVarType[] argTypes, int argCount) {
		Map<String, Integer> labelPos = ((CommandLABEL) engine.getCommand("LABEL")).labelPos;
		if (labelPos.containsKey(label)) {
			engine.Goto(labelPos.get(label));
		}else {
			engine.throwError("Label not found.");
		}
		return null;
	}

	@Override
	public boolean isRunnable(Object[] input, Engine engine, int argCount) {
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

	@Override
	public IVarType[] getArgVarTypes(IVarType[] argTypes, Engine engine, int argCount) {
		return new IVarType[]{EnumVarType.Int};
	}

	@Override
	public IVarType getReturnVarType(IVarType[] argTypes, IVarType resultType, Engine engine, int argCount) {
		return EnumVarType.Void;
	}

}
