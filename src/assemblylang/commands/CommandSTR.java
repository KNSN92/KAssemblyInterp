package assemblylang.commands;

import assemblylang.Engine;
import assemblylang.EnumVarType;
import assemblylang.ICommand;
import assemblylang.IVarType;

public class CommandSTR implements ICommand {

	@Override
	public Object runCommand(Object[] input, Engine engine, IVarType[] argTypes, int argCount) {
		return input[0].toString();
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
	public IVarType[] getArgVarTypes(IVarType[] argTypes, Engine engine, int argCount) {
		return new IVarType[] {EnumVarType.None};
	}

	@Override
	public IVarType getReturnVarType(IVarType[] argTypes, IVarType resultType, Engine engine, int argCount) {
		return EnumVarType.String;
	}

	@Override
	public String getReturnRegName() {
		return Engine.DEFAULT_RETURN_REG_NAME;
	}

}
