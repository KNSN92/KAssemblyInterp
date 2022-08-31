package assemblylang.commands;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.math.NumberUtils;

import assemblylang.Engine;
import assemblylang.ICommand;
import assemblylang.util.CmdStrUtil;

public class CommandVAR implements ICommand{
	
	public static final String[] CONST_WORDS = {"CONST","FINAL"};
	
	String VarName = "";
	int useNumIndex = -1;
	boolean toDecimal = false;
	boolean isConst = false;

	@Override
	public int runCommand(int[] input, Engine engine, int argCount) {
		if (engine.hasRegName(VarName)) {
			engine.throwError("The variable name already exists.\n"+"VarName:"+VarName);
		}else {
			engine.addReg(VarName);
			if(useNumIndex != -1)engine.setReg(VarName, input[useNumIndex]);
			if(isConst)engine.setRegChange(VarName, false);
		}
		System.out.println(engine.getRegs().toString());
		return 0;
	}

	@Override
	public boolean isRunnable(int[] input, Engine engine, int argCount) {
		return true;
	}

	@Override
	public int[] getArgCounts() {
		return new int[]{1,2,3};
	}

	@Override
	public String getReturnRegName() {
		return null;
	}

	@Override
	public int[] getNoConversionLocations() {
		return null;
	}
	
	@Override
	public String[] getInitResult(String[] args, Engine engine, int argCount) {
		int index = 0;
		if(ArrayUtils.contains(CONST_WORDS, args[index])) {
			isConst=true;
			args = CmdStrUtil.replaceZero(args, index);
			index++;
		}
		if(args[index].matches("^[A-Za-z]\\w+$") & !ArrayUtils.contains(engine.NGWordList, args[index])) {
			VarName = args[index];
			args = CmdStrUtil.replaceZero(args, index);
			index++;
			if(NumberUtils.isParsable(args[index])) {
				useNumIndex = index;
				index++;
			}
		}
		
		return args;
	}
}
