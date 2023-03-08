package assemblylang.commands;

import assemblylang.Engine;
import assemblylang.EnumVarType;
import assemblylang.ICommand;
import assemblylang.IVarType;
import assemblylang.util.VariableTypeUtils;

public class CommandRETURN implements ICommand {

	Object returnValue = null;

	CommandFUNCTION.CommandFUNCTIONOBJECT funcObj = null;

	@Override
	public Object runCommand(Object[] input, Engine engine, IVarType[] argTypes, int argCount) {
		
		if (funcObj.returnType != argTypes[0])
			engine.throwError("The type passed to the return command is different.\nCorrect type:" + funcObj.returnType.toString()
					+ "\nThis time type:" + VariableTypeUtils.toEnumVarType(input[0]).toString());
		returnValue = input[0];
		engine.Exit();
		return null;
	}

	@Override
	public void initRun(Object[] input, Engine engine, IVarType[] argTypes, int argCount) {
		
		if(!engine.isInMultilineCommand("function") && funcObj == null) {
			engine.throwError("The return command can only be executed within a function command.");
		}
	}

	@Override
	public boolean isRunnable(Object[] input, Engine engine, int argCount) {
		return true;
	}

	@Override
	public int[] getArgCounts() {
		return new int[] { 0, 1 };
	}

	@Override
	public IVarType[] getArgVarTypes(IVarType[] argTypes, Engine engine, int argCount) {
		if(funcObj.returnType != EnumVarType.Void && argCount <= 0) {
			engine.throwError("In a function command with a void return value, the return command cannot specify a return value.");
		}
		return new IVarType[] { EnumVarType.None };
	}

	@Override
	public IVarType getReturnVarType(IVarType[] argTypes, IVarType resultType, Engine engine, int argCount) {
		return EnumVarType.Void;
	}

	@Override
	public String getReturnRegName() {
		return null;
	}

}