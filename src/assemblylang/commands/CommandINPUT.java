package assemblylang.commands;

import java.util.InputMismatchException;
import java.util.Scanner;

import assemblylang.Engine;
import assemblylang.EnumVarType;
import assemblylang.ICommand;
import assemblylang.IVarType;
import assemblylang.util.CmdStrUtil;
import assemblylang.util.VariableTypeUtils;

public class CommandINPUT implements ICommand {
	
	private EnumVarType returnType = EnumVarType.None;
	
	private Scanner scanner = new Scanner(System.in);


	@Override
	public Object runCommand(Object[] input, Engine engine, IVarType[] argTypes, int argCount) {
		if(input.length == 2) System.out.println(input[1]);
		Object result = null;
		
		try{
			switch(returnType){
			case Int:
				result = scanner.nextLong();
				break;
			case Float:
				result = scanner.nextDouble();
				break;
			case Boolean:
				result = scanner.nextBoolean();
				break;
			case String:
				result = scanner.nextLine();
				break;
			default:
				break;
			}
		}catch(InputMismatchException e){
			engine.throwError("Format does not match "+returnType.toString()+".");
			result = returnType.defaultVal();
		}
		
		return result;
	}

	@Override
	public boolean isRunnable(Object[] input, Engine engine, int argCount) {
		return true;
	}

	@Override
	public int[] getArgCounts() {
		return new int[]{1,2};
	}

	@Override
	public IVarType[] getArgVarTypes(IVarType[] argTypes, Engine engine, int argCount) {
		return argTypes.length == 1 ? new EnumVarType[] {EnumVarType.Int} : new EnumVarType[] {EnumVarType.Int,EnumVarType.String};
	}

	@Override
	public IVarType getReturnVarType(IVarType[] argTypes, IVarType resultType, Engine engine, int argCount) {
		return returnType;
	}

	@Override
	public String getReturnRegName() {
		return Engine.DEFAULT_RETURN_REG_NAME;
	}

	@Override
	public String[] getInitResult(String[] args, Engine engine, int argCount, boolean isInit) {
		this.returnType = VariableTypeUtils.stringToEnumVarType(args[0]);
		if(returnType == null) {
			engine.throwError("The type "+args[0]+" was not found.");
		}
		if(returnType == EnumVarType.Void) {
			engine.throwError("Type void is an invalid type.");
		}
		args = CmdStrUtil.replaceZero(args, 0);
		return args;
	}
	

}
