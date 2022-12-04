package assemblylang.commands;

import java.util.Objects;

import assemblylang.Engine;
import assemblylang.EnumVarType;
import assemblylang.ICommand;
import assemblylang.IVarType;

public class CommandEQ implements ICommand {
	
	private boolean not;
	
	
	public CommandEQ(boolean not) {
		this.not = not;
	}

	@Override
	public Object runCommand(Object[] input, Engine engine, IVarType[] argTypes, int argCount) {
		boolean r = Objects.equals(input[0], input[1]);
		return not? !r : r;
	}

	@Override
	public boolean isRunnable(Object[] input, Engine engine, int argCount) {
		return true;
	}

	@Override
	public int[] getArgCounts() {
		return new int[] {2};
	}

	@Override
	public IVarType[] getArgVarTypes(IVarType[] argTypes, Engine engine, int argCount) {
		return new IVarType[] {argTypes[0], argTypes[0]};
	}

	@Override
	public IVarType getReturnVarType(IVarType[] argTypes, IVarType resultType, Engine engine, int argCount) {
		return EnumVarType.Boolean;
	}

	@Override
	public String getReturnRegName() {
		return Engine.DEFAULT_RETURN_REG_NAME;
	}

}
