package assemblylang.commands;

import java.util.Map;

import com.google.common.collect.Maps;

import assemblylang.Engine;
import assemblylang.ICommand;
import assemblylang.util.CmdStrUtil;

public class CommandLABEL implements ICommand{
	
	Map<String,Integer> labelPos = Maps.newHashMap();
	String LabelName = "";
	int LabelValue = 0;

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
		return new int[] {1};
	}

	@Override
	public String getReturnRegName() {
		return null;
	}
	
	@Override
	public String[] getInitResult(String[] args, Engine engine, int argCount, boolean isInit) {
		LabelName = args[0];
		LabelValue = engine.getReg("C");
		args = CmdStrUtil.replaceZero(args, 0);
		return args;
	}
	
	@Override
	public void init() {
		labelPos = Maps.newHashMap();
	}
	
	@Override
	public void initRun(int[] args, Engine engine, int argCount) {
		labelPos.put(LabelName, LabelValue);
	}

}
