package assemblylang.commands;

import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import assemblylang.CommandMultiLine;
import assemblylang.Engine;
import assemblylang.EnumVarType;
import assemblylang.ICommand;
import assemblylang.IVarType;
import assemblylang.KAsmException;
import assemblylang.Variable;
import assemblylang.util.CmdStrUtil;
import assemblylang.util.VariableTypeUtils;

public class CommandFUNCTION extends CommandMultiLine {
	
	public Map<String, ICommand> funcCommands = Maps.newHashMap();

	private IVarType returnType = EnumVarType.None;
	private String funcName = "";

	private IVarType[] varTypes = new IVarType[0];
	private String[] varNames = new String[0];

	private String[] codes = new String[0];

	@Override
	public Object runCommand(Object[] input, Engine engine, IVarType[] argTypes, int argCount) {
		engine.setExecution(false, true);
		return null;
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
		return 0;
	}

	@Override
	public IVarType[] getArgVarTypes(IVarType[] argTypes, Engine engine, int argCount) {
		return new IVarType[] { EnumVarType.Int };
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
	public String[] getInitResult(String[] args, Engine engine, int argCount, boolean isInit) {
		int i = 0;
		if (VariableTypeUtils.varTypeSymbols.get(args[i]) != null) {
			returnType = VariableTypeUtils.varTypeSymbols.get(args[i]);
			args = CmdStrUtil.replaceZero(args, i);
			i++;
		} else {
			engine.throwError("Variable type must be specified.");
			return args;
		}
		if (args[i].matches("^[A-Za-z]\\w*$") & !ArrayUtils.contains(engine.keyWordList, args[i])) {
			funcName = args[i];
			args = CmdStrUtil.replaceZero(args, i);
			i++;
		} else {
			engine.throwError("A function name is required.");
			return args;
		}
		while (i < args.length) {
			if (VariableTypeUtils.varTypeSymbols.get(args[i]) != null) {
				varTypes = ArrayUtils.add(varTypes, VariableTypeUtils.varTypeSymbols.get(args[i]));
				if (varTypes[varTypes.length - 1] == EnumVarType.Void) {
					engine.throwError("Type void is an invalid type.");
					return args;
				}
				args = CmdStrUtil.replaceZero(args, i);
				i++;
			} else {
				engine.throwError("Variable type must be specified.");
				return args;
			}
			if (ArrayUtils.isArrayIndexValid(args, i)) {
				if (args[i].matches("^[A-Za-z]\\w*$") & !ArrayUtils.contains(engine.keyWordList, args[i])) {
					varNames = ArrayUtils.add(varNames, args[i]);
					args = CmdStrUtil.replaceZero(args, i);
					i++;
				} else {
					engine.throwError("A variable name is required.");
					return args;
				}
			} else {
				engine.throwError(
						"Insufficient number of arguments. One of the variable types or variable names may not exist.");
			}
		}
		return args;
	}

	@Override
	public void initRun(Object[] input, Engine engine, IVarType[] argTypes, int argCount) {
		engine.setExecution(false, true);
	}

	@Override
	public void contentExecutingIf(Engine engine, String code) {
		codes = ArrayUtils.add(codes, code);
	}

	@Override
	public Map<String, IEndCommand> getEndCommands() {
		return ImmutableMap.of("endfunc", new CommandENDFUNCTION(this));
	}

	@Override
	public void init(Engine engine, String[] codes) {
		if (engine.getSuper() == null) {
			Engine executer = Engine.createExecuter(engine, null);
			executer.addSkipCommand("function");
			executer.addSkipCommand("endfunc");
			executer.setSkipConditionReverse(true);
			try {
				executer.run(codes);
			} catch (KAsmException e) {
				return;
			}
		
			Map<String, ICommand> commands = ((CommandFUNCTION)executer.getCommand("function")).funcCommands;
			for (Map.Entry<String, ICommand> entry : commands.entrySet()) {
				engine.registerCommand(entry.getKey(), entry.getValue());
			}
		}
	}

	private class CommandENDFUNCTION implements IEndCommand {

		private CommandFUNCTION funccmd;

		public CommandENDFUNCTION(CommandFUNCTION funccmd) {
			this.funccmd = funccmd;
		}

		@Override
		public Object runCommand(Object[] input, Engine engine, IVarType[] argTypes, int argCount) {
			engine.setExecution(true,false);
			return null;
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
		public IVarType[] getArgVarTypes(IVarType[] argTypes, Engine engine, int argCount) {
			return null;
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
			if (engine.getSuper() != null) {
				if (engine.getCommands().containsKey(funccmd.funcName))
					engine.throwError("The function name already exists.\n" + "FuncName:" + funccmd.funcName);
				funccmd.codes = ArrayUtils.subarray(funccmd.codes, 0, funccmd.codes.length - 1);
				ICommand regCommand = CommandFUNCTIONOBJECT.create(funccmd.varNames, funccmd.varTypes.length <= 0 ? null : funccmd.varTypes,
						funccmd.varTypes.length <= 0 ? null : new int[] { funccmd.varTypes.length },
						funccmd.returnType, funccmd.codes);
				engine.registerCommand(funccmd.funcName,
						regCommand);
				funcCommands.put(funccmd.funcName, regCommand);
				funccmd.codes = new String[0];
				funccmd.funcName = "";
				funccmd.returnType = EnumVarType.None;
				funccmd.varTypes = new IVarType[0];
				funccmd.varNames = new String[0];
			}
		}

		@Override
		public void RunWhenNotExec(Engine engine) {
			engine.setExecution(true, false);
		}

	}

	public static class CommandFUNCTIONOBJECT implements ICommand {

		private String argNames[] = new String[0];
		private IVarType[] argTypes = new IVarType[0];
		private int[] argCounts = new int[0];
		IVarType returnType = EnumVarType.None;

		private String[] codes = new String[0];

		public static ICommand create(String[] argNames, IVarType[] argTypes, int[] argCounts, IVarType returnType,
				String[] codes) {
			return new CommandFUNCTIONOBJECT(argNames, argTypes, argCounts, returnType, codes);
		}

		private CommandFUNCTIONOBJECT(String[] argNames, IVarType[] argTypes, int[] argCounts, IVarType returnType,
				String[] codes) {
			this.argNames = argNames;
			this.argTypes = argTypes;
			this.argCounts = argCounts;
			this.returnType = returnType;
			this.codes = codes;
		}

		@Override
		public Object runCommand(Object[] input, Engine engine, IVarType[] argTypes, int argCount) {

			Map<String, Variable> args = Maps.newHashMap();

			for (int i = 0; i < argNames.length; i++) {
				args.put(this.argNames[i], new Variable(input[i]));
			}

			Engine executer = Engine.createExecuter(engine, args);

			((CommandRETURN) executer.getCommand("return")).funcObj = this;

			for (int i = 0; i < argNames.length; i++) {
				executer.setReg(argNames[i], input[i]);
			}

			try {
				executer.run(codes);
			} catch (Exception e) {
				System.exit(1);
			}

			if (returnType == EnumVarType.Void) {
				return null;
			} else {
				CommandRETURN ret = (CommandRETURN) executer.getCommand("return");
				return ret.returnValue;
			}
		}

		@Override
		public boolean isRunnable(Object[] input, Engine engine, int argCount) {
			return true;
		}

		@Override
		public int[] getArgCounts() {
			return argCounts;
		}

		@Override
		public IVarType[] getArgVarTypes(IVarType[] argTypes, Engine engine, int argCount) {
			return this.argTypes;
		}

		@Override
		public IVarType getReturnVarType(IVarType[] argTypes, IVarType resultType, Engine engine, int argCount) {
			return this.returnType;
		}

		@Override
		public String getReturnRegName() {
			return this.returnType == EnumVarType.Void ? null : Engine.DEFAULT_RETURN_REG_NAME;
		}

	}
}
