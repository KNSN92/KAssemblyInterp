package assemblylang.commands;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import assemblylang.CommandMultiLine;
import assemblylang.Engine;

public class CommandIF extends CommandMultiLine {

	List<Boolean> AlreadyRan = Lists.newArrayList();

	@Override
	public int runCommand(int[] input, Engine engine, int argCount) {
		AlreadyRan.add(false);
		if (input[0] == 0) {
			engine.setExecution(engine.getScope(), false);
		} else {
			AlreadyRan.set(AlreadyRan.size() - 1, true);
		}
		return 0;
	}

	@Override
	public boolean isRunnable(int[] input, Engine engine, int argCount) {
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
	public Map<String, IEndCommand> getEndCommands() {
		Map<String, IEndCommand> EndCommands = Maps.newHashMap();
		EndCommands.put("ELSE", new CommandELSE());
		EndCommands.put("ELIF", new CommandELSEIF());
		EndCommands.put("ENDIF", new CommandENDIF());
		return EndCommands;
	}

	public class CommandENDIF implements IEndCommand {

		@Override
		public int runCommand(int[] input, Engine engine, int argCount) {
			return 0;
		}

		@Override
		public boolean isRunnable(int[] input, Engine engine, int argCount) {
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

	}

	public class CommandELSE extends CommandEndMultiLine {

		@Override
		public int runCommand(int[] input, Engine engine, int argCount) {
			engine.setExecution(engine.getScope(), !AlreadyRan.get(AlreadyRan.size() - 1));
			return 0;
		}

		@Override
		public boolean isRunnable(int[] input, Engine engine, int argCount) {
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
		public Map<String, IEndCommand> getEndCommands() {
			Map<String, IEndCommand> EndCommands = Maps.newHashMap();
			EndCommands.put("ENDIF", new CommandENDIF());
			return EndCommands;
		}

	}

	public class CommandELSEIF extends CommandEndMultiLine {

		@Override
		public int runCommand(int[] input, Engine engine, int argCount) {
			if (!AlreadyRan.get(AlreadyRan.size() - 1)) {
				if (input[0] == 0) {
					engine.setExecution(engine.getScope(), false);
				} else {
					AlreadyRan.set(AlreadyRan.size() - 1, true);
				}
			}else {
				engine.setExecution(engine.getScope(), false);
			}
			return 0;
		}

		@Override
		public boolean isRunnable(int[] input, Engine engine, int argCount) {
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
		public Map<String, IEndCommand> getEndCommands() {
			Map<String, IEndCommand> EndCommands = Maps.newHashMap();
			EndCommands.put("ELSE", new CommandELSE());
			EndCommands.put("ELIF", new CommandELSEIF());
			EndCommands.put("ENDIF", new CommandENDIF());
			return EndCommands;
		}
	}

}
