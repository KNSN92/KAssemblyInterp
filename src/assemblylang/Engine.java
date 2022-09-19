package assemblylang;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Maps;

import assemblylang.commands.Command0401;
import assemblylang.commands.CommandABS;
import assemblylang.commands.CommandADD;
import assemblylang.commands.CommandDIV;
import assemblylang.commands.CommandDSP;
import assemblylang.commands.CommandEQRL;
import assemblylang.commands.CommandEXIT;
import assemblylang.commands.CommandEXPORT;
import assemblylang.commands.CommandGOTO;
import assemblylang.commands.CommandLABEL;
import assemblylang.commands.CommandMLT;
import assemblylang.commands.CommandMOD;
import assemblylang.commands.CommandMOV;
import assemblylang.commands.CommandPOW;
import assemblylang.commands.CommandSET;
import assemblylang.commands.CommandSUB;
import assemblylang.commands.CommandSWP;
import assemblylang.commands.CommandVAR;

@SuppressWarnings("unused")
public final class Engine {
	
	public static final String defaultReturnRegName = "OP";

	private Map<String, Integer> Regs = new HashMap<>();
	private Map<String, Boolean[]> RegSetting = new HashMap<>();
	private List<String> RegNames = new ArrayList<>();
	private Map<String, Integer> RegIDs = new HashMap<>();
	private Map<String, ICommand> commands = new HashMap<>();
	private Stack<String> multiLineEleStack = new Stack<>();

	private int regSize = 0;

	private String[] codes = ArrayUtils.EMPTY_STRING_ARRAY;
	private String code = "";
	private String commandname = "";
	private int codeLen = 0;

	private String lastErrorMessage = "";
	private String lastErrorCode = "";
	private int lastErrorLine = 0;

	private boolean isRunningNow = false;
	private boolean isGoto = false;
	private boolean isExit = false;
	private boolean isInit = false;

	public boolean isOutError = true;
	public String[] keyWordList = { "TRUE", "FALSE", "NULL", "NIL", "NONE", "VOID", "CONST", "FINAL" };

	/**
	 * Constructor
	 * @param Register(variable)size
	 */
	public Engine(int regCount) {
		initRegMap(regCount);
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
		//condition
		this.registerCommand("EQRL", new CommandEQRL(false));//equal
		this.registerCommand("EQRLNOT", new CommandEQRL(true));//equal not
		//other
		this.registerCommand("DSP", new CommandDSP());//display(print) 
		this.registerCommand("EXPORT", new CommandEXPORT());//export
		this.registerCommand("LABEL", new CommandLABEL());

	}

	private void init() {

	}

	/**
	 * running code one line
	 * @param code
	 * @return result
	 */
	public int[] run(String code) {

		this.isRunningNow = true;

		this.code = code;
		if (code.contains("#")) {
			if (StringUtils.countMatches(code, '#') >= 2) {
				code = code.substring(0, code.indexOf("#")) + code.substring(code.lastIndexOf("#") + 1);
			} else {
				code = code.substring(0, code.indexOf("#"));
			}
		}

		if (code.contains("\n") || code.contains(";")) {
			return this.run(code.split("[;\n]"));
		}

		if (isInit) {
			this.codes = ArrayUtils.add(this.codes, code);
		}

		if (code.length() <= 0) {
			this.setReg("C", this.getReg("C") + 1);
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
			this.setReg("C", this.getReg("C") + 1);
			return new int[] { 0 };
		}

		ICommand command = commands.get(commandname);

		if (command instanceof CommandMultiLine) {
			multiLineEleStack.push(this.commandname);
		}

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
			this.setReg("C", this.getReg("C") + 1);
			return new int[] { 0 };
		}

		int[] convlocation = command.getNoConversionLocations();
		StrArr = this.replaceKeyWord(StrArr, convlocation, this.commandname);
		int[] IntArr = new int[0];

		for (int i = 0; i < StrArr.length; i++) {
			if (this.hasRegName(StrArr[i])) {
				if (this.getRegReference(StrArr[i])) {
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

		for (String str : StrArr) {
			try {
				IntArr = ArrayUtils.add(IntArr, Integer.parseInt(str));
			} catch (NumberFormatException e) {
				throwError(
						"Incorrect argument. We looked for that argument as a variable or keyword, but could not find it. \nErrorArg:"
								+ str);
			}
		}

		if (isExit) {
			this.isRunningNow = false;
			this.setReg("C", this.getReg("C") + 1);
			return new int[] { 0 };
		}

		if (isExit) {
			this.isRunningNow = false;
			this.setReg("C", this.getReg("C") + 1);
			return new int[] { 0 };
		}

		int result = 0;

		if (isInit) {
			command.initRun(IntArr, this, IntArr.length);
			this.setReg("C", this.getReg("C") + 1);
		} else {
			if (command.isRunnable(IntArr, this, IntArr.length)) {
				result = command.runCommand(IntArr, this, IntArr.length);
			} else {
				throwError("Command not found.");
			}

			if (isExit) {
				this.isRunningNow = false;
				this.setReg("C", this.getReg("C") + 1);
				return new int[] { 0 };
			}

			if (isGoto) {
				this.isGoto = false;
			} else {
				this.setReg("C", this.getReg("C") + 1);
			}

			if (command.getReturnRegName() == null) {
				return new int[] { 0 };
			} else if (this.hasRegName(command.getReturnRegName())) {
				this.Regs.put(command.getReturnRegName(), result);
			}

			if (isExit) {
				this.isRunningNow = false;
				this.setReg("C", this.getReg("C") + 1);
				return new int[] { 0 };
			}
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
		this.codeLen = codes.length;
		this.codes = codes;
		this.setReg("C", 1, true);

		for (ICommand command : commands.values()) {
			command.init(this);
		}

		int[] results = new int[0];
		results = new int[codeLen];
		isInit = true;
		for (int i = 0; i < 2; i++) {
			while (this.getReg("C") <= codeLen) {
				try {
					if (isInit) {
						this.run(codes[this.getReg("C") - 1]);
					} else {
						results = ArrayUtils.addAll(results, this.run(codes[this.getReg("C") - 1]));
					}
				} catch (Exception e) {
					System.out.println("\nOh my god...\n" + "It looks like an error occurred in java.\n");
					System.out.println("Code:" + codes[this.getReg("C") - 1] + "\nLine:" + this.getReg("C") + "\n");
					e.printStackTrace();
					System.exit(0);
				}
				if (isExit) {
					break;
				}
			}
			isInit = false;
			this.setReg("C", 1, true);
		}
		this.codeLen = 0;

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

	/**
	 * throw error
	 * @param errorMessage
	 * @param errorLine
	 */
	public void throwError(String errorMessage, int errorLine) {
		if (this.isOutError)
			System.out.println("Error:" + errorMessage + "\nCode:" + code + "\nLine:" + errorLine);
		this.lastErrorMessage = errorMessage;
		this.lastErrorCode = this.code;
		this.lastErrorLine = errorLine;
		this.isExit = true;
		this.isRunningNow = false;
	}

	/**
	 * throw error
	 * @param errorMessage
	 */
	public void throwError(String errorMessage) {
		this.throwError(errorMessage, this.getReg("C"));
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
			this.setReg("C", index);
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
		this.setReg(index, value, false);
	}

	public void setReg(String index, int value, boolean enforcement) {
		if (this.getRegChange(index) || enforcement) {
			this.Regs.put(index, value);
		} else {
			if (this.isRunningNow) {
				this.throwError("That register cannot be changed.");
			} else {
				throw new IllegalArgumentException("That register cannot be changed.");
			}
		}
	}

	public int getReg(String index) {
		return this.getReg(index, false);
	}

	public int getReg(String index, boolean enforcement) {
		if (this.getRegReference(index) || enforcement) {
			return this.Regs.get(index);
		} else {
			if (this.isRunningNow) {
				this.throwError("Elements of this register are not available");
				return 0;
			} else {
				throw new IllegalArgumentException("Elements of this register are not available");
			}
		}
	}

	public boolean hasRegName(String str) {
		return this.Regs.containsKey(str);
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
	public void setRegChange(String Reg, boolean value) {
		this.setRegSetting(Reg, 1, value);
	}

	/**
	 * set reg referable
	 * @param Reg
	 * @param value
	 */
	public void setRegReference(String Reg, boolean value) {
		this.setRegSetting(Reg, 0, value);
	}

	private void setRegSetting(String Reg, int index, boolean value) {
		boolean boollist[] = ArrayUtils.toPrimitive(this.RegSetting.get(Reg));
		boollist[index] = value;
		this.RegSetting.put(Reg, ArrayUtils.toObject(boollist));
	}

	/**
	 * get reg changeable
	 * @param Reg
	 * @param value
	 */
	public boolean getRegChange(String Reg) {
		return this.RegSetting.get(Reg)[1];
	}

	/**
	 * get reg referable
	 * @param Reg
	 * @param value
	 */
	public boolean getRegReference(String Reg) {
		return this.RegSetting.get(Reg)[0];
	}

	/*private boolean isAllNum(String[] strarr) {
		for (String str : strarr) {
			if (!NumberUtils.isParsable(str)) {
				return false;
			}
		}
		return true;
	}*/

	private void initRegMap(int size) {
		this.regSize = size;

		commands.put("APRIL", new Command0401());

		for (int i = 1; i <= size; i++) {
			this.addReg("R" + i);
		}
		this.addReg("OP");
		this.addReg("C", 1);

		this.setRegChange("OP", false);
		//this.setRegChange("C", false);
	}

	/**
	 * add new register
	 * @param RegName
	 * @param defaultValue
	 */
	public void addReg(String RegName, int defaultValue) {
		this.Regs.put(RegName, defaultValue);
		this.RegNames.add(RegName);
		this.RegSetting.put(RegName, new Boolean[] { true, true }); //参照可能か　変更可能か
	}

	/**
	 * add new register
	 * @param RegName
	 */
	public void addReg(String RegName) {
		this.addReg(RegName, 0);
	}

	public Map<String, Integer> getRegs() {
		return Maps.newHashMap(Regs);
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
		return ((CommandEXPORT) this.commands.get("EXPORT")).ExportInfos;
	}

}
