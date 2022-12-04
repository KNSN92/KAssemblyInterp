package assemblylang.commands;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.BooleanUtils;

import assemblylang.Engine;
import assemblylang.EnumVarType;
import assemblylang.ICommand;
import assemblylang.IVarType;

public class CommandAND implements ICommand {
	
	private boolean not;
	
	public CommandAND(boolean not) {
		this.not = not;
	}

	@Override
	public Object runCommand(Object[] input, Engine engine, IVarType[] argTypes, int argCount) {
		Boolean[] boolArray = new Boolean[0];
		for(Object obj:input) {
			boolArray = ArrayUtils.add(boolArray, (Boolean) obj);
		}
		Boolean result = BooleanUtils.and(boolArray);
		return not ? !result : result;
	}

	@Override
	public boolean isRunnable(Object[] input, Engine engine, int argCount) {
		return true;
	}

	@Override
	public int[] getArgCounts() {
		return null;
	}
	
	@Override
	public int getMinArgCount() {
		return 2;
	}

	@Override
	public IVarType[] getArgVarTypes(IVarType[] argTypes, Engine engine, int argCount) {
		return new IVarType[] {EnumVarType.Boolean};
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
