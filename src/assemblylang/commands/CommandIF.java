package assemblylang.commands;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import assemblylang.CommandMultiLine;
import assemblylang.Engine;
import assemblylang.EnumVarType;
import assemblylang.IVarType;

public class CommandIF extends CommandMultiLine {

	List<Boolean> AlreadyRan = Lists.newArrayList();

	@Override
	public Object runCommand(Object[] input, Engine engine, IVarType[] argTypes, int argCount) {
		AlreadyRan.add(false);
		boolean bool = !((Boolean) input[0]);
		if (bool) {
			engine.setExecution(engine.getScope(), false);
		} else {
			AlreadyRan.set(AlreadyRan.size() - 1, true);
		}
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
		return new IVarType[]{EnumVarType.Boolean};
	}
	
	@Override
	public IVarType getReturnVarType(IVarType[] argTypes, IVarType resultType, Engine engine, int argCount) {
		return EnumVarType.Void;
	}

	@Override
	public Map<String, IEndCommand> getEndCommands() {
		Map<String, IEndCommand> EndCommands = Maps.newHashMap();
		EndCommands.put("else", new CommandELSE());
		EndCommands.put("elif", new CommandELSEIF());
		EndCommands.put("endif", new CommandENDIF());
		return EndCommands;
	}

	public class CommandENDIF implements IEndCommand {

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
			return new int[] { 0 };
		}

		@Override
		public String getReturnRegName() {
			return null;
		}

		@Override
		public void RunWhenNotExec(Engine engine) {
			AlreadyRan.remove(AlreadyRan.size() - 1);
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

	public class CommandELSE extends CommandEndMultiLine {

		@Override
		public Object runCommand(Object[] input, Engine engine, IVarType[] argTypes, int argCount) {
			engine.setExecution(engine.getScope(), !AlreadyRan.get(AlreadyRan.size() - 1));
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
			engine.setExecution(engine.getScope(), !AlreadyRan.get(AlreadyRan.size() - 1));
		}
		
		@Override
		public IVarType[] getArgVarTypes(IVarType[] argTypes, Engine engine, int argCount) {
			return null;
		}

		@Override
		public Map<String, IEndCommand> getEndCommands() {
			Map<String, IEndCommand> EndCommands = Maps.newHashMap();
			EndCommands.put("endif", new CommandENDIF());
			return EndCommands;
		}
		
		@Override
		public IVarType getReturnVarType(IVarType[] argTypes, IVarType resultType, Engine engine, int argCount) {
			return EnumVarType.Void;
		}

	}

	public class CommandELSEIF extends CommandEndMultiLine {

		@Override
		public Object runCommand(Object[] input, Engine engine, IVarType[] argTypes, int argCount) {
			if (!AlreadyRan.get(AlreadyRan.size() - 1)) {
				boolean bool = !((Boolean) input[0]);
				if (bool) {
					engine.setExecution(engine.getScope(), false);
				} else {
					AlreadyRan.set(AlreadyRan.size() - 1, true);
				}
			}else {
				engine.setExecution(engine.getScope(), false);
			}
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
		public String[] getInitResult(String[] args, Engine engine, int argCount, boolean isInit) {
			engine.setExecution(engine.getScope(), true);
			return args;
		}

		@Override
		public void RunWhenNotExec(Engine engine) {
		}
		
		@Override
		public IVarType[] getArgVarTypes(IVarType[] argTypes, Engine engine, int argCount) {
			return new IVarType[]{EnumVarType.Boolean};
		}
		
		@Override
		public IVarType getReturnVarType(IVarType[] argTypes, IVarType resultType, Engine engine, int argCount) {
			return EnumVarType.Void;
		}

		@Override
		public Map<String, IEndCommand> getEndCommands() {
			Map<String, IEndCommand> EndCommands = Maps.newHashMap();
			EndCommands.put("else", new CommandELSE());
			EndCommands.put("elif", new CommandELSEIF());
			EndCommands.put("endif", new CommandENDIF());
			return EndCommands;
		}
	}

}
