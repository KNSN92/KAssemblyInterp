package assemblylang.commands;

import java.util.Map;

import com.google.common.collect.Maps;

import assemblylang.Engine;
import assemblylang.EnumVarType;
import assemblylang.ICommand;
import assemblylang.IVarType;
import assemblylang.util.CmdStrUtil;

public class CommandLABEL implements ICommand{
	
	public Map<String,Integer> labelPos = Maps.newHashMap();
	private String LabelName = "";
	private int LabelValue = 0;

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
		return new int[] {1};
	}

	@Override
	public String getReturnRegName() {
		return null;
	}
	
	@Override
	public String[] getInitResult(String[] args, Engine engine, int argCount, boolean isInit) {
		if (isInit) {
			if(args[0].matches("^[A-Za-z]\\w+$")) {
				LabelName = args[0];
				LabelValue = engine.getCodeCount();
			}else {
				engine.throwError("This is contrary to the label naming convention.");
			}
			args = CmdStrUtil.replaceZero(args, 0);
		}else {
			for(int i=0;i<args.length;i++) {
				args = CmdStrUtil.replaceZero(args,i);
			}
		}
		return args;
	}
	
	@Override
	public void init(Engine engine, String[] codes) {
		labelPos = Maps.newHashMap();
	}
	
	@Override
	public void initRun(Object[] input, Engine engine, IVarType[] argTypes, int argCount) {
		labelPos.put(LabelName, LabelValue);
	}

	@Override
	public IVarType[] getArgVarTypes(IVarType[] argTypes, Engine engine, int argCount) {
		return new IVarType[]{EnumVarType.Int};
	}
	
	@Override
	public IVarType getReturnVarType(IVarType[] argTypes, IVarType resultType, Engine engine, int argCount) {
		return EnumVarType.Void;
	}

}
