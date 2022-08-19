package assemblylang.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Triple;

import assemblylang.Engine;

@Deprecated
public final class SyntaxChecker {

	private List<Triple<String[], CommandFunction<String[]>, Boolean>> Syntaxs = new ArrayList<>();

	private SyntaxChecker() {

	}

	public static SyntaxChecker of() {
		return new SyntaxChecker();
	}

	public SyntaxChecker joinSyntaxNoRegex(String[] Syntax, CommandFunction<String[]> CmdFunc) {
		return this.joinSyntax(Syntax, CmdFunc, false);
	}

	public SyntaxChecker joinSyntaxRegex(String[] Syntax, CommandFunction<String[]> CmdFunc) {
		return this.joinSyntax(Syntax, CmdFunc, true);
	}

	public SyntaxChecker joinSyntax(String[] Syntax, CommandFunction<String[]> CmdFunc, boolean usingRegex) {
		Validate.notNull(Syntax);
		Validate.notNull(CmdFunc);

		String[] inSyntax = CmdStrUtil.toAllUpperCase(Syntax);

		this.Syntaxs.add(Triple.of(inSyntax, CmdFunc, usingRegex));

		return this;
	}

	public boolean syntaxCheck(String[] args) {
		for (Triple<String[], CommandFunction<String[]>, Boolean> triple : Syntaxs) {
			if (triple.getLeft().length == args.length) {
				int index = 0;
				for (String str : triple.getLeft()) {
					if (triple.getRight()) {
						if (!args[index].matches(str)) {
							break;
						}
					} else {
						if (!args[index].equals(str)) {
							break;
						}
					}
					index++;
				}
				if (args.length == index) {
					return true;
				}
				index = 0;
			}
		}
		return false;
	}

	public String[] syntaxCheckAndRun(String[] args, Engine engine, Map<String, ?> customValueMap, int argCount) {
		if (this.syntaxCheck(args)) {
			return this.getSyntaxCheckFunc(args).run(args, engine, customValueMap, argCount);
		}

		return null;
	}
	
	public CommandFunction<String[]> getSyntaxCheckFunc(String[] args){
		for (Triple<String[], CommandFunction<String[]>, Boolean> triple : Syntaxs) {
			if (triple.getLeft().length == args.length) {
				int index = 0;
				for (String str : triple.getLeft()) {
					if (triple.getRight()) {
						if (!args[index].matches(str)) {
							break;
						}
					} else {
						if (!args[index].equals(str)) {
							break;
						}
					}
					index++;
				}
				if (args.length == index) {
					return triple.getMiddle();
				}
				index = 0;
			}
		}
		return null;
	}

}
