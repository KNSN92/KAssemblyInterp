package assemblylang.commands;

import org.apache.commons.lang3.ArrayUtils;

import assemblylang.Engine;
import assemblylang.EnumVarType;
import assemblylang.ICommand;
import assemblylang.IVarType;

public class CommandABS implements ICommand {

	@Override
	public Object runCommand(Object[] input, Engine engine, IVarType[] argTypes, int argCount) {
		Object result;
		switch((EnumVarType) argTypes[0]) {
		case Int:
			result = Math.abs((Long)input[0]);
			break;
		case Float:
			result = Math.abs((Double)input[0]);
			break;
		default:
			result = null;
			break;
		}
		return result;
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
		return Engine.DEFAULT_RETURN_REG_NAME;
	}

	@Override
	public IVarType[] getArgVarTypes(IVarType[] argTypes, Engine engine, int argCount) {
		return new IVarType[]{EnumVarType.Int};
	}
	
	@Override
	public IVarType getReturnVarType(IVarType[] argTypes, IVarType resultType, Engine engine, int argCount) {
		return ArrayUtils.contains(argTypes, EnumVarType.Float) ? EnumVarType.Float : EnumVarType.Int;
	}

}
