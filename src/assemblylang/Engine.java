package assemblylang;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;

import assemblylang.CommandMultiLine.CommandEndMultiLine;
import assemblylang.CommandMultiLine.IEndCommand;
import assemblylang.commands.Command0401;
import assemblylang.commands.CommandABS;
import assemblylang.commands.CommandADD;
import assemblylang.commands.CommandDIV;
import assemblylang.commands.CommandDSP;
import assemblylang.commands.CommandEQRL;
import assemblylang.commands.CommandEXIT;
import assemblylang.commands.CommandEXPORT;
import assemblylang.commands.CommandGOTO;
import assemblylang.commands.CommandIF;
import assemblylang.commands.CommandLABEL;
import assemblylang.commands.CommandMLT;
import assemblylang.commands.CommandMOD;
import assemblylang.commands.CommandMOV;
import assemblylang.commands.CommandPOW;
import assemblylang.commands.CommandSET;
import assemblylang.commands.CommandSUB;
import assemblylang.commands.CommandSWP;
import assemblylang.commands.CommandVAR;
import assemblylang.commands.CommandWHILE;

@SuppressWarnings("unused")
public final class Engine {

	public static final String DEFAULT_RETURN_REG_NAME = "OP";
	public static final int GLOBAL_SCOPE = -1;

	//datas
	private Table<Integer, String, Variable<Integer>> Regs = HashBasedTable.create();
	private List<Boolean> ScopeNotExecutionInfo = Lists.newArrayList();
	private List<String> MultiLineCmdHeadStr = Lists.newArrayList();
	private List<String> RegNames = Lists.newArrayList();
	private Map<String, Integer> RegIDs = Maps.newHashMap();
	private Map<String, ICommand> commands = Maps.newHashMap();

	//run first data
	private String[] codes = ArrayUtils.EMPTY_STRING_ARRAY;
	private String code = "";
	private String commandname = "";
	private int codeLen = 0;

	//error
	private String lastErrorMessage = "";
	private String lastErrorCode = "";
	private int lastErrorLine = 0;

	//run data
	private int regSize = 0;
	private int scope = -1;//global:-1 local:0 or more
	private int saveReg = 0;

	private boolean isRunningNow = false;
	private boolean isGoto = false;
	private boolean isExit = false;
	private boolean isInit = false;
	private boolean isRecursiveExec = false;

	public boolean isOutError = true;
	public String[] keyWordList = { "TRUE", "FALSE", "NULL", "NIL", "NONE", "VOID", "CONST", "FINAL" };

	/**
	 * Constructor
	 * @param Register(variable)size
	 */
	public Engine() {
		initRegMap();
		commandRegister();
		init();
	}

	/**
	 * Register a CommandMap
	 */
	private void commandRegister() {
		//calc
		this.registerCommand("ADD", new CommandADD());//addition 
		this.registerCommand("SUB", new CommandSUB());//subtraction 
		this.registerCommand("MLT", new CommandMLT());//multiplication 
		this.registerCommand("DIV", new CommandDIV());//division 
		this.registerCommand("MOD", new CommandMOD());//mod 
		this.registerCommand("ABS", new CommandABS());//abs 
		this.registerCommand("POW", new CommandPOW());//pow 
		//register
		this.registerCommand("SET", new CommandSET());//reg value set 
		this.registerCommand("MOV", new CommandMOV());//reg value move 
		this.registerCommand("SWP", new CommandSWP());//reg value swap 
		this.registerCommand("VAR", new CommandVAR()); //new variable
		//control
		this.registerCommand("GOTO", new CommandGOTO());//goto 
		this.registerCommand("EXIT", new CommandEXIT());//exit 
		this.registerCommand("WHILE", new CommandWHILE(false));//while
		this.registerCommand("UNTIL", new CommandWHILE(true));//until
		//condition
		this.registerCommand("EQRL", new CommandEQRL(false));//equal
		this.registerCommand("EQRLNOT", new CommandEQRL(true));//equal not
		this.registerCommand("IF", new CommandIF());//if
		//other
		this.registerCommand("DSP", new CommandDSP());//display(print) 
		this.registerCommand("EXPORT", new CommandEXPORT());//export
		this.registerCommand("LABEL", new CommandLABEL());//label

	}

	private void init() {
		ScopeNotExecutionInfo.add(true);
	}

	/**
	 * running code one line
	 * @param code
	 * @return result
	 */
	public int[] run(String code) {

		this.isRunningNow = true;

		this.code = code;
		this.codes[this.getReg("C", GLOBAL_SCOPE) - 1] = this.code.toUpperCase();
		if (code.contains("#")) {
			if (StringUtils.countMatches(code, '#') >= 2) {
				code = code.substring(0, code.indexOf("#")) + code.substring(code.lastIndexOf("#") + 1);
			} else {
				code = code.substring(0, code.indexOf("#"));
			}
		}

		if (code.contains("\n") || code.contains(";")) {
			this.isRecursiveExec = true;
			int[] results = this.run(code.split("[;\n]"));
			this.isRecursiveExec = false;
			return results;
		}

		if (isInit) {
			this.codes = ArrayUtils.add(this.codes, code);
		}

		if (code.length() <= 0) {
			this.setReg("C", GLOBAL_SCOPE, this.getReg("C", GLOBAL_SCOPE) + 1, true);
			return new int[] { 0 };
		}

		code = code.toUpperCase();
		code = StringUtils.trim(code);
		String[] StrArr = StringUtils.split(code);
		commandname = StrArr[0];

		if (!commands.containsKey(commandname)) {
			throwError("Command not found.");
		}

		if (isExit) {
			this.isRunningNow = false;
			this.code = "";
			this.commandname = "";
			this.isGoto = false;
			this.setReg("C", GLOBAL_SCOPE, this.getReg("C", GLOBAL_SCOPE) + 1, true);
			return new int[] { 0 };
		}

		ICommand command = commands.get(commandname);

		StrArr = ArrayUtils.subarray(StrArr, 1, StrArr.length);

		if (!ArrayUtils.contains(command.getArgCounts(), StrArr.length) &
				!(command.getArgCounts() == null)) {
			throwError("The number of arguments does not match the number of values set.");
		} else if (StrArr.length < command.getMinArgCount()) {
			throwError("The number of arguments does not match the number of values set.");
		}

		StrArr = command.getInitResult(StrArr, this,
				StrArr.length, isInit);

		if (isExit) {
			this.isRunningNow = false;
			this.code = "";
			this.commandname = "";
			this.isGoto = false;
			this.setReg("C", GLOBAL_SCOPE, this.getReg("C", GLOBAL_SCOPE) + 1, true);
			return new int[] { 0 };
		}

		int[] convlocation = command.getNoConversionLocations();
		StrArr = this.replaceKeyWord(StrArr, convlocation, this.commandname);
		int[] IntArr = new int[0];

		for (int i = 0; i < StrArr.length; i++) {
			if (this.hasRegName(StrArr[i])) {
				if (this.getRegReference(StrArr[i], this.getMostNearVarScope(StrArr[i]))) {
					if (ArrayUtils.contains(convlocation, i)) {
						StrArr[i] = Integer.toString(this.RegNames.indexOf(StrArr[i]));
					} else {
						StrArr[i] = Integer.toString(this.getReg(StrArr[i]));
					}
				} else {
					throwError("This variable cannot be referenced.");
				}
			}
		}

		for (int i = 0; i < StrArr.length; i++) {
			try {
				IntArr = ArrayUtils.add(IntArr, Integer.parseInt(StrArr[i]));
			} catch (NumberFormatException e) {
				throwError(
						"Incorrect argument. We looked for that argument as a variable or keyword, but could not find it. \nErrorArg:"
								+ StringUtils.split(this.code)[i + 1]);
			}
		}

		if (isExit) {
			this.isRunningNow = false;
			this.code = "";
			this.commandname = "";
			this.isGoto = false;
			this.setReg("C", GLOBAL_SCOPE, this.getReg("C", GLOBAL_SCOPE) + 1, true);
			return new int[] { 0 };
		}

		int result = 0;

		if (isInit) {
			if (command instanceof CommandMultiLine & !(command instanceof CommandEndMultiLine)) {
				this.ScopeNotExecutionInfo.add(true);
				this.MultiLineCmdHeadStr.add(this.commandname);
				this.scope++;
			}
			
			if (command instanceof IEndCommand & !(command instanceof CommandEndMultiLine)) {
				this.ScopeNotExecutionInfo.remove(this.ScopeNotExecutionInfo.size() - 1);
				
				String lastEndCmd = this.MultiLineCmdHeadStr.get(this.MultiLineCmdHeadStr.size() - 1);
				if(!((CommandMultiLine)commands.get(lastEndCmd)).getEndCommands().containsKey(this.commandname)) {
					this.throwError("End commands are different.");
				}
				this.MultiLineCmdHeadStr.remove(this.MultiLineCmdHeadStr.size()-1);
				this.scope--;
			}
			
			if(command instanceof CommandEndMultiLine) {
				String lastEndCmd = this.MultiLineCmdHeadStr.get(this.MultiLineCmdHeadStr.size() - 1);
				if(!((CommandMultiLine)commands.get(lastEndCmd)).getEndCommands().containsKey(this.commandname)) {
					this.throwError("End commands are different.");
				}
				this.MultiLineCmdHeadStr.remove(this.MultiLineCmdHeadStr.size()-1);
				this.MultiLineCmdHeadStr.add(this.commandname);
			}
			
			command.initRun(IntArr, this, IntArr.length);
			this.setReg("C", GLOBAL_SCOPE, this.getReg("C", GLOBAL_SCOPE) + 1, true);
		} else {

			if (command instanceof CommandMultiLine & !(command instanceof CommandEndMultiLine)) {
				this.ScopeNotExecutionInfo.add(true);
				this.MultiLineCmdHeadStr.add(this.commandname);
				this.scope++;
			}

			if (this.isExecution()) {
				if (command.isRunnable(IntArr, this, IntArr.length)) {
					result = command.runCommand(IntArr, this, IntArr.length);
				} else {
					throwError("Command not found.");
				}
			}else {
				command.RunWhenNotExec(this);
			}

			if (command instanceof IEndCommand & !(command instanceof CommandEndMultiLine)) {
				this.ScopeNotExecutionInfo.remove(ScopeNotExecutionInfo.size() - 1);
				this.MultiLineCmdHeadStr.remove(this.MultiLineCmdHeadStr.size()-1);
				this.Regs.row(this.scope).clear();
				this.scope--;
			}
			
			if(command instanceof CommandEndMultiLine) {
				this.MultiLineCmdHeadStr.remove(this.MultiLineCmdHeadStr.size()-1);
				this.MultiLineCmdHeadStr.add(this.commandname);
			}

			if (isExit) {
				this.isRunningNow = false;
				this.code = "";
				this.commandname = "";
				this.isGoto = false;
				this.setReg("C", GLOBAL_SCOPE, this.getReg("C", GLOBAL_SCOPE) + 1, true);
				return new int[] { 0 };
			}

			if (isGoto) {
				this.isGoto = false;
			} else {
				this.setReg("C", GLOBAL_SCOPE, this.getReg("C", GLOBAL_SCOPE) + 1, true);
			}

			if (command.getReturnRegName() == null) {
				return new int[] { 0 };
			} else if (this.hasRegName(command.getReturnRegName())) {
				this.setReg(command.getReturnRegName(), result, true);
			}
		}
		
		if (isExit) {
			this.isRunningNow = false;
			this.code = "";
			this.commandname = "";
			this.isGoto = false;
			this.setReg("C", GLOBAL_SCOPE, this.getReg("C", GLOBAL_SCOPE) + 1, true);
			return new int[] { 0 };
		}

		this.code = "";
		this.commandname = "";
		this.isGoto = false;
		this.isExit = false;
		this.isRunningNow = false;

		return new int[] { result };
	}

	/**
	 * running code multi line
	 * @param codes
	 * @return result
	 */
	public int[] run(String[] codes) {
		this.saveReg = this.getReg("C", GLOBAL_SCOPE);
		this.setReg("C", GLOBAL_SCOPE, 1, true);
		if (!isRecursiveExec) {
			this.codeLen = codes.length;
			this.codes = codes;

			for (ICommand command : commands.values()) {
				command.init(this);
			}
		}

		int[] results = new int[0];
		results = new int[codeLen];
		if (!isRecursiveExec)
			isInit = true;
		for (int i = 0; i < 2; i++) {
			while (this.getReg("C", GLOBAL_SCOPE) <= codes.length) {
				try {
					if (isInit) {
						this.run(codes[this.getReg("C", GLOBAL_SCOPE) - 1]);
					} else {
						results = ArrayUtils.addAll(results, this.run(codes[this.getReg("C", GLOBAL_SCOPE) - 1]));
					}
				} catch (Exception e) {
					boolean available = true;

					try {
						this.getReg("C", GLOBAL_SCOPE, true);
					} catch (Exception e1) {
						available = false;
					}

					System.out.println("\nOh my god...\n" + "It looks like an error occurred in java.\n");

					if (available) {
						System.out.println("Code:" + codes[this.getReg("C", GLOBAL_SCOPE, true) - 1] + "\nLine:"
								+ this.getReg("C", GLOBAL_SCOPE, true)
								+ "\nisInit:" + isInit 
								+ "\nScope:" + this.scope
								+ "\n");
					} else {
						System.out.println("Code:Error\nLine:Error\nisInit:" + isInit + "\nScope:" + this.scope +  "\n");
					}

					System.out.println("");

					e.printStackTrace();

					System.exit(0);
				}
				if (isExit) {
					break;
				}
			}
			if (!isRecursiveExec)
				isInit = false;

			if (!isExit & this.scope != -1) {
				this.throwError("End commands for multiple line commands are missing.\nCommand:" + this.MultiLineCmdHeadStr.get(MultiLineCmdHeadStr.size()-1), false, true);
				break;
			}
			this.setReg("C", GLOBAL_SCOPE, 1, true);
		}
		if (!isRecursiveExec) {
			this.codeLen = 0;
		} else {
			this.setReg("C", GLOBAL_SCOPE, this.saveReg, true);
			this.setReg("C", GLOBAL_SCOPE, this.getReg("C", GLOBAL_SCOPE) + 1, true);
		}

		return results;
	}

	/**
	 * running command import from text file
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public int[] run(File file) throws IOException {
		String[] codes = FileUtils.readLines(file, StandardCharsets.UTF_8).toArray(new String[0]);
		int[] results = this.run(codes);
		return results;
	}
	
	/*public void throwError(String format, Object... obj) {
		System.out.printf(format, obj);
		this.lastErrorMessage = format;
		this.lastErrorCode = this.code;
		this.lastErrorLine = -1;
		this.isExit = true;
		this.isRunningNow = false;
	}*/

	public void throwError(String errorMessage, int errorLine, boolean code, boolean line) {
		if (this.isOutError) {
			System.out.println("Error:" + errorMessage);
			System.out.println("Code:" + (code?this.code:"None"));
			System.out.println("Line:" + (line?errorLine:"None"));
		}
		this.lastErrorMessage = errorMessage;
		this.lastErrorCode = this.code;
		this.lastErrorLine = errorLine;
		this.isExit = true;
		this.isRunningNow = false;
	}
	
	public void throwError(String errorMessage, boolean code, boolean line) {
		this.throwError(errorMessage, isRecursiveExec ? this.saveReg : this.getReg("C", GLOBAL_SCOPE), code, line);
	}

	/**
	 * throw error
	 * @param errorMessage
	 * @param errorLine
	 */
	public void throwError(String errorMessage, int errorLine) {
		this.throwError(errorMessage, errorLine, true, true);
	}

	/**
	 * throw error
	 * @param errorMessage
	 */
	public void throwError(String errorMessage) {
		this.throwError(errorMessage, isRecursiveExec ? this.saveReg : this.getReg("C", GLOBAL_SCOPE));
	}

	public String getLastErrorMessage() {
		return this.lastErrorMessage;
	}

	public String getLastErrorCode() {
		return this.lastErrorCode;
	}

	public int getLastErrorLine() {
		return this.lastErrorLine;
	}

	/**
	 * get error_message + error_code + error_line string
	 * @return result
	 */
	public String getAllLastError() {
		return "Error:" + this.lastErrorMessage + "\nCode:" + this.lastErrorCode + "\nLine:" + this.lastErrorLine;
	}

	/**
	 * goto
	 * @param index
	 */
	public void Goto(int index) {
		if (index <= this.codeLen & index >= 1) {
			this.setReg("C", GLOBAL_SCOPE, index, true);
			this.isGoto = true;
		} else {
			this.throwError("The number of lines of code specified does not exist.");
		}
	}

	/**
	 * exiting
	 */
	public void Exit() {
		this.isExit = true;
		this.isRunningNow = false;
	}

	/**
	 * replace_key_word True = 1 False = 0 Null = 0 Nil = 0 None = 0 Void = 0
	 * @param strarr
	 * @param convlocation
	 * @param command
	 * @return
	 */
	private String[] replaceKeyWord(String[] strarr, int[] convlocation, String command) {
		if (strarr == null)
			return strarr;
		for (int i = 0; i < strarr.length; i++) {
			if (ArrayUtils.contains(convlocation, i)) {
			} else {
				switch (strarr[i]) {
				case "TRUE":
					strarr[i] = "1";
					break;
				case "FALSE":
				case "NULL":
				case "NIL":
				case "NONE":
				case "VOID":
					strarr[i] = "0";
					break;
				}
			}
		}

		return strarr;

	}

	/**
	 * set reg using reg index
	 * @param index
	 * @param value
	 */
	public void setReg(int index, int value) {
		this.setReg(this.toRegName(index), value);
	}

	/**
	 * get reg using reg index
	 * @param index
	 * @return
	 */
	public int getReg(int index) {
		return this.getReg(this.toRegName(index), false);
	}

	public void setReg(String index, int value) {
		this.setReg(index, this.scope, value, false);
	}

	public void setReg(String index, int scope, int value) {
		this.setReg(index, scope, value, false);
	}

	public void setReg(String index, int value, boolean enforcement) {
		setReg(index, this.scope, value, enforcement);
	}

	public void setReg(String index, int scope, int value, boolean enforcement) {
		Validate.isTrue(-1 <= scope);
		if (this.getRegChange(index, this.getMostNearVarScope(index)) || enforcement) {
			this.Regs.get(this.getMostNearVarScope(index), index).setValue(value);
		} else {
			if (this.isRunningNow) {
				this.throwError("That register cannot be changed.");
			} else {
				throw new IllegalArgumentException("That register cannot be changed.");
			}
		}
	}

	public int getReg(String index) {
		return this.getReg(index, this.scope);
	}

	public int getReg(String index, int scope) {
		return this.getReg(index, scope, false);
	}

	public int getReg(String index, boolean enforcement) {
		return this.getReg(index, this.scope, enforcement);
	}

	public int getReg(String index, int scope, boolean enforcement) {
		Validate.isTrue(-1 <= scope);
		if (this.getRegReference(index, this.getMostNearVarScope(index)) || enforcement) {
			return this.Regs.get(this.getMostNearVarScope(index), index).getValue();
		} else {
			if (this.isRunningNow) {
				this.throwError("Elements of this register are not available");
				return 0;
			} else {
				throw new IllegalArgumentException("Elements of this register are not available");
			}
		}
	}

	public boolean hasRegName(String str, int scope) {
		return this.Regs.contains(scope, str);
	}

	public boolean hasRegName(String str) {
		return this.Regs.containsColumn(str);
	}

	/**
	 * reg index to reg name
	 * @param regindex
	 * @return
	 */
	public String toRegName(int regindex) {
		return this.RegNames.get(regindex);
	}

	/**
	 * set reg changeable
	 * @param Reg
	 * @param value
	 */
	public void setRegChange(String Reg, int scope, boolean value) {
		this.Regs.get(scope, Reg).setChangeable(value);
	}

	/**
	 * set reg referable
	 * @param Reg
	 * @param value
	 */
	public void setRegReference(String Reg, int scope, boolean value) {
		this.Regs.get(scope, Reg).setReferable(value);
	}

	/**
	 * get reg changeable
	 * @param Reg
	 * @param value
	 */
	public boolean getRegChange(String Reg, int scope) {
		return this.Regs.get(scope, Reg).isChangeable();
	}

	/**
	 * get reg referable
	 * @param Reg
	 * @param value
	 */
	public boolean getRegReference(String Reg, int scope) {
		return this.Regs.get(scope, Reg).isReferable();
	}

	/*private boolean isAllNum(String[] strarr) {
		for (String str : strarr) {
			if (!NumberUtils.isParsable(str)) {
				return false;
			}
		}
		return true;
	}*/

	private void initRegMap() {

		commands.put("APRIL", new Command0401());
		this.addReg("OP", Engine.GLOBAL_SCOPE, 0);
		this.addReg("C", Engine.GLOBAL_SCOPE, 1);

		this.setRegChange("OP", Engine.GLOBAL_SCOPE, false);
		this.setRegChange("C", Engine.GLOBAL_SCOPE, false);
	}

	/**
	 * add new register
	 * @param RegName
	 * @param defaultValue
	 */
	public void addReg(String RegName, int defaultValue) {
		this.addReg(RegName, this.scope, defaultValue);
	}

	public void addReg(String RegName, int scope, int defaultValue) {
		this.Regs.put(scope, RegName, new Variable<>(defaultValue));
		this.RegNames.add(RegName);
	}

	/**
	 * add new register
	 * @param RegName
	 */
	public void addReg(String RegName) {
		this.addReg(RegName, 0);
	}

	public Table<Integer, String, Variable<Integer>> getRegs() {
		return HashBasedTable.create(Regs);
	}

	/**
	 * register a command
	 * @param commandName
	 * @param command
	 */
	public void registerCommand(String commandName, ICommand command) {
		this.commands.put(commandName.toUpperCase(), command);
		command.registered(this);
	}

	public ICommand getCommand(String commandName) {
		return commands.get(commandName);
	}

	public Map<String, ICommand> getCommands() {
		return Maps.newHashMap(commands);
	}

	/**
	 * remove command
	 * @param commandName
	 */
	public void removeCommand(String commandName) {
		this.commands.remove(commandName);
	}

	public void clearCommands() {
		this.commands.clear();
	}

	/**
	 * set command registry default
	 */
	public void resetDefaultCommand() {
		this.commands.clear();
		this.commandRegister();
	}

	public int[] getExportValues() {
		return commands.containsKey("EXPORT") ? ((CommandEXPORT) this.commands.get("EXPORT")).ExportInfos : new int[0];
	}

	public boolean isExecution() {
		return !ScopeNotExecutionInfo.contains(false);
	}
	
	public boolean isLastExecution() {
		return ScopeNotExecutionInfo.get(ScopeNotExecutionInfo.size() - 1);
	}

	public void setExecution(int scope, boolean bool) {
		this.ScopeNotExecutionInfo.set(scope + 1, bool);
	}

	public int getScope() {
		return this.scope;
	}
	
	public int getMostNearVarScope(String varName) {
		int countScope = this.scope;
		while (!hasRegName(varName, countScope)) {
			countScope--;
			if (countScope < -1) {
				this.throwError("Variable not found.");
				return 0;
			}
		}
		return countScope;
	}

}
