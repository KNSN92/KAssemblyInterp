package assemblylang.commands;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import assemblylang.CommandMultiLine;
import assemblylang.Engine;

public class CommandWHILE extends CommandMultiLine {
	
	private final boolean not;
	
	private List<Integer> startIndexes = Lists.newArrayList();

	public CommandWHILE(boolean not) {
		this.not = not;
	}

	@Override
	public int runCommand(int[] input, Engine engine, int argCount) {
		if(not?input[0]!=0:input[0]==0) {
			engine.setExecution(engine.getScope(), false);
		}
		if(!startIndexes.contains(engine.getReg("C", Engine.GLOBAL_SCOPE))) startIndexes.add(engine.getReg("C", Engine.GLOBAL_SCOPE));
		return 0;
	}

	@Override
	public boolean isRunnable(int[] input, Engine engine, int argCount) {
		return true;
	}

	@Override
	public int[] getArgCounts() {
		return new int[]{1};
	}

	@Override
	public String getReturnRegName() {
		return null;
	}

	@Override
	public Map<String, IEndCommand> getEndCommands() {
		Map<String, IEndCommand> EndCommands = Maps.newHashMap();
		
		EndCommands.put(not?"ENDUNTIL":"ENDWHILE",new CommandENDWHILE());
		return EndCommands;
	}
	
	public class CommandENDWHILE implements IEndCommand {

		@Override
		public int runCommand(int[] input, Engine engine, int argCount) {
			engine.Goto(startIndexes.get(startIndexes.size()-1));
			return 0;
		}

		@Override
		public boolean isRunnable(int[] input, Engine engine, int argCount) {
			return true;
		}

		@Override
		public int[] getArgCounts() {
			return new int[]{0};
		}

		@Override
		public String getReturnRegName() {
			return null;
		}

		@Override
		public void RunWhenNotExec(Engine engine) {
			startIndexes.remove(startIndexes.size()-1);
			engine.setExecution(engine.getScope(), true);
		}
		
	}

}
