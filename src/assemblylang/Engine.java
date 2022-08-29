package assemblylang;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import assemblylang.commands.Command0401;
import assemblylang.commands.CommandABS;
import assemblylang.commands.CommandADD;
import assemblylang.commands.CommandDIV;
import assemblylang.commands.CommandDSP;
import assemblylang.commands.CommandEQRL;
import assemblylang.commands.CommandEXIT;
import assemblylang.commands.CommandEXPORT;
import assemblylang.commands.CommandGOTO;
import assemblylang.commands.CommandMLT;
import assemblylang.commands.CommandMOD;
import assemblylang.commands.CommandMOV;
import assemblylang.commands.CommandPOW;
import assemblylang.commands.CommandSET;
import assemblylang.commands.CommandSUB;
import assemblylang.commands.CommandSWP;
import assemblylang.commands.CommandVAR;

public final class Engine {

	private Map<String, Integer> Regs = new HashMap<>();
	private Map<String, Boolean[]> RegSetting = new HashMap<>();
	private List<String> RegNames = new ArrayList<>();
	private Map<String, Integer> RegIDs = new HashMap<>();
	private Map<String, ICommand> commands = new HashMap<>();
	private String[] commandMultiLineCount = new String[0];

	private String code = "";
	private String commandname = "";
	private int codeLen = 0;

	private String lastErrorMessage = "";
	private String lastErrorCode = "";
	private int lastErrorLine = 0;

	private boolean isRunningNow = false;
	private boolean isGoto = false;
	private boolean isExit = false;

	public boolean isOutError = true;
	public String[] NGWordList = { "TRUE", "FALSE", "NULL", "NIL", "NONE", "VOID", "CONST", "FINAL" };

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
		commands.put("ADD", new CommandADD());//addition 
		commands.put("SUB", new CommandSUB());//subtraction 
		commands.put("MLT", new CommandMLT());//multiplication 
		commands.put("DIV", new CommandDIV());//division 
		commands.put("MOD", new CommandMOD());//mod 
		commands.put("ABS", new CommandABS());//abs 
		commands.put("POW", new CommandPOW());//pow 
		//register
		commands.put("SET", new CommandSET());//reg set 
		commands.put("MOV", new CommandMOV());//reg value move 
		commands.put("SWP", new CommandSWP());//reg value swap 
		commands.put("VAR", new CommandVAR()); //new variable
		//control
		commands.put("GOTO", new CommandGOTO());//goto 
		commands.put("EXIT", new CommandEXIT());//exit 
		//condition
		commands.put("EQRL", new CommandEQRL());//equal
		//other
		commands.put("DSP", new CommandDSP());//display(print) 
		commands.put("EXPORT", new CommandEXPORT());//export

	}

	private void init() {

	}

	/**
	 * running code one line
	 * @param code
	 * @return result
	 */
	public int[] run(String code) {

		if (code.contains("\n") || code.contains(";")) {
			return this.run(code.split("[;\n]"));
		}

		this.isRunningNow = true;
		if (code.contains("#")) {
			if (StringUtils.countMatches(code, '#') >= 2) {
				code = code.substring(0, code.indexOf("#")) + code.substring(code.lastIndexOf("#") + 1);
			} else {
				code = code.substring(0, code.indexOf("#"));
			}
		}
		if (code.length() <= 0) {
			this.setReg("C", this.getReg("C") + 1);
			return new int[] {0};
		}
		this.code = code;
		code = code.toUpperCase();
		code = StringUtils.trim(code);
		String[] StrArr = StringUtils.split(code);
		commandname = StrArr[0];
		if(!commands.containsKey(commandname)) {
			throwError("Command not found.");
		}
		
		if (isExit) {
			this.isRunningNow = false;
			this.setReg("C", this.getReg("C") + 1);
			return new int[] {0};
		}
		
		ICommand command = commands.get(commandname);
		StrArr = ArrayUtils.subarray(StrArr, 1, StrArr.length);
		StrArr = command.getInitResult(StrArr, this,
				StrArr.length);

		if (isExit) {
			this.isRunningNow = false;
			this.setReg("C", this.getReg("C") + 1);
			return new int[] {0};
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

				}
			}
		}

		try {
			for (String str : StrArr) {
				IntArr = ArrayUtils.add(IntArr, Integer.parseInt(str));
			}
		} catch (NumberFormatException e) {
			throwError("Incorrect argument.\n" + ArrayUtils.toString(StrArr));
		}

		if (isExit) {
			this.isRunningNow = false;
			this.setReg("C", this.getReg("C") + 1);
			return new int[] {0};
		}

		if (!ArrayUtils.contains(command.getArgCounts(), IntArr.length) &
				!(command.getArgCounts() == null)) {
			throwError("The number of arguments does not match the number of values set.");
		} else if (IntArr.length < command.getMinArgCount()) {
			throwError("The number of arguments does not match the number of values set.");
		}

		if (isExit) {
			this.isRunningNow = false;
			this.setReg("C", this.getReg("C") + 1);
			return new int[] {0};
		}

		int result = 0;
		if (command.isRunnable(IntArr, this, IntArr.length)) {
			result = command.runCommand(IntArr, this, IntArr.length);
		} else {
			throwError("Command not found.");
		}

		if (isExit) {
			this.isRunningNow = false;
			this.setReg("C", this.getReg("C") + 1);
			return new int[] {0};
		}

		if (isGoto) {
			this.isGoto = false;
		} else {
			this.setReg("C", this.getReg("C") + 1);
		}

		if (command.getReturnRegName() == null) {
			return new int[] {0};
		} else if (this.hasRegName(command.getReturnRegName())) {
			this.Regs.put(command.getReturnRegName(), result);
		}

		if (isExit) {
			this.isRunningNow = false;
			this.setReg("C", this.getReg("C") + 1);
			return new int[] {0};
		}
		
		this.code = "";
		this.commandname = "";
		this.isGoto = false;
		this.isExit = false;
		this.isRunningNow = false;

		return new int[]{result};
	}

	/**
	 * running code multi line
	 * @param codes
	 * @return result
	 */
	public int[] run(String[] codes) {
		for(ICommand command:commands.values()) {
			command.reset();
		}
		codes = StringUtils.join(codes, ';').split("[;\n]");
		this.codeLen = codes.length;
		int[] results = new int[codes.length];
		while (this.getReg("C") <= codeLen) {
			try {
				results = ArrayUtils.addAll(results, this.run(codes[this.getReg("C") - 1]));
			} catch (Exception e) {
				System.out.println("Oh my god...\n" + "It looks like an error occurred in java.");
				System.out.println("Error:" + "\nLine:" + this.getReg("C"));
				e.printStackTrace();
				System.exit(0);
			}
			if (isExit) {
				break;
			}
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
		return this.getReg(this.toRegName(index));
	}

	public void setReg(String index, int value) {
		if (this.getRegChange(index)) {
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
		if (this.getRegReference(index)) {
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

	private boolean isAllNum(String[] strarr) {
		for (String str : strarr) {
			if (!NumberUtils.isParsable(str)) {
				return false;
			}
		}
		return true;
	}

	private void initRegMap(int size) {
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

	/**
	 * add command
	 * @param commandName
	 * @param command
	 */
	public void addCommand(String commandName, ICommand command) {
		this.commands.put(commandName, command);
	}

	/**
	 * remove command
	 * @param commandName
	 */
	public void removeCommand(String commandName) {
		this.commands.remove(commandName);
	}

	/**
	 * set command registry default
	 */
	public void resetDefaultCommand() {
		this.commands.clear();
		this.commandRegister();
	}
	
	public int[] getExportValues() {
		return ((CommandEXPORT)this.commands.get("EXPORT")).ExportInfos;
	}
}
