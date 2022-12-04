 package assemblylang.commands;

import assemblylang.Engine;
import assemblylang.EnumVarType;
import assemblylang.ICommand;
import assemblylang.IVarType;

public class CommandDSP implements ICommand {

	@Override
	public Object runCommand(Object[]input, Engine engine, IVarType[] argTypes, int argCount) {
		if(argCount == 0) {
			System.out.println();
			return null;
		}
		String printStr = "";
		for(Object obj:input) {
			printStr += obj.toString();
		}
		System.out.println(printStr);
		return null;
	}

	@Override
	public int[] getArgCounts() {
		return null;
	}

	@Override
	public boolean isRunnable(Object[]input, Engine engine, int argCount) {
		return true;
	}

	@Override
	public String getReturnRegName() {
		return null;
	}

	@Override
	public IVarType[] getArgVarTypes(IVarType[] argTypes, Engine engine, int argCount) {
		return argTypes.length <= 0 ? null : new EnumVarType[]{EnumVarType.None};
	}

	@Override
	public IVarType getReturnVarType(IVarType[] argTypes, IVarType resultType, Engine engine, int argCount) {
		return EnumVarType.Void;
	}
	
	/*@Override
	public String[] getInitResult(String[] args, Engine engine, int argCount, boolean isInit) {
		String[] initResult = new String[args.length];
		if(initResult.length <= 0) initResult = new String[] {"\"\""};
		for(int i = 0; i < args.length; i++) {
			initResult[i] = VariableTypeUtils.ParseType(args[i])==EnumVarType.None ? args[i] : StringQuotationUtils.isStringFormat(args[i]) ? args[i] : StringQuotationUtils.toStringQuotationFormat(args[i]);
		}
		return initResult;
	}*/

}
