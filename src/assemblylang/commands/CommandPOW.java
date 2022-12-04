package assemblylang.commands;

import org.apache.commons.lang3.ArrayUtils;

import assemblylang.Engine;
import assemblylang.EnumVarType;
import assemblylang.ICommand;
import assemblylang.IVarType;

public class CommandPOW implements ICommand {

	@Override
	public Object runCommand(Object[] input, Engine engine, IVarType[] argTypes, int argCount) {
		double[] floatObj = new double[0];
		for(Object obj : input) {
			floatObj = ArrayUtils.add(floatObj,obj instanceof Double ? (Double)obj : (Double)(double)(long)(Long)obj);
		}
		double result = 0D;
		result = Math.pow(floatObj[0],floatObj[1]);
		
		if(ArrayUtils.contains(argTypes, EnumVarType.Float)) {
			return result;
		}else {
			return ((Long)(long)result);
		}
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
		return Engine.DEFAULT_RETURN_REG_NAME;
	}

	@Override
	public IVarType[] getArgVarTypes(IVarType[] argTypes, Engine engine, int argCount) {
		IVarType[] returnTypes = new EnumVarType[argCount];
		for(int i = 0;i<argCount;i++) {
			if(argTypes[i] == EnumVarType.Int || argTypes[i] == EnumVarType.Float) {
				returnTypes[i] = argTypes[i];
			}else {
				returnTypes[i] = EnumVarType.Int;
			}
		}
		return returnTypes;
	}

	@Override
	public IVarType getReturnVarType(IVarType[] argTypes, IVarType resultType, Engine engine, int argCount) {
		return ArrayUtils.contains(argTypes, EnumVarType.Float) ? EnumVarType.Float : EnumVarType.Int;
	}

}
