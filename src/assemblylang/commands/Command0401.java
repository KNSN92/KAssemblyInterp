package assemblylang.commands;

import java.util.Map;

import assemblylang.Engine;
import assemblylang.ICommand;

public class Command0401 implements ICommand {

	@Override
	public int runCommand(int[] input, Engine engine, Map<String, ?> customValueMap, int argCount) {
		if(argCount > 0 & input[0]==401) {
			System.out.println("Today is April Fool's Day! Right?");
		}else {
			System.out.println("Hello World!!");
		}
		return 0;
	}

	@Override
	public boolean isRunnable(int[] input, Engine engine, Map<String, ?> customValueMap, int argCount) {
		return argCount > 0 ? input[0] == 401 | input[0] == 0 : false;
	}

	@Override
	public int[] getArgCounts() {
		return null;
	}

	@Override
	public String getReturnRegName() {
		return null;
	}

	@Override
	public int getMinArgCount() {
		return 0;
	}
}
