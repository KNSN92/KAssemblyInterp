package assemblylang.commands;

import org.apache.commons.lang3.ArrayUtils;

import assemblylang.Engine;
import assemblylang.EnumVarType;
import assemblylang.ICommand;
import assemblylang.IVarType;
import assemblylang.util.CmdStrUtil;
import assemblylang.util.VariableTypeUtils;

public class CommandVAR implements ICommand{
	
	public static final String[] CONST_WORDS = {"const","final"};
	String VarName = "";
	EnumVarType Type = EnumVarType.None;
	int useNumIndex = -1;
	boolean toDecimal = false;
	boolean isConst = false;
	@Override
	public Object runCommand(Object[] input, Engine engine, IVarType[] argTypes, int argCount) {
		if (engine.hasRegName(VarName)) {
			engine.throwError("The variable name already exists.\n"+"VarName:"+VarName);
		}else {
			engine.addReg(VarName, Type);
			if(useNumIndex != -1)engine.setReg(VarName, input[useNumIndex]);
			if(isConst)engine.setRegChange(VarName, engine.getScope(), false);
		}
		return null;
	}

	@Override
	public boolean isRunnable(Object[] input, Engine engine, int argCount) {
		return true;
	}

	@Override
	public int[] getArgCounts() {
		return new int[]{2,3,4};
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
	public String[] getInitResult(String[] args, Engine engine, int argCount, boolean isInit) {
		int index = 0;
		if (ArrayUtils.contains(CONST_WORDS, args[index])) {
			isConst = true;
			args = CmdStrUtil.replaceZero(args, index);
			index++;
		} else {
			isConst = false;
		}
		
		if(VariableTypeUtils.varTypeSymbols.get(args[index]) != null) {
			Type = VariableTypeUtils.varTypeSymbols.get(args[index]);
			if(Type == EnumVarType.Void) {
				engine.throwError("Type void is an invalid type.");
				return args;
			}
			args = CmdStrUtil.replaceZero(args, index);
			index++;
		}else {
			engine.throwError("Variable type must be specified.");
			return args;
		}
		
		if (args[index].matches("^[A-Za-z]\\w*$") & !ArrayUtils.contains(engine.keyWordList, args[index])) {
			VarName = args[index];
			args = CmdStrUtil.replaceZero(args, index);
			index++;
		} else {
			engine.throwError("A variable name is required.");
			return args;
		}
		if (isConst ? argCount == 4 : argCount == 3) {
			EnumVarType type = VariableTypeUtils.ParseType(args[index]);
			if (type != EnumVarType.None & Type == type) {
				useNumIndex = index;
				index++;
			} else {
				engine.throwError("An initial value is different.\nCorrect type:"+Type.toString()+"\nThis time type:"+type.toString());
				return args;
			}
		} else {
			useNumIndex = -1;
		} 
		return args;
	}

	@Override
	public void initRun(Object[] input, Engine engine, int argCount) {
		if (engine.hasRegName(VarName)) {
			engine.throwError("The variable name already exists.\n"+"VarName:"+VarName);
		}else {
			engine.addReg(VarName, Type);
			if(useNumIndex != -1)engine.setReg(VarName, input[useNumIndex]);
			if(isConst)engine.setRegChange(VarName, engine.getScope(), false);
		}
	}

	@Override
	public IVarType[] getArgVarTypes(IVarType[] argTypes, Engine engine, int argCount) {
		return new IVarType[]{EnumVarType.None,EnumVarType.None,EnumVarType.None};
	}
	
	@Override
	public IVarType getReturnVarType(IVarType[] argTypes, IVarType resultType, Engine engine, int argCount) {
		return EnumVarType.Void;
	}
	

}
