package assemblylang.commands;

import assemblylang.Engine;
import assemblylang.EnumVarType;
import assemblylang.ICommand;
import assemblylang.IVarType;

public class CommandFLOAT implements ICommand {

	@Override
	public Object runCommand(Object[] input, Engine engine, IVarType[] argTypes, int argCount) {
		try {
			return argTypes[0] == EnumVarType.String ? Double.parseDouble((String) input[0])
					: (argTypes[0] == EnumVarType.Float) ? (Double) input[0]
							: (Double)(double)(long)(Long) input[0];
		} catch (Exception e) {
			engine.throwError(e.getLocalizedMessage());
			return 0;
		}
	}

	@Override
	public boolean isRunnable(Object[] input, Engine engine, int argCount) {
		return true;
	}

	@Override
	public int[] getArgCounts() {
		return new int[] { 1 };
	}

	@Override
	public IVarType[] getArgVarTypes(IVarType[] argTypes, Engine engine, int argCount) {
		return new IVarType[] { ((argTypes[0] == EnumVarType.String) ? EnumVarType.String
				: (argTypes[0] == EnumVarType.Float) ? EnumVarType.Float : EnumVarType.Int) };
	}

	@Override
	public IVarType getReturnVarType(IVarType[] argTypes, IVarType resultType, Engine engine, int argCount) {
		return EnumVarType.Float;
	}

	@Override
	public String getReturnRegName() {
		return Engine.DEFAULT_RETURN_REG_NAME;
	}

}
