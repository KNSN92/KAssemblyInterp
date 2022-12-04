package assemblylang.commands;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import assemblylang.CommandMultiLine;
import assemblylang.Engine;
import assemblylang.EnumVarType;
import assemblylang.IVarType;

public class CommandWHILE extends CommandMultiLine {

	private final boolean not;

	private List<Integer> startIndexes = Lists.newArrayList();

	public CommandWHILE(boolean not) {
		this.not = not;
	}

	@Override
	public Object runCommand(Object[] input, Engine engine, IVarType[] argTypes, int argCount) {
		Boolean bool = ((input[0] instanceof Boolean) ? !((Boolean) input[0]) : ((Long)input[0]) == 0);
		if (not ? !bool : bool) {
			engine.setExecution(engine.getScope(), false);
		}
		if (!startIndexes.contains(engine.getCodeCount()))
			startIndexes.add(engine.getCodeCount());
		return null;
	}

	@Override
	public boolean isRunnable(Object[] input, Engine engine, int argCount) {
		return true;
	}

	@Override
	public int[] getArgCounts() {
		return new int[] { 1 };
	}

	@Override
	public String getReturnRegName() {
		return null;
	}

	@Override
	public IVarType[] getArgVarTypes(IVarType[] argTypes, Engine engine, int argCount) {
		return new IVarType[] { argTypes[0]==EnumVarType.Int || argTypes[0]==EnumVarType.Float ? argTypes[0] : EnumVarType.Boolean };
	}
	
	@Override
	public IVarType getReturnVarType(IVarType[] argTypes, IVarType resultType, Engine engine, int argCount) {
		return EnumVarType.Void;
	}

	@Override
	public Map<String, IEndCommand> getEndCommands() {
		Map<String, IEndCommand> EndCommands = Maps.newHashMap();

		EndCommands.put(not ? "enduntil" : "endwhile", new CommandENDWHILE());
		return EndCommands;
	}

	public class CommandENDWHILE implements IEndCommand {

		@Override
		public Object runCommand(Object[] input, Engine engine, IVarType[] argTypes, int argCount) {
			engine.Goto(startIndexes.get(startIndexes.size() - 1));
			return null;
		}

		@Override
		public boolean isRunnable(Object[] input, Engine engine, int argCount) {
			return true;
		}

		@Override
		public int[] getArgCounts() {
			return new int[] { 0 };
		}

		@Override
		public String getReturnRegName() {
			return null;
		}

		@Override
		public void RunWhenNotExec(Engine engine) {
			startIndexes.remove(startIndexes.size() - 1);
			engine.setExecution(engine.getScope(), true);
		}

		@Override
		public IVarType[] getArgVarTypes(IVarType[] argTypes, Engine engine, int argCount) {
			return null;
		}
		
		@Override
		public IVarType getReturnVarType(IVarType[] argTypes, IVarType resultType, Engine engine, int argCount) {
			return EnumVarType.Void;
		}
	}

}
