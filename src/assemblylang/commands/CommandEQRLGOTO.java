package assemblylang.commands;

import java.util.Map;

import assemblylang.Engine;
import assemblylang.EnumVarType;
import assemblylang.ICommand;
import assemblylang.IVarType;

public class CommandEQRLGOTO implements ICommand {

	private String label = "";
	
	private boolean not = false;
	
	public CommandEQRLGOTO(boolean not) {
		this.not = not;
	}

	@Override
	public Object runCommand(Object[] input, Engine engine, IVarType[] argTypes, int argCount) {
		label = input[0].toString();
		if(not) {
			if(input[1]!=input[2]) {
				Map<String, Integer> labelPos = ((CommandLABEL) engine.getCommand("LABEL")).labelPos;
				if (labelPos.containsKey(label)) {
					engine.Goto(labelPos.get(label));
				}else {
					engine.throwError("Label not found.");
				}
			}
		}else {
			if(input[1]==input[2]) {
				Map<String, Integer> labelPos = ((CommandLABEL) engine.getCommand("LABEL")).labelPos;
				if (labelPos.containsKey(label)) {
					engine.Goto(labelPos.get(label));
				}else {
					engine.throwError("Label not found.");
				}
			}
		}
		return null;
	}

	@Override
	public boolean isRunnable(Object[] input, Engine engine, int argCount) {
		return true;
	}

	@Override
	public int[] getArgCounts() {
		return new int[] {3};
	}

	@Override
	public String getReturnRegName() {
		return null;
	}

	@Override
	public IVarType[] getArgVarTypes(IVarType[] argTypes, Engine engine, int argCount) {
		return new IVarType[]{EnumVarType.String,argTypes[1],argTypes[1]};
	}

	@Override
	public IVarType getReturnVarType(IVarType[] argTypes, IVarType resultType, Engine engine, int argCount) {
		return EnumVarType.Void;
	}

}
