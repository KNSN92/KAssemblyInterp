package assemblylang.commands;

import java.util.function.BiPredicate;

import assemblylang.Engine;
import assemblylang.EnumVarType;
import assemblylang.ICommand;
import assemblylang.IVarType;

public class CommandCompare implements ICommand {
	
	private BiPredicate<Double, Double> compare;
	private boolean not;
	
	public CommandCompare(BiPredicate<Double,Double> compare) {
		this(compare, false);
	}
	
	public CommandCompare(BiPredicate<Double, Double> compare, boolean not) {
		this.compare = compare;
		this.not = not;
	}

	@Override
	public Object runCommand(Object[] input, Engine engine, IVarType[] argTypes, int argCount) {
		
		double r = 0;
		double l = 0;
		
		if(argTypes[0] == EnumVarType.Int) {
			l = (double)(long)(Long)input[0];
		}else if(argTypes[0] == EnumVarType.Float) {
			l = (double)input[0];
		}
		
		if(argTypes[1] == EnumVarType.Int) {
			r = (double)(long)(Long)input[1];
		}else if(argTypes[1] == EnumVarType.Float) {
			r = (double)input[1];
		}
		
		boolean res = compare.test(l,r);
		return not?!res:res;
	}

	@Override
	public boolean isRunnable(Object[] input, Engine engine, int argCount) {
		return true;
	}

	@Override
	public int[] getArgCounts() {
		return new int[]{2};
	}

	@Override
	public IVarType[] getArgVarTypes(IVarType[] argTypes, Engine engine, int argCount) {
		IVarType[] ret = new EnumVarType[2];
		ret[0] = argTypes[0].isNumber() ? argTypes[0] : EnumVarType.Int;
		ret[1] = argTypes[1].isNumber() ? argTypes[1] : EnumVarType.Int;
		return ret;
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
