package assemblylang.commands;

import java.io.File;
import java.nio.file.Paths;

import assemblylang.Engine;
import assemblylang.EnumVarType;
import assemblylang.ICommand;
import assemblylang.IVarType;

public class CommandIMPORT implements ICommand {

	@Override
	public Object runCommand(Object[] input, Engine engine, IVarType[] argTypes, int argCount) {
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
	public IVarType[] getArgVarTypes(IVarType[] argTypes, Engine engine, int argCount) {
		return new IVarType[] {EnumVarType.String};
	}

	@Override
	public IVarType getReturnVarType(IVarType[] argTypes, IVarType resultType, Engine engine, int argCount) {
		return EnumVarType.Void;
	}

	@Override
	public String getReturnRegName() {
		return null;
	}

	@Override
	public void initRun(Object[] input, Engine engine, IVarType[] argTypes, int argCount) {
		File file = Paths.get((String)input[0]).toFile();
		if(file.isAbsolute()) {
			engine.loadFunctionFromOtherAbsolutePathFile(file);
		}else {
			engine.loadFunctionFromOtherFile(file);
		}
	}
	
	

}
