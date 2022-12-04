package assemblylang.commands;

import assemblylang.Engine;
import assemblylang.EnumVarType;
import assemblylang.ICommand;
import assemblylang.IVarType;
import assemblylang.util.VariableTypeUtils;

public class CommandSET implements ICommand {
	
	private String saveRegName = "";

	@Override
	public Object runCommand(Object[] input, Engine engine, IVarType[] argTypes, int argCount) {
		int name = (int)((long)input[0]);
		Object val = input[1];
		if(!engine.hasRegName(saveRegName)) {
			engine.throwError("The variable "+saveRegName+" does not exist.");
			return null;
		}
		if(!engine.getReg(name).getClass().isInstance(input[1])) {
			engine.throwError("An argument type is different.\nCorrect type:"+VariableTypeUtils.toEnumVarType(engine.getReg(name)).toString()+"\nThis time type:"+VariableTypeUtils.toEnumVarType(val).toString());
			return null;
		}
		engine.setReg(name, val);
		return null;
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
	public String getReturnRegName() {
		return null;
	}

	@Override
	public int[] getNoConversionLocations() {
		return new int[]{0};
	}

	@Override
	public IVarType[] getArgVarTypes(IVarType[] argTypes, Engine engine, int argCount) {
		return new IVarType[]{EnumVarType.Int,argTypes[1]};
	}
	
	@Override
	public IVarType getReturnVarType(IVarType[] argTypes, IVarType resultType, Engine engine, int argCount) {
		return EnumVarType.Void;
	}
	
	@Override
	public String[] getInitResult(String[] args, Engine engine, int argCount, boolean isInit) {
		saveRegName = args[0];
		return args;
	}

}
